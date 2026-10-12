package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.Voucher
import com.example.data.VoucherRepository
import com.example.data.VoucherType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun testAppNameIsSecureBank() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SecureBank", appName)
  }

  @Test
  fun testVoucherRepositoryUploadAndVerification() {
    val repository = VoucherRepository()
    assertEquals(false, repository.isListUploaded.value)

    repository.uploadDailyList()
    assertEquals(true, repository.isListUploaded.value)
    assertEquals(50, repository.vouchers.value.size)
    assertEquals(50, repository.summaryData.value.totalVouchers)
    assertEquals(0, repository.summaryData.value.verifiedCount)

    val firstPending = repository.getNextPendingVoucher()
    assertNotNull(firstPending)

    val verified = repository.markVoucherAsVerified(firstPending!!)
    assertEquals(true, verified.isVerified)
    assertEquals(1, repository.summaryData.value.verifiedCount)
    assertEquals(49, repository.summaryData.value.pendingCount)
    assertTrue(repository.summaryData.value.totalAmount > 0)
  }

  @Test
  fun testBurmeseLabelsIntegrity() {
    assertEquals("ဘဏ်အချင်းချင်းလွှဲငွေ", VoucherType.BANK_TO_BANK.burmeseLabel)
    assertEquals("အခြားဘဏ်လွှဲငွေ", VoucherType.OTHER_BANK.burmeseLabel)
    assertEquals("နိုင်ငံခြားငွေလွှဲ", VoucherType.FOREIGN_REMITTANCE.burmeseLabel)
  }
}
