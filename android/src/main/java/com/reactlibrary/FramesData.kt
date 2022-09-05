package com.reactlibrarynativetoast;

import android.graphics.Bitmap;

class FramesData{
    var bitmaps = arrayListOf<Bitmap>()
    var delays  = arrayListOf<Int>()
    var frameCount : Int = 0
    var stoppped : Boolean = false
    // single
    fun addBitmap(bitmap : Bitmap){
        this.bitmaps.add(bitmap)
    }
    fun addDelay(delay : Int){
        this.delays.add(delay)
    }

    fun getBitmap(index : Int) : Bitmap{
        return this.bitmaps[index]
    }

    fun getDelay(index : Int) : Int{
        return this.delays[index]
    }

    // lists
    fun setListOfDelays (delays : ArrayList<Int>){
        this.delays = delays
    }
    fun setListOfBitmaps (bitmaps : ArrayList<Bitmap>){
        this.bitmaps = bitmaps
    }
    

    fun getListOfDelays() : ArrayList<Int>{
        return this.delays
    }

    fun getListOfBitmaps() : ArrayList<Bitmap>{
        return this.bitmaps
    }

    // count
    fun setFramesCount(count:Int) {
        android.util.Log.d("FramesData", "setFramesCount: $count")
        this.frameCount=count
    }
    fun getFramesCount() : Int{
        return this.frameCount
    }

    //state
    fun setStopped(stopped : Boolean){
        this.stoppped = stopped
    }
    fun getStopped() : Boolean{
        return this.stoppped
    }
}