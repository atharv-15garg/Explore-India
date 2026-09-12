package com.example.ui.screens.translator

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TouristPhrase
import com.example.ui.theme.PurpleLavenderContainer
import com.example.ui.theme.RoyalPurplePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TranslatorScreen(
  sourceText: String,
  sourceLang: String,
  targetLang: String,
  translatedResult: String,
  translatedPhonetic: String,
  isTranslating: Boolean,
  selectedCategory: String,
  phrases: List<TouristPhrase>,
  onSourceTextChanged: (String) -> Unit,
  onSourceLangChanged: (String) -> Unit,
  onTargetLangChanged: (String) -> Unit,
  onTranslateClick: () -> Unit,
  onCategorySelected: (String) -> Unit,
  onSpeak: (String, Boolean) -> Unit,
  modifier: Modifier = Modifier
) {
  val clipboardManager = LocalClipboardManager.current
  var isMicListening by remember { mutableStateOf(false) }

  val sourceLanguages = listOf("English", "French", "German", "Japanese", "Chinese", "Spanish")
  val targetLanguages = listOf("Hindi", "Tamil", "Bengali")

  val phraseCategories = listOf("All", "Greetings", "Bargaining & Auto", "Food & Water", "Directions", "Emergency")

  val filteredPhrases = phrases.filter {
    selectedCategory == "All" || it.category == selectedCategory
  }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background),
    contentPadding = PaddingValues(bottom = 100.dp)
  ) {
    // Header
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .statusBarsPadding()
          .padding(horizontal = 20.dp, vertical = 12.dp)
      ) {
        Text(
          text = "Voice & Text Translator",
          style = MaterialTheme.typography.headlineMedium.copy(
            fontWeight = FontWeight.ExtraBold,
            fontSize = 24.sp,
            color = MaterialTheme.colorScheme.onBackground
          )
        )
        Text(
          text = "Translate phrases with phonetic pronunciation & audio",
          style = MaterialTheme.typography.bodyMedium.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 14.sp
          )
        )
      }
    }

    // Language Selector Bar
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = sourceLang,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = RoyalPurplePrimary
          )

          Icon(
            imageVector = Icons.Default.SwapHoriz,
            contentDescription = "Swap Languages",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
          )

          Text(
            text = "$targetLang (🇮🇳)",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = RoyalPurplePrimary
          )
        }
      }
    }

    // Text Input & Mic Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          OutlinedTextField(
            value = sourceText,
            onValueChange = onSourceTextChanged,
            placeholder = { Text("Type here or tap mic (e.g. 'How much does this cost?')") },
            modifier = Modifier
              .fillMaxWidth()
              .height(110.dp)
              .testTag("translator_input_field"),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = RoyalPurplePrimary,
              unfocusedBorderColor = MaterialTheme.colorScheme.outline
            )
          )

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            // Voice Mic Button
            IconButton(
              onClick = {
                isMicListening = !isMicListening
                if (isMicListening) {
                  onSourceTextChanged("How much is this auto rickshaw to India Gate?")
                }
              },
              modifier = Modifier
                .clip(CircleShape)
                .background(if (isMicListening) RoyalPurplePrimary else PurpleLavenderContainer)
                .testTag("voice_mic_button")
            ) {
              Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Voice Input",
                tint = if (isMicListening) Color.White else RoyalPurplePrimary
              )
            }

            // Translate Action Button
            Button(
              onClick = onTranslateClick,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = RoyalPurplePrimary),
              enabled = sourceText.isNotBlank() && !isTranslating,
              modifier = Modifier.testTag("translate_button")
            ) {
              if (isTranslating) {
                CircularProgressIndicator(
                  color = Color.White,
                  modifier = Modifier.size(18.dp),
                  strokeWidth = 2.dp
                )
              } else {
                Icon(Icons.Default.Translate, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Translate", fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    // Translation Output Card
    if (translatedResult.isNotBlank()) {
      item {
        Card(
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = PurpleLavenderContainer),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "Hindi Translation",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = RoyalPurplePrimary
              )

              Row {
                IconButton(
                  onClick = { onSpeak(translatedResult, true) },
                  modifier = Modifier.testTag("speak_translation_button")
                ) {
                  Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Speak Hindi",
                    tint = RoyalPurplePrimary
                  )
                }

                IconButton(
                  onClick = {
                    clipboardManager.setText(AnnotatedString(translatedResult))
                  }
                ) {
                  Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy Translation",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                  )
                }
              }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
              text = translatedResult,
              fontWeight = FontWeight.ExtraBold,
              fontSize = 24.sp,
              color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = "🗣️ Pronounce: \"$translatedPhonetic\"",
              fontWeight = FontWeight.SemiBold,
              fontSize = 15.sp,
              color = RoyalPurplePrimary
            )
          }
        }
      }
    }

    // Phrasebook Header
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 8.dp)
      ) {
        Text(
          text = "Tourist Phrasebook",
          style = MaterialTheme.typography.titleMedium.copy(
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
          )
        )
        Text(
          text = "Essential phrases for shopping, auto-rickshaws, food & safety",
          style = MaterialTheme.typography.bodySmall.copy(
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        )
      }
    }

    // Phrase Category Filter Chips
    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(bottom = 8.dp)
      ) {
        items(phraseCategories) { cat ->
          FilterChip(
            selected = selectedCategory == cat,
            onClick = { onCategorySelected(cat) },
            label = { Text(cat, fontSize = 12.sp) },
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = RoyalPurplePrimary,
              selectedLabelColor = Color.White
            )
          )
        }
      }
    }

    // Phrases List
    items(filteredPhrases) { phrase ->
      PhrasebookItemCard(
        phrase = phrase,
        onSpeak = { onSpeak(phrase.hindiScript, true) }
      )
    }
  }
}

@Composable
private fun PhrasebookItemCard(
  phrase: TouristPhrase,
  onSpeak: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .padding(horizontal = 20.dp, vertical = 5.dp),
    shape = RoundedCornerShape(14.dp),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column(modifier = Modifier.weight(1f)) {
        Text(
          text = phrase.english,
          fontWeight = FontWeight.Bold,
          fontSize = 14.sp,
          color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = phrase.hindiScript,
          fontWeight = FontWeight.SemiBold,
          fontSize = 16.sp,
          color = RoyalPurplePrimary
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "Say: ${phrase.hindiPhonetic}",
          fontSize = 12.sp,
          color = MaterialTheme.colorScheme.onSurfaceVariant
        )
      }

      IconButton(
        onClick = onSpeak,
        modifier = Modifier
          .clip(CircleShape)
          .background(PurpleLavenderContainer)
          .testTag("phrase_speak_button")
      ) {
        Icon(
          imageVector = Icons.Default.VolumeUp,
          contentDescription = "Speak Phrase",
          tint = RoyalPurplePrimary,
          modifier = Modifier.size(20.dp)
        )
      }
    }
  }
}
