package com.example.cashcactus.utils
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import android.util.Base64

class CryptoManager {

    private val keyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }

    private val alias = "vault_key"

    private fun getKey(): SecretKey {
        val existingKey = keyStore.getKey(alias, null)
        return if (existingKey != null) {
            existingKey as SecretKey
        } else {
            val keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )

            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .build()
            )

            keyGenerator.generateKey()
        }
    }

    fun encrypt(data: String): Pair<String, String> {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val iv = cipher.iv
        val encrypted = cipher.doFinal(data.toByteArray())

        return Pair(
            Base64.encodeToString(encrypted, Base64.DEFAULT),
            Base64.encodeToString(iv, Base64.DEFAULT)
        )
    }

    fun decrypt(encryptedData: String, iv: String): String {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")

        val spec = GCMParameterSpec(
            128,
            Base64.decode(iv, Base64.DEFAULT)
        )

        cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)

        val decoded = cipher.doFinal(
            Base64.decode(encryptedData, Base64.DEFAULT)
        )

        return String(decoded)
    }
}