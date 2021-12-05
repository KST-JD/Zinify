package JandD.zinify

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class CapturedImgActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.image_edit)
        // Handles getting captured image displayed on preview
        val capturedUri = intent.data
        findViewById<ImageView>(R.id.imagePreview).setImageURI(capturedUri)

        // Handling img saving
        val saveBtn = findViewById<ImageButton>(R.id.safeBtn)
        saveBtn.setOnClickListener {
            val savedUri = intent.data
            val msg = "jd"
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            //Log.d(TAG, msg)
        }
        // Handling filtering

        /* Tutaj wchodzimy z twoim kodem :) */

        // Handling exit
        val backBtn = findViewById<ImageButton>(R.id.returnBtn)
        backBtn.setOnClickListener {
            val intent = Intent(this@CapturedImgActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }


}