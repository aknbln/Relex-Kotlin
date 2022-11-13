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

    // tripCost for the transaction
    private val _tripCost = MutableLiveData<Double?>()
    val tripCost: LiveData<Double?> = _tripCost

    // distance remaining for the transaction
    private val _distance = MutableLiveData<Double?>()
    val distance: LiveData<Double?> = _distance

    // scannedCosts for the transaction
    private val _scannedCosts = MutableLiveData<Double?>()
    val scannedCosts: LiveData<Double?> = _scannedCosts

    // manualCosts for the transaction
    private val _manualCosts = MutableLiveData<Double?>()
    val manualCosts: LiveData<Double?> = _manualCosts


    // Total cost of the order
    private val _total = MutableLiveData(0.0)

    val total: LiveData<String> = Transformations.map(_total) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    /**
     * Set curLocation.
     */
    fun setCurLocation(location: Location) {

        _location.value = location
        //update Total
//        updateTotal(_location.value)


    }

    /**
     * Set curLocation.
     */
    fun setDistance(distance: Double) {

        _distance.value = distance
        //update Total
//        updateTotal(_location.value)


    }

    fun getFormattedDistance(): String {
        return if(distance.value != null) {
            val formatter = NumberFormat.getNumberInstance()
            formatter.minimumFractionDigits = 2
            formatter.maximumFractionDigits = 2
            distance.value?.let { formatter.format(it.toDouble()) } + " km"
        }else{
            "0 km"
        }
    }

    /**
     * Getter method for price.
     * Includes formatting.
     */
    fun getFormattedPrice(): String = NumberFormat.getCurrencyInstance().format(_total.value)

    fun setDestination(location: LatLng) {

        _destination.value = LatLng(location.latitude, location.longitude)
        //update Total
//        updateTotal(_location.value)


    }


    /**
     * Update subtotal value.
     */
    fun updateTotal(itemPrice: Double) {
        // TODO: if _subtotal.value is not null, update it to reflect the price of the recently
        //  added item.
        //  Otherwise, set _subtotal.value to equal the price of the item.
        if(_total.value == null){
            _total.value = itemPrice
        }else{
            _total.value = _total.value!! + itemPrice
        }
        println("new subtotal: " + this._total.value)
//        // TODO: calculate the tax and resulting total
//        calculateTaxAndTotal()
    }
}