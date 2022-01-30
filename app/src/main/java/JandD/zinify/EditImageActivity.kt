package JandD.zinify

import JandD.zinify.gallery.GalleryActivity
import android.content.ContentValues.TAG
import android.content.Intent
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
import java.util.*
import java.io.File as File
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.util.Base64
import android.view.View
import android.widget.*

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform


class EditImageActivity : AppCompatActivity() {


   // var btn: Button? = null
    var iv: ImageView? = null
    var iv2: ImageView? = null

    var drawable: BitmapDrawable? = null
    var bitmap: Bitmap? = null
    var imageString = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_edit)

        //var btn: Button? = null


        //btn = findViewById<View>(R.id.applyFilterBtn) as Button

        iv = findViewById<View>(R.id.imagePreview) as ImageView
        iv2 = findViewById<View>(R.id.imagePreview) as ImageView

        if (!Python.isStarted()) Python.start(AndroidPlatform(this))

        val py = Python.getInstance()

        //val pyo = py.getModule("algorithmZin")
        //val obj = pyo.callAttr("main", imageString)


        val Btn = findViewById<Button>(R.id.applyFilterBtn)
        Btn.setOnClickListener {
            drawable = iv?.getDrawable() as BitmapDrawable
            bitmap = drawable?.getBitmap()
            imageString = getStringImage(bitmap)

            //val pyo = py . getModule ("TEST");
            val pyo = py . getModule ("algorithmZin");
            val obj = pyo.callAttr("main", imageString)



            val str = obj.toString()
            val data = Base64.decode(str, Base64.DEFAULT)
            val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
            iv2!!.setImageBitmap(bmp)

        }






            // fun getStringImage(bitmap: Bitmap?): String {
              //  val baos = ByteArrayOutputStream()
               // bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
                //val imageBytes = baos.toByteArray()
              //  val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
              //  return encodedImage




           // }







        val capturedUri = intent.data!!
        val previousActivity = intent.getIntExtra("callerID", 0)
        var imageBitmap = getImageBitmap(capturedUri)

        if (previousActivity == 1) imageBitmap = rotateBitmap(imageBitmap, 90F)
        // Handles getting captured image displayed on preview
        val imagePreview = findViewById<ImageView>(R.id.imagePreview)
        imagePreview.setImageBitmap(imageBitmap)


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
        exitActivity(previousActivity,capturedUri)
    }

    private fun getStringImage(bitmap: Bitmap?): String {

        val baos = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val imageBytes = baos.toByteArray()
        val encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        return encodedImage

    }


    override fun onBackPressed() {
        super.onBackPressed()
        exitActivity(intent.getIntExtra("callerID", 0),intent.data!!)
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

    private fun exitActivity(previousActivity: Int, uri: Uri) {
        val backBtn = findViewById<Button>(R.id.return_button)
        backBtn.setOnClickListener{
            if(previousActivity== 0 || previousActivity == 1) {
                val tempImg = File(uri.path) // To handle later delete of temp img file
                tempImg.delete() // Delete tmp img
            }
            // handles going back to previous activity
            val intent = when (previousActivity) {
                2 -> Intent(this@EditImageActivity, GalleryActivity::class.java)
                else -> Intent(this@EditImageActivity, MainActivity::class.java)
            }
            startActivity(intent)
        }
    }
}