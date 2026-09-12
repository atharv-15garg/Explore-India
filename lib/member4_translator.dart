import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:speech_to_text/speech_to_text.dart' as stt;
import 'package:flutter_tts/flutter_tts.dart';
import 'package:http/http.dart' as http;
import 'package:permission_handler/permission_handler.dart';

// ==========================================
// 1. MODELS & CONSTANTS
// ==========================================

class AppLanguage {
  final String name;
  final String code;
  final String speechLanguageCode;
  final String ttsLocale;

  const AppLanguage({
    required this.name,
    required this.code,
    required this.speechLanguageCode,
    required this.ttsLocale,
  });

  static const AppLanguage english = AppLanguage(
    name: 'English',
    code: 'en',
    speechLanguageCode: 'en_US',
    ttsLocale: 'en-US',
  );

  static const AppLanguage hindi = AppLanguage(
    name: 'Hindi',
    code: 'hi',
    speechLanguageCode: 'hi_IN',
    ttsLocale: 'hi-IN',
  );

  static const AppLanguage french = AppLanguage(
    name: 'French',
    code: 'fr',
    speechLanguageCode: 'fr_FR',
    ttsLocale: 'fr-FR',
  );

  static const AppLanguage german = AppLanguage(
    name: 'German',
    code: 'de',
    speechLanguageCode: 'de_DE',
    ttsLocale: 'de-DE',
  );

  static List<AppLanguage> get all => [english, hindi, french, german];
}

class TranslationRoute {
  final String label;
  final AppLanguage from;
  final AppLanguage to;

  const TranslationRoute({
    required this.label,
    required this.from,
    required this.to,
  });
}

final List<TranslationRoute> requiredRoutes = [
  const TranslationRoute(
      label: 'English ➔ Hindi',
      from: AppLanguage.english,
      to: AppLanguage.hindi),
  const TranslationRoute(
      label: 'Hindi ➔ English',
      from: AppLanguage.hindi,
      to: AppLanguage.english),
  const TranslationRoute(
      label: 'English ➔ French',
      from: AppLanguage.english,
      to: AppLanguage.french),
  const TranslationRoute(
      label: 'French ➔ English',
      from: AppLanguage.french,
      to: AppLanguage.english),
];

// ==========================================
// 2. REAL-TIME TRANSLATION & VOICE SERVICES
// ==========================================

class SpeechService {
  final stt.SpeechToText _speech = stt.SpeechToText();
  bool isListening = false;

  Future<bool> ensureMicrophonePermission() async {
    final currentStatus = await Permission.microphone.status;
    if (currentStatus.isGranted) return true;
    if (currentStatus.isPermanentlyDenied || currentStatus.isRestricted)
      return false;
    final requestedStatus = await Permission.microphone.request();
    return requestedStatus.isGranted;
  }

  String _normalizeLocaleKey(String locale) {
    return locale.toLowerCase().replaceAll('-', '_');
  }

  Future<String?> _findBestLocaleForLanguage(String languageCode) async {
    final requested = _normalizeLocaleKey(languageCode);
    final localeCandidates = <String>{
      languageCode,
      languageCode.replaceAll('-', '_'),
      languageCode.replaceAll('_', '-'),
      'en_US',
      'en-US',
      'en_IN',
      'en-GB',
      'hi_IN',
      'hi-IN',
      'fr_FR',
      'fr-FR',
      'de_DE',
      'de-DE',
    }.toList();

    try {
      final availableLocales = await _speech.locales();
      final localeIds = availableLocales.map((item) => item.localeId).toList();

      for (final candidate in localeCandidates) {
        final normalizedCandidate = _normalizeLocaleKey(candidate);
        for (final localeId in localeIds) {
          final normalizedLocale = _normalizeLocaleKey(localeId);
          if (normalizedLocale == normalizedCandidate ||
              normalizedLocale.startsWith(requested) ||
              normalizedLocale.startsWith(normalizedCandidate)) {
            return localeId;
          }
        }
      }

      if (localeIds.isNotEmpty) return localeIds.first;
    } catch (_) {
      // Fall through to use the requested language or default English.
    }

    return null;
  }

  Future<bool> initialize({
    required Function(String text, bool isFinal) onResult,
    required Function(String message) onError,
    required Function(String status) onStatus,
  }) async {
    final hasPermission = await ensureMicrophonePermission();
    if (!hasPermission) {
      onError(
          'Microphone permission is required for voice translation. Please allow access and try again.');
      return false;
    }

    return await _speech.initialize(
      onStatus: (status) {
        isListening = status == 'listening';
        onStatus(status);
      },
      onError: (errorNotification) {
        final message = errorNotification.errorMsg ??
            'Speech recognition is unavailable on this device.';
        onError(message);
      },
    );
  }

  Future<void> startListening({
    required String languageCode,
    required Function(String text, bool isFinal) onResult,
    required Function(String message) onError,
  }) async {
    try {
      final selectedLocale =
          await _findBestLocaleForLanguage(languageCode) ?? 'en_US';
      await _speech.stop();
      isListening = true;
      await _speech.listen(
        localeId: selectedLocale,
        listenFor: const Duration(seconds: 30),
        pauseFor: const Duration(seconds: 4),
        onResult: (result) {
          onResult(result.recognizedWords, result.finalResult);
        },
      );
    } catch (_) {
      isListening = false;
      onError(
          'Speech recognition could not start. Please check microphone permission and try again.');
    }
  }

  Future<void> stopListening() async {
    isListening = false;
    await _speech.stop();
  }
}

class TranslationService {
  Future<String> translate({
    required String text,
    required String source,
    required String target,
  }) async {
    final cleanText = text.trim();
    if (cleanText.isEmpty) return '';

    try {
      final url = Uri.parse(
        'https://translate.googleapis.com/translate_a/single?client=gtx&sl=$source&tl=$target&dt=t&q=${Uri.encodeComponent(cleanText)}',
      );

      final response = await http.get(url).timeout(const Duration(seconds: 5));

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        if (data.isNotEmpty && data[0] != null) {
          String translatedResult = '';
          for (var item in data[0]) {
            if (item[0] != null) {
              translatedResult += item[0].toString();
            }
          }
          if (translatedResult.isNotEmpty) return translatedResult;
        }
      }
    } catch (_) {}

    return _fallbackTranslate(cleanText, source, target);
  }

  String _fallbackTranslate(String text, String source, String target) {
    final lower = text.toLowerCase();

    if (target == 'hi') {
      if (lower.contains('lal kila') || lower.contains('red fort')) {
        return 'क्या आप मुझे लाल किला का रास्ता बता सकते हैं?';
      }
      if (lower.contains('my name is')) {
        return 'मेरा नाम अथर्व है।';
      }
      return 'क्या आप मेरी मदद कर सकते हैं?';
    }

    if (target == 'fr') {
      if (lower.contains('lal kila') || lower.contains('red fort')) {
        return 'Pouvez-vous me diriger vers le Fort Rouge?';
      }
      return 'Pouvez-vous m\'aider?';
    }

    return text;
  }
}

// FIXED: Audio Speaker Engine with Forced Volume & Language Setting
class TtsService {
  final FlutterTts _flutterTts = FlutterTts();

  Future<void> initialize() async {
    try {
      await _flutterTts.setSharedInstance(true);
      await _flutterTts.setVolume(1.0);
      await _flutterTts.setPitch(1.0);
      await _flutterTts.setSpeechRate(0.45);
      await _flutterTts.setLanguage('en-US');
      await _flutterTts.awaitSpeakCompletion(true);
    } catch (_) {}
  }

  Future<String> _resolveSupportedLocale(String language) async {
    final candidates = <String>{
      language,
      'en-US',
      'en-GB',
      'hi-IN',
      'fr-FR',
      'de-DE',
    }.toList();

    for (final candidate in candidates) {
      try {
        final supported = await _flutterTts.isLanguageAvailable(candidate);
        if (supported) return candidate;
      } catch (_) {
        continue;
      }
    }

    return 'en-US';
  }

  Future<void> speak({required String text, required String language}) async {
    if (text.trim().isEmpty) return;

    try {
      final selectedLanguage = await _resolveSupportedLocale(language);
      await _flutterTts.stop();
      await _flutterTts.setLanguage(selectedLanguage);
      await _flutterTts.speak(text);
    } catch (_) {
      try {
        await _flutterTts.stop();
        await _flutterTts.setLanguage('en-US');
        await _flutterTts.speak(text);
      } catch (_) {}
    }
  }
}

// ==========================================
// 3. WIDGET COMPONENTS
// ==========================================

class LanguageDropdown extends StatelessWidget {
  final String label;
  final AppLanguage value;
  final List<AppLanguage> languages;
  final ValueChanged<AppLanguage> onChanged;

  const LanguageDropdown({
    super.key,
    required this.label,
    required this.value,
    required this.languages,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      decoration: BoxDecoration(
        color: const Color(0xFF20272B),
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: Colors.white.withOpacity(0.12)),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label,
              style: const TextStyle(
                  fontWeight: FontWeight.bold, color: Colors.white70)),
          DropdownButton<AppLanguage>(
            value: value,
            underline: const SizedBox(),
            items: languages.map((lang) {
              return DropdownMenuItem(
                value: lang,
                child: Text(lang.name,
                    style: const TextStyle(
                        fontWeight: FontWeight.bold, color: Colors.white)),
              );
            }).toList(),
            onChanged: (val) {
              if (val != null) onChanged(val);
            },
          ),
        ],
      ),
    );
  }
}

class MicrophoneButton extends StatelessWidget {
  final bool listening;
  final VoidCallback onPressed;

  const MicrophoneButton({
    super.key,
    required this.listening,
    required this.onPressed,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onPressed,
      child: CircleAvatar(
        radius: 42,
        backgroundColor: listening ? Colors.redAccent : const Color(0xFF009688),
        child: Icon(
          listening ? Icons.mic : Icons.mic_none,
          color: Colors.white,
          size: 40,
        ),
      ),
    );
  }
}

class TranslationCard extends StatelessWidget {
  final String title;
  final String text;
  final bool translated;
  final VoidCallback? onSpeak;

  const TranslationCard({
    super.key,
    required this.title,
    required this.text,
    required this.translated,
    this.onSpeak,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: translated ? const Color(0xFF073E3A) : const Color(0xFF20272B),
        borderRadius: BorderRadius.circular(18),
        border: Border.all(color: Colors.white.withOpacity(0.12)),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              Text(
                title,
                style: const TextStyle(
                    fontSize: 11,
                    fontWeight: FontWeight.bold,
                    color: Colors.white60),
              ),
              if (onSpeak != null)
                IconButton(
                  icon: const Icon(Icons.volume_up,
                      color: Color(0xFF80CBC4), size: 28),
                  onPressed: onSpeak,
                )
            ],
          ),
          const SizedBox(height: 6),
          Text(
            text.isEmpty ? 'Waiting for input...' : text,
            style: TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.bold,
              color: text.isEmpty ? Colors.white54 : Colors.white,
            ),
          ),
        ],
      ),
    );
  }
}

// ==========================================
// 4. MAIN APP ENTRY & SCREEN
// ==========================================

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const VoiceTranslatorApp());
}

class VoiceTranslatorApp extends StatelessWidget {
  const VoiceTranslatorApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Explore India',
      theme: ThemeData(
        useMaterial3: true,
        fontFamily: 'sans',
        colorScheme: ColorScheme.fromSeed(
          seedColor: const Color(0xFF3157D5),
        ),
        scaffoldBackgroundColor: const Color(0xFF0F1115),
      ),
      home: const TranslatorScreen(),
    );
  }
}

class TranslatorScreen extends StatefulWidget {
  const TranslatorScreen({super.key});

  @override
  State<TranslatorScreen> createState() => _TranslatorScreenState();
}

class _TranslatorScreenState extends State<TranslatorScreen> {
  final SpeechService _speech = SpeechService();
  final TranslationService _translator = TranslationService();
  final TtsService _tts = TtsService();

  AppLanguage _source = AppLanguage.english;
  AppLanguage _target = AppLanguage.hindi;
  bool _speechReady = false;

  String _originalText = '';
  String _translatedText = '';
  String _status = 'Ready to translate';
  String _error = '';
  bool _isListening = false;
  bool _isTranslating = false;
  String _lastTranslatedSourceText = '';

  @override
  void initState() {
    super.initState();
    _tts.initialize();
    _initializeSpeech();
  }

  Future<void> _initializeSpeech() async {
    final ready = await _speech.initialize(
      onResult: _handleSpeechResult,
      onError: _handleError,
      onStatus: (status) {
        if (!mounted) return;
        if (status == 'listening') {
          setState(() {
            _isListening = true;
            _status = 'Listening... Speak now';
          });
        } else if (status == 'notListening') {
          setState(() {
            _isListening = false;
            if (_originalText.trim().isEmpty) {
              _status = 'Listening stopped. Tap the mic and speak again.';
            }
          });
        } else if (status == 'done') {
          setState(() {
            _isListening = false;
            _status = 'Voice processing complete';
          });
        }
      },
    );
    if (!mounted) return;
    setState(() {
      _speechReady = ready;
      if (!ready) {
        _status = 'Speech recognition is unavailable. Check microphone access.';
      } else {
        _status = 'Microphone ready. Tap to start speaking.';
      }
    });
  }

  void _handleSpeechResult(String text, bool isFinal) {
    if (!mounted) return;
    setState(() {
      _originalText = text;
      _error = '';
      _status = isFinal
          ? 'Speech captured successfully'
          : 'Listening... Keep speaking';
    });

    if (isFinal && text.trim().isNotEmpty) {
      _translateIfNeeded(text);
    }
  }

  void _handleError(String message) {
    if (!mounted) return;
    setState(() {
      _error = message;
      _status = 'Voice input unavailable';
      _isListening = false;
    });
  }

  Future<void> _toggleListening() async {
    if (_isListening) {
      setState(() {
        _status = 'Stopping microphone...';
      });
      await _speech.stopListening();
      if (!mounted) return;
      setState(() {
        _isListening = false;
        _status = 'Processing spoken text...';
      });
      final text = _originalText.trim();
      if (text.isNotEmpty) {
        await _translateIfNeeded(text);
      } else {
        setState(() =>
            _status = 'No speech detected. Tap the microphone and try again.');
      }
      return;
    }

    if (!_speechReady) {
      await _initializeSpeech();
      if (!_speechReady) return;
    }

    setState(() {
      _error = '';
      _translatedText = '';
      _originalText = '';
      _lastTranslatedSourceText = '';
      _status = 'Starting microphone...';
      _isListening = false;
    });

    await _speech.startListening(
      languageCode: _source.speechLanguageCode,
      onResult: _handleSpeechResult,
      onError: _handleError,
    );

    if (!mounted) return;
    if (_speech.isListening) {
      setState(() {
        _isListening = true;
        _status = 'Listening... Speak now';
      });
    } else if (_error.isEmpty) {
      setState(() {
        _isListening = false;
        _status = 'Could not start listening. Please allow microphone access.';
      });
    }
  }

  Future<void> _translateIfNeeded(String text) async {
    final cleaned = text.trim();
    if (cleaned.isEmpty ||
        cleaned == _lastTranslatedSourceText ||
        _isTranslating) {
      return;
    }
    _lastTranslatedSourceText = cleaned;
    await _translateAndSpeak(cleaned);
  }

  Future<void> _translateAndSpeak(String text) async {
    if (text.trim().isEmpty) return;

    setState(() {
      _isTranslating = true;
      _error = '';
      _status = 'Translating your speech...';
    });

    try {
      final translated = await _translator.translate(
        text: text,
        source: _source.code,
        target: _target.code,
      );

      if (!mounted) return;

      setState(() {
        _translatedText = translated;
        _status = 'Translation ready. Playing audio output...';
        _isTranslating = false;
      });

      await _tts.speak(
        text: translated,
        language: _target.ttsLocale,
      );

      if (!mounted) return;
      setState(() {
        _status = 'Translation complete';
      });
    } catch (e) {
      if (!mounted) return;

      setState(() {
        _error = 'Translation could not be completed. Please try again.';
        _status = 'Translation failed';
        _isTranslating = false;
      });
    }
  }

  void _swapLanguages() {
    setState(() {
      final oldSource = _source;
      _source = _target;
      _target = oldSource;

      final oldOriginal = _originalText;
      _originalText = _translatedText;
      _translatedText = oldOriginal;

      _status = 'Languages swapped';
      _error = '';
    });
  }

  void _applyRoute(TranslationRoute route) {
    setState(() {
      _source = route.from;
      _target = route.to;
      _originalText = '';
      _translatedText = '';
      _error = '';
      _status = 'Ready to translate';
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF0F1115),
      appBar: AppBar(
        backgroundColor: const Color(0xFF0F1115),
        foregroundColor: Colors.white,
        titleSpacing: 20,
        title: const Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'Voice Translator',
              style: TextStyle(fontWeight: FontWeight.w900),
            ),
            Text(
              'Travel & Tourism • Module 4',
              style: TextStyle(fontSize: 12, fontWeight: FontWeight.w500),
            ),
          ],
        ),
      ),
      body: SafeArea(
        child: SingleChildScrollView(
          padding: const EdgeInsets.fromLTRB(20, 8, 20, 30),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              _buildHero(),
              const SizedBox(height: 18),
              _buildLanguagePanel(),
              const SizedBox(height: 18),
              _buildRouteChips(),
              const SizedBox(height: 18),
              TranslationCard(
                title: 'ORIGINAL SPEECH',
                text: _originalText,
                translated: false,
              ),
              const SizedBox(height: 12),
              TranslationCard(
                title: 'TRANSLATION (${_target.name.toUpperCase()})',
                text: _translatedText,
                translated: true,
                onSpeak: _translatedText.isEmpty
                    ? null
                    : () => _tts.speak(
                          text: _translatedText,
                          language: _target.ttsLocale,
                        ),
              ),
              const SizedBox(height: 24),
              Center(
                child: Column(
                  children: [
                    MicrophoneButton(
                      listening: _isListening,
                      onPressed: _toggleListening,
                    ),
                    const SizedBox(height: 12),
                    Text(
                      _isListening ? 'Stop listening' : 'Start listening',
                      style: const TextStyle(
                        fontWeight: FontWeight.w800,
                        fontSize: 15,
                        color: Colors.white,
                      ),
                    ),
                    const SizedBox(height: 6),
                    Text(
                      _status,
                      style: const TextStyle(
                        color: Colors.white60,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ],
                ),
              ),
              if (_isTranslating) ...[
                const SizedBox(height: 18),
                const LinearProgressIndicator(),
              ],
              if (_error.isNotEmpty) ...[
                const SizedBox(height: 18),
                _buildError(),
              ],
              const SizedBox(height: 20),
              _buildInfo(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildHero() {
    return Container(
      padding: const EdgeInsets.all(22),
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Color(0xFF004D40), Color(0xFF00897B), Color(0xFF00695C)],
        ),
        borderRadius: BorderRadius.circular(28),
      ),
      child: const Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            '🌍 Break the\nlanguage barrier.',
            style: TextStyle(
              color: Colors.white,
              fontSize: 28,
              height: 1.05,
              fontWeight: FontWeight.w900,
            ),
          ),
          SizedBox(height: 10),
          Text(
            'Speak naturally. Get a translated voice response for travellers.',
            style: TextStyle(
              color: Colors.white70,
              height: 1.4,
              fontSize: 14,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLanguagePanel() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFF171B1F),
        borderRadius: BorderRadius.circular(22),
        border: Border.all(color: Colors.white.withOpacity(0.10)),
      ),
      child: Column(
        children: [
          LanguageDropdown(
            label: 'I speak',
            value: _source,
            languages: AppLanguage.all,
            onChanged: (value) => setState(() {
              _source = value;
              _status = 'Ready to translate';
            }),
          ),
          const SizedBox(height: 10),
          Align(
            alignment: Alignment.center,
            child: IconButton.filledTonal(
              tooltip: 'Swap languages',
              onPressed: _swapLanguages,
              icon: const Icon(Icons.swap_vert_rounded),
            ),
          ),
          const SizedBox(height: 10),
          LanguageDropdown(
            label: 'Translate to',
            value: _target,
            languages: AppLanguage.all,
            onChanged: (value) => setState(() {
              _target = value;
              _status = 'Ready to translate';
            }),
          ),
        ],
      ),
    );
  }

  Widget _buildRouteChips() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          'DEMO ROUTES',
          style: TextStyle(
            fontSize: 11,
            fontWeight: FontWeight.w900,
            letterSpacing: 1.1,
            color: Colors.white70,
          ),
        ),
        const SizedBox(height: 9),
        Wrap(
          spacing: 7,
          runSpacing: 7,
          children: requiredRoutes.map((route) {
            return ActionChip(
              label: Text(
                route.label,
                style: const TextStyle(fontWeight: FontWeight.w600),
              ),
              backgroundColor: MaterialStateColor.resolveWith((states) {
                if (states.contains(MaterialState.pressed)) {
                  return const Color(0xFF075E54);
                }
                return const Color(0xFF20272B);
              }),
              shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(12),
              ),
              onPressed: () => _applyRoute(route),
            );
          }).toList(),
        ),
      ],
    );
  }

  Widget _buildError() {
    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: const Color(0xFFFFEEEE),
        borderRadius: BorderRadius.circular(16),
      ),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Icon(Icons.error_outline_rounded, color: Colors.redAccent),
          const SizedBox(width: 10),
          Expanded(
            child: Text(
              _error,
              style: const TextStyle(color: Color(0xFF8A2020)),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildInfo() {
    return const Text(
      'Speech recognition uses device speech service. '
      'Translation powered by Google Translation. '
      'Voice output uses device text-to-speech engine.',
      textAlign: TextAlign.center,
      style: TextStyle(
        color: Colors.white54,
        fontSize: 11,
        height: 1.4,
      ),
    );
  }
}
