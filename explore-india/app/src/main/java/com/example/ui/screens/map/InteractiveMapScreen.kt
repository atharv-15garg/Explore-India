package com.example.ui.screens.map

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.border
import com.example.data.model.HeritageSite
import com.example.data.model.MapPoi
import com.example.ui.theme.*

@Composable
fun InteractiveMapScreen(
  pois: List<MapPoi>,
  heritageSites: List<HeritageSite>,
  searchQuery: String,
  selectedCategory: String,
  mapLayerType: String,
  selectedPoi: MapPoi?,
  onSearchQueryChanged: (String) -> Unit,
  onCategorySelected: (String) -> Unit,
  onLayerTypeChanged: (String) -> Unit,
  onSelectPoi: (MapPoi?) -> Unit,
  onOpenSiteDetail: (HeritageSite) -> Unit,
  onBookTickets: (Context, String) -> Unit,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  var scale by remember { mutableFloatStateOf(1f) }
  var offset by remember { mutableStateOf(Offset.Zero) }

  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 8f,
    targetValue = 28f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_radius"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.7f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1500, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulse_alpha"
  )

  val categories = listOf(
    "all" to "All Places",
    "heritage" to "🏛️ Heritage Sites",
    "restaurant" to "🍛 Dining",
    "hotel" to "🏨 Hotels",
    "atm" to "🏧 ATMs",
    "police" to "🚨 Tourist Police"
  )

  val filteredPois = pois.filter { poi ->
    val matchesCategory = selectedCategory == "all" || poi.category == selectedCategory
    val matchesSearch = poi.name.contains(searchQuery, ignoreCase = true) ||
      poi.address.contains(searchQuery, ignoreCase = true)
    matchesCategory && matchesSearch
  }

  val mapCanvasBg = when (mapLayerType) {
    "Satellite" -> Color(0xFF132015)
    "Terrain" -> Color(0xFF18161A)
    else -> Color(0xFF0D0F14)
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(mapCanvasBg)
  ) {
    // Custom Interactive Vector Map Canvas
    Canvas(
      modifier = Modifier
        .fillMaxSize()
        .pointerInput(Unit) {
          detectTransformGestures { _, pan, zoom, _ ->
            scale = (scale * zoom).coerceIn(0.6f, 3.5f)
            offset = Offset(offset.x + pan.x, offset.y + pan.y)
          }
        }
        .pointerInput(Unit) {
          detectTapGestures { tapOffset ->
            // Check tap on any POI
            // Reset selection or select closest
            onSelectPoi(null)
          }
        }
    ) {
      drawMapBackground(
        layerType = mapLayerType,
        scale = scale,
        offset = offset,
        pulseRadius = pulseRadius,
        pulseAlpha = pulseAlpha
      )
    }

    // POI Overlay Pins rendered on top of Map
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
      val density = LocalDensity.current.density
      val centerX = maxWidth.value / 2
      val centerY = maxHeight.value / 2

      // Positions mapping for demonstration in Delhi NCR / Agra / Jaipur region
      filteredPois.forEachIndexed { index, poi ->
        val xOffset = when (poi.id) {
          "poi1" -> 160f // Taj Mahal (South-East)
          "poi2" -> 0f   // India Gate (Center)
          "poi3" -> -140f // Hawa Mahal (West)
          "poi4" -> 220f  // Varanasi (East)
          "poi5" -> 140f  // Peshawri
          "poi6" -> 20f   // Karim's
          "poi7" -> -30f  // Imperial Hotel
          "poi8" -> -40f  // SBI ATM
          "poi9" -> 35f   // Police post
          else -> (index * 40f - 100f)
        }
        val yOffset = when (poi.id) {
          "poi1" -> 180f
          "poi2" -> -20f
          "poi3" -> 110f
          "poi4" -> 240f
          "poi5" -> 190f
          "poi6" -> -80f
          "poi7" -> -40f
          "poi8" -> -60f
          "poi9" -> -10f
          else -> (index * 30f - 50f)
        }

        val pinX = (centerX + (xOffset * scale) + (offset.x / density)).dp
        val pinY = (centerY + (yOffset * scale) + (offset.y / density)).dp

        MapPoiPin(
          poi = poi,
          isSelected = selectedPoi?.id == poi.id,
          onClick = { onSelectPoi(poi) },
          modifier = Modifier.offset(x = pinX - 20.dp, y = pinY - 40.dp)
        )
      }
    }

    // Top Search & Controls Overlay
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .statusBarsPadding()
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
      // Search Bar
      Surface(
        shape = RoundedCornerShape(16.dp),
        color = ImmersiveSurface,
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, ImmersiveBorder, RoundedCornerShape(16.dp))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search",
            tint = ImmersivePrimary
          )
          Spacer(modifier = Modifier.width(8.dp))
          OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            placeholder = { Text("Search monuments, ATMs, police...", color = ImmersiveTextTertiary) },
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = Color.Transparent,
              unfocusedBorderColor = Color.Transparent,
              focusedTextColor = ImmersiveTextPrimary,
              unfocusedTextColor = ImmersiveTextPrimary
            ),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("map_search_input")
          )
          if (searchQuery.isNotEmpty()) {
            IconButton(onClick = { onSearchQueryChanged("") }) {
              Icon(Icons.Default.Clear, contentDescription = "Clear", tint = ImmersiveTextSecondary)
            }
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Category Chips Row
      LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        items(categories) { (key, label) ->
          val isSelected = selectedCategory == key
          FilterChip(
            selected = isSelected,
            onClick = { onCategorySelected(key) },
            label = { Text(label, fontSize = 12.sp) },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ImmersivePrimary,
              selectedLabelColor = ImmersiveOnPrimary,
              containerColor = ImmersiveSurface,
              labelColor = ImmersiveTextSecondary
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = isSelected,
              borderColor = if (isSelected) ImmersivePrimary else ImmersiveBorderSubtle
            )
          )
        }
      }
    }

    // Floating Map Layer & GPS Controls
    Column(
      modifier = Modifier
        .align(Alignment.TopEnd)
        .padding(top = 150.dp, end = 16.dp),
      verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      // Map Layer Toggle
      SmallFloatingActionButton(
        onClick = {
          val next = when (mapLayerType) {
            "Standard" -> "Satellite"
            "Satellite" -> "Terrain"
            else -> "Standard"
          }
          onLayerTypeChanged(next)
        },
        containerColor = ImmersiveSurface,
        contentColor = ImmersivePrimary,
        shape = CircleShape,
        modifier = Modifier.border(1.dp, ImmersiveBorder, CircleShape)
      ) {
        Icon(
          imageVector = Icons.Default.Layers,
          contentDescription = "Map Layers ($mapLayerType)"
        )
      }

      // Re-center GPS
      SmallFloatingActionButton(
        onClick = {
          scale = 1f
          offset = Offset.Zero
        },
        containerColor = ImmersiveSurface,
        contentColor = ImmersivePrimary,
        shape = CircleShape,
        modifier = Modifier.border(1.dp, ImmersiveBorder, CircleShape)
      ) {
        Icon(
          imageVector = Icons.Default.MyLocation,
          contentDescription = "My Location"
        )
      }

      // Zoom in
      SmallFloatingActionButton(
        onClick = { scale = (scale * 1.3f).coerceAtMost(3.5f) },
        containerColor = ImmersiveSurface,
        contentColor = ImmersiveTextPrimary,
        shape = CircleShape,
        modifier = Modifier.border(1.dp, ImmersiveBorder, CircleShape)
      ) {
        Icon(Icons.Default.Add, contentDescription = "Zoom In")
      }

      // Zoom out
      SmallFloatingActionButton(
        onClick = { scale = (scale / 1.3f).coerceAtLeast(0.6f) },
        containerColor = ImmersiveSurface,
        contentColor = ImmersiveTextPrimary,
        shape = CircleShape,
        modifier = Modifier.border(1.dp, ImmersiveBorder, CircleShape)
      ) {
        Icon(Icons.Default.Remove, contentDescription = "Zoom Out")
      }
    }

    // Offline Map Active Badge
    Surface(
      color = IndiaTealContainer,
      shape = RoundedCornerShape(12.dp),
      modifier = Modifier
        .align(Alignment.TopStart)
        .padding(top = 150.dp, start = 16.dp)
        .border(1.dp, IndiaTeal.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    ) {
      Row(
        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        Icon(
          imageVector = Icons.Default.CloudDone,
          contentDescription = "Offline Ready",
          tint = IndiaTeal,
          modifier = Modifier.size(16.dp)
        )
        Text(
          text = "Offline Mode Ready",
          color = IndiaTeal,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Selected POI Bottom Card Sheet
    if (selectedPoi != null) {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomCenter)
          .padding(start = 16.dp, end = 16.dp, bottom = 90.dp)
          .border(1.dp, ImmersiveBorder, RoundedCornerShape(24.dp))
          .testTag("map_poi_details_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceCard)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            if (selectedPoi.imageRes != 0) {
              Image(
                painter = painterResource(id = selectedPoi.imageRes),
                contentDescription = selectedPoi.name,
                modifier = Modifier
                  .size(64.dp)
                  .clip(RoundedCornerShape(14.dp)),
                contentScale = ContentScale.Crop
              )
            } else {
              Box(
                modifier = Modifier
                  .size(56.dp)
                  .clip(RoundedCornerShape(14.dp))
                  .background(ImmersivePrimaryContainer),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = when (selectedPoi.category) {
                    "restaurant" -> "🍛"
                    "hotel" -> "🏨"
                    "atm" -> "🏧"
                    "police" -> "🚨"
                    else -> "🏛️"
                  },
                  fontSize = 24.sp
                )
              }
            }

            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = selectedPoi.name,
                style = MaterialTheme.typography.titleMedium.copy(
                  fontWeight = FontWeight.Bold,
                  fontSize = 16.sp,
                  color = ImmersiveTextPrimary
                )
              )
              Text(
                text = selectedPoi.address,
                style = MaterialTheme.typography.bodySmall.copy(
                  color = ImmersiveTextSecondary,
                  fontSize = 12.sp
                )
              )
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "${selectedPoi.extraInfo} • ${selectedPoi.distanceKm} km away",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = ImmersivePrimary
              )
            }

            IconButton(onClick = { onSelectPoi(null) }) {
              Icon(Icons.Default.Close, contentDescription = "Close", tint = ImmersiveTextSecondary)
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            val matchingSite = heritageSites.firstOrNull { it.name.contains(selectedPoi.name.take(6), ignoreCase = true) }
            if (matchingSite != null) {
              Button(
                onClick = { onOpenSiteDetail(matchingSite) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                  containerColor = ImmersivePrimary,
                  contentColor = ImmersiveOnPrimary
                )
              ) {
                Icon(Icons.Default.Info, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("View Details", fontSize = 13.sp, fontWeight = FontWeight.Bold)
              }
            }

            OutlinedButton(
              onClick = {
                // Trigger directions via map intent
                val uri = "geo:${selectedPoi.latitude},${selectedPoi.longitude}?q=${selectedPoi.name}"
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(uri))
                try {
                  context.startActivity(intent)
                } catch (e: Exception) {
                  // Handled gracefully
                }
              },
              modifier = Modifier.weight(1f),
              shape = RoundedCornerShape(12.dp),
              border = androidx.compose.foundation.BorderStroke(1.dp, ImmersiveBorder),
              colors = ButtonDefaults.outlinedButtonColors(
                containerColor = ImmersiveSurface,
                contentColor = ImmersiveTextPrimary
              )
            ) {
              Icon(Icons.Default.Directions, contentDescription = null, modifier = Modifier.size(16.dp), tint = ImmersivePrimary)
              Spacer(modifier = Modifier.width(6.dp))
              Text("Directions", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            }
          }
        }
      }
    }
  }
}

@Composable
private fun MapPoiPin(
  poi: MapPoi,
  isSelected: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val iconEmoji = when (poi.category) {
    "restaurant" -> "🍛"
    "hotel" -> "🏨"
    "atm" -> "🏧"
    "police" -> "🚨"
    else -> "🏛️"
  }

  val pinColor = when (poi.category) {
    "police" -> EmergencyRed
    "atm" -> IndiaTeal
    "restaurant" -> IndiaMarigold
    else -> ImmersivePrimary
  }

  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (isSelected) pinColor else ImmersiveSurface,
    modifier = modifier
      .clip(RoundedCornerShape(14.dp))
      .border(
        width = 1.dp,
        color = if (isSelected) pinColor else ImmersiveBorder,
        shape = RoundedCornerShape(14.dp)
      )
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Text(iconEmoji, fontSize = 14.sp)
      Text(
        text = poi.name.take(14),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = if (isSelected) (if (poi.category == "police" || poi.category == "restaurant" || poi.category == "atm") Color.White else ImmersiveOnPrimary) else ImmersiveTextPrimary
      )
    }
  }
}

private fun DrawScope.drawMapBackground(
  layerType: String,
  scale: Float,
  offset: Offset,
  pulseRadius: Float,
  pulseAlpha: Float
) {
  val width = size.width
  val height = size.height
  val cx = (width / 2 + offset.x)
  val cy = (height / 2 + offset.y)

  val roadColor = if (layerType == "Satellite") Color(0xFF223322) else Color(0xFF1E222A)
  val waterColor = if (layerType == "Satellite") Color(0xFF102230) else Color(0xFF172B3D)
  val parkColor = if (layerType == "Satellite") Color(0xFF152A1C) else Color(0xFF132219)

  // Draw River Yamuna / Ganges curved ribbon
  val riverPath = Path().apply {
    moveTo(cx - 300 * scale, cy - 450 * scale)
    cubicTo(
      cx - 100 * scale, cy - 200 * scale,
      cx + 150 * scale, cy + 50 * scale,
      cx + 250 * scale, cy + 400 * scale
    )
  }
  drawPath(
    path = riverPath,
    color = waterColor,
    style = Stroke(width = 42f * scale)
  )

  // Draw Lush Green Parks (Kartavya Path, Mehtab Bagh, Lodhi Gardens)
  drawCircle(
    color = parkColor,
    radius = 120f * scale,
    center = Offset(cx, cy)
  )
  drawCircle(
    color = parkColor,
    radius = 70f * scale,
    center = Offset(cx + 150 * scale, cy + 180 * scale)
  )

  // Radial Road Network (New Delhi Connaught Place Radial Roads & Ring Roads)
  val radialCount = 8
  for (i in 0 until radialCount) {
    val angle = Math.toRadians((i * (360.0 / radialCount)))
    val endX = cx + (Math.cos(angle) * 500 * scale).toFloat()
    val endY = cy + (Math.sin(angle) * 500 * scale).toFloat()
    drawLine(
      color = roadColor,
      start = Offset(cx, cy),
      end = Offset(endX, endY),
      strokeWidth = 14f * scale
    )
  }

  // Inner & Outer Ring Roads
  drawCircle(
    color = roadColor,
    radius = 180f * scale,
    center = Offset(cx, cy),
    style = Stroke(width = 16f * scale)
  )
  drawCircle(
    color = roadColor,
    radius = 320f * scale,
    center = Offset(cx, cy),
    style = Stroke(width = 20f * scale)
  )

  // User Current Location Glowing Pulse (Connaught Place Center)
  drawCircle(
    color = ImmersivePrimary.copy(alpha = pulseAlpha),
    radius = pulseRadius * scale * 2f,
    center = Offset(cx, cy),
    style = Fill
  )
  drawCircle(
    color = Color.White,
    radius = 10f * scale,
    center = Offset(cx, cy),
    style = Fill
  )
  drawCircle(
    color = ImmersivePrimary,
    radius = 7f * scale,
    center = Offset(cx, cy),
    style = Fill
  )
}
