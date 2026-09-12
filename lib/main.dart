import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:url_launcher/url_launcher.dart';

// IMPORTING OTHER MEMBERS' FILES
import 'member2_maps_sos.dart';
import 'member3_heritage.dart';
import 'member4_translator.dart';

// ==========================================
// 1. DATA MODELS & LOCALIZED HERITAGE ENGINE
// ==========================================

class CityModel {
  final String id;
  final String name;
  final String description;
  final String state;

  const CityModel({
    required this.id,
    required this.name,
    required this.description,
    required this.state,
  });
}

const List<CityModel> sampleCities = [
  CityModel(
      id: 'delhi',
      name: 'Delhi',
      description: 'Explore India\'s historical capital',
      state: 'Delhi'),
  CityModel(
      id: 'mumbai',
      name: 'Mumbai',
      description: 'City of Dreams & Coastal Heritage',
      state: 'Maharashtra'),
  CityModel(
      id: 'agra',
      name: 'Agra',
      description: 'Home to the iconic Taj Mahal',
      state: 'Uttar Pradesh'),
  CityModel(
      id: 'bengaluru',
      name: 'Bengaluru',
      description: 'Garden City & Tech Hub',
      state: 'Karnataka'),
  CityModel(
      id: 'varanasi',
      name: 'Varanasi',
      description: 'Spiritual heart of India',
      state: 'Uttar Pradesh'),
  CityModel(
      id: 'kolkata',
      name: 'Kolkata',
      description: 'Cultural & Literary Hub',
      state: 'West Bengal'),
  CityModel(
      id: 'pune',
      name: 'Pune',
      description: 'Cultural Capital of Maharashtra',
      state: 'Maharashtra'),
  CityModel(
      id: 'jaipur',
      name: 'Jaipur',
      description: 'The Pink City of Forts & Palaces',
      state: 'Rajasthan'),
];

class Country {
  final String name;
  final String code;
  final String flag;
  Country(this.name, this.code, this.flag);
}

final List<Country> countriesList = [
  Country('India', '+91', '🇮🇳'),
  Country('United States', '+1', '🇺🇸'),
  Country('United Kingdom', '+44', '🇬🇧'),
  Country('France', '+33', '🇫🇷'),
  Country('Germany', '+49', '🇩🇪'),
  Country('Japan', '+81', '🇯🇵'),
  Country('China', '+86', '🇨🇳'),
  Country('Australia', '+61', '🇦🇺'),
];

class HeritageTranslationEngine {
  static Map<String, Map<String, Map<String, String>>> siteTranslations = {
    'Qutub Minar': {
      'German': {
        'history':
            'Erbaut im Jahr 1193 von Qutab-ud-din Aibak nach dem Sieg über das letzte hinduistische Königreich von Delhi. Es ist 72.5 Meter hoch mit fünf Etagen aus rotem Sandstein und Marmor.',
        'peak': '11:00 Uhr – 16:00 Uhr',
        'rules':
            'Gültiger Ausweis erforderlich. Stative, Drohnen und große Rucksäcke sind verboten.',
      },
      'French': {
        'history':
            'Construit en 1193 par Qutab-ud-din Aibak après la défaite du dernier royaume hindou de Delhi. Il mesure 72.5 mètres de haut avec cinq étages en grès rouge et marbre.',
        'peak': '11h00 – 16h00',
        'rules':
            'Pièce d identité valide requise. Trépieds, drones et grands sacs à dos interdits.',
      },
      'Japanese': {
        'history':
            'デリの最後のヒンドゥー王国の敗北後、1193年にクトゥブ・ウッディーン・アイバクによって建設されました。赤砂岩と大理石で作られた5層の塔で、高さは72.5メートルです。',
        'peak': '午前 11:00 – 午後 04:00',
        'rules': '有効な身分証明書が必要です。三脚、ドローン、大きなリュックサックは禁止されています。',
      },
      'Chinese': {
        'history':
            '于1193年由库特布-乌德-丁-艾巴克在击败德里最后一个印度教王国后建造。它高72.5米，共有五层，由红砂岩和大理石建成。',
        'peak': '上午 11:00 – 下午 04:00',
        'rules': '需要有效的身份证件。禁止使用三脚架、无人机和大背包。',
      },
    },
    'Red Fort (Lal Qila)': {
      'German': {
        'history':
            '1638 von Shah Jahan in Auftrag gegeben, als er seine Hauptstadt von Agra nach Delhi verlegte. Es diente fast zwei Jahrhunderte lang als Hauptwohnsitz der Mughal-Kaiser.',
        'peak': '13:00 Uhr – 17:00 Uhr',
        'rules':
            'Montags geschlossen. Obligatorische Sicherheitskontrolle am Lahori Gate.',
      },
      'French': {
        'history':
            'Commandé par Shah Jahan en 1638 lorsqu il a transféré sa capitale d Agra à Delhi. Il a servi de résidence principale aux empereurs moghols pendant près de deux siècles.',
        'peak': '13h00 – 17h00',
        'rules':
            'Fermé le lundi. Contrôle de sécurité obligatoire à la porte Lahori.',
      },
    }
  };

  static String getField(
      String siteName, String field, String lang, String defaultText) {
    if (siteTranslations.containsKey(siteName) &&
        siteTranslations[siteName]!.containsKey(lang)) {
      return siteTranslations[siteName]![lang]![field] ?? defaultText;
    }
    return defaultText;
  }
}

// ==========================================
// 2. STATE MANAGEMENT & FULL LOCALIZATION
// ==========================================

class LanguageProvider extends ChangeNotifier {
  String _currentLanguage = 'English';
  String get currentLanguage => _currentLanguage;

  static const Map<String, Map<String, String>> _localizedStrings = {
    'English': {
      'welcome': 'Welcome to India',
      'explore': 'Explore',
      'home': 'Home',
      'sos': 'Emergency SOS',
      'maps': 'Maps',
      'translator': 'Translator',
      'currency': 'Currency Exchange',
      'selectCity': 'Select City',
      'quickTools': 'Quick Tourist Tools',
      'permissionsTitle': 'Allow Permissions 🛡️',
      'permissionsSubtitle':
          'To provide you with the best travel experience, we need access to hardware features.',
      'locationAccess': 'Location Access',
      'locationDesc': 'Used for nearby places and navigation.',
      'micAccess': 'Microphone Access',
      'micDesc': 'Used for real-time voice translation.',
      'allow': 'Allow',
      'allowed': 'Allowed',
      'allowAll': 'Allow All & Continue',
      'skipNow': 'Skip for Now',
      'profile': 'Profile',
      'logout': 'Logout',
      'createProfileTitle': 'Create Profile',
      'profileSubtitle':
          'Enter your details to customize your Explore India experience',
      'fullName': 'FULL NAME',
      'fullNameHint': 'e.g. Sarah Jenkins',
      'fullNameRequired': 'Full name is required',
      'fullNameMin': 'Must be at least 2 characters',
      'phoneNumber': 'PHONE NUMBER',
      'phoneHint': 'e.g. 9876543210',
      'phoneRequired': 'Phone number is required',
      'phoneInvalid': 'Phone number must contain 10 digits',
      'phoneStartZero': 'Phone number cannot start with 0',
      'preferredLanguageLabel': 'PREFERRED AUDIO & TRANSLATION LANGUAGE',
      'continue': 'Continue',
      'countryCodeTitle': 'Select Country Code',
      'selectCountry': 'Select Country Code',
      'permissionHeroText':
          'To enable interactive offline maps, nearby monument discovery, and instant voice translation.',
      'permissionLocationTitle': 'Location Access',
      'permissionLocationDesc':
          'To show nearby heritage places, offline maps & walking routes.',
      'permissionMicTitle': 'Microphone Access',
      'permissionMicDesc':
          'To enable real-time foreign voice translation & audio pronunciation.',
      'notNow': 'Not Now',
      'convertFrom': 'Convert From',
      'convertTo': 'Convert To',
      'amount': 'Enter Amount',
      'result': 'Converted Value',
      'historyTitle': 'History & Architecture',
      'peakRushTitle': 'Peak Rush Hours',
      'peakWindow': 'Peak Window',
      'entryRulesTitle': 'Entry Rules & Advisory',
      'bookTickets': 'Book Official Tickets (Govt Portal)',
      'searchHint': 'Search places, cities or attractions...',
    },
    'German': {
      'welcome': 'Willkommen in Indien',
      'explore': 'Erkunden',
      'home': 'Startseite',
      'sos': 'Notfall-SOS',
      'maps': 'Karten',
      'translator': 'Übersetzer',
      'currency': 'Währungsrechner',
      'selectCity': 'Stadt auswählen',
      'quickTools': 'Schnelle Werkzeuge',
      'permissionsTitle': 'Berechtigungen erteilen 🛡️',
      'permissionsSubtitle':
          'Für das beste Reiseerlebnis benötigen wir Zugriff auf Hardware-Funktionen.',
      'locationAccess': 'Standortzugriff',
      'locationDesc': 'Für Orte in der Nähe und Navigation.',
      'micAccess': 'Mikrofonzugriff',
      'micDesc': 'Für Echtzeit-Sprachübersetzung.',
      'allow': 'Erlauben',
      'allowed': 'Erlaubt',
      'allowAll': 'Alle erlauben & Weiter',
      'skipNow': 'Vorerst überspringen',
      'profile': 'Profil',
      'logout': 'Abmelden',
      'createProfileTitle': 'Profil erstellen',
      'profileSubtitle':
          'Geben Sie Ihre Daten ein, um Ihr individuelles Erlebnis in Indien zu personalisieren',
      'fullName': 'VOLLER NAME',
      'fullNameHint': 'z. B. Sarah Jenkins',
      'fullNameRequired': 'Vollständiger Name ist erforderlich',
      'fullNameMin': 'Mindestens 2 Zeichen erforderlich',
      'phoneNumber': 'TELEFONNUMMER',
      'phoneHint': 'z. B. 9876543210',
      'phoneRequired': 'Telefonnummer ist erforderlich',
      'phoneInvalid': 'Die Telefonnummer muss 10 Ziffern enthalten',
      'phoneStartZero': 'Die Telefonnummer darf nicht mit 0 beginnen',
      'preferredLanguageLabel': 'BEVORZUGTE AUDIO- UND ÜBERSETZUNGSSPRACHE',
      'continue': 'Weiter',
      'countryCodeTitle': 'Landesvorwahl auswählen',
      'selectCountry': 'Landesvorwahl auswählen',
      'permissionHeroText':
          'Damit interaktive Offline-Karten, nahe Monumente und sofortige Sprachübersetzung funktionieren.',
      'permissionLocationTitle': 'Standortzugriff',
      'permissionLocationDesc':
          'Um nahe Sehenswürdigkeiten, Offline-Karten und Spazierwege anzuzeigen.',
      'permissionMicTitle': 'Mikrofonzugriff',
      'permissionMicDesc':
          'Damit Echtzeit-Übersetzung und Aussprache funktionieren.',
      'notNow': 'Jetzt nicht',
      'convertFrom': 'Umrechnen von',
      'convertTo': 'Umrechnen in',
      'amount': 'Betrag eingeben',
      'result': 'Umgerechneter Wert',
      'historyTitle': 'Geschichte & Architektur',
      'peakRushTitle': 'Stoßzeiten & Besucherandrang',
      'peakWindow': 'Hauptbesuchszeit',
      'entryRulesTitle': 'Einlassregeln & Hinweise',
      'bookTickets': 'Offizielle Tickets buchen (Behördenportal)',
      'searchHint': 'Orte, Städte oder Sehenswürdigkeiten suchen...',
    },
    'French': {
      'welcome': 'Bienvenue en Inde',
      'explore': 'Explorer',
      'home': 'Accueil',
      'sos': 'SOS Urgence',
      'maps': 'Cartes',
      'translator': 'Traducteur',
      'currency': 'Change de devises',
      'selectCity': 'Sélectionnez une ville',
      'quickTools': 'Outils touristiques rapides',
      'permissionsTitle': 'Autoriser les permissions 🛡️',
      'permissionsSubtitle':
          'Pour la meilleure expérience, nous avons besoin d un accès aux fonctionnalités.',
      'locationAccess': 'Accès localisation',
      'locationDesc': 'Utilisé pour la navigation et lieux proches.',
      'micAccess': 'Accès microphone',
      'micDesc': 'Utilisé pour la traduction vocale en direct.',
      'allow': 'Autoriser',
      'allowed': 'Autorisé',
      'allowAll': 'Tout autoriser et continuer',
      'skipNow': 'Ignorer pour l instant',
      'profile': 'Profil',
      'logout': 'Se déconnecter',
      'createProfileTitle': 'Créer un profil',
      'profileSubtitle':
          'Entrez vos détails pour personnaliser votre expérience en Inde',
      'fullName': 'NOM COMPLET',
      'fullNameHint': 'ex. Sarah Jenkins',
      'fullNameRequired': 'Le nom complet est requis',
      'fullNameMin': 'Le nom doit contenir au moins 2 caractères',
      'phoneNumber': 'NUMÉRO DE TÉLÉPHONE',
      'phoneHint': 'ex. 9876543210',
      'phoneRequired': 'Le numéro de téléphone est requis',
      'phoneInvalid': 'Le numéro doit contenir 10 chiffres',
      'phoneStartZero': 'Le numéro ne peut pas commencer par 0',
      'preferredLanguageLabel': 'LANGUE PRÉFÉRÉE POUR L AUDIO ET LA TRADUCTION',
      'continue': 'Continuer',
      'countryCodeTitle': 'Choisir le code pays',
      'selectCountry': 'Choisir le code pays',
      'permissionHeroText':
          'Pour activer les cartes hors ligne, les monuments à proximité et la traduction vocale instantanée.',
      'permissionLocationTitle': 'Accès localisation',
      'permissionLocationDesc':
          'Pour afficher les lieux à proximité, cartes hors ligne et itinéraires.',
      'permissionMicTitle': 'Accès microphone',
      'permissionMicDesc':
          'Pour activer la traduction vocale en direct et la prononciation audio.',
      'notNow': 'Pas maintenant',
      'convertFrom': 'Convertir de',
      'convertTo': 'Convertir en',
      'amount': 'Entrez le montant',
      'result': 'Valeur convertie',
      'historyTitle': 'Histoire et architecture',
      'peakRushTitle': 'Heures de pointe',
      'peakWindow': 'Fenêtre de pointe',
      'entryRulesTitle': 'Règles d entrée et conseils',
      'bookTickets': 'Réserver des billets officiels (Portail Gov)',
      'searchHint': 'Rechercher des lieux, villes ou attractions...',
    },
    'Japanese': {
      'welcome': 'インドへようこそ',
      'explore': '探索',
      'home': 'ホーム',
      'sos': '緊急SOS',
      'maps': 'マップ',
      'translator': '翻訳機',
      'currency': '通貨換算',
      'selectCity': '都市を選択',
      'quickTools': 'クイックツール',
      'permissionsTitle': '権限を許可 🛡️',
      'permissionsSubtitle': '最高の体験を提供するために機能へのアクセスが必要です。',
      'locationAccess': '位置情報アクセス',
      'locationDesc': 'ナビゲーションと近くの場所に使用。',
      'micAccess': 'マイクアクセス',
      'micDesc': 'リアルタイム音声翻訳に使用。',
      'allow': '許可する',
      'allowed': '許可済み',
      'allowAll': 'すべて許可して続行',
      'skipNow': '今はスキップ',
      'profile': 'プロフィール',
      'logout': 'ログアウト',
      'createProfileTitle': 'プロフィールを作成',
      'profileSubtitle': '詳細を入力して、インドでの体験をカスタマイズしましょう',
      'fullName': '氏名',
      'fullNameHint': '例: Sarah Jenkins',
      'fullNameRequired': '氏名を入力してください',
      'fullNameMin': '2文字以上で入力してください',
      'phoneNumber': '電話番号',
      'phoneHint': '例: 9876543210',
      'phoneRequired': '電話番号を入力してください',
      'phoneInvalid': '電話番号は10桁で入力してください',
      'phoneStartZero': '電話番号は0で始められません',
      'preferredLanguageLabel': '優先音声・翻訳言語',
      'continue': '続行',
      'countryCodeTitle': '国コードを選択',
      'selectCountry': '国コードを選択',
      'permissionHeroText': 'オフライン地図、近くの記念碑、即時音声翻訳を有効にするため。',
      'permissionLocationTitle': '位置情報アクセス',
      'permissionLocationDesc': '近くの遺産スポット、オフラインマップ、徒歩ルートを表示します。',
      'permissionMicTitle': 'マイクアクセス',
      'permissionMicDesc': 'リアルタイムの外国語音声翻訳と音声発音を有効にします。',
      'notNow': '後で',
      'convertFrom': '変換元',
      'convertTo': '変換先',
      'amount': '金額を入力',
      'result': '変換後の値',
      'historyTitle': '歴史と建築',
      'peakRushTitle': 'ピーク時の混雑時間',
      'peakWindow': 'ピークウィンドウ',
      'entryRulesTitle': '入場ルールとアドバイス',
      'bookTickets': '公式チケットを予約（政府ポータル）',
      'searchHint': '場所、都市、観光スポットを検索...',
    },
    'Chinese': {
      'welcome': '欢迎来到印度',
      'explore': '探索',
      'home': '首页',
      'sos': '紧急求助',
      'maps': '地图',
      'translator': '翻译器',
      'currency': '货币兑换',
      'selectCity': '选择城市',
      'quickTools': '快捷工具',
      'permissionsTitle': '允许权限 🛡️',
      'permissionsSubtitle': '为了最佳体验，我们需要访问相关硬件功能。',
      'locationAccess': '定位权限',
      'locationDesc': '用于周边地点与导航。',
      'micAccess': '麦克风权限',
      'micDesc': '用于实时语音翻译。',
      'allow': '允许',
      'allowed': '已允许',
      'allowAll': '允许全部并继续',
      'skipNow': '暂时跳过',
      'profile': '个人资料',
      'logout': '退出登录',
      'createProfileTitle': '创建个人资料',
      'profileSubtitle': '输入您的详细信息，定制您的印度体验',
      'fullName': '全名',
      'fullNameHint': '例如：Sarah Jenkins',
      'fullNameRequired': '全名为必填项',
      'fullNameMin': '至少需要2个字符',
      'phoneNumber': '电话号码',
      'phoneHint': '例如：9876543210',
      'phoneRequired': '电话号码为必填项',
      'phoneInvalid': '电话号码必须为10位数字',
      'phoneStartZero': '电话号码不能以0开头',
      'preferredLanguageLabel': '首选音频与翻译语言',
      'continue': '继续',
      'countryCodeTitle': '选择国家代码',
      'selectCountry': '选择国家代码',
      'permissionHeroText': '启用离线地图、附近古迹发现和即时语音翻译。',
      'permissionLocationTitle': '定位权限',
      'permissionLocationDesc': '用于显示附近遗产地点、离线地图和步行路线。',
      'permissionMicTitle': '麦克风权限',
      'permissionMicDesc': '用于启用实时外语语音翻译和语音发音。',
      'notNow': '稍后',
      'convertFrom': '兑换自',
      'convertTo': '兑换为',
      'amount': '输入金额',
      'result': '兑换结果',
      'historyTitle': '历史与建筑',
      'peakRushTitle': '高峰拥挤时间',
      'peakWindow': '高峰时段',
      'entryRulesTitle': '入场规则与建议',
      'bookTickets': '预订官方门票（政府门户网站）',
      'searchHint': '搜索地点、城市或景点...',
    },
  };

  String getText(String key) {
    return _localizedStrings[_currentLanguage]?[key] ??
        _localizedStrings['English']![key]!;
  }

  Future<void> setLanguage(String lang) async {
    _currentLanguage = lang;
    notifyListeners();
    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('preferred_language', lang);
  }

  Future<void> loadFromPrefs() async {
    final prefs = await SharedPreferences.getInstance();
    _currentLanguage = prefs.getString('preferred_language') ?? 'English';
    notifyListeners();
  }
}

class UserProvider extends ChangeNotifier {
  String _fullName = '';
  String _countryCode = '+91';
  String _phoneNumber = '';
  bool _isLoggedIn = false;
  String _selectedCity = 'Delhi';

  String get fullName => _fullName;
  String get countryCode => _countryCode;
  String get phoneNumber => _phoneNumber;
  bool get isLoggedIn => _isLoggedIn;
  String get selectedCity => _selectedCity;

  Future<void> loadUserData() async {
    final prefs = await SharedPreferences.getInstance();
    final loadedLogin = prefs.getBool('is_logged_in') ?? false;
    final loadedName = prefs.getString('user_name') ?? '';
    final loadedCountryCode = prefs.getString('country_code') ?? '+91';
    final loadedPhone = prefs.getString('phone_number') ?? '';

    _isLoggedIn = loadedLogin;
    _fullName = loadedName;
    _countryCode = loadedCountryCode;
    _phoneNumber = loadedPhone;
    notifyListeners();
  }

  Future<void> setUser({
    required String name,
    required String code,
    required String phone,
  }) async {
    final sanitizedName = name.trim();
    final sanitizedCode = code.trim();
    final sanitizedPhone = phone.trim();

    _fullName = sanitizedName;
    _countryCode = sanitizedCode.isEmpty ? '+91' : sanitizedCode;
    _phoneNumber = sanitizedPhone;
    _isLoggedIn = true;
    notifyListeners();

    final prefs = await SharedPreferences.getInstance();
    await prefs.setString('user_name', _fullName);
    await prefs.setString('country_code', _countryCode);
    await prefs.setString('phone_number', _phoneNumber);
    await prefs.setBool('is_logged_in', true);
  }

  void setCity(String city) {
    _selectedCity = city;
    notifyListeners();
  }

  Future<void> logout() async {
    _isLoggedIn = false;
    _fullName = '';
    _phoneNumber = '';
    _countryCode = '+91';
    notifyListeners();

    final prefs = await SharedPreferences.getInstance();
    await prefs.clear();
  }
}

// ==========================================
// 3. MAIN ENTRY POINT & THEME
// ==========================================

Future<void> main() async {
  WidgetsFlutterBinding.ensureInitialized();

  final userProvider = UserProvider();
  final languageProvider = LanguageProvider();
  await userProvider.loadUserData();
  await languageProvider.loadFromPrefs();

  runApp(
    MultiProvider(
      providers: [
        ChangeNotifierProvider.value(value: userProvider),
        ChangeNotifierProvider.value(value: languageProvider),
      ],
      child: const YatraCompanionApp(),
    ),
  );
}

class YatraCompanionApp extends StatelessWidget {
  const YatraCompanionApp({super.key});

  @override
  Widget build(BuildContext context) {
    const primaryPurple = Color(0xFF5E2B97);
    const accentOrange = Color(0xFFFF6F00);

    return MaterialApp(
      title: 'Explore India',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        useMaterial3: true,
        colorScheme: ColorScheme.fromSeed(
          seedColor: primaryPurple,
          primary: primaryPurple,
          secondary: accentOrange,
          surface: Colors.white,
        ),
        scaffoldBackgroundColor: const Color(0xFFF5F1FB),
        appBarTheme: const AppBarTheme(
          backgroundColor: Colors.white,
          elevation: 0,
          iconTheme: IconThemeData(color: Colors.black),
          titleTextStyle: TextStyle(
              color: Colors.black, fontSize: 20, fontWeight: FontWeight.bold),
        ),
        cardTheme: CardThemeData(
          color: Colors.white,
          elevation: 0,
          margin: EdgeInsets.zero,
          shape:
              RoundedRectangleBorder(borderRadius: BorderRadius.circular(20)),
        ),
        elevatedButtonTheme: ElevatedButtonThemeData(
          style: ElevatedButton.styleFrom(
            backgroundColor: primaryPurple,
            foregroundColor: Colors.white,
            minimumSize: const Size.fromHeight(52),
            shape:
                RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
            textStyle:
                const TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
          ),
        ),
      ),
      home: const SplashScreen(),
    );
  }
}

// ==========================================
// 4. AUTH & SETUP SCREENS
// ==========================================

class SplashScreen extends StatefulWidget {
  const SplashScreen({super.key});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen>
    with SingleTickerProviderStateMixin {
  late AnimationController _controller;
  late Animation<double> _fadeAnimation;

  @override
  void initState() {
    super.initState();
    _controller = AnimationController(
        vsync: this, duration: const Duration(milliseconds: 1500));
    _fadeAnimation = Tween<double>(begin: 0.0, end: 1.0).animate(_controller);
    _controller.forward();
    _navigateToNext();
  }

  void _navigateToNext() async {
    await Future.delayed(const Duration(seconds: 2));
    if (!mounted) return;

    final prefs = await SharedPreferences.getInstance();
    final isLoggedIn = prefs.getBool('is_logged_in') ?? false;

    Widget nextScreen =
        isLoggedIn ? const MainNavigationScreen() : const WelcomeScreen();

    Navigator.of(context).pushReplacement(
      PageRouteBuilder(
        pageBuilder: (_, __, ___) => nextScreen,
        transitionsBuilder: (_, a, __, c) =>
            FadeTransition(opacity: a, child: c),
        transitionDuration: const Duration(milliseconds: 500),
      ),
    );
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: Container(
        decoration: const BoxDecoration(
          gradient: LinearGradient(
            colors: [Color(0xFF3B1566), Color(0xFF5E2B97), Color(0xFF8A42DB)],
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
          ),
        ),
        child: Center(
          child: FadeTransition(
            opacity: _fadeAnimation,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Container(
                  padding: const EdgeInsets.all(24),
                  decoration: BoxDecoration(
                      color: Colors.white.withOpacity(0.15),
                      shape: BoxShape.circle),
                  child: const Icon(Icons.explore_outlined,
                      size: 80, color: Colors.white),
                ),
                const SizedBox(height: 24),
                const Text('Explore India',
                    style: TextStyle(
                        color: Colors.white,
                        fontSize: 32,
                        fontWeight: FontWeight.bold,
                        letterSpacing: 1.2)),
                const SizedBox(height: 8),
                const Text(
                    'Heritage, maps, language, and travel safety in one place.',
                    style: TextStyle(color: Colors.white70, fontSize: 16)),
                const SizedBox(height: 48),
                const CircularProgressIndicator(color: Colors.white),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class WelcomeScreen extends StatelessWidget {
  const WelcomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0F1115),
      body: SafeArea(
        child: LayoutBuilder(
          builder: (context, constraints) {
            return SingleChildScrollView(
              padding: const EdgeInsets.fromLTRB(20, 16, 20, 16),
              child: ConstrainedBox(
                constraints: BoxConstraints(minHeight: constraints.maxHeight),
                child: IntrinsicHeight(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Container(
                        height: 360,
                        decoration: BoxDecoration(
                          borderRadius: BorderRadius.circular(32),
                          boxShadow: [
                            BoxShadow(
                              color: const Color(0xFF381E72).withOpacity(0.35),
                              blurRadius: 28,
                              offset: const Offset(0, 12),
                            ),
                          ],
                          border:
                              Border.all(color: Colors.white.withOpacity(0.12)),
                        ),
                        child: ClipRRect(
                          borderRadius: BorderRadius.circular(32),
                          child: Stack(
                            children: [
                              Positioned.fill(
                                child: Image.asset(
                                  'assets/images/img_taj_mahal_hero.jpg',
                                  fit: BoxFit.cover,
                                ),
                              ),
                              Positioned.fill(
                                child: DecoratedBox(
                                  decoration: BoxDecoration(
                                    gradient: LinearGradient(
                                      begin: Alignment.topCenter,
                                      end: Alignment.bottomCenter,
                                      colors: [
                                        Colors.transparent,
                                        Colors.black.withOpacity(0.15),
                                        Colors.black.withOpacity(0.78),
                                      ],
                                    ),
                                  ),
                                ),
                              ),
                              Positioned(
                                left: -30,
                                top: -30,
                                child: Container(
                                  width: 170,
                                  height: 170,
                                  decoration: BoxDecoration(
                                    shape: BoxShape.circle,
                                    color: Colors.white.withOpacity(0.07),
                                  ),
                                ),
                              ),
                              Positioned(
                                right: -45,
                                bottom: -20,
                                child: Container(
                                  width: 240,
                                  height: 240,
                                  decoration: BoxDecoration(
                                    shape: BoxShape.circle,
                                    color: Colors.white.withOpacity(0.08),
                                  ),
                                ),
                              ),
                              Positioned(
                                top: 18,
                                right: 18,
                                child: Container(
                                  padding: const EdgeInsets.symmetric(
                                      horizontal: 10, vertical: 5),
                                  decoration: BoxDecoration(
                                    color: const Color(0xFFD0BCFF)
                                        .withOpacity(0.22),
                                    borderRadius: BorderRadius.circular(8),
                                  ),
                                  child: const Text(
                                    '🇮🇳 EXPLORE INDIA',
                                    style: TextStyle(
                                      color: Colors.white,
                                      fontWeight: FontWeight.w800,
                                      fontSize: 11,
                                      letterSpacing: 0.2,
                                    ),
                                  ),
                                ),
                              ),
                              Positioned(
                                left: 20,
                                right: 20,
                                bottom: 20,
                                child: Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  children: const [
                                    Text(
                                      'Namaste & Welcome',
                                      style: TextStyle(
                                        color: Colors.white,
                                        fontSize: 24,
                                        fontWeight: FontWeight.bold,
                                      ),
                                    ),
                                    SizedBox(height: 4),
                                    Text(
                                      "Your intelligent companion for exploring India's heritage & wonders.",
                                      style: TextStyle(
                                        color: Colors.white70,
                                        fontSize: 13,
                                        height: 1.5,
                                      ),
                                    ),
                                  ],
                                ),
                              ),
                            ],
                          ),
                        ),
                      ),
                      const SizedBox(height: 24),
                      const Text(
                        'Seamless Travel',
                        style: TextStyle(
                          color: Colors.white,
                          fontSize: 28,
                          fontWeight: FontWeight.w800,
                        ),
                      ),
                      const SizedBox(height: 6),
                      const Text(
                        'Verified monuments, live maps, instant voice translator & 24/7 tourist helpline.',
                        style: TextStyle(
                          color: Colors.white70,
                          fontSize: 14,
                          height: 1.45,
                        ),
                      ),
                      const SizedBox(height: 14),
                      Wrap(
                        spacing: 8,
                        runSpacing: 8,
                        children: const [
                          _WelcomeChip(text: '🏛️ Heritage'),
                          _WelcomeChip(text: '🗣️ Translator'),
                          _WelcomeChip(text: '🚨 SOS 24x7'),
                        ],
                      ),
                      const SizedBox(height: 28),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: () =>
                              Navigator.of(context).pushReplacement(
                            MaterialPageRoute(
                                builder: (_) => const AuthScreen()),
                          ),
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFFD0BCFF),
                            foregroundColor: const Color(0xFF381E72),
                            minimumSize: const Size.fromHeight(54),
                            shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(16)),
                          ),
                          child: const Text('Get Started',
                              style: TextStyle(
                                  fontWeight: FontWeight.bold, fontSize: 16)),
                        ),
                      ),
                      const SizedBox(height: 12),
                      SizedBox(
                        width: double.infinity,
                        child: OutlinedButton(
                          onPressed: () =>
                              Navigator.of(context).pushReplacement(
                            MaterialPageRoute(
                                builder: (_) => const AuthScreen()),
                          ),
                          style: OutlinedButton.styleFrom(
                            side: const BorderSide(color: Colors.white24),
                            foregroundColor: Colors.white,
                            minimumSize: const Size.fromHeight(54),
                            shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(16)),
                          ),
                          child: const Text('Login',
                              style: TextStyle(
                                  fontWeight: FontWeight.w600, fontSize: 16)),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}

class _WelcomeChip extends StatelessWidget {
  final String text;
  const _WelcomeChip({required this.text});

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.08),
        border: Border.all(color: Colors.white.withOpacity(0.12)),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(text,
          style: const TextStyle(
              color: Colors.white, fontSize: 12, fontWeight: FontWeight.w600)),
    );
  }
}

class LoginScreen extends StatelessWidget {
  const LoginScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return const AuthScreen();
  }
}

class AuthScreen extends StatefulWidget {
  const AuthScreen({super.key});

  @override
  State<AuthScreen> createState() => _AuthScreenState();
}

class _AuthScreenState extends State<AuthScreen> {
  final _formKey = GlobalKey<FormState>();
  final _nameController = TextEditingController();
  final _phoneController = TextEditingController();

  Country _selectedCountry = countriesList[0];
  String _selectedLanguage = 'English';

  void _showCountryPicker() {
    final lang = Provider.of<LanguageProvider>(context, listen: false);

    showModalBottomSheet(
      context: context,
      shape: const RoundedRectangleBorder(
          borderRadius: BorderRadius.vertical(top: Radius.circular(20))),
      builder: (context) {
        return Container(
          padding: const EdgeInsets.all(16),
          height: 350,
          child: Column(
            children: [
              Text(lang.getText('countryCodeTitle'),
                  style: const TextStyle(
                      fontSize: 18, fontWeight: FontWeight.bold)),
              const SizedBox(height: 12),
              Expanded(
                child: ListView.builder(
                  itemCount: countriesList.length,
                  itemBuilder: (context, idx) {
                    final c = countriesList[idx];
                    return ListTile(
                      leading:
                          Text(c.flag, style: const TextStyle(fontSize: 24)),
                      title: Text(c.name),
                      trailing: Text(c.code,
                          style: const TextStyle(fontWeight: FontWeight.bold)),
                      onTap: () {
                        setState(() => _selectedCountry = c);
                        Navigator.pop(context);
                      },
                    );
                  },
                ),
              )
            ],
          ),
        );
      },
    );
  }

  Future<void> _handleContinue() async {
    if (_formKey.currentState!.validate()) {
      final userProv = Provider.of<UserProvider>(context, listen: false);
      final langProv = Provider.of<LanguageProvider>(context, listen: false);

      await userProv.setUser(
        name: _nameController.text,
        code: _selectedCountry.code,
        phone: _phoneController.text,
      );
      await langProv.setLanguage(_selectedLanguage);

      if (!mounted) return;
      Navigator.of(context).pushReplacement(
        MaterialPageRoute(builder: (_) => const PermissionsScreen()),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    final lang = Provider.of<LanguageProvider>(context);

    return Scaffold(
      backgroundColor: const Color(0xFF0F1115),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 16),
          child: Form(
            key: _formKey,
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                IconButton(
                  onPressed: () => Navigator.of(context).pop(),
                  style: IconButton.styleFrom(
                    backgroundColor: Colors.white.withOpacity(0.08),
                    shape: const CircleBorder(),
                    minimumSize: const Size(42, 42),
                  ),
                  icon: const Icon(Icons.arrow_back, color: Color(0xFFD0BCFF)),
                ),
                const SizedBox(height: 16),
                Text(
                  lang.getText('createProfileTitle'),
                  style: const TextStyle(
                      fontSize: 28,
                      fontWeight: FontWeight.w800,
                      color: Colors.white),
                ),
                const SizedBox(height: 6),
                Text(
                  lang.getText('profileSubtitle'),
                  style: const TextStyle(
                      color: Colors.white70, fontSize: 14, height: 1.45),
                ),
                const SizedBox(height: 28),
                Text(
                  lang.getText('fullName'),
                  style: const TextStyle(
                      color: Colors.white60,
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 1.2),
                ),
                const SizedBox(height: 8),
                TextFormField(
                  controller: _nameController,
                  inputFormatters: [
                    FilteringTextInputFormatter.allow(RegExp(r'[a-zA-Z\s]'))
                  ],
                  style: const TextStyle(color: Colors.white),
                  decoration: InputDecoration(
                    hintText: lang.getText('fullNameHint'),
                    hintStyle: const TextStyle(color: Colors.white38),
                    prefixIcon:
                        const Icon(Icons.person_outline, color: Colors.white70),
                    filled: true,
                    fillColor: Colors.white.withOpacity(0.04),
                    border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(16),
                        borderSide: BorderSide(color: Colors.white12)),
                    enabledBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(16),
                        borderSide: BorderSide(color: Colors.white12)),
                    focusedBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(16),
                        borderSide: BorderSide(color: Color(0xFFD0BCFF))),
                  ),
                  validator: (val) {
                    if (val == null || val.trim().isEmpty)
                      return lang.getText('fullNameRequired');
                    if (val.trim().length < 2)
                      return lang.getText('fullNameMin');
                    return null;
                  },
                ),
                const SizedBox(height: 16),
                Text(
                  lang.getText('phoneNumber'),
                  style: const TextStyle(
                      color: Colors.white60,
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 1.2),
                ),
                const SizedBox(height: 8),
                Row(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    InkWell(
                      onTap: _showCountryPicker,
                      child: Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 12, vertical: 16),
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.04),
                          border: Border.all(color: Colors.white12),
                          borderRadius: BorderRadius.circular(16),
                        ),
                        child: Row(
                          children: [
                            Text(_selectedCountry.flag,
                                style: const TextStyle(fontSize: 20)),
                            const SizedBox(width: 6),
                            Text(_selectedCountry.code,
                                style: const TextStyle(
                                    fontWeight: FontWeight.bold,
                                    color: Colors.white)),
                            const Icon(Icons.arrow_drop_down,
                                color: Color(0xFFD0BCFF)),
                          ],
                        ),
                      ),
                    ),
                    const SizedBox(width: 10),
                    Expanded(
                      child: TextFormField(
                        controller: _phoneController,
                        keyboardType: TextInputType.phone,
                        inputFormatters: [
                          FilteringTextInputFormatter.digitsOnly
                        ],
                        style: const TextStyle(color: Colors.white),
                        decoration: InputDecoration(
                          hintText: lang.getText('phoneHint'),
                          hintStyle: const TextStyle(color: Colors.white38),
                          filled: true,
                          fillColor: Colors.white.withOpacity(0.04),
                          border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(16),
                              borderSide: BorderSide(color: Colors.white12)),
                          enabledBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(16),
                              borderSide: BorderSide(color: Colors.white12)),
                          focusedBorder: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(16),
                              borderSide: BorderSide(color: Color(0xFFD0BCFF))),
                        ),
                        validator: (val) {
                          if (val == null || val.isEmpty)
                            return lang.getText('phoneRequired');
                          final clean = val.trim();
                          if (clean.length != 10)
                            return lang.getText('phoneInvalid');
                          if (clean.startsWith('0'))
                            return lang.getText('phoneStartZero');
                          return null;
                        },
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                Text(
                  lang.getText('preferredLanguageLabel'),
                  style: const TextStyle(
                      color: Colors.white60,
                      fontSize: 12,
                      fontWeight: FontWeight.bold,
                      letterSpacing: 0.9),
                ),
                const SizedBox(height: 8),
                DropdownButtonFormField<String>(
                  value: _selectedLanguage,
                  dropdownColor: const Color(0xFF1C1B1F),
                  style: const TextStyle(color: Colors.white),
                  decoration: InputDecoration(
                    filled: true,
                    fillColor: Colors.white.withOpacity(0.04),
                    prefixIcon:
                        const Icon(Icons.language, color: Colors.white70),
                    border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(16),
                        borderSide: BorderSide(color: Colors.white12)),
                    enabledBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(16),
                        borderSide: BorderSide(color: Colors.white12)),
                    focusedBorder: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(16),
                        borderSide: BorderSide(color: Color(0xFFD0BCFF))),
                  ),
                  items: ['English', 'French', 'German', 'Japanese', 'Chinese']
                      .map((langOption) => DropdownMenuItem(
                          value: langOption, child: Text(langOption)))
                      .toList(),
                  onChanged: (val) {
                    if (val != null) setState(() => _selectedLanguage = val);
                  },
                ),
                const SizedBox(height: 36),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: _handleContinue,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: const Color(0xFFD0BCFF),
                      foregroundColor: const Color(0xFF381E72),
                      minimumSize: const Size.fromHeight(54),
                      shape: RoundedRectangleBorder(
                          borderRadius: BorderRadius.circular(16)),
                    ),
                    child: Text(lang.getText('continue'),
                        style: const TextStyle(
                            fontWeight: FontWeight.bold, fontSize: 16)),
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class PermissionsScreen extends StatefulWidget {
  const PermissionsScreen({super.key});

  @override
  State<PermissionsScreen> createState() => _PermissionsScreenState();
}

class _PermissionsScreenState extends State<PermissionsScreen> {
  PermissionStatus _locationStatus = PermissionStatus.denied;
  PermissionStatus _micStatus = PermissionStatus.denied;

  @override
  void initState() {
    super.initState();
    _refreshPermissionStatuses();
  }

  Future<void> _refreshPermissionStatuses() async {
    final locationStatus = await Permission.location.status;
    final micStatus = await Permission.microphone.status;
    if (!mounted) return;
    setState(() {
      _locationStatus = locationStatus;
      _micStatus = micStatus;
    });
  }

  Future<void> _requestLocation() async {
    final status = await Permission.location.request();
    if (!mounted) return;
    setState(() => _locationStatus = status);
  }

  Future<void> _requestMic() async {
    var status = await Permission.microphone.request();
    if (status.isPermanentlyDenied) {
      await openAppSettings();
      status = await Permission.microphone.status;
    }
    if (!mounted) return;
    setState(() => _micStatus = status);
  }

  Future<void> _requestAll() async {
    await _requestLocation();
    await _requestMic();
    _proceed();
  }

  void _proceed() {
    Navigator.of(context).pushReplacement(
      MaterialPageRoute(builder: (_) => const MainNavigationScreen()),
    );
  }

  @override
  Widget build(BuildContext context) {
    final lang = Provider.of<LanguageProvider>(context);

    return Scaffold(
      backgroundColor: const Color(0xFF0F1115),
      body: SafeArea(
        child: LayoutBuilder(
          builder: (context, constraints) {
            return SingleChildScrollView(
              padding:
                  const EdgeInsets.symmetric(horizontal: 24.0, vertical: 24.0),
              child: ConstrainedBox(
                constraints: BoxConstraints(minHeight: constraints.maxHeight),
                child: IntrinsicHeight(
                  child: Column(
                    children: [
                      const SizedBox(height: 20),
                      Container(
                        width: 96,
                        height: 96,
                        decoration: BoxDecoration(
                          color: const Color(0xFF381E72),
                          shape: BoxShape.circle,
                          border: Border.all(
                              color: const Color(0xFFD0BCFF).withOpacity(0.45),
                              width: 1.5),
                        ),
                        child: const Icon(Icons.location_on_rounded,
                            size: 48, color: Color(0xFFD0BCFF)),
                      ),
                      const SizedBox(height: 24),
                      Text(
                        lang.getText('permissionsTitle'),
                        style: const TextStyle(
                            fontSize: 28,
                            fontWeight: FontWeight.w800,
                            color: Colors.white),
                      ),
                      const SizedBox(height: 8),
                      Text(
                        lang.getText('permissionHeroText'),
                        textAlign: TextAlign.center,
                        style: const TextStyle(
                            color: Colors.white70, fontSize: 14, height: 1.45),
                      ),
                      const SizedBox(height: 32),
                      _PermissionCard(
                        icon: Icons.location_on_rounded,
                        iconColor: const Color(0xFFD0BCFF),
                        title: lang.getText('permissionLocationTitle'),
                        subtitle: lang.getText('permissionLocationDesc'),
                        action: _locationStatus.isGranted
                            ? lang.getText('allowed')
                            : lang.getText('allow'),
                        onPressed:
                            _locationStatus.isGranted ? null : _requestLocation,
                      ),
                      const SizedBox(height: 16),
                      _PermissionCard(
                        icon: Icons.mic_rounded,
                        iconColor: const Color(0xFFFFB74D),
                        title: lang.getText('permissionMicTitle'),
                        subtitle: lang.getText('permissionMicDesc'),
                        action: _micStatus.isGranted
                            ? lang.getText('allowed')
                            : lang.getText('allow'),
                        onPressed: _micStatus.isGranted ? null : _requestMic,
                      ),
                      const Spacer(),
                      const SizedBox(height: 16),
                      SizedBox(
                        width: double.infinity,
                        child: ElevatedButton(
                          onPressed: _requestAll,
                          style: ElevatedButton.styleFrom(
                            backgroundColor: const Color(0xFFD0BCFF),
                            foregroundColor: const Color(0xFF381E72),
                            minimumSize: const Size.fromHeight(54),
                            shape: RoundedRectangleBorder(
                                borderRadius: BorderRadius.circular(16)),
                          ),
                          child: Text(lang.getText('allowAll'),
                              style: const TextStyle(
                                  fontWeight: FontWeight.bold, fontSize: 16)),
                        ),
                      ),
                      const SizedBox(height: 12),
                      SizedBox(
                        width: double.infinity,
                        child: TextButton(
                          onPressed: _proceed,
                          child: Text(lang.getText('notNow'),
                              style: const TextStyle(
                                  color: Colors.white70,
                                  fontWeight: FontWeight.w600,
                                  fontSize: 15)),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            );
          },
        ),
      ),
    );
  }
}

class _PermissionCard extends StatelessWidget {
  final IconData icon;
  final Color iconColor;
  final String title;
  final String subtitle;
  final String action;
  final VoidCallback? onPressed;

  const _PermissionCard({
    required this.icon,
    required this.iconColor,
    required this.title,
    required this.subtitle,
    required this.action,
    this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        color: Colors.white.withOpacity(0.04),
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: Colors.white12),
      ),
      child: Row(
        children: [
          Container(
            width: 52,
            height: 52,
            decoration: BoxDecoration(
              color: const Color(0xFF381E72).withOpacity(0.52),
              borderRadius: BorderRadius.circular(16),
            ),
            child: Icon(icon, color: iconColor, size: 24),
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(title,
                    style: const TextStyle(
                        color: Colors.white,
                        fontSize: 16,
                        fontWeight: FontWeight.bold)),
                const SizedBox(height: 4),
                Text(subtitle,
                    style: const TextStyle(
                        color: Colors.white60, fontSize: 12.5, height: 1.4)),
              ],
            ),
          ),
          const SizedBox(width: 8),
          ElevatedButton(
            onPressed: onPressed,
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFFD0BCFF),
              foregroundColor: const Color(0xFF381E72),
              minimumSize: const Size(88, 36),
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(10)),
            ),
            child: Text(action,
                style:
                    const TextStyle(fontWeight: FontWeight.bold, fontSize: 12)),
          ),
        ],
      ),
    );
  }
}

// ==========================================
// 5. MAIN NAVIGATION BAR CONTAINER
// ==========================================

class MainNavigationScreen extends StatefulWidget {
  const MainNavigationScreen({super.key});

  @override
  State<MainNavigationScreen> createState() => _MainNavigationScreenState();
}

class _MainNavigationScreenState extends State<MainNavigationScreen> {
  int _currentIndex = 0;

  final List<Widget> _tabs = [
    const HomeTab(),
    const OfflineMapEngineWidget(),
    const SOSWidget(),
    const TranslatorScreen(),
    const ProfileTab(),
  ];

  @override
  Widget build(BuildContext context) {
    final lang = Provider.of<LanguageProvider>(context);

    return Scaffold(
      backgroundColor: const Color(0xFF0F1115),
      body: IndexedStack(
        index: _currentIndex,
        children: _tabs,
      ),
      bottomNavigationBar: Container(
        decoration: BoxDecoration(
          color: const Color(0xFF171B1F),
          border:
              Border(top: BorderSide(color: Colors.white.withOpacity(0.12))),
        ),
        child: NavigationBar(
          selectedIndex: _currentIndex,
          onDestinationSelected: (index) =>
              setState(() => _currentIndex = index),
          backgroundColor: Colors.transparent,
          elevation: 0,
          height: 76,
          indicatorColor: const Color(0xFF2A615C),
          destinations: [
            NavigationDestination(
                icon: Icon(Icons.home_rounded), label: lang.getText('home')),
            NavigationDestination(
                icon: Icon(Icons.map_rounded), label: lang.getText('maps')),
            NavigationDestination(
                icon:
                    Icon(Icons.warning_amber_rounded, color: Colors.redAccent),
                label: lang.getText('sos')),
            NavigationDestination(
                icon: Icon(Icons.translate_rounded),
                label: lang.getText('translator')),
            NavigationDestination(
                icon: Icon(Icons.person_rounded),
                label: lang.getText('profile')),
          ],
        ),
      ),
    );
  }

  Widget _buildNavItem(int index, IconData icon, String label) {
    final selected = _currentIndex == index;
    return InkWell(
      onTap: () => setState(() => _currentIndex = index),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon,
              color: selected ? Theme.of(context).primaryColor : Colors.grey),
          Text(label,
              style: TextStyle(
                  fontSize: 12,
                  color:
                      selected ? Theme.of(context).primaryColor : Colors.grey,
                  fontWeight: selected ? FontWeight.bold : FontWeight.normal)),
        ],
      ),
    );
  }
}

// WRAPPER TO OVERRIDE EXPLORE TAB CLICK TO FULLY LOCALIZED SITE SCREEN
class CityFoodPlace {
  final String name;
  final String subtitle;
  final String detail;
  final bool isCafe;

  const CityFoodPlace({
    required this.name,
    required this.subtitle,
    required this.detail,
    required this.isCafe,
  });
}

const Map<String, Map<String, List<CityFoodPlace>>> cityFoodGuide = {
  'Delhi': {
    'cafes': [
      CityFoodPlace(
          name: 'United Coffee House',
          subtitle: 'Connaught Place',
          detail: 'Old-school Delhi café experience',
          isCafe: true),
      CityFoodPlace(
          name: 'Café Lota',
          subtitle: 'National Crafts Museum',
          detail: 'Indian food + beautiful traditional setting',
          isCafe: true),
      CityFoodPlace(
          name: 'Indian Coffee House',
          subtitle: 'Connaught Place',
          detail: 'Iconic old Delhi intellectual café vibe',
          isCafe: true),
      CityFoodPlace(
          name: 'Perch Wine & Coffee Bar',
          subtitle: 'Khan Market',
          detail: 'Modern café experience',
          isCafe: true),
      CityFoodPlace(
          name: 'Triveni Terrace Café',
          subtitle: 'Mandi House',
          detail: 'Artsy Delhi experience',
          isCafe: true),
    ],
    'streetFood': [
      CityFoodPlace(
          name: 'Jain Chawal Wale',
          subtitle: 'Rajma Chawal / Kadhi Chawal',
          detail: 'Comfort classic',
          isCafe: false),
      CityFoodPlace(
          name: 'Paranthe Wali Gali',
          subtitle: 'Stuffed Parathas',
          detail: 'Must-have Delhi specialty',
          isCafe: false),
      CityFoodPlace(
          name: 'Jung Bahadur Kachori Wala',
          subtitle: 'Kachori-Sabzi',
          detail: 'Crunchy and spicy',
          isCafe: false),
      CityFoodPlace(
          name: 'Natraj Dahi Bhalle',
          subtitle: 'Dahi Bhalla + Aloo Tikki',
          detail: 'Street snack favorite',
          isCafe: false),
      CityFoodPlace(
          name: 'Old Famous Jalebi Wala',
          subtitle: 'Jalebi',
          detail: 'Sweet Delhi icon',
          isCafe: false),
    ],
  },
  'Mumbai': {
    'cafes': [
      CityFoodPlace(
          name: 'Britannia & Co.',
          subtitle: 'Berry Pulao + Caramel Custard',
          detail: 'Classic Irani café legacy',
          isCafe: true),
      CityFoodPlace(
          name: 'Kyani & Co.',
          subtitle: 'Bun Maska + Irani Chai',
          detail: 'Heritage café moment',
          isCafe: true),
      CityFoodPlace(
          name: 'Yazdani Bakery',
          subtitle: 'Brun Maska + Apple Pie',
          detail: 'Old-school bakery charm',
          isCafe: true),
      CityFoodPlace(
          name: 'Café Military',
          subtitle: 'Keema Pav / Akuri',
          detail: 'Neighborhood favorite',
          isCafe: true),
      CityFoodPlace(
          name: 'B. Merwan & Co.',
          subtitle: 'Mawa Cake + Chai',
          detail: 'Mumbai breakfast classic',
          isCafe: true),
    ],
    'streetFood': [
      CityFoodPlace(
          name: 'Ashok Vada Pav',
          subtitle: 'Vada Pav',
          detail: 'Mumbai staple',
          isCafe: false),
      CityFoodPlace(
          name: 'Shivaji Park / Dadar',
          subtitle: 'Mumbai-style Vada Pav',
          detail: 'Quick city bite',
          isCafe: false),
      CityFoodPlace(
          name: 'Bademiya',
          subtitle: 'Kebabs / Rolls',
          detail: 'Late-night indulgence',
          isCafe: false),
      CityFoodPlace(
          name: 'Elco Pani Puri Centre',
          subtitle: 'Pani Puri',
          detail: 'Crisp and tangy',
          isCafe: false),
      CityFoodPlace(
          name: 'Girgaum Chowpatty',
          subtitle: 'Pav Bhaji / Kulfi',
          detail: 'Beach-side treat',
          isCafe: false),
    ],
  },
  'Agra': {
    'cafes': [
      CityFoodPlace(
          name: 'The Salt Café Kitchen',
          subtitle: 'Rooftop Taj-view experience',
          detail: 'Best for scenic café vibes',
          isCafe: true),
      CityFoodPlace(
          name: 'Café Turquoise Cottage',
          subtitle: 'Casual café',
          detail: 'Laid-back stop',
          isCafe: true),
      CityFoodPlace(
          name: 'Café Sheroes Hangout',
          subtitle: 'Social / unique experience',
          detail: 'Creative café energy',
          isCafe: true),
      CityFoodPlace(
          name: 'Pind Balluchi',
          subtitle: 'Traditional North Indian ambience',
          detail: 'Comfort food and heritage feel',
          isCafe: true),
      CityFoodPlace(
          name: 'Esphahan',
          subtitle: 'Luxury Mughlai dining',
          detail: 'Elegant food escape',
          isCafe: true),
    ],
    'streetFood': [
      CityFoodPlace(
          name: 'Panchhi Petha',
          subtitle: 'Agra Petha',
          detail: 'Classic sweet souvenir',
          isCafe: false),
      CityFoodPlace(
          name: 'Deviram Sweets',
          subtitle: 'Bedai + Jalebi',
          detail: 'Breakfast favorite',
          isCafe: false),
      CityFoodPlace(
          name: 'Ram Babu Paratha Bhandar',
          subtitle: 'Agra-style Paratha',
          detail: 'Hearty local food',
          isCafe: false),
      CityFoodPlace(
          name: 'Sadar Bazaar Chaat',
          subtitle: 'Golgappa / Aloo Tikki / Chaat',
          detail: 'Tangy street crunch',
          isCafe: false),
      CityFoodPlace(
          name: 'Agra Chaat Gali',
          subtitle: 'Local Chaat experience',
          detail: 'Popular quick bites',
          isCafe: false),
    ],
  },
  'Bengaluru': {
    'cafes': [
      CityFoodPlace(
          name: 'Indian Coffee House',
          subtitle: 'Old-school Bengaluru experience',
          detail: 'Classic café culture',
          isCafe: true),
      CityFoodPlace(
          name: 'Third Wave Coffee',
          subtitle: 'Modern Indian coffee culture',
          detail: 'Trend-setting coffee stop',
          isCafe: true),
      CityFoodPlace(
          name: 'MTR',
          subtitle: 'Mavalli Tiffin Rooms',
          detail: 'Legendary South Indian institution',
          isCafe: true),
      CityFoodPlace(
          name: 'Airlines Hotel',
          subtitle: 'Open-air old Bengaluru café vibe',
          detail: 'Retro relaxation',
          isCafe: true),
      CityFoodPlace(
          name: 'The Hole in the Wall Café',
          subtitle: 'Breakfast / brunch favorite',
          detail: 'Traveller and local favourite',
          isCafe: true),
    ],
    'streetFood': [
      CityFoodPlace(
          name: 'Vidyarthi Bhavan',
          subtitle: 'Benne Masala Dosa',
          detail: 'Iconic dosa experience',
          isCafe: false),
      CityFoodPlace(
          name: 'VV Puram Food Street',
          subtitle: 'Multiple Bengaluru street foods',
          detail: 'A food lover’s lane',
          isCafe: false),
      CityFoodPlace(
          name: 'Brahmin\'s Coffee Bar',
          subtitle: 'Idli + Vada + Filter Coffee',
          detail: 'Breakfast staple',
          isCafe: false),
      CityFoodPlace(
          name: 'CTR / Shri Sagar',
          subtitle: 'Benne Masala Dosa',
          detail: 'South Indian flavor icon',
          isCafe: false),
      CityFoodPlace(
          name: 'Shivajinagar',
          subtitle: 'Kebabs / Biriyani / Street food',
          detail: 'Night-time food crawl',
          isCafe: false),
    ],
  },
  'Varanasi': {
    'cafes': [
      CityFoodPlace(
          name: 'Pizzeria Vaatika Café',
          subtitle: 'Assi Ghat',
          detail: 'Pizza + Ganga view',
          isCafe: true),
      CityFoodPlace(
          name: 'Brown Bread Bakery',
          subtitle: 'Assi / Ghat area',
          detail: 'Friendly bakery stop',
          isCafe: true),
      CityFoodPlace(
          name: 'Open Hand Café',
          subtitle: 'Modern café + traveller vibe',
          detail: 'Easy, relaxed hangout',
          isCafe: true),
      CityFoodPlace(
          name: 'Aum Café',
          subtitle: 'Assi area',
          detail: 'Quiet café break',
          isCafe: true),
      CityFoodPlace(
          name: 'Vaatika Café',
          subtitle: 'Rooftop / river-view',
          detail: 'Scenic café experience',
          isCafe: true),
    ],
    'streetFood': [
      CityFoodPlace(
          name: 'Blue Lassi Shop',
          subtitle: 'Fruit Lassi',
          detail: 'Refreshing local classic',
          isCafe: false),
      CityFoodPlace(
          name: 'Ram Bhandar',
          subtitle: 'Kachori-Sabzi',
          detail: 'Famous savory bite',
          isCafe: false),
      CityFoodPlace(
          name: 'Kashi Chat Bhandar',
          subtitle: 'Tamatar Chaat',
          detail: 'Spicy local favorite',
          isCafe: false),
      CityFoodPlace(
          name: 'Deena Chaat Bhandar',
          subtitle: 'Chaat',
          detail: 'Crisp and tangy',
          isCafe: false),
      CityFoodPlace(
          name: 'Kachori Gali',
          subtitle: 'Kachori-Sabzi',
          detail: 'Street-food lane staple',
          isCafe: false),
    ],
  },
  'Kolkata': {
    'cafes': [
      CityFoodPlace(
          name: 'Indian Coffee House',
          subtitle: 'College Street',
          detail: 'Classic student café energy',
          isCafe: true),
      CityFoodPlace(
          name: 'Flurys',
          subtitle: 'Park Street',
          detail: 'Iconic café institution',
          isCafe: true),
      CityFoodPlace(
          name: '8th Day Café & Bakery',
          subtitle: 'Cafe / bakery',
          detail: 'Relaxed dessert stop',
          isCafe: true),
      CityFoodPlace(
          name: 'Sienna Store & Café',
          subtitle: 'Cafe and dining',
          detail: 'Comfort coffee spot',
          isCafe: true),
      CityFoodPlace(
          name: 'The Bhawanipur House',
          subtitle: 'Heritage café experience',
          detail: 'Elegant local charm',
          isCafe: true),
    ],
    'streetFood': [
      CityFoodPlace(
          name: 'Nizam\'s',
          subtitle: 'Kathi Roll',
          detail: 'Kolkata legend',
          isCafe: false),
      CityFoodPlace(
          name: 'Vivekananda Park',
          subtitle: 'Puchka',
          detail: 'Bursting local favorite',
          isCafe: false),
      CityFoodPlace(
          name: 'Jhalmuri stalls',
          subtitle: 'Park Street / Triangular Park',
          detail: 'Crunchy and nostalgic',
          isCafe: false),
      CityFoodPlace(
          name: 'Kusum Rolls',
          subtitle: 'Kathi Roll',
          detail: 'Quick bite favorite',
          isCafe: false),
      CityFoodPlace(
          name: 'Dacre Street / New Market',
          subtitle: 'Street-food experience',
          detail: 'Classic Kolkata food crawl',
          isCafe: false),
    ],
  },
  'Pune': {
    'cafes': [
      CityFoodPlace(
          name: 'Vohuman Café',
          subtitle: 'Bun Maska + Irani Chai',
          detail: 'Legacy café must-try',
          isCafe: true),
      CityFoodPlace(
          name: 'Goodluck Café',
          subtitle: 'Keema Pav + Bun Maska',
          detail: 'Pune classic',
          isCafe: true),
      CityFoodPlace(
          name: 'Vaishali',
          subtitle: 'South Indian café institution',
          detail: 'Breakfast and coffee landmark',
          isCafe: true),
      CityFoodPlace(
          name: 'Café Goodluck / Deccan',
          subtitle: 'Old Pune vibe',
          detail: 'Comforting city stop',
          isCafe: true),
      CityFoodPlace(
          name: 'German Bakery',
          subtitle: 'Koregaon Park',
          detail: 'Traveller favourite',
          isCafe: true),
    ],
    'streetFood': [
      CityFoodPlace(
          name: 'Shree Upahaar Gruha / Bedekar Misal',
          subtitle: 'Misal Pav',
          detail: 'Pune signature',
          isCafe: false),
      CityFoodPlace(
          name: 'Katakirr Misal',
          subtitle: 'Misal Pav',
          detail: 'Hot and fiery',
          isCafe: false),
      CityFoodPlace(
          name: 'Garden Vada Pav',
          subtitle: 'Vada Pav',
          detail: 'On-the-go local snack',
          isCafe: false),
      CityFoodPlace(
          name: 'Sujata Mastani',
          subtitle: 'Mastani',
          detail: 'Sweet indulgence',
          isCafe: false),
      CityFoodPlace(
          name: 'Chitale Bandhu',
          subtitle: 'Bhakarwadi',
          detail: 'Savory snack staple',
          isCafe: false),
    ],
  },
  'Jaipur': {
    'cafes': [
      CityFoodPlace(
          name: 'Tapri Central',
          subtitle: 'Chai + snacks + rooftop vibe',
          detail: 'Popular city hangout',
          isCafe: true),
      CityFoodPlace(
          name: 'The Tattoo Café & Lounge',
          subtitle: 'Hawa Mahal view',
          detail: 'Rooftop café scene',
          isCafe: true),
      CityFoodPlace(
          name: 'Wind View Café',
          subtitle: 'Hawa Mahal view',
          detail: 'Scenic city stop',
          isCafe: true),
      CityFoodPlace(
          name: 'Café Palladio',
          subtitle: 'Heritage / luxury café experience',
          detail: 'Elegant dining break',
          isCafe: true),
      CityFoodPlace(
          name: 'Anokhi Café',
          subtitle: 'Modern Indian café',
          detail: 'Stylish and relaxed',
          isCafe: true),
    ],
    'streetFood': [
      CityFoodPlace(
          name: 'Rawat Mishthan Bhandar',
          subtitle: 'Pyaaz Kachori',
          detail: 'Jaipur icon',
          isCafe: false),
      CityFoodPlace(
          name: 'Lassiwala',
          subtitle: 'Lassi',
          detail: 'Masala and sweet blends',
          isCafe: false),
      CityFoodPlace(
          name: 'Samrat Restaurant',
          subtitle: 'Kachori / Dal Baati',
          detail: 'Classic local flavors',
          isCafe: false),
      CityFoodPlace(
          name: 'Gulab Ji Chai Wale',
          subtitle: 'Chai',
          detail: 'Must-stop tea break',
          isCafe: false),
      CityFoodPlace(
          name: 'Masala Chowk',
          subtitle: 'Jaipur street-food sampler',
          detail: 'Great for trying many bites',
          isCafe: false),
    ],
  },
};

class CityFoodDetailScreen extends StatelessWidget {
  final String cityName;

  const CityFoodDetailScreen({super.key, required this.cityName});

  @override
  Widget build(BuildContext context) {
    final entries = cityFoodGuide[cityName] ??
        {
          'cafes': const <CityFoodPlace>[],
          'streetFood': const <CityFoodPlace>[]
        };
    final cafeItems = entries['cafes'] ?? const <CityFoodPlace>[];
    final streetItems = entries['streetFood'] ?? const <CityFoodPlace>[];

    final cityAccent = {
          'Delhi': const Color(0xFFB8572D),
          'Mumbai': const Color(0xFF1E88E5),
          'Agra': const Color(0xFF8E5E3B),
          'Bengaluru': const Color(0xFF43A047),
          'Varanasi': const Color(0xFF6A1B9A),
          'Kolkata': const Color(0xFFE53935),
          'Pune': const Color(0xFF009688),
          'Jaipur': const Color(0xFFFFB300),
        }[cityName] ??
        const Color(0xFF381E72);

    return DefaultTabController(
      length: 2,
      child: Scaffold(
        backgroundColor: const Color(0xFF0F1115),
        appBar: AppBar(
          backgroundColor: const Color(0xFF0F1115),
          foregroundColor: Colors.white,
          elevation: 0,
          title: Text('$cityName Food Guide'),
          bottom: TabBar(
            indicatorColor: const Color(0xFFD0BCFF),
            labelColor: Colors.white,
            unselectedLabelColor: Colors.white60,
            tabs: const [
              Tab(text: 'Cafés'),
              Tab(text: 'Street Food'),
            ],
          ),
        ),
        body: Column(
          children: [
            Container(
              width: double.infinity,
              margin: const EdgeInsets.fromLTRB(16, 10, 16, 0),
              padding: const EdgeInsets.all(18),
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  colors: [
                    cityAccent.withOpacity(0.97),
                    cityAccent.withOpacity(0.78),
                    const Color(0xFF1B1B2F).withOpacity(0.95),
                  ],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.circular(24),
                border: Border.all(color: Colors.white.withOpacity(0.14)),
                boxShadow: [
                  BoxShadow(
                    color: cityAccent.withOpacity(0.38),
                    blurRadius: 28,
                    offset: const Offset(0, 14),
                  ),
                ],
              ),
              child: Stack(
                children: [
                  Positioned(
                    right: -18,
                    top: -18,
                    child: Container(
                      width: 120,
                      height: 120,
                      decoration: BoxDecoration(
                        shape: BoxShape.circle,
                        gradient: RadialGradient(
                          colors: [
                            Colors.white.withOpacity(0.28),
                            Colors.white.withOpacity(0.08),
                            Colors.transparent,
                          ],
                          stops: const [0.0, 0.5, 1.0],
                        ),
                      ),
                    ),
                  ),
                  Row(
                    children: [
                      Container(
                        width: 54,
                        height: 54,
                        decoration: BoxDecoration(
                          color: Colors.white.withOpacity(0.14),
                          borderRadius: BorderRadius.circular(16),
                          border:
                              Border.all(color: Colors.white.withOpacity(0.2)),
                        ),
                        child: const Icon(Icons.restaurant_rounded,
                            color: Colors.white, size: 28),
                      ),
                      const SizedBox(width: 14),
                      Expanded(
                        child: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Container(
                              padding: const EdgeInsets.symmetric(
                                  horizontal: 8, vertical: 4),
                              decoration: BoxDecoration(
                                color: Colors.white.withOpacity(0.12),
                                borderRadius: BorderRadius.circular(999),
                                border: Border.all(
                                    color: Colors.white.withOpacity(0.18)),
                              ),
                              child: const Text(
                                'Curated for you',
                                style: TextStyle(
                                    fontSize: 10,
                                    fontWeight: FontWeight.w700,
                                    color: Colors.white,
                                    letterSpacing: 0.8),
                              ),
                            ),
                            const SizedBox(height: 8),
                            Text(
                              cityName,
                              style: const TextStyle(
                                  fontSize: 24,
                                  fontWeight: FontWeight.w800,
                                  color: Colors.white),
                            ),
                            const SizedBox(height: 4),
                            Text(
                              'Must-try cafés & street food picks',
                              style: TextStyle(
                                  fontSize: 13,
                                  color: Colors.white.withOpacity(0.8)),
                            ),
                          ],
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            Expanded(
              child: TabBarView(
                children: [
                  _FoodListTileView(cityName: cityName, items: cafeItems),
                  _FoodListTileView(cityName: cityName, items: streetItems),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _FoodListTileView extends StatelessWidget {
  final String cityName;
  final List<CityFoodPlace> items;

  const _FoodListTileView({required this.cityName, required this.items});

  static const _cafePhotos = [
    'https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1511081692775-05d0f180a065?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1509042239860-f550ce710b93?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1554118811-1e0d58224f24?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=900&q=85',
  ];

  static const _streetFoodPhotos = [
    'https://images.unsplash.com/photo-1601050690597-df0568f70950?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1606491956689-2ea866880c84?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1585937421612-70a008356fbe?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1630383249896-424e482df921?auto=format&fit=crop&w=900&q=85',
    'https://images.unsplash.com/photo-1565557623262-b51c2513a641?auto=format&fit=crop&w=900&q=85',
  ];

  @override
  Widget build(BuildContext context) {
    return ListView.separated(
      padding: const EdgeInsets.fromLTRB(16, 16, 16, 24),
      itemCount: items.length,
      separatorBuilder: (_, __) => const SizedBox(height: 12),
      itemBuilder: (context, index) {
        final item = items[index];
        final badgeColor =
            item.isCafe ? const Color(0xFFD0BCFF) : const Color(0xFFFFC107);
        final badgeEmoji = item.isCafe ? '☕' : '🌮';
        final badgeLabel = item.isCafe ? 'Cafe' : 'Street';

        return Container(
          padding: const EdgeInsets.all(16),
          decoration: BoxDecoration(
            color: Colors.white.withOpacity(0.06),
            borderRadius: BorderRadius.circular(22),
            border: Border.all(color: Colors.white.withOpacity(0.12)),
            boxShadow: [
              BoxShadow(
                color: Colors.black.withOpacity(0.22),
                blurRadius: 18,
                offset: const Offset(0, 8),
              ),
            ],
          ),
          child: Row(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              ClipRRect(
                borderRadius: BorderRadius.circular(16),
                child: SizedBox(
                  width: 92,
                  height: 104,
                  child: Stack(
                    fit: StackFit.expand,
                    children: [
                      Image.network(
                        (item.isCafe ? _cafePhotos : _streetFoodPhotos)[index % 5],
                        fit: BoxFit.cover,
                        alignment: item.isCafe
                            ? Alignment.topCenter
                            : Alignment.bottomCenter,
                        errorBuilder: (_, __, ___) => _FoodPhotoFallback(
                          index: index,
                          isCafe: item.isCafe,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(width: 12),
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Row(
                      children: [
                        Expanded(
                          child: Text(
                            item.name,
                            style: const TextStyle(
                                fontSize: 17,
                                fontWeight: FontWeight.bold,
                                color: Colors.white),
                          ),
                        ),
                        Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 8, vertical: 4),
                          decoration: BoxDecoration(
                            color: badgeColor.withOpacity(0.18),
                            borderRadius: BorderRadius.circular(999),
                            border:
                                Border.all(color: badgeColor.withOpacity(0.5)),
                          ),
                          child: Text(
                            '$badgeEmoji $badgeLabel',
                            style: TextStyle(
                              fontSize: 10,
                              fontWeight: FontWeight.w700,
                              color: badgeColor,
                              letterSpacing: 0.5,
                            ),
                          ),
                        ),
                      ],
                    ),
                    const SizedBox(height: 4),
                    Text(
                      item.subtitle,
                      style:
                          const TextStyle(fontSize: 13, color: Colors.white70),
                    ),
                    const SizedBox(height: 6),
                    Text(
                      item.detail,
                      style: const TextStyle(
                          fontSize: 12.5, color: Colors.white60, height: 1.4),
                    ),
                  ],
                ),
              ),
            ],
          ),
        );
      },
    );
  }
}

class _FoodPhotoFallback extends StatelessWidget {
  final int index;
  final bool isCafe;

  const _FoodPhotoFallback({required this.index, required this.isCafe});

  @override
  Widget build(BuildContext context) {
    const cafeColors = [
      Color(0xFF51344D),
      Color(0xFF234E52),
      Color(0xFF654321),
      Color(0xFF3D405B),
      Color(0xFF6B3E26),
    ];
    const streetColors = [
      Color(0xFF9A3E22),
      Color(0xFF8A5A16),
      Color(0xFF7D3045),
      Color(0xFF44633F),
      Color(0xFF7A4238),
    ];
    final color = (isCafe ? cafeColors : streetColors)[index % 5];

    return Container(
      decoration: BoxDecoration(
        gradient: LinearGradient(
          colors: [color, color.withOpacity(0.56), const Color(0xFF15171B)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
      ),
      child: Center(
        child: Icon(
          isCafe ? Icons.coffee_rounded : Icons.ramen_dining_rounded,
          color: Colors.white.withOpacity(0.9),
          size: 34,
        ),
      ),
    );
  }
}

class LocalizedCitySelectionScreen extends StatelessWidget {
  const LocalizedCitySelectionScreen({super.key});

  static const _cityAccents = {
    'Delhi': Color(0xFFB8572D),
    'Mumbai': Color(0xFF1E88E5),
    'Agra': Color(0xFF8E5E3B),
    'Bengaluru': Color(0xFF43A047),
    'Varanasi': Color(0xFF6A1B9A),
    'Kolkata': Color(0xFFE53935),
    'Pune': Color(0xFF009688),
    'Jaipur': Color(0xFFFFB300),
  };

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0F1115),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0F1115),
        foregroundColor: Colors.white,
        elevation: 0,
        title: const Text('Heritage Explorer',
            style: TextStyle(fontWeight: FontWeight.w800)),
      ),
      body: ListView.separated(
        padding: const EdgeInsets.fromLTRB(16, 8, 16, 24),
        itemCount: sampleCities.length + 1,
        separatorBuilder: (_, index) => SizedBox(height: index == 0 ? 18 : 12),
        itemBuilder: (context, index) {
          if (index == 0) return const _HeritageExplorerIntro();
          final city = sampleCities[index - 1];
          final accent = _cityAccents[city.name] ?? const Color(0xFF7C4DFF);
          final sites = heritageData[city.name] ?? const <HeritageSite>[];
          final coverImage = sites.isEmpty ? '' : sites.first.imageUrl;

          return InkWell(
            borderRadius: BorderRadius.circular(20),
            onTap: () => Navigator.push(
              context,
              MaterialPageRoute(
                  builder: (_) =>
                      LocalizedHeritageSitesListScreen(cityName: city.name)),
            ),
            child: Ink(
              height: 104,
              decoration: BoxDecoration(
                color: Colors.white.withOpacity(0.05),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: Colors.white.withOpacity(0.10)),
              ),
              child: Row(
                children: [
                  ClipRRect(
                    borderRadius: const BorderRadius.horizontal(
                        left: Radius.circular(20)),
                    child: SizedBox(
                      width: 104,
                      height: double.infinity,
                      child: coverImage.isEmpty
                          ? Container(color: accent.withOpacity(0.36))
                          : Image.asset(
                              coverImage,
                              fit: BoxFit.cover,
                              errorBuilder: (_, __, ___) => Container(
                                color: accent.withOpacity(0.36),
                                child: const Icon(Icons.account_balance_rounded,
                                    color: Colors.white),
                              ),
                            ),
                    ),
                  ),
                  Expanded(
                    child: Padding(
                      padding: const EdgeInsets.fromLTRB(14, 14, 12, 14),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        mainAxisAlignment: MainAxisAlignment.center,
                        children: [
                          Row(
                            children: [
                              Icon(Icons.location_on_rounded,
                                  color: accent, size: 17),
                              const SizedBox(width: 5),
                              Expanded(
                                child: Text(city.name,
                                    style: const TextStyle(
                                        color: Colors.white,
                                        fontSize: 17,
                                        fontWeight: FontWeight.w800)),
                              ),
                              Icon(Icons.arrow_forward_ios_rounded,
                                  size: 15,
                                  color: Colors.white.withOpacity(0.48)),
                            ],
                          ),
                          const SizedBox(height: 8),
                          Text(
                            '${sites.length} handpicked heritage ${sites.length == 1 ? 'site' : 'sites'}',
                            style: const TextStyle(
                                color: Colors.white60, fontSize: 12),
                          ),
                          const SizedBox(height: 8),
                          Text(city.state,
                              style: TextStyle(
                                  color: accent.withOpacity(0.95),
                                  fontSize: 11,
                                  fontWeight: FontWeight.w700)),
                        ],
                      ),
                    ),
                  ),
                ],
              ),
            ),
          );
        },
      ),
    );
  }
}

class _HeritageExplorerIntro extends StatelessWidget {
  const _HeritageExplorerIntro();

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(18),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF3B1F66), Color(0xFF5F2F9E), Color(0xFF291C3B)],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(24),
        border: Border.all(color: Colors.white12),
        boxShadow: [
          BoxShadow(
            color: const Color(0xFF7C4DFF).withOpacity(0.26),
            blurRadius: 24,
            offset: const Offset(0, 12),
          ),
        ],
      ),
      child: Row(
        children: [
          Container(
            width: 54,
            height: 54,
            decoration: BoxDecoration(
              color: Colors.white.withOpacity(0.14),
              borderRadius: BorderRadius.circular(16),
              border: Border.all(color: Colors.white24),
            ),
            child: const Icon(Icons.account_balance_rounded,
                color: Colors.white, size: 27),
          ),
          const SizedBox(width: 14),
          const Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text('Discover India’s living history',
                    style: TextStyle(
                        color: Colors.white,
                        fontWeight: FontWeight.w800,
                        fontSize: 18)),
                SizedBox(height: 5),
                Text(
                    'Choose a city for monument stories, visitor tips, and peak-hour insights.',
                    style: TextStyle(
                        color: Colors.white70, fontSize: 12.5, height: 1.35)),
              ],
            ),
          ),
        ],
      ),
    );
  }
}

// ==========================================
// 6. HOME TAB & UTILITY SCREENS
// ==========================================

class HomeTab extends StatefulWidget {
  const HomeTab({super.key});

  @override
  State<HomeTab> createState() => _HomeTabState();
}

class _HomeTabState extends State<HomeTab> {
  final TextEditingController _searchController = TextEditingController();

  void _handleSearch(String query) {
    final cleanQuery = query.trim().toLowerCase();
    if (cleanQuery.isEmpty) return;

    for (var entry in heritageData.entries) {
      for (var site in entry.value) {
        if (site.name.toLowerCase().contains(cleanQuery) ||
            cleanQuery.contains(site.name.toLowerCase())) {
          Navigator.push(
              context,
              MaterialPageRoute(
                  builder: (_) => FullyLocalizedSiteDetailScreen(site: site)));
          return;
        }
      }
    }

    for (var city in sampleCities) {
      if (city.name.toLowerCase().contains(cleanQuery) ||
          cleanQuery.contains(city.name.toLowerCase())) {
        Navigator.push(
            context,
            MaterialPageRoute(
                builder: (_) =>
                    LocalizedHeritageSitesListScreen(cityName: city.name)));
        return;
      }
    }

    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
          content: Text('No matching attraction or city found for "$query".')),
    );
  }

  @override
  Widget build(BuildContext context) {
    final lang = Provider.of<LanguageProvider>(context);

    return Consumer<UserProvider>(
      builder: (context, user, child) {
        final String displayName =
            user.fullName.trim().isEmpty ? 'Traveller' : user.fullName.trim();

        return Scaffold(
          backgroundColor: const Color(0xFF0F1115),
          body: SafeArea(
            child: SingleChildScrollView(
              padding: const EdgeInsets.fromLTRB(20, 12, 20, 20),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Container(
                    padding: const EdgeInsets.fromLTRB(12, 10, 12, 10),
                    decoration: BoxDecoration(
                      gradient: const LinearGradient(
                        colors: [Color(0xFF171522), Color(0xFF201A2B)],
                        begin: Alignment.centerLeft,
                        end: Alignment.centerRight,
                      ),
                      borderRadius: BorderRadius.circular(20),
                      border: Border.all(color: Colors.white.withOpacity(0.08)),
                    ),
                    child: Row(
                      crossAxisAlignment: CrossAxisAlignment.center,
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        Row(
                          children: [
                            Container(
                              width: 46,
                              height: 46,
                              decoration: BoxDecoration(
                                gradient: const LinearGradient(
                                  colors: [
                                    Color(0xFFD0BCFF),
                                    Color(0xFF7C4DFF)
                                  ],
                                  begin: Alignment.topLeft,
                                  end: Alignment.bottomRight,
                                ),
                                borderRadius: BorderRadius.circular(14),
                                boxShadow: [
                                  BoxShadow(
                                    color: const Color(0xFF7C4DFF)
                                        .withOpacity(0.35),
                                    blurRadius: 20,
                                    offset: const Offset(0, 10),
                                  ),
                                ],
                              ),
                              child: const Icon(Icons.explore_rounded,
                                  color: Colors.white, size: 22),
                            ),
                            const SizedBox(width: 12),
                            Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: const [
                                Text(
                                  'Explore India',
                                  style: TextStyle(
                                      fontSize: 24,
                                      fontWeight: FontWeight.w800,
                                      color: Colors.white),
                                ),
                                Text(
                                  'Monuments & Cultural Wonders',
                                  style: TextStyle(
                                      fontSize: 13, color: Colors.white60),
                                ),
                              ],
                            ),
                          ],
                        ),
                        Container(
                          width: 42,
                          height: 42,
                          decoration: BoxDecoration(
                            color: Colors.white.withOpacity(0.05),
                            borderRadius: BorderRadius.circular(12),
                            border: Border.all(
                                color: Colors.white.withOpacity(0.12)),
                          ),
                          child: const Icon(Icons.notifications_none_rounded,
                              color: Colors.white, size: 20),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 18),
                  TextField(
                    controller: _searchController,
                    onSubmitted: _handleSearch,
                    style: const TextStyle(color: Colors.white),
                    decoration: InputDecoration(
                      hintText: lang.getText('searchHint'),
                      hintStyle: const TextStyle(color: Colors.white38),
                      prefixIcon:
                          const Icon(Icons.search, color: Colors.white70),
                      suffixIcon: IconButton(
                        icon: const Icon(Icons.arrow_forward,
                            color: Color(0xFFD0BCFF)),
                        onPressed: () => _handleSearch(_searchController.text),
                      ),
                      filled: true,
                      fillColor: Colors.white.withOpacity(0.04),
                      border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(16),
                          borderSide: BorderSide(color: Colors.white12)),
                      enabledBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(16),
                          borderSide: BorderSide(color: Colors.white12)),
                      focusedBorder: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(16),
                          borderSide: BorderSide(color: Color(0xFFD0BCFF))),
                    ),
                  ),
                  const SizedBox(height: 18),
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.symmetric(
                        horizontal: 12, vertical: 10),
                    decoration: BoxDecoration(
                      gradient: const LinearGradient(
                        colors: [Color(0xFF1F1B2E), Color(0xFF2A2340)],
                        begin: Alignment.centerLeft,
                        end: Alignment.centerRight,
                      ),
                      borderRadius: BorderRadius.circular(18),
                      border: Border.all(color: Colors.white.withOpacity(0.08)),
                    ),
                    child: Row(
                      children: [
                        Container(
                          width: 34,
                          height: 34,
                          decoration: BoxDecoration(
                            gradient: const LinearGradient(
                              colors: [Color(0xFFD0BCFF), Color(0xFFB9F6CA)],
                              begin: Alignment.topLeft,
                              end: Alignment.bottomRight,
                            ),
                            borderRadius: BorderRadius.circular(10),
                          ),
                          child: const Center(
                              child: Text('✦',
                                  style: TextStyle(
                                      fontSize: 16, color: Color(0xFF1B1B2F)))),
                        ),
                        const SizedBox(width: 10),
                        const Expanded(
                          child: Text(
                            'Curated for you',
                            style: TextStyle(
                                fontSize: 12,
                                fontWeight: FontWeight.w700,
                                color: Colors.white,
                                letterSpacing: 0.7),
                          ),
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 12),
                  SizedBox(
                    height: 132,
                    child: ListView.builder(
                      scrollDirection: Axis.horizontal,
                      itemCount: sampleCities.length,
                      itemBuilder: (context, idx) {
                        final city = sampleCities[idx];
                        final isSel = user.selectedCity == city.name;
                        final cityFoods = cityFoodGuide[city.name] ??
                            const {
                              'cafes': <CityFoodPlace>[],
                              'streetFood': <CityFoodPlace>[]
                            };
                        final cafeCount =
                            (cityFoods['cafes'] ?? const <CityFoodPlace>[])
                                .length;
                        final streetCount =
                            (cityFoods['streetFood'] ?? const <CityFoodPlace>[])
                                .length;
                        final cityAccent = {
                              'Delhi': const Color(0xFFB8572D),
                              'Mumbai': const Color(0xFF1E88E5),
                              'Agra': const Color(0xFF8E5E3B),
                              'Bengaluru': const Color(0xFF43A047),
                              'Varanasi': const Color(0xFF6A1B9A),
                              'Kolkata': const Color(0xFFE53935),
                              'Pune': const Color(0xFF009688),
                              'Jaipur': const Color(0xFFFFB300),
                            }[city.name] ??
                            const Color(0xFF381E72);

                        return GestureDetector(
                          onTap: () {
                            user.setCity(city.name);
                            Navigator.push(
                              context,
                              MaterialPageRoute(
                                builder: (_) =>
                                    CityFoodDetailScreen(cityName: city.name),
                              ),
                            );
                          },
                          child: Container(
                            width: 170,
                            margin: const EdgeInsets.only(right: 12),
                            padding: const EdgeInsets.symmetric(
                                horizontal: 12, vertical: 10),
                            decoration: BoxDecoration(
                              borderRadius: BorderRadius.circular(24),
                              gradient: LinearGradient(
                                begin: Alignment.topLeft,
                                end: Alignment.bottomRight,
                                colors: isSel
                                    ? [
                                        cityAccent.withOpacity(1.0),
                                        cityAccent.withOpacity(0.86),
                                        const Color(0xFF201A2B)
                                            .withOpacity(0.88),
                                      ]
                                    : [
                                        Colors.white.withOpacity(0.06),
                                        Colors.white.withOpacity(0.03)
                                      ],
                              ),
                              border: Border.all(
                                  color: isSel
                                      ? Colors.white.withOpacity(0.18)
                                      : Colors.white12),
                              boxShadow: [
                                BoxShadow(
                                  color: cityAccent
                                      .withOpacity(isSel ? 0.32 : 0.1),
                                  blurRadius: isSel ? 26 : 12,
                                  offset: const Offset(0, 10),
                                ),
                              ],
                            ),
                            child: Stack(
                              children: [
                                Positioned(
                                  right: -20,
                                  top: -20,
                                  child: Container(
                                    width: 86,
                                    height: 86,
                                    decoration: BoxDecoration(
                                      shape: BoxShape.circle,
                                      gradient: RadialGradient(
                                        colors: [
                                          Colors.white.withOpacity(0.24),
                                          cityAccent.withOpacity(0.18),
                                          Colors.transparent,
                                        ],
                                        stops: const [0.0, 0.5, 1.0],
                                      ),
                                    ),
                                  ),
                                ),
                                Column(
                                  crossAxisAlignment: CrossAxisAlignment.start,
                                  mainAxisAlignment:
                                      MainAxisAlignment.spaceBetween,
                                  children: [
                                    Row(
                                      mainAxisAlignment:
                                          MainAxisAlignment.spaceBetween,
                                      children: [
                                        Container(
                                          padding: const EdgeInsets.symmetric(
                                              horizontal: 8, vertical: 6),
                                          decoration: BoxDecoration(
                                            color:
                                                Colors.white.withOpacity(0.12),
                                            borderRadius:
                                                BorderRadius.circular(999),
                                            border: Border.all(
                                                color: Colors.white
                                                    .withOpacity(0.22)),
                                          ),
                                          child: Text(
                                            isSel ? '✨' : '🍽️',
                                            style:
                                                const TextStyle(fontSize: 12),
                                          ),
                                        ),
                                        Row(
                                          children: [
                                            const Text('☕',
                                                style: TextStyle(fontSize: 11)),
                                            const SizedBox(width: 3),
                                            Text('$cafeCount',
                                                style: TextStyle(
                                                    fontSize: 11,
                                                    color: isSel
                                                        ? const Color(
                                                            0xFF381E72)
                                                        : Colors.white70)),
                                            const SizedBox(width: 8),
                                            const Text('🌮',
                                                style: TextStyle(fontSize: 11)),
                                            const SizedBox(width: 3),
                                            Text('$streetCount',
                                                style: TextStyle(
                                                    fontSize: 11,
                                                    color: isSel
                                                        ? const Color(
                                                            0xFF381E72)
                                                        : Colors.white70)),
                                          ],
                                        ),
                                      ],
                                    ),
                                    const SizedBox(height: 12),
                                    Text(city.name,
                                        style: TextStyle(
                                            fontWeight: FontWeight.bold,
                                            color: isSel
                                                ? const Color(0xFF381E72)
                                                : Colors.white,
                                            fontSize: 16)),
                                    Text(city.state,
                                        style: TextStyle(
                                            fontSize: 11,
                                            color: isSel
                                                ? const Color(0xFF381E72)
                                                    .withOpacity(0.82)
                                                : Colors.white60)),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        );
                      },
                    ),
                  ),
                  const SizedBox(height: 18),
                  Container(
                    width: double.infinity,
                    padding: const EdgeInsets.all(16),
                    decoration: BoxDecoration(
                      gradient: const LinearGradient(
                        colors: [
                          Color(0xFF3B1F66),
                          Color(0xFF5F2F9E),
                          Color(0xFF291C3B)
                        ],
                        begin: Alignment.topLeft,
                        end: Alignment.bottomRight,
                      ),
                      borderRadius: BorderRadius.circular(22),
                      border: Border.all(color: Colors.white.withOpacity(0.12)),
                      boxShadow: [
                        BoxShadow(
                          color: const Color(0xFF7C4DFF).withOpacity(0.28),
                          blurRadius: 26,
                          offset: const Offset(0, 14),
                        ),
                      ],
                    ),
                    child: Stack(
                      children: [
                        Positioned(
                          right: -24,
                          top: -26,
                          child: Container(
                            width: 120,
                            height: 120,
                            decoration: BoxDecoration(
                              shape: BoxShape.circle,
                              gradient: RadialGradient(
                                colors: [
                                  Colors.white.withOpacity(0.24),
                                  Colors.white.withOpacity(0.08),
                                  Colors.transparent,
                                ],
                                stops: const [0.0, 0.45, 1.0],
                              ),
                            ),
                          ),
                        ),
                        Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Container(
                              padding: const EdgeInsets.symmetric(
                                  horizontal: 8, vertical: 5),
                              decoration: BoxDecoration(
                                color: Colors.white.withOpacity(0.12),
                                borderRadius: BorderRadius.circular(999),
                                border: Border.all(
                                    color: Colors.white.withOpacity(0.18)),
                              ),
                              child: const Text(
                                'Curated for you',
                                style: TextStyle(
                                    fontSize: 10,
                                    fontWeight: FontWeight.w700,
                                    color: Colors.white,
                                    letterSpacing: 0.8),
                              ),
                            ),
                            const SizedBox(height: 12),
                            const Text(
                              'Explore India',
                              style: TextStyle(
                                fontSize: 20,
                                fontWeight: FontWeight.bold,
                                color: Colors.white,
                              ),
                            ),
                            const SizedBox(height: 6),
                            const Text(
                              'Explore heritage cities, monument guides, and local travel insights from one place.',
                              style: TextStyle(
                                  fontSize: 13,
                                  color: Colors.white70,
                                  height: 1.4),
                            ),
                            const SizedBox(height: 14),
                            SizedBox(
                              width: double.infinity,
                              child: ElevatedButton.icon(
                                onPressed: () {
                                  Navigator.push(
                                    context,
                                    MaterialPageRoute(
                                        builder: (_) =>
                                            const LocalizedCitySelectionScreen()),
                                  );
                                },
                                style: ElevatedButton.styleFrom(
                                  backgroundColor: Colors.white,
                                  foregroundColor: const Color(0xFF5E2B97),
                                  minimumSize: const Size.fromHeight(46),
                                  shape: RoundedRectangleBorder(
                                      borderRadius: BorderRadius.circular(12)),
                                ),
                                icon: const Icon(Icons.explore_rounded),
                                label: const Text('Open heritage explorer'),
                              ),
                            ),
                          ],
                        ),
                      ],
                    ),
                  ),
                  const SizedBox(height: 24),
                  Text(lang.getText('quickTools'),
                      style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                          color: Colors.white)),
                  const SizedBox(height: 12),
                  Row(
                    children: [
                      Expanded(
                        child: _buildToolCard(
                          context,
                          title: lang.getText('translator'),
                          icon: Icons.translate,
                          color: Colors.deepOrangeAccent,
                          onTap: () => Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (_) => const TranslatorScreen())),
                        ),
                      ),
                      const SizedBox(width: 12),
                      Expanded(
                        child: _buildToolCard(
                          context,
                          title: lang.getText('currency'),
                          icon: Icons.currency_exchange,
                          color: Colors.teal,
                          onTap: () => Navigator.push(
                              context,
                              MaterialPageRoute(
                                  builder: (_) => const CurrencyTab())),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
          ),
        );
      },
    );
  }

  Widget _buildToolCard(BuildContext context,
      {required String title,
      required IconData icon,
      required Color color,
      required VoidCallback onTap}) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.all(16),
        decoration: BoxDecoration(
          gradient: LinearGradient(
            colors: [
              color.withOpacity(0.22),
              color.withOpacity(0.10),
              Colors.white.withOpacity(0.04),
            ],
            begin: Alignment.topLeft,
            end: Alignment.bottomRight,
          ),
          borderRadius: BorderRadius.circular(18),
          border: Border.all(color: color.withOpacity(0.28)),
          boxShadow: [
            BoxShadow(
              color: color.withOpacity(0.14),
              blurRadius: 14,
              offset: const Offset(0, 8),
            ),
          ],
        ),
        child: Row(
          children: [
            Container(
              width: 42,
              height: 42,
              decoration: BoxDecoration(
                color: color,
                borderRadius: BorderRadius.circular(12),
                boxShadow: [
                  BoxShadow(
                    color: color.withOpacity(0.35),
                    blurRadius: 14,
                    offset: const Offset(0, 8),
                  ),
                ],
              ),
              child: Icon(icon, color: Colors.white, size: 20),
            ),
            const SizedBox(width: 12),
            Expanded(
                child: Text(title,
                    style: TextStyle(
                        fontWeight: FontWeight.bold,
                        color: Colors.white,
                        fontSize: 14),
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis)),
          ],
        ),
      ),
    );
  }
}

// LOCALIZED HERITAGE LIST SCREEN THAT MANDATES LOCALIZED DETAIL SCREEN ON TAP
class LocalizedHeritageSitesListScreen extends StatelessWidget {
  final String cityName;
  const LocalizedHeritageSitesListScreen({super.key, required this.cityName});

  @override
  Widget build(BuildContext context) {
    final sites = heritageData[cityName] ?? [];

    return Scaffold(
      backgroundColor: const Color(0xFF0F1115),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0F1115),
        foregroundColor: Colors.white,
        elevation: 0,
        title:
            Text(cityName, style: const TextStyle(fontWeight: FontWeight.w800)),
      ),
      body: ListView.separated(
        padding: const EdgeInsets.fromLTRB(16, 16, 16, 20),
        itemCount: sites.length,
        separatorBuilder: (_, __) => const SizedBox(height: 12),
        itemBuilder: (context, idx) {
          final site = sites[idx];
          final tagColor = const {
                'Architecture': Color(0xFFB8572D),
                'Religious': Color(0xFF8E5E3B),
                'Monument': Color(0xFF6A1B9A),
                'Historical': Color(0xFF1E88E5),
              }[site.tag] ??
              const Color(0xFF7C4DFF);

          return GestureDetector(
            onTap: () {
              Navigator.push(
                context,
                MaterialPageRoute(
                    builder: (_) => FullyLocalizedSiteDetailScreen(site: site)),
              );
            },
            child: Container(
              padding: const EdgeInsets.all(14),
              decoration: BoxDecoration(
                gradient: LinearGradient(
                  colors: [
                    tagColor.withOpacity(0.15),
                    tagColor.withOpacity(0.05),
                    Colors.white.withOpacity(0.03),
                  ],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.circular(16),
                border: Border.all(color: tagColor.withOpacity(0.25)),
                boxShadow: [
                  BoxShadow(
                    color: tagColor.withOpacity(0.14),
                    blurRadius: 14,
                    offset: const Offset(0, 6),
                  ),
                ],
              ),
              child: Row(
                children: [
                  Container(
                    width: 50,
                    height: 50,
                    decoration: BoxDecoration(
                      color: tagColor,
                      borderRadius: BorderRadius.circular(12),
                      boxShadow: [
                        BoxShadow(
                          color: tagColor.withOpacity(0.35),
                          blurRadius: 12,
                          offset: const Offset(0, 6),
                        ),
                      ],
                    ),
                    child: Icon(
                      Icons.place_outlined,
                      color: Colors.white,
                      size: 22,
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          site.name,
                          style: const TextStyle(
                              fontWeight: FontWeight.bold,
                              fontSize: 15,
                              color: Colors.white),
                        ),
                        const SizedBox(height: 4),
                        Container(
                          padding: const EdgeInsets.symmetric(
                              horizontal: 6, vertical: 2),
                          decoration: BoxDecoration(
                            color: tagColor.withOpacity(0.22),
                            borderRadius: BorderRadius.circular(4),
                            border:
                                Border.all(color: tagColor.withOpacity(0.4)),
                          ),
                          child: Text(
                            site.tag,
                            style: TextStyle(
                                fontSize: 11,
                                fontWeight: FontWeight.w600,
                                color: tagColor),
                          ),
                        ),
                      ],
                    ),
                  ),
                  Icon(Icons.arrow_forward_ios,
                      size: 16, color: Colors.white.withOpacity(0.5)),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  // removed icon helper
}

class FullyLocalizedSiteDetailScreen extends StatelessWidget {
  final HeritageSite site;
  const FullyLocalizedSiteDetailScreen({super.key, required this.site});

  Future<void> _openBookingPortal(BuildContext context) async {
    final portal = Uri.tryParse(site.bookingUrl);
    if (portal == null || !portal.hasScheme) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('This ticket portal link is unavailable.')),
      );
      return;
    }

    try {
      final opened = await launchUrl(
        portal,
        mode: LaunchMode.externalApplication,
      );
      if (!opened && context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
              content: Text('Could not open the ticket portal. Please try again.')),
        );
      }
    } catch (_) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
              content: Text('Could not open the ticket portal. Please try again.')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    final lang = Provider.of<LanguageProvider>(context);
    const hoursLabels = [
      '9a',
      '10a',
      '11a',
      '12p',
      '1p',
      '2p',
      '3p',
      '4p',
      '5p'
    ];

    final localizedHistory = HeritageTranslationEngine.getField(
        site.name, 'history', lang.currentLanguage, site.history);
    final localizedPeak = HeritageTranslationEngine.getField(
        site.name, 'peak', lang.currentLanguage, site.peakHours);
    final localizedRules = HeritageTranslationEngine.getField(
        site.name, 'rules', lang.currentLanguage, site.entryRules);

    return Scaffold(
      backgroundColor: const Color(0xFF0F1115),
      appBar: AppBar(
        title: Text(site.name),
        backgroundColor: const Color(0xFF0F1115),
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            ClipRRect(
              borderRadius: BorderRadius.circular(22),
              child: SizedBox(
                height: 220,
                width: double.infinity,
                child: Image.asset(site.imageUrl, fit: BoxFit.cover),
              ),
            ),
            const SizedBox(height: 16),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
              decoration: BoxDecoration(
                  color: const Color(0xFF7C4DFF).withOpacity(0.18),
                  borderRadius: BorderRadius.circular(999),
                  border: Border.all(
                      color: const Color(0xFFB39DDB).withOpacity(0.45))),
              child: const Text('HERITAGE HIGHLIGHT',
                  style: TextStyle(
                      color: Color(0xFFD0BCFF),
                      fontWeight: FontWeight.bold,
                      fontSize: 11,
                      letterSpacing: 0.8)),
            ),
            const SizedBox(height: 8),
            Text(site.name,
                style: const TextStyle(
                    fontSize: 27,
                    fontWeight: FontWeight.w800,
                    color: Colors.white)),
            Text('${site.city}, India',
                style: const TextStyle(color: Colors.white60, fontSize: 14)),
            Divider(height: 28, color: Colors.white.withOpacity(0.12)),
            Text(lang.getText('historyTitle'),
                style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: Colors.white)),
            const SizedBox(height: 8),
            Text(localizedHistory,
                style: const TextStyle(
                    fontSize: 14.5, height: 1.5, color: Colors.white70)),
            Divider(height: 32, color: Colors.white.withOpacity(0.12)),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(lang.getText('peakRushTitle'),
                    style: const TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                        color: Colors.white)),
                Container(
                  padding:
                      const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                      color: Colors.red.shade50,
                      borderRadius: BorderRadius.circular(4)),
                  child: const Text('High traffic slots',
                      style: TextStyle(fontSize: 11, color: Colors.red)),
                )
              ],
            ),
            const SizedBox(height: 4),
            Text('${lang.getText('peakWindow')}: $localizedPeak',
                style: const TextStyle(color: Colors.white60, fontSize: 13)),
            const SizedBox(height: 16),
            Container(
              height: 140,
              padding: const EdgeInsets.only(
                  top: 18, bottom: 8, left: 12, right: 12),
              decoration: BoxDecoration(
                color: const Color(0xFF171B1F),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.white.withOpacity(0.12)),
              ),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                crossAxisAlignment: CrossAxisAlignment.end,
                children: List.generate(site.rushLevels.length, (index) {
                  final rush = site.rushLevels[index];
                  final isPeak = rush >= 0.75;

                  return Column(
                    mainAxisAlignment: MainAxisAlignment.end,
                    children: [
                      Container(
                        width: 18,
                        height: 75 * rush,
                        decoration: BoxDecoration(
                          color: isPeak
                              ? const Color(0xFFE53935)
                              : const Color(0xFFFFB74D),
                          borderRadius: BorderRadius.circular(4),
                        ),
                      ),
                      const SizedBox(height: 6),
                      Text(hoursLabels[index],
                          style: const TextStyle(
                              fontSize: 11,
                              fontWeight: FontWeight.w500,
                              color: Colors.white60)),
                    ],
                  );
                }),
              ),
            ),
            Divider(height: 32, color: Colors.white.withOpacity(0.12)),
            Text(lang.getText('entryRulesTitle'),
                style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                    color: Colors.white)),
            const SizedBox(height: 12),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: const Color(0xFF2E2513),
                borderRadius: BorderRadius.circular(8),
                border: Border.all(color: Colors.amber.shade300),
              ),
              child: Column(
                children: [
                  Row(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      const Icon(Icons.gavel, color: Colors.orange, size: 20),
                      const SizedBox(width: 8),
                      Expanded(
                          child: Text(localizedRules,
                              style: const TextStyle(
                                  fontSize: 13.5,
                                  height: 1.3,
                                  color: Colors.white70))),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      const Icon(Icons.confirmation_num_outlined,
                          color: Colors.orange, size: 20),
                      const SizedBox(width: 8),
                      Expanded(
                          child: Text('Estimated Fee: ${site.ticketPrice}',
                              style: const TextStyle(
                                  fontSize: 13.5,
                                  fontWeight: FontWeight.w600,
                                  color: Colors.white))),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 32),
            SizedBox(
              width: double.infinity,
              height: 52,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFF5E2B97),
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(10)),
                ),
                onPressed: () => _openBookingPortal(context),
                icon: const Icon(Icons.open_in_browser),
                label: Text(lang.getText('bookTickets'),
                    style: const TextStyle(
                        fontSize: 15, fontWeight: FontWeight.bold)),
              ),
            ),
            const SizedBox(height: 16),
          ],
        ),
      ),
    );
  }
}

class CurrencyTab extends StatefulWidget {
  const CurrencyTab({super.key});

  @override
  State<CurrencyTab> createState() => _CurrencyTabState();
}

class _CurrencyTabState extends State<CurrencyTab> {
  final TextEditingController _amountController =
      TextEditingController(text: '1');
  String _fromCurrency = 'INR';
  String _toCurrency = 'USD';
  double _convertedValue = 0.01;

  final Map<String, double> _ratesInINR = {
    'INR': 1.00,
    'USD': 94.92,
    'EUR': 92.40,
    'GBP': 108.50,
    'JPY': 0.58,
    'CAD': 62.80,
    'AUD': 55.60,
  };

  static const Map<String, String> _currencyNames = {
    'INR': 'Indian Rupee',
    'USD': 'US Dollar',
    'EUR': 'Euro',
    'GBP': 'British Pound',
    'JPY': 'Japanese Yen',
    'CAD': 'Canadian Dollar',
    'AUD': 'Australian Dollar',
  };

  @override
  void initState() {
    super.initState();
    _calculateConversion();
  }

  void _calculateConversion() {
    final double inputAmount = double.tryParse(_amountController.text) ?? 0.0;
    final double fromRateInINR = _ratesInINR[_fromCurrency] ?? 1.0;
    final double toRateInINR = _ratesInINR[_toCurrency] ?? 1.0;

    // Map stores 1 unit of each currency in INR.
    // Example: 1 USD = 86.75 INR, so 1 INR = 1 / 86.75 USD.
    final double result = inputAmount * fromRateInINR / toRateInINR;

    setState(() {
      _convertedValue = result;
    });
  }

  void _swapCurrencies() {
    setState(() {
      final temp = _fromCurrency;
      _fromCurrency = _toCurrency;
      _toCurrency = temp;
      _calculateConversion();
    });
  }

  @override
  Widget build(BuildContext context) {
    final lang = Provider.of<LanguageProvider>(context);

    return Scaffold(
      backgroundColor: const Color(0xFF0F1115),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0F1115),
        foregroundColor: Colors.white,
        elevation: 0,
        title: Row(
          children: [
            Container(
              width: 38,
              height: 38,
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [Color(0xFF009688), Color(0xFF00897B)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.circular(10),
                boxShadow: [
                  BoxShadow(
                    color: const Color(0xFF009688).withOpacity(0.35),
                    blurRadius: 16,
                    offset: const Offset(0, 8),
                  ),
                ],
              ),
              child: const Icon(Icons.currency_exchange,
                  color: Colors.white, size: 18),
            ),
            const SizedBox(width: 10),
            Text(lang.getText('currency')),
          ],
        ),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(20),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [
                    Color(0xFF004D40),
                    Color(0xFF00695C),
                    Color(0xFF00574B)
                  ],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.circular(20),
                border: Border.all(color: Colors.white.withOpacity(0.12)),
                boxShadow: [
                  BoxShadow(
                    color: const Color(0xFF009688).withOpacity(0.28),
                    blurRadius: 24,
                    offset: const Offset(0, 12),
                  ),
                ],
              ),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('Multi-Currency Converter',
                      style: TextStyle(
                          fontSize: 22,
                          fontWeight: FontWeight.w800,
                          color: Colors.white)),
                  const SizedBox(height: 6),
                  Text(
                      'Select source & target currencies to calculate live conversion rates.',
                      style: TextStyle(
                          color: Colors.white.withOpacity(0.75), fontSize: 13)),
                ],
              ),
            ),
            const SizedBox(height: 24),
            Row(
              children: [
                Expanded(
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(14),
                      border: Border.all(color: Colors.white.withOpacity(0.15)),
                    ),
                    child: DropdownButtonFormField<String>(
                      value: _fromCurrency,
                      isExpanded: true,
                      dropdownColor: const Color(0xFF20272B),
                      iconEnabledColor: const Color(0xFF80CBC4),
                      style: const TextStyle(color: Colors.white),
                      decoration: InputDecoration(
                        labelText: lang.getText('convertFrom'),
                        labelStyle:
                            TextStyle(color: Colors.white.withOpacity(0.7)),
                        border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(14),
                            borderSide: BorderSide(
                                color: Colors.white.withOpacity(0.15))),
                        enabledBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(14),
                            borderSide: BorderSide(
                                color: Colors.white.withOpacity(0.15))),
                        focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(14),
                            borderSide:
                                const BorderSide(color: Color(0xFF00BCD4))),
                        filled: true,
                        fillColor: Colors.white.withOpacity(0.05),
                      ),
                      items: _ratesInINR.keys
                          .map((curr) => DropdownMenuItem(
                                value: curr,
                                child: Text(
                                  '$curr  •  ${_currencyNames[curr]}',
                                  overflow: TextOverflow.ellipsis,
                                  style: const TextStyle(
                                      color: Colors.white,
                                      fontWeight: FontWeight.w700),
                                ),
                              ))
                          .toList(),
                      onChanged: (val) {
                        if (val != null) {
                          setState(() => _fromCurrency = val);
                          _calculateConversion();
                        }
                      },
                    ),
                  ),
                ),
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 8),
                  child: Container(
                    width: 42,
                    height: 42,
                    decoration: BoxDecoration(
                      color: const Color(0xFF009688),
                      borderRadius: BorderRadius.circular(12),
                      boxShadow: [
                        BoxShadow(
                          color: const Color(0xFF009688).withOpacity(0.35),
                          blurRadius: 12,
                          offset: const Offset(0, 6),
                        ),
                      ],
                    ),
                    child: IconButton(
                      icon: const Icon(Icons.swap_horiz,
                          size: 22, color: Colors.white),
                      onPressed: _swapCurrencies,
                    ),
                  ),
                ),
                Expanded(
                  child: Container(
                    decoration: BoxDecoration(
                      borderRadius: BorderRadius.circular(14),
                      border: Border.all(color: Colors.white.withOpacity(0.15)),
                    ),
                    child: DropdownButtonFormField<String>(
                      value: _toCurrency,
                      isExpanded: true,
                      dropdownColor: const Color(0xFF20272B),
                      iconEnabledColor: const Color(0xFF80CBC4),
                      style: const TextStyle(color: Colors.white),
                      decoration: InputDecoration(
                        labelText: lang.getText('convertTo'),
                        labelStyle:
                            TextStyle(color: Colors.white.withOpacity(0.7)),
                        border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(14),
                            borderSide: BorderSide(
                                color: Colors.white.withOpacity(0.15))),
                        enabledBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(14),
                            borderSide: BorderSide(
                                color: Colors.white.withOpacity(0.15))),
                        focusedBorder: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(14),
                            borderSide:
                                const BorderSide(color: Color(0xFF00BCD4))),
                        filled: true,
                        fillColor: Colors.white.withOpacity(0.05),
                      ),
                      items: _ratesInINR.keys
                          .map((curr) => DropdownMenuItem(
                                value: curr,
                                child: Text(
                                  '$curr  •  ${_currencyNames[curr]}',
                                  overflow: TextOverflow.ellipsis,
                                  style: const TextStyle(
                                      color: Colors.white,
                                      fontWeight: FontWeight.w700),
                                ),
                              ))
                          .toList(),
                      onChanged: (val) {
                        if (val != null) {
                          setState(() => _toCurrency = val);
                          _calculateConversion();
                        }
                      },
                    ),
                  ),
                ),
              ],
            ),
            const SizedBox(height: 20),
            Container(
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(14),
                border: Border.all(color: Colors.white.withOpacity(0.15)),
              ),
              child: TextField(
                controller: _amountController,
                keyboardType:
                    const TextInputType.numberWithOptions(decimal: true),
                inputFormatters: [
                  FilteringTextInputFormatter.allow(RegExp(r'^\d*\.?\d*'))
                ],
                onChanged: (_) => _calculateConversion(),
                style: const TextStyle(color: Colors.white),
                decoration: InputDecoration(
                  labelText: '${lang.getText('amount')} ($_fromCurrency)',
                  labelStyle: TextStyle(color: Colors.white.withOpacity(0.7)),
                  prefixIcon:
                      const Icon(Icons.attach_money, color: Color(0xFF00BCD4)),
                  border: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(14),
                      borderSide:
                          BorderSide(color: Colors.white.withOpacity(0.15))),
                  enabledBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(14),
                      borderSide:
                          BorderSide(color: Colors.white.withOpacity(0.15))),
                  focusedBorder: OutlineInputBorder(
                      borderRadius: BorderRadius.circular(14),
                      borderSide: const BorderSide(color: Color(0xFF00BCD4))),
                  filled: true,
                  fillColor: Colors.white.withOpacity(0.05),
                ),
              ),
            ),
            const SizedBox(height: 30),
            Container(
              width: double.infinity,
              padding: const EdgeInsets.all(20),
              decoration: BoxDecoration(
                gradient: const LinearGradient(
                  colors: [Color(0xFF004D40), Color(0xFF00695C)],
                  begin: Alignment.topLeft,
                  end: Alignment.bottomRight,
                ),
                borderRadius: BorderRadius.circular(18),
                border: Border.all(color: Colors.white.withOpacity(0.15)),
                boxShadow: [
                  BoxShadow(
                    color: const Color(0xFF009688).withOpacity(0.35),
                    blurRadius: 20,
                    offset: const Offset(0, 10),
                  ),
                ],
              ),
              child: Column(
                children: [
                  Text(
                    '${lang.getText('result')} ($_toCurrency)',
                    style: TextStyle(
                        color: Colors.white.withOpacity(0.85),
                        fontWeight: FontWeight.w600,
                        fontSize: 13),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '$_toCurrency ${_convertedValue.toStringAsFixed(2)}',
                    style: const TextStyle(
                        fontSize: 32,
                        fontWeight: FontWeight.w800,
                        color: Color(0xFFB2DFDB)),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class ProfileTab extends StatelessWidget {
  const ProfileTab({super.key});

  @override
  Widget build(BuildContext context) {
    final lang = Provider.of<LanguageProvider>(context);

    return Consumer<UserProvider>(
      builder: (context, user, child) {
        final String nameVal = user.fullName.trim();
        final String phoneVal = user.phoneNumber.trim();

        final String displayName = nameVal;
        final String displayPhone =
            phoneVal.isNotEmpty ? '${user.countryCode} $phoneVal' : '';

        return Scaffold(
          backgroundColor: const Color(0xFF0F1115),
          appBar: AppBar(
            backgroundColor: const Color(0xFF0F1115),
            foregroundColor: Colors.white,
            elevation: 0,
            title: Row(
              children: [
                Container(
                  width: 38,
                  height: 38,
                  decoration: BoxDecoration(
                    gradient: const LinearGradient(
                      colors: [Color(0xFFD0BCFF), Color(0xFF7C4DFF)],
                      begin: Alignment.topLeft,
                      end: Alignment.bottomRight,
                    ),
                    borderRadius: BorderRadius.circular(10),
                    boxShadow: [
                      BoxShadow(
                        color: const Color(0xFF7C4DFF).withOpacity(0.35),
                        blurRadius: 16,
                        offset: const Offset(0, 8),
                      ),
                    ],
                  ),
                  child:
                      const Icon(Icons.person, color: Colors.white, size: 18),
                ),
                const SizedBox(width: 10),
                Text(lang.getText('profile')),
              ],
            ),
          ),
          body: ListView(
            padding: const EdgeInsets.fromLTRB(20, 20, 20, 24),
            children: [
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  gradient: const LinearGradient(
                    colors: [
                      Color(0xFF2A1D42),
                      Color(0xFF381E72),
                      Color(0xFF291C3A)
                    ],
                    begin: Alignment.topLeft,
                    end: Alignment.bottomRight,
                  ),
                  borderRadius: BorderRadius.circular(18),
                  border: Border.all(color: Colors.white.withOpacity(0.12)),
                  boxShadow: [
                    BoxShadow(
                      color: const Color(0xFF7C4DFF).withOpacity(0.24),
                      blurRadius: 20,
                      offset: const Offset(0, 10),
                    ),
                  ],
                ),
                child: Stack(
                  children: [
                    Positioned(
                      right: -16,
                      top: -16,
                      child: Container(
                        width: 90,
                        height: 90,
                        decoration: BoxDecoration(
                          shape: BoxShape.circle,
                          gradient: RadialGradient(
                            colors: [
                              Colors.white.withOpacity(0.22),
                              Colors.white.withOpacity(0.06),
                              Colors.transparent,
                            ],
                            stops: const [0.0, 0.5, 1.0],
                          ),
                        ),
                      ),
                    ),
                    Row(
                      children: [
                        Container(
                          width: 56,
                          height: 56,
                          decoration: BoxDecoration(
                            gradient: const LinearGradient(
                              colors: [Color(0xFFD0BCFF), Color(0xFF9575CD)],
                              begin: Alignment.topLeft,
                              end: Alignment.bottomRight,
                            ),
                            borderRadius: BorderRadius.circular(14),
                            boxShadow: [
                              BoxShadow(
                                color:
                                    const Color(0xFFD0BCFF).withOpacity(0.35),
                                blurRadius: 14,
                                offset: const Offset(0, 8),
                              ),
                            ],
                          ),
                          child: Center(
                            child: Text(
                              displayName.isNotEmpty
                                  ? displayName[0].toUpperCase()
                                  : 'T',
                              style: const TextStyle(
                                  color: Color(0xFF381E72),
                                  fontSize: 24,
                                  fontWeight: FontWeight.bold),
                            ),
                          ),
                        ),
                        const SizedBox(width: 16),
                        Expanded(
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Text(displayName,
                                  style: const TextStyle(
                                      fontSize: 20,
                                      fontWeight: FontWeight.w800,
                                      color: Colors.white)),
                              const SizedBox(height: 4),
                              Text(displayPhone,
                                  style: TextStyle(
                                      fontSize: 14,
                                      color: Colors.white.withOpacity(0.65))),
                            ],
                          ),
                        ),
                      ],
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 20),
              Container(
                padding:
                    const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
                decoration: BoxDecoration(
                  color: Colors.white.withOpacity(0.05),
                  borderRadius: BorderRadius.circular(14),
                  border: Border.all(color: Colors.white.withOpacity(0.1)),
                ),
                child: Row(
                  children: [
                    Container(
                      width: 40,
                      height: 40,
                      decoration: BoxDecoration(
                        gradient: const LinearGradient(
                          colors: [Color(0xFF7C4DFF), Color(0xFFD0BCFF)],
                          begin: Alignment.topLeft,
                          end: Alignment.bottomRight,
                        ),
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: const Icon(Icons.language,
                          color: Colors.white, size: 18),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Text(
                        lang.getText('preferredLanguageLabel'),
                        style: const TextStyle(
                            fontSize: 15,
                            fontWeight: FontWeight.w600,
                            color: Colors.white),
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 10, vertical: 6),
                      decoration: BoxDecoration(
                        color: const Color(0xFF7C4DFF),
                        borderRadius: BorderRadius.circular(8),
                        boxShadow: [
                          BoxShadow(
                            color: const Color(0xFF7C4DFF).withOpacity(0.3),
                            blurRadius: 10,
                            offset: const Offset(0, 4),
                          ),
                        ],
                      ),
                      child: Text(lang.currentLanguage,
                          style: const TextStyle(
                              fontWeight: FontWeight.bold,
                              color: Colors.white,
                              fontSize: 13)),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 24),
              Container(
                padding:
                    const EdgeInsets.symmetric(horizontal: 16, vertical: 14),
                decoration: BoxDecoration(
                  color: Colors.red.withOpacity(0.08),
                  borderRadius: BorderRadius.circular(14),
                  border: Border.all(color: Colors.red.withOpacity(0.15)),
                ),
                child: GestureDetector(
                  onTap: () {
                    user.logout();
                    Navigator.of(context).pushAndRemoveUntil(
                      MaterialPageRoute(builder: (_) => const LoginScreen()),
                      (route) => false,
                    );
                  },
                  child: Row(
                    children: [
                      Container(
                        width: 40,
                        height: 40,
                        decoration: BoxDecoration(
                          color: Colors.red,
                          borderRadius: BorderRadius.circular(10),
                          boxShadow: [
                            BoxShadow(
                              color: Colors.red.withOpacity(0.3),
                              blurRadius: 10,
                              offset: const Offset(0, 4),
                            ),
                          ],
                        ),
                        child: const Icon(Icons.logout,
                            color: Colors.white, size: 18),
                      ),
                      const SizedBox(width: 12),
                      Text(
                        lang.getText('logout'),
                        style: const TextStyle(
                            color: Colors.red,
                            fontWeight: FontWeight.w800,
                            fontSize: 15),
                      ),
                    ],
                  ),
                ),
              ),
            ],
          ),
        );
      },
    );
  }
}
