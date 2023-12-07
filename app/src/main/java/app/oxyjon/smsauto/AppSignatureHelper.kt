package app.oxyjon.smsauto

import android.content.Context
import android.content.ContextWrapper
import android.content.pm.PackageManager
import android.content.pm.Signature
import android.util.Base64.*
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*
import kotlin.collections.ArrayList


class AppSignatureHelper constructor(context: Context?) :
    ContextWrapper(context) {// Get all package signatures for the current package

    // For each signature create a compatible hash
    /**
     * Get all the app signatures for the current package
     *
     * @return
     */
    val appSignatures: ArrayList<String?>
        get() {
            val appCodes: ArrayList<String?> = ArrayList()
            try {
                // Get all package signatures for the current package
                val packageName: String = packageName
                val packageManager: PackageManager = packageManager
                val signatures: Array<Signature> = packageManager.getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES).signatures

                // For each signature create a compatible hash
                for (signature: Signature in signatures) {
                    val hash: String? = hash(packageName, signature.toCharsString())
                    if (hash != null) {
                        appCodes.add(String.format("%s", hash))
                    }
                }
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, "Unable to find package to obtain hash.", e)
            }
            return appCodes
        }

    companion object {
        val TAG: String = AppSignatureHelper::class.java.simpleName
        private val HASH_TYPE: String = "SHA-256"
        val NUM_HASHED_BYTES: Int = 9
        val NUM_BASE64_CHAR: Int = 11
        private fun hash(packageName: String, signature: String): String? {
            val appInfo: String = "$packageName $signature"
            try {
                val messageDigest: MessageDigest = MessageDigest.getInstance(HASH_TYPE)
                messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
                var hashSignature: ByteArray? = messageDigest.digest()

                // truncated into NUM_HASHED_BYTES
                hashSignature = Arrays.copyOfRange(hashSignature, 0, NUM_HASHED_BYTES)
                // encode into Base64
                var base64Hash: String =
                    encodeToString(hashSignature, NO_PADDING or NO_WRAP)
                base64Hash = base64Hash.substring(0, NUM_BASE64_CHAR)
                Log.d(TAG, String.format("pkg: %s -- hash: %s", packageName, base64Hash))
                return base64Hash
            } catch (e: NoSuchAlgorithmException) {
                Log.e(TAG, "hash:NoSuchAlgorithm", e)
            }
            return null
        }
    }
}