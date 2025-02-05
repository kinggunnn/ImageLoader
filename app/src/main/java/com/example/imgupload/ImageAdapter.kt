//이미지 어답터 url 가져와서 이미지 받아서 그리는 코드

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import coil3.ImageLoader
import coil3.load
import coil3.request.crossfade
import com.example.imgupload.R

class ImageAdapter(private val imageLoader: ImageLoader) : RecyclerView.Adapter<ImageAdapter.ImageViewHolder>() {
    private var imageUrls: List<String> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view,imageLoader)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        holder.bind(imageUrls[position])
    }

    override fun getItemCount(): Int = imageUrls.size

    fun submitList(newList: List<String>) {
        imageUrls = newList
        notifyDataSetChanged()//혹시몰라 넣음 데이터 변동시 다시 그리기
    }

    class ImageViewHolder(itemView: View, private val imageLoader: ImageLoader) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        fun bind(imageUrl: String) {
            imageView.load(imageUrl, imageLoader) {
                crossfade(true)
                listener(
                    onError = { _, error ->
                        Log.e("ImageLoading", "Error loading image: $imageUrl", error.throwable)
                    }
                )
            }
        }
    }
}
