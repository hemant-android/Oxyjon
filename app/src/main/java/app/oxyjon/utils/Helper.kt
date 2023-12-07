package app.oxyjon.utils

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.net.Uri
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import app.oxyjon.R
import java.io.*

object Helper {
    var fileUri: Uri? = null
    const val IMAGE_DIRECTORY_NAME: String = "Oxyjen"
    private var file: File? = null
    fun initProgress(context: Context?): ProgressDialog {
        val dialog = ProgressDialog(context)
        dialog.setMessage(context!!.getString(R.string.loading))
        return dialog
    }

    fun getFilePathFromInputStreamUri(context: Activity, uri: Uri): String? {
        var inputStream: InputStream? = null
        var filePath: String? = null
        var fileName: String? = ""
        if (uri.authority != null) {
            try {
                inputStream = context.contentResolver.openInputStream(uri) // context needed
                val scheme: String? = uri.scheme
                if ((scheme == "file")) {
                    fileName = uri.lastPathSegment
                } else if ((scheme == "content")) {
                    val splitableuri: String = uri.toString()
                    val fileSplit: Array<String> = splitableuri.split("/").toTypedArray()
                    fileName = fileSplit[fileSplit.size - 1]
                    fileName = "$fileName.pdf"
                }

                val testFile = File(context.externalCacheDir, fileName)
                if (testFile.exists()) {
                    testFile.delete()
                }
                val photoFile: File? = createTemporalFileFrom(context, inputStream, fileName)
                filePath = photoFile!!.path
            } catch (e: FileNotFoundException) {
                // log
            } catch (e: IOException) {
                // log
            } finally {
                try {
                    inputStream!!.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return filePath
    }

    @Throws(IOException::class)
    fun createTemporalFileFrom(
        context: Activity,
        inputStream: InputStream?,
        imageFileName: String?
    ): File? {
        var targetFile: File? = null
        if (inputStream != null) {
            var read: Int
            val buffer = ByteArray(50 * 1024)
            targetFile = createTemporalFile(context, imageFileName)
            val outputStream: OutputStream = FileOutputStream(targetFile)
            while ((inputStream.read(buffer).also { read = it }) != -1) {
                outputStream.write(buffer, 0, read)
            }
            outputStream.flush()
            try {
                outputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return targetFile
    }

    private fun createTemporalFile(context: Context, filename: String?): File {
        return File(context.externalCacheDir, filename) // context needed
    }
}