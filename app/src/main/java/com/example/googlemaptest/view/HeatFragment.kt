package com.example.googlemaptest.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.googlemaptest.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import com.google.maps.android.heatmaps.WeightedLatLng
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray

@AndroidEntryPoint
class HeatFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView

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

        return view
    }

    private fun createDrawableFromView(context: Context, customIcon: Int): Bitmap {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val markerView = inflater.inflate(R.layout.marker_layout, null)

        // 배경 설정
        val textView = markerView.findViewById<TextView>(R.id.tv_marker)
        textView.setBackgroundResource(customIcon)

        markerView.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        markerView.layout(0, 0, markerView.measuredWidth, markerView.measuredHeight)
        val bitmap = Bitmap.createBitmap(markerView.measuredWidth, markerView.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        markerView.draw(canvas)


        return bitmap
    }

    private fun addMarker(googleMap: GoogleMap, latitude: Double, longitude: Double, title: String?, snippet: String?, icon: Int) {
        val markerLatLng = LatLng(latitude, longitude)

        val bitmap = createDrawableFromView(requireContext(), icon)
        val customMarker = BitmapDescriptorFactory.fromBitmap(bitmap)

        val markerOptions = MarkerOptions()
            .position(markerLatLng)
            .title(title)
            .snippet(snippet)
            .icon(customMarker)

        googleMap.addMarker(markerOptions)
    }

//    weightedData(data: Collection<WeightedLatLng>): HeatmapTileProvider.Builder - heatmap을 생성할 가중치를 가진 위치 데이터를 지정합니다.
//    radius(radius: Int): HeatmapTileProvider.Builder - heatmap 내 데이터의 크기를 지정합니다.
//    opacity(opacity: Double): HeatmapTileProvider.Builder - heatmap의 투명도를 조절합니다.
//    maxIntensity(maxIntensity: Double): HeatmapTileProvider.Builder - heatmap 내 가장 높은 밀도의 데이터의 밀도 값을 설정합니다.
//    gradient(gradient: Gradient): HeatmapTileProvider.Builder - heatmap의 색상을 지정합니다.
//    build(): HeatmapTileProvider - 설정된 속성으로 heatmap을 생성합니다.
//    setGradient(gradient: Gradient): Unit - heatmap의 색상을 변경합니다.
//    setWeightedData(data: Collection<WeightedLatLng>): Unit - heatmap을 생성할 가중치를 가진 위치 데이터를 지정합니다.
//    setRadius(radius: Int): Unit - heatmap 내 데이터의 크기를 지정합니다.
//    setMaxIntensity(maxIntensity: Double): Unit - heatmap 내 가장 높은 밀도의 데이터의 밀도 값을 설정합니다.
//    setOpacity(opacity: Double): Unit - heatmap의 투명도를 조절합니다.
//    clearTileCache(): Unit - HeatmapTileProvider가 생성한 모든 tile cache를 제거합니다.

    // 지도 객체를 사용할 수 있을 때 자동으로 호출되는 함수
    override fun onMapReady(map: GoogleMap) {

        map.uiSettings.isZoomControlsEnabled = true // 확대,축소 컨트롤 활성화

        map.setOnMapLongClickListener {
            Log.d("sband", "latitude: ${it.latitude} , longitude: ${it.longitude}")
        }

        map.setOnCameraMoveListener {
            Log.d("sband", "움직인다")
        }

        val data = generateHeatMapData()

        // Create the gradient with whatever start and end colors you wish to use
        val colors = intArrayOf(Color.YELLOW, Color.WHITE)
        val startPoints = floatArrayOf(0.2f, 1f)
        val gradient = Gradient(colors, startPoints)

        val heatMapProvider = HeatmapTileProvider.Builder()
            .weightedData(data) // 히트맵을 생성할 가중치를 가진 위치 데이터를 지정
            .radius(50) // 히트맵 내 데이터의 크기를 지정. 값은 픽셀 단위로 표시되며 기본값은 20. 10~50 사이
            .maxIntensity(1000.0) // 히트맵 내 가장 높은 밀도의 데이터의 밀도값을 설정
            .opacity(0.8) // 투명도 (0~1, 기본 0.7)
            .gradient(gradient) // 히트맵 색상 변경
            .build()

        map.addTileOverlay(TileOverlayOptions().tileProvider(heatMapProvider))

        // 인도 중앙에 카메라 배치
        val indiaLatLng = LatLng(20.5937, 78.9629)
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(indiaLatLng, 5f))

    }

    private fun generateHeatMapData(): ArrayList<WeightedLatLng> {
        val data = ArrayList<WeightedLatLng>()

        val jsonData = getJsonDataFromAsset("district_data.json")
        jsonData?.let {
            for (i in 0 until it.length()) {
                val entry = it.getJSONObject(i)
                val lat = entry.getDouble("lat")
                val lon = entry.getDouble("lon")
                val density = entry.getDouble("density")

                if (density != 0.0) {
                    val weightedLatLng = WeightedLatLng(LatLng(lat, lon), density)
                    data.add(weightedLatLng)
                }
            }
        }

        return data
    }

    private fun getJsonDataFromAsset(fileName: String): JSONArray? {
        try {
            val jsonString = requireContext().assets.open(fileName).bufferedReader().use { it.readText() }
            return JSONArray(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
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