package com.androidarmour.android_gaming_security_library

import android.content.Context
import android.os.Bundle
import android.os.Debug
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.io.File
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.SecretKey

class AntiCheatingExample : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

fun isDeviceRooted(): Boolean {
    val paths = arrayOf(
        "/system/app/Superuser.apk",
        "/system/xbin/su",
        "/system/bin/su"
    )
    return paths.any { File(it).exists() }
}

fun detectRootWithRootBeer( context: Context): Boolean {
    val rootBeer = RootBear(context)
    return rootBeer.isRooted
}

fun detectDebugging(): Boolean {
    return Debug.isDebuggerConnected() || Debug.waitingForDebugger()
}

fun verifyGameFileIntegrity( context: Context ): Boolean {
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

fun encryptData(data: String, secretKey: SecretKey): ByteArray {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return cipher.doFinal(data.toByteArray())
}
