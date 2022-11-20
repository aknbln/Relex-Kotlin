package com.example.relex20

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.relex20.model.TransactionViewModel
import com.example.relex20.map.MapsFragment
import com.example.relex20.map.StartTripFragment
import com.google.android.gms.maps.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView


private const val TAG = "MainActivity"
private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34

class HomeActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var curFragment: Fragment
    private val sharedViewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        //supportActionBar?.hide()
        setSupportActionBar(findViewById(R.id.myToolbar))

        curFragment = StartTripFragment()


        loadFragment(curFragment)
        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.maps -> {
                    if(sharedViewModel.tripStarted) {
                        loadFragment(MapsFragment())
                    }else{
                        loadFragment(StartTripFragment())
                    }
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