package com.example.cashcactus.utils

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordSecurity {

    private const val ITERATIONS = 120_000
    private const val KEY_LENGTH = 256
    private const val SALT_LENGTH = 16
    private const val PREFIX = "pbkdf2"

    fun hash(password: String): String {
        val salt = ByteArray(SALT_LENGTH).also(SecureRandom()::nextBytes)
        val derived = derive(password, salt)
        return buildString {
            append(PREFIX)
            append("$")
            append(ITERATIONS)
            append("$")
            append(Base64.encodeToString(salt, Base64.NO_WRAP))
            append("$")
            append(Base64.encodeToString(derived, Base64.NO_WRAP))
        }
    }

    fun verify(password: String, storedValue: String): Boolean {
        if (!isHashed(storedValue)) {
            return storedValue == password
        }

        val parts = storedValue.split("$")
        if (parts.size != 4) return false

        val iterations = parts[1].toIntOrNull() ?: return false
        val salt = Base64.decode(parts[2], Base64.NO_WRAP)
        val expected = Base64.decode(parts[3], Base64.NO_WRAP)
        val actual = derive(password, salt, iterations)

        return expected.contentEquals(actual)
    }

    fun isHashed(value: String): Boolean = value.startsWith("$PREFIX$")

    private fun derive(
        password: String,
        salt: ByteArray,
        iterations: Int = ITERATIONS
    ): ByteArray {
        val spec = PBEKeySpec(password.toCharArray(), salt, iterations, KEY_LENGTH)
        return SecretKeyFactory
            .getInstance("PBKDF2WithHmacSHA256")
            .generateSecret(spec)
            .encoded
    }
}
