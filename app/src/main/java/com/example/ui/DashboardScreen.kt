package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ListAlt
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.example.data.VoucherType
import com.example.ui.theme.BankBlueAccent
import com.example.ui.theme.BankBlueDark
import com.example.ui.theme.BankBlueLight
import com.example.ui.theme.BankBluePrimary
import com.example.ui.theme.BankEmerald
import com.example.ui.theme.BankEmeraldContainer
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
fun DashboardScreen(
  viewModel: VoucherViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val isListUploaded by viewModel.isListUploaded.collectAsState()
  val vouchers by viewModel.vouchers.collectAsState()
  val summaryData by viewModel.summaryData.collectAsState()
  val simulationMode by viewModel.simulationMode.collectAsState()
  val showMasterListDialog by viewModel.showMasterListDialog.collectAsState()

  var modeMenuExpanded by remember { mutableStateOf(false) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(Color(0xFFF8FAFC))
  ) {
    // Top Bank Header
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(bottomStart = 36.dp, bottomEnd = 36.dp))
        .background(
          Brush.verticalGradient(
            listOf(BankBlueDark, BankBluePrimary)
          )
        )
        .statusBarsPadding()
        .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 28.dp)
    ) {
      Column {
        // App identity row
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.AccountBalance,
                contentDescription = "Bank Logo",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
              )
            }
            Column {
              Text(
                text = "SecureBank",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "Voucher Verification System",
                color = Color(0xFFBFDBFE),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }

          // Simulation Mode badge/selector
          Box {
            Surface(
              onClick = { modeMenuExpanded = true },
              shape = RoundedCornerShape(16.dp),
              color = Color.White.copy(alpha = 0.15f),
              modifier = Modifier.testTag("simulation_mode_chip")
            ) {
              Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Security,
                  contentDescription = "Mode",
                  tint = Color(0xFF93C5FD),
                  modifier = Modifier.size(14.dp)
                )
                Text(
                  text = when (simulationMode) {
                    ScanSimulationMode.PROTOTYPE_RANDOM -> "75% OCR"
                    ScanSimulationMode.ALWAYS_SUCCESS -> "100% OK"
                    ScanSimulationMode.ALWAYS_ERROR -> "Error Test"
                  },
                  color = Color.White,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }

            DropdownMenu(
              expanded = modeMenuExpanded,
              onDismissRequest = { modeMenuExpanded = false }
            ) {
              ScanSimulationMode.entries.forEach { mode ->
                DropdownMenuItem(
                  text = {
                    Text(
                      text = mode.label,
                      fontWeight = if (mode == simulationMode) FontWeight.Bold else FontWeight.Normal
                    )
                  },
                  onClick = {
                    viewModel.setSimulationMode(mode)
                    modeMenuExpanded = false
                  }
                )
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "Good Morning,",
          color = Color.White.copy(alpha = 0.85f),
          fontSize = 28.sp,
          fontWeight = FontWeight.Light
        )
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Bottom
        ) {
          Text(
            text = "Officer",
            color = Color.White,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0x33FFFFFF)
          ) {
            Text(
              text = "Branch #042 • Yangon",
              color = Color(0xFFE0E7FF),
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
          }
        }
      }
    }

    // Main content area
    Column(
      modifier = Modifier
        .weight(1f)
        .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
      if (!isListUploaded) {
        // STATE 1: Not Uploaded yet
        Column(
          modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 20.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth(0.85f)
              .aspectRatio(1f)
              .clip(RoundedCornerShape(36.dp))
              .border(
                width = 2.dp,
                color = Color(0xFF93C5FD),
                shape = RoundedCornerShape(36.dp)
              )
              .background(Color(0xFFEFF6FF))
              .clickable { viewModel.handleUpload(context) }
              .testTag("upload_daily_list_button"),
            contentAlignment = Alignment.Center
          ) {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              verticalArrangement = Arrangement.Center,
              modifier = Modifier.padding(24.dp)
            ) {
              Box(
                modifier = Modifier
                  .size(80.dp)
                  .clip(CircleShape)
                  .background(Color(0xFFDBEAFE)),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.CloudUpload,
                  contentDescription = "Upload Daily List",
                  tint = BankBlueAccent,
                  modifier = Modifier.size(46.dp)
                )
              }
              Spacer(modifier = Modifier.height(16.dp))
              Text(
                text = "Upload Daily List",
                color = BankBluePrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "Tap to sync transactions",
                color = Color(0xFF60A5FA),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
              )
            }
          }

          Spacer(modifier = Modifier.height(24.dp))
          Text(
            text = "Daily Batch: 50 Banking Vouchers",
            color = BankSlate500,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
          )
        }
      } else {
        // STATE 2: Daily List is Uploaded
        AnimatedVisibility(
          visible = true,
          enter = fadeIn() + slideInVertically { it / 4 }
        ) {
          Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
          ) {
            Column {
              // Master List Card
              Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(24.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "DAILY MASTER LIST",
                      color = BankSlate500,
                      fontSize = 12.sp,
                      fontWeight = FontWeight.Bold,
                      letterSpacing = 1.sp
                    )
                    Row(
                      verticalAlignment = Alignment.CenterVertically,
                      horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified",
                        tint = BankEmerald,
                        modifier = Modifier.size(20.dp)
                      )
                      Text(
                        text = "READY",
                        color = BankEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(14.dp))

                  Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                  ) {
                    Text(
                      text = "${summaryData.totalVouchers}",
                      color = BankSlate800,
                      fontSize = 54.sp,
                      fontWeight = FontWeight.Black,
                      lineHeight = 54.sp
                    )
                    Text(
                      text = "Vouchers to Verify",
                      color = BankSlate500,
                      fontSize = 16.sp,
                      fontWeight = FontWeight.Medium,
                      modifier = Modifier.padding(bottom = 8.dp)
                    )
                  }

                  Spacer(modifier = Modifier.height(18.dp))

                  // Status badge
                  Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFEFF6FF),
                    modifier = Modifier.fillMaxWidth()
                  ) {
                    Row(
                      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                      verticalAlignment = Alignment.CenterVertically
                    ) {
                      Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = BankBlueAccent,
                        modifier = Modifier.size(18.dp)
                      )
                      Spacer(modifier = Modifier.width(8.dp))
                      Text(
                        text = "Data synced and ready for scanning",
                        color = BankBluePrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                      )
                    }
                  }
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              // Categories snapshot card
              Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Column(modifier = Modifier.padding(16.dp)) {
                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = "Voucher Categories in Batch",
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = BankSlate700
                    )
                    IconButton(
                      onClick = { viewModel.setShowMasterListDialog(true) },
                      modifier = Modifier.size(32.dp)
                    ) {
                      Icon(
                        imageVector = Icons.Default.ListAlt,
                        contentDescription = "Inspect List",
                        tint = BankBlueAccent,
                        modifier = Modifier.size(20.dp)
                      )
                    }
                  }

                  Spacer(modifier = Modifier.height(10.dp))

                  Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                  ) {
                    CategoryChip(
                      label = "Bank to Bank",
                      sub = "Internal",
                      color = BankBlueLight,
                      textColor = BankBluePrimary,
                      modifier = Modifier.weight(1f)
                    )
                    CategoryChip(
                      label = "Other Bank",
                      sub = "External",
                      color = BankIndigoLight,
                      textColor = BankIndigo,
                      modifier = Modifier.weight(1f)
                    )
                    CategoryChip(
                      label = "Remittance",
                      sub = "နိုင်ငံခြားငွေလွှဲ",
                      color = BankEmeraldContainer,
                      textColor = BankEmerald,
                      modifier = Modifier.weight(1f)
                    )
                  }
                }
              }
            }

            // Bottom Anchored Start Button
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
            ) {
              Button(
                onClick = { viewModel.navigateTo(AppScreen.SCANNER) },
                colors = ButtonDefaults.buttonColors(containerColor = BankBlueDark),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                  .fillMaxWidth()
                  .height(58.dp)
                  .testTag("start_scanning_button")
              ) {
                Icon(
                  imageVector = Icons.Default.CameraAlt,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                  text = "Start Scanning",
                  fontSize = 18.sp,
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

  // Master List Inspector Bottom Sheet
  if (showMasterListDialog) {
    ModalBottomSheet(
      onDismissRequest = { viewModel.setShowMasterListDialog(false) },
      sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "Master List (${vouchers.size} Vouchers)",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = BankSlate800
          )
          Surface(
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFFEFF6FF)
          ) {
            Text(
              text = "Verified: ${summaryData.verifiedCount}",
              color = BankBluePrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
          modifier = Modifier
            .fillMaxWidth()
            .height(420.dp),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(vouchers) { voucher ->
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (voucher.isVerified) Color(0xFFF0FDF4) else Color(0xFFF8FAFC)
              ),
              border = if (voucher.isVerified) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF86EFAC)) else null,
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                      text = voucher.voucherNumber,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp,
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
                    text = "${voucher.senderName} ➔ ${voucher.receiverName}",
                    fontSize = 12.sp,
                    color = BankSlate600
                  )
                }
                Column(horizontalAlignment = Alignment.End) {
                  Text(
                    text = voucher.formattedAmount,
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = if (voucher.isVerified) BankEmerald else BankSlate700
                  )
                  if (voucher.isVerified) {
                    Text(
                      text = "Verified ✓",
                      fontSize = 11.sp,
                      fontWeight = FontWeight.Bold,
                      color = BankEmerald
                    )
                  }
                }
              }
            }
          }
        }
      }
    }
  }
}

@Composable
private fun CategoryChip(
  label: String,
  sub: String,
  color: Color,
  textColor: Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(color)
      .padding(vertical = 10.dp, horizontal = 6.dp),
    contentAlignment = Alignment.Center
  ) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
      Text(
        text = label,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold
      )
      Text(
        text = sub,
        color = textColor.copy(alpha = 0.8f),
        fontSize = 9.sp,
        fontWeight = FontWeight.Medium
      )
    }
  }
}
