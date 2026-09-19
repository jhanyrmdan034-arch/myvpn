import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import 'package:flutter_v2ray_client/flutter_v2ray_client.dart';

void main() {
  runApp(const MyVpnApp());
}

class MyVpnApp extends StatefulWidget {
  const MyVpnApp({super.key});

  @override
  State<MyVpnApp> createState() => _MyVpnAppState();
}

class _MyVpnAppState extends State<MyVpnApp> {
  String _currentLanguage = 'fa'; // زبان پیش‌فرض: فارسی

  void _changeLanguage(String lang) {
    setState(() {
      _currentLanguage = lang;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      theme: ThemeData.dark().copyWith(
        scaffoldBackgroundColor: const Color(0xFF070B1E), // رنگ پس‌زمینه دارک تصویر
        primaryColor: const Color(0xFF2952FF),
      ),
      home: SplashScreen(
        currentLanguage: _currentLanguage,
        onLanguageChange: _changeLanguage,
      ),
    );
  }
}

// ۱. صفحه لودینگ اولیه (Splash Screen)
class SplashScreen extends StatefulWidget {
  final String currentLanguage;
  final Function(String) onLanguageChange;
  const SplashScreen({super.key, required this.currentLanguage, required this.onLanguageChange});

  @override
  State<SplashScreen> createState() => _SplashScreenState();
}

class _SplashScreenState extends State<SplashScreen> {
  @override
  void initState() {
    super.initState();
    Future.delayed(const Duration(seconds: 3), () {
      Navigator.pushReplacement(
        context,
        MaterialPageRoute(
          builder: (context) => LanguageAndPrivacyScreen(
            currentLanguage: widget.currentLanguage,
            onLanguageChange: widget.onLanguageChange,
          ),
        ),
      );
    });
  }

  @override
  Widget build(BuildContext context) {
    return const Scaffold(
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.bolt, size: 80, color: Color(0xFF2952FF)),
            SizedBox(height: 20),
            Text(
              'SWITCH VPN',
              style: TextStyle(fontSize: 26, fontWeight: FontWeight.bold, letterSpacing: 3, color: Colors.white),
            ),
            SizedBox(height: 10),
            CircularProgressIndicator(color: Color(0xFF2952FF)),
          ],
        ),
      ),
    );
  }
}

// ۲. صفحه انتخاب زبان و حریم خصوصی در اولین ورود
class LanguageAndPrivacyScreen extends StatefulWidget {
  final String currentLanguage;
  final Function(String) onLanguageChange;
  const LanguageAndPrivacyScreen({super.key, required this.currentLanguage, required this.onLanguageChange});

  @override
  State<LanguageAndPrivacyScreen> createState() => _LanguageAndPrivacyScreenState();
}

class _LanguageAndPrivacyScreenState extends State<LanguageAndPrivacyScreen> {
  bool _isAccepted = false;

  Map<String, String> _getTexts() {
    if (widget.currentLanguage == 'fa') {
      return {
        'title': 'انتخاب زبان و حریم خصوصی',
        'privacy': 'با استفاده از این اپلیکیشن، شما با قوانین حریم خصوصی و امنیت داده‌های ما موافقت می‌کنید. اطلاعات شما به هیچ وجه ذخیره یا فروخته نخواهد شد.',
        'btn': 'موافقم و ادامه',
      };
    } else {
      return {
        'title': 'Language & Privacy',
        'privacy': 'By using this app, you agree to our Privacy Policy and data security. Your information will never be stored or sold.',
        'btn': 'Agree & Continue',
      };
    }
  }

  @override
  Widget build(BuildContext context) {
    var texts = _getTexts();
    return Scaffold(
      body: Padding(
        padding: const EdgeInsets.symmetric(horizontal: 24.0, vertical: 60.0),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.spaceEvenly,
          children: [
            Text(texts['title']!, style: const TextStyle(fontSize: 22, fontWeight: FontWeight.bold)),
            Row(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                ElevatedButton(
                  onPressed: () => widget.onLanguageChange('fa'),
                  style: ElevatedButton.styleFrom(backgroundColor: widget.currentLanguage == 'fa' ? const Color(0xFF2952FF) : Colors.grey[800]),
                  child: const Text('فارسی'),
                ),
                const SizedBox(width: 20),
                ElevatedButton(
                  onPressed: () => widget.onLanguageChange('en'),
                  style: ElevatedButton.styleFrom(backgroundColor: widget.currentLanguage == 'en' ? const Color(0xFF2952FF) : Colors.grey[800]),
                  child: const Text('English'),
                ),
              ],
            ),
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(color: const Color(0xFF131834), borderRadius: BorderRadius.circular(15)),
              child: Text(texts['privacy']!, style: const TextStyle(color: Colors.white70, height: 1.5), textAlign: TextAlign.center),
            ),
            Row(
              children: [
                Checkbox(
                  value: _isAccepted,
                  activeColor: const Color(0xFF2952FF),
                  onChanged: (val) => setState(() => _isAccepted = val!),
                ),
                Text(widget.currentLanguage == 'fa' ? 'قوانین را قبول دارم' : 'I accept the terms'),
              ],
            ),
            ElevatedButton(
              onPressed: _isAccepted
                  ? () => Navigator.pushReplacement(
                        context,
                        MaterialPageRoute(
                          builder: (context) => VpnScreen(
                            currentLanguage: widget.currentLanguage,
                            onLanguageChange: widget.onLanguageChange,
                          ),
                        ),
                      )
                  : null,
              style: ElevatedButton.styleFrom(
                minimumSize: const Size(double.infinity, 55),
                backgroundColor: const Color(0xFF2952FF),
                shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(30)),
              ),
              child: Text(texts['btn']!, style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            ),
          ],
        ),
      ),
    );
  }
}

// ۳. صفحه اصلی وی‌پی‌ان
class VpnScreen extends StatefulWidget {
  final String currentLanguage;
  final Function(String) onLanguageChange;
  const VpnScreen({super.key, required this.currentLanguage, required this.onLanguageChange});

  @override
  State<VpnScreen> createState() => _VpnScreenState();
}

class _VpnScreenState extends State<VpnScreen> {
  final FlutterV2ray _v2ray = FlutterV2ray();
  String _vpnStatus = "Disconnected";
  List<dynamic> _serverList = [];
  Map<String, dynamic>? _selectedServer;
  bool _isLoadingServers = false;

  // اصلاح لینک گیت‌هاب شما برای دریافت زنده
  final String _githubUrl = "https://githubusercontent.com";

  @override
  void initState() {
    super.initState();
    _initVpn();
  }

  void _initVpn() async {
    await _v2ray.initializeV2ray();
    _v2ray.v2rayStatusStream.listen((status) {
      setState(() => _vpnStatus = status.state);
    });
    _fetchServers();
  }

  Future<void> _fetchServers() async {
    setState(() => _isLoadingServers = true);
    try {
      final response = await http.get(Uri.parse(_githubUrl));
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['status'] == 'success') {
          setState(() {
            _serverList = data['servers'];
            if (_serverList.isNotEmpty) _selectedServer = _serverList[0];
          });
        }
      }
    } catch (e) {
      // خطا هندل شد
    } finally {
      setState(() => _isLoadingServers = false);
    }
  }

  void _toggleVpn() async {
    if (_selectedServer == null) return;
    if (!await _v2ray.requestPermission()) return;
    if (_vpnStatus == "Connected") {
      await _v2ray.stopV2Ray();
    } else {
      setState(() => _vpnStatus = "Connecting...");
      await _v2ray.startV2Ray(
        remark: _selectedServer!['country'], 
        config: _selectedServer!['config'], 
        proxyMode: false,
      );
    }
  }

  void _showSettingsMenu() {
    bool isFa = widget.currentLanguage == 'fa';
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(isFa ? 'تنظیمات برنامه' : 'App Settings'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              title: const Text('فارسی'),
              leading: Radio<String>(
                value: 'fa',
                groupValue: widget.currentLanguage,
                onChanged: (val) {
                  widget.onLanguageChange('fa');
                  Navigator.pop(context);
                },
              ),
            ),
            ListTile(
              title: const Text('English'),
              leading: Radio<String>(
                value: 'en',
                groupValue: widget.currentLanguage,
                onChanged: (val) {
                  widget.onLanguageChange('en');
                  Navigator.pop(context);
                },
              ),
            ),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    bool isFa = widget.currentLanguage == 'fa';
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Colors.transparent,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.settings, color: Colors.white),
            onPressed: _showSettingsMenu,
          ),
        ],
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text(

