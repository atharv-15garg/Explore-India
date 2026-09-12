import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:geolocator/geolocator.dart';
import 'package:flutter_map/flutter_map.dart';
import 'package:latlong2/latlong.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

const _appBackground = Color(0xFF0F1115);
const _appSurface = Color(0xFF171B1F);
const _appTeal = Color(0xFF009688);

void main() {
  runApp(const Member2App());
}

class Member2App extends StatelessWidget {
  const Member2App({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      title: 'Explore India',
      theme: ThemeData(
        useMaterial3: true,
        scaffoldBackgroundColor: _appBackground,
        colorScheme: ColorScheme.fromSeed(
          seedColor: _appTeal,
          brightness: Brightness.dark,
        ),
      ),
      home: const Member2TestShell(),
    );
  }
}

class Member2TestShell extends StatefulWidget {
  const Member2TestShell({super.key});

  @override
  State<Member2TestShell> createState() => _Member2TestShellState();
}

class _Member2TestShellState extends State<Member2TestShell> {
  int _currentIndex = 0;

  final List<Widget> _screens = [
    const SOSWidget(),
    const LocalHelplinesWidget(),
    const OfflineMapEngineWidget(),
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: _screens[_currentIndex],
      bottomNavigationBar: BottomNavigationBar(
        currentIndex: _currentIndex,
        backgroundColor: _appSurface,
        selectedItemColor: _appTeal,
        unselectedItemColor: Colors.white60,
        onTap: (index) => setState(() => _currentIndex = index),
        items: const [
          BottomNavigationBarItem(icon: Icon(Icons.sos), label: 'SOS'),
          BottomNavigationBarItem(icon: Icon(Icons.phone_in_talk), label: 'Helplines'),
          BottomNavigationBarItem(icon: Icon(Icons.map_outlined), label: 'Maps'),
        ],
      ),
    );
  }
}

// ----------------- COMPONENT 1: SOS EMERGENCY (FIXED ICON) -----------------
class SOSWidget extends StatelessWidget {
  const SOSWidget({super.key});

  Future<void> _triggerSOS(BuildContext context) async {
    Position? pos;
    try {
      pos = await Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
    } catch (_) {}

    String latStr = pos != null ? pos.latitude.toString() : "28.6139";
    String lngStr = pos != null ? pos.longitude.toString() : "77.2090";
    String googleMapsLink = "https://maps.google.com/?q=$latStr,$lngStr";
    String msg = "EMERGENCY! Need help in India. My GPS Location: $googleMapsLink";

    if (!context.mounted) return;

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        backgroundColor: _appSurface,
        title: const Text('EMERGENCY SOS ACTIVE', style: TextStyle(color: Colors.white, fontWeight: FontWeight.bold)),
        content: Text('GPS Link Generated:\n$googleMapsLink\n\nDispatching alert to guardian SMS & Local Police (112)...', style: const TextStyle(color: Colors.white70)),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx), child: const Text('CANCEL', style: TextStyle(color: Colors.white54))),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: _appTeal, foregroundColor: Colors.white),
            onPressed: () async {
              Navigator.pop(ctx);
              final Uri smsUri = Uri(scheme: 'sms', path: '+919999999999', queryParameters: {'body': msg});
              if (await canLaunchUrl(smsUri)) await launchUrl(smsUri);
              final Uri telUri = Uri(scheme: 'tel', path: '112');
              if (await canLaunchUrl(telUri)) await launchUrl(telUri);
            },
            child: const Text('DISPATCH ALERTS NOW', style: TextStyle(color: Colors.red, fontWeight: FontWeight.bold)),
          ),
        ],
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: _appBackground,
      appBar: AppBar(
        title: const Text('Explore India', style: TextStyle(fontWeight: FontWeight.w800)),
        backgroundColor: _appBackground,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: Center(
        child: Padding(
          padding: const EdgeInsets.all(20.0),
          child: Container(
            width: double.infinity,
            padding: const EdgeInsets.all(24),
            decoration: BoxDecoration(
              gradient: const LinearGradient(colors: [Color(0xFF2A171B), Color(0xFF1B1C20)]),
              borderRadius: BorderRadius.circular(28),
              border: Border.all(color: Colors.redAccent.withOpacity(0.35)),
            ),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                Container(
                  width: 92,
                  height: 92,
                  decoration: BoxDecoration(
                    shape: BoxShape.circle,
                    color: Colors.redAccent.withOpacity(0.16),
                    border: Border.all(color: Colors.redAccent.withOpacity(0.55), width: 2),
                  ),
                  child: const Icon(Icons.emergency_rounded, size: 46, color: Colors.redAccent),
                ),
                const SizedBox(height: 20),
                const Text('Emergency SOS', style: TextStyle(color: Colors.white, fontSize: 25, fontWeight: FontWeight.w800)),
                const SizedBox(height: 8),
                const Text('Send your current location and contact local emergency services.', textAlign: TextAlign.center, style: TextStyle(color: Colors.white70, height: 1.45)),
                const SizedBox(height: 24),
                ElevatedButton.icon(
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.redAccent,
                    foregroundColor: Colors.white,
                    minimumSize: const Size(double.infinity, 62),
                    shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
                  ),
                  onPressed: () => _triggerSOS(context),
                  icon: const Icon(Icons.warning_amber_rounded, size: 28),
                  label: const Text('SEND SOS ALERT', style: TextStyle(fontSize: 16, fontWeight: FontWeight.w800)),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

// ----------------- COMPONENT 2: HELPLINES -----------------
class LocalHelplinesWidget extends StatefulWidget {
  const LocalHelplinesWidget({super.key});

  @override
  State<LocalHelplinesWidget> createState() => _LocalHelplinesWidgetState();
}

class _LocalHelplinesWidgetState extends State<LocalHelplinesWidget> {
  String selectedCity = 'None';

  final List<Map<String, String>> nationalHelplines = [
    {'title': 'National Tourist Helpline', 'number': '1363', 'desc': '24x7 Multi-lingual Tourist Support'},
    {'title': 'National Emergency / Police', 'number': '112', 'desc': 'All India Police & Emergency Response'},
    {'title': 'Women Emergency Helpline', 'number': '1091', 'desc': 'Dedicated Women Safety'},
    {'title': 'Ambulance / Medical', 'number': '102', 'desc': 'Medical Emergency Service'},
    {'title': 'Fire Brigade', 'number': '101', 'desc': 'Fire Safety Department'},
  ];

  final Map<String, List<Map<String, String>>> localSpecialHelplines = {
    'Jaipur': [{'title': 'Jaipur Tourist Police Desk', 'number': '0141-2374448'}],
    'Agra': [{'title': 'Agra Taj Security Police', 'number': '0562-2226471'}],
    'Varanasi': [{'title': 'Varanasi Ghat Tourist Police', 'number': '0542-2508112'}],
    'Kolkata': [{'title': 'Kolkata Tourist Assistance', 'number': '033-22145857'}],
  };

  void _call(String num) async {
    final Uri uri = Uri(scheme: 'tel', path: num);
    if (await canLaunchUrl(uri)) await launchUrl(uri);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Helpline Numbers'), backgroundColor: Colors.redAccent, foregroundColor: Colors.white),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('All India National Emergency Lines', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold, color: Colors.black87)),
            const SizedBox(height: 10),
            ListView.builder(
              shrinkWrap: true,
              physics: const NeverScrollableScrollPhysics(),
              itemCount: nationalHelplines.length,
              itemBuilder: (ctx, i) {
                final item = nationalHelplines[i];
                return Card(
                  elevation: 2,
                  child: ListTile(
                    leading: const CircleAvatar(backgroundColor: Colors.red, child: Icon(Icons.phone, color: Colors.white)),
                    title: Text(item['title']!, style: const TextStyle(fontWeight: FontWeight.bold)),
                    subtitle: Text('${item['number']} - ${item['desc']}'),
                    trailing: IconButton(
                      icon: const Icon(Icons.call, color: Colors.green, size: 28),
                      onPressed: () => _call(item['number']!),
                    ),
                  ),
                );
              },
            ),
            const SizedBox(height: 20),
            const Divider(),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('Local Heritage Police Desks:', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                DropdownButton<String>(
                  value: selectedCity,
                  items: ['None', 'Jaipur', 'Agra', 'Varanasi', 'Kolkata'].map((c) => DropdownMenuItem(value: c, child: Text(c))).toList(),
                  onChanged: (val) => setState(() => selectedCity = val!),
                ),
              ],
            ),
            if (selectedCity != 'None' && localSpecialHelplines.containsKey(selectedCity))
              ListView.builder(
                shrinkWrap: true,
                physics: const NeverScrollableScrollPhysics(),
                itemCount: localSpecialHelplines[selectedCity]!.length,
                itemBuilder: (ctx, i) {
                  final item = localSpecialHelplines[selectedCity]![i];
                  return Card(
                    color: Colors.amber.shade50,
                    child: ListTile(
                      leading: const Icon(Icons.local_police, color: Colors.amber),
                      title: Text(item['title']!),
                      subtitle: Text(item['number']!),
                      trailing: IconButton(
                        icon: const Icon(Icons.call, color: Colors.green),
                        onPressed: () => _call(item['number']!),
                      ),
                    ),
                  );
                },
              )
          ],
        ),
      ),
    );
  }
}

// ----------------- COMPONENT 3: HYBRID SATELLITE & DYNAMIC MAP ROUTING -----------------
class OfflineMapEngineWidget extends StatefulWidget {
  const OfflineMapEngineWidget({super.key});

  @override
  State<OfflineMapEngineWidget> createState() => _OfflineMapEngineWidgetState();
}

class _OfflineMapEngineWidgetState extends State<OfflineMapEngineWidget> {
  final MapController _mapController = MapController();
  final TextEditingController _searchController = TextEditingController();

  LatLng _currentLocation = const LatLng(28.6139, 77.2090); // Delhi Centre Default
  LatLng? _destinationLocation;
  List<LatLng> _roadPolyline = [];
  bool isLoadingRoute = false;
  
  bool isSatelliteView = true;

  // Pre-indexed Major Indian Destination Coordinates
  final Map<String, LatLng> indianCitiesDB = {
    'mumbai': const LatLng(19.0760, 72.8777),
    'delhi': const LatLng(28.6139, 77.2090),
    'ghaziabad': const LatLng(28.6692, 77.4538),
    'red fort': const LatLng(28.6562, 77.2410),
    'taj mahal': const LatLng(27.1751, 78.0421),
    'agra': const LatLng(27.1767, 78.0081),
    'jaipur': const LatLng(26.9124, 75.7873),
    'bengaluru': const LatLng(12.9716, 77.5946),
    'bangalore': const LatLng(12.9716, 77.5946),
    'varanasi': const LatLng(25.3176, 82.9739),
    'kolkata': const LatLng(22.5726, 88.3639),
    'pune': const LatLng(18.5204, 73.8567),
  };

  @override
  void initState() {
    super.initState();
    _getPreciseLocation();
  }

  Future<void> _getPreciseLocation() async {
    try {
      Position pos = await Geolocator.getCurrentPosition(desiredAccuracy: LocationAccuracy.high);
      setState(() {
        _currentLocation = LatLng(pos.latitude, pos.longitude);
      });
      _mapController.move(_currentLocation, 12.0);
    } catch (_) {}
  }

  // Fetch Real Road Route using OSRM
  Future<void> _fetchRealRoadRoute(LatLng dest) async {
    setState(() => isLoadingRoute = true);
    final url = Uri.parse(
        'https://router.project-osrm.org/route/v1/driving/${_currentLocation.longitude},${_currentLocation.latitude};${dest.longitude},${dest.latitude}?overview=full&geometries=geojson');

    try {
      final response = await http.get(url);
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final List coordinates = data['routes'][0]['geometry']['coordinates'];
        setState(() {
          _roadPolyline = coordinates.map((c) => LatLng(c[1].toDouble(), c[0].toDouble())).toList();
          isLoadingRoute = false;
        });
        _mapController.move(dest, 7.0); // Fit wider zoom for far cities like Mumbai
      }
    } catch (e) {
      setState(() => isLoadingRoute = false);
    }
  }

  // Dynamic Geocoding Search Engine
  Future<void> _onSearchDestination() async {
    String query = _searchController.text.trim().toLowerCase();
    if (query.isEmpty) return;

    LatLng? target;

    // Check Local Database First
    if (indianCitiesDB.containsKey(query)) {
      target = indianCitiesDB[query];
    } else {
      // Fallback Search via OpenStreetMap Nominatim API
      setState(() => isLoadingRoute = true);
      try {
        final searchUrl = Uri.parse('https://nominatim.openstreetmap.org/search?q=$query,India&format=json&limit=1');
        final res = await http.get(searchUrl, headers: {'User-Agent': 'member2_app'});
        if (res.statusCode == 200) {
          final data = json.decode(res.body);
          if (data.isNotEmpty) {
            double lat = double.parse(data[0]['lat']);
            double lon = double.parse(data[0]['lon']);
            target = LatLng(lat, lon);
          }
        }
      } catch (_) {}
    }

    if (target != null) {
      setState(() => _destinationLocation = target);
      _fetchRealRoadRoute(target);
    } else {
      setState(() => isLoadingRoute = false);
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('Location "$query" not found. Try Mumbai, Jaipur, Agra, Delhi etc.')));
    }
  }

  @override
  Widget build(BuildContext context) {
    String standardMapUrl = 'https://tile.openstreetmap.org/{z}/{x}/{y}.png';
    String satelliteMapUrl = 'https://server.arcgisonline.com/ArcGIS/rest/services/World_Imagery/MapServer/tile/{z}/{y}/{x}';
    // Hybrid City/Road Labels Overlay
    String labelsOverlayUrl = 'https://services.arcgisonline.com/ArcGIS/rest/services/Reference/World_Boundaries_and_Places/MapServer/tile/{z}/{y}/{x}';

    return Scaffold(
      backgroundColor: _appBackground,
      appBar: AppBar(
        title: const Text('Explore India', style: TextStyle(fontWeight: FontWeight.w800)),
        backgroundColor: _appBackground,
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: Stack(
        children: [
          FlutterMap(
            mapController: _mapController,
            options: MapOptions(
              initialCenter: _currentLocation,
              initialZoom: 12.0,
              minZoom: 4.0,
              maxZoom: 18.0,
            ),
            children: [
              // Base Map Layer
              TileLayer(
                urlTemplate: isSatelliteView ? satelliteMapUrl : standardMapUrl,
                userAgentPackageName: 'com.example.member2_sos_maps',
              ),
              // Hybrid Labels Overlay Layer (Shows City/Road names over satellite imagery)
              if (isSatelliteView)
                TileLayer(
                  urlTemplate: labelsOverlayUrl,
                  userAgentPackageName: 'com.example.member2_sos_maps',
                ),
              PolylineLayer(
                polylines: [
                  if (_roadPolyline.isNotEmpty)
                    Polyline(
                      points: _roadPolyline,
                      strokeWidth: 5.0,
                      color: Colors.cyanAccent,
                    ),
                ],
              ),
              MarkerLayer(
                markers: [
                  Marker(
                    point: _currentLocation,
                    width: 50,
                    height: 50,
                    child: const Icon(Icons.my_location, color: Colors.blueAccent, size: 38),
                  ),
                  if (_destinationLocation != null)
                    Marker(
                      point: _destinationLocation!,
                      width: 50,
                      height: 50,
                      child: const Icon(Icons.location_on, color: Colors.redAccent, size: 45),
                    ),
                ],
              ),
            ],
          ),

          // Search Bar Over Map
          Positioned(
            top: 15,
            left: 15,
            right: 15,
            child: Card(
              color: _appSurface,
              elevation: 8,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
              child: Padding(
                padding: const EdgeInsets.symmetric(horizontal: 10.0),
                child: Row(
                  children: [
                    const Icon(Icons.search, color: Color(0xFF80CBC4)),
                    const SizedBox(width: 8),
                    Expanded(
                      child: TextField(
                        controller: _searchController,
                        onSubmitted: (_) => _onSearchDestination(),
                        style: const TextStyle(color: Colors.white),
                        decoration: const InputDecoration(
                          hintText: 'Search: Mumbai, Jaipur, Red Fort, Taj Mahal...',
                          hintStyle: TextStyle(color: Colors.white54),
                          border: InputBorder.none,
                        ),
                      ),
                    ),
                    if (isLoadingRoute)
                      const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
                    else
                      IconButton(
                        icon: const Icon(Icons.directions, color: Color(0xFF80CBC4), size: 30),
                        onPressed: _onSearchDestination,
                      ),
                  ],
                ),
              ),
            ),
          ),

          // Satellite / Hybrid Vector Toggle Button
          Positioned(
            bottom: 20,
            right: 20,
            child: FloatingActionButton.extended(
              backgroundColor: _appTeal,
              foregroundColor: Colors.white,
              onPressed: () {
                setState(() {
                  isSatelliteView = !isSatelliteView;
                });
              },
              icon: Icon(isSatelliteView ? Icons.map : Icons.satellite_alt),
              label: Text(isSatelliteView ? 'Vector View' : 'Satellite Hybrid'),
            ),
          )
        ],
      ),
    );
  }
}
