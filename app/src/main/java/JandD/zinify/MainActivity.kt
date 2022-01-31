package JandD.zinify

import JandD.zinify.gallery.GalleryActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null
    private var photoTaken = false

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Request camera permissions
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        // gallery button listener
        findViewById<ImageButton>(R.id.gallery_button).setOnClickListener {
            val intent = Intent(this@MainActivity, GalleryActivity::class.java)
            startActivity(intent)
        }
        // upload button listener
        findViewById<ImageButton>(R.id.upload_button).setOnClickListener {
            pickExternalImg()
        }
        // take photo button listener
            val takePhoto = findViewById<ImageButton>(R.id.zinify_floating_btn)
            takePhoto.setOnClickListener {
                if (!photoTaken) {
                    photoTaken = true
                    takePhoto()
                }
            }
            outputDirectory = getOutputDirectory()
            cameraExecutor = Executors.newSingleThreadExecutor()
        }

        // Functions
        private fun takePhoto() {
            // Get a stable reference of the modifiable image capture use case
            val imageCapture = imageCapture ?: return

            // Create time-stamped output file to hold the image
            val photoFile = File(
                outputDirectory,
                "captured_temp.jpg")

            // Create output options object which contains file + metadata
            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

            // Set up image capture listener, which is triggered after photo has been taken
            imageCapture.takePicture(
                outputOptions, ContextCompat.getMainExecutor(this), object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val savedUri = Uri.fromFile(photoFile)
                        // Go to img preview
                        goToImagePreview(savedUri, 1)
                    }
                })
        }

        private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(camera_view.surfaceProvider)
                }
            imageCapture = ImageCapture.Builder()
                .build()
            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(this))
    }

        private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                baseContext, it) == PackageManager.PERMISSION_GRANTED
        }

        private fun getOutputDirectory(): File {
            val mediaDir = externalMediaDirs.firstOrNull()?.let {
                File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
            return if (mediaDir != null && mediaDir.exists())
                mediaDir else filesDir
        }

        private fun pickExternalImg() {
            val intentGallery = Intent(Intent.ACTION_PICK)
            intentGallery.type = "image/*"
            startActivityForResult(intentGallery, IMAGE_REQUEST_CODE)// NOTTODO za cholere nie moge tego zrobic bez tej funkcji
        }

        fun goToImagePreview(imageUri:Uri?, callerId: Int) {
            val intent = Intent(this, EditImageActivity::class.java)
            intent.data = imageUri // parse img uri to intent
            intent.putExtra("callerID",callerId)
            startActivity(intent)
        }

    override fun onResume() {
        super.onResume()
        photoTaken = false
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)// TODO depricated function
        if(requestCode == IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            intent.data = data?.data // parse img uri to intent
            goToImagePreview(intent.data, 3)
        }
    }

        override fun onDestroy() {
            super.onDestroy()
            cameraExecutor.shutdown()
        }

        companion object {
            private const val TAG = "CameraXBasic"
            private const val REQUEST_CODE_PERMISSIONS = 10
            private const val IMAGE_REQUEST_CODE = 50
            private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
            }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Toast.makeText(this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}