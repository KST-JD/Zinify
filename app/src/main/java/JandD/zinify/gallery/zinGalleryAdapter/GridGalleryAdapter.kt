package JandD.zinify.gallery.zinGalleryAdapter

import JandD.zinify.EditImageActivity
import JandD.zinify.R
import JandD.zinify.gallery.GalleryActivity
import JandD.zinify.gallery.PreviewImageFromGallery
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView

class GridGalleryAdapter (
    private val dataset: List<Uri>
    ): RecyclerView.Adapter<GridGalleryAdapter.ImageViewHolder>() {
    class ImageViewHolder(view: View?) : RecyclerView.ViewHolder(view!!) {
        val zinImg: ImageView = view!!.findViewById(R.id.image)
    }

    override fun getItemCount(): Int {
        return dataset.size
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = dataset[position]
        holder.zinImg.setImageURI(item)

        holder.zinImg.setOnClickListener() {
            val intent = Intent(holder.zinImg.context,PreviewImageFromGallery::class.java)
            intent.data = dataset[position]// parse img uri to intent
            holder.zinImg.context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.zingallery_image, parent, false)
        return ImageViewHolder(adapterLayout)
    }
}