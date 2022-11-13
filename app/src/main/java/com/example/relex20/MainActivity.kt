package com.example.relex20

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.Settings
import android.util.Log
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.relex20.map.MapsFragment
import com.example.relex20.map.StartTripFragment
import com.example.relex20.model.TransactionViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

class MainActivity : AppCompatActivity(){

    private lateinit var bottomNav : BottomNavigationView
    private lateinit var curFragment: Fragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //supportActionBar?.hide()
        setSupportActionBar(findViewById(R.id.my_toolbar))

        curFragment = StartTripFragment()


        loadFragment(curFragment)
        bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.maps -> {
                    loadFragment(MapsFragment())
                    true
                }
                R.id.scan -> {

                    //navController.navigate(R.id.action_mapsFragment_to_scanFragment2)
                    loadFragment(ScanFragment())
                    true
                }
                R.id.account -> {
                    //navController.navigate(R.id.action_scanFragment2_to_accountFragment)
                    loadFragment(AccountFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.remove(curFragment)
        transaction.add( R.id.container, fragment)
        curFragment = fragment
        transaction.commit()
    }

}