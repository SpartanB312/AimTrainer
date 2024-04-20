package net.spartanb312.everett.utils.encryption

interface Encryptor {
    fun encode(input: String, key: String): String
    fun decode(input: String, key: String): String
    fun String.processKey(): String
}