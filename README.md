# AndroidArmour

We have include the following 2 Social Media Android app security features in this respository :
- [Anti-Cheating Mechanisms](#section-1)  - 
- [Transaction Encryption](#section-2)

# <a name="section-1">
## 1.Anti-Cheating Mechanisms :

### 1. **Detect Cheating Tools Using Root and Debugging Detection**

Cheating tools often operate on **rooted devices** or during **debugging** sessions to modify the game’s behavior. You can implement detection mechanisms for rooted devices and prevent debugging.

#### **Step 1: Detect Rooted Devices**
   - Use a root detection library or implement a basic root detection technique.

```kotlin
fun isDeviceRooted(): Boolean {
    val paths = arrayOf(
        "/system/app/Superuser.apk",
        "/system/xbin/su",
        "/system/bin/su"
    )
    return paths.any { File(it).exists() }
}
```

- You can also use popular libraries like **RootBeer** for advanced detection.

```kotlin
// Add RootBeer dependency in your build.gradle
implementation 'com.scottyab:rootbeer-lib:0.0.8'

fun detectRootWithRootBeer(): Boolean {
    val rootBeer = RootBeer(context)
    return rootBeer.isRooted
}
```

#### **Step 2: Detect Debugging Attempts**
   - Prevent users from running the game in debug mode using the `isDebuggerConnected()` method.

```kotlin
fun detectDebugging(): Boolean {
    return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
}
```

### 2. **Memory and Code Tampering Detection**

Cheating tools often modify the memory or code to alter game logic (e.g., speed hacks or score manipulation). Implement memory and code integrity checks.

#### **Step 1: Use Checksum Verification for Integrity**
   - Store a hash of critical game files or code at compile-time and validate the hash at runtime.

```kotlin
fun verifyGameFileIntegrity(): Boolean {
    val expectedHash = "your_precomputed_hash"  // Hash calculated during development
    val gameFile = File(context.filesDir, "game_binary")
    val fileHash = calculateFileHash(gameFile)
    return fileHash == expectedHash
}

fun calculateFileHash(file: File): String {
    val digest = MessageDigest.getInstance("SHA-256")
    file.inputStream().use { fis ->
        val buffer = ByteArray(1024)
        var read = fis.read(buffer)
        while (read != -1) {
            digest.update(buffer, 0, read)
            read = fis.read(buffer)
        }
    }
    return digest.digest().joinToString("") { "%02x".format(it) }
}
```

#### **Step 2: Detect Memory Modification**
   - Monitor specific memory areas for unauthorized modifications (using native code, JNI) or use commercial anti-cheating SDKs like **BattlEye** or **Xigncode3**.

### 3. **Obfuscation and Encryption**

#### **Step 1: Use Code Obfuscation**
   - Use **ProGuard** or **R8** to obfuscate code to prevent reverse engineering.

```groovy
// Enable ProGuard in your build.gradle
minifyEnabled true
proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
```

#### **Step 2: Encrypt Critical Data**
   - Encrypt important game data, such as scores or player stats, using **AES** encryption.

```kotlin
fun encryptData(data: String, secretKey: SecretKey): ByteArray {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return cipher.doFinal(data.toByteArray())
}
```

### 4. **Use Anti-Cheating SDKs**

   - Integrate third-party anti-cheating tools like:
     - **Play Integrity API** for detecting compromised devices.
     - **BattlEye** and **Xigncode3** for real-time protection against cheating tools.

### Steps Summary:
1. **Root and Debug Detection**: Detect rooted devices and active debuggers.
2. **Memory and Code Integrity**: Use file checksum and memory integrity checks.
3. **Obfuscation and Encryption**: Protect code with ProGuard and encrypt sensitive data.
4. **Use Anti-Cheat SDKs**: Integrate SDKs like BattlEye or Xigncode3 for real-time protection.

</a>


