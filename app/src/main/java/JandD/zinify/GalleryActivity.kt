package JandD.zinify

import JandD.zinify.zinGalleryAdapter.gridGalleryAdapter
import android.net.Uri
import android.os.Bundle
import android.os.Environment
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
        gallery.adapter = gridGalleryAdapter(imageList)
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
            //TODO()
         }
        return imageUriList
    }
}