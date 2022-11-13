package com.example.relex20

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.gms.maps.MapFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView
    private lateinit var navController: NavController
    private lateinit var curFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //supportActionBar?.hide()
        setSupportActionBar(findViewById(R.id.myToolbar))
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController)

        curFragment = MapsFragment()
        loadFragment(curFragment)
        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
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

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.remove(curFragment)
        transaction.add( R.id.container, fragment)
        curFragment = fragment
        transaction.commit()
    }
}