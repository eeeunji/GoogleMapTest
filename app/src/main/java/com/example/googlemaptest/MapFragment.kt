package com.example.googlemaptest

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm


class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap
    private lateinit var clusterManager: ClusterManager<LatLngData> // 마커 클러스터링을 위한 ClusterManager 객체

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
        view.findViewById<Button>(R.id.btn_go_heat).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_heatFragment)
        }

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

    private fun addMarker(latitude: Double, longitude: Double, title: String?, snippet: String?, icon: Int) {
        val markerLatLng = LatLng(latitude, longitude)

        val bitmap = createDrawableFromView(requireContext(), icon)
        val customMarker = BitmapDescriptorFactory.fromBitmap(bitmap)

        val item = LatLngData(latitude, longitude, title ?: "", snippet ?: "")
        clusterManager.addItem(item)

        val markerOptions = MarkerOptions()
            .position(markerLatLng)
            .title(title)
            .snippet(snippet)
            .icon(customMarker)

//        map.addMarker(markerOptions)
    }


    //지도 객체를 사용할 수 있을 때 자동으로 호출되는 함수
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        clusterManager = ClusterManager(context, map)

        // 클러스터링 아이템을 클릭했을 때의 이벤트 처리
        map.setOnMarkerClickListener(clusterManager)

        // 마커 클릭 리스너 등록
        map.setOnInfoWindowClickListener(clusterManager)

        // 클러스터링된 아이템의 마커를 눌렀을 때의 이벤트 처리
        clusterManager.setOnClusterItemClickListener { item ->
            //클러스터링된 아이템 클릭 이벤트 처리
            true
        }

        // 클러스터링된 마커를 눌렀을 때의 이벤트 처리
        clusterManager.setOnClusterClickListener { cluster ->
            //클러스터링된 마커 클릭 이벤트 처리
            true
        }

        // 클러스터링 알고리즘 설정
        val algorithm = NonHierarchicalDistanceBasedAlgorithm<LatLngData>()
        clusterManager.algorithm = algorithm

        // 지도 카메라가 이동할 때마다 호출되는 리스너 등록
        map.setOnCameraIdleListener(clusterManager)

        // 초기 마커 추가
        addMarker(37.566, 126.978, "서울", "서울광장", R.drawable.marker_background)

        map.uiSettings.isZoomControlsEnabled = true // 확대,축소 컨트롤 활성화

        val seoul = LatLng(37.566, 126.978)
        map.moveCamera(CameraUpdateFactory.newLatLng(seoul))
        map.moveCamera(CameraUpdateFactory.zoomTo(12f))

//        addMarker(37.566, 126.978, "서울", "서울광장", R.drawable.marker_background)
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