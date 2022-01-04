package JandD.zinify.zinGalleryAdapter

import JandD.zinify.R
import android.content.Context
import android.net.Uri
import android.service.autofill.Dataset
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import javax.sql.DataSource

class gridGalleryAdapter (
    private val dataset: List<Uri>
    ): RecyclerView.Adapter<gridGalleryAdapter.ImageViewHolder>() {
        class ImageViewHolder(view: View?): RecyclerView.ViewHolder(view!!) {
            val zinImg: ImageView = view!!.findViewById(R.id.image)
        }

        override fun getItemCount(): Int {
            return dataset.size
        }

        override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
            val item = dataset[position]
            holder.zinImg.setImageURI(dataset[position])
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.zingallery_image, parent, false)
        return ImageViewHolder(adapterLayout)
    }
}