package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.database.AppDatabase
import com.example.data.model.*
import com.example.data.repository.TouristRepository
import com.example.utils.TtsHelper
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

enum class AppDestination {
  WELCOME,
  AUTH,
  PERMISSIONS,
  MAIN_APP
}

data class AuthFormState(
  val fullName: String = "",
  val phoneNumber: String = "",
  val country: Country = Country("India", "+91", "🇮🇳", "IN", isPopular = true),
  val preferredLanguage: String = "English",
  val fullNameError: String? = null,
  val phoneError: String? = null,
  val isValid: Boolean = false
)

class ExploreIndiaViewModel(application: Application) : AndroidViewModel(application) {

  private val repository: TouristRepository
  val ttsHelper: TtsHelper = TtsHelper(application)

  init {
    val database = AppDatabase.getDatabase(application)
    repository = TouristRepository(database.appDao())
  }

  // App Navigation Flow State
  private val _destination = MutableStateFlow(AppDestination.WELCOME)
  val destination: StateFlow<AppDestination> = _destination.asStateFlow()

  // Bottom Navigation Active Tab
  private val _activeTab = MutableStateFlow(0) // 0: Home, 1: Explore/Map, 2: SOS, 3: Translator, 4: Profile
  val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

  // User Profile
  val userProfile = repository.getUserProfile().stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    null
  )

  // Auth Form State
  private val _authForm = MutableStateFlow(AuthFormState())
  val authForm: StateFlow<AuthFormState> = _authForm.asStateFlow()

  // Country Picker Modal
  private val _isCountryPickerOpen = MutableStateFlow(false)
  val isCountryPickerOpen: StateFlow<Boolean> = _isCountryPickerOpen.asStateFlow()

  private val _countrySearchQuery = MutableStateFlow("")
  val countrySearchQuery: StateFlow<String> = _countrySearchQuery.asStateFlow()

  // Saved / Bookmarked Sites
  val savedSiteIds = repository.getSavedSiteIds().map { it.toSet() }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    emptySet()
  )

  // Heritage Sites & Search/Filters
  val allSites = repository.getHeritageSites()

  private val _homeSearchQuery = MutableStateFlow("")
  val homeSearchQuery: StateFlow<String> = _homeSearchQuery.asStateFlow()

  private val _selectedCity = MutableStateFlow("All")
  val selectedCity: StateFlow<String> = _selectedCity.asStateFlow()

  private val _selectedCategory = MutableStateFlow("All")
  val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

  private val _selectedSiteDetail = MutableStateFlow<HeritageSite?>(null)
  val selectedSiteDetail: StateFlow<HeritageSite?> = _selectedSiteDetail.asStateFlow()

  // Interactive Map State
  private val _mapSearchQuery = MutableStateFlow("")
  val mapSearchQuery: StateFlow<String> = _mapSearchQuery.asStateFlow()

  private val _mapCategoryFilter = MutableStateFlow("all")
  val mapCategoryFilter: StateFlow<String> = _mapCategoryFilter.asStateFlow()

  private val _mapLayerType = MutableStateFlow("Standard") // Standard, Satellite, Terrain
  val mapLayerType: StateFlow<String> = _mapLayerType.asStateFlow()

  private val _selectedMapPoi = MutableStateFlow<MapPoi?>(null)
  val selectedMapPoi: StateFlow<MapPoi?> = _selectedMapPoi.asStateFlow()

  val allPois = repository.getMapPois()

  // Offline Maps
  private val _offlineMapsList = MutableStateFlow(repository.getOfflineCityMaps())
  val offlineMapsList: StateFlow<List<OfflineCityMap>> = _offlineMapsList.asStateFlow()

  // Emergency SOS State
  private val _isSosCountingDown = MutableStateFlow(false)
  val isSosCountingDown: StateFlow<Boolean> = _isSosCountingDown.asStateFlow()

  private val _sosCountdownTimer = MutableStateFlow(3)
  val sosCountdownTimer: StateFlow<Int> = _sosCountdownTimer.asStateFlow()

  private val _isSosTriggered = MutableStateFlow(false)
  val isSosTriggered: StateFlow<Boolean> = _isSosTriggered.asStateFlow()

  private val _isSirenActive = MutableStateFlow(false)
  val isSirenActive: StateFlow<Boolean> = _isSirenActive.asStateFlow()

  val emergencyContacts = repository.getEmergencyContacts()
  val foreignEmbassies = repository.getForeignEmbassies()
  val touristSafetyTips = repository.getTouristSafetyTips()

  // Translator State
  private val _translatorSourceText = MutableStateFlow("")
  val translatorSourceText: StateFlow<String> = _translatorSourceText.asStateFlow()

  private val _translatorSourceLang = MutableStateFlow("English")
  val translatorSourceLang: StateFlow<String> = _translatorSourceLang.asStateFlow()

  private val _translatorTargetLang = MutableStateFlow("Hindi")
  val translatorTargetLang: StateFlow<String> = _translatorTargetLang.asStateFlow()

  private val _translatedResult = MutableStateFlow("")
  val translatedResult: StateFlow<String> = _translatedResult.asStateFlow()

  private val _translatedPhonetic = MutableStateFlow("")
  val translatedPhonetic: StateFlow<String> = _translatedPhonetic.asStateFlow()

  private val _isTranslating = MutableStateFlow(false)
  val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

  private val _selectedPhraseCategory = MutableStateFlow("All")
  val selectedPhraseCategory: StateFlow<String> = _selectedPhraseCategory.asStateFlow()

  val allPhrases = repository.getTouristPhrases()

  // Currency Converter State
  val currencyRates = repository.getCurrencyRates()

  private val _selectedCurrency = MutableStateFlow(currencyRates.first())
  val selectedCurrency: StateFlow<CurrencyRate> = _selectedCurrency.asStateFlow()

  private val _currencyInputAmount = MutableStateFlow("100")
  val currencyInputAmount: StateFlow<String> = _currencyInputAmount.asStateFlow()

  private val _isConvertingFromInr = MutableStateFlow(false)
  val isConvertingFromInr: StateFlow<Boolean> = _isConvertingFromInr.asStateFlow()

  // Travel Advisories popover
  private val _showAdvisoriesDialog = MutableStateFlow(false)
  val showAdvisoriesDialog: StateFlow<Boolean> = _showAdvisoriesDialog.asStateFlow()

  // Initial user check
  init {
    viewModelScope.launch {
      userProfile.collect { profile ->
        if (profile != null && profile.isOnboarded) {
          _destination.value = AppDestination.MAIN_APP
        }
      }
    }
  }

  // Navigation handlers
  fun navigateTo(destination: AppDestination) {
    _destination.value = destination
  }

  fun setTab(index: Int) {
    _activeTab.value = index
  }

  // Auth Form Handlers
  fun onFullNameChanged(name: String) {
    _authForm.update { current ->
      val error = if (name.isNotBlank() && name.trim().length < 2) "Name must be at least 2 characters" else null
      val updated = current.copy(fullName = name, fullNameError = error)
      updated.copy(isValid = validateAuthForm(updated))
    }
  }

  fun onPhoneNumberChanged(number: String) {
    // Digits only
    val digits = number.filter { it.isDigit() }
    _authForm.update { current ->
      val error = when {
        digits.isEmpty() -> null
        digits.startsWith("0") -> "Phone number cannot start with 0"
        digits.length > 10 -> "Phone number must be exactly 10 digits"
        digits.length != 10 -> "Please enter a valid 10-digit number"
        else -> null
      }
      val updated = current.copy(phoneNumber = digits, phoneError = error)
      updated.copy(isValid = validateAuthForm(updated))
    }
  }

  fun onCountrySelected(country: Country) {
    _authForm.update { current ->
      val updated = current.copy(country = country)
      updated.copy(isValid = validateAuthForm(updated))
    }
    _isCountryPickerOpen.value = false
  }

  fun onLanguageSelected(language: String) {
    _authForm.update { it.copy(preferredLanguage = language) }
  }

  fun setCountryPickerOpen(isOpen: Boolean) {
    _isCountryPickerOpen.value = isOpen
    if (!isOpen) _countrySearchQuery.value = ""
  }

  fun setCountrySearchQuery(query: String) {
    _countrySearchQuery.value = query
  }

  private fun validateAuthForm(state: AuthFormState): Boolean {
    val isNameValid = state.fullName.trim().length >= 2
    val isPhoneValid = state.phoneNumber.length == 10 && !state.phoneNumber.startsWith("0")
    return isNameValid && isPhoneValid
  }

  fun submitAuthForm() {
    val form = _authForm.value
    if (!validateAuthForm(form)) {
      _authForm.update {
        it.copy(
          fullNameError = if (it.fullName.trim().length < 2) "Name required (min 2 characters)" else null,
          phoneError = if (it.phoneNumber.length != 10 || it.phoneNumber.startsWith("0")) "Valid 10-digit phone number required" else null
        )
      }
      return
    }

    _destination.value = AppDestination.PERMISSIONS
  }

  fun completePermissions(locationGranted: Boolean, micGranted: Boolean) {
    viewModelScope.launch {
      val form = _authForm.value
      val profile = UserProfile(
        fullName = form.fullName.ifBlank { "Tourist Guest" },
        dialCode = form.country.dialCode,
        countryName = form.country.name,
        countryFlag = form.country.flagEmoji,
        phoneNumber = form.phoneNumber.ifBlank { "9876543210" },
        preferredLanguage = form.preferredLanguage,
        isOnboarded = true,
        locationPermissionGranted = locationGranted,
        micPermissionGranted = micGranted
      )
      repository.saveUserProfile(profile)
      _destination.value = AppDestination.MAIN_APP
    }
  }

  // Home & Site Explorer
  fun setHomeSearchQuery(query: String) {
    _homeSearchQuery.value = query
  }

  fun setSelectedCity(city: String) {
    _selectedCity.value = city
  }

  fun setSelectedCategory(category: String) {
    _selectedCategory.value = category
  }

  fun openSiteDetail(site: HeritageSite) {
    _selectedSiteDetail.value = site
  }

  fun closeSiteDetail() {
    _selectedSiteDetail.value = null
  }

  fun toggleSaveSite(siteId: String) {
    viewModelScope.launch {
      val isSaved = savedSiteIds.value.contains(siteId)
      repository.toggleSaveSite(siteId, isSaved)
    }
  }

  // Map Controls
  fun setMapSearchQuery(query: String) {
    _mapSearchQuery.value = query
  }

  fun setMapCategoryFilter(category: String) {
    _mapCategoryFilter.value = category
  }

  fun setMapLayerType(type: String) {
    _mapLayerType.value = type
  }

  fun selectMapPoi(poi: MapPoi?) {
    _selectedMapPoi.value = poi
  }

  // Offline Maps Manager
  fun toggleDownloadMap(cityId: String) {
    viewModelScope.launch {
      val currentList = _offlineMapsList.value.toMutableList()
      val index = currentList.indexOfFirst { it.id == cityId }
      if (index != -1) {
        val map = currentList[index]
        if (map.isDownloaded) {
          // Remove
          currentList[index] = map.copy(isDownloaded = false, downloadProgress = 0f)
          _offlineMapsList.value = currentList
          repository.deleteCityMap(cityId)
        } else {
          // Simulate progress download
          for (p in 1..5) {
            delay(150)
            currentList[index] = map.copy(downloadProgress = p * 0.2f)
            _offlineMapsList.value = currentList.toList()
          }
          currentList[index] = map.copy(isDownloaded = true, downloadProgress = 1f)
          _offlineMapsList.value = currentList.toList()
          repository.downloadCityMap(map.id, map.cityName, map.downloadSizeMb)
        }
      }
    }
  }

  // SOS Emergency Engine
  private var countdownJob: Job? = null

  fun startSosCountdown() {
    _isSosCountingDown.value = true
    _sosCountdownTimer.value = 3
    countdownJob?.cancel()
    countdownJob = viewModelScope.launch {
      for (i in 3 downTo 1) {
        _sosCountdownTimer.value = i
        delay(1000)
      }
      _isSosCountingDown.value = false
      _isSosTriggered.value = true
      _isSirenActive.value = true
    }
  }

  fun cancelSosCountdown() {
    countdownJob?.cancel()
    _isSosCountingDown.value = false
    _isSosTriggered.value = false
    _isSirenActive.value = false
  }

  fun toggleSiren() {
    _isSirenActive.value = !_isSirenActive.value
  }

  fun sendEmergencySms(context: Context, customNote: String = "") {
    val coords = "28.6129° N, 77.2295° E (Near India Gate, New Delhi)"
    val user = userProfile.value?.fullName ?: "Foreign Tourist"
    val message = "EMERGENCY! I need immediate help. Name: $user. Current GPS Location: $coords. $customNote"
    
    val intent = Intent(Intent.ACTION_SENDTO).apply {
      data = Uri.parse("smsto:112")
      putExtra("sms_body", message)
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    try {
      context.startActivity(intent)
    } catch (e: Exception) {
      val genericSmsIntent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("sms:")
        putExtra("sms_body", message)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
      }
      context.startActivity(genericSmsIntent)
    }
  }

  fun dialNumber(context: Context, number: String) {
    val cleanNumber = number.replace("-", "").replace(" ", "")
    val intent = Intent(Intent.ACTION_DIAL).apply {
      data = Uri.parse("tel:$cleanNumber")
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
  }

  fun openExternalUrl(context: Context, url: String) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
      flags = Intent.FLAG_ACTIVITY_NEW_TASK
    }
    context.startActivity(intent)
  }

  // Translator Engine
  fun setTranslatorSourceText(text: String) {
    _translatorSourceText.value = text
  }

  fun setTranslatorSourceLang(lang: String) {
    _translatorSourceLang.value = lang
  }

  fun setTranslatorTargetLang(lang: String) {
    _translatorTargetLang.value = lang
  }

  fun setSelectedPhraseCategory(cat: String) {
    _selectedPhraseCategory.value = cat
  }

  fun translateCurrentText() {
    val text = _translatorSourceText.value.trim()
    if (text.isBlank()) return

    viewModelScope.launch {
      _isTranslating.value = true
      delay(300) // Smooth response

      val (hiScript, hiPhonetic) = smartTranslateToHindi(text)
      _translatedResult.value = hiScript
      _translatedPhonetic.value = hiPhonetic
      _isTranslating.value = false

      repository.recordTranslation(
        sourceText = text,
        translatedText = hiScript,
        phoneticText = hiPhonetic,
        sourceLang = _translatorSourceLang.value,
        targetLang = _translatorTargetLang.value
      )
    }
  }

  private fun smartTranslateToHindi(input: String): Pair<String, String> {
    val lower = input.lowercase().trim()
    return when {
      lower.contains("how much") || lower.contains("price") || lower.contains("cost") ->
        Pair("यह कितने का है?", "Yeh kit-nay ka hai?")
      lower.contains("hello") || lower.contains("hi") || lower.contains("good morning") ->
        Pair("नमस्ते! आप कैसे हैं?", "Nah-mas-tay! Aap kai-say hain?")
      lower.contains("thank") ->
        Pair("बहुत बहुत धन्यवाद!", "Ba-hut ba-hut dhan-ya-vaad!")
      lower.contains("help") || lower.contains("emergency") ->
        Pair("कृपया मेरी मदद कीजिये!", "Krip-ya me-ree ma-dad kee-jee-ye!")
      lower.contains("water") ->
        Pair("क्या मुझे सील बंद मिनरल पानी मिल सकता है?", "Kya mu-jhay sealed mineral paani mil sak-ta hai?")
      lower.contains("spicy") || lower.contains("chili") ->
        Pair("कृपया तीखा कम डालियेगा।", "Krip-ya tee-kha kam daa-li-ye-ga.")
      lower.contains("where is") || lower.contains("direction") || lower.contains("metro") ->
        Pair("नज़दीकी मेट्रो स्टेशन कहाँ है?", "Naz-dee-kee metro station ka-haan hai?")
      lower.contains("discount") || lower.contains("less") || lower.contains("cheap") ->
        Pair("थोड़ा कम कर दीजिये भैया!", "Tho-da kam kar dee-jee-ye bhai-ya!")
      lower.contains("bill") || lower.contains("check") ->
        Pair("कृपया बिल ले आइये।", "Krip-ya bill lay aai-ye.")
      lower.contains("hospital") || lower.contains("doctor") ->
        Pair("मुझे तुरंत डॉक्टर या अस्पताल जाना है।", "Mu-jhay tu-rant doctor ya as-pa-taal jaana hai.")
      else -> {
        Pair("नमस्ते, $input (अनुवादित)", "Namaste, $input (Hindi tourist phrase)")
      }
    }
  }

  fun speakText(text: String, isHindi: Boolean = true) {
    ttsHelper.speak(text, isHindi)
  }

  // Currency Calculator
  fun setSelectedCurrency(rate: CurrencyRate) {
    _selectedCurrency.value = rate
    calculateCurrency()
  }

  fun setCurrencyAmount(amount: String) {
    _currencyInputAmount.value = amount
    calculateCurrency()
  }

  fun toggleCurrencyDirection() {
    _isConvertingFromInr.value = !_isConvertingFromInr.value
    calculateCurrency()
  }

  private fun calculateCurrency() {
    // Triggered automatically by reactive states
  }

  fun setShowAdvisoriesDialog(show: Boolean) {
    _showAdvisoriesDialog.value = show
  }

  fun logout() {
    viewModelScope.launch {
      repository.logout()
      _authForm.value = AuthFormState()
      _destination.value = AppDestination.WELCOME
      _activeTab.value = 0
    }
  }

  override fun onCleared() {
    super.onCleared()
    ttsHelper.shutdown()
  }
}
