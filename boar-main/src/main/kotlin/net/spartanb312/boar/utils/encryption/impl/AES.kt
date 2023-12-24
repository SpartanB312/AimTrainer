package net.spartanb312.boar.utils.encryption.impl

import net.spartanb312.boar.utils.encryption.Encryptor
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

object AES : Encryptor {

    private const val STANDARD_NAME = "AES"
    private const val SPARTAN_KEY = "spartanb312isgod"

    override fun encode(input: String, key: String): String = Cipher.getInstance(STANDARD_NAME).let {
        val secretKeySpec = SecretKeySpec(key.processKey().toByteArray(), STANDARD_NAME)
        it.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val result = it.doFinal(input.toByteArray())
        String(Base64.getEncoder().encode(result))
    }

    override fun decode(input: String, key: String): String = Cipher.getInstance(STANDARD_NAME).let {
        val secretKeySpec = SecretKeySpec(key.processKey().toByteArray(), STANDARD_NAME)
        it.init(Cipher.DECRYPT_MODE, secretKeySpec)
        val result = it.doFinal(Base64.getDecoder().decode(input))
        String(result)
    }

    override fun String.processKey(): String = when {
        this.length < 16 -> {
            this + SPARTAN_KEY.substring(this.length)
        }
        this.length > 16 -> {
            this.substring(0, 8) + this.substring(this.length - 8, this.length)
        }
        else -> this
    }

}