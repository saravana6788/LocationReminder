package com.udacity.project4.locationreminders.savereminder.selectreminderlocation


import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.udacity.project4.R
import com.udacity.project4.base.BaseFragment
import com.udacity.project4.databinding.FragmentSelectLocationBinding
import com.udacity.project4.locationreminders.savereminder.SaveReminderViewModel
import com.udacity.project4.utils.setDisplayHomeAsUpEnabled
import org.koin.android.ext.android.inject
import java.util.*

class SelectLocationFragment : BaseFragment(),OnMapReadyCallback {

    //Use Koin to get the view model of the SaveReminder
    override val _viewModel: SaveReminderViewModel by inject()
    private lateinit var binding: FragmentSelectLocationBinding
    private lateinit var map:GoogleMap
    private lateinit var locationClient:FusedLocationProviderClient
    private lateinit var selectedMarker:Marker
    private lateinit var poi:PointOfInterest

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_location, container, false)

        binding.viewModel = _viewModel
        binding.lifecycleOwner = this

        setHasOptionsMenu(true)
        setDisplayHomeAsUpEnabled(true)

//        TODO: add the map setup implementation
//        TODO: zoom to the user location after taking his permission
//        TODO: add style to the map
//        TODO: put a marker to location that the user selected
        locationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        val supportMapFragment = childFragmentManager.findFragmentById(R.id.select_map_fragment) as SupportMapFragment
        supportMapFragment.getMapAsync(this)

//        TODO: call this function after the user confirms on the selected location
        binding.saveButton.setOnClickListener {
            onLocationSelected()
        }


        return binding.root
    }

    private fun onLocationSelected() {
        //        TODO: When the user confirms on the selected location,
        //         send back the selected location details to the view model
        //         and navigate back to the previous fragment to save the reminder and add the geofence
        /*if (this::selectedMarker.isInitialized) {
            _viewModel.latitude.value = selectedMarker.position.latitude
            _viewModel.longitude.value = selectedMarker.position.longitude
            _viewModel.reminderSelectedLocationStr.value = selectedMarker.title
            findNavController().popBackStack()
        }*/

        if (this::poi.isInitialized){
            _viewModel.latitude.value = poi.latLng.latitude
            _viewModel.longitude.value = poi.latLng.longitude
            _viewModel.reminderSelectedLocationStr.value = poi.name
            _viewModel.selectedPOI.value = poi
            findNavController().popBackStack()
        }


    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_options, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        // TODO: Change the map type based on the user's selection.
        R.id.normal_map -> {
            map.mapType = GoogleMap.MAP_TYPE_NORMAL
            true
        }
        R.id.hybrid_map -> {
            map.mapType = GoogleMap.MAP_TYPE_HYBRID
            true
        }
        R.id.satellite_map -> {
            map.mapType = GoogleMap.MAP_TYPE_SATELLITE
            true
        }
        R.id.terrain_map -> {
            map.mapType = GoogleMap.MAP_TYPE_TERRAIN
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getCurrentLocation()
        setPOIClick()
            //onLongClick()


           /* map.addMarker(
                MarkerOptions()
                    .position(defaultLocation)
                    .title("Marker in Current location")
            )
        */
    }

    private fun setPOIClick(){
        map.setOnPoiClickListener { it ->
            map.clear()
            poi = it

            val poiMarker = map.addMarker(
                MarkerOptions()
                    .position(poi.latLng)
                    .title(poi.name)
            )

            map.addCircle(
                CircleOptions()
                    .center(poi.latLng)
                    .radius(200.0)
                    .strokeColor(Color.argb(255,255,0,0))
                    .fillColor(Color.argb(64,255,0,0)).strokeWidth(4F)

            )

            poiMarker.showInfoWindow()


        }
    }



    /*private fun onLongClick(){
        map.setOnMapLongClickListener {
            map.clear()
            val snippet = String.format(
                Locale.getDefault(),
                "Lat: %1\$.5f, Long: %2\$.5f\",",it.latitude,
                it.longitude
            )
            val longClickMarker = map.addMarker(
                MarkerOptions()
                .position(it)
                    .title("Dropped Pin")
                    .snippet(snippet)
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            )

            longClickMarker.showInfoWindow()
            selectedMarker = longClickMarker

        }
    }*/



    private fun getCurrentLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())
        try {
            if (isLocationPermissionGranted()) {
                map.isMyLocationEnabled = true
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()){
                    if(it.isSuccessful){
                        if(it.result != null)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(it.result.latitude,it.result.longitude),15f))
                        else {
                            Log.i("", "Unable to fetch the current location. Defaulting to Sydney")
                            map.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        -33.852,
                                        151.211
                                    ), 15f
                                )
                            )
                        }
                    }else{
                        Log.i("","Unable to fetch the current location. Defaulting to Sydney")
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(-33.852, 151.211),15f))
                    }
                }
            }else{
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),111)
            }
        } catch (exception: SecurityException) {

        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == 1111 && grantResults.isNotEmpty() && grantResults[0] ==  PackageManager.PERMISSION_GRANTED){
            getCurrentLocation()
        }
    }




   private fun isLocationPermissionGranted() = context?.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED







}
