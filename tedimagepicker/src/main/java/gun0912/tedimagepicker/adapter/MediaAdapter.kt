package gun0912.tedimagepicker.adapter

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView.NO_POSITION
import com.bumptech.glide.Glide
import gun0912.tedimagepicker.R
import gun0912.tedimagepicker.base.BaseSimpleHeaderAdapter
import gun0912.tedimagepicker.base.BaseViewHolder
import gun0912.tedimagepicker.builder.TedImagePickerBaseBuilder
import gun0912.tedimagepicker.builder.type.MediaType
import gun0912.tedimagepicker.databinding.ItemGalleryCameraBinding
import gun0912.tedimagepicker.databinding.ItemGalleryMediaBinding
import gun0912.tedimagepicker.model.Media
import gun0912.tedimagepicker.util.ToastUtil
import gun0912.tedimagepicker.zoom.TedImageZoomActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


internal class MediaAdapter(
    private val activity: Activity,
    private val builder: TedImagePickerBaseBuilder<*>,
) : BaseSimpleHeaderAdapter<Media>(if (builder.showCameraTile) 1 else 0) {

    internal val selectedUriList: MutableList<Uri> = mutableListOf()
    var onMediaAddListener: (() -> Unit)? = null

    private val executorService: ExecutorService = Executors.newFixedThreadPool(4)

    override fun getHeaderViewHolder(parent: ViewGroup) = CameraViewHolder(parent)
    override fun getItemViewHolder(parent: ViewGroup) = ImageViewHolder(parent)

    fun toggleMediaSelect(uri: Uri) {
        if (selectedUriList.contains(uri)) {
            removeMedia(uri)
        } else {
            addMedia(uri)
        }
    }


    private fun addMedia(uri: Uri) {
        if (selectedUriList.size == builder.maxCount) {
            val message =
                builder.maxCountMessage ?: activity.getString(builder.maxCountMessageResId)
            ToastUtil.showToast(message)
        } else {
            selectedUriList.add(uri)
            onMediaAddListener?.invoke()
            refreshSelectedView()
        }
    }

    private fun getViewPosition(it: Uri): Int =
        items.indexOfFirst { media -> media.uri == it } + headerCount


    private fun removeMedia(uri: Uri) {
        val position = getViewPosition(uri)
        selectedUriList.remove(uri)
        notifyItemChanged(position)
        refreshSelectedView()
    }

    private fun refreshSelectedView() {
        selectedUriList.forEach {
            val position: Int = getViewPosition(it)
            notifyItemChanged(position)
        }
    }

    /*private fun getImageResize(context: Context): Int {
        if (mImageResize == 0) {
            val lm: RecyclerView.LayoutManager = mRecyclerView.getLayoutManager()
            val spanCount = (lm as GridLayoutManager).spanCount
            val screenWidth = context.resources.displayMetrics.widthPixels
            val availableWidth = screenWidth - context.resources.getDimensionPixelSize(
                R.dimen.spacin
            ) * (spanCount - 1)
            mImageResize = availableWidth / spanCount
            mImageResize = (mImageResize * mSelectionSpec.thumbnailScale) as Int
        }
        return mImageResize
    }*/

    inner class ImageViewHolder(parent: ViewGroup) :
        BaseViewHolder<ItemGalleryMediaBinding, Media>(parent, R.layout.item_gallery_media) {

        init {
            binding.run {
                selectType = builder.selectType
                mediaType = builder.mediaType

                viewZoomOut.setOnClickListener {
                    val item = getItem(adapterPosition.takeIf { it != NO_POSITION }
                        ?: return@setOnClickListener)
                    if (mediaType == MediaType.VIDEO) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(item.uri, "video/*")
                        try {
                            activity.startActivity(intent)
                        } catch (e: ActivityNotFoundException) {
                            ToastUtil.showToast(activity.getString(R.string.error_no_video_activity))
                        }
                        return@setOnClickListener
                    }
                    startZoomActivity(item)
                }
                showZoom = false
            }

        }

        override fun bind(data: Media) {
            binding.run {
                media = data
                isSelected = selectedUriList.contains(data.uri)
                if (isSelected) {
                    selectedNumber = selectedUriList.indexOf(data.uri) + 1
                }

                showZoom = builder.showZoomIndicator && media is Media.Image
                showDuration = builder.showVideoDuration && media is Media.Video
                if (data is Media.Video) {
                    binding.duration = data.durationText
                }

            }
        }

        override fun recycled() {
            if (activity.isDestroyed) {
                return
            }
            Glide.with(activity).clear(binding.ivImage)
        }

        private fun startZoomActivity(media: Media) {
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity,
                binding.ivImage,
                media.uri.toString()
            ).toBundle()

            activity.startActivity(TedImageZoomActivity.getIntent(activity, media.uri), options)

        }
    }

    inner class CameraViewHolder(parent: ViewGroup) : HeaderViewHolder<ItemGalleryCameraBinding>(
        parent, R.layout.item_gallery_camera
    ) {

        init {
            binding.ivImage.setCompoundDrawablesRelativeWithIntrinsicBounds(null, ResourcesCompat.getDrawable(binding.ivImage.resources, builder.cameraTileImageResId, null), null, null)
            binding.ivImage.setCompoundDrawableTintList(
                ContextCompat.getColorStateList(
                    binding.ivImage.context,
                    R.color.ted_image_capture
                ))
            itemView.setBackgroundResource(builder.cameraTileBackgroundResId)
        }

    }

}
