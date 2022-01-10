package JandD.zinify.gallery

import JandD.zinify.EditImageActivity
import JandD.zinify.R
import JandD.zinify.gallery.GalleryActivity
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
import android.widget.Button
import kotlinx.android.synthetic.main.gallery_image_preview.*


class PreviewImageFromGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_image_preview)
        val capturedUri = intent.data!!
        var imageBitmap = getImageBitmap(capturedUri)

        // Handles getting captured image displayed on preview
        val imagePreview = findViewById<ImageView>(R.id.galleryImagePreview)
        imagePreview.setImageBitmap(imageBitmap)


        // Handling edit call
        findViewById<Button>(R.id.btnEdit).setOnClickListener() {
                val intent = Intent(this,EditImageActivity::class.java)
                intent.data = capturedUri
                intent.putExtra("callerID", 2)
                startActivity(intent)
        }

        // Handling delete
        findViewById<Button>(R.id.btnDelete).setOnClickListener() {
            exitActivity(true)
        }

        // Handling exit
        findViewById<Button>(R.id.btnReturn).setOnClickListener() {
            finish()
        }

    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 11
        private val WRITE_PERMISSION = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
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

    private fun exitActivity(delete: Boolean = false) {
            if (delete) {
                val tempImg = File(intent.data?.path) // To handle later delete of temp img file
                tempImg.delete() // Delete tmp img
            }
            finish()
    }
}