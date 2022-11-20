package com.example.relex20.model

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import java.text.NumberFormat
import java.util.*

class TransactionViewModel : ViewModel() {

    // Default tax rate
    private val taxRate = 0.08

    //for tripStart Fragment tracking
    var tripStarted = false


    private val _location = MutableLiveData<Location?>()
    val location: LiveData<Location?> = _location

    private val _destination = MutableLiveData<LatLng?>()
    val destination: LiveData<LatLng?> = _destination


    // distance remaining for the transaction
    private val _distance = MutableLiveData(0.0)
    val distance: LiveData<String> = Transformations.map(_distance) {
        val formatter = NumberFormat.getNumberInstance()
        formatter.minimumFractionDigits = 2
        formatter.maximumFractionDigits = 2
        formatter.format(it) + " km"
    }

    // tripCost for the transaction
    private val _tripCost = MutableLiveData(0.00)
    val tripCost: LiveData<String> = Transformations.map(_distance) {

        NumberFormat.getCurrencyInstance().format(it * taxRate)
    }




    // scannedCosts for the transaction
    private val _scannedCosts = MutableLiveData<Double?>()
    val scannedCosts: LiveData<Double?> = _scannedCosts

    // manualCosts for the transaction
    private val _manualCosts = MutableLiveData<Double?>()
    val manualCosts: LiveData<Double?> = _manualCosts


    // Total cost of the order
    val _total = MutableLiveData(0.0)

    val total: LiveData<String> = Transformations.map(_total) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    /**
     * Set curLocation.
     */
    fun setCurLocation(location: Location) {

        _location.value = location


    }

    /**
     * Reset all values pertaining to the order.
     */
    fun resetOrder() {
        // TODO: Reset all values associated with an order
        _destination.value = null
        _distance.value = 0.0
        _scannedCosts.value= 0.0
        _manualCosts.value= 0.0
        _total.value = 0.0

    }



    /**
     * Set curLocation.
     */
    fun setDistance(distance: Double) {

        _distance.value = distance
        //update Total
        updateTotal(_distance.value!! * taxRate)


    }



    fun setDestination(location: LatLng) {

        _destination.value = LatLng(location.latitude, location.longitude)
        //update Total
//        updateTotal(_location.value)


    }


    /**
     * Update subtotal value.
     */
    fun updateTotal(itemPrice: Double) {
        if(_total.value == null){
            _total.value = itemPrice
        } else {
            _total.value = _total.value!! + itemPrice
        }
        println("new subtotal: " + this._total.value)
    }
}