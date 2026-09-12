import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:provider/provider.dart';

import '../lib/main.dart';

void main() {
  testWidgets('Home tab shows Explore India feature section', (tester) async {
    await tester.pumpWidget(
      MultiProvider(
        providers: [
          ChangeNotifierProvider(create: (_) => UserProvider()),
          ChangeNotifierProvider(create: (_) => LanguageProvider()),
        ],
        child: const MaterialApp(home: HomeTab()),
      ),
    );

    expect(find.text('Explore India'), findsWidgets);
  });

  testWidgets('Monument image falls back to local asset when remote URL fails', (tester) async {
    await tester.pumpWidget(
      const MaterialApp(
        home: MonumentImage(
          imageUrl: 'https://example.invalid/monument.jpg',
          monumentName: 'Taj Mahal',
        ),
      ),
    );

    expect(find.text('Taj Mahal'), findsOneWidget);
    expect(find.byType(Image), findsWidgets);

    final imageWidget = tester.widget<Image>(find.byType(Image));
    expect(imageWidget.image, isA<AssetImage>());
  });
}
