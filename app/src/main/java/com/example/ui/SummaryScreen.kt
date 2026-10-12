package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Voucher
import com.example.ui.theme.BankBlueDark
import com.example.ui.theme.BankBlueLight
import com.example.ui.theme.BankBluePrimary
import com.example.ui.theme.BankEmerald
import com.example.ui.theme.BankIndigo
import com.example.ui.theme.BankIndigoLight
import com.example.ui.theme.BankSlate100
import com.example.ui.theme.BankSlate400
import com.example.ui.theme.BankSlate500
import com.example.ui.theme.BankSlate600
import com.example.ui.theme.BankSlate700
import com.example.ui.theme.BankSlate800
import com.example.ui.theme.BankSlate900

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreen(
  viewModel: VoucherViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val summaryData by viewModel.summaryData.collectAsState()
  val verifiedHistory by viewModel.verifiedHistory.collectAsState()

  var showConfirmSaveDialog by remember { mutableStateOf(false) }

  BackHandler {
    viewModel.navigateTo(AppScreen.SCANNER)
  }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
  ) {
    // Top Navy Header
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .background(
          Brush.verticalGradient(
            listOf(BankBlueDark, BankBluePrimary)
          )
        )
        .statusBarsPadding()
        .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(
          onClick = { viewModel.navigateTo(AppScreen.SCANNER) },
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f))
            .testTag("summary_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = "Summary Report",
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.3).sp
          )
          Text(
            text = "Daily Verification Completed",
            color = Color(0xFFBFDBFE),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )
        }
      }
    }

    // Scrollable Report Body
    LazyColumn(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 20.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // 1. Stats Row: Verified vs Pending
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Verified Card
          Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(18.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "${summaryData.verifiedCount}",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = BankBlueDark
              )
              Text(
                text = "VERIFIED",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BankSlate400,
                letterSpacing = 1.sp
              )
            }
          }

          // Pending Card
          Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.weight(1f)
          ) {
            Column(
              modifier = Modifier.padding(18.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Text(
                text = "${summaryData.pendingCount}",
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                color = BankSlate400
              )
              Text(
                text = "PENDING",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = BankSlate400,
                letterSpacing = 1.sp
              )
            }
          }
        }
      }

      // 2. Deposits Breakdown Section
      item {
        Column {
          Text(
            text = "DEPOSITS BREAKDOWN",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = BankSlate500,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
          )

          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Category 1: Bank to Bank
            DepositCategoryCard(
              icon = Icons.Default.AccountBalance,
              iconColor = BankBluePrimary,
              iconBg = Color(0xFFEFF6FF),
              title = "Bank to Bank",
              subtitle = "Internal Transfers",
              burmeseBadge = null,
              amount = Voucher.formatKyat(summaryData.bankToBank)
            )

            // Category 2: Other Bank
            DepositCategoryCard(
              icon = Icons.Default.Sync,
              iconColor = BankIndigo,
              iconBg = BankIndigoLight,
              title = "Other Bank",
              subtitle = "External Transfers",
              burmeseBadge = null,
              amount = Voucher.formatKyat(summaryData.otherBank)
            )

            // Category 3: Foreign Remittance (with Myanmar text)
            DepositCategoryCard(
              icon = Icons.Default.Public,
              iconColor = BankEmerald,
              iconBg = Color(0xFFECFDF5),
              title = "Foreign Remittance",
              subtitle = null,
              burmeseBadge = "နိုင်ငံခြားငွေလွှဲ",
              amount = Voucher.formatKyat(summaryData.foreignRemittance)
            )
          }
        }
      }

      // 3. Grand Total Card
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
              Brush.linearGradient(
                listOf(BankBlueDark, Color(0xFF1E40AF), BankBlueDark)
              )
            )
            .border(1.dp, Color(0xFF3B82F6).copy(alpha = 0.3f), RoundedCornerShape(28.dp))
            .padding(24.dp)
        ) {
          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
          ) {
            Text(
              text = "TOTAL AMOUNT VERIFIED",
              color = Color(0xFF93C5FD),
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = Voucher.formatKyat(summaryData.totalAmount),
              color = Color.White,
              fontSize = 32.sp,
              fontWeight = FontWeight.Black,
              letterSpacing = (-0.5).sp
            )
          }
        }
      }

      // 4. Scanned Audit Trail (if any items were scanned)
      if (verifiedHistory.isNotEmpty()) {
        item {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 4.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.History,
                contentDescription = null,
                tint = BankSlate500,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "VERIFIED IN THIS SESSION (${verifiedHistory.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = BankSlate500,
                letterSpacing = 0.5.sp
              )
            }
          }
        }

        items(verifiedHistory.take(5)) { voucher ->
          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = voucher.voucherNumber,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = BankSlate800
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "• ${voucher.type.displayName}",
                    fontSize = 11.sp,
                    color = BankSlate500
                  )
                }
                Text(
                  text = "${voucher.senderName} (${voucher.accountNumber})",
                  fontSize = 11.sp,
                  color = BankSlate600
                )
              }
              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = voucher.formattedAmount,
                  fontSize = 14.sp,
                  fontWeight = FontWeight.Black,
                  color = BankEmerald
                )
                Text(
                  text = voucher.verifiedAt ?: "Verified",
                  fontSize = 10.sp,
                  color = BankSlate400
                )
              }
            }
          }
        }
      }
    }

    // Bottom Anchored "Save & Finish"
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(horizontal = 20.dp, vertical = 18.dp)
    ) {
      Button(
        onClick = { showConfirmSaveDialog = true },
        colors = ButtonDefaults.buttonColors(containerColor = BankBlueDark),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
          .fillMaxWidth()
          .height(56.dp)
          .testTag("save_and_finish_button")
      ) {
        Icon(
          imageVector = Icons.Default.FileDownload,
          contentDescription = null,
          tint = Color.White,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "Save & Finish",
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
      }
    }
  }

  // Confirmation Alert Dialog
  if (showConfirmSaveDialog) {
    AlertDialog(
      onDismissRequest = { showConfirmSaveDialog = false },
      icon = {
        Icon(
          imageVector = Icons.Default.CheckCircle,
          contentDescription = null,
          tint = BankEmerald,
          modifier = Modifier.size(44.dp)
        )
      },
      title = {
        Text(
          text = "မှတ်တမ်းသိမ်းဆည်းပြီးပါပြီ",
          fontWeight = FontWeight.Bold,
          fontSize = 18.sp
        )
      },
      text = {
        Text(
          text = "Daily verification session for ${summaryData.verifiedCount} vouchers has been completed and logged successfully.\n\nReturn to dashboard for next batch?",
          fontSize = 14.sp,
          color = BankSlate600
        )
      },
      confirmButton = {
        Button(
          onClick = {
            showConfirmSaveDialog = false
            viewModel.resetAndFinish(context)
          },
          colors = ButtonDefaults.buttonColors(containerColor = BankBlueDark)
        ) {
          Text("Done")
        }
      },
      dismissButton = {
        TextButton(onClick = { showConfirmSaveDialog = false }) {
          Text("Cancel", color = BankSlate600)
        }
      }
    )
  }
}

@Composable
private fun DepositCategoryCard(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  iconColor: Color,
  iconBg: Color,
  title: String,
  subtitle: String?,
  burmeseBadge: String?,
  amount: String
) {
  Card(
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(iconBg),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconColor,
            modifier = Modifier.size(22.dp)
          )
        }
        Column {
          Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = BankSlate800
          )
          if (subtitle != null) {
            Text(
              text = subtitle,
              fontSize = 11.sp,
              fontWeight = FontWeight.Medium,
              color = BankSlate400
            )
          }
          if (burmeseBadge != null) {
            Text(
              text = burmeseBadge,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              color = BankEmerald
            )
          }
        }
      }

      Text(
        text = amount,
        fontSize = 16.sp,
        fontWeight = FontWeight.Black,
        color = BankSlate700
      )
    }
  }
}
