package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.EmergencyRed
import com.example.ui.theme.PurpleLavenderContainer
import com.example.ui.theme.RoyalPurplePrimary

@Composable
fun TravelAdvisoriesDialog(
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("travel_advisories_dialog")
    ) {
      Column(modifier = Modifier.padding(20.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "Advisories",
              tint = RoyalPurplePrimary
            )
            Text(
              text = "Live Travel Advisories",
              style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Close")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Advisory 1
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = PurpleLavenderContainer.copy(alpha = 0.7f),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "🏛️ Taj Mahal Friday Closure",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = RoyalPurplePrimary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Taj Mahal is closed for general tourists on Fridays (open only for afternoon Friday prayers). Plan Agra trips for Saturday-Thursday.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurface,
              lineHeight = 16.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Advisory 2
        Surface(
          shape = RoundedCornerShape(12.dp),
          color = MaterialTheme.colorScheme.surfaceVariant,
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(12.dp)) {
            Text(
              text = "💳 UPI for International Visitors",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "Foreign tourists arriving at IGI Airport Delhi can now get UPI-enabled prepaid wallets (Cheq / Transcorp) for QR code payments across all shops & autos.",
              fontSize = 12.sp,
              color = MaterialTheme.colorScheme.onSurfaceVariant,
              lineHeight = 16.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = onDismiss,
          modifier = Modifier.fillMaxWidth(),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary)
        ) {
          Text("Got It, Thanks!", fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
