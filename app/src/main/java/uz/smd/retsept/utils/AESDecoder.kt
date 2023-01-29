package uz.smd.retsept.utils

import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Created by Siddikov Mukhriddin on 9/20/21
 */
fun decryptMessage(encrypted: String, password: String, iv: String): String {
    val dst = decrypt(encrypted, password, iv)
    val str = String(dst, StandardCharsets.UTF_8)
    Log.e("TTT", "encrypted = $encrypted")
    Log.e("TTT", "password = $password")
    Log.e("TTT", "decrypted = $str")
    return str
}


fun decrypt(encrypted: String, password: String, iv: String): ByteArray {
    val ivAndCipherText = Base64.decode(encrypted, Base64.NO_WRAP)
    val IV = IvParameterSpec(iv.toByteArray());
    val keySpec = SecretKeySpec(password.toByteArray(StandardCharsets.UTF_8), "AES")
    val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, keySpec, IV)
    return cipher.doFinal(ivAndCipherText)
}

fun decrypt(encrypted: String, password: String): ByteArray {
    val ivAndCipherText = Base64.decode(encrypted, Base64.NO_WRAP)
    val keySpec = SecretKeySpec(password.toByteArray(StandardCharsets.UTF_8), "AES")
    val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.DECRYPT_MODE, keySpec)
    return cipher.doFinal(ivAndCipherText)
}