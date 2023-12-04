package gun0912.tedimagepicker.builder.type

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
enum class MediaType : Parcelable {
    IMAGE,
    VIDEO,
    IMAGE_AND_VIDEO,
    ;
}
