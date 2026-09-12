package com.example.ui.screens.sos

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmbassyInfo
import com.example.data.model.EmergencyContact
import com.example.data.model.TouristSafetyTip
import com.example.ui.theme.*

@Composable
fun SosScreen(
  emergencyContacts: List<EmergencyContact>,
  embassies: List<EmbassyInfo>,
  safetyTips: List<TouristSafetyTip>,
  isCountingDown: Boolean,
  countdownSeconds: Int,
  isSosTriggered: Boolean,
  isSirenActive: Boolean,
  onStartSos: () -> Unit,
  onCancelSos: () -> Unit,
  onToggleSiren: () -> Unit,
  onSendSms: (Context) -> Unit,
  onDialNumber: (Context, String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Helplines & SOS, 1: Embassies, 2: Scams & Safety

  val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = 1.12f,
    animationSpec = infiniteRepeatable(
      animation = tween(800, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "sos_scale"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(ImmersiveBackground),
    contentPadding = PaddingValues(bottom = 100.dp)
  ) {
    // Top Bar
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Text(
          text = "Emergency SOS & Helplines",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = ImmersiveTextPrimary
          )
        )
        Text(
          text = "24x7 Foreign Tourist Assistance & Safety",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = ImmersiveTextSecondary,
            fontSize = 14.sp
          )
        )
      }
    }

    // Big Instant SOS Trigger Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp)
          .border(
            1.dp,
            if (isSosTriggered || isCountingDown) EmergencyRed.copy(alpha = 0.6f) else ImmersiveBorder,
            RoundedCornerShape(28.dp)
          ),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (isSosTriggered || isCountingDown) EmergencyRedContainer.copy(alpha = 0.5f) else ImmersiveSurfaceCard
        )
      ) {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          if (isCountingDown) {
            Text(
              text = "TRIGGERING SOS IN",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = EmergencyRed
            )
            Spacer(modifier = Modifier.height(12.dp))
            Box(
              modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(EmergencyRed),
              contentAlignment = Alignment.Center
            ) {
              Text(
                text = "$countdownSeconds",
                color = Color.White,
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold
              )
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedButton(
              onClick = onCancelSos,
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, EmergencyRed),
              colors = ButtonDefaults.outlinedButtonColors(contentColor = EmergencyRed)
            ) {
              Text("Cancel SOS", fontWeight = FontWeight.Bold)
            }
          } else if (isSosTriggered) {
            Text(
              text = "🚨 SOS ACTIVE!",
              fontWeight = FontWeight.ExtraBold,
              fontSize = 20.sp,
              color = EmergencyRed
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Emergency coordinators alert ready with live GPS location.",
              textAlign = TextAlign.Center,
              fontSize = 13.sp,
              color = ImmersiveTextPrimary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Button(
                onClick = { onSendSms(context) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EmergencyRed, contentColor = Color.White)
              ) {
                Icon(Icons.Default.Sms, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("SMS Location", fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = { onDialNumber(context, "112") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ImmersivePrimary, contentColor = ImmersiveOnPrimary)
              ) {
                Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Call 112", fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(onClick = onCancelSos) {
              Text("Dismiss SOS Alert", color = ImmersiveTextSecondary)
            }
          } else {
            // Default Idle State
            Box(
              modifier = Modifier
                .size((110 * pulseScale).dp)
                .clip(CircleShape)
                .background(EmergencyRed)
                .clickable { onStartSos() }
                .testTag("instant_sos_trigger_button"),
              contentAlignment = Alignment.Center
            ) {
              Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = "SOS",
                  tint = Color.White,
                  modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = "SOS",
                  color = Color.White,
                  fontSize = 18.sp,
                  fontWeight = FontWeight.ExtraBold
                )
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
              text = "Tap for Instant Emergency Alert",
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = ImmersiveTextPrimary
            )
            Text(
              text = "Auto-attaches current GPS coordinates to SMS & dialer",
              fontSize = 12.sp,
              color = ImmersiveTextSecondary,
              textAlign = TextAlign.Center
            )
          }
        }
      }
    }

    // Tab Switcher (Helplines / Foreign Embassies / Scams)
    item {
      TabRow(
        selectedTabIndex = selectedTab,
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 10.dp),
        containerColor = Color.Transparent,
        contentColor = ImmersivePrimary,
        divider = {
          HorizontalDivider(color = ImmersiveBorderSubtle)
        }
      ) {
        Tab(
          selected = selectedTab == 0,
          onClick = { selectedTab = 0 },
          text = {
            Text(
              "Helplines",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = if (selectedTab == 0) ImmersivePrimary else ImmersiveTextSecondary
            )
          }
        )
        Tab(
          selected = selectedTab == 1,
          onClick = { selectedTab = 1 },
          text = {
            Text(
              "Embassies",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = if (selectedTab == 1) ImmersivePrimary else ImmersiveTextSecondary
            )
          }
        )
        Tab(
          selected = selectedTab == 2,
          onClick = { selectedTab = 2 },
          text = {
            Text(
              "Scams & Safety",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = if (selectedTab == 2) ImmersivePrimary else ImmersiveTextSecondary
            )
          }
        )
      }
    }

    // Tab Content
    when (selectedTab) {
      0 -> {
        // Emergency Contacts List
        items(emergencyContacts) { contact ->
          EmergencyContactCard(
            contact = contact,
            onCall = { onDialNumber(context, contact.number) },
            onSms = { onSendSms(context) }
          )
        }
      }
      1 -> {
        // Foreign Embassies Directory
        item {
          Text(
            text = "Diplomatic Missions in New Delhi",
            style = MaterialTheme.typography.labelLarge.copy(
              fontWeight = FontWeight.Bold,
              color = ImmersiveTextPrimary
            ),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
          )
        }
        items(embassies) { embassy ->
          EmbassyCard(
            embassy = embassy,
            onCall = { onDialNumber(context, embassy.emergencyPhone) }
          )
        }
      }
      2 -> {
        // Safety Tips & Scam Alerts
        items(safetyTips) { tip ->
          SafetyTipCard(tip = tip)
        }
      }
    }
  }
}

@Composable
private fun EmergencyContactCard(
  contact: EmergencyContact,
  onCall: () -> Unit,
  onSms: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .border(1.dp, if (contact.isNationalTouristHelpline) ImmersivePrimary.copy(alpha = 0.5f) else ImmersiveBorderSubtle, RoundedCornerShape(20.dp)),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(
      containerColor = if (contact.isNationalTouristHelpline) ImmersivePrimaryContainer.copy(alpha = 0.4f) else ImmersiveSurface
    )
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = contact.name,
              style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = ImmersiveTextPrimary
              )
            )
            if (contact.isNationalTouristHelpline) {
              Spacer(modifier = Modifier.width(6.dp))
              Surface(
                color = ImmersivePrimary,
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = "TOURIST",
                  color = ImmersiveOnPrimary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
          Text(
            text = contact.subtitle,
            style = MaterialTheme.typography.bodySmall.copy(
              color = if (contact.isNationalTouristHelpline) ImmersivePrimary else ImmersiveTextSecondary,
              fontWeight = FontWeight.SemiBold,
              fontSize = 12.sp
            )
          )
        }

        // Dial Action Button
        FilledIconButton(
          onClick = onCall,
          colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = if (contact.isNationalTouristHelpline) ImmersivePrimary else EmergencyRed,
            contentColor = if (contact.isNationalTouristHelpline) ImmersiveOnPrimary else Color.White
          ),
          modifier = Modifier.testTag("call_${contact.number}_button")
        ) {
          Icon(Icons.Default.Phone, contentDescription = "Call ${contact.number}")
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = contact.description,
        style = MaterialTheme.typography.bodySmall.copy(
          color = ImmersiveTextSecondary,
          fontSize = 12.sp,
          lineHeight = 18.sp
        )
      )

      if (contact.isSmsReady) {
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
          onClick = onSms,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveBorder),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = ImmersiveSurface,
            contentColor = ImmersiveTextPrimary
          )
        ) {
          Icon(Icons.Default.ShareLocation, contentDescription = null, modifier = Modifier.size(16.dp), tint = ImmersivePrimary)
          Spacer(modifier = Modifier.width(6.dp))
          Text("Send Current Location to ${contact.number}", fontSize = 12.sp)
        }
      }
    }
  }
}

@Composable
private fun EmbassyCard(
  embassy: EmbassyInfo,
  onCall: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(20.dp)),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = embassy.flagEmoji, fontSize = 24.sp)
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = embassy.country,
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = ImmersiveTextPrimary
            )
            Text(
              text = "Embassy in ${embassy.city}",
              fontSize = 12.sp,
              color = ImmersiveTextSecondary
            )
          }
        }

        IconButton(onClick = onCall) {
          Icon(
            imageVector = Icons.Default.Call,
            contentDescription = "Call Embassy",
            tint = ImmersivePrimary
          )
        }
      }

      Spacer(modifier = Modifier.height(8.dp))

      Text(
        text = "📍 ${embassy.address}",
        fontSize = 12.sp,
        color = ImmersiveTextSecondary
      )
      Text(
        text = "📞 Emergency Line: ${embassy.emergencyPhone}",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = ImmersivePrimary
      )
    }
  }
}

@Composable
private fun SafetyTipCard(tip: TouristSafetyTip) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(20.dp)),
    shape = RoundedCornerShape(20.dp),
    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = tip.title,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          color = if (tip.isUrgent) EmergencyRed else ImmersiveTextPrimary
        )
        Surface(
          color = ImmersivePrimaryContainer,
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = tip.category,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = ImmersivePrimary,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(6.dp))

      Text(
        text = tip.description,
        fontSize = 13.sp,
        color = ImmersiveTextSecondary,
        lineHeight = 18.sp
      )

      Spacer(modifier = Modifier.height(10.dp))

      Surface(
        color = IndiaTealContainer.copy(alpha = 0.3f),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, IndiaTeal.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Text(
            text = "✅ DO: ${tip.actionableDo}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = IndiaTeal
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "❌ DON'T: ${tip.actionableDont}",
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = EmergencyRed
          )
        }
      }
    }
  }
}

