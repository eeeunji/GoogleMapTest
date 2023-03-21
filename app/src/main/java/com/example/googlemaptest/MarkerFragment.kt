package com.example.googlemaptest

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener
import com.google.android.gms.maps.GoogleMap.OnMapClickListener
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.maps.android.clustering.ClusterManager
import javax.sql.DataSource


class MarkerFragment : Fragment(), OnMapReadyCallback, OnMarkerClickListener, OnInfoWindowClickListener, OnMapClickListener {

    private lateinit var mapView: MapView
//    private val markerList = mutableListOf<LatLng>()

    lateinit var navController: NavController

//    private val DEFAULT_ZOOM_LEVEL = 17f
//    private val CITY_HALL = LatLng(37.566, 126.977)
//
//    private lateinit var googleMap: GoogleMap
//
//    private val PERMISSIONS = arrayOf(
//        Manifest.permission.ACCESS_COARSE_LOCATION,
//        Manifest.permission.ACCESS_FINE_LOCATION)

//    private val REQUEST_PERMISSION_CODE = 1

//    private fun checkPermissions(): Boolean {
//        for (permission in PERMISSIONS) {
//            if (ActivityCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED) {
//                return false
//            }
//        }
//        return true
//    }
//
//    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        initMap()
//    }
//


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

//    private fun initMap() {
//        mapView.getMapAsync {
//
//            googleMap = it
//
//            it.uiSettings.isMyLocationButtonEnabled = false
//            when {
//                checkPermissions() -> {
//                    it.isMyLocationEnabled = true
//                    getMyLocation()?.let { it1 ->
//                        CameraUpdateFactory.newLatLngZoom(
//                            it1, DEFAULT_ZOOM_LEVEL)
//                    }?.let { it2 -> it.moveCamera(it2) }
//                }
//                else -> {
//                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(CITY_HALL, DEFAULT_ZOOM_LEVEL))
//                }
//            }
//        }
//    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_marker, container, false)

        mapView = view.findViewById(R.id.markerView) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

//        if (checkPermissions()) {
//            initMap()
//        } else {
//            ActivityCompat.requestPermissions(requireActivity(), PERMISSIONS, REQUEST_PERMISSION_CODE)
//        }
//
//        view.findViewById<FloatingActionButton>(R.id.btn_my_location).setOnClickListener {
//            onMyLocationButtonClick()
//        }
        val bottomSheet = view.findViewById<View>(R.id.info_layout)
        val behavior = BottomSheetBehavior.from<View>(bottomSheet!!)

        bottomSheet.visibility = View.GONE

        view.findViewById<Button>(R.id.btn_go_map).setOnClickListener {
            navController.navigate(R.id.action_markerFragment_to_mapFragment)
        }

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("sband", "onSlide()")
            }
        })

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
//        markerList.add(markerLatLng)
//        Log.d("sband", "markerList: $markerList / markerLatLng: $markerLatLng")

        val bitmap = createDrawableFromView(requireContext(), icon)
        val customMarker = BitmapDescriptorFactory.fromBitmap(bitmap)

        val markerOptions = MarkerOptions()
            .position(markerLatLng)
            .title(title)
            .snippet(snippet)
            .icon(customMarker)

        googleMap.addMarker(markerOptions)
    }


    //지도 객체를 사용할 수 있을 때 자동으로 호출되는 함수
    override fun onMapReady(map: GoogleMap) {

        map.uiSettings.isMyLocationButtonEnabled = true // 내 위치 버튼 활성화
        map.uiSettings.isZoomControlsEnabled = true // 확대,축소 컨트롤 활성화
        map.uiSettings.isMapToolbarEnabled = false //지도 도구 모음 비활성화

        val seoul = LatLng(37.566, 126.978)
        map.moveCamera(CameraUpdateFactory.newLatLng(seoul))
        map.moveCamera(CameraUpdateFactory.zoomTo(12f))


        // 인포 윈도우 커스텀을 위한 어댑터 생성
        val adapter = CustomInfoWindowAdapter(requireContext())

        // GoogleMap 객체에 인포 윈도우 어댑터 등록
        map.setInfoWindowAdapter(adapter)

        addMarker(map,37.566, 126.978, "테스트", "안녕안녕", R.drawable.marker_background)
        addMarker(map,37.5692, 126.9784, "테스트2", "하이하이", R.drawable.maker_background_y)

        map.setOnMarkerClickListener(this)
        map.setOnInfoWindowClickListener(this)
    }

    // 마커 클릭
    override fun onMarkerClick(p0: Marker): Boolean {
        val title = p0.title
        val snippet = p0.snippet

        Toast.makeText(requireContext(), "$title $snippet", Toast.LENGTH_SHORT).show()

        if (view?.findViewById<View>(R.id.info_layout)!!.visibility == View.VISIBLE) {
            view?.findViewById<TextView>(R.id.tv_info_title)?.text = title
            view?.findViewById<TextView>(R.id.tv_info_snippet)?.text = snippet
        }

        return false
    }

    // 인포윈도우 클릭
    override fun onInfoWindowClick(p0: Marker) {
        val title = p0.title
        val snippet = p0.snippet

        view?.findViewById<TextView>(R.id.tv_info_title)?.text = title
        view?.findViewById<TextView>(R.id.tv_info_snippet)?.text = snippet

        view?.findViewById<View>(R.id.info_layout)!!.visibility = View.VISIBLE

        view?.findViewById<TextView>(R.id.tv_info_title)?.setOnClickListener {
            val bundle = Bundle()
            bundle.putString("title", title)
            bundle.putString("snippet", snippet)
            navController.navigate(R.id.action_markerFragment_to_infoFragment, bundle)

        }


//        Toast.makeText(requireContext(), "$snippet", Toast.LENGTH_SHORT).show()

    }


    override fun onMapClick(p0: LatLng) {

        Log.d("sband", "맵 클릭")
        view?.findViewById<View>(R.id.info_layout)!!.visibility = View.GONE
    }

//    private fun getMyLocation(): LatLng? {
//
//        val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
//        val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER) ?: return null
//        return LatLng(location.latitude, location.longitude)
//    }
//
//    private fun onMyLocationButtonClick() {
//        val myLocation = getMyLocation()
//        if (myLocation != null) {
//            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, DEFAULT_ZOOM_LEVEL))
//        } else Toast.makeText(requireContext(), "위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG).show()
//    }




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

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
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