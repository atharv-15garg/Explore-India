import 'package:flutter/material.dart';
import 'package:url_launcher/url_launcher.dart';
import 'package:cached_network_image/cached_network_image.dart';

// Helper function to get gradient color for a monument
LinearGradient getMonumentGradient(String name) {
  final colorMap = {
    'Qutub Minar': LinearGradient(
      colors: [const Color(0xFF8B4513), const Color(0xFFD2691E)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Red Fort (Lal Qila)': LinearGradient(
      colors: [const Color(0xFFB22234), const Color(0xFF8B0000)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Humayun\'s Tomb': LinearGradient(
      colors: [const Color(0xFF696969), const Color(0xFF808080)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Elephanta Caves': LinearGradient(
      colors: [const Color(0xFF654321), const Color(0xFF8B4513)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Kanheri Caves': LinearGradient(
      colors: [const Color(0xFF556B2F), const Color(0xFF6B8E23)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Taj Mahal': LinearGradient(
      colors: [const Color(0xFFF5F5F5), const Color(0xFFDCDCDC)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Agra Fort': LinearGradient(
      colors: [const Color(0xFF8B4513), const Color(0xFFA0522D)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Bangalore Palace': LinearGradient(
      colors: [const Color(0xFF4B0082), const Color(0xFF8B008B)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Tipu Sultan\'s Summer Palace': LinearGradient(
      colors: [const Color(0xFF8B7355), const Color(0xFFD2B48C)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Sarnath Excavated Remains': LinearGradient(
      colors: [const Color(0xFFA0522D), const Color(0xFFCD853F)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Ramnagar Fort': LinearGradient(
      colors: [const Color(0xFF800020), const Color(0xFF660000)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Victoria Memorial Hall': LinearGradient(
      colors: [const Color(0xFFFFFFFF), const Color(0xFFE0E0E0)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Indian Museum': LinearGradient(
      colors: [const Color(0xFF2F4F4F), const Color(0xFF708090)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Shaniwar Wada': LinearGradient(
      colors: [const Color(0xFF654321), const Color(0xFFD2691E)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Aga Khan Palace': LinearGradient(
      colors: [const Color(0xFF4B3621), const Color(0xFF704214)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Amer Fort': LinearGradient(
      colors: [const Color(0xFFCD853F), const Color(0xFF8B4513)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
    'Hawa Mahal': LinearGradient(
      colors: [const Color(0xFFFF6B6B), const Color(0xFFFF8C8C)],
      begin: Alignment.topLeft,
      end: Alignment.bottomRight,
    ),
  };
  
  return colorMap[name] ?? LinearGradient(
    colors: [const Color(0xFF667eea), const Color(0xFF764ba2)],
    begin: Alignment.topLeft,
    end: Alignment.bottomRight,
  );
}

// Custom monument image widget
class MonumentImage extends StatelessWidget {
  final String imageUrl;
  final String monumentName;
  final double height;
  final bool isListCard;

  const MonumentImage({
    required this.imageUrl,
    required this.monumentName,
    this.height = 180,
    this.isListCard = true,
    super.key,
  });

  static String getFallbackAsset(String monumentName) {
    final normalized = monumentName.toLowerCase();
    if (normalized.contains('taj')) {
      return 'assets/images/monument_6.png';
    }
    return 'assets/images/monument_1.png';
  }

  @override
  Widget build(BuildContext context) {
    final fallbackAsset = getFallbackAsset(monumentName);
    final isLocalAsset = imageUrl.trim().startsWith('assets/');
    final hasValidImageUrl = imageUrl.trim().isNotEmpty && Uri.tryParse(imageUrl)?.hasScheme == true;

    return Container(
      width: double.infinity,
      height: height,
      decoration: BoxDecoration(
        gradient: getMonumentGradient(monumentName),
      ),
      child: ClipRRect(
        borderRadius: BorderRadius.circular(isListCard ? 12 : 0),
        child: Stack(
          fit: StackFit.expand,
          children: [
            if (isLocalAsset)
              Image.asset(
                imageUrl,
                fit: BoxFit.cover,
                width: double.infinity,
                height: double.infinity,
                errorBuilder: (context, error, stackTrace) => Image.asset(
                  fallbackAsset,
                  fit: BoxFit.cover,
                  width: double.infinity,
                  height: double.infinity,
                ),
              )
            else if (hasValidImageUrl)
              CachedNetworkImage(
                imageUrl: imageUrl,
                fit: BoxFit.cover,
                width: double.infinity,
                height: double.infinity,
                key: ValueKey(imageUrl),
                placeholder: (context, url) => Image.asset(
                  fallbackAsset,
                  fit: BoxFit.cover,
                  width: double.infinity,
                  height: double.infinity,
                ),
                errorWidget: (context, url, error) => Image.asset(
                  fallbackAsset,
                  fit: BoxFit.cover,
                  width: double.infinity,
                  height: double.infinity,
                ),
              )
            else
              Image.asset(
                fallbackAsset,
                fit: BoxFit.cover,
                width: double.infinity,
                height: double.infinity,
              ),
          ],
        ),
      ),
    );
  }
}

void main() {
  runApp(const IncredibleIndiaApp());
}

class IncredibleIndiaApp extends StatelessWidget {
  const IncredibleIndiaApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Explore India',
      debugShowCheckedModeBanner: false,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFFFF5722)),
        useMaterial3: true,
      ),
      home: const CitySelectionScreen(),
    );
  }
}

// -------------------------------------------------------------
// Data Models
// -------------------------------------------------------------
class HeritageSite {
  final String name;
  final String city;
  final String tag;
  final String history;
  final String peakHours;
  final List<double> rushLevels; // Index 0 = 9 AM, Index 8 = 5 PM (Scale 0.0 to 1.0)
  final String entryRules;
  final String ticketPrice;
  final String bookingUrl;
  final String imageUrl;

  HeritageSite({
    required this.name,
    required this.city,
    required this.tag,
    required this.history,
    required this.peakHours,
    required this.rushLevels,
    required this.entryRules,
    required this.ticketPrice,
    required this.bookingUrl,
    required this.imageUrl,
  });
}

// -------------------------------------------------------------
// Dataset for All 8 Cities
// -------------------------------------------------------------
final Map<String, List<HeritageSite>> heritageData = {
  'Delhi': [
    HeritageSite(
      name: 'Qutub Minar',
      city: 'Delhi',
      tag: 'UNESCO World Heritage Site',
      history:
          'Constructed in 1193 by Qutab-ud-din Aibak after the defeat of Delhi\'s last Hindu kingdom. It stands at 72.5 meters tall with five distinct storeys built of red sandstone and marble.',
      peakHours: '11:00 AM – 04:00 PM',
      rushLevels: [0.2, 0.4, 0.8, 0.95, 0.85, 0.7, 0.4, 0.2, 0.1],
      entryRules: 'Valid ID required. Tripods, drones, and large backpacks are prohibited.',
      ticketPrice: '₹50 (Indians) / ₹600 (Foreigners)',
      bookingUrl: 'https://asi.paygov.org.in/',
      imageUrl: 'assets/images/Qutub minar.jpg',
    ),
    HeritageSite(
      name: 'Red Fort (Lal Qila)',
      city: 'Delhi',
      tag: 'Mughal Architecture Landmark',
      history:
          'Commissioned by Shah Jahan in 1638 when he shifted his capital from Agra to Delhi. It served as the main residence of the Mughal emperors for nearly two centuries.',
      peakHours: '01:00 PM – 05:00 PM',
      rushLevels: [0.1, 0.3, 0.6, 0.8, 0.9, 0.85, 0.6, 0.3, 0.1],
      entryRules: 'Closed on Mondays. Mandatory security check at Lahori Gate.',
      ticketPrice: '₹50 (Indians) / ₹600 (Foreigners)',
      bookingUrl: 'https://asi.paygov.org.in/',
      imageUrl: 'assets/images/red fort.jpg',
    ),
    HeritageSite(
      name: 'Humayun’s Tomb',
      city: 'Delhi',
      tag: 'UNESCO World Heritage Site',
      history:
          'Built in 1570, this garden-tomb was the first substantial example of Mughal architecture in India and inspired the design of the Taj Mahal.',
      peakHours: '02:00 PM – 05:30 PM',
      rushLevels: [0.2, 0.3, 0.5, 0.65, 0.8, 0.85, 0.5, 0.2, 0.0],
      entryRules: 'Drone cameras strictly forbidden. Open from sunrise to sunset.',
      ticketPrice: '₹40 (Indians) / ₹600 (Foreigners)',
      bookingUrl: 'https://asi.paygov.org.in/',
      imageUrl: 'assets/images/humyuns tomb.jpg',
    ),
  ],
  'Mumbai': [
    HeritageSite(
      name: 'Elephanta Caves',
      city: 'Mumbai',
      tag: 'UNESCO Rock-Cut Temples',
      history:
          'A complex of sculpted caves located on Elephanta Island dating back to the 5th and 7th centuries, predominantly dedicated to the Hindu god Shiva.',
      peakHours: '12:00 PM – 04:00 PM',
      rushLevels: [0.1, 0.3, 0.7, 0.9, 0.85, 0.6, 0.3, 0.1, 0.0],
      entryRules: 'Closed on Mondays. Ferry transfers from Gateway of India are ticketed separately.',
      ticketPrice: '₹40 (Indians) / ₹600 (Foreigners)',
      bookingUrl: 'https://asi.paygov.org.in/',
      imageUrl: 'assets/images/elephant ccaves.jpg',
    ),
    HeritageSite(
      name: 'Kanheri Caves',
      city: 'Mumbai',
      tag: 'Buddhist Heritage Site',
      history:
          'Located inside Sanjay Gandhi National Park, these 109 rock-cut Buddhist caves span from the 1st century BCE to the 10th century CE.',
      peakHours: '11:00 AM – 03:00 PM',
      rushLevels: [0.2, 0.5, 0.7, 0.8, 0.7, 0.5, 0.3, 0.1, 0.0],
      entryRules: 'National Park entry fee is charged separately at the main gate.',
      ticketPrice: '₹25 (Indians) / ₹300 (Foreigners)',
      bookingUrl: 'https://asi.paygov.org.in/',
      imageUrl: 'assets/images/kanheri caves.jpg',
    ),
  ],
  'Agra': [
    HeritageSite(
      name: 'Taj Mahal',
      city: 'Agra',
      tag: 'UNESCO Wonder of the World',
      history:
          'An ivory-white marble mausoleum on the right bank of the river Yamuna, built between 1631 and 1648 under the orders of Shah Jahan for his favorite wife, Mumtaz Mahal.',
      peakHours: '06:00 AM – 08:30 AM & 04:00 PM – 06:30 PM',
      rushLevels: [0.95, 0.7, 0.5, 0.45, 0.6, 0.75, 0.9, 0.95, 0.3],
      entryRules: 'Closed on Fridays. Shoe covers mandatory for upper marble mausoleum platform.',
      ticketPrice: '₹50 (+₹200 for main dome) / ₹1100 (Foreigners)',
      bookingUrl: 'https://asi.paygov.org.in/',
      imageUrl: 'assets/images/Taj mahal.jpeg',
    ),
    HeritageSite(
      name: 'Agra Fort',
      city: 'Agra',
      tag: 'UNESCO World Heritage Site',
      history:
          'A historical red-sandstone fortress along the Yamuna River which served as the primary seat of the Mughal rulers until 1638.',
      peakHours: '02:00 PM – 05:00 PM',
      rushLevels: [0.2, 0.4, 0.6, 0.75, 0.85, 0.8, 0.5, 0.2, 0.1],
      entryRules: 'Entry open from sunrise to sunset. Baggage counter available at Amar Singh Gate.',
      ticketPrice: '₹50 (Indians) / ₹650 (Foreigners)',
      bookingUrl: 'https://asi.paygov.org.in/',
      imageUrl: 'assets/images/Agra fort.jpg',
    ),
  ],
  'Bengaluru': [
    HeritageSite(
      name: 'Bangalore Palace',
      city: 'Bengaluru',
      tag: 'Tudor Revival Palace',
      history:
          'Constructed in 1878 by Chamarajendra Wadiyar X, the palace is renowned for its fortified towers, battlements, and Tudor-style architecture.',
      peakHours: '01:00 PM – 05:00 PM',
      rushLevels: [0.2, 0.3, 0.5, 0.75, 0.85, 0.8, 0.6, 0.2, 0.0],
      entryRules: 'Separate fees apply for handheld and professional camera equipment.',
      ticketPrice: '₹250 (Indians) / ₹500 (Foreigners)',
      bookingUrl: 'https://karnatakatourism.org/',
      imageUrl: 'assets/images/Bangalore-Palace.jpeg',
    ),
    HeritageSite(
      name: 'Tipu Sultan’s Summer Palace',
      city: 'Bengaluru',
      tag: 'Indo-Islamic Wooden Heritage',
      history:
          'Completed in 1791 within the Bangalore Fort, constructed predominantly from teak wood featuring ornate pillars and balconies.',
      peakHours: '11:00 AM – 03:30 PM',
      rushLevels: [0.3, 0.5, 0.7, 0.8, 0.7, 0.5, 0.3, 0.1, 0.0],
      entryRules: 'Footwear must be deposited in designated sections outside wooden galleries.',
      ticketPrice: '₹20 (Indians) / ₹250 (Foreigners)',
      bookingUrl: 'https://asi.paygov.org.in/',
      imageUrl: 'assets/images/tipu sultan summer palace.jpg',
    ),
  ],
  'Varanasi': [
    HeritageSite(
      name: 'Sarnath Excavated Remains',
      city: 'Varanasi',
      tag: 'Sacred Buddhist Heritage Site',
      history:
          'The historical Deer Park where Gautama Buddha delivered his first sermon (Dharmachakra Pravartana) after reaching enlightenment.',
      peakHours: '02:30 PM – 05:30 PM',
      rushLevels: [0.2, 0.3, 0.5, 0.6, 0.8, 0.9, 0.7, 0.3, 0.0],
      entryRules: 'Silent zone. ASI museum adjacent to the premises is closed on Fridays.',
      ticketPrice: '₹25 (Indians) / ₹300 (Foreigners)',
      bookingUrl: 'https://asi.paygov.org.in/',
      imageUrl: 'assets/images/sarnath excavated remains.jpg',
    ),
    HeritageSite(
      name: 'Ramnagar Fort',
      city: 'Varanasi',
      tag: '18th-Century Riverfront Fort',
      history:
          'Built in 1750 by Kashi Naresh Raja Balwant Singh on the eastern bank of the Ganges, featuring a vintage museum of weapons and royal cars.',
      peakHours: '03:00 PM – 06:00 PM',
      rushLevels: [0.1, 0.3, 0.4, 0.6, 0.8, 0.85, 0.6, 0.2, 0.0],
      entryRules: 'Photography strictly restricted inside the museum gallery chambers.',
      ticketPrice: '₹50 per person',
      bookingUrl: 'https://uptourism.gov.in/',
      imageUrl: 'assets/images/ramnagar-fort-.jpg',
    ),
  ],
  'Kolkata': [
    HeritageSite(
      name: 'Victoria Memorial Hall',
      city: 'Kolkata',
      tag: 'Colonial Indo-Saracenic Landmark',
      history:
          'Built between 1906 and 1921 using white Makrana marble in honor of Queen Victoria, housing 25 galleries of rare historical artifacts.',
      peakHours: '02:00 PM – 05:30 PM',
      rushLevels: [0.2, 0.4, 0.6, 0.8, 0.9, 0.85, 0.7, 0.3, 0.0],
      entryRules: 'Galleries closed on Mondays; gardens remain accessible daily.',
      ticketPrice: '₹50 (Museum) / ₹20 (Garden Only)',
      bookingUrl: 'https://victoriamemorial-cal.org/',
      imageUrl: 'assets/images/victoria memorial hall.jpg',
    ),
    HeritageSite(
      name: 'Indian Museum',
      city: 'Kolkata',
      tag: 'Oldest Museum in India (1814)',
      history:
          'The ninth oldest regular museum in the world, containing six main sections covering Art, Archaeology, Anthropology, Geology, Zoology, and Botany.',
      peakHours: '12:00 PM – 04:00 PM',
      rushLevels: [0.3, 0.5, 0.7, 0.85, 0.8, 0.6, 0.4, 0.1, 0.0],
      entryRules: 'Closed on Mondays and national holidays.',
      ticketPrice: '₹50 (Indians) / ₹500 (Foreigners)',
      bookingUrl: 'https://indianmuseumkolkata.org/',
      imageUrl: 'assets/images/inidan mueseuem.jpg',
    ),
  ],
  'Pune': [
    HeritageSite(
      name: 'Shaniwar Wada',
      city: 'Pune',
      tag: 'Maratha Empire Seat (1732)',
      history:
          'Built by Peshwa Baji Rao I as the seat of the Peshwas of the Maratha Empire, famed for its massive teak Delhi Darwaza gate.',
      peakHours: '03:30 PM – 06:30 PM',
      rushLevels: [0.1, 0.3, 0.5, 0.65, 0.8, 0.9, 0.85, 0.4, 0.0],
      entryRules: 'Evening sound & light show tickets must be obtained via the local counter.',
      ticketPrice: '₹25 (Indians) / ₹300 (Foreigners)',
      bookingUrl: 'https://asi.paygov.org.in/',
      imageUrl: 'assets/images/shaniwar wada.jpg',
    ),
    HeritageSite(
      name: 'Aga Khan Palace',
      city: 'Pune',
      tag: 'Freedom Struggle Heritage Site',
      history:
          'Constructed in 1892 by Sultan Muhammed Shah Aga Khan III; served as a prison for Mahatma Gandhi, Kasturba Gandhi, and Mahadev Desai in 1942.',
      peakHours: '01:30 PM – 04:30 PM',
      rushLevels: [0.2, 0.3, 0.5, 0.7, 0.8, 0.7, 0.4, 0.2, 0.0],
      entryRules: 'Footwear removal mandatory inside the central Gandhi Samadhi memorial section.',
      ticketPrice: '₹25 (Indians) / ₹300 (Foreigners)',
      bookingUrl: 'https://asi.paygov.org.in/',
      imageUrl: 'assets/images/aga khan palace.jpg',
    ),
  ],
  'Jaipur': [
    HeritageSite(
      name: 'Amer Fort',
      city: 'Jaipur',
      tag: 'UNESCO Hill Fort of Rajasthan',
      history:
          'Built by Raja Man Singh I atop Cheel ka Teela, renowned for its artistic Hindu-style elements including the Sheesh Mahal (Mirror Palace).',
      peakHours: '10:00 AM – 03:00 PM',
      rushLevels: [0.4, 0.75, 0.9, 0.85, 0.7, 0.55, 0.4, 0.5, 0.2],
      entryRules: 'Composite monument tickets accepted. Elephant rides cease operations by 11:30 AM.',
      ticketPrice: '₹100 (Indians) / ₹500 (Foreigners)',
      bookingUrl: 'https://tourism.rajasthan.gov.in/',
      imageUrl: 'assets/images/Amer-Fort.jpg',
    ),
    HeritageSite(
      name: 'Hawa Mahal',
      city: 'Jaipur',
      tag: 'Palace of Winds (1799)',
      history:
          'Built by Maharaja Sawai Pratap Singh with 953 small casements (jharokhas) so royal women could observe street festivities unnoticed.',
      peakHours: '10:30 AM – 02:30 PM',
      rushLevels: [0.3, 0.6, 0.85, 0.9, 0.75, 0.5, 0.4, 0.2, 0.0],
      entryRules: 'Narrow staircases inside; oversized luggage must be checked at the entrance counter.',
      ticketPrice: '₹50 (Indians) / ₹200 (Foreigners)',
      bookingUrl: 'https://tourism.rajasthan.gov.in/',
      imageUrl: 'assets/images/hawa mahal palace.jpg',
    ),
  ],
};

// -------------------------------------------------------------
// UI Screen 1: City Selection
// -------------------------------------------------------------
class CitySelectionScreen extends StatelessWidget {
  const CitySelectionScreen({super.key});

  final List<String> cities = const [
    'Delhi',
    'Mumbai',
    'Agra',
    'Bengaluru',
    'Varanasi',
    'Kolkata',
    'Pune',
    'Jaipur',
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA),
      appBar: AppBar(
        title: const Text('Explore India', style: TextStyle(fontWeight: FontWeight.bold)),
        backgroundColor: const Color(0xFFFF5722),
        foregroundColor: Colors.white,
        elevation: 0,
      ),
      body: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(16),
            color: const Color(0xFFFF5722),
            child: const Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  'Cultural & Heritage Sites',
                  style: TextStyle(fontSize: 22, fontWeight: FontWeight.bold, color: Colors.white),
                ),
                SizedBox(height: 4),
                Text(
                  'Select a city to explore monuments, rush graphs, and ticket portals.',
                  style: TextStyle(fontSize: 14, color: Colors.white70),
                ),
              ],
            ),
          ),
          Expanded(
            child: ListView.builder(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              itemCount: cities.length,
              itemBuilder: (context, index) {
                final city = cities[index];
                final siteCount = heritageData[city]?.length ?? 0;

                return Card(
                  elevation: 1.5,
                  margin: const EdgeInsets.only(bottom: 12),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  child: ListTile(
                    contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                    leading: CircleAvatar(
                      backgroundColor: const Color(0xFFFF5722).withOpacity(0.12),
                      child: const Icon(Icons.location_on, color: Color(0xFFFF5722)),
                    ),
                    title: Text(
                      city,
                      style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                    ),
                    subtitle: Text('$siteCount Heritage Sites Documented'),
                    trailing: const Icon(Icons.arrow_forward_ios, size: 16, color: Colors.grey),
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => HeritageSitesListScreen(cityName: city),
                        ),
                      );
                    },
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

// -------------------------------------------------------------
// UI Screen 2: Heritage Sites List for Selected City
// -------------------------------------------------------------
class HeritageSitesListScreen extends StatelessWidget {
  final String cityName;

  const HeritageSitesListScreen({super.key, required this.cityName});

  @override
  Widget build(BuildContext context) {
    final sites = heritageData[cityName] ?? [];

    return Scaffold(
      backgroundColor: const Color(0xFFF8F9FA),
      appBar: AppBar(
        title: Text('$cityName Heritage Sites'),
        backgroundColor: const Color(0xFFFF5722),
        foregroundColor: Colors.white,
      ),
      body: sites.isEmpty
          ? const Center(child: Text('No monuments found for this city.'))
          : ListView.builder(
              padding: const EdgeInsets.all(16),
              itemCount: sites.length,
              itemBuilder: (context, index) {
                final site = sites[index];
                return Card(
                  elevation: 2,
                  margin: const EdgeInsets.only(bottom: 16),
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
                  child: InkWell(
                    borderRadius: BorderRadius.circular(12),
                    onTap: () {
                      Navigator.push(
                        context,
                        MaterialPageRoute(
                          builder: (context) => HeritageSiteDetailScreen(site: site),
                        ),
                      );
                    },
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        // Monument Thumbnail
                        ClipRRect(
                          borderRadius: const BorderRadius.only(
                            topLeft: Radius.circular(0),
                            topRight: Radius.circular(0),
                          ),
                          child: MonumentImage(
                            imageUrl: site.imageUrl,
                            monumentName: site.name,
                            height: 180,
                            isListCard: true,
                          ),
                        ),
                        Padding(
                          padding: const EdgeInsets.all(16.0),
                          child: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              Row(
                                children: [
                                  Container(
                                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                                    decoration: BoxDecoration(
                                      color: Colors.orange.shade50,
                                      borderRadius: BorderRadius.circular(6),
                                      border: Border.all(color: Colors.orange.shade200),
                                    ),
                                    child: Text(
                                      site.tag,
                                      style: TextStyle(
                                        fontSize: 12,
                                        color: Colors.orange.shade900,
                                        fontWeight: FontWeight.w600,
                                      ),
                                    ),
                                  ),
                                ],
                              ),
                              const SizedBox(height: 8),
                              Text(
                                site.name,
                                style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                              ),
                              const SizedBox(height: 6),
                              Text(
                                site.history,
                                maxLines: 2,
                                overflow: TextOverflow.ellipsis,
                                style: TextStyle(color: Colors.grey.shade700, fontSize: 13),
                              ),
                              const SizedBox(height: 12),
                              Row(
                                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                                children: [
                                  Row(
                                    children: [
                                      const Icon(Icons.access_time, size: 15, color: Colors.grey),
                                      const SizedBox(width: 4),
                                      Text(
                                        site.peakHours,
                                        style: const TextStyle(fontSize: 12, color: Colors.black87),
                                      ),
                                    ],
                                  ),
                                  const Row(
                                    children: [
                                      Text('View Details', style: TextStyle(color: Color(0xFFFF5722), fontWeight: FontWeight.bold, fontSize: 13)),
                                      Icon(Icons.chevron_right, size: 16, color: Color(0xFFFF5722)),
                                    ],
                                  ),
                                ],
                              ),
                            ],
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

// -------------------------------------------------------------
// UI Screen 3: Heritage Site Detail View
// -------------------------------------------------------------
class HeritageSiteDetailScreen extends StatelessWidget {
  final HeritageSite site;

  const HeritageSiteDetailScreen({super.key, required this.site});

  Future<void> _openBookingPortal(BuildContext context, String urlString) async {
    final uri = Uri.parse(urlString);
    try {
      final launched = await launchUrl(uri, mode: LaunchMode.platformDefault);
      if (!launched) {
        final fallbackLaunched = await launchUrl(uri, mode: LaunchMode.externalApplication);
        if (!fallbackLaunched && context.mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('Could not open portal: $urlString')),
          );
        }
      }
    } catch (e) {
      if (context.mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error launching link: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    const hoursLabels = ['9a', '10a', '11a', '12p', '1p', '2p', '3p', '4p', '5p'];

    return Scaffold(
      appBar: AppBar(
        title: Text(site.name),
        backgroundColor: const Color(0xFFFF5722),
        foregroundColor: Colors.white,
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // Monument Hero Image
            Container(
              width: double.infinity,
              height: 280,
              decoration: BoxDecoration(
                borderRadius: BorderRadius.circular(12),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.15),
                    blurRadius: 8,
                    offset: const Offset(0, 4),
                  ),
                ],
              ),
              child: ClipRRect(
                borderRadius: BorderRadius.circular(12),
                child: MonumentImage(
                  imageUrl: site.imageUrl,
                  monumentName: site.name,
                  height: 280,
                  isListCard: false,
                ),
              ),
            ),
            const SizedBox(height: 20),
            // Monument Header & Tag
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
              decoration: BoxDecoration(
                color: Colors.deepOrange.shade50,
                borderRadius: BorderRadius.circular(6),
              ),
              child: Text(
                site.tag,
                style: const TextStyle(
                  color: Color(0xFFFF5722),
                  fontWeight: FontWeight.bold,
                  fontSize: 13,
                ),
              ),
            ),
            const SizedBox(height: 8),
            Text(
              site.name,
              style: const TextStyle(fontSize: 24, fontWeight: FontWeight.bold),
            ),
            Text(
              '${site.city}, India',
              style: TextStyle(color: Colors.grey.shade600, fontSize: 14),
            ),
            const Divider(height: 28),

            // History Section
            const Text(
              'History & Architecture',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            Text(
              site.history,
              style: const TextStyle(fontSize: 14.5, height: 1.5, color: Colors.black87),
            ),
            const Divider(height: 32),

            // Peak Rush Hours Chart
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text(
                  'Peak Rush Hours',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                    color: Colors.red.shade50,
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: const Text('High traffic slots', style: TextStyle(fontSize: 11, color: Colors.red)),
                )
              ],
            ),
            const SizedBox(height: 4),
            Text(
              'Peak Window: ${site.peakHours}',
              style: TextStyle(color: Colors.grey.shade700, fontSize: 13),
            ),
            const SizedBox(height: 16),

            // Rush Graph Visualization
            Container(
              height: 140,
              padding: const EdgeInsets.only(top: 18, bottom: 8, left: 12, right: 12),
              decoration: BoxDecoration(
                color: const Color(0xFFF1F3F4),
                borderRadius: BorderRadius.circular(12),
                border: Border.all(color: Colors.grey.shade300),
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
                          color: isPeak ? const Color(0xFFE53935) : const Color(0xFFFFB74D),
                          borderRadius: BorderRadius.circular(4),
                        ),
                      ),
                      const SizedBox(height: 6),
                      Text(
                        hoursLabels[index],
                        style: const TextStyle(fontSize: 11, fontWeight: FontWeight.w500),
                      ),
                    ],
                  );
                }),
              ),
            ),
            const Divider(height: 32),

            // Entry Rules & Fee Details
            const Text(
              'Entry Rules & Booking Advisory',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 12),
            Container(
              padding: const EdgeInsets.all(12),
              decoration: BoxDecoration(
                color: Colors.amber.shade50,
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
                        child: Text(
                          site.entryRules,
                          style: const TextStyle(fontSize: 13.5, height: 1.3),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 8),
                  Row(
                    children: [
                      const Icon(Icons.confirmation_num_outlined, color: Colors.orange, size: 20),
                      const SizedBox(width: 8),
                      Expanded(
                        child: Text(
                          'Estimated Fee: ${site.ticketPrice}',
                          style: const TextStyle(fontSize: 13.5, fontWeight: FontWeight.w600),
                        ),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const SizedBox(height: 32),

            // Booking Button
            SizedBox(
              width: double.infinity,
              height: 52,
              child: ElevatedButton.icon(
                style: ElevatedButton.styleFrom(
                  backgroundColor: const Color(0xFFFF5722),
                  foregroundColor: Colors.white,
                  shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(10)),
                  elevation: 2,
                ),
                onPressed: () => _openBookingPortal(context, site.bookingUrl),
                icon: const Icon(Icons.open_in_browser),
                label: const Text(
                  'Book Official Tickets (Govt Portal)',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
              ),
            ),
            const SizedBox(height: 16),
          ],
        ),
      ),
    );
  }
}
