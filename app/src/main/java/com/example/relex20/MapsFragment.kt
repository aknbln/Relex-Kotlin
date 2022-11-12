package com.example.relex20

import android.content.ComponentName
import android.content.ServiceConnection
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import android.content.BroadcastReceiver
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.relex20.model.TransactionViewModel

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(){


    // Use the 'by activityViewModels()' Kotlin property delegate from the fragment-ktx artifact
    private val sharedViewModel: TransactionViewModel by activityViewModels()

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

        val curLocation: Location? = sharedViewModel.location.value

        println("here")
        println(curLocation.toText())
//        if (curLocation != null) {
//            println(curLocation.latitude)
//            println(curLocation.longitude)
//        };
//        println("here")

        val sydney = curLocation?.let { LatLng(it.latitude , curLocation.longitude) }
        sydney?.let { MarkerOptions().position(it).title("Marker in Sydney") }
            ?.let { googleMap.addMarker(it) }
        sydney?.let { CameraUpdateFactory.newLatLng(it) }?.let { googleMap.moveCamera(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

    }


}