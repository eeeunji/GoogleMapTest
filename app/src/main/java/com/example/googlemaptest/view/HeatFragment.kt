package com.example.googlemaptest.view

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.googlemaptest.R
import com.example.googlemaptest.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import dagger.hilt.android.AndroidEntryPoint
import model.HeatItem

@AndroidEntryPoint
class HeatFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap

    private val heatData = ArrayList<WeightedLatLng>()

    private val viewModel by activityViewModels<MapViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_heat, container, false)

        mapView = view.findViewById(R.id.heatView) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        view.findViewById<Button>(R.id.btn_go_map).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_heatFragment_to_mapFragment)
        }

        mapView.getMapAsync { googleMap ->
            map = googleMap
            viewModel.liveHeat.observe(viewLifecycleOwner, Observer {
                Log.d("sband", "observer liveHeat it: $it")
                createHeat(map, it)
            })
            viewModel.getHeatData()
        }

        return view
    }

    // 지도 객체를 사용할 수 있을 때 자동으로 호출되는 함수
    override fun onMapReady(map: GoogleMap) {

        map.uiSettings.isZoomControlsEnabled = true // 확대,축소 컨트롤 활성화

        val indiaLatLng = LatLng(20.5937, 78.9629)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(indiaLatLng, 5f))

    }

    private fun createHeat(googleMap: GoogleMap, heatItem: List<HeatItem>) {
        heatData.addAll(heatItem.map { WeightedLatLng(LatLng(it.lat, it.lon), it.density) })
        val heatMapProvider = HeatmapTileProvider.Builder()
            .weightedData(heatData)
            .radius(50)
            .maxIntensity(1000.0) // 히트맵 내 가장 높은 밀도의 데이터의 밀도값을 설정
            .opacity(0.8) // 투명도
            .gradient(Gradient(intArrayOf(Color.YELLOW, Color.WHITE),floatArrayOf(0.2f, 1f)))
            .build()

        googleMap.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))
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