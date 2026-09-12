package com.example.data.repository

import com.example.R
import com.example.data.database.AppDao
import com.example.data.database.DownloadedMapEntity
import com.example.data.database.SavedSiteEntity
import com.example.data.database.TranslationHistoryEntity
import com.example.data.database.UserEntity
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TouristRepository(private val appDao: AppDao) {

  // User Profile
  fun getUserProfile(): Flow<UserProfile?> {
    return appDao.getUserProfile().map { entity ->
      entity?.let {
        UserProfile(
          fullName = it.fullName,
          dialCode = it.dialCode,
          countryName = it.countryName,
          countryFlag = it.countryFlag,
          phoneNumber = it.phoneNumber,
          preferredLanguage = it.preferredLanguage,
          isOnboarded = it.isOnboarded,
          locationPermissionGranted = it.locationPermissionGranted,
          micPermissionGranted = it.micPermissionGranted
        )
      }
    }
  }

  suspend fun saveUserProfile(profile: UserProfile) {
    appDao.saveUserProfile(
      UserEntity(
        id = 1,
        fullName = profile.fullName,
        dialCode = profile.dialCode,
        countryName = profile.countryName,
        countryFlag = profile.countryFlag,
        phoneNumber = profile.phoneNumber,
        preferredLanguage = profile.preferredLanguage,
        isOnboarded = profile.isOnboarded,
        locationPermissionGranted = profile.locationPermissionGranted,
        micPermissionGranted = profile.micPermissionGranted
      )
    )
  }

  suspend fun logout() {
    appDao.clearUserProfile()
  }

  // Saved Sites
  fun getSavedSiteIds(): Flow<List<String>> = appDao.getSavedSiteIds()

  suspend fun toggleSaveSite(siteId: String, isCurrentlySaved: Boolean) {
    if (isCurrentlySaved) {
      appDao.removeSavedSite(siteId)
    } else {
      appDao.saveSite(SavedSiteEntity(siteId))
    }
  }

  // Downloaded Maps
  fun getDownloadedMaps(): Flow<List<DownloadedMapEntity>> = appDao.getDownloadedMaps()

  suspend fun downloadCityMap(cityId: String, cityName: String, sizeMb: Int) {
    appDao.addDownloadedMap(DownloadedMapEntity(cityId = cityId, cityName = cityName, sizeMb = sizeMb))
  }

  suspend fun deleteCityMap(cityId: String) {
    appDao.deleteDownloadedMap(cityId)
  }

  // Translation History
  fun getTranslationHistory(): Flow<List<TranslationHistoryEntity>> = appDao.getTranslationHistory()

  suspend fun recordTranslation(
    sourceText: String,
    translatedText: String,
    phoneticText: String,
    sourceLang: String,
    targetLang: String
  ) {
    appDao.addTranslationHistory(
      TranslationHistoryEntity(
        sourceText = sourceText,
        translatedText = translatedText,
        phoneticText = phoneticText,
        sourceLang = sourceLang,
        targetLang = targetLang
      )
    )
  }

  companion object {
    val SUPPORTED_COUNTRIES: List<Country> = listOf(
      Country("India", "+91", "🇮🇳", "IN", isPopular = true),
      Country("United States", "+1", "🇺🇸", "US", isPopular = true),
      Country("United Kingdom", "+44", "🇬🇧", "GB", isPopular = true),
      Country("Australia", "+61", "🇦🇺", "AU", isPopular = true),
      Country("France", "+33", "🇫🇷", "FR", isPopular = true),
      Country("Germany", "+49", "🇩🇪", "DE", isPopular = true),
      Country("Japan", "+81", "🇯🇵", "JP", isPopular = true),
      Country("Canada", "+1", "🇨🇦", "CA", isPopular = true),
      Country("Singapore", "+65", "🇸🇬", "SG", isPopular = true),
      Country("Afghanistan", "+93", "🇦🇫", "AF"),
      Country("Albania", "+355", "🇦🇱", "AL"),
      Country("Algeria", "+213", "🇩🇿", "DZ"),
      Country("Argentina", "+54", "🇦🇷", "AR"),
      Country("Austria", "+43", "🇦🇹", "AT"),
      Country("Belgium", "+32", "🇧🇪", "BE"),
      Country("Brazil", "+55", "🇧🇷", "BR"),
      Country("China", "+86", "🇨🇳", "CN"),
      Country("Denmark", "+45", "🇩🇰", "DK"),
      Country("Egypt", "+20", "🇪🇬", "EG"),
      Country("Finland", "+358", "🇫🇮", "FI"),
      Country("Greece", "+30", "🇬🇷", "GR"),
      Country("Hong Kong", "+852", "🇭🇰", "HK"),
      Country("Indonesia", "+62", "🇮🇩", "ID"),
      Country("Ireland", "+353", "🇮🇪", "IE"),
      Country("Israel", "+972", "🇮🇱", "IL"),
      Country("Italy", "+39", "🇮🇹", "IT"),
      Country("Malaysia", "+60", "🇲🇾", "MY"),
      Country("Mexico", "+52", "🇲🇽", "MX"),
      Country("Netherlands", "+31", "🇳🇱", "NL"),
      Country("New Zealand", "+64", "🇳🇿", "NZ"),
      Country("Norway", "+47", "🇳🇴", "NO"),
      Country("Philippines", "+63", "🇵🇭", "PH"),
      Country("Poland", "+48", "🇵🇱", "PL"),
      Country("Portugal", "+351", "🇵🇹", "PT"),
      Country("Russia", "+7", "🇷🇺", "RU"),
      Country("Saudi Arabia", "+966", "🇸🇦", "SA"),
      Country("South Africa", "+27", "🇿🇦", "ZA"),
      Country("South Korea", "+82", "🇰🇷", "KR"),
      Country("Spain", "+34", "🇪🇸", "ES"),
      Country("Sweden", "+46", "🇸🇪", "SE"),
      Country("Switzerland", "+41", "🇨🇭", "CH"),
      Country("Thailand", "+66", "🇹🇭", "TH"),
      Country("Turkey", "+90", "🇹🇷", "TR"),
      Country("United Arab Emirates", "+971", "🇦🇪", "AE"),
      Country("Vietnam", "+84", "🇻🇳", "VN")
    )
  }

  // Preloaded Data Sources
  fun getCountries(): List<Country> = SUPPORTED_COUNTRIES

  fun getHeritageSites(): List<HeritageSite> = listOf(
    HeritageSite(
      id = "taj_mahal",
      name = "Taj Mahal",
      hindiName = "ताज महल",
      city = "Agra",
      state = "Uttar Pradesh",
      category = "UNESCO Heritage",
      description = "One of the Seven Wonders of the World, an ivory-white marble mausoleum on the right bank of the river Yamuna.",
      history = "Commissioned in 1632 by the Mughal emperor Shah Jahan to house the tomb of his favourite wife, Mumtaz Mahal. It also houses the tomb of Shah Jahan himself.",
      timings = "30 mins before Sunrise to 30 mins before Sunset (Closed on Fridays)",
      peakRushHours = "10:30 AM - 3:30 PM (Best at sunrise 6:00 AM)",
      entryRules = "No drones, large bags, lighters, tripods, or eatables inside main mausoleum. Free shoe covers provided at entry.",
      dressCode = "Modest clothing covering shoulders and knees recommended.",
      foreignTicketInr = 1100,
      localTicketInr = 50,
      officialBookingUrl = "https://asi.payumoney.com/",
      rating = 4.9,
      reviewsCount = 18450,
      isPopular = true,
      imageRes = R.drawable.img_taj_mahal_hero,
      latitude = 27.1751,
      longitude = 78.0421,
      bestPhotoSpot = "Central reflection fountain walkway & Mehtab Bagh across the river",
      audioGuideIntro = "Welcome to the Taj Mahal. Built between 1631 and 1648 by order of the Mughal emperor Shah Jahan, this architectural masterpiece represents the pinnacle of Mughal art.",
      foreignReviews = listOf(
        ForeignerReview("r1", "Sarah Jenkins", "United States", "🇺🇸", 5.0, "Yesterday", "Breathtaking at 6 AM sunrise! Beat the crowds and got the official ASI online ticket in 2 mins.", "Bring a light jacket for early morning."),
        ForeignerReview("r2", "Liam O'Connor", "United Kingdom", "🇬🇧", 4.9, "3 days ago", "Incredible symmetry and intricate pietra dura gemstone inlays. Foreign ticket includes shoe covers & water bottle.", "Hire an official ASI licensed badge guide only.")
      )
    ),
    HeritageSite(
      id = "india_gate",
      name = "India Gate",
      hindiName = "इंडिया गेट",
      city = "New Delhi",
      state = "Delhi NCR",
      category = "Monuments",
      description = "A 42-meter high triumphal war memorial arch located astride the Kartavya Path in the heart of the national capital.",
      history = "Designed by Sir Edwin Lutyens and completed in 1931, it commemorates 84,000 soldiers of the British Indian Army who died in the First World War.",
      timings = "Open 24/7 (Grand Illumination & Fountains from 7:00 PM to 9:30 PM)",
      peakRushHours = "5:30 PM - 9:00 PM (Lively evening crowds and street food)",
      entryRules = "Free entry for all. Strict security perimeter around the Amar Jawan Jyoti memorial.",
      dressCode = "Casual respectful attire.",
      foreignTicketInr = 0,
      localTicketInr = 0,
      officialBookingUrl = "https://delhitourism.gov.in/",
      rating = 4.8,
      reviewsCount = 12400,
      isPopular = true,
      imageRes = R.drawable.img_india_gate,
      latitude = 28.6129,
      longitude = 77.2295,
      bestPhotoSpot = "Kartavya Path central axis walkway looking towards Rashtrapati Bhavan",
      audioGuideIntro = "You are standing before India Gate, one of New Delhi's most cherished landmarks.",
      foreignReviews = listOf(
        ForeignerReview("r3", "Elena Rossi", "Italy", "🇮🇹", 4.8, "Last week", "Atmosphere in the evening is electric! Locals gathering, ice cream carts, and the lighted canopy.", "Best to take the Delhi Metro to Central Secretariat.")
      )
    ),
    HeritageSite(
      id = "hawa_mahal",
      name = "Hawa Mahal (Palace of Winds)",
      hindiName = "हवा महल",
      city = "Jaipur",
      state = "Rajasthan",
      category = "Forts & Palaces",
      description = "An iconic 5-storey pink and red sandstone palace featuring 953 intricately carved jharokhas (windows) designed for cool breezes.",
      history = "Built in 1799 by Maharaja Sawai Pratap Singh, designed by Lal Chand Ustad in the form of the crown of Lord Krishna.",
      timings = "9:00 AM - 5:00 PM Daily",
      peakRushHours = "11:00 AM - 2:00 PM (Morning sunlight illuminates the facade best)",
      entryRules = "Footwear permitted in open courtyard. Stairs inside are narrow.",
      dressCode = "Breathable clothing; sunglasses and sun hat recommended.",
      foreignTicketInr = 200,
      localTicketInr = 50,
      officialBookingUrl = "https://rajasthandiscovery.gov.in/",
      rating = 4.7,
      reviewsCount = 9800,
      isPopular = true,
      imageRes = R.drawable.img_hawa_mahal,
      latitude = 26.9239,
      longitude = 75.8267,
      bestPhotoSpot = "The rooftop of Wind View Cafe or Tattoo Cafe across the street",
      audioGuideIntro = "Namaste! Hawa Mahal, or the Palace of Winds, allowed royal ladies to observe daily street life unobserved through 953 honeycombed windows.",
      foreignReviews = listOf(
        ForeignerReview("r4", "Kenji Sato", "Japan", "🇯🇵", 4.8, "5 days ago", "The facade is breathtaking. Early morning 8:30 AM light is perfect for photography before the traffic starts.", "Have chai on the rooftop across the road!")
      )
    ),
    HeritageSite(
      id = "varanasi_ghats",
      name = "Varanasi Ganges Ghats & Aarti",
      hindiName = "वाराणसी गंगा घाट",
      city = "Varanasi",
      state = "Uttar Pradesh",
      category = "Temples & Spiritual",
      description = "The spiritual heart of India along the sacred River Ganges with 84 ancient stone ghats and the mesmerizing evening Ganga Aarti ceremony.",
      history = "Varanasi is among the world's oldest continuously inhabited cities (over 3,000 years). Dashashwamedh Ghat was built by Peshwa Balaji Baji Rao in 1748.",
      timings = "Open 24 hours (Evening Ganga Aarti begins at 6:30 PM in Summer / 6:00 PM in Winter)",
      peakRushHours = "5:30 PM - 8:00 PM around Dashashwamedh & Assi Ghat",
      entryRules = "No photography of cremation fires at Manikarnika Ghat out of respect. Remove shoes when boarding boats or entering riverside sanctums.",
      dressCode = "Modest conservative clothing covering shoulders and knees.",
      foreignTicketInr = 0,
      localTicketInr = 0,
      officialBookingUrl = "https://varanasi.nic.in/",
      rating = 4.9,
      reviewsCount = 15300,
      isPopular = true,
      imageRes = R.drawable.img_varanasi,
      latitude = 25.3109,
      longitude = 83.0104,
      bestPhotoSpot = "Wooden rowing boat on the river facing Dashashwamedh Ghat at twilight",
      audioGuideIntro = "Welcome to Varanasi, the city of light and spirituality on the banks of the sacred Ganges.",
      foreignReviews = listOf(
        ForeignerReview("r5", "Marie Dubois", "France", "🇫🇷", 5.0, "2 days ago", "The Ganga Aarti ceremony with chanting, brass lamps, and river bells was an unforgettable spiritual experience.", "Book a private sunrise boat the night before.")
      )
    ),
    HeritageSite(
      id = "qutub_minar",
      name = "Qutub Minar Complex",
      hindiName = "क़ुतुब मीनार",
      city = "New Delhi",
      state = "Delhi NCR",
      category = "UNESCO Heritage",
      description = "A towering 72.5-meter red sandstone victory minaret with ornate Arabic calligraphy and the ancient 1,600-year-old rust-resistant Iron Pillar.",
      history = "Construction begun in 1192 by Qutb-ud-din Aibak and completed by his successors Iltutmish and Firoz Shah Tughlaq.",
      timings = "7:00 AM - 8:00 PM Daily (Night illumination until 10 PM)",
      peakRushHours = "1:00 PM - 4:00 PM",
      entryRules = "Online ASI QR ticket entry available at turnstiles. No tripods without permission.",
      dressCode = "Casual comfortable attire.",
      foreignTicketInr = 600,
      localTicketInr = 40,
      officialBookingUrl = "https://asi.payumoney.com/",
      rating = 4.7,
      reviewsCount = 8900,
      isPopular = false,
      imageRes = R.drawable.img_india_gate,
      latitude = 28.5244,
      longitude = 77.1855,
      bestPhotoSpot = "Garden lawns framing the minaret with the Alai Darwaza arch",
      audioGuideIntro = "The Qutub Minar is a soaring tower of victory begun in 1192, standing 72.5 meters tall with five distinct storeys.",
      foreignReviews = listOf(
        ForeignerReview("r6", "David Miller", "Australia", "🇦🇺", 4.7, "1 week ago", "Fascinating architecture. The 4th century iron pillar that doesn't rust is mindblowing.", "Use Delhi Metro yellow line to Qutub Minar station.")
      )
    ),
    HeritageSite(
      id = "red_fort",
      name = "Red Fort (Lal Qila)",
      hindiName = "लाल क़िला",
      city = "New Delhi",
      state = "Delhi NCR",
      category = "UNESCO Heritage",
      description = "The historic fortified palace of Mughal emperors in Old Delhi, crafted with massive red sandstone battlements.",
      history = "Commissioned by Emperor Shah Jahan in 1638 when he decided to shift his capital from Agra to Shahjahanabad (Delhi).",
      timings = "9:30 AM - 4:30 PM (Closed on Mondays)",
      peakRushHours = "11:00 AM - 3:00 PM",
      entryRules = "Security frisking at Lahore Gate. Museum tickets included in foreign ASI entry.",
      dressCode = "Comfortable walking shoes as the complex is expansive.",
      foreignTicketInr = 600,
      localTicketInr = 50,
      officialBookingUrl = "https://asi.payumoney.com/",
      rating = 4.6,
      reviewsCount = 11100,
      isPopular = false,
      imageRes = R.drawable.img_taj_mahal_hero,
      latitude = 28.6562,
      longitude = 77.2410,
      bestPhotoSpot = "Lahore Gate facade and Diwan-i-Khas marble pavilion",
      audioGuideIntro = "Welcome to the Red Fort, the seat of Mughal power for nearly 200 years and the site of India's annual Independence Day address.",
      foreignReviews = listOf(
        ForeignerReview("r7", "Clara Schmidt", "Germany", "🇩🇪", 4.6, "4 days ago", "Massive fortress with great museums inside. Pair it with a rickshaw tour of Chandni Chowk!", "Avoid Mondays when it is closed.")
      )
    )
  )

  fun getEmergencyContacts(): List<EmergencyContact> = listOf(
    EmergencyContact(
      id = "tourist_helpline",
      name = "National Tourist Helpline",
      number = "1363",
      subtitle = "Toll-Free 24x7 (12 Foreign Languages)",
      description = "Dedicated Ministry of Tourism helpline supporting English, French, German, Japanese, Spanish, Italian, Russian, Chinese, Arabic.",
      isDirectDial = true,
      isSmsReady = true,
      isNationalTouristHelpline = true
    ),
    EmergencyContact(
      id = "nat_emergency",
      name = "All-in-One Emergency",
      number = "112",
      subtitle = "Police, Ambulance & Fire (Pan-India)",
      description = "Universal national emergency response service equivalent to 911 / 999.",
      isDirectDial = true,
      isSmsReady = true
    ),
    EmergencyContact(
      id = "police",
      name = "Police Control Room",
      number = "100",
      subtitle = "Immediate Police Assistance",
      description = "City and highway police dispatch for safety incidents or theft reports.",
      isDirectDial = true
    ),
    EmergencyContact(
      id = "ambulance",
      name = "Medical Ambulance",
      number = "102",
      subtitle = "Government Medical Emergency",
      description = "Dispatch for trauma and urgent hospital transport (or 108 in many states).",
      isDirectDial = true
    ),
    EmergencyContact(
      id = "women_helpline",
      name = "Women's Safety Helpline",
      number = "1091",
      subtitle = "24x7 Rapid Safety Response",
      description = "Specialized cell for female travelers and residents in distress.",
      isDirectDial = true
    )
  )

  fun getForeignEmbassies(): List<EmbassyInfo> = listOf(
    EmbassyInfo(
      country = "United States of America",
      flagEmoji = "🇺🇸",
      city = "New Delhi",
      address = "Shantipath, Chanakyapuri, New Delhi 110021",
      phone = "+91-11-2419-8000",
      emergencyPhone = "+91-11-2419-8000",
      email = "NewDelhiACS@state.gov",
      website = "https://in.usembassy.gov/"
    ),
    EmbassyInfo(
      country = "United Kingdom",
      flagEmoji = "🇬🇧",
      city = "New Delhi",
      address = "Shantipath, Chanakyapuri, New Delhi 110021",
      phone = "+91-11-2419-2100",
      emergencyPhone = "+91-11-2419-2100",
      email = "web.newdelhi@fcdo.gov.uk",
      website = "https://www.gov.uk/world/india"
    ),
    EmbassyInfo(
      country = "France",
      flagEmoji = "🇫🇷",
      city = "New Delhi",
      address = "2/50-E, Shantipath, Chanakyapuri, New Delhi 110021",
      phone = "+91-11-4319-6100",
      emergencyPhone = "+91-98-1004-9404",
      email = "cad.new-delhi-amba@diplomatie.gouv.fr",
      website = "https://in.ambafrance.org/"
    ),
    EmbassyInfo(
      country = "Germany",
      flagEmoji = "🇩🇪",
      city = "New Delhi",
      address = "No. 6/50G, Shantipath, Chanakyapuri, New Delhi 110021",
      phone = "+91-11-4419-9199",
      emergencyPhone = "+91-98-1000-4882",
      email = "info@new-delhi.diplo.de",
      website = "https://india.diplo.de/"
    ),
    EmbassyInfo(
      country = "Japan",
      flagEmoji = "🇯🇵",
      city = "New Delhi",
      address = "50-G, Shantipath, Chanakyapuri, New Delhi 110021",
      phone = "+91-11-4610-4610",
      emergencyPhone = "+91-11-4610-4610",
      email = "jpemb-del@nd.mofa.go.jp",
      website = "https://www.in.emb-japan.go.jp/"
    ),
    EmbassyInfo(
      country = "Australia",
      flagEmoji = "🇦🇺",
      city = "New Delhi",
      address = "1/50G, Shantipath, Chanakyapuri, New Delhi 110021",
      phone = "+91-11-4139-9900",
      emergencyPhone = "+61-2-6261-3305",
      email = "ahc.newdelhi@dfat.gov.au",
      website = "https://india.highcommission.gov.au/"
    ),
    EmbassyInfo(
      country = "Canada",
      flagEmoji = "🇨🇦",
      city = "New Delhi",
      address = "7/8 Shantipath, Chanakyapuri, New Delhi 110021",
      phone = "+91-11-4178-2000",
      emergencyPhone = "+1-613-996-8885",
      email = "delhi-consular@international.gc.ca",
      website = "https://www.international.gc.ca/country-pays/india-inde/"
    )
  )

  fun getTouristPhrases(): List<TouristPhrase> = listOf(
    // Greetings
    TouristPhrase("p1", "Greetings", "Hello / Greetings", "नमस्ते", "Nah-mas-tay", "Bonjour", "Hallo", "Konnichiwa", "Nǐ hǎo"),
    TouristPhrase("p2", "Greetings", "Thank you very much", "धन्यवाद / शुक्रिया", "Dhan-ya-vaad / Shuk-ree-ya", "Merci beaucoup", "Vielen Dank", "Arigatō gozaimasu", "Xièxiè"),
    TouristPhrase("p3", "Greetings", "Yes / No", "हाँ / नहीं", "Haan / Na-heen", "Oui / Non", "Ja / Nein", "Hai / Iie", "Shì / Bù shì"),
    TouristPhrase("p4", "Greetings", "Excuse me / Brother (polite)", "सुनो भैया", "Su-no Bhai-ya", "Excusez-moi", "Entschuldigung", "Sumimasen", "Láojià"),
    TouristPhrase("p5", "Greetings", "My name is...", "मेरा नाम ... है", "Me-ra naam ... hai", "Je m'appelle...", "Ich heiße...", "Watashi no namae wa...", "Wǒ de míngzì shì..."),

    // Bargaining & Auto
    TouristPhrase("p6", "Bargaining & Auto", "How much for this?", "यह कितने का है?", "Yeh kit-nay ka hai?", "Combien ça coûte?", "Wie viel kostet das?", "Kore wa ikura desu ka?", "Zhège duōshǎo qián?"),
    TouristPhrase("p7", "Bargaining & Auto", "Please lower the price a little", "थोड़ा कम कीजिये", "Tho-da kam kee-jee-ye", "Baissez un peu le prix", "Etwas günstiger bitte", "Sukoshi yasuku shite", "Piányí yīdiǎn"),
    TouristPhrase("p8", "Bargaining & Auto", "Please turn on the meter", "मीटर से चलिए", "Mee-ter say cha-li-ye", "Mettez le compteur svp", "Bitte Taxameter an", "Mītā o tsukatte", "Qǐng dǎbiǎo"),
    TouristPhrase("p9", "Bargaining & Auto", "Too expensive!", "बहुत महँगा है!", "Ba-hut me-han-ga hai!", "Trop cher!", "Zu teuer!", "Takasugiru!", "Tài guìle!"),
    TouristPhrase("p10", "Bargaining & Auto", "Take me to this hotel/place", "मुझे यहाँ ले चलो", "Mu-jhay ya-haan lay cha-lo", "Emmenez-moi ici", "Bringen Sie mich hierhin", "Koko ni tsureteitte", "Dài wǒ qù zhèlǐ"),

    // Food & Water
    TouristPhrase("p11", "Food & Water", "Make it not spicy / less spicy", "तीखा कम डालना", "Tee-kha kam daal-na", "Pas épicé svp", "Nicht scharf bitte", "Karashiku shinaide", "Bùyào là"),
    TouristPhrase("p12", "Food & Water", "Do you have sealed mineral water?", "सील पैक्ड पानी की बोतल है?", "Sealed paani ki bo-tal hai?", "Avez-vous de l'eau minérale scellée?", "Versiegeltes Mineralwasser?", "Mīru sareta mizu?", "Yǒu fēngkǒu kuàngquánshuǐ ma?"),
    TouristPhrase("p13", "Food & Water", "Please bring the bill", "बिल ले आइये", "Bill lay aai-ye", "L'addition svp", "Die Rechnung bitte", "Okaikei onegaishimasu", "Qǐng jiézhàng"),
    TouristPhrase("p14", "Food & Water", "Is this vegetarian?", "क्या यह शाकाहारी है?", "Kya yeh shaa-ka-haa-ree hai?", "Est-ce végétarien?", "Ist das vegetarisch?", "Kore wa bejitarian desu ka?", "Zhè shì sùshí ma?"),

    // Directions
    TouristPhrase("p15", "Directions", "Where is the nearest metro/station?", "नज़दीकी मेट्रो कहाँ है?", "Naz-dee-kee metro ka-haan hai?", "Où est le métro?", "Wo ist die Metro?", "Chikakuno metoro wa doko?", "Zuìjìn de dìtiě zài nǎlǐ?"),
    TouristPhrase("p16", "Directions", "Where is the restroom/toilet?", "शौचालय / वॉशरूम कहाँ है?", "Washroom ka-haan hai?", "Où sont les toilettes?", "Wo ist die Toilette?", "Toire wa doko desu ka?", "Cèsuǒ zài nǎlǐ?"),
    TouristPhrase("p17", "Directions", "Turn left / Turn right", "बाएं मुड़ें / दाएं मुड़ें", "Ba-yein mu-dein / Da-yein mu-dein", "À gauche / À droite", "Links / Rechts", "Hidari / Migi", "Zuǒ zhuǎn / Yòu zhuǎn"),

    // Emergency
    TouristPhrase("p18", "Emergency", "Please help me!", "कृपया मेरी मदद कीजिये!", "Krip-ya me-ree ma-dad kee-jee-ye!", "Aidez-moi svp!", "Bitte helfen Sie mir!", "Tasukete kudasai!", "Qǐng bāngbāng wǒ!"),
    TouristPhrase("p19", "Emergency", "I need a doctor / hospital", "मुझे डॉक्टर / अस्पताल जाना है", "Mu-jhay doctor / as-pa-taal jaana hai", "J'ai besoin d'un médecin", "Ich brauche einen Arzt", "Isha ga hitsuyō desu", "Wǒ xūyào yīshēng"),
    TouristPhrase("p20", "Emergency", "Call the tourist police!", "पुलिस को बुलाइये!", "Police ko bu-laai-ye!", "Appelez la police!", "Rufen Sie die Polizei!", "Keisatsu o yonde!", "Bàojǐng!")
  )

  fun getCurrencyRates(): List<CurrencyRate> = listOf(
    CurrencyRate("USD", "US Dollar", "$", "🇺🇸", 86.50),
    CurrencyRate("EUR", "Euro", "€", "🇪🇺", 93.20),
    CurrencyRate("GBP", "British Pound", "£", "🇬🇧", 111.40),
    CurrencyRate("AUD", "Australian Dollar", "A$", "🇦🇺", 56.80),
    CurrencyRate("CAD", "Canadian Dollar", "C$", "🇨🇦", 62.70),
    CurrencyRate("JPY", "Japanese Yen", "¥", "🇯🇵", 0.58),
    CurrencyRate("SGD", "Singapore Dollar", "S$", "🇸🇬", 65.10),
    CurrencyRate("CNY", "Chinese Yuan", "¥", "🇨🇳", 11.90),
    CurrencyRate("AED", "UAE Dirham", "AED", "🇦🇪", 23.55),
    CurrencyRate("CHF", "Swiss Franc", "CHF", "🇨🇭", 97.80)
  )

  fun getOfflineCityMaps(): List<OfflineCityMap> = listOf(
    OfflineCityMap("delhi", "New Delhi & NCR", "Delhi", 42, isDownloaded = true, monumentsCount = 28, safetyScore = "9.5 / 10"),
    OfflineCityMap("agra", "Agra & Taj Zone", "Uttar Pradesh", 28, isDownloaded = true, monumentsCount = 14, safetyScore = "9.2 / 10"),
    OfflineCityMap("jaipur", "Jaipur Pink City", "Rajasthan", 34, isDownloaded = false, monumentsCount = 22, safetyScore = "9.6 / 10"),
    OfflineCityMap("varanasi", "Varanasi Ghats", "Uttar Pradesh", 22, isDownloaded = false, monumentsCount = 18, safetyScore = "9.1 / 10"),
    OfflineCityMap("mumbai", "Mumbai Heritage", "Maharashtra", 48, isDownloaded = false, monumentsCount = 26, safetyScore = "9.7 / 10"),
    OfflineCityMap("goa", "Goa Old Churches & Coast", "Goa", 36, isDownloaded = false, monumentsCount = 19, safetyScore = "9.8 / 10")
  )

  fun getTouristSafetyTips(): List<TouristSafetyTip> = listOf(
    TouristSafetyTip(
      id = "scam1",
      title = "The 'Monument is Closed Today' Scam",
      category = "Scam Alert",
      description = "Tuk-tuk drivers or strangers near monuments may tell you the site is closed for a VIP or festival, offering to take you to an expensive emporium instead.",
      actionableDo = "Verify timings directly on the official ASI ticket portal or walk directly to the ticket counter.",
      actionableDont = "Never divert your itinerary to suggested private shops or substitute travel agencies.",
      isUrgent = true
    ),
    TouristSafetyTip(
      id = "scam2",
      title = "Auto-Rickshaw Meter Refusal",
      category = "Transport",
      description = "Drivers outside stations and airports may demand 5x the normal fare without turning on the electronic meter.",
      actionableDo = "Use app-based cabs (Uber/Ola) or prepaid government taxi/auto booths located right inside terminal exits.",
      actionableDont = "Never get into an auto without fixing the price beforehand or ensuring the meter is on."
    ),
    TouristSafetyTip(
      id = "safety1",
      title = "Safe Drinking Water (Pani) Rule",
      category = "Health & Food",
      description = "Tap water in India is not potable for tourists without boiling or reverse-osmosis filtration.",
      actionableDo = "Drink only sealed bottled mineral water (Kinley, Aquafina, Bisleri) with an intact cap seal ring, or carry a UV purifier bottle.",
      actionableDont = "Avoid ice in street drinks and pre-cut unpeeled fruits sold open in roadside stalls."
    ),
    TouristSafetyTip(
      id = "etiquette1",
      title = "Temple & Sacred Place Customs",
      category = "Cultural Etiquette",
      description = "Temples, Gurudwaras (Sikh temples), and Mosques have sacred sanctity rules.",
      actionableDo = "Always remove your footwear at the designated 'Joota Ghar' stall, cover your head at Gurudwaras (scarves provided), and dress with covered shoulders and knees.",
      actionableDont = "Never touch deity idols or point your feet directly at shrines or monks."
    )
  )

  fun getMapPois(): List<MapPoi> = listOf(
    MapPoi("poi1", "Taj Mahal East Gate", "heritage", 27.1751, 78.0421, "Taj East Gate Rd, Agra", 4.9, 0.2, R.drawable.img_taj_mahal_hero, "Open • Foreign Entry ₹1100"),
    MapPoi("poi2", "India Gate National Memorial", "heritage", 28.6129, 77.2295, "Kartavya Path, New Delhi", 4.8, 1.4, R.drawable.img_india_gate, "Open 24/7 • Free Entry"),
    MapPoi("poi3", "Hawa Mahal Palace", "heritage", 26.9239, 75.8267, "Badi Choupad, Jaipur", 4.7, 2.1, R.drawable.img_hawa_mahal, "Open until 5:00 PM • Entry ₹200"),
    MapPoi("poi4", "Dashashwamedh Ghat", "heritage", 25.3109, 83.0104, "Dashashwamedh Ghat Rd, Varanasi", 4.9, 0.8, R.drawable.img_varanasi, "Evening Aarti 6:30 PM"),
    MapPoi("poi5", "Peshawri Indian Dining", "restaurant", 27.1610, 78.0450, "ITC Mughal, Fatehabad Rd, Agra", 4.9, 1.8, 0, "Authentic North Indian & Tandoori"),
    MapPoi("poi6", "Karim's Historic Mughal Cuisine", "restaurant", 28.6508, 77.2334, "Jama Masjid Gali Kababian, Delhi", 4.7, 3.2, 0, "Famous Mutton Kebabs & Biryani since 1913"),
    MapPoi("poi7", "The Imperial Palace Hotel", "hotel", 28.6255, 77.2185, "Janpath, Connaught Place, New Delhi", 4.9, 2.6, 0, "5-Star Heritage Luxury Hotel"),
    MapPoi("poi8", "State Bank of India 24/7 ATM", "atm", 28.6315, 77.2195, "Connaught Place Radial 2, Delhi", 4.5, 0.5, 0, "Accepts Visa, Mastercard, Amex (INR Cash)"),
    MapPoi("poi9", "Tourist Police Assistance Post", "police", 28.6140, 77.2300, "India Gate Hexagon, New Delhi", 4.8, 0.3, 0, "24x7 Tourist Support & Safety"),
    MapPoi("poi10", "All India Institute of Medical Sciences (AIIMS)", "hospital", 28.5672, 77.2100, "Sri Aurobindo Marg, New Delhi", 4.7, 4.5, 0, "Premier 24x7 Emergency Trauma Center")
  )
}
