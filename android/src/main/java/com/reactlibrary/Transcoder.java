package com.reactlibrarynativetoast;

import com.waynejo.androidndkgif.GifDecoder;
import com.waynejo.androidndkgif.GifEncoder;

import java.util.*;
import java.io.*;

import android.content.Context;

import android.graphics.*;
import android.util.Log;

// using old method
public class Transcoder {

    public HashMap<String, ArrayList<Object>> DecodeGif(String source) {
        GifDecoder gifDecoder = new GifDecoder();
        boolean isSucceeded = gifDecoder.load(source);
        
        // List<Object> list=new ArrayList<>();
        
        HashMap<String, ArrayList<Object>> map = new HashMap<>();
        List<Object> bitmaps = new ArrayList();
        List<Object> delays = new ArrayList();


        if (isSucceeded) {
            for (int i = 0; i < gifDecoder.frameNum(); ++i) {
                Bitmap frame = gifDecoder.frame(i);
                int delay = gifDecoder.delay(i);
                bitmaps.add(frame);
                delays.add(delay);
            //   List<Object> obj =  new ArrayList<>();
            //   obj.add(delay);
            //   obj.add(frame);
            //   list.add(obj);
                Log.d("ReactNative3",i+" delay : "+delay);
                
            }
        }
        map.put("bitmaps", (ArrayList<Object>) bitmaps);
        map.put("delays",  (ArrayList<Object>) delays);

        return map;
    //    return list;
   }

   public FramesData DecodeGif2(String source) {
    GifDecoder gifDecoder = new GifDecoder();
    boolean isSucceeded = gifDecoder.load(source);
    
    // List<Object> list=new ArrayList<>();
    
    HashMap<String, ArrayList<Object>> map = new HashMap<>();
    List<Object> bitmaps = new ArrayList();
    List<Object> delays = new ArrayList();

    FramesData fb = new FramesData();

    if (isSucceeded) {
        for (int i = 0; i < gifDecoder.frameNum(); ++i) {
            Bitmap frame = gifDecoder.frame(i);
            int delay = gifDecoder.delay(i);
            // bitmaps.add(frame);
            // delays.add(delay);
            fb.addBitmap(frame);
            fb.addDelay(delay);
        //   List<Object> obj =  new ArrayList<>();
        //   obj.add(delay);
        //   obj.add(frame);
        //   list.add(obj);
            Log.d("ReactNative3",i+" delay : "+delay);
            
        }
    }


    return fb;
//    return list;
    }

    public String EncodeGif(List<Object> obj) throws FileNotFoundException{
        List<Bitmap> bitmaps = new ArrayList<Bitmap>();
        List<Integer> delays = new ArrayList<Integer>();
    
        // extract bitmap and delays and add to above declare variable
        for(Object obj1:obj){
          ArrayList x = (ArrayList)obj1; // single object of [delay, frame]
    
          int a = (int)x.get(0); // get delay as int
          Bitmap b = (Bitmap)x.get(1); // get frame as bitmap 
          delays.add(a);
          bitmaps.add(b);
        }
    
        // First fix the delay and bitmap size to 512x512
        List<Integer> fixDelays = new Kishor1().sum(delays);
        if((int)fixDelays.size()==0){
          return null; //FIXME if did not fix any then something wrong with delay logic...
        }


        File directory = new File("/storage/emulated/0/Pictures/stk/");
        File file = new File(directory, UUID.randomUUID().toString()+".webp");

        GifEncoder gifEncoder = new GifEncoder();
        gifEncoder.init(512, 512, file.getAbsolutePath(), GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
        
        // Bitmap is MUST ARGB_8888.
        for(Bitmap bitmap: bitmaps) {
          Bitmap frame = bitmap;
          Bitmap resized = scalePreserveRatio(frame,512,512,Color.BLACK);
          Bitmap compressed = compress(resized);
          Bitmap final_Bitmap = compressed;

          int index = bitmaps.indexOf(bitmap);
          int delay = 8;
          if(delays.get(index)>8)
            delay = delays.get(index);

          gifEncoder.encodeFrame(final_Bitmap, delay);
        }
        
        gifEncoder.close();

        return file.getAbsolutePath();
    }

    public String EncodeGif2(FramesMeta myFrames) throws FileNotFoundException{


        File directory = new File("/storage/emulated/0/Pictures/stk/");
        File file = new File(directory, UUID.randomUUID().toString()+".webp");

        GifEncoder gifEncoder = new GifEncoder();
        gifEncoder.init(512, 512, file.getAbsolutePath(), GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
        
        // Bitmap is MUST ARGB_8888.
        for(int i = 0; i < myFrames.getFramesCount(); i++) {
            Bitmap frame = myFrames.getBitmap(i);
            int delay = myFrames.getDelay(i);
            // make sure minimum delay is 8
            if(delay < 8)
                delay = 8;

            gifEncoder.encodeFrame(frame, delay);
        }
        
        gifEncoder.close();

        return file.getAbsolutePath();
    }

    public static String DecodeWebP(String input) {
        return input;
    }

    public static String EncodeWebP(String input) {
        return input;
    }
    
    public static Bitmap scalePreserveRatio(Bitmap imageToScale, int destinationWidth, int destinationHeight, int color) {
        
        if (destinationHeight > 0 && destinationWidth > 0 && imageToScale != null) {
            int width = imageToScale.getWidth();
            int height = imageToScale.getHeight();

            //Calculate the max changing amount and decide which dimension to use
            float widthRatio = (float) destinationWidth / (float) width;
            float heightRatio = (float) destinationHeight / (float) height;

            //Use the ratio that will fit the image into the desired sizes
            int finalWidth = (int)Math.floor(width * widthRatio);
            int finalHeight = (int)Math.floor(height * widthRatio);
            if (finalWidth > destinationWidth || finalHeight > destinationHeight) {
                finalWidth = (int)Math.floor(width * heightRatio);
                finalHeight = (int)Math.floor(height * heightRatio);
            }

            //Scale given bitmap to fit into the desired area
            imageToScale = Bitmap.createScaledBitmap(imageToScale, finalWidth, finalHeight, true);

            //Created a bitmap with desired sizes
            Bitmap scaledImage = Bitmap.createBitmap(destinationWidth, destinationHeight, Bitmap.Config.ARGB_8888);
            
            Canvas canvas = new Canvas(scaledImage);

            //Draw background color
            Paint paint = new Paint();
            // paint.setColor(color); // if transparent then ecnoder must read ALPH
            paint.setColor(Color.TRANSPARENT); // if transparent then ecnoder must read ALPH
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), paint);

            //Calculate the ratios and decide which part will have empty areas (width or height)
            float ratioBitmap = (float)finalWidth / (float)finalHeight;
            float destinationRatio = (float) destinationWidth / (float) destinationHeight;
            float left = ratioBitmap >= destinationRatio ? 0 : (float)(destinationWidth - finalWidth) / 2;
            float top = ratioBitmap < destinationRatio ? 0: (float)(destinationHeight - finalHeight) / 2;
            canvas.drawBitmap(imageToScale, left, top, null);

            return scaledImage;
        } else {
            return imageToScale;
        }
    }

    public Bitmap compress(Bitmap yourBitmap){
    //converted into webp into lowest quality
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    
    yourBitmap.compress(Bitmap.CompressFormat.WEBP,0,stream);//0=lowest, 100=highest quality
    byte[] byteArray = stream.toByteArray();
    
    BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = true;

    //convert your byteArray into bitmap
    Bitmap yourCompressBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length,options);
    return yourCompressBitmap;
    }

}
