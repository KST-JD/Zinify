package JandD.zinify.gallery

import JandD.zinify.MainActivity
import JandD.zinify.R
import JandD.zinify.gallery.zinGalleryAdapter.GridGalleryAdapter
import android.content.ContentValues.TAG
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class GalleryActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery_view)

        val imageList = loadImagesUri()

        //
        val gallery = findViewById<RecyclerView>(R.id.grid_recycler_view)
        gallery.adapter = GridGalleryAdapter(imageList)
    }

    // Handles updating gallery content when deleting images
    override fun onResume() {
        super.onResume()
        val imageList = loadImagesUri()
        val gallery = findViewById<RecyclerView>(R.id.grid_recycler_view)
        gallery.adapter = GridGalleryAdapter(imageList)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun loadImagesUri(): List<Uri> {
        val directory = File(
            baseContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            getString(R.string.image_folder_name))
        val imageUriList = mutableListOf<Uri>()
        if (directory.exists())
        {
            directory.walk().forEach {
                imageUriList.add(it.normalize().toUri())
            }
         }else {
            findViewById<TextView>(R.id.galleryText).text = getString(R.string.gallery_empty_info)
            //TODO(handling empty app gallery)
         }
        Log.w(TAG, "loadImagesUri: $imageUriList")
        imageUriList.removeAt(0)
        return imageUriList
    }
}