package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ScanUiState
import com.example.data.SummaryData
import com.example.data.Voucher
import com.example.data.VoucherRepository
import com.example.data.VoucherType
import com.example.util.HapticHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class AppScreen {
  DASHBOARD,
  SCANNER,
  SUMMARY
}

enum class ScanSimulationMode(val label: String) {
  PROTOTYPE_RANDOM("75% Match / 25% Error (Default)"),
  ALWAYS_SUCCESS("Force Success (100% Match)"),
  ALWAYS_ERROR("Force Mismatch (Test Error Alert)")
}

class VoucherViewModel(
  private val repository: VoucherRepository = VoucherRepository()
) : ViewModel() {

  private val _currentScreen = MutableStateFlow(AppScreen.DASHBOARD)
  val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

  val isListUploaded: StateFlow<Boolean> = repository.isListUploaded
  val vouchers: StateFlow<List<Voucher>> = repository.vouchers
  val summaryData: StateFlow<SummaryData> = repository.summaryData
  val verifiedHistory: StateFlow<List<Voucher>> = repository.verifiedVoucherHistory

  private val _scanState = MutableStateFlow<ScanUiState>(ScanUiState.Idle)
  val scanState: StateFlow<ScanUiState> = _scanState.asStateFlow()

  private val _simulationMode = MutableStateFlow(ScanSimulationMode.PROTOTYPE_RANDOM)
  val simulationMode: StateFlow<ScanSimulationMode> = _simulationMode.asStateFlow()

  private val _currentScanVoucher = MutableStateFlow<Voucher?>(null)
  val currentScanVoucher: StateFlow<Voucher?> = _currentScanVoucher.asStateFlow()

  private val _showMasterListDialog = MutableStateFlow(false)
  val showMasterListDialog: StateFlow<Boolean> = _showMasterListDialog.asStateFlow()

  private var scanTimerJob: Job? = null

  init {
    updateNextPendingVoucher()
  }

  fun setSimulationMode(mode: ScanSimulationMode) {
    _simulationMode.value = mode
  }

  fun setShowMasterListDialog(show: Boolean) {
    _showMasterListDialog.value = show
  }

  fun handleUpload(context: Context) {
    HapticHelper.triggerScanTap(context)
    repository.uploadDailyList()
    updateNextPendingVoucher()
  }

  fun navigateTo(screen: AppScreen) {
    _currentScreen.value = screen
    if (screen == AppScreen.SCANNER) {
      updateNextPendingVoucher()
      _scanState.value = ScanUiState.Idle
    }
  }

  private fun updateNextPendingVoucher() {
    _currentScanVoucher.value = repository.getNextPendingVoucher()
  }

  fun simulateScan(context: Context) {
    if (_scanState.value is ScanUiState.Scanning) return

    HapticHelper.triggerScanTap(context)
    _scanState.value = ScanUiState.Scanning

    scanTimerJob?.cancel()
    scanTimerJob = viewModelScope.launch {
      // 1000ms delay to simulate OCR text recognition and master-list lookup
      delay(1000)

      val isMatch = when (_simulationMode.value) {
        ScanSimulationMode.PROTOTYPE_RANDOM -> Random.nextFloat() < 0.75f
        ScanSimulationMode.ALWAYS_SUCCESS -> true
        ScanSimulationMode.ALWAYS_ERROR -> false
      }

      val pendingVoucher = repository.getNextPendingVoucher()

      if (isMatch && pendingVoucher != null) {
        val verified = repository.markVoucherAsVerified(pendingVoucher)
        HapticHelper.triggerSuccess(context)
        _scanState.value = ScanUiState.Success(verified)
        updateNextPendingVoucher()

        // Auto dismiss success alert after 2.5 seconds if officer doesn't tap OK
        launch {
          delay(2500)
          if (_scanState.value is ScanUiState.Success) {
            _scanState.value = ScanUiState.Idle
          }
        }
      } else {
        HapticHelper.triggerError(context)
        val failureReason = if (pendingVoucher == null) {
          "ဘောက်ချာအားလုံး စစ်ဆေးပြီးပါပြီ (All 50 vouchers already verified)"
        } else {
          listOf(
            "ဘောက်ချာနံပါတ်စာရင်းတွင် မတွေ့ရှိပါ (Voucher not found in daily list)",
            "ငွေပမာဏ ကိုက်ညီမှုမရှိပါ (Amount discrepancy detected by OCR)",
            "တရားမဝင်သော ဘောက်ချာပုံစံ ဖြစ်နေပါသည် (Unrecognized bank voucher slip format)"
          ).random()
        }
        _scanState.value = ScanUiState.Error(
          burmeseTitle = "မှန်ကန်မှုမရှိပါ",
          englishSubtitle = "Voucher verification failed",
          failureReason = failureReason
        )
      }
    }
  }

  fun dismissScanState() {
    scanTimerJob?.cancel()
    _scanState.value = ScanUiState.Idle
  }

  fun resetAndFinish(context: Context) {
    HapticHelper.triggerScanTap(context)
    repository.resetAll()
    _scanState.value = ScanUiState.Idle
    _currentScanVoucher.value = null
    _currentScreen.value = AppScreen.DASHBOARD
  }
}
