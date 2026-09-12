package com.example.ui.screens.home

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ForeignerReview
import com.example.data.model.HeritageSite
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SiteDetailSheet(
  site: HeritageSite,
  isSaved: Boolean,
  onToggleSave: () -> Unit,
  onPlayAudioGuide: (String) -> Unit,
  onBookTickets: (Context, String) -> Unit,
  onDismiss: () -> Unit
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()
  var isPlayingAudio by remember { mutableStateOf(false) }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    dragHandle = { BottomSheetDefaults.DragHandle(color = ImmersiveTextTertiary) },
    containerColor = ImmersiveSurface,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.92f)
        .navigationBarsPadding()
        .verticalScroll(scrollState)
    ) {
      // Header Image with Action Overlays
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .padding(horizontal = 16.dp)
          .clip(RoundedCornerShape(20.dp))
          .border(1.dp, ImmersiveBorder, RoundedCornerShape(20.dp))
      ) {
        Image(
          painter = painterResource(id = site.imageRes),
          contentDescription = site.name,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Category Tag
        Surface(
          color = ImmersivePrimary,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier
            .padding(12.dp)
            .align(Alignment.TopStart)
        ) {
          Text(
            text = "🏛️ ${site.category}",
            color = ImmersiveOnPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
          )
        }

        // Bookmark Action
        IconButton(
          onClick = onToggleSave,
          modifier = Modifier
            .padding(12.dp)
            .align(Alignment.TopEnd)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.6f))
            .border(1.dp, ImmersiveBorder, CircleShape)
            .testTag("detail_bookmark_button")
        ) {
          Icon(
            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = "Save Site",
            tint = if (isSaved) IndiaMarigold else Color.White
          )
        }
      }

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp)
      ) {
        // Title & Location
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.Top
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = site.name,
              style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.ExtraBold,
                fontSize = 24.sp,
                color = ImmersiveTextPrimary
              )
            )
            Text(
              text = "${site.hindiName} • ${site.city}, ${site.state}",
              style = MaterialTheme.typography.bodyMedium.copy(
                color = ImmersiveTextSecondary,
                fontWeight = FontWeight.Medium
              )
            )
          }

          // Rating Pill
          Surface(
            color = IndiaMarigold.copy(alpha = 0.15f),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.border(1.dp, IndiaMarigold.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
          ) {
            Row(
              modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Rating",
                tint = IndiaMarigold,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = "${site.rating}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ImmersiveTextPrimary
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Audio Guide Button
        Surface(
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ImmersiveBorder, RoundedCornerShape(16.dp)),
          shape = RoundedCornerShape(16.dp),
          color = ImmersivePrimaryContainer
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Headphones,
                contentDescription = "Audio Guide",
                tint = ImmersivePrimary
              )
              Column {
                Text(
                  text = "Smart Audio Guide",
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  color = ImmersivePrimary
                )
                Text(
                  text = "Audio walkthrough & historical insights",
                  fontSize = 12.sp,
                  color = ImmersiveTextSecondary
                )
              }
            }

            Button(
              onClick = {
                isPlayingAudio = !isPlayingAudio
                onPlayAudioGuide(site.audioGuideIntro)
              },
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = ImmersivePrimary,
                contentColor = ImmersiveOnPrimary
              ),
              contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
              Icon(
                imageVector = if (isPlayingAudio) Icons.Default.VolumeUp else Icons.Default.PlayArrow,
                contentDescription = "Play",
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(text = if (isPlayingAudio) "Playing" else "Listen", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Quick Stats Cards Grid
        Text(
          text = "Key Visitor Information",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = ImmersiveTextPrimary
          )
        )

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          InfoStatCard(
            icon = "🎟️",
            title = "Foreigner Ticket",
            value = if (site.foreignTicketInr > 0) "₹${site.foreignTicketInr} (~$${String.format("%.1f", site.foreignTicketInr / 86.5)})" else "Free Entry",
            subtext = "Local: ₹${site.localTicketInr}",
            modifier = Modifier.weight(1f)
          )
          InfoStatCard(
            icon = "⏰",
            title = "Opening Timings",
            value = site.timings,
            subtext = "Peak: ${site.peakRushHours}",
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          InfoStatCard(
            icon = "👔",
            title = "Dress Code",
            value = site.dressCode,
            subtext = "Sacred sanctity rules",
            modifier = Modifier.weight(1f)
          )
          InfoStatCard(
            icon = "📸",
            title = "Best Photo Spot",
            value = site.bestPhotoSpot,
            subtext = "Ideal lighting",
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Description & History
        Text(
          text = "About & Architectural History",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            color = ImmersiveTextPrimary
          )
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = site.description,
          style = MaterialTheme.typography.bodyMedium.copy(
            color = ImmersiveTextPrimary,
            lineHeight = 22.sp
          )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = site.history,
          style = MaterialTheme.typography.bodySmall.copy(
            color = ImmersiveTextSecondary,
            lineHeight = 20.sp
          )
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Important Entry Rules
        Surface(
          shape = RoundedCornerShape(16.dp),
          color = ImmersiveSurfaceElevated,
          modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(16.dp))
        ) {
          Column(modifier = Modifier.padding(14.dp)) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Entry Rules",
                tint = ImmersivePrimary,
                modifier = Modifier.size(20.dp)
              )
              Text(
                text = "Important Entry Rules & Security",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = ImmersiveTextPrimary
              )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = site.entryRules,
              fontSize = 13.sp,
              color = ImmersiveTextSecondary,
              lineHeight = 18.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Official Ticket Booking Button
        Button(
          onClick = { onBookTickets(context, site.officialBookingUrl) },
          modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .testTag("book_official_ticket_button"),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = ImmersivePrimary,
            contentColor = ImmersiveOnPrimary
          )
        ) {
          Icon(
            imageVector = Icons.Default.ConfirmationNumber,
            contentDescription = "Ticket",
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Book Official ASI Ticket Online",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
          )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Foreigner Reviews
        if (site.foreignReviews.isNotEmpty()) {
          Text(
            text = "Reviews from Foreign Travelers",
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              color = ImmersiveTextPrimary
            )
          )
          Spacer(modifier = Modifier.height(10.dp))

          site.foreignReviews.forEach { review ->
            ForeignReviewItem(review)
            Spacer(modifier = Modifier.height(10.dp))
          }
        }
      }
    }
  }
}

@Composable
private fun InfoStatCard(
  icon: String,
  title: String,
  value: String,
  subtext: String,
  modifier: Modifier = Modifier
) {
  Surface(
    modifier = modifier.border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(14.dp)),
    shape = RoundedCornerShape(14.dp),
    color = ImmersiveSurfaceElevated
  ) {
    Column(modifier = Modifier.padding(12.dp)) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Text(text = icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = title,
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = ImmersiveTextSecondary
        )
      }
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp,
        color = ImmersiveTextPrimary
      )
      Spacer(modifier = Modifier.height(2.dp))
      Text(
        text = subtext,
        fontSize = 11.sp,
        color = ImmersiveTextSecondary
      )
    }
  }
}

@Composable
private fun ForeignReviewItem(review: ForeignerReview) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = ImmersiveSurfaceElevated,
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(14.dp))
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(text = review.flagEmoji, fontSize = 16.sp)
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = review.author,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = ImmersiveTextPrimary
          )
          Text(
            text = " (${review.country})",
            fontSize = 12.sp,
            color = ImmersiveTextSecondary
          )
        }
        Text(
          text = "★ ${review.rating}",
          fontWeight = FontWeight.Bold,
          fontSize = 12.sp,
          color = IndiaMarigold
        )
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = "\"${review.comment}\"",
        fontSize = 13.sp,
        color = ImmersiveTextPrimary,
        lineHeight = 18.sp
      )

      if (review.tipForTourists.isNotBlank()) {
        Spacer(modifier = Modifier.height(6.dp))
        Text(
          text = "💡 Tip: ${review.tipForTourists}",
          fontSize = 12.sp,
          color = ImmersivePrimary,
          fontWeight = FontWeight.Medium
        )
      }
    }
  }
}

