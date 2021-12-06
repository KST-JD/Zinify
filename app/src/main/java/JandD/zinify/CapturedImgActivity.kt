package JandD.zinify

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.*
import java.util.*

class CapturedImgActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_edit)
        // Handles getting captured image displayed on preview
        val capturedUri = intent.data
        val tempImg = File(intent.data?.path)
        val imagePreview = findViewById<ImageView>(R.id.imagePreview)
        imagePreview.setImageURI(capturedUri)

        // Handling img saving


        val saveBtn = findViewById<ImageButton>(R.id.safeBtn)
        saveBtn.setOnClickListener {
            if (writePermissionsGranted()) {
                val appName = resources.getString(R.string.app_name)
                val time = Calendar.getInstance().time.toString()
                val newImg = File(appName)
                //newImg.createNewFile()
                Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show()
                //tempImg.copyTo(newImg)
            } else {
                // Request write permissions
                ActivityCompat.requestPermissions(
                    this, CapturedImgActivity.WRITE_PERMISSION, CapturedImgActivity.REQUEST_CODE_PERMISSIONS
                )
            }
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

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }
    private fun writePermissionsGranted() = CapturedImgActivity.WRITE_PERMISSION.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

}