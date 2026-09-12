package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.repository.TouristRepository
import com.example.ui.components.MainAppScaffold
import com.example.ui.components.TravelAdvisoriesDialog
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.auth.CountryPickerSheet
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.home.SiteDetailSheet
import com.example.ui.screens.map.InteractiveMapScreen
import com.example.ui.screens.permissions.PermissionsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.sos.SosScreen
import com.example.ui.screens.translator.TranslatorScreen
import com.example.ui.screens.welcome.WelcomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppDestination
import com.example.ui.viewmodel.ExploreIndiaViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          ExploreIndiaApp()
        }
      }
    }
  }
}

@Composable
fun ExploreIndiaApp(
  viewModel: ExploreIndiaViewModel = viewModel()
) {
  val destination by viewModel.destination.collectAsState()
  val activeTab by viewModel.activeTab.collectAsState()
  val userProfile by viewModel.userProfile.collectAsState()
  val authFormState by viewModel.authForm.collectAsState()
  val isCountryPickerOpen by viewModel.isCountryPickerOpen.collectAsState()
  val countrySearchQuery by viewModel.countrySearchQuery.collectAsState()
  val savedSiteIds by viewModel.savedSiteIds.collectAsState()

  // Sights
  val homeSearchQuery by viewModel.homeSearchQuery.collectAsState()
  val selectedCity by viewModel.selectedCity.collectAsState()
  val selectedCategory by viewModel.selectedCategory.collectAsState()
  val selectedSiteDetail by viewModel.selectedSiteDetail.collectAsState()

  // Maps
  val mapSearchQuery by viewModel.mapSearchQuery.collectAsState()
  val mapCategoryFilter by viewModel.mapCategoryFilter.collectAsState()
  val mapLayerType by viewModel.mapLayerType.collectAsState()
  val selectedMapPoi by viewModel.selectedMapPoi.collectAsState()
  val offlineMapsList by viewModel.offlineMapsList.collectAsState()

  // SOS
  val isSosCountingDown by viewModel.isSosCountingDown.collectAsState()
  val sosCountdownTimer by viewModel.sosCountdownTimer.collectAsState()
  val isSosTriggered by viewModel.isSosTriggered.collectAsState()
  val isSirenActive by viewModel.isSirenActive.collectAsState()

  // Translator
  val translatorSourceText by viewModel.translatorSourceText.collectAsState()
  val translatorSourceLang by viewModel.translatorSourceLang.collectAsState()
  val translatorTargetLang by viewModel.translatorTargetLang.collectAsState()
  val translatedResult by viewModel.translatedResult.collectAsState()
  val translatedPhonetic by viewModel.translatedPhonetic.collectAsState()
  val isTranslating by viewModel.isTranslating.collectAsState()
  val selectedPhraseCategory by viewModel.selectedPhraseCategory.collectAsState()

  // Currency
  val selectedCurrency by viewModel.selectedCurrency.collectAsState()
  val currencyInputAmount by viewModel.currencyInputAmount.collectAsState()
  val isConvertingFromInr by viewModel.isConvertingFromInr.collectAsState()

  // Dialogs
  val showAdvisoriesDialog by viewModel.showAdvisoriesDialog.collectAsState()

  // Flow Navigation
  when (destination) {
    AppDestination.WELCOME -> {
      WelcomeScreen(
        onGetStartedClick = { viewModel.navigateTo(AppDestination.AUTH) },
        onLoginClick = { viewModel.navigateTo(AppDestination.AUTH) }
      )
    }
    AppDestination.AUTH -> {
      AuthScreen(
        formState = authFormState,
        onBackClick = { viewModel.navigateTo(AppDestination.WELCOME) },
        onFullNameChanged = { viewModel.onFullNameChanged(it) },
        onPhoneNumberChanged = { viewModel.onPhoneNumberChanged(it) },
        onCountryCodeClick = { viewModel.setCountryPickerOpen(true) },
        onLanguageSelected = { viewModel.onLanguageSelected(it) },
        onContinueClick = { viewModel.submitAuthForm() }
      )
    }
    AppDestination.PERMISSIONS -> {
      PermissionsScreen(
        onComplete = { loc, mic ->
          viewModel.completePermissions(loc, mic)
        }
      )
    }
    AppDestination.MAIN_APP -> {
      MainAppScaffold(
        selectedTab = activeTab,
        onTabSelected = { viewModel.setTab(it) }
      ) { _ ->
        when (activeTab) {
          0 -> {
            HomeScreen(
              sites = viewModel.allSites,
              savedSiteIds = savedSiteIds,
              searchQuery = homeSearchQuery,
              selectedCity = selectedCity,
              selectedCategory = selectedCategory,
              onSearchQueryChanged = { viewModel.setHomeSearchQuery(it) },
              onCitySelected = { viewModel.setSelectedCity(it) },
              onCategorySelected = { viewModel.setSelectedCategory(it) },
              onSiteClick = { viewModel.openSiteDetail(it) },
              onToggleSaveSite = { viewModel.toggleSaveSite(it) },
              onQuickChipClick = { chip ->
                when (chip) {
                  "heritage" -> {
                    viewModel.setSelectedCategory("UNESCO Heritage")
                  }
                  "sos" -> {
                    viewModel.setTab(2)
                  }
                  "translator" -> {
                    viewModel.setTab(3)
                  }
                  "currency" -> {
                    viewModel.setTab(4)
                  }
                  "offline_maps" -> {
                    viewModel.setTab(4)
                  }
                }
              },
              onNotificationClick = { viewModel.setShowAdvisoriesDialog(true) }
            )
          }
          1 -> {
            InteractiveMapScreen(
              pois = viewModel.allPois,
              heritageSites = viewModel.allSites,
              searchQuery = mapSearchQuery,
              selectedCategory = mapCategoryFilter,
              mapLayerType = mapLayerType,
              selectedPoi = selectedMapPoi,
              onSearchQueryChanged = { viewModel.setMapSearchQuery(it) },
              onCategorySelected = { viewModel.setMapCategoryFilter(it) },
              onLayerTypeChanged = { viewModel.setMapLayerType(it) },
              onSelectPoi = { viewModel.selectMapPoi(it) },
              onOpenSiteDetail = { viewModel.openSiteDetail(it) },
              onBookTickets = { ctx, url -> viewModel.openExternalUrl(ctx, url) }
            )
          }
          2 -> {
            SosScreen(
              emergencyContacts = viewModel.emergencyContacts,
              embassies = viewModel.foreignEmbassies,
              safetyTips = viewModel.touristSafetyTips,
              isCountingDown = isSosCountingDown,
              countdownSeconds = sosCountdownTimer,
              isSosTriggered = isSosTriggered,
              isSirenActive = isSirenActive,
              onStartSos = { viewModel.startSosCountdown() },
              onCancelSos = { viewModel.cancelSosCountdown() },
              onToggleSiren = { viewModel.toggleSiren() },
              onSendSms = { ctx -> viewModel.sendEmergencySms(ctx) },
              onDialNumber = { ctx, num -> viewModel.dialNumber(ctx, num) }
            )
          }
          3 -> {
            TranslatorScreen(
              sourceText = translatorSourceText,
              sourceLang = translatorSourceLang,
              targetLang = translatorTargetLang,
              translatedResult = translatedResult,
              translatedPhonetic = translatedPhonetic,
              isTranslating = isTranslating,
              selectedCategory = selectedPhraseCategory,
              phrases = viewModel.allPhrases,
              onSourceTextChanged = { viewModel.setTranslatorSourceText(it) },
              onSourceLangChanged = { viewModel.setTranslatorSourceLang(it) },
              onTargetLangChanged = { viewModel.setTranslatorTargetLang(it) },
              onTranslateClick = { viewModel.translateCurrentText() },
              onCategorySelected = { viewModel.setSelectedPhraseCategory(it) },
              onSpeak = { text, isHindi -> viewModel.speakText(text, isHindi) }
            )
          }
          4 -> {
            ProfileScreen(
              userProfile = userProfile,
              currencyRates = viewModel.currencyRates,
              selectedCurrency = selectedCurrency,
              currencyAmount = currencyInputAmount,
              isConvertingFromInr = isConvertingFromInr,
              offlineMaps = offlineMapsList,
              onCurrencySelected = { viewModel.setSelectedCurrency(it) },
              onCurrencyAmountChanged = { viewModel.setCurrencyAmount(it) },
              onToggleCurrencyDirection = { viewModel.toggleCurrencyDirection() },
              onToggleDownloadMap = { viewModel.toggleDownloadMap(it) },
              onLogoutClick = { viewModel.logout() }
            )
          }
        }
      }
    }
  }

  // Country Picker Modal Sheet
  if (isCountryPickerOpen) {
    CountryPickerSheet(
      countries = TouristRepository.SUPPORTED_COUNTRIES,
      selectedCountry = authFormState.country,
      searchQuery = countrySearchQuery,
      onSearchQueryChanged = { viewModel.setCountrySearchQuery(it) },
      onCountrySelected = { viewModel.onCountrySelected(it) },
      onDismiss = { viewModel.setCountryPickerOpen(false) }
    )
  }

  // Site Detail Modal Sheet
  if (selectedSiteDetail != null) {
    val site = selectedSiteDetail!!
    SiteDetailSheet(
      site = site,
      isSaved = savedSiteIds.contains(site.id),
      onToggleSave = { viewModel.toggleSaveSite(site.id) },
      onPlayAudioGuide = { intro -> viewModel.speakText(intro, false) },
      onBookTickets = { ctx, url -> viewModel.openExternalUrl(ctx, url) },
      onDismiss = { viewModel.closeSiteDetail() }
    )
  }

  // Live Travel Advisories Dialog
  if (showAdvisoriesDialog) {
    TravelAdvisoriesDialog(
      onDismiss = { viewModel.setShowAdvisoriesDialog(false) }
    )
  }
}
