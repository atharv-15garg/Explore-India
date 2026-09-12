package com.example.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyRate
import com.example.data.model.OfflineCityMap
import com.example.data.model.UserProfile
import com.example.ui.theme.IndiaMarigold
import com.example.ui.theme.IndiaTeal
import com.example.ui.theme.PurpleLavenderContainer
import com.example.ui.theme.RoyalPurplePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
  userProfile: UserProfile?,
  currencyRates: List<CurrencyRate>,
  selectedCurrency: CurrencyRate,
  currencyAmount: String,
  isConvertingFromInr: Boolean,
  offlineMaps: List<OfflineCityMap>,
  onCurrencySelected: (CurrencyRate) -> Unit,
  onCurrencyAmountChanged: (String) -> Unit,
  onToggleCurrencyDirection: () -> Unit,
  onToggleDownloadMap: (String) -> Unit,
  onLogoutClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isCurrencyDropdownOpen by remember { mutableStateOf(false) }

  val inputVal = currencyAmount.toDoubleOrNull() ?: 0.0
  val convertedResult = if (isConvertingFromInr) {
    if (selectedCurrency.rateToInr > 0) inputVal / selectedCurrency.rateToInr else 0.0
  } else {
    inputVal * selectedCurrency.rateToInr
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 100.dp)
  ) {
    // Header
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Text(
          text = "Traveler Profile & Tools",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground
          )
        )
        Text(
          text = "Currency exchange, offline maps & cultural guide",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
          )
        )
      }
    }

    // User Profile Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(PurpleLavenderContainer),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = userProfile?.countryFlag ?: "🇮🇳",
              fontSize = 28.sp
            )
          }

          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = userProfile?.fullName ?: "Foreign Tourist",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 18.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "${userProfile?.dialCode ?: "+91"} ${userProfile?.phoneNumber ?: ""}",
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
              text = "${userProfile?.countryName ?: "International"} • Language: ${userProfile?.preferredLanguage ?: "English"}",
              fontSize = 12.sp,
              color = RoyalPurplePrimary,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    // Currency Converter Section
    item {
      Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp)) {
        Text(
          text = "💱 Live Currency Converter",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = PurpleLavenderContainer.copy(alpha = 0.6f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              // Currency Dropdown
              ExposedDropdownMenuBox(
                expanded = isCurrencyDropdownOpen,
                onExpandedChange = { isCurrencyDropdownOpen = !isCurrencyDropdownOpen }
              ) {
                Surface(
                  shape = RoundedCornerShape(10.dp),
                  color = MaterialTheme.colorScheme.surface,
                  modifier = Modifier
                    .menuAnchor()
                    .clickable { isCurrencyDropdownOpen = true }
                    .testTag("currency_selector_box")
                ) {
                  Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(selectedCurrency.flag, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                      selectedCurrency.code,
                      fontWeight = FontWeight.Bold,
                      fontSize = 14.sp
                    )
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                  }
                }

                ExposedDropdownMenu(
                  expanded = isCurrencyDropdownOpen,
                  onDismissRequest = { isCurrencyDropdownOpen = false }
                ) {
                  currencyRates.forEach { rate ->
                    DropdownMenuItem(
                      text = { Text("${rate.flag} ${rate.code} - ${rate.name}") },
                      onClick = {
                        onCurrencySelected(rate)
                        isCurrencyDropdownOpen = false
                      }
                    )
                  }
                }
              }

              // Swap Button
              IconButton(
                onClick = onToggleCurrencyDirection,
                modifier = Modifier
                  .clip(CircleShape)
                  .background(RoyalPurplePrimary)
              ) {
                Icon(
                  imageVector = Icons.Default.SwapHoriz,
                  contentDescription = "Swap Direction",
                  tint = Color.White
                )
              }

              // Target INR Display
              Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface
              ) {
                Row(
                  modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Text("🇮🇳", fontSize = 18.sp)
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    "INR (₹)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amount Input Field
            OutlinedTextField(
              value = currencyAmount,
              onValueChange = onCurrencyAmountChanged,
              label = { Text(if (isConvertingFromInr) "Amount in INR (₹)" else "Amount in ${selectedCurrency.code} (${selectedCurrency.symbol})") },
              keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
              singleLine = true,
              shape = RoundedCornerShape(12.dp),
              colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = RoyalPurplePrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
              ),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("currency_amount_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Conversion Result Box
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = MaterialTheme.colorScheme.surface,
              modifier = Modifier.fillMaxWidth()
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Text(
                  text = "Converted Value",
                  fontSize = 12.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = if (isConvertingFromInr) {
                    "${selectedCurrency.symbol}${String.format("%.2f", convertedResult)} ${selectedCurrency.code}"
                  } else {
                    "₹${String.format("%.2f", convertedResult)} INR"
                  },
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 24.sp,
                  color = RoyalPurplePrimary
                )
                Text(
                  text = "1 ${selectedCurrency.code} = ₹${selectedCurrency.rateToInr} INR",
                  fontSize = 11.sp,
                  color = MaterialTheme.colorScheme.onSurfaceVariant
                )
              }
            }
          }
        }
      }
    }

    // Indian Banknotes Guide
    item {
      Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp)) {
        Text(
          text = "💵 Indian Rupee (INR) Banknote Guide",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          BanknoteChip("₹10", "Brown", "Sun Temple", Modifier.weight(1f))
          BanknoteChip("₹50", "Teal", "Hampi Stone", Modifier.weight(1f))
          BanknoteChip("₹100", "Lavender", "Rani ki Vav", Modifier.weight(1f))
          BanknoteChip("₹500", "Stone Grey", "Red Fort", Modifier.weight(1f))
        }
      }
    }

    // Offline Maps Section
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp)
      ) {
        Text(
          text = "🗺️ Offline City Maps",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        )
        Text(
          text = "Download vector map packs to navigate without cellular data",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }

    // Offline Maps Items
    items(offlineMaps) { map ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 4.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = map.cityName,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp
            )
            Text(
              text = "${map.downloadSizeMb} MB • Includes landmarks & offline navigation",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (map.downloadProgress > 0f && map.downloadProgress < 1f) {
              Spacer(modifier = Modifier.height(4.dp))
              LinearProgressIndicator(
                progress = { map.downloadProgress },
                modifier = Modifier.fillMaxWidth(0.8f),
                color = RoyalPurplePrimary
              )
            }
          }

          if (map.isDownloaded) {
            FilledTonalButton(
              onClick = { onToggleDownloadMap(map.id) },
              colors = ButtonDefaults.filledTonalButtonColors(containerColor = IndiaTeal.copy(alpha = 0.15f))
            ) {
              Icon(Icons.Default.Check, contentDescription = null, tint = IndiaTeal, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Saved", color = IndiaTeal, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          } else {
            OutlinedButton(
              onClick = { onToggleDownloadMap(map.id) },
              shape = RoundedCornerShape(10.dp)
            ) {
              Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("Download", fontSize = 12.sp)
            }
          }
        }
      }
    }

    // Cultural Etiquette Quick Guide
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
      ) {
        Text(
          text = "🙏 Cultural Etiquette for Foreign Guests",
          style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(8.dp))

        EtiquetteCard(
          icon = "🥿",
          title = "Footwear at Sacred Places",
          description = "Always remove shoes before entering Hindu temples, Sikh gurdwaras, and mosques. Socks are permitted and protect feet on hot marble."
        )

        Spacer(modifier = Modifier.height(8.dp))

        EtiquetteCard(
          icon = "🫱",
          title = "Right Hand Custom",
          description = "Use your right hand for eating food, giving money to shopkeepers, and accepting temple prasad/offerings."
        )

        Spacer(modifier = Modifier.height(8.dp))

        EtiquetteCard(
          icon = "💧",
          title = "Safe Drinking Water",
          description = "Always drink branded sealed bottled mineral water (Bisleri, Kinley, Aquafina) or verified UV-filtered water."
        )
      }
    }

    // Logout / Reset App Button
    item {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 24.dp),
        contentAlignment = Alignment.Center
      ) {
        OutlinedButton(
          onClick = onLogoutClick,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
        ) {
          Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(18.dp))
          Spacer(modifier = Modifier.width(8.dp))
          Text("Log Out & Reset Profile", fontWeight = FontWeight.SemiBold)
        }
      }
    }
  }
}

@Composable
private fun BanknoteChip(
  denom: String,
  colorName: String,
  monument: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier,
    shape = RoundedCornerShape(10.dp),
    color = MaterialTheme.colorScheme.surfaceVariant
  ) {
    Column(
      modifier = Modifier.padding(8.dp),
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Text(
        text = denom,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 14.sp,
        color = RoyalPurplePrimary
      )
      Text(
        text = colorName,
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      )
      Text(
        text = monument,
        fontSize = 10.sp,
        fontWeight = FontWeight.Medium,
        color = MaterialTheme.colorScheme.onSurface
      )
    }
  }
}

@Composable
private fun EtiquetteCard(
  icon: String,
  title: String,
  description: String
) {
  Card(
    modifier = Modifier.fillMaxWidth(),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier.padding(14.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      Text(icon, fontSize = 24.sp)
      Column {
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = description,
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant,
          lineHeight = 16.sp
        )
      }
    }
  }
}
