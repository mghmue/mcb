package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ScanUiState
import com.example.data.Voucher
import com.example.data.VoucherType
import com.example.ui.theme.BankBlueAccent
import com.example.ui.theme.BankBluePrimary
import com.example.ui.theme.BankEmerald
import com.example.ui.theme.BankRed
import com.example.ui.theme.BankSlate400
import com.example.ui.theme.BankSlate500
import com.example.ui.theme.BankSlate600
import com.example.ui.theme.BankSlate700
import com.example.ui.theme.BankSlate800
import com.example.ui.theme.BankSlate900

@Composable
fun ScannerScreen(
  viewModel: VoucherViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val scanState by viewModel.scanState.collectAsState()
  val summaryData by viewModel.summaryData.collectAsState()
  val currentVoucher by viewModel.currentScanVoucher.collectAsState()

  BackHandler {
    viewModel.navigateTo(AppScreen.DASHBOARD)
  }

  // Laser scanning line animation
  val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
  val scanLineProgress by infiniteTransition.animateFloat(
    initialValue = 0.05f,
    targetValue = 0.95f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 1800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "scan_laser_progress"
  )

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(BankSlate900)
  ) {
    // Main Scanner Content
    Column(
      modifier = Modifier.fillMaxSize()
    ) {
      // Top Navigation & Progress Bar
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(
            Brush.verticalGradient(
              listOf(BankSlate900.copy(alpha = 0.95f), BankSlate900.copy(alpha = 0.6f), Color.Transparent)
            )
          )
          .statusBarsPadding()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Close button
          IconButton(
            onClick = { viewModel.navigateTo(AppScreen.DASHBOARD) },
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.12f))
              .testTag("scanner_close_button")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Close Scanner",
              tint = Color.White,
              modifier = Modifier.size(22.dp)
            )
          }

          // Verified Counter Pill
          Surface(
            shape = RoundedCornerShape(20.dp),
            color = BankSlate800.copy(alpha = 0.85f),
            border = androidx.compose.foundation.BorderStroke(1.dp, BankSlate700)
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DocumentScanner,
                contentDescription = null,
                tint = Color(0xFF60A5FA),
                modifier = Modifier.size(18.dp)
              )
              Text(
                text = "Verified: ${summaryData.verifiedCount} / ${summaryData.totalVouchers}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Linear Progress bar
        LinearProgressIndicator(
          progress = { summaryData.completionPercentage },
          modifier = Modifier
            .fillMaxWidth()
            .height(4.dp)
            .clip(RoundedCornerShape(2.dp)),
          color = Color(0xFF60A5FA),
          trackColor = BankSlate800
        )
      }

      // Viewfinder / Interactive Camera Area
      Box(
        modifier = Modifier
          .weight(1f)
          .fillMaxWidth()
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
          ) {
            viewModel.simulateScan(context)
          }
          .testTag("viewfinder_scan_area"),
        contentAlignment = Alignment.Center
      ) {
        // Grid pattern background
        ScannerGridBackground()

        // Instructional Pill
        Box(
          modifier = Modifier
            .align(Alignment.TopCenter)
            .padding(top = 16.dp)
        ) {
          Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Black.copy(alpha = 0.5f)
          ) {
            Text(
              text = if (scanState is ScanUiState.Scanning) "Processing OCR..." else "TAP ANYWHERE TO SCAN",
              color = if (scanState is ScanUiState.Scanning) Color(0xFF93C5FD) else Color.White.copy(alpha = 0.9f),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp,
              modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
          }
        }

        // Viewfinder Target Frame
        Box(
          modifier = Modifier
            .fillMaxWidth(0.85f)
            .fillMaxSize(0.82f),
          contentAlignment = Alignment.Center
        ) {
          val frameBorderColor = when (scanState) {
            is ScanUiState.Success -> BankEmerald
            is ScanUiState.Error -> BankRed
            else -> Color(0xFF60A5FA)
          }

          // Corner Brackets Canvas
          Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeW = 4.dp.toPx()
            val cornerLen = 32.dp.toPx()
            val w = size.width
            val h = size.height

            // Top Left
            drawLine(frameBorderColor, Offset(0f, 0f), Offset(cornerLen, 0f), strokeW)
            drawLine(frameBorderColor, Offset(0f, 0f), Offset(0f, cornerLen), strokeW)

            // Top Right
            drawLine(frameBorderColor, Offset(w, 0f), Offset(w - cornerLen, 0f), strokeW)
            drawLine(frameBorderColor, Offset(w, 0f), Offset(w, cornerLen), strokeW)

            // Bottom Left
            drawLine(frameBorderColor, Offset(0f, h), Offset(cornerLen, h), strokeW)
            drawLine(frameBorderColor, Offset(0f, h), Offset(0f, h - cornerLen), strokeW)

            // Bottom Right
            drawLine(frameBorderColor, Offset(w, h), Offset(w - cornerLen, h), strokeW)
            drawLine(frameBorderColor, Offset(w, h), Offset(w, h - cornerLen), strokeW)

            // Draw animated scan line when actively scanning
            if (scanState is ScanUiState.Scanning) {
              val lineY = h * scanLineProgress
              drawLine(
                color = Color(0xFF60A5FA),
                start = Offset(0f, lineY),
                end = Offset(w, lineY),
                strokeWidth = 3.dp.toPx()
              )
            }
          }

          // Real Voucher Ticket Preview Inside Viewfinder
          if (currentVoucher != null) {
            VoucherCardPreview(voucher = currentVoucher!!)
          } else {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                tint = BankEmerald,
                modifier = Modifier.size(54.dp)
              )
              Spacer(modifier = Modifier.height(12.dp))
              Text(
                text = "All Vouchers Processed",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Tap 'All OK' to view summary report",
                color = BankSlate400,
                fontSize = 12.sp
              )
            }
          }
        }
      }

      // Bottom Action Area
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .background(BankSlate900)
          .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Prominent Primary Button: "All OK (View Summary)"
        Button(
          onClick = { viewModel.navigateTo(AppScreen.SUMMARY) },
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .testTag("all_ok_summary_button")
        ) {
          Text(
            text = "All OK (View Summary)",
            color = Color.White,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // OVERLAY 1: Success Alert
    AnimatedVisibility(
      visible = scanState is ScanUiState.Success,
      enter = fadeIn() + scaleIn(initialScale = 0.9f),
      exit = fadeOut() + scaleOut(targetScale = 0.9f)
    ) {
      val successState = scanState as? ScanUiState.Success
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.75f))
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Surface(
          shape = RoundedCornerShape(32.dp),
          color = Color.White,
          shadowElevation = 16.dp,
          modifier = Modifier.fillMaxWidth(0.92f)
        ) {
          Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Big Green Checkmark
            Box(
              modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Color(0xFFDCFCE7)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Success",
                tint = BankEmerald,
                modifier = Modifier.size(54.dp)
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Burmese Primary Text
            Text(
              text = "ဘောက်ချာပါရှိပါသည်\nမှန်ကန်ပါသည်",
              fontSize = 22.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B),
              textAlign = TextAlign.Center,
              lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "Voucher exists and is correct",
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              color = BankSlate600,
              textAlign = TextAlign.Center
            )

            if (successState != null) {
              Spacer(modifier = Modifier.height(16.dp))
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFF1F5F9),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(
                  modifier = Modifier.padding(12.dp),
                  horizontalAlignment = Alignment.CenterHorizontally
                ) {
                  Text(
                    text = "${successState.voucher.voucherNumber} • ${successState.voucher.type.displayName}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = BankSlate700
                  )
                  Text(
                    text = successState.voucher.formattedAmount,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = BankEmerald
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // OK Button
            Button(
              onClick = { viewModel.dismissScanState() },
              colors = ButtonDefaults.buttonColors(containerColor = BankEmerald),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("alert_success_ok_button")
            ) {
              Text(
                text = "OK",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }

    // OVERLAY 2: Error Alert
    AnimatedVisibility(
      visible = scanState is ScanUiState.Error,
      enter = fadeIn() + scaleIn(initialScale = 0.9f),
      exit = fadeOut() + scaleOut(targetScale = 0.9f)
    ) {
      val errorState = scanState as? ScanUiState.Error
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color.Black.copy(alpha = 0.82f))
          .padding(24.dp),
        contentAlignment = Alignment.Center
      ) {
        Surface(
          shape = RoundedCornerShape(32.dp),
          color = Color.White,
          shadowElevation = 16.dp,
          modifier = Modifier.fillMaxWidth(0.92f)
        ) {
          Column(
            modifier = Modifier.padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            // Big Red Cross
            Box(
              modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Color(0xFFFEE2E2)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Error",
                tint = BankRed,
                modifier = Modifier.size(54.dp)
              )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Burmese Primary Text
            Text(
              text = errorState?.burmeseTitle ?: "မှန်ကန်မှုမရှိပါ",
              fontSize = 26.sp,
              fontWeight = FontWeight.Bold,
              color = Color(0xFF1E293B),
              textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = errorState?.englishSubtitle ?: "Voucher verification failed",
              fontSize = 13.sp,
              fontWeight = FontWeight.Medium,
              color = BankSlate600,
              textAlign = TextAlign.Center
            )

            if (errorState != null) {
              Spacer(modifier = Modifier.height(14.dp))
              Surface(
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFEF2F2),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = errorState.failureReason,
                  fontSize = 12.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = BankRed,
                  textAlign = TextAlign.Center,
                  modifier = Modifier.padding(12.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dismiss Button
            Button(
              onClick = { viewModel.dismissScanState() },
              colors = ButtonDefaults.buttonColors(containerColor = BankRed),
              shape = RoundedCornerShape(14.dp),
              modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("alert_error_ok_button")
            ) {
              Text(
                text = "OK (Dismiss)",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
              )
            }
          }
        }
      }
    }
  }
}

@Composable
private fun VoucherCardPreview(voucher: Voucher) {
  Surface(
    shape = RoundedCornerShape(16.dp),
    color = Color(0xFFFDFEFE),
    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1)),
    shadowElevation = 8.dp,
    modifier = Modifier
      .fillMaxWidth(0.92f)
      .padding(8.dp)
  ) {
    Column(
      modifier = Modifier.padding(14.dp)
    ) {
      // Header
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = null,
            tint = BankBluePrimary,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "DEPOSIT SLIP",
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.5.sp,
            color = BankBluePrimary
          )
        }
        Text(
          text = voucher.voucherNumber,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = BankSlate600
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Category Tag
      Surface(
        shape = RoundedCornerShape(6.dp),
        color = when (voucher.type) {
          VoucherType.BANK_TO_BANK -> Color(0xFFEFF6FF)
          VoucherType.OTHER_BANK -> Color(0xFFEEF2FF)
          VoucherType.FOREIGN_REMITTANCE -> Color(0xFFECFDF5)
        }
      ) {
        Row(
          modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = voucher.type.displayName,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = when (voucher.type) {
              VoucherType.BANK_TO_BANK -> BankBluePrimary
              VoucherType.OTHER_BANK -> Color(0xFF4338CA)
              VoucherType.FOREIGN_REMITTANCE -> BankEmerald
            }
          )
          if (voucher.type == VoucherType.FOREIGN_REMITTANCE) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "(နိုင်ငံခြားငွေလွှဲ)",
              fontSize = 9.sp,
              fontWeight = FontWeight.Bold,
              color = BankEmerald
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Details
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(text = "Sender", fontSize = 9.sp, color = BankSlate400)
          Text(text = voucher.senderName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BankSlate800)
        }
        Column(horizontalAlignment = Alignment.End) {
          Text(text = "Beneficiary", fontSize = 9.sp, color = BankSlate400)
          Text(text = voucher.receiverName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = BankSlate800)
        }
      }

      Spacer(modifier = Modifier.height(4.dp))

      Text(text = "A/C: ${voucher.accountNumber}", fontSize = 10.sp, color = BankSlate600)

      Spacer(modifier = Modifier.height(8.dp))

      // Amount Row
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(8.dp))
          .background(Color(0xFFF1F5F9))
          .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "AMOUNT",
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          color = BankSlate500
        )
        Text(
          text = voucher.formattedAmount,
          fontSize = 16.sp,
          fontWeight = FontWeight.Black,
          color = BankBluePrimary
        )
      }
    }
  }
}

@Composable
private fun ScannerGridBackground() {
  Canvas(modifier = Modifier.fillMaxSize()) {
    val step = 40.dp.toPx()
    val lineColor = Color.White.copy(alpha = 0.04f)
    var x = 0f
    while (x < size.width) {
      drawLine(lineColor, Offset(x, 0f), Offset(x, size.height), 1f)
      x += step
    }
    var y = 0f
    while (y < size.height) {
      drawLine(lineColor, Offset(0f, y), Offset(size.width, y), 1f)
      y += step
    }
  }
}
