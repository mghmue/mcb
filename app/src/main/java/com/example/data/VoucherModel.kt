package com.example.data

import java.text.NumberFormat
import java.util.Locale

enum class VoucherType(
  val displayName: String,
  val subtitle: String,
  val burmeseLabel: String
) {
  BANK_TO_BANK(
    displayName = "Bank to Bank",
    subtitle = "Internal Transfers",
    burmeseLabel = "ဘဏ်အချင်းချင်းလွှဲငွေ"
  ),
  OTHER_BANK(
    displayName = "Other Bank",
    subtitle = "External Transfers",
    burmeseLabel = "အခြားဘဏ်လွှဲငွေ"
  ),
  FOREIGN_REMITTANCE(
    displayName = "Foreign Remittance",
    subtitle = "International Transfers",
    burmeseLabel = "နိုင်ငံခြားငွေလွှဲ"
  )
}

data class Voucher(
  val id: String,
  val voucherNumber: String,
  val type: VoucherType,
  val amountKyats: Long,
  val senderName: String,
  val receiverName: String,
  val accountNumber: String,
  val sourceBank: String,
  val transactionTime: String,
  val isVerified: Boolean = false,
  val verifiedAt: String? = null,
  val referenceNote: String = ""
) {
  val formattedAmount: String
    get() = formatKyat(amountKyats)

  companion object {
    fun formatKyat(amount: Long): String {
      val formatter = NumberFormat.getNumberInstance(Locale.US)
      return "${formatter.format(amount)} Ks"
    }
  }
}

sealed interface ScanUiState {
  data object Idle : ScanUiState
  data object Scanning : ScanUiState
  data class Success(val voucher: Voucher) : ScanUiState
  data class Error(
    val burmeseTitle: String = "မှန်ကန်မှုမရှိပါ",
    val englishSubtitle: String = "Voucher verification failed",
    val failureReason: String = "ဘောက်ချာနံပါတ်စာရင်းတွင် မတွေ့ရှိပါ (Voucher not found in daily master list)"
  ) : ScanUiState
}

data class SummaryData(
  val bankToBank: Long = 0L,
  val otherBank: Long = 0L,
  val foreignRemittance: Long = 0L,
  val verifiedCount: Int = 0,
  val totalVouchers: Int = 50
) {
  val totalAmount: Long
    get() = bankToBank + otherBank + foreignRemittance

  val pendingCount: Int
    get() = (totalVouchers - verifiedCount).coerceAtLeast(0)

  val completionPercentage: Float
    get() = if (totalVouchers > 0) verifiedCount.toFloat() / totalVouchers.toFloat() else 0f
}
