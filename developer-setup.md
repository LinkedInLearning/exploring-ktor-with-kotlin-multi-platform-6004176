### macOS Additional Setup (for iOS development)
- **Xcode**: Latest version installed from App Store
- **Xcode Command Line Tools**: `xcode-select --install`# KMP Course Developer Setup Notes

## Required Software Versions

### Android Studio
- **Required**: Android Studio Narwhal 2025.1.1 Patch 1 or later
- Build #AI-251.25410.109.2511.13752376 (built on July 8, 2025)
- **Required Plugin**: Kotlin Multiplatform Plugin (com.jetbrains.kmm 0.9-251.25410-AS-69)
- Enable K2 mode in Kotlin plugin settings

### Java Version Requirements
- **Project Requirement**: Java 17 (mandatory)
- **Note**: Java 21 is bundled with Android Studio but will NOT work correctly with this project
- **Must use Java 17** for proper Kotlin Multiplatform compilation

## Java 17 Setup Instructions

### Install Java 17 via Homebrew (Recommended)
```bash
# Install Java 17
brew install openjdk@17

# Add to your shell profile (.zshrc, .bash_profile, etc.)
echo 'export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"' >> ~/.zshrc
echo 'export JAVA_HOME="/opt/homebrew/opt/openjdk@17"' >> ~/.zshrc

# Reload your shell
source ~/.zshrc

# Verify installation
java -version
# Should show: openjdk version "17.x.x"
```

### Configure Android Studio to Use Java 17

#### Method 1: Gradle JVM Settings
1. Open Android Studio
2. Go to **Preferences** → **Build, Execution, Deployment** → **Build Tools** → **Gradle**
3. Set **Gradle JVM** to:
    - `/opt/homebrew/opt/openjdk@17` (if installed via Homebrew)
    - Or browse to your Java 17 installation path

#### Method 2: Project Structure Settings
1. **File** → **Project Structure** → **SDK Location**
2. Set **JDK Location** to Java 17 path
3. Click **Apply** and **OK**

#### Method 3: gradle.properties (Backup)
Add to your project's `gradle.properties`:
```properties
org.gradle.java.home=/opt/homebrew/opt/openjdk@17
```

## Android Studio Configuration

### Essential Settings
- **View**: Switch to **Project view** (NOT Android view)
    - In Project tool window, select "Project" from dropdown at top
    - Provides complete project structure visibility
- **Kotlin Plugin**: Ensure K2 mode is enabled
- **Memory**: Recommended 2048M+ for KMP projects

### Platform Requirements
- **macOS**: Required for iOS development and testing
- **Windows**: Fully supported for Android development (iOS development not available)

## Server Development Setup

### Running the Ktor Server
1. Navigate to: `server/src/main/kotlin/com/kmp/explore/Application.kt`
2. Right-click on the file
3. Choose either:
    - **"Run 'ApplicationKt'"** (normal execution)
    - **"Debug 'ApplicationKt'"** (with debugging)
4. This creates a run configuration named "ApplicationKt"
5. **Recommended**: Rename to "Run Server" for clarity
    - Right-click configuration → **Edit Configurations** → Rename

### Server Verification
- Server runs on: `http://localhost:8080`
- Use Insomnia/Postman for API testing

## Build Troubleshooting

### Common Issues
```bash
# Clean build if issues occur
./gradlew clean
./gradlew build

# iOS-specific clean (if needed)
rm -rf ~/.konan
./gradlew :composeApp:linkDebugFrameworkIosSimulatorArm64
```

### Android Studio Cache Issues
1. **File** → **Invalidate Caches and Restart**
2. **File** → **Sync Project with Gradle Files**
3. Restart Android Studio if red squiggles persist

## Testing & Development Workflow

### Server Development
1. Start server: Run "Run Server" configuration
2. Verify: Server starts and shows startup logs
3. Verify: Android app builds and runs

### API Testing Examples
```bash
# Basic server health check (once endpoints are implemented)
curl http://localhost:8080/health
```

### Development Tips
- Use Project view for better file navigation
- Keep server running during development