package app.oxyjon.dialog

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import app.oxyjon.R
import app.oxyjon.ui.kotlin.activity.UploadDocumentActivity
import app.oxyjon.utils.Helper
import butterknife.ButterKnife
import butterknife.OnClick
import com.github.dhaval2404.imagepicker.ImagePicker
import java.io.File
import java.util.*


class OpenGallery : DialogFragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val builder: StrictMode.VmPolicy.Builder = StrictMode.VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        setStyle(STYLE_NO_TITLE, R.style.AppTheme)
        //        View view=inflater.inflate(R.layout.dialog_pick_image,container,false);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window!!
            .setBackgroundDrawable(ColorDrawable(Color.parseColor("#B3000000")))
        val view: View = inflater.inflate(R.layout.cam_layout, container, false)
        ButterKnife.bind(this, view)
        val btn_file: TextView = view.findViewById(R.id.btn_file)
        val tvFileView: TextView = view.findViewById(R.id.tvFileView)
        if (navType.equals("profile", ignoreCase = true)) {
            btn_file.visibility = View.GONE
            tvFileView.visibility = View.GONE
        } else {
            btn_file.visibility = View.VISIBLE
            tvFileView.visibility = View.VISIBLE
        }
        return view
    }

    @RequiresApi(Build.VERSION_CODES.N)
    @OnClick(R.id.btn_cam)
    fun callCamera() {
        dismiss()
        captureImage()
    }

    @OnClick(R.id.btn_file)
    fun callFile() {
        dismiss()
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        if (activity is UploadDocumentActivity) {
            (activity as UploadDocumentActivity?)!!.createPdfFileResultLauncher!!.launch(intent)
        }
    }

    @OnClick(R.id.btn_gallery)
    fun callGallery() {
        dismiss()
        /*val intent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (activity is UploadDocumentActivity) {
            (activity as UploadDocumentActivity?)!!.createGalleryFileResultLauncher!!.launch(
                intent)
        }*/
        ImagePicker.with(this).cropSquare().galleryOnly()
            .setImageProviderInterceptor { imageProvider -> // Intercept ImageProvider
                Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
            }.setDismissListener {
                Log.d("ImagePicker", "Dialog Dismiss")
            }.maxResultSize(1080,
                1080)  //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                (activity as UploadDocumentActivity?)!!.createGalleryFileResultLauncher!!.launch(
                    intent)
            }
    }

    @OnClick(R.id.btn_close)
    fun closeDialog() {
        dismiss()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun captureImage() {
        /*val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        Helper.fileUri = outputMediaFileUri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Helper.fileUri)
        if (activity is UploadDocumentActivity) {
            (activity as UploadDocumentActivity?)!!.createCameraFileResultLauncher!!.launch(
                intent)
        }*/

        ImagePicker.with(this).cropSquare().cameraOnly()
            .setImageProviderInterceptor { imageProvider -> // Intercept ImageProvider
                Log.d("ImagePicker", "Selected ImageProvider: " + imageProvider.name)
            }.setDismissListener {
                Log.d("ImagePicker", "Dialog Dismiss")
            }.maxResultSize(1080,
                1080)  //Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                (activity as UploadDocumentActivity?)!!.createGalleryFileResultLauncher!!.launch(
                    intent)
            }

    }

    private val outputMediaFileUri: Uri
        @RequiresApi(Build.VERSION_CODES.N)
        get() {
            return Uri.fromFile(outputMediaFile)
        }

    companion object {
        const val PICKFILE_REQUEST_CODE: Int = 8888
        var PICK_IMAGE_FROM_GALLERY: Int = 9090
        var PICK_IMAGE_FROM_CAMERA: Int = 1010
        var navType: String = "upload"
        var activity: Activity? = null
        fun newInstance(type: String, mActivity: Activity?): OpenGallery {
            navType = type
            activity = mActivity
            return OpenGallery()
        }

        // Create the storage directory if it does not exist
        private val outputMediaFile: File?
            @RequiresApi(Build.VERSION_CODES.N) private get() {
                val mediaStorageDir: File = File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), Helper.IMAGE_DIRECTORY_NAME)
                // Create the storage directory if it does not exist
                if (!mediaStorageDir.exists()) {
                    if (!mediaStorageDir.mkdirs()) {
                        Log.d(Helper.IMAGE_DIRECTORY_NAME,
                            "Oops! Failed create " + Helper.IMAGE_DIRECTORY_NAME + " directory")
                        return null
                    }
                }
                val timeStamp: String =
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(
                        Date())
                return File(mediaStorageDir.path + File.separator + "IMG_" + timeStamp + ".jpg")
            }
    }
}