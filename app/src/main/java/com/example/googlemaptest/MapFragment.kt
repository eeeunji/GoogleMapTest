package com.example.googlemaptest

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        mapView = view.findViewById(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        view.findViewById<Button>(R.id.btn_go_marker).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_markerFragment)
        }

        return view
    }

    //지도 객체를 사용할 수 있을 때 자동으로 호출되는 함수
    override fun onMapReady(map: GoogleMap) {

        val seoul = LatLng(37.566, 126.978)
        map.moveCamera(CameraUpdateFactory.newLatLng(seoul))
        map.moveCamera(CameraUpdateFactory.zoomTo(12f))
        val marker =
            MarkerOptions()
                .position(seoul)
                .title("서울")
                .snippet("기본 마커")
        map.addMarker(marker)
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

}