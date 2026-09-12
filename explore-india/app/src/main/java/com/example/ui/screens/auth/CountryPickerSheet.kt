package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Country
import com.example.ui.theme.RoyalPurplePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CountryPickerSheet(
  countries: List<Country>,
  selectedCountry: Country,
  searchQuery: String,
  onSearchQueryChanged: (String) -> Unit,
  onCountrySelected: (Country) -> Unit,
  onDismiss: () -> Unit
) {
  val filteredCountries = countries.filter {
    it.name.contains(searchQuery, ignoreCase = true) ||
      it.dialCode.contains(searchQuery, ignoreCase = true) ||
      it.code.contains(searchQuery, ignoreCase = true)
  }

  val popularCountries = filteredCountries.filter { it.isPopular }
  val allOtherCountries = filteredCountries.filter { !it.isPopular }

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    dragHandle = { BottomSheetDefaults.DragHandle() },
    containerColor = MaterialTheme.colorScheme.surface,
    shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(0.85f)
        .padding(horizontal = 20.dp)
        .navigationBarsPadding()
    ) {
      // Sheet Header
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text(
          text = "Select Country",
          style = MaterialTheme.typography.titleLarge.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
          )
        )
        IconButton(
          onClick = onDismiss,
          modifier = Modifier.testTag("close_country_picker")
        ) {
          Icon(
            imageVector = Icons.Default.Close,
            contentDescription = "Close"
          )
        }
      }

      // Search Bar
      OutlinedTextField(
        value = searchQuery,
        onValueChange = onSearchQueryChanged,
        placeholder = { Text("Search country...") },
        leadingIcon = {
          Icon(
            imageVector = Icons.Default.Search,
            contentDescription = "Search"
          )
        },
        singleLine = true,
        modifier = Modifier
          .fillMaxWidth()
          .testTag("country_search_input"),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = RoyalPurplePrimary,
          unfocusedBorderColor = MaterialTheme.colorScheme.outline
        )
      )

      Spacer(modifier = Modifier.height(16.dp))

      // Country List
      LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(4.dp)
      ) {
        if (searchQuery.isBlank() && popularCountries.isNotEmpty()) {
          item {
            Text(
              text = "Popular",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.padding(vertical = 8.dp)
            )
          }

          items(popularCountries) { country ->
            CountryRow(
              country = country,
              isSelected = country.dialCode == selectedCountry.dialCode && country.name == selectedCountry.name,
              onSelect = { onCountrySelected(country) }
            )
          }

          item {
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text(
              text = "All Countries",
              style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
              ),
              modifier = Modifier.padding(vertical = 8.dp)
            )
          }
        }

        items(if (searchQuery.isBlank()) allOtherCountries else filteredCountries) { country ->
          CountryRow(
            country = country,
            isSelected = country.dialCode == selectedCountry.dialCode && country.name == selectedCountry.name,
            onSelect = { onCountrySelected(country) }
          )
        }
      }
    }
  }
}

@Composable
private fun CountryRow(
  country: Country,
  isSelected: Boolean,
  onSelect: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(10.dp))
      .background(if (isSelected) RoyalPurplePrimary.copy(alpha = 0.08f) else Color.Transparent)
      .clickable { onSelect() }
      .padding(horizontal = 12.dp, vertical = 12.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = country.flagEmoji,
      fontSize = 22.sp,
      modifier = Modifier.padding(end = 12.dp)
    )

    Text(
      text = country.name,
      style = MaterialTheme.typography.bodyLarge.copy(
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
        color = MaterialTheme.colorScheme.onSurface
      ),
      modifier = Modifier.weight(1f)
    )

    Text(
      text = country.dialCode,
      style = MaterialTheme.typography.bodyMedium.copy(
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
      ),
      modifier = Modifier.padding(end = 8.dp)
    )

    if (isSelected) {
      Icon(
        imageVector = Icons.Default.Check,
        contentDescription = "Selected",
        tint = RoyalPurplePrimary,
        modifier = Modifier.size(20.dp)
      )
    }
  }
}
