package com.example.relex20.map

import android.annotation.SuppressLint
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.relex20.R
import com.example.relex20.databinding.FragmentMapsBinding
import com.example.relex20.model.TransactionViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.gson.Gson
import com.google.maps.android.SphericalUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class MapsFragment : Fragment(){


    private var mMap: GoogleMap? = null
    private var destinationMarker: Marker? = null
    private var lineOptions: Polyline? = null
    private var curPosition: LatLng? = null
    private var _binding: FragmentMapsBinding? = null
    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val sharedViewModel: TransactionViewModel by activityViewModels()

    private val binding get() = _binding!!
    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        mMap = googleMap

         val curLocation: android.location.Location? = sharedViewModel.location.value

        //Setting the current position on the map
        curPosition = curLocation?.let { LatLng(it.latitude , curLocation.longitude) }
        curPosition?.let { MarkerOptions().position(it).title("Current Position") }
            ?.let { googleMap.addMarker(it) }

        if(sharedViewModel.destination.value != null){
            mMap?.addMarker(MarkerOptions().position(sharedViewModel.destination.value!!).title("Destination"))
        }

        curPosition?.let { CameraUpdateFactory.newLatLngZoom(it, 14F) }
            ?.let { mMap!!.animateCamera(it) }


    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMapsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            viewModel = sharedViewModel
            lifecycleOwner = viewLifecycleOwner
        }

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        if(sharedViewModel.distance_name.value != null) {
            binding.destination.setText(sharedViewModel.distance_name.value)
        }
        //For route https://www.geeksforgeeks.org/how-to-generate-route-between-two-locations-in-google-map-in-android/

        // Fetching API_KEY which we wrapped
        val ai: ApplicationInfo? = requireActivity().applicationContext
            ?.packageManager?.getApplicationInfo(requireActivity().applicationContext.packageName, PackageManager.GET_META_DATA)

        val value = ai?.metaData?.get("com.google.android.geo.API_KEY")
        val apiKey = value.toString()

        // Initializing the Places API with the help of our API_KEY
        if (!Places.isInitialized()) {
            activity?.applicationContext?.let { Places.initialize(it, apiKey) }
        }
        //



        binding.destination.setOnEditorActionListener(
            OnEditorActionListener { v, actionId, event ->
                //listening to done button on the keyboard to close the keyboard and add the destination marker
                if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_ACTION_DONE || event != null && event.action === KeyEvent.ACTION_DOWN && event.keyCode === KeyEvent.KEYCODE_ENTER) {
                    if (event == null || !event.isShiftPressed) {
                        // the user is done typing.
                        val location: String = v.text.toString()
                        // below line is to create a list of address
                        // where we will store the list of all address.
                        var addressList: List<Address>? = null

                        println(location)
                        // checking if the entered location is null or not.

                        //FOR THE ROUTE

                        // checking if the entered location is null or not.
                        if (location != "") {
                            println("inside if1")
                            sharedViewModel.setDestName(location)
                            // on below line we are creating and initializing a geo coder.
                            val geocoder = activity?.let { Geocoder(it.applicationContext) }
                            try {

                                // on below line we are getting location from the
                                // location name and adding that location to address list.
                                if (geocoder != null) {
                                    println("inside if2")
                                    addressList = geocoder.getFromLocationName(location, 1)
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            // on below line we are getting the location
                            // from our list a first position.
                            val address = addressList!![0]

                            // on below line we are creating a variable for our location
                            // where we will add our locations latitude and longitude.
                            val dest = LatLng(address.latitude, address.longitude)


                            //save destination to the viewmodel
                            sharedViewModel.setDestination(dest)

                            //make it a mutable data, set it on xml file
                            val totalDistance: Double = SphericalUtil.computeDistanceBetween(curPosition, dest);
                            sharedViewModel.setDistance(totalDistance/1000)


                            // on below line we are adding marker to that position.
                            if(destinationMarker != null) {
                                    destinationMarker!!.remove()
                            }
                            destinationMarker =
                                mMap?.addMarker(MarkerOptions().position(dest).title(location))

                            val builder = LatLngBounds.Builder()
                            curPosition?.let { builder.include(it) }
                            builder.include(dest)

                            val bounds = builder.build()
                            val width = resources.displayMetrics.widthPixels


                            val padding = width * 0.3 // offset from edges of the map in pixels

                            val cu = CameraUpdateFactory.newLatLngBounds(bounds, padding.toInt())


                            val urll =
                                curPosition?.let { getDirectionURL(it, dest, apiKey) }

                            println("Is url null?:  " + urll)
                            if (urll != null) {
                                mapFragment?.getMapAsync {
                                    GetDirection(urll).execute()
                                    curPosition?.let { it1 ->
                                        CameraUpdateFactory.newLatLngZoom(
                                            it1, 14F)
                                    }?.let { it2 -> mMap?.animateCamera(it2) }
                                }
                            }
                            // below line is to animate camera to that position.
                            //mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))
                            mapFragment?.getMapAsync {
                                mMap?.animateCamera(cu)
                            }
                        }


                        return@OnEditorActionListener true // consume.
                    }
                }
                false // pass on to other listeners.
            }
        )
    }


    private fun getDirectionURL(origin:LatLng, dest:LatLng, secret: String) : String{
        return "https://maps.googleapis.com/maps/api/directions/json?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${dest.latitude},${dest.longitude}" +
                "&sensor=false" +
                "&mode=driving" +
                "&key=$secret"
    }


    fun decodePolyline(encoded: String): List<LatLng> {
        val poly = ArrayList<LatLng>()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val latLng = LatLng((lat.toDouble() / 1E5),(lng.toDouble() / 1E5))
            poly.add(latLng)
        }
        return poly
    }

    class MapData {
        var routes = ArrayList<Routes>()
    }

    class Routes {
        var legs = ArrayList<Legs>()
    }

    class Legs {
        var distance = Distance()
        var duration = Duration()
        var end_address = ""
        var start_address = ""
        var end_location =Location()
        var start_location = Location()
        var steps = ArrayList<Steps>()
    }

    class Steps {
        var distance = Distance()
        var duration = Duration()
        var end_address = ""
        var start_address = ""
        var end_location =Location()
        var start_location = Location()
        var polyline = PolyLine()
        var travel_mode = ""
        var maneuver = ""
    }

    class Duration {
        var text = ""
        var value = 0
    }

    class Distance {
        var text = ""
        var value = 0
    }

    class PolyLine {
        var points = ""
    }

    class Location{
        var lat =""
        var lng =""
    }


    @SuppressLint("StaticFieldLeak")
    private inner class GetDirection(val url : String) : AsyncTask<Void, Void, List<List<LatLng>>>(){
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg params: Void?): List<List<LatLng>> {
            val client = OkHttpClient()
            val request = Request.Builder().url(url).build()
            val response = client.newCall(request).execute()
            val data = response.body!!.string()

            val result =  ArrayList<List<LatLng>>()
            try{
                val respObj = Gson().fromJson(data,MapData::class.java)
                val path =  ArrayList<LatLng>()
                for (i in 0 until respObj.routes[0].legs[0].steps.size){
                    path.addAll(decodePolyline(respObj.routes[0].legs[0].steps[i].polyline.points))
                }
                result.add(path)
            }catch (e:Exception){
                e.printStackTrace()
            }
            return result
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(result: List<List<LatLng>>) {
            lineOptions?.remove()
            val lineoption = PolylineOptions()
            for (i in result.indices){
                lineoption.addAll(result[i])
                lineoption.width(10f)
                lineoption.color(Color.MAGENTA)
                lineoption.geodesic(true)
            }
           lineOptions = mMap?.addPolyline(lineoption)
        }
    }
}