package JandD.zinify

import android.content.ContentValues.TAG
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.*
import java.lang.Exception
import java.io.File as File
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import android.widget.*

import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.android.material.slider.Slider


class EditImageActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_edit)

        var sigma: Float = 50F // <-- To wrzucasz w filtr
        val capturedUri = intent.data!!
        val previousActivity = intent.getIntExtra("callerID", 0)
        var imageBitmap = if (previousActivity == 1) { // Bo android sobie to obraca (pewnie przez to że to linux)
            rotateBitmap(getImageBitmap(capturedUri), 90F)
        } else
            getImageBitmap(capturedUri)

        val scalingScale = 4
        imageBitmap = Bitmap.createScaledBitmap(imageBitmap, imageBitmap.width/scalingScale, imageBitmap.height/scalingScale, true)
        var originalImgBitmap = imageBitmap

        if (previousActivity == 1) {
            rotateBitmap(imageBitmap, 90F)
        }
        // Handles getting captured image displayed on preview
        val imagePreview = findViewById<ImageView>(R.id.imagePreview)
        imagePreview.setImageBitmap(imageBitmap)

        // Chaquopy init
        if (!Python.isStarted()) Python.start(AndroidPlatform(this))
        val py = Python.getInstance()


        val btn = findViewById<Button>(R.id.applyFilterBtn)
        btn.setOnClickListener {
            // Tutaj zmieniłem żeby brało bitmape z zmiennej którą już mamy żeby uniknąć 2 zmiennych z bitmapami na raz
            val imageString = getStringImage(originalImgBitmap)

            //val pyo = py . getModule ("TEST");
            val pyo = py . getModule ("algorithmZin");
            val obj = pyo.callAttr("main", imageString)



            val str = obj.toString()
            val data = Base64.decode(str, Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
            imageBitmap = bmp
            imagePreview.setImageBitmap(bmp)
        }

        // Handling img saving
        val saveBtn = findViewById<Button>(R.id.save_button)
        saveBtn.setOnClickListener {
            if (writePermissionsGranted()) {
                saveImg(imageBitmap)
                finish()
            } else {         // Request write permissions
                ActivityCompat.requestPermissions(
                    this, WRITE_PERMISSION, REQUEST_CODE_PERMISSIONS
                )
            }
        }

        val sigmaSlider = findViewById<Slider>(R.id.sigma_slider)
        sigmaSlider.addOnChangeListener { _, value, _ ->
            sigma = value
            Log.d(TAG, "onCreate: $sigma")
        }

        // Image rotating
        findViewById<ImageView>(R.id.rotateLeftBtn).setOnClickListener {
            imageBitmap = rotateBitmap(imageBitmap, 90F)
            originalImgBitmap = rotateBitmap(originalImgBitmap, 90F)
            imagePreview.setImageBitmap(imageBitmap)
        }
        findViewById<ImageView>(R.id.rotateRightBtn).setOnClickListener {
            imageBitmap = rotateBitmap(imageBitmap, -90F)
            imagePreview.setImageBitmap(imageBitmap)
            originalImgBitmap = rotateBitmap(originalImgBitmap, -90F)
        }

        // Handling exit
        exitActivity()
    }

    private fun getStringImage(bitmap: Bitmap?): String {

        val baos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val imageBytes = baos.toByteArray()
        return Base64.encodeToString(imageBytes, Base64.DEFAULT)

    }


    override fun onBackPressed() {
        super.onBackPressed()
        exitActivity()
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
                getString(R.string.image_folder_name)
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

    private fun exitActivity() {
        val backBtn = findViewById<Button>(R.id.return_button)
        backBtn.setOnClickListener{
            finish()
        }
    }
}