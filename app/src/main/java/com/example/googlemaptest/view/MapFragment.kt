package com.example.googlemaptest.view

import android.Manifest
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.example.googlemaptest.LatLngData
import com.example.googlemaptest.viewmodel.MapViewModel
import com.example.googlemaptest.R
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import dagger.hilt.android.AndroidEntryPoint
import model.PositionItem

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val viewModel by activityViewModels<MapViewModel>()

    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap

    // 마커 클러스터링을 위한 ClusterManager 객체
    private lateinit var clusterManager: ClusterManager<LatLngData>

    // 현재 내 위치
    private var myCircle: Circle? = null
    private var myMarker: Marker? = null
    private lateinit var locationManager: LocationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("sband", "onCreateView()")
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        locationManager = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager

        mapView = view.findViewById(R.id.mapView) as MapView
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 이미 허용된 경우
//            Toast.makeText(context,"이미 허용", Toast.LENGTH_SHORT).show()
        } else {
            // 권한을 요청하는 다이얼로그 표시
            requestLocationPermission.launch(locationPermissions)
        }

        view.findViewById<Button>(R.id.btn_go_marker).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_markerFragment)
        }
        view.findViewById<Button>(R.id.btn_go_heat).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_heatFragment)
        }
        view.findViewById<Button>(R.id.btn_now_location).setOnClickListener {
            getMyLocation(requireContext())
        }

        mapView.getMapAsync { googleMap ->
            map = googleMap
            viewModel.getPosition()
            viewModel.livePosition.observe(viewLifecycleOwner, Observer {
                Log.d("sband", "observer livePosition it: $it")
                createMarker(map, it)
            })
        }

        return view
    }

    private fun createMarker(googleMap: GoogleMap, positionItem: List<PositionItem>) {
        val clusterItem = mutableListOf<LatLngData>()

        for (item in positionItem) {
            Log.d("sband", "item: $item")

            val cItem = LatLngData(item.lat, item.lng, item.title, item.snippet)
            clusterItem.add(cItem)
        }
        Log.d("sband", "clusterItem: $clusterItem")

        clusterManager.addItems(clusterItem) // ClusterManager에 마커 추가
        clusterManager.cluster()
    }

    private val requestLocationPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true && permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
                Toast.makeText(requireContext(), "허용", Toast.LENGTH_SHORT).show()
            } else {
                // API 30부터 권한을 두 번 이상 거부한다면 권한 요청 대화 상자가 표시되지 않음
                Toast.makeText(requireContext(), "거부, 앱 종료", Toast.LENGTH_SHORT).show()
                requireActivity().finish()
            }
        }

    private fun getMyLocation(context: Context): Location? {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0f, myLocationListener)
        }
        return null
    }

    private var myLocationListener: LocationListener = object : LocationListener {
        // 위치가 업데이트될 때마다 호출됨
        override fun onLocationChanged(location: Location) {
            Log.d("sband", "location : $location")
            removeMyLocation()
            showMyLocation(location)
        }
        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}

    }

    private fun showMyLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions().position(latLng)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        myMarker = map.addMarker(markerOptions)

        // 반경 추가
        val circleOptions = CircleOptions()
            .radius(500.0)
            .strokeColor(Color.BLUE)
            .center(LatLng(location.latitude, location.longitude))
            .fillColor(Color.argb(35,  0, 0, 255))

        myCircle = map.addCircle(circleOptions)

        // 지도 이동
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }

    private fun removeMyLocation() {
        myMarker?.remove()
        myMarker = null
        myCircle?.remove()
        myCircle = null
    }

    //지도 객체를 사용할 수 있을 때 자동으로 호출되는 함수
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true // 확대,축소 컨트롤 활성화

        val seoul = LatLng(37.5514963, 126.991849)
        map.moveCamera(CameraUpdateFactory.newLatLng(seoul))
        map.moveCamera(CameraUpdateFactory.zoomTo(10f))

        // 클러스터링 초기화
        clusterManager = ClusterManager(requireContext(), map)
        clusterManager.algorithm = NonHierarchicalDistanceBasedAlgorithm<LatLngData>()
        clusterManager.renderer = DefaultClusterRenderer(requireContext(), map, clusterManager)

        // 클러스터링된 마커를 눌렀을 때의 이벤트 처리
        clusterManager.setOnClusterClickListener { cluster ->
            //클러스터링된 마커 클릭 이벤트 처리
            Log.d("sband", "클릭 cluster: ${cluster.size}개의 핀")
            true
        }

        // 맵 클릭하면 현재 위치 표시 지우기
        map.setOnMapClickListener {
            removeMyLocation()
            locationManager.removeUpdates(myLocationListener)
            Log.d("sband", "setOnMapClickListener()")
        }

        // 클러스터링 적용
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

    }

    override fun onStart() {
        Log.d("sband", "onStart()")
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        Log.d("sband", "onStop()")
        super.onStop()
        mapView.onStop()
    }

    override fun onResume() {
        Log.d("sband", "onResume()")
        super.onResume()
        mapView.onResume()
        removeMyLocation()
    }

    override fun onPause() {
        Log.d("sband", "onPause()")
        super.onPause()
        mapView.onPause()
        locationManager.removeUpdates(myLocationListener)
    }

    // 앱이 일시정지되거나 다시 시작될 때 MapView의 상태를 저장하고 복원하는 데 중요
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        Log.d("sband", "onDestroy()")
        super.onDestroy()
        mapView.onDestroy()
    }

}