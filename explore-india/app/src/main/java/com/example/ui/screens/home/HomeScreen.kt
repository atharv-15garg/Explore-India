package com.example.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.HeritageSite
import com.example.ui.theme.*

@Composable
fun HomeScreen(
  sites: List<HeritageSite>,
  savedSiteIds: Set<String>,
  searchQuery: String,
  selectedCity: String,
  selectedCategory: String,
  onSearchQueryChanged: (String) -> Unit,
  onCitySelected: (String) -> Unit,
  onCategorySelected: (String) -> Unit,
  onSiteClick: (HeritageSite) -> Unit,
  onToggleSaveSite: (String) -> Unit,
  onQuickChipClick: (String) -> Unit,
  onNotificationClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val cities = listOf("All", "Agra", "New Delhi", "Jaipur", "Varanasi")
  val categories = listOf("All", "UNESCO Heritage", "Forts & Palaces", "Temples & Spiritual", "Monuments")

  val filteredSites = sites.filter { site ->
    val matchesSearch = site.name.contains(searchQuery, ignoreCase = true) ||
      site.city.contains(searchQuery, ignoreCase = true) ||
      site.category.contains(searchQuery, ignoreCase = true) ||
      site.hindiName.contains(searchQuery, ignoreCase = true)

    val matchesCity = selectedCity == "All" || site.city.contains(selectedCity, ignoreCase = true)
    val matchesCategory = selectedCategory == "All" || site.category.equals(selectedCategory, ignoreCase = true)

    matchesSearch && matchesCity && matchesCategory
  }

  val popularSites = sites.filter { it.isPopular }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(ImmersiveBackground),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // Header & Search
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        // App Title & Advisory Bell
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            // Header decorative glow badge
            Box(
              modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(ImmersiveSurfaceElevated)
                .border(1.dp, ImmersiveBorder, CircleShape),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = "Explore Icon",
                tint = ImmersivePrimary,
                modifier = Modifier.size(22.dp)
              )
            }

            Column {
              Text(
                text = "Explore India",
                style = MaterialTheme.typography.headlineMedium.copy(
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 24.sp,
                  color = ImmersiveTextPrimary
                )
              )
              Text(
                text = "Monuments & Cultural Wonders",
                style = MaterialTheme.typography.bodySmall.copy(
                  color = ImmersiveTextSecondary,
                  fontSize = 13.sp
                )
              )
            }
          }

          IconButton(
            onClick = onNotificationClick,
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(ImmersiveSurfaceElevated)
              .border(1.dp, ImmersiveBorder, CircleShape)
              .testTag("home_notification_bell")
          ) {
            BadgedBox(
              badge = {
                Badge(
                  containerColor = ImmersivePrimary,
                  contentColor = ImmersiveOnPrimary
                ) {
                  Text("2", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Travel Advisories",
                tint = ImmersivePrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Search Input
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchQueryChanged,
          placeholder = { Text("Search monuments, cities, temples...", color = ImmersiveTextTertiary) },
          leadingIcon = {
            Icon(
              imageVector = Icons.Default.Search,
              contentDescription = "Search",
              tint = ImmersiveTextSecondary
            )
          },
          trailingIcon = {
            if (searchQuery.isNotEmpty()) {
              IconButton(onClick = { onSearchQueryChanged("") }) {
                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = ImmersiveTextSecondary)
              }
            }
          },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("home_search_bar"),
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = ImmersiveSurface,
            unfocusedContainerColor = ImmersiveSurfaceCard,
            focusedBorderColor = ImmersivePrimary,
            unfocusedBorderColor = ImmersiveBorder,
            focusedTextColor = ImmersiveTextPrimary,
            unfocusedTextColor = ImmersiveTextPrimary
          )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Feature Chips
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          item {
            QuickFilterChip(
              icon = "🏛️",
              label = "Heritage Sites",
              onClick = { onQuickChipClick("heritage") }
            )
          }
          item {
            QuickFilterChip(
              icon = "🚨",
              label = "SOS 24x7",
              onClick = { onQuickChipClick("sos") },
              isAlert = true
            )
          }
          item {
            QuickFilterChip(
              icon = "🗣️",
              label = "Translator",
              onClick = { onQuickChipClick("translator") }
            )
          }
          item {
            QuickFilterChip(
              icon = "💱",
              label = "Currency (INR)",
              onClick = { onQuickChipClick("currency") }
            )
          }
          item {
            QuickFilterChip(
              icon = "🗺️",
              label = "Offline Maps",
              onClick = { onQuickChipClick("offline_maps") }
            )
          }
        }
      }
    }

    // Featured / Popular Golden Triangle Carousel (when no search active)
    if (searchQuery.isBlank() && selectedCity == "All" && selectedCategory == "All") {
      item {
        Column(modifier = Modifier.fillMaxWidth().padding(top = 6.dp, bottom = 16.dp)) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(horizontal = 20.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "TOP ATTRACTIONS",
              style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                color = ImmersiveTextSecondary
              )
            )
            Text(
              text = "Golden Triangle",
              style = MaterialTheme.typography.labelSmall.copy(
                color = ImmersivePrimary,
                fontWeight = FontWeight.Bold
              )
            )
          }

          LazyRow(
            contentPadding = PaddingValues(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 6.dp)
          ) {
            items(popularSites) { site ->
              FeaturedSiteCard(
                site = site,
                isSaved = savedSiteIds.contains(site.id),
                onSiteClick = { onSiteClick(site) },
                onToggleSave = { onToggleSaveSite(site.id) }
              )
            }
          }
        }
      }
    }

    // City Filter Pills
    item {
      Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 6.dp)) {
        Text(
          text = "FILTER BY DESTINATION",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            letterSpacing = 1.sp,
            color = ImmersiveTextSecondary
          )
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
          items(cities) { city ->
            FilterChip(
              selected = selectedCity == city,
              onClick = { onCitySelected(city) },
              label = { Text(city, fontSize = 13.sp) },
              shape = RoundedCornerShape(12.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = ImmersivePrimary,
                selectedLabelColor = ImmersiveOnPrimary,
                containerColor = ImmersiveSurface,
                labelColor = ImmersiveTextSecondary
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selectedCity == city,
                borderColor = if (selectedCity == city) ImmersivePrimary else ImmersiveBorder
              )
            )
          }
        }
      }
    }

    // Category Filter Pills
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 6.dp)
      ) {
        items(categories) { category ->
          FilterChip(
            selected = selectedCategory == category,
            onClick = { onCategorySelected(category) },
            label = { Text(category, fontSize = 13.sp) },
            shape = RoundedCornerShape(12.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = ImmersivePrimaryContainer,
              selectedLabelColor = ImmersivePrimary,
              containerColor = ImmersiveSurface,
              labelColor = ImmersiveTextSecondary
            ),
            border = FilterChipDefaults.filterChipBorder(
              enabled = true,
              selected = selectedCategory == category,
              borderColor = if (selectedCategory == category) ImmersivePrimary.copy(alpha = 0.6f) else ImmersiveBorderSubtle
            )
          )
        }
      }
    }

    // All Sights Section Title
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "Monuments & Cultural Sites",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = ImmersiveTextPrimary
          )
        )
        Text(
          text = "${filteredSites.size} places",
          style = MaterialTheme.typography.bodySmall.copy(
            color = ImmersiveTextSecondary
          )
        )
      }
    }

    // Site Items List
    if (filteredSites.isEmpty()) {
      item {
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp),
          contentAlignment = Alignment.Center
        ) {
          Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "🔍", fontSize = 40.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "No places found matching '$searchQuery'",
              fontWeight = FontWeight.Medium,
              color = ImmersiveTextSecondary
            )
          }
        }
      }
    } else {
      items(filteredSites) { site ->
        SiteListItemCard(
          site = site,
          isSaved = savedSiteIds.contains(site.id),
          onSiteClick = { onSiteClick(site) },
          onToggleSave = { onToggleSaveSite(site.id) }
        )
      }
    }

    // Cultural Advice & Safety Footer Card
    item {
      Surface(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp)
          .border(1.dp, ImmersiveBorder, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        color = ImmersiveSurface
      ) {
        Row(
          modifier = Modifier.padding(18.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(RoundedCornerShape(14.dp))
              .background(ImmersivePrimaryContainer),
            contentAlignment = Alignment.Center
          ) {
            Text("🇮🇳", fontSize = 22.sp)
          }
          Column(modifier = Modifier.weight(1f)) {
            Text(
              text = "Express Entry for Foreign Visitors",
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp,
              color = ImmersivePrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "Foreign tourist tickets often have dedicated express fast-track lanes at ASI monuments including Taj Mahal & Red Fort.",
              fontSize = 12.sp,
              color = ImmersiveTextSecondary,
              lineHeight = 16.sp
            )
          }
        }
      }
    }
  }
}

@Composable
private fun QuickFilterChip(
  icon: String,
  label: String,
  onClick: () -> Unit,
  isAlert: Boolean = false
) {
  Surface(
    shape = RoundedCornerShape(14.dp),
    color = if (isAlert) EmergencyRedContainer.copy(alpha = 0.4f) else ImmersiveSurface,
    modifier = Modifier
      .clip(RoundedCornerShape(14.dp))
      .border(
        width = 1.dp,
        color = if (isAlert) EmergencyRed.copy(alpha = 0.4f) else ImmersiveBorderSubtle,
        shape = RoundedCornerShape(14.dp)
      )
      .clickable { onClick() }
  ) {
    Row(
      modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      Text(icon, fontSize = 14.sp)
      Text(
        text = label,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        color = if (isAlert) EmergencyRed else ImmersiveTextPrimary
      )
    }
  }
}

@Composable
private fun FeaturedSiteCard(
  site: HeritageSite,
  isSaved: Boolean,
  onSiteClick: () -> Unit,
  onToggleSave: () -> Unit
) {
  Card(
    modifier = Modifier
      .width(290.dp)
      .height(225.dp)
      .clip(RoundedCornerShape(28.dp))
      .border(1.dp, ImmersiveBorder, RoundedCornerShape(28.dp))
      .clickable { onSiteClick() },
    shape = RoundedCornerShape(28.dp),
    colors = CardDefaults.cardColors(containerColor = ImmersiveSurfaceCard),
    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
  ) {
    Box(modifier = Modifier.fillMaxSize()) {
      Image(
        painter = painterResource(id = site.imageRes),
        contentDescription = site.name,
        modifier = Modifier.fillMaxSize(),
        contentScale = ContentScale.Crop
      )

      // Immersive Depth Gradient Overlay
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(
            Brush.verticalGradient(
              colors = listOf(
                Color.Black.copy(alpha = 0.25f),
                Color.Transparent,
                Color.Black.copy(alpha = 0.88f)
              )
            )
          )
      )

      // Top City Tag & Bookmark
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Surface(
          color = ImmersivePrimary,
          shape = RoundedCornerShape(8.dp)
        ) {
          Text(
            text = site.city.uppercase(),
            color = ImmersiveOnPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
          )
        }

        IconButton(
          onClick = onToggleSave,
          modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.55f))
            .border(1.dp, ImmersiveBorder, CircleShape)
        ) {
          Icon(
            imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
            contentDescription = "Save",
            tint = if (isSaved) IndiaMarigold else Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      }

      // Bottom Info Section
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .align(Alignment.BottomStart)
          .padding(14.dp)
      ) {
        Text(
          text = site.name,
          color = Color.White,
          fontWeight = FontWeight.Bold,
          fontSize = 17.sp,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (site.foreignTicketInr > 0) "Foreigner: ₹${site.foreignTicketInr}" else "Free Admission",
            color = Color.White.copy(alpha = 0.85f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "Rating",
              tint = IndiaMarigold,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${site.rating}",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}

@Composable
private fun SiteListItemCard(
  site: HeritageSite,
  isSaved: Boolean,
  onSiteClick: () -> Unit,
  onToggleSave: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 6.dp)
      .clip(RoundedCornerShape(24.dp))
      .border(1.dp, ImmersiveBorderSubtle, RoundedCornerShape(24.dp))
      .clickable { onSiteClick() },
    shape = RoundedCornerShape(24.dp),
    colors = CardDefaults.cardColors(containerColor = ImmersiveSurface),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.spacedBy(14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Thumbnail with 16dp rounded corners
      Image(
        painter = painterResource(id = site.imageRes),
        contentDescription = site.name,
        modifier = Modifier
          .size(86.dp)
          .clip(RoundedCornerShape(16.dp)),
        contentScale = ContentScale.Crop
      )

      // Content
      Column(modifier = Modifier.weight(1f)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = site.name,
            style = MaterialTheme.typography.titleMedium.copy(
              fontWeight = FontWeight.Bold,
              fontSize = 15.sp,
              color = ImmersiveTextPrimary
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
          )

          IconButton(
            onClick = onToggleSave,
            modifier = Modifier.size(32.dp)
          ) {
            Icon(
              imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
              contentDescription = "Save",
              tint = if (isSaved) IndiaMarigold else ImmersiveTextSecondary,
              modifier = Modifier.size(20.dp)
            )
          }
        }

        Text(
          text = "${site.city}, ${site.state} • ${site.category}",
          style = MaterialTheme.typography.bodySmall.copy(
            color = ImmersiveTextSecondary,
            fontSize = 12.sp
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (site.foreignTicketInr > 0) "Foreign Entry: ₹${site.foreignTicketInr}" else "Free Entry",
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp,
            color = ImmersivePrimary
          )

          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Star,
              contentDescription = "Rating",
              tint = IndiaMarigold,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
              text = "${site.rating}",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              color = ImmersiveTextPrimary
            )
          }
        }
      }
    }
  }
}

