# 🚀 Exploring Ktor with Kotlin Multiplatform Developer Setup Notes

## ✅ Required Software Versions

### 🧰 Android Studio
- **Required Version**: Android Studio *Narwhal 2025.1.1 Patch 1* or later  
  Build: `#AI-251.25410.109.2511.13752376` (built on July 8, 2025)
- **Required Plugin**: Kotlin Multiplatform Plugin  
  ID: `com.jetbrains.kmm 0.9-251.25410-AS-69`
- **Enable K2 Mode**: Go to Kotlin plugin settings and enable **K2**

### ☕ Java
- **Required Version**: Java 17 (**mandatory**)
- ⚠️ Java 21 (bundled with Android Studio) is **not supported**

---

## 🛠️ Java 17 Setup Instructions

### 📦 Install via Homebrew (Recommended)
```bash
brew install openjdk@17

# Add to your shell config
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@17"' >> ~/.zshrc
source ~/.zshrc

# Verify
java -version
# Expected: openjdk version "17.x.x"


⸻

🧩 Configure Android Studio to Use Java 17

Option 1: Gradle JVM Settings
	•	Preferences → Build, Execution, Deployment → Build Tools → Gradle
	•	Set Gradle JVM to /opt/homebrew/opt/openjdk@17 or manually browse to it

Option 2: Project Structure
	•	File → Project Structure → SDK Location
	•	Set JDK Location to Java 17 path
✅ Apply and OK

Option 3: gradle.properties

org.gradle.java.home=/opt/homebrew/opt/openjdk@17


⸻

⚙️ Android Studio Configuration

🔧 Essential Settings
	•	Switch to Project view (not “Android” view)
📁 Project Tool Window → Top dropdown → Select “Project”
	•	Enable K2 mode in the Kotlin plugin
	•	Allocate 2048M+ memory for KMP work

🖥️ Platform Requirements
	•	macOS: Required for iOS development
	•	Windows: Supported for Android only

⸻

🍏 macOS: iOS Development Requirements
	•	Install Xcode via App Store
	•	Install Xcode Command Line Tools:

xcode-select --install



⸻

🌐 Server Development Setup

▶️ Running the Ktor Server
	1.	Navigate to server/src/main/kotlin/com/kmp/explore/Application.kt
	2.	Right-click → Run or Debug ‘ApplicationKt’
	3.	Rename run configuration to "Run Server":
	•	Right-click config → Edit Configurations → Rename

✅ Verify Server
	•	Server URL: http://localhost:8080
	•	Use Insomnia or Postman to test endpoints

⸻

🧹 Build Troubleshooting

🧼 Common Clean Commands

./gradlew clean
./gradlew build

# iOS-specific cleanup
rm -rf ~/.konan
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64

🧽 Fix Android Studio Cache Issues
	1.	File → Invalidate Caches / Restart
	2.	File → Sync Project with Gradle Files
	3.	Restart Android Studio if red squiggles persist

⸻

🧪 Testing & Development Workflow

🔄 Server Development
	•	Start the server using the Run Server config
	•	Confirm server logs show a successful start
	•	Ensure Android app builds and connects

🧾 Sample API Test

curl http://localhost:8080/health


⸻

🌐 Network Configuration for Mobile Devices

🔍 Find Your Local IP

On macOS

# Try primary interface (Wi-Fi usually en0)
ifconfig en0 | grep "inet " | awk '{print $2}'

# Fallback
ifconfig | grep -A1 "flags.*UP" | grep "inet " | grep -v 127.0.0.1

Or go to: System Settings → Network → Your connection

On Windows

ipconfig | findstr "IPv4"

Or go to: Settings → Network & Internet → Properties

⸻

📲 Update Project Files with IP

Replace YOUR_IP_ADDRESS in:
	•	ServerConfig.kt:

const val WORKING_SERVER = "http://YOUR_IP_ADDRESS:8080"


	•	AndroidManifest.xml:
	•	Add <uses-permission android:name="android.permission.INTERNET" />
	•	Reference network_security_config.xml
	•	network_security_config.xml:
	•	Allow cleartext traffic for your IP
	•	Info.plist (iOS):
	•	Add NSAppTransportSecurity if using HTTP

⸻

🔗 Platform-Specific Access URLs

Platform	URL	Purpose
Android Emulator	http://10.0.2.2:8080	Special host alias for emulator
iOS Simulator	http://YOUR_IP:8080	Use your Mac’s IP
Physical Devices	http://YOUR_IP:8080	Must be on the same Wi-Fi network
JVM/Desktop	http://localhost:8080	Localhost is fine


⸻

🧪 Test Network Setup
	1.	Start the server
	2.	Run from another device:

curl http://YOUR_IP_ADDRESS:8080/api/admin/db-status

	3.	Launch mobile apps — confirm they connect

⸻

🐞 Common Network Pitfalls
	•	Android Emulator: Use 10.0.2.2, not your actual IP
	•	iOS Simulator: Ensure ServerConfig.kt has the correct IP
	•	Physical Devices: Must be on the same Wi-Fi as the dev machine
	•	Firewall: Allow traffic on port 8080
	•	Android: HTTP image loading requires network_security_config.xml

