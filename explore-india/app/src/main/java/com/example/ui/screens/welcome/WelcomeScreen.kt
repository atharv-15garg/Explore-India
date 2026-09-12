package com.example.ui.screens.welcome

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.ImmersiveBackgroundContainer
import com.example.ui.theme.*

@Composable
fun WelcomeScreen(
  onGetStartedClick: () -> Unit,
  onLoginClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  ImmersiveBackgroundContainer(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
    ) {
      // Top Hero Image Container with Immersive UI aesthetic (rounded 32dp, subtle glass border, deep gradient)
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1.35f)
          .padding(start = 20.dp, end = 20.dp, top = 16.dp)
          .clip(RoundedCornerShape(32.dp))
          .background(ImmersiveSurfaceCard)
          .border(1.dp, ImmersiveBorder, RoundedCornerShape(32.dp))
      ) {
        Image(
          painter = painterResource(id = R.drawable.img_taj_mahal_hero),
          contentDescription = "Taj Mahal Welcome Banner",
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Gradient overlay for depth
        Box(
          modifier = Modifier
            .fillMaxSize()
            .background(
              Brush.verticalGradient(
                colors = listOf(
                  Color.Transparent,
                  Color.Black.copy(alpha = 0.4f),
                  Color.Black.copy(alpha = 0.85f)
                )
              )
            )
        )

        // Top Luminous Pill Badge
        Surface(
          color = ImmersivePrimary,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier
            .padding(18.dp)
            .align(Alignment.TopEnd)
        ) {
          Text(
            text = "🇮🇳 INCREDIBLE INDIA",
            color = ImmersiveOnPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
          )
        }

        // Overlay Bottom Title on Hero
        Column(
          modifier = Modifier
            .align(Alignment.BottomStart)
            .padding(20.dp)
        ) {
          Text(
            text = "Namaste & Welcome",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "Your intelligent companion for exploring India's heritage & wonders.",
            color = Color.White.copy(alpha = 0.75f),
            fontSize = 13.sp,
            lineHeight = 18.sp
          )
        }
      }

      // Bottom Content & Actions
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .weight(1f)
          .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Column(
          modifier = Modifier.fillMaxWidth()
        ) {
          Text(
            text = "Seamless Travel",
            style = MaterialTheme.typography.headlineMedium.copy(
              fontSize = 28.sp,
              fontWeight = FontWeight.ExtraBold,
              color = ImmersiveTextPrimary
            )
          )

          Spacer(modifier = Modifier.height(6.dp))

          Text(
            text = "Verified monuments, live maps, instant voice translator & 24/7 tourist helpline.",
            style = MaterialTheme.typography.bodyMedium.copy(
              fontSize = 14.sp,
              color = ImmersiveTextSecondary,
              lineHeight = 20.sp
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Key highlight pills in Immersive UI style
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            Surface(
              shape = RoundedCornerShape(12.dp),
              color = ImmersiveSurface,
              modifier = Modifier.border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(12.dp))
            ) {
              Text(
                text = "🏛️ Heritage",
                color = ImmersiveTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = ImmersiveSurface,
              modifier = Modifier.border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(12.dp))
            ) {
              Text(
                text = "🗣️ Translator",
                color = ImmersiveTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }

            Surface(
              shape = RoundedCornerShape(12.dp),
              color = ImmersiveSurface,
              modifier = Modifier.border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(12.dp))
            ) {
              Text(
                text = "🚨 SOS 24x7",
                color = EmergencyRed,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
              )
            }
          }
        }

        // Action Buttons
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          Button(
            onClick = onGetStartedClick,
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("welcome_get_started_button"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = ImmersivePrimary,
              contentColor = ImmersiveOnPrimary
            )
          ) {
            Text(
              text = "Get Started",
              fontSize = 16.sp,
              fontWeight = FontWeight.Bold
            )
          }

          OutlinedButton(
            onClick = onLoginClick,
            modifier = Modifier
              .fillMaxWidth()
              .height(54.dp)
              .testTag("welcome_login_button"),
            shape = RoundedCornerShape(16.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveBorder),
            colors = ButtonDefaults.outlinedButtonColors(
              containerColor = ImmersiveSurface,
              contentColor = ImmersiveTextPrimary
            )
          ) {
            Text(
              text = "Login",
              fontSize = 16.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }
    }
  }
}

