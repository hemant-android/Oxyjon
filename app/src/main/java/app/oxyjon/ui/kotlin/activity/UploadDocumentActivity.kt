package app.oxyjon.ui.kotlin.activity

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.*
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import app.oxyjon.MainApplication
import app.oxyjon.R
import app.oxyjon.bean.UploadDocumentResponse
import app.oxyjon.database.AppSharedPreferences
import app.oxyjon.databinding.ActivityUploadDocumentBinding
import app.oxyjon.dialog.OpenGallery
import app.oxyjon.retrofit.ApiCall
import app.oxyjon.retrofit.IApiCallback
import app.oxyjon.ui.activity.BaseActivity
import app.oxyjon.utils.FunctionHelper
import app.oxyjon.utils.Helper
import app.oxyjon.utils.PermissionUtil
import app.oxyjon.utils.RealPathUtil
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.github.dhaval2404.imagepicker.ImagePicker
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import retrofit2.Response
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt


class UploadDocumentActivity : BaseActivity(), IApiCallback {
    lateinit var binding: ActivityUploadDocumentBinding
    var preferences: AppSharedPreferences? = null

    var createPdfFileResultLauncher: ActivityResultLauncher<Intent>? = null
    var createGalleryFileResultLauncher: ActivityResultLauncher<Intent>? = null
    var createCameraFileResultLauncher: ActivityResultLauncher<Intent>? = null

    private var testType = arrayOf("Medical prescription", "Lab test report")

    var type = ""
    var documentType = ""
    private var checkedItemType = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadDocumentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        requestPermissions()

        preferences = AppSharedPreferences.getInstance(this)
        binding.edtFileName.tag = ""

        binding.imgBack.setOnClickListener {
            finish()
        }

        binding.imgBrowseFile.setOnClickListener {
            OpenGallery.newInstance("upload", this).show(supportFragmentManager, "pick_image");
            /*if (checkPermission()) {

            }*/
        }

        binding.tvDocumentType.setOnClickListener {
            selectDocumentType()
        }

        binding.tvSave.setOnClickListener {
            if (TextUtils.isEmpty(binding.edtFileName.tag.toString())) {
                Toast.makeText(this, "Please select document", Toast.LENGTH_LONG).show();
            } else if (TextUtils.isEmpty(binding.edtFileName.text.toString().trim())) {
                Toast.makeText(this, "Please enter file name", Toast.LENGTH_LONG).show();
                binding.edtFileName.requestFocus()
            } else if (TextUtils.isEmpty(documentType)) {
                Toast.makeText(this, "Please select document type", Toast.LENGTH_LONG).show();
            } else {
                if (AddDiaryActivity.isConnection(this@UploadDocumentActivity)) {
                    FunctionHelper.disable_user_Intration(this,
                        resources.getString(R.string.loading))
                    ApiCall.instance.getupdatestepseven(preferences!!.getprofileid(),
                        documentType,
                        binding.edtFileName.tag.toString(),
                        binding.edtFileName.text.toString(),
                        this)
                } else {
                    Toast.makeText(this@UploadDocumentActivity,
                        "please check your internet connection",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }

        createPdfFileResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultIntent = result.data
                if (resultIntent != null) {
                    val resultIntent = result.data
                    if (resultIntent != null) {
                        val path: String =
                            Helper.getFilePathFromInputStreamUri(this, resultIntent.data!!)
                                .toString()
                        val file = File(path)
                        val file_size: Int = java.lang.String.valueOf(file.length() / 1024).toInt()
                        Log.d("filesize", "" + file_size)
                        if (file_size < 2049) {
                            Glide.with(this).load(R.drawable.pdf_logo).transform(RoundedCorners(20))
                                .into(binding.imgUpload)
                            binding.edtFileName.tag = path
                            type = "1"
                        } else {
                            Toast.makeText(this,
                                "Only less than 2MB file size allowed",
                                Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }

        createGalleryFileResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data

                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //Image Uri will not be null for RESULT_OK
                        val fileUri = data?.data!!
                        binding.edtFileName.tag = fileUri.path
                        type = "0"
                        Glide.with(this).load(fileUri.path).transform(RoundedCorners(20))
                            .into(binding.imgUpload)
                    }
                    ImagePicker.RESULT_ERROR -> {
//                        Toast.makeText(this,ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                        showSettingsDialog()
                    }
                    else -> {
                        Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        /*createGalleryFileResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val resultIntent = result.data
                if (resultIntent != null) {
                    val data = result.data
                    if (data != null) {
                        val path = data.data?.let { Helper.getRealPathForImagesURI(it, this) }
                        if (path != null) {
                            compressImage(path)
                        }
                    }
                }
            }
        }*/

        createCameraFileResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                try {
                    if (Helper.fileUri != null) {
                        binding.edtFileName.tag = RealPathUtil.getRealPath(this, Helper.fileUri!!)
                        type = "0"
                        compressImage(binding.edtFileName.tag.toString())
                    }
                } catch (exp: Exception) {
                    Log.d("Exception", "" + exp.message)
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        return if (PermissionUtil.verifyPermissions(this,
                PermissionUtil.cameraPermissions)
        ) {
            true
        } else {
            PermissionUtil.requestPermission(PermissionUtil.cameraPermissions, this)
            false
        }
    }

    private fun selectDocumentType() {
        val alertDialog = AlertDialog.Builder(this@UploadDocumentActivity)
        alertDialog.setTitle("Choose an document type")
        alertDialog.setSingleChoiceItems(testType,
            checkedItemType,
            DialogInterface.OnClickListener { dialog: DialogInterface, which: Int ->
                checkedItemType = which
                documentType = testType[which]

                documentType = if (testType[which] == "Medical prescription") {
                    "medical_prescription"
                } else if (testType[which] == "Lab test report") {
                    "lab_test_report"
                } else {
                    ""
                }

                binding.tvDocumentType.text = testType[which]
                dialog.dismiss()
            })
        alertDialog.setNegativeButton("Cancel"
        ) { dialog: DialogInterface?, which: Int -> }
        val customAlertDialog = alertDialog.create()
        customAlertDialog.show()
    }

    private fun compressImage(imageUri: String): File? {
        var scaledBitmap: Bitmap? = null
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        var bmp = BitmapFactory.decodeFile(imageUri, options)
        var actualHeight = options.outHeight
        var actualWidth = options.outWidth
        val maxHeight = 816.0f
        val maxWidth = 612.0f
        var imgRatio = (actualWidth / actualHeight).toFloat()
        val maxRatio = maxWidth / maxHeight
        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight
                actualWidth = (imgRatio * actualWidth).toInt()
                actualHeight = maxHeight.toInt()
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth
                actualHeight = (imgRatio * actualHeight).toInt()
                actualWidth = maxWidth.toInt()
            } else {
                actualHeight = maxHeight.toInt()
                actualWidth = maxWidth.toInt()
            }
        }
        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight)
        options.inJustDecodeBounds = false
        options.inPurgeable = true
        options.inInputShareable = true
        options.inTempStorage = ByteArray(16 * 1024)
        try {
            bmp = BitmapFactory.decodeFile(imageUri, options)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888)
        } catch (exception: OutOfMemoryError) {
            exception.printStackTrace()
        }
        val ratioX = actualWidth / options.outWidth.toFloat()
        val ratioY = actualHeight / options.outHeight.toFloat()
        val middleX = actualWidth / 2.0f
        val middleY = actualHeight / 2.0f
        val scaleMatrix = Matrix()
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY)
        val canvas = Canvas(scaledBitmap!!)
        canvas.setMatrix(scaleMatrix)
        canvas.drawBitmap(bmp,
            middleX - bmp.width / 2,
            middleY - bmp.height / 2,
            Paint(Paint.FILTER_BITMAP_FLAG))
        val exif: ExifInterface
        try {
            exif = ExifInterface(imageUri)
            val orientation: Int = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION, 0)
            Log.d("EXIF", "Exif: $orientation")
            val matrix = Matrix()
            when (orientation) {
                6 -> {
                    matrix.postRotate(90F)
                    Log.d("EXIF", "Exif: $orientation")
                }
                3 -> {
                    matrix.postRotate(180F)
                    Log.d("EXIF", "Exif: $orientation")
                }
                8 -> {
                    matrix.postRotate(270F)
                    Log.d("EXIF", "Exif: $orientation")
                }
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap!!, 0, 0,
                scaledBitmap.width, scaledBitmap.height, matrix,
                true)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(imageUri)
            scaledBitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, out)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val file = File(imageUri)
        val file_size = (file.length() / 1024).toString().toInt()
        Log.d("compresssize", "" + file_size.toString())
        binding.edtFileName.tag = file.absoluteFile.absolutePath
        type = "0"
        Glide.with(this).load(file.absoluteFile.absolutePath).transform(RoundedCorners(20))
            .into(binding.imgUpload)
        return file
    }

    fun getFilename(): String? {
        val file =
            File(Environment.getExternalStorageDirectory().path,
                "MyFolder/Images")
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.absolutePath + "/" + System.currentTimeMillis() + ".jpg"
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int,
    ): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val heightRatio = (height.toFloat() / reqHeight.toFloat()).roundToInt()
            val widthRatio = (width.toFloat() / reqWidth.toFloat()).roundToInt()
            inSampleSize = if (heightRatio < widthRatio) heightRatio else widthRatio
        }
        val totalPixels = (width * height).toFloat()
        val totalReqPixelsCap = (reqWidth * reqHeight * 2).toFloat()
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++
        }
        return inSampleSize
    }


    override fun onSuccess(type: Any, data: Any, extraData: Any?) {
        FunctionHelper.enableUserIntraction()
        if (type == "updatestepseven") {
            val response = data as Response<UploadDocumentResponse?>
            if (response.isSuccessful && response.body()!!.errorCode == "0") {
                Toast.makeText(this@UploadDocumentActivity,
                    response.body()!!.errorMsg,
                    Toast.LENGTH_SHORT).show()

                binding.edtFileName.tag = ""
                binding.edtFileName.text = Editable.Factory.getInstance().newEditable("")
                documentType = ""
            }
        }
    }

    override fun onFailure(data: Any) {
        FunctionHelper.enableUserIntraction()
    }

    private fun requestPermissions() {
        Dexter.withActivity(this) // below line is use to request the number of permissions which are required in our app.
            .withPermissions(Manifest.permission.CAMERA) // after adding permissions we are calling an with listener method.
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    // this method is called when all permissions are granted
                    if (multiplePermissionsReport.areAllPermissionsGranted()) {
                        // do you work now
                    }
                    // check for permanent denial of any permission
                    if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied) {
                        // permission is denied permanently, we will show user a dialog message.
                        showSettingsDialog()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest?>?,
                    permissionToken: PermissionToken,
                ) {
                    // this method is called when user grants some permission and denies some of them.
                    permissionToken.continuePermissionRequest()
                }
            }).withErrorListener { error: DexterError? ->
                // we are displaying a toast message for error message.
                Toast.makeText(applicationContext, "Error occurred! ", Toast.LENGTH_SHORT).show()
            } // below line is use to run the permissions on same thread and to check the permissions
            .onSameThread().check()
    }

    private fun showSettingsDialog() {
        // we are displaying an alert dialog for permissions
        val builder = android.app.AlertDialog.Builder(MainApplication.currentActivity)

        // below line is the title
        // for our alert dialog.
        builder.setTitle("Need Permission")

        // below line is our message for our dialog
        builder.setMessage("You need to give permission for Camera. Please go to settings-> Permission-> Camera and click allow.")
        builder.setPositiveButton("GOTO SETTINGS"
        ) { dialog, which -> // this method is called on click on positive
            // button and on clicking shit button we
            // are redirecting our user from our app to the
            // settings page of our app.
            dialog.cancel()
            // below is the intent from which we
            // are redirecting our user.
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", MainApplication.currentActivity!!.packageName, null)
            intent.data = uri
            startActivityForResult(intent, 101)
        }
        builder.setNegativeButton("Cancel"
        ) { dialog, which -> // this method is called when
            // user click on negative button.
            dialog.cancel()
        }
        // below line is used
        // to display our dialog
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 101) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestPermissions()
                }
            }
        }
    }
}