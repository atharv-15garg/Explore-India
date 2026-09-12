package com.example.data.model

data class Country(
  val name: String,
  val dialCode: String,
  val flagEmoji: String,
  val code: String,
  val isPopular: Boolean = false
)

data class UserProfile(
  val fullName: String = "",
  val dialCode: String = "+91",
  val countryName: String = "India",
  val countryFlag: String = "🇮🇳",
  val phoneNumber: String = "",
  val preferredLanguage: String = "English",
  val isOnboarded: Boolean = false,
  val locationPermissionGranted: Boolean = false,
  val micPermissionGranted: Boolean = false
)

data class HeritageSite(
  val id: String,
  val name: String,
  val hindiName: String,
  val city: String,
  val state: String,
  val category: String, // "UNESCO Heritage", "Forts & Palaces", "Temples & Spiritual", "Monuments"
  val description: String,
  val history: String,
  val timings: String,
  val peakRushHours: String,
  val entryRules: String,
  val dressCode: String,
  val foreignTicketInr: Int,
  val localTicketInr: Int,
  val officialBookingUrl: String,
  val rating: Double,
  val reviewsCount: Int,
  val isPopular: Boolean,
  val isSaved: Boolean = false,
  val imageRes: Int,
  val latitude: Double,
  val longitude: Double,
  val bestPhotoSpot: String,
  val audioGuideIntro: String,
  val foreignReviews: List<ForeignerReview> = emptyList()
)

data class ForeignerReview(
  val id: String,
  val author: String,
  val country: String,
  val flagEmoji: String,
  val rating: Double,
  val date: String,
  val comment: String,
  val tipForTourists: String
)

data class OfflineCityMap(
  val id: String,
  val cityName: String,
  val state: String,
  val downloadSizeMb: Int,
  val isDownloaded: Boolean = false,
  val monumentsCount: Int,
  val safetyScore: String, // e.g. "9.4 / 10"
  val downloadProgress: Float = 0f
)

data class EmergencyContact(
  val id: String,
  val name: String,
  val number: String,
  val subtitle: String,
  val description: String,
  val isDirectDial: Boolean = true,
  val isSmsReady: Boolean = false,
  val isNationalTouristHelpline: Boolean = false
)

data class EmbassyInfo(
  val country: String,
  val flagEmoji: String,
  val city: String,
  val address: String,
  val phone: String,
  val emergencyPhone: String,
  val email: String,
  val website: String
)

data class TouristPhrase(
  val id: String,
  val category: String, // "Greetings", "Bargaining & Auto", "Food & Water", "Directions", "Emergency"
  val english: String,
  val hindiScript: String,
  val hindiPhonetic: String,
  val french: String = "",
  val german: String = "",
  val japanese: String = "",
  val chinese: String = ""
)

data class CurrencyRate(
  val code: String,
  val name: String,
  val symbol: String,
  val flag: String,
  val rateToInr: Double // 1 Unit of foreign currency = X INR
)

data class TouristSafetyTip(
  val id: String,
  val title: String,
  val category: String, // "Scam Alert", "Health & Food", "Transport", "Cultural Etiquette"
  val description: String,
  val actionableDo: String,
  val actionableDont: String,
  val isUrgent: Boolean = false
)

data class MapPoi(
  val id: String,
  val name: String,
  val category: String, // "heritage", "restaurant", "hotel", "atm", "police", "hospital"
  val latitude: Double,
  val longitude: Double,
  val address: String,
  val rating: Double,
  val distanceKm: Double,
  val imageRes: Int = 0,
  val extraInfo: String = ""
)
