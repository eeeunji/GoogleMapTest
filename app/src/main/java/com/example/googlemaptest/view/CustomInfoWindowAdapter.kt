package com.example.googlemaptest.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.googlemaptest.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CustomInfoWindowAdapter(private val context: Context) : GoogleMap.InfoWindowAdapter {

    val cView = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null)

    private fun customInfoWindow(marker: Marker, view: View) {
        val titleView = view.findViewById<TextView>(R.id.tv_title)
        val snippetView = view.findViewById<TextView>(R.id.tv_snippet)

        titleView.text = marker.title
        snippetView.text = marker.snippet

    }

    override fun getInfoWindow(marker: Marker): View? {
        customInfoWindow(marker, cView)
        return cView
    }

    override fun getInfoContents(marker: Marker): View {
        customInfoWindow(marker, cView)
        return cView
    }
}