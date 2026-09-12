package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
  @PrimaryKey val id: Int = 1,
  val fullName: String,
  val dialCode: String,
  val countryName: String,
  val countryFlag: String,
  val phoneNumber: String,
  val preferredLanguage: String,
  val isOnboarded: Boolean,
  val locationPermissionGranted: Boolean,
  val micPermissionGranted: Boolean
)

@Entity(tableName = "saved_sites")
data class SavedSiteEntity(
  @PrimaryKey val siteId: String,
  val savedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "downloaded_maps")
data class DownloadedMapEntity(
  @PrimaryKey val cityId: String,
  val cityName: String,
  val downloadedAt: Long = System.currentTimeMillis(),
  val sizeMb: Int
)

@Entity(tableName = "translation_history")
data class TranslationHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val sourceText: String,
  val translatedText: String,
  val phoneticText: String,
  val sourceLang: String,
  val targetLang: String,
  val timestamp: Long = System.currentTimeMillis()
)
