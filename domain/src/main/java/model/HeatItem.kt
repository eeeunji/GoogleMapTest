package model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class HeatItem(
    val district: String,
    val hq: String,
    val density: Double,
    val lat: Double,
    val lon: Double
) : Parcelable