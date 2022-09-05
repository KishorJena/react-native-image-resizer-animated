package com.reactlibrarynativetoast

import android.content.Context


import com.bumptech.glide.*
import com.bumptech.glide.Glide
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bytes.BytesResource
import com.bumptech.glide.load.resource.gif.GifDrawableTransformation
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.load.resource.gif.GifDrawableResource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.Request
import com.bumptech.glide.request.target.SizeReadyCallback
import com.bumptech.glide.load.resource.bitmap.FitCenter
import com.bumptech.glide.load.resource.gif.*
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.request.RequestListener

import com.bumptech.glide.load.*
import com.bumptech.glide.load.Options
import com.bumptech.glide.integration.webp.decoder.WebpFrameLoader
import com.bumptech.glide.integration.webp.decoder.WebpDecoder
import com.bumptech.glide.integration.webp.decoder.WebpDrawable
import com.bumptech.glide.integration.webp.decoder.*
import com.bumptech.glide.integration.webp.WebpImage

import com.bumptech.glide.load.resource.gif.GifBitmapProvider

// import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.gifdecoder.StandardGifDecoder

import com.bumptech.glide.gifdecoder.GifDecoder
// import com.bumptech.glide.gifdecoder.*

import android.os.Environment
import android.graphics.drawable.Drawable
import java.lang.reflect.*

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Color
import android.util.Log
import com.facebook.react.bridge.*
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
// import java.io.File
// import java.io.FileOutputStream
import java.io.*
import java.net.URL
import kotlin.math.min
import kotlin.time.ExperimentalTime
import kotlin.time.measureTime
import java.io.ByteArrayOutputStream
import java.lang.Exception

// import com.n4no.webpencoder.webp.graphics.WebpBitmapEncoder

// standalone gif encode decoder 
import com.waynejo.androidndkgif.GifDecoder as GifDecoder2

class DecodeWEBP  {

    fun sum(list:MutableList<Int>):List<Int> {
        val MIN_FRAME_DURATION:Int = 8 // 8 ms
        val MAX_WEBP_DURATION:Int = 1000*10 // 10 sec
        val sum:Int = list.sum()
        val min:Int = (list.sum()/list.count())
        val avg:Int = list.average().toInt()
        val count:Int = list.count()

        if(sum<=MAX_WEBP_DURATION){
            return list
        }

        Log.d("ReactNative", "list is $list")
        Log.d("ReactNative", "sum is $sum")
        Log.d("ReactNative", "min is $min")
        Log.d("ReactNative", "avg is $avg")
        Log.d("ReactNative", "count is $count")



        var value = if(avg*count <= MAX_WEBP_DURATION){
             avg
//             Log.d("ReactNative", "avg*count is $value")
         }else if(min*count <= MAX_WEBP_DURATION){
             min
//             Log.d("ReactNative", "min*count is $value")
         }else{
                Log.d("ReactNative", "in else sum is $sum")
               val frames = list.count()
               val duration = list.sum()
               val extraDuration = (duration - MAX_WEBP_DURATION)

               val singleDelay:Float = (duration / frames).toFloat()
               val extraFrames:Int = (extraDuration/singleDelay).toInt()

               val percantage:Float = ((extraFrames.toFloat()/count.toFloat())*100).toFloat()
               
               if(percantage<=50.00){
                    val position = frames/extraFrames
                    var newList = list.filterIndexed({ index, x -> (index+1) % position != 0 })
                    Log.d("ReactNative", "newList is $newList")
                    if(newList.sum()<=MAX_WEBP_DURATION){
                        return newList
                    }
               }else{
                    val halfDelay = kotlin.math.ceil((singleDelay/2)).toInt()
                    val halfDuration = halfDelay*count
                    if(halfDuration<=MAX_WEBP_DURATION && singleDelay>=8){
                        val newList = list.map { (it - halfDelay).toInt() }
                        Log.d("ReactNative", "newList is $newList : Duration: ${newList.sum()}")
                        if(newList.sum()<=MAX_WEBP_DURATION){
                            return newList
                        }
                    }
                    list.clear()
               }
//             Log .d("ReactNative", "return null | sum=$sm | min=$min | avg=$avg | count=$count")
             list.clear()
             return list
         }

         list.fill(value)

        return list

        // if sum > MAX
            // set average
            // if sum > MAX
                // set min
                // if sum > MAX
        // Else tell user to compress         

    }


    fun bitmapToFile(
        bitmap: Bitmap,
        fileNameToSave: String
    ): File? { // File name like "image.png"
        //create a file to write bitmap data
        var file: File? = null
        return try {
            file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "tmp")

            // file =
                // File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/" + "fileNameToSave")
            // file =  File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+ "/Christmas");
            if (file != null) {
                file.createNewFile()
            }

//Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.WEBP, 0, bos) // YOU can also save it in JPEG
            val bitmapdata: ByteArray = bos.toByteArray()

//write the bytes in file
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            file // it will return null
        }
    }

    private fun File.writeBitmap(bitmap: Bitmap, format: Bitmap.CompressFormat, quality: Int) {
        outputStream().use { out ->
            bitmap.compress(format, quality, out)
            out.flush()
        }
    }

     fun scalePreserveRatio(
        imageToScale: Bitmap?, destinationWidth: Int,
        destinationHeight: Int
    ): Bitmap? {
        // NOTE
        var imageToScale: Bitmap? = imageToScale
        return if (destinationHeight > 0 && destinationWidth > 0 && imageToScale != null) {
            val width: Int = imageToScale.getWidth().toInt()
            val height: Int = imageToScale.getHeight().toInt()

            //Calculate the max changing amount and decide which dimension to use
            val widthRatio = destinationWidth.toFloat() / width.toFloat()
            val heightRatio = destinationHeight.toFloat() / height.toFloat()

            //Use the ratio that will fit the image into the desired sizes
            var finalWidth = Math.floor((width * widthRatio).toDouble()).toInt()
            var finalHeight = Math.floor((height * widthRatio).toDouble()).toInt()
            if (finalWidth > destinationWidth || finalHeight > destinationHeight) {
                finalWidth = Math.floor((width * heightRatio).toDouble()).toInt()
                finalHeight = Math.floor((height * heightRatio).toDouble()).toInt()
            }

            //Scale given bitmap to fit into the desired area
            imageToScale = Bitmap.createScaledBitmap(imageToScale, finalWidth, finalHeight, false)

            //Created a bitmap with desired sizes
            val scaledImage: Bitmap =
                Bitmap.createBitmap(destinationWidth, destinationHeight, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(scaledImage)

            //Draw background color
            val paint = Paint()
            paint.setColor(Color.BLACK)
            // paint.setColor(Color.TRANSPARENT)
            paint.setStyle(Paint.Style.FILL)
            canvas.drawRect(0.toFloat(), 0.toFloat(), canvas.getWidth().toFloat(), canvas.getHeight().toFloat(), paint)

            //Calculate the ratios and decide which part will have empty areas (width or height)
            val ratioBitmap = finalWidth.toFloat() / finalHeight.toFloat()
            val destinationRatio = destinationWidth.toFloat() / destinationHeight.toFloat()
            val left: Float =
                if (ratioBitmap >= destinationRatio) 0F else (destinationWidth - finalWidth).toFloat() / 2
            val top: Float =
                if (ratioBitmap < destinationRatio) 0F else (destinationHeight - finalHeight).toFloat() / 2
            canvas.drawBitmap(imageToScale, left, top, null)
            scaledImage
        } else {
            imageToScale
        }
    }

    fun save(bitmap:Bitmap, name:String){ 
        Log.d("ReactNative","saving ${name}")
        val filename:String = name
        val sd:File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/tmp")
        val dest:File = File(sd, filename)
        
        try {
            val out:FileOutputStream = FileOutputStream(dest)
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
            out.flush()
            out.close()
            Log.d("ReactNative","flushed ${name}")
        } catch (e:Exception) {
            Log.d("ReactNative"," er saving: ${e.toString()}")
        }
        Log.d("ReactNative","saved ${name}")
    }

    // fun saveFile(file:File){ 
    //     Log.d("ReactNative","saving ${name}")
    //     // val filename:String = name
    //     // val sd:File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/tmp")
    //     // val dest:File = File(sd, filename)
        
    //     try {
    //         val out:FileOutputStream = FileOutputStream(file)
    //         // bitmap.compress(Bitmap.CompressFormat.PNG, 90, out)
    //         out.flush()
    //         out.close()
    //         Log.d("ReactNative","flushed ${name}")
    //     } catch (e:Exception) {
    //         Log.d("ReactNative"," er saving: ${e.toString()}")
    //     }
    //     Log.d("ReactNative","saved ${name}")
    // }
    
    fun workGif(context:Context, src:String, dst:String) {
        val gifDecoder:GifDecoder2 = GifDecoder2()
        val isSucceeded = gifDecoder.load(src)

        // if (isSucceeded) {
        //     for (int i = 0; i < gifDecoder.frameNum(); ++i) {
        //         Bitmap bitmap = gifDecoder.frame(i);
        //     }
        // }
    }
    
    fun decodeWebp(context:Context, src:String) :MutableList<Any?>{

        // GlideApp.with()
         // Bolo().foo()
         // GlideApp.with(context)
         Log.d("ReactNative", "init decpdeWEbp")
 
       
         var both = mutableListOf<Any?>()

         var bms = mutableListOf<Bitmap?>()
         var dur = mutableListOf<Int?>()

 
         try{
             // val f:File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/new103.webp")
             // val f:File = File(dst)
             // if(f.exists()){
             //     Log.d("ReactNative", "exist ${f.getAbsolutePath()}")
             // }else{
             //     Log.d("ReactNative", "No00 exist ${f.getAbsolutePath()}")
 
             // }
             // if(f.isFile()){
             //     Log.d("ReactNative", "isFIle ${f.getAbsolutePath()}")
             // }else{
             //     Log.d("ReactNative", "is not a FIle ${f.getAbsolutePath()}")
 
             // }
             // val webpEncoder:WebpBitmapEncoder = WebpBitmapEncoder(f)
             // webpEncoder.setLoops(0); // 0 = infinity.  
 
 
             
             val source_file:File = File(src)
             // val dest_file:File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/new103.webp")
             // val dest_file:File = File(Environment.getExternalStoragePublicDirectory(), src)
 
             val drawable = GlideApp.with(context).load(source_file).skipMemoryCache(true)
                     .diskCacheStrategy(DiskCacheStrategy.NONE)
                     .submit().get() as WebpDrawable
             drawable.constantState
             val state = drawable.constantState as Drawable.ConstantState
 
             val frameLoader: Field = state::class.java.getDeclaredField("frameLoader")
             frameLoader.isAccessible = true
 
             @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
             
             val webpFrameLoader = frameLoader.get(state) as WebpFrameLoader
             val webpDecoder: Field = webpFrameLoader.javaClass.getDeclaredField("webpDecoder")
             webpDecoder.isAccessible = true
             val standardGifDecoder = webpDecoder.get(webpFrameLoader) as GifDecoder
             Log.d("ReactNative", "got ${standardGifDecoder.frameCount} frames:")
 
             // val del = arrayOfNulls<Int>(standardGifDecoder.frameCount)
             
             for (i in 0 until standardGifDecoder.frameCount) {
                 val delay = standardGifDecoder.nextDelay
                 val bitmap = standardGifDecoder.nextFrame as Bitmap
                 // val bm = scalePreserveRatio(bitmap, 512,512)
                 var list = mutableListOf(delay,bitmap)
                 
                 both.add(list)
                 // del[i] = delay
                 // KSR Type 1 
                 // bitmapToFile(bitmap, "file${i}.webp")
                 
                 // KSR Type 2 
                 // File(Environment.DIRECTORY_DOWNLOADS, "map${i}.webp").writeBitmap(bitmap, Bitmap.CompressFormat.WEBP, 85)
                 
                 // KSR Type 3
                 // save(bitmap, "file_${i}.webp") 
                 // webpEncoder.setDuration(delay)
                 // webpEncoder.writeFrame(bm, 100)
 
 
                 //image is available here on the bitmap object
                 // bitmapToFile(bitmap, "some_${standardGifDecoder.currentFrameIndex}.webp")
                //  Log.d("ReactNative", "${standardGifDecoder.currentFrameIndex} - $delay ${bitmap?.width}x${bitmap?.height}")
                 standardGifDecoder.advance()
             }
             // webpEncoder.close()
           
             Log.i("ReactNative", "hiKishor")
         }catch (e: Exception) {
             // webpEncoder.close()
             Log.i("ReactNative", "exception: ${e.toString()}")
         }
         return both
      }
 
     
 
    fun hiKishor(context:Context, src:String) :MutableList<Bitmap?>{
        // GlideApp.with()
        // Bolo().foo()
        // GlideApp.with(context)
        Log.d("ReactNative", "init")

      
        var bms = mutableListOf<Bitmap?>()

        try{
            // val f:File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/new103.webp")
            // val f:File = File(dst)
            // if(f.exists()){
            //     Log.d("ReactNative", "exist ${f.getAbsolutePath()}")
            // }else{
            //     Log.d("ReactNative", "No00 exist ${f.getAbsolutePath()}")

            // }
            // if(f.isFile()){
            //     Log.d("ReactNative", "isFIle ${f.getAbsolutePath()}")
            // }else{
            //     Log.d("ReactNative", "is not a FIle ${f.getAbsolutePath()}")

            // }
            // val webpEncoder:WebpBitmapEncoder = WebpBitmapEncoder(f)
            // webpEncoder.setLoops(0); // 0 = infinity.  


            
            val source_file:File = File(src)
            // val dest_file:File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS+"/new103.webp")
            // val dest_file:File = File(Environment.getExternalStoragePublicDirectory(), src)

            val drawable = GlideApp.with(context).load(source_file).skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .submit().get() as WebpDrawable
            drawable.constantState
            val state = drawable.constantState as Drawable.ConstantState

            val frameLoader: Field = state::class.java.getDeclaredField("frameLoader")
            frameLoader.isAccessible = true

            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            
            val webpFrameLoader = frameLoader.get(state) as WebpFrameLoader
            val webpDecoder: Field = webpFrameLoader.javaClass.getDeclaredField("webpDecoder")
            webpDecoder.isAccessible = true
            val standardGifDecoder = webpDecoder.get(webpFrameLoader) as GifDecoder
            Log.d("ReactNative", "got ${standardGifDecoder.frameCount} frames:")

            // val del = arrayOfNulls<Int>(standardGifDecoder.frameCount)

            for (i in 0 until standardGifDecoder.frameCount) {
                val delay = standardGifDecoder.nextDelay
                val bitmap = standardGifDecoder.nextFrame as Bitmap
                // val bm = scalePreserveRatio(bitmap, 512,512)
                bms.add(bitmap)
                // del[i] = delay
                // KSR Type 1 
                // bitmapToFile(bitmap, "file${i}.webp")
                
                // KSR Type 2 
                // File(Environment.DIRECTORY_DOWNLOADS, "map${i}.webp").writeBitmap(bitmap, Bitmap.CompressFormat.WEBP, 85)
                
                // KSR Type 3
                // save(bitmap, "file_${i}.webp") 
                // webpEncoder.setDuration(delay)
                // webpEncoder.writeFrame(bm, 100)


                //image is available here on the bitmap object
                // bitmapToFile(bitmap, "some_${standardGifDecoder.currentFrameIndex}.webp")
                // Log.d("ReactNative", "${standardGifDecoder.currentFrameIndex} - $delay ${bitmap?.width}x${bitmap?.height}")
                standardGifDecoder.advance()
            }
            // webpEncoder.close()
          
            Log.i("ReactNative", "hiKishor")
        }catch (e: Exception) {
            // webpEncoder.close()
            Log.i("ReactNative", "exception: ${e.toString()}")
        }
        return bms
     }

     fun DecodeWebP(context:Context, src:String) :HashMap<String, ArrayList<Any?>>{

         Log.d("ReactNative", "init decpdeWEbp")

        var hashMap : HashMap<String, ArrayList<Any?>> = HashMap<String, ArrayList<Any?>> ()

        var both = mutableListOf<Any?>()

        //  var bms = mutableListOf<Any?>()
        //  var dur = mutableListOf<Any?>()
         var bms:ArrayList<Any?> = arrayListOf<Any?>()
         var dur:ArrayList<Any?> = arrayListOf<Any?>()

 
         try{
            
             val source_file:File = File(src)
 
             val drawable = GlideApp.with(context).load(source_file).skipMemoryCache(true)
                     .diskCacheStrategy(DiskCacheStrategy.NONE)
                     .submit().get() as WebpDrawable
             drawable.constantState
             val state = drawable.constantState as Drawable.ConstantState
 
             val frameLoader: Field = state::class.java.getDeclaredField("frameLoader")
             frameLoader.isAccessible = true
 
             @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
             
             val webpFrameLoader = frameLoader.get(state) as WebpFrameLoader
             val webpDecoder: Field = webpFrameLoader.javaClass.getDeclaredField("webpDecoder")
             webpDecoder.isAccessible = true
             val standardGifDecoder = webpDecoder.get(webpFrameLoader) as GifDecoder
             Log.d("ReactNative", "got ${standardGifDecoder.frameCount} frames:")
 
             // val del = arrayOfNulls<Int>(standardGifDecoder.frameCount)
             
             for (i in 0 until standardGifDecoder.frameCount) {
                 val delay = standardGifDecoder.nextDelay
                 val bitmap = standardGifDecoder.nextFrame as Bitmap
                 // val bm = scalePreserveRatio(bitmap, 512,512)
                 var list = mutableListOf(delay,bitmap)
                 bms.add(bitmap)
                 dur.add(delay)
                 both.add(list)
                 // del[i] = delay
                 // KSR Type 1 
                 // bitmapToFile(bitmap, "file${i}.webp")
                 
                 // KSR Type 2 
                 // File(Environment.DIRECTORY_DOWNLOADS, "map${i}.webp").writeBitmap(bitmap, Bitmap.CompressFormat.WEBP, 85)
                 
                 // KSR Type 3
                 // save(bitmap, "file_${i}.webp") 
                 // webpEncoder.setDuration(delay)
                 // webpEncoder.writeFrame(bm, 100)
 
 
                 //image is available here on the bitmap object
                 // bitmapToFile(bitmap, "some_${standardGifDecoder.currentFrameIndex}.webp")
                //  Log.d("ReactNative", "${standardGifDecoder.currentFrameIndex} - $delay ${bitmap?.width}x${bitmap?.height}")
                 standardGifDecoder.advance()
             }
             // webpEncoder.close()
            hashMap.put("bitmaps", bms)
            hashMap.put("delays", dur)

             Log.i("ReactNative", "hiKishor")
         }catch (e: Exception) {
             // webpEncoder.close()
             Log.i("ReactNative", "exception: ${e.toString()}")
         }
        //  return both
         return hashMap
      }

    fun DecodeWebP2(context:Context, src:String) :FramesData{

         Log.d("ReactNative", "init decpdeWEbp")

         var framesData = FramesData()
         try{
            
             val source_file:File = File(src)
 
             val drawable = GlideApp.with(context).load(source_file).skipMemoryCache(true)
                     .diskCacheStrategy(DiskCacheStrategy.NONE)
                     .submit().get() as WebpDrawable
             drawable.constantState
             val state = drawable.constantState as Drawable.ConstantState
 
             val frameLoader: Field = state::class.java.getDeclaredField("frameLoader")
             frameLoader.isAccessible = true
 
             @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
             
             val webpFrameLoader = frameLoader.get(state) as WebpFrameLoader
             val webpDecoder: Field = webpFrameLoader.javaClass.getDeclaredField("webpDecoder")
             webpDecoder.isAccessible = true
             val standardGifDecoder = webpDecoder.get(webpFrameLoader) as GifDecoder
             Log.d("ReactNative", "got ${standardGifDecoder.frameCount} frames:")
 
             // val del = arrayOfNulls<Int>(standardGifDecoder.frameCount)
             
             for (i in 0 until standardGifDecoder.frameCount) {
                 val delay = standardGifDecoder.nextDelay
                 val bitmap = standardGifDecoder.nextFrame as Bitmap
                 // val bm = scalePreserveRatio(bitmap, 512,512)
                 framesData.addBitmap(bitmap)
                 framesData.addDelay(delay)
                 standardGifDecoder.advance()
             }
             // webpEncoder.close()


             Log.i("ReactNative", "hiKishor")
         }catch (e: Exception) {
             // webpEncoder.close()
             Log.i("ReactNative", "exception: ${e.toString()}")
         }
        //  return both
         return framesData
      }

}

