package com.example.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Country
import com.example.ui.components.ImmersiveBackgroundContainer
import com.example.ui.theme.*
import com.example.ui.viewmodel.AuthFormState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
  formState: AuthFormState,
  onBackClick: () -> Unit,
  onFullNameChanged: (String) -> Unit,
  onPhoneNumberChanged: (String) -> Unit,
  onCountryCodeClick: () -> Unit,
  onLanguageSelected: (String) -> Unit,
  onContinueClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  var isLanguageDropdownExpanded by remember { mutableStateOf(false) }
  val supportedLanguages = listOf("English", "French", "German", "Japanese", "Chinese", "Hindi", "Spanish")

  val scrollState = rememberScrollState()

  ImmersiveBackgroundContainer(modifier = modifier) {
    Column(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding()
        .navigationBarsPadding()
        .verticalScroll(scrollState)
        .padding(horizontal = 24.dp, vertical = 16.dp),
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Column {
        // Top Navigation Icon in Immersive Glass style
        IconButton(
          onClick = onBackClick,
          modifier = Modifier
            .padding(bottom = 16.dp)
            .size(42.dp)
            .clip(CircleShape)
            .background(ImmersiveSurfaceElevated)
            .border(1.dp, ImmersiveBorder, CircleShape)
            .testTag("auth_back_button")
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = ImmersivePrimary
          )
        }

        // Header
        Text(
          text = "Create Profile",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 28.sp,
            color = ImmersiveTextPrimary
          )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
          text = "Enter your details to customize your Incredible India experience",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = ImmersiveTextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp
          )
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Full Name Field Card
        Text(
          text = "FULL NAME",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = ImmersiveTextSecondary,
            letterSpacing = 1.sp
          ),
          modifier = Modifier.padding(bottom = 8.dp)
        )

        OutlinedTextField(
          value = formState.fullName,
          onValueChange = onFullNameChanged,
          placeholder = { Text("e.g. Sarah Jenkins", color = ImmersiveTextTertiary) },
          trailingIcon = {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = "Full Name Icon",
              tint = ImmersiveTextSecondary
            )
          },
          singleLine = true,
          isError = formState.fullNameError != null,
          supportingText = {
            if (formState.fullNameError != null) {
              Text(
                text = formState.fullNameError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp
              )
            }
          },
          modifier = Modifier
            .fillMaxWidth()
            .testTag("full_name_input"),
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

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Number Field with Country Code Picker
        Text(
          text = "PHONE NUMBER",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = ImmersiveTextSecondary,
            letterSpacing = 1.sp
          ),
          modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
          verticalAlignment = Alignment.Top
        ) {
          // Country Code Button
          Surface(
            modifier = Modifier
              .height(56.dp)
              .clip(RoundedCornerShape(16.dp))
              .border(1.dp, ImmersiveBorder, RoundedCornerShape(16.dp))
              .clickable { onCountryCodeClick() }
              .testTag("country_code_selector"),
            color = ImmersiveSurfaceCard
          ) {
            Row(
              modifier = Modifier
                .fillMaxHeight()
                .padding(horizontal = 14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              Text(text = formState.country.flagEmoji, fontSize = 20.sp)
              Text(
                text = formState.country.dialCode,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                color = ImmersiveTextPrimary
              )
              Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = ImmersivePrimary,
                modifier = Modifier.size(20.dp)
              )
            }
          }

          // Number Input Field
          OutlinedTextField(
            value = formState.phoneNumber,
            onValueChange = onPhoneNumberChanged,
            placeholder = { Text("e.g. 9876543210", color = ImmersiveTextTertiary) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = formState.phoneError != null,
            supportingText = {
              if (formState.phoneError != null) {
                Text(
                  text = formState.phoneError,
                  color = MaterialTheme.colorScheme.error,
                  fontSize = 12.sp
                )
              }
            },
            modifier = Modifier
              .weight(1f)
              .testTag("phone_number_input"),
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preferred Language Dropdown
        Text(
          text = "PREFERRED AUDIO & TRANSLATION LANGUAGE",
          style = MaterialTheme.typography.labelSmall.copy(
            fontWeight = FontWeight.Bold,
            color = ImmersiveTextSecondary,
            letterSpacing = 0.8.sp
          ),
          modifier = Modifier.padding(bottom = 8.dp)
        )

        ExposedDropdownMenuBox(
          expanded = isLanguageDropdownExpanded,
          onExpandedChange = { isLanguageDropdownExpanded = !isLanguageDropdownExpanded },
          modifier = Modifier.fillMaxWidth()
        ) {
          OutlinedTextField(
            value = formState.preferredLanguage,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isLanguageDropdownExpanded) },
            modifier = Modifier
              .fillMaxWidth()
              .menuAnchor()
              .testTag("preferred_language_picker"),
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

          ExposedDropdownMenu(
            expanded = isLanguageDropdownExpanded,
            onDismissRequest = { isLanguageDropdownExpanded = false },
            modifier = Modifier.background(ImmersiveSurface)
          ) {
            supportedLanguages.forEach { language ->
              DropdownMenuItem(
                text = { Text(language, color = ImmersiveTextPrimary) },
                onClick = {
                  onLanguageSelected(language)
                  isLanguageDropdownExpanded = false
                }
              )
            }
          }
        }
      }

      // Bottom Submit Section
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        Button(
          onClick = onContinueClick,
          modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .testTag("auth_continue_button"),
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = ImmersivePrimary,
            contentColor = ImmersiveOnPrimary
          )
        ) {
          Text(
            text = "Continue to Explore",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }

        Text(
          text = "By continuing, you agree to official tourist terms & guidelines.",
          style = MaterialTheme.typography.bodySmall.copy(
            color = ImmersiveTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 16.sp
          )
        )
      }
    }
  }
}

