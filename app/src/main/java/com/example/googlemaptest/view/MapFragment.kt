package com.example.googlemaptest.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
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
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import dagger.hilt.android.AndroidEntryPoint
import model.PositionItem
import java.io.IOException
import java.nio.charset.Charset

@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private val locationPermissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private val viewModel by activityViewModels<MapViewModel>()

    private val gson = Gson()

    private lateinit var mapView: MapView
    private lateinit var map: GoogleMap

    // 마커 클러스터링을 위한 ClusterManager 객체
    private lateinit var clusterManager: ClusterManager<LatLngData>

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

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // 권한이 이미 허용된 경우에 실행할 코드
//            Toast.makeText(context,"이미 허용", Toast.LENGTH_SHORT).show()
        } else {
            // 권한을 요청하는 다이얼로그 표시
            requestPermissions(locationPermissions, PERMISSION_REQUEST_CODE)
        }

        view.findViewById<Button>(R.id.btn_go_marker).setOnClickListener {
//            Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_markerFragment)
        }
        view.findViewById<Button>(R.id.btn_go_heat).setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_mapFragment_to_heatFragment)
        }

        viewModel.getPosition()
        viewModel.livePosition.observe(viewLifecycleOwner, Observer {
            Log.d("sband", "observer livePosition it: $it")
        })

        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // 권한 허용됏을 때
                Toast.makeText(requireContext(), "허용", Toast.LENGTH_SHORT).show()
            } else {
                // 권한 거부햇을때
                // API 30부터 권한을 두 번 이상 거부한다면 권한 요청 대화 상자가 표시되지 않음 -> 다시 묻지 않음과 동일 (직접 사용자가 설정에서 켜줘야함)
                Toast.makeText(requireContext(), "거부, 앱 종료", Toast.LENGTH_SHORT).show()
                requireActivity().finish()

            }
        }
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

        val items = mutableListOf<LatLngData>()

//        items.add(LatLngData(37.5514963, 126.991849, "Marker 1", "Snippet 1"))
//        items.add(LatLngData(37.545631, 127.038175, "Marker 2", "Snippet 2"))
//        items.add(LatLngData(37.514234, 127.062380, "Marker 3", "Snippet 3"))
//        items.add(LatLngData(37.506072, 126.973881, "Marker 4", "Snippet 4"))
//        items.add(LatLngData(37.578334, 126.976906, "Marker 5", "Snippet 5"))

        clusterManager = ClusterManager(requireContext(), map)
        clusterManager.addItems(items)
        clusterManager.cluster()

        // 클러스터링된 아이템의 마커를 눌렀을 때의 이벤트 처리
//        clusterManager.setOnClusterItemClickListener { item ->
//            //클러스터링된 아이템 클릭 이벤트 처리
//            Log.d("sband", "클릭 item")
//            true
//        }

        // 클러스터링된 마커를 눌렀을 때의 이벤트 처리
        clusterManager.setOnClusterClickListener { cluster ->
            //클러스터링된 마커 클릭 이벤트 처리
            Log.d("sband", "클릭 cluster: ${cluster.size}개의 핀")
            true
        }

        // 클러스터링 적용
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)

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
        super.onDestroy()
        mapView.onDestroy()
    }

}