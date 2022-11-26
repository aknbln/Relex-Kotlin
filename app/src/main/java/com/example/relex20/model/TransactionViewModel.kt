package com.example.relex20.model

import android.graphics.Bitmap
import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import java.text.NumberFormat
import java.util.*
import kotlin.reflect.typeOf

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

    private val _distance_name = MutableLiveData("")
    val distance_name: LiveData<String> = _distance_name

    // tripCost for the transaction
    private val _tripCost = MutableLiveData(0.00)
    val tripCost: LiveData<String> = Transformations.map(_distance) {

        NumberFormat.getCurrencyInstance().format(it * taxRate)
    }




    private val _tripCosts = MutableLiveData(0.0)
    val tripCosts: LiveData<String> = Transformations.map(_tripCosts) {
        NumberFormat.getCurrencyInstance().format(it)
    }

    private val _receiptCosts = MutableLiveData(0.0)
    val receiptCosts: LiveData<String> = Transformations.map(_receiptCosts) {
        NumberFormat.getCurrencyInstance().format(it)
    }


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
     * Set curLocation.
     */
    fun setDestName(location: String) {

        _distance_name.value = location


    }

    /**
     * Reset all values pertaining to the order.
     */
    fun resetOrder() {
        // TODO: Reset all values associated with an order
        println("GETS TO RESET")
        _destination.value = null
        _distance_name.value = null
        _distance.value = 0.0
        _tripCosts.value= 0.0
        _receiptCosts.value= 0.0
        _total.value = 0.0

    }



    /**
     * Set curLocation.
     */
    fun setDistance(distance: Double) {


        //if destination is changed, 1 trip per transaction
        if(_distance.value != null){
            updateTotal(-1 * _distance.value!! * taxRate)
        }
        _distance.value = distance
        //update Total
        updateTotal(_distance.value!! * taxRate)
        updateTripCosts(_distance.value!! * taxRate)

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
        println("Double is: $itemPrice with type")
        println("new subtotal: " + this._total.value)
    }

    fun updateReceiptCosts(itemPrice: Double) {
        if(receiptCosts.value == null){
            _receiptCosts.value = itemPrice
        } else {
            _receiptCosts.value = _receiptCosts.value!! + itemPrice
        }
        println("_receiptCosts is: $itemPrice with type")
        updateTotal(itemPrice)
        println("new subtotal: " + this._receiptCosts.value)
    }
    fun updateReceiptImage(image: Bitmap) {
        println("Updated image to: " + image);
    }

    fun updateTripCosts(itemPrice: Double) {
        if(_total.value == null){
            _tripCosts.value = itemPrice
        } else {
            _tripCosts.value = _tripCosts.value!! + itemPrice

        }
        println("Double is: $itemPrice with type")
        println("new subtotal: " + this._total.value)
    }

}