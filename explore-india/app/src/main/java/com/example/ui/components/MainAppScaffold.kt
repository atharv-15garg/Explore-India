package com.example.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

data class NavigationItem(
  val label: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val testTag: String
)

@Composable
fun MainAppScaffold(
  selectedTab: Int,
  onTabSelected: (Int) -> Unit,
  content: @Composable (PaddingValues) -> Unit
) {
  val navItems = listOf(
    NavigationItem("Home", Icons.Filled.Home, Icons.Outlined.Home, "tab_home"),
    NavigationItem("Maps", Icons.Filled.Map, Icons.Outlined.Map, "tab_maps"),
    NavigationItem("SOS", Icons.Filled.Warning, Icons.Outlined.Warning, "tab_sos"),
    NavigationItem("Translator", Icons.Filled.Translate, Icons.Outlined.Translate, "tab_translator"),
    NavigationItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, "tab_profile")
  )

  Scaffold(
    containerColor = ImmersiveBackground,
    bottomBar = {
      Surface(
        color = ImmersiveSurface,
        modifier = Modifier
          .fillMaxWidth()
          .border(
            width = 1.dp,
            color = ImmersiveBorderSubtle,
            shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
          )
      ) {
        NavigationBar(
          containerColor = Color.Transparent,
          tonalElevation = 0.dp,
          modifier = Modifier.fillMaxWidth()
        ) {
          navItems.forEachIndexed { index, item ->
            val isSelected = selectedTab == index
            val isSos = index == 2

            NavigationBarItem(
              selected = isSelected,
              onClick = { onTabSelected(index) },
              icon = {
                Icon(
                  imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                  contentDescription = item.label,
                  tint = when {
                    isSos && isSelected -> EmergencyRed
                    isSos -> EmergencyRed.copy(alpha = 0.8f)
                    isSelected -> ImmersivePrimary
                    else -> ImmersiveTextSecondary
                  }
                )
              },
              label = {
                Text(
                  text = item.label,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                  fontSize = 11.sp,
                  color = when {
                    isSos && isSelected -> EmergencyRed
                    isSelected -> ImmersivePrimary
                    else -> ImmersiveTextSecondary
                  }
                )
              },
              colors = NavigationBarItemDefaults.colors(
                indicatorColor = when {
                  isSos -> EmergencyRedContainer.copy(alpha = 0.5f)
                  else -> ImmersivePrimaryContainer
                },
                selectedIconColor = if (isSos) EmergencyRed else ImmersivePrimary,
                unselectedIconColor = ImmersiveTextSecondary,
                selectedTextColor = if (isSos) EmergencyRed else ImmersivePrimary,
                unselectedTextColor = ImmersiveTextSecondary
              ),
              modifier = Modifier.testTag(item.testTag)
            )
          }
        }
      }
    }
  ) { innerPadding ->
    content(innerPadding)
  }
}

