package model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PositionItem(
    val lat: Double,
    val lng: Double,
    val title: String,
    val snippet: String,
) : Parcelable