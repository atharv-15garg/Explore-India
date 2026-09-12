package com.example.ui.screens.permissions

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.ImmersiveBackgroundContainer
import com.example.ui.theme.*

@Composable
fun PermissionsScreen(
  onComplete: (locationGranted: Boolean, micGranted: Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestMultiplePermissions()
  ) { results ->
    val locationGranted = results[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
      results[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    val micGranted = results[Manifest.permission.RECORD_AUDIO] == true
    onComplete(locationGranted, micGranted)
  }

  val scrollState = rememberScrollState()

  ImmersiveBackgroundContainer(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Spacer(modifier = Modifier.height(36.dp))

        // Hero Glowing Pin Icon
        Box(
          modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(ImmersivePrimaryContainer)
            .border(1.5.dp, ImmersivePrimary.copy(alpha = 0.5f), CircleShape),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "Permissions Pin",
            tint = ImmersivePrimary,
            modifier = Modifier.size(48.dp)
          )
        }

        Spacer(modifier = Modifier.height(28.dp))

        Text(
          text = "Allow Permissions",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            color = ImmersiveTextPrimary
          ),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "To enable interactive offline maps, nearby monument discovery, and instant voice translation.",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = ImmersiveTextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            textAlign = TextAlign.Center
          )
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Permission Items
        PermissionItemCard(
          icon = Icons.Default.LocationOn,
          title = "Location Access",
          description = "To show nearby heritage places, offline maps & walking routes."
        )

        Spacer(modifier = Modifier.height(16.dp))

        PermissionItemCard(
          icon = Icons.Default.Mic,
          title = "Microphone Access",
          description = "To enable real-time foreign voice translation & audio pronunciation."
        )
      }

      // Action Buttons
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 36.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = {
            permissionLauncher.launch(
              arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.RECORD_AUDIO
              )
            )
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("allow_all_permissions_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = ImmersivePrimary,
            contentColor = ImmersiveOnPrimary
          )
        ) {
          Text(
            text = "Allow All Permissions",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }

        TextButton(
          onClick = {
            onComplete(false, false)
          },
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("not_now_permissions_button")
        ) {
          Text(
            text = "Not Now",
            fontSize = 15.sp,
            color = ImmersiveTextSecondary,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}

@Composable
private fun PermissionItemCard(
  icon: ImageVector,
  title: String,
  description: String
) {
  Surface(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, ImmersiveBorder, RoundedCornerShape(24.dp)),
    shape = RoundedCornerShape(24.dp),
    color = ImmersiveSurface
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(18.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      Box(
        modifier = Modifier
          .size(48.dp)
          .clip(RoundedCornerShape(16.dp))
          .background(ImmersivePrimaryContainer),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = title,
          tint = ImmersivePrimary,
          modifier = Modifier.size(24.dp)
        )
      }

      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = title,
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            color = ImmersiveTextPrimary
          )
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
          text = description,
          style = MaterialTheme.typography.bodySmall.copy(
            color = ImmersiveTextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
          )
        )
      }
    }
  }
}

