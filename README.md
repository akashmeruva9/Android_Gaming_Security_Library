# AndroidArmour

## We have include the following 2 Gaming Android app security features in this respository :
- [Anti-Cheating Mechanisms](#section-1) 
- [In-App Purchase Protection](#section-2)

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


# <a name="section-2">
## 2. In-App Purchase Protection :

### 1. **Use Google Play Billing Library for Purchase Handling**

#### **Step 1: Add Google Play Billing Library**
   - Add the **Google Play Billing** dependency to your `build.gradle` file.

```groovy
implementation 'com.android.billingclient:billing:5.0.0'
```

#### **Step 2: Handle the Purchase in the App**

1. Set up **BillingClient** to handle in-app purchases.
   
```kotlin
private lateinit var billingClient: BillingClient

fun setupBillingClient(context: Context) {
    billingClient = BillingClient.newBuilder(context)
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
                for (purchase in purchases) {
                    handlePurchase(purchase)
                }
            }
        }
        .enablePendingPurchases()
        .build()
    billingClient.startConnection(object : BillingClientStateListener {
        override fun onBillingSetupFinished(billingResult: BillingResult) {
            // Ready for purchases
        }

        override fun onBillingServiceDisconnected() {
            // Try to reconnect
        }
    })
}
```

2. When a purchase is made, handle it in the `handlePurchase()` function and pass the purchase token to your server for validation.

```kotlin
fun handlePurchase(purchase: Purchase) {
    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
        val purchaseToken = purchase.purchaseToken
        // Send token to your server for validation
        validatePurchaseOnServer(purchaseToken)
    }
}
```

### 2. **Server-Side Purchase Validation**

#### **Step 1: Set Up a Server**
   - Create a server using **Node.js**, **Python**, or any preferred backend technology that communicates with **Google Play API** to validate purchases.

#### **Step 2: Validate Purchase with Google Play API**

1. Use Google’s **Play Developer API** to verify the purchase token sent from the app. This ensures the purchase was legitimate.

2. Make a **POST request** from your server to Google Play API to verify the purchase.

Example of a Node.js server-side validation:

```javascript
const {google} = require('googleapis');

const androidpublisher = google.androidpublisher('v3');

// Authenticate your server using Google API credentials
async function validatePurchase(packageName, productId, purchaseToken) {
    const auth = new google.auth.GoogleAuth({
        scopes: ['https://www.googleapis.com/auth/androidpublisher']
    });
    const client = await auth.getClient();

    const res = await androidpublisher.purchases.products.get({
        auth: client,
        packageName: packageName,
        productId: productId,
        token: purchaseToken,
    });

    return res.data; // Verify the response
}
```

### 3. **Securely Handle Purchase Tokens**
   - Ensure that **purchase tokens** are sent securely from your Android app to your server using HTTPS.
   - After server-side validation, deliver the in-app content only if the purchase is verified.

### Steps Summary:
1. **In-App Purchase (IAP) Handling**: Use Google Play Billing Library to manage purchases and capture purchase tokens.
2. **Server-Side Validation**: Send purchase tokens to your server and validate using Google Play Developer API.
3. **Secure Transaction Flow**: Ensure tokens are sent over HTTPS, and validate before delivering any game content.

</a>
