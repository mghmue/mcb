package com.example.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

class VoucherRepository {

  private val _vouchers = MutableStateFlow<List<Voucher>>(emptyList())
  val vouchers: StateFlow<List<Voucher>> = _vouchers.asStateFlow()

  private val _isListUploaded = MutableStateFlow(false)
  val isListUploaded: StateFlow<Boolean> = _isListUploaded.asStateFlow()

  private val _summaryData = MutableStateFlow(SummaryData())
  val summaryData: StateFlow<SummaryData> = _summaryData.asStateFlow()

  private val _verifiedVoucherHistory = MutableStateFlow<List<Voucher>>(emptyList())
  val verifiedVoucherHistory: StateFlow<List<Voucher>> = _verifiedVoucherHistory.asStateFlow()

  // Generate the 50 Master List vouchers
  fun uploadDailyList() {
    val sampleList = generateSampleMasterList()
    _vouchers.value = sampleList
    _isListUploaded.value = true
    _verifiedVoucherHistory.value = emptyList()
    _summaryData.value = SummaryData(
      bankToBank = 0L,
      otherBank = 0L,
      foreignRemittance = 0L,
      verifiedCount = 0,
      totalVouchers = sampleList.size
    )
  }

  fun resetAll() {
    _vouchers.value = emptyList()
    _isListUploaded.value = false
    _verifiedVoucherHistory.value = emptyList()
    _summaryData.value = SummaryData(totalVouchers = 0)
  }

  // Get current voucher to present in camera scanner
  fun getNextPendingVoucher(): Voucher? {
    return _vouchers.value.firstOrNull { !it.isVerified }
  }

  // Record a verified voucher
  fun markVoucherAsVerified(voucher: Voucher): Voucher {
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val verifiedTime = timeFormat.format(Date())
    val updatedVoucher = voucher.copy(isVerified = true, verifiedAt = verifiedTime)

    val currentList = _vouchers.value.toMutableList()
    val index = currentList.indexOfFirst { it.id == voucher.id }
    if (index >= 0) {
      currentList[index] = updatedVoucher
      _vouchers.value = currentList
    }

    _verifiedVoucherHistory.value = listOf(updatedVoucher) + _verifiedVoucherHistory.value

    val currentSummary = _summaryData.value
    _summaryData.value = currentSummary.copy(
      bankToBank = currentSummary.bankToBank + if (updatedVoucher.type == VoucherType.BANK_TO_BANK) updatedVoucher.amountKyats else 0L,
      otherBank = currentSummary.otherBank + if (updatedVoucher.type == VoucherType.OTHER_BANK) updatedVoucher.amountKyats else 0L,
      foreignRemittance = currentSummary.foreignRemittance + if (updatedVoucher.type == VoucherType.FOREIGN_REMITTANCE) updatedVoucher.amountKyats else 0L,
      verifiedCount = currentSummary.verifiedCount + 1
    )

    return updatedVoucher
  }

  private fun generateSampleMasterList(): List<Voucher> {
    val customers = listOf(
      "U Kyaw Win" to "Daw Khin Mar",
      "Daw Myint Myint" to "Ko Than Zaw",
      "Ma Su Su Lwin" to "U Min Aung",
      "Ko Aung Myo" to "Daw Aye Aye",
      "Daw Hla Hla Win" to "Ko Kyaw Zin",
      "U Tin Maung" to "Ma Thinzar",
      "Daw Moe Moe" to "Ko Ye Htut",
      "Ko Zaw Zaw" to "Daw Nilar",
      "Ma Thida" to "U Thein Lwin",
      "U Phyo Wai" to "Daw Thuzar"
    )

    val bankSources = listOf(
      "KBZ Bank (Branch 01)",
      "AYA Bank (Hledan Branch)",
      "CB Bank (Pansoedan Branch)",
      "Yoma Bank (Downtown)",
      "Western Union (Singapore Remittance)",
      "MoneyGram (Bangkok Agent)",
      "UAB Bank (Central)"
    )

    val list = mutableListOf<Voucher>()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val today = dateFormat.format(Date())

    for (i in 1..50) {
      val pair = customers[i % customers.size]
      val type = when {
        i % 3 == 1 -> VoucherType.BANK_TO_BANK
        i % 3 == 2 -> VoucherType.OTHER_BANK
        else -> VoucherType.FOREIGN_REMITTANCE
      }

      val randomAmount = (Random.nextInt(1, 51)) * 10000L // 10,000 to 500,000 Ks
      val voucherNumber = "VC-%04d".format(1000 + i)
      val accNum = "200-101-00%04d".format(3000 + i)
      val bank = bankSources[i % bankSources.size]

      list.add(
        Voucher(
          id = "VCH_$i",
          voucherNumber = voucherNumber,
          type = type,
          amountKyats = randomAmount,
          senderName = pair.first,
          receiverName = pair.second,
          accountNumber = accNum,
          sourceBank = bank,
          transactionTime = "$today %02d:%02d".format(8 + (i % 8), (i * 7) % 60),
          isVerified = false,
          referenceNote = "Daily Deposit Transfer batch #${100 + (i / 10)}"
        )
      )
    }
    return list
  }
}
