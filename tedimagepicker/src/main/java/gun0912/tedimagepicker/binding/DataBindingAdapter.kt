package gun0912.tedimagepicker.binding

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import gun0912.tedimagepicker.R
import gun0912.tedimagepicker.base.BaseRecyclerViewAdapter

internal class DataBindingAdapter {

    companion object {
        @BindingAdapter("imageUri")
        @JvmStatic
        fun loadImage(imageView: ImageView, uri: Uri) {
            Glide.with(imageView.context)
                .asBitmap() // some .jpeg files are actually gif
                .load(uri)
                /*.thumbnail(0.1f)
                .dontAnimate()*/
                .apply(
                    RequestOptions()
                        .override(imageView.width, imageView.height)
                        .placeholder(ColorDrawable(imageView.context.getColor(R.color.ted_image_item_placeholder)))
                        .centerCrop()
                )
                .into(imageView)
        }

        @Suppress("UNCHECKED_CAST")
        @BindingAdapter("replaceAll")
        @JvmStatic
        fun <D> replaceAll(recyclerView: RecyclerView, items: List<D>?) {
            items?.let { (recyclerView.adapter as? BaseRecyclerViewAdapter<D, *>)?.replaceAll(it) }
        }

        @Suppress("UNCHECKED_CAST")
        @BindingAdapter("replaceAll", "diffCallback")
        @JvmStatic
        fun <D> replaceAll(recyclerView: RecyclerView, items: List<D>?, diffCallback: Boolean) {
            items?.let {
                (recyclerView.adapter as? BaseRecyclerViewAdapter<D, *>)?.replaceAll(
                    it,
                    diffCallback
                )
            }
        }


        @Suppress("UNCHECKED_CAST")
        @BindingAdapter("animateTranslationY")
        @JvmStatic
        fun animateTranslationY(view: View, show: Boolean) {
            val animateValue = when (show) {
                true -> -view.y / 2
                false -> 0f
            }
            view.animate().translationY(animateValue).start()
        }

        @BindingAdapter("src")
        @JvmStatic
        fun setImageViewResource(imageView: ImageView, resId: Int?) {
            try {
                resId?.let { imageView.setImageResource(it) }
            } catch (e: Exception) {

            }

        }

        @BindingAdapter("background")
        @JvmStatic
        fun setBackgroundResource(view: View, resId: Int?) {
            try {
                resId?.let { view.setBackgroundResource(it) }
            } catch (e: Exception) {

            }
        }

    }

}
