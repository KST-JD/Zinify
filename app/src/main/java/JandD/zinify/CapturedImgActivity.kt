package JandD.zinify

import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.*
import java.lang.Exception
import java.util.*
import java.io.File as File
import android.graphics.BitmapFactory
import android.graphics.Matrix


class CapturedImgActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_edit)
        val capturedUri = intent.data
        val tempImg = File(capturedUri!!.path) // To handle later delete of temp img file
        var imageBitmap = getImageBitmap(capturedUri)
        imageBitmap = rotateBitmap(imageBitmap, 90F)
        // Handles getting captured image displayed on preview
        val imagePreview = findViewById<ImageView>(R.id.imagePreview)
        imagePreview.setImageBitmap(imageBitmap)


        // Handling img saving
        val saveBtn = findViewById<ImageButton>(R.id.safeBtn)
        saveBtn.setOnClickListener {
            if (writePermissionsGranted()) {
                saveImg(imageBitmap)
            } else {         // Request write permissions
                ActivityCompat.requestPermissions(
                    this, WRITE_PERMISSION, REQUEST_CODE_PERMISSIONS
                )
            }
        }

        // Image rotating
        findViewById<ImageView>(R.id.rotateLeftBtn).setOnClickListener {
            imageBitmap = rotateBitmap(imageBitmap, 90F)
            imagePreview.setImageBitmap(imageBitmap)
        }
        findViewById<ImageView>(R.id.rotateRightBtn).setOnClickListener {
            imageBitmap = rotateBitmap(imageBitmap, -90F)
            imagePreview.setImageBitmap(imageBitmap)
        }

        // Handling exit
        val backBtn = findViewById<ImageButton>(R.id.returnBtn)
        backBtn.setOnClickListener {
            tempImg.delete()
            val intent = Intent(this@CapturedImgActivity, MainActivity::class.java)
            startActivity(intent)
            tempImg.delete()
        }

    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 11
        private val WRITE_PERMISSION = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    private fun writePermissionsGranted() = WRITE_PERMISSION.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun saveImg(imgToSave: Bitmap): Uri? {
        return try {
            val directory = File(
                baseContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                "saved ZIN_images"
            )
            if(!directory.exists()) {
                directory.mkdirs()
            }
            val fileName = "ZIN_${System.currentTimeMillis()}.jpg"
            Log.d(TAG, "saveImg: $directory")
            val img = File(directory, fileName)
            with(FileOutputStream(img)) {
                imgToSave.compress(Bitmap.CompressFormat.JPEG, 100, this)
                flush()
                close()
            }
            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show()
            FileProvider.getUriForFile(baseContext, "${baseContext.packageName}.provider", img)
        } catch (exception: Exception) {
            null
        }
    }

    private fun rotateBitmap(source: Bitmap, angle: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }

    private fun getImageBitmap(imageUri: Uri): Bitmap {
        var imageStream: InputStream? = null
        try {
            imageStream = contentResolver.openInputStream(
                imageUri
            )
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        val imageBitmap = BitmapFactory.decodeStream(imageStream)
        try {
            imageStream!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return imageBitmap
    }
}