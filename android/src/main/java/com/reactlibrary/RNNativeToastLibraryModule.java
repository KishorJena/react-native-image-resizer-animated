
package com.reactlibrarynativetoast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.*;
import com.facebook.animated.webp.WebPImage;
import com.facebook.imagepipeline.common.ImageDecodeOptions;

import androidx.annotation.NonNull;


import android.content.Context;
import android.widget.Toast;
import android.util.Log;
import android.graphics.*;
import android.content.SharedPreferences;
import android.content.*;

import java.nio.*;
import java.util.*;
import java.io.*;
import java.util.concurrent.atomic.AtomicInteger;
// import java.io.File;
// import java.io.FileOutputStream;
// import java.io.FileInputStream;
// import java.io.InputStream;
// import java.io.IOException;


// standalone encoder WEBP
import com.n4no.webpencoder.webp.graphics.WebpBitmapEncoder;
// standalone decoder GIF
// import com.waynejo.androidndkgif.*;
// import com.waynejo.androidndkgif.GifDecoder;
// import com.waynejo.androidndkgif.GifEncoder;
// import com.waynejo.androidndkgif.GifImage;
// import com.waynejo.androidndkgif.GifImageIterator;


// File Loader
import com.github.penfeizhou.animation.loader.FileLoader;

// drawable WEBP, GIF
import com.github.penfeizhou.animation.webp.WebPDrawable;
import com.github.penfeizhou.animation.gif.GifDrawable;

// parse WEBP, GIF
import com.github.penfeizhou.animation.webp.decode.WebPParser;
import com.github.penfeizhou.animation.gif.decode.GifParser;

// Decoder for WEBP
import com.github.penfeizhou.animation.webp.decode.WebPDecoder;

// Decoder for GIF
import com.github.penfeizhou.animation.gif.decode.GifDecoder;

// Encoder for WEBP
import com.github.penfeizhou.animation.awebpencoder.WebPEncoder;

// Decoder
import com.github.penfeizhou.animation.loader.Loader;
import com.github.penfeizhou.animation.loader.FileLoader;
import com.github.penfeizhou.animation.decode.FrameSeqDecoder;
import com.github.penfeizhou.animation.decode.Frame;

// MISC
import com.github.penfeizhou.animation.FrameAnimationDrawable;

//
import java.lang.reflect.Method;

public class RNNativeToastLibraryModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;
  public static final String TAG =  "ReactNative";

  public RNNativeToastLibraryModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNResizer";
  }

  @ReactMethod
  public void ResizeStatic(String path, String folder, int h, int w, int q, Promise promise){
    if(! new File(path).exists())
      promise.reject("File not found");

    Bitmap bitmap = BitmapFactory.decodeFile(path);
    Bitmap scaledBitmap = scalePreserveRatio(bitmap, h, w, Color.TRANSPARENT);
    String newFile = getNewFileAt(folder,"webp");
    saveBitmap(scaledBitmap, newFile, q );
    promise.resolve(newFile);
  }

  @ReactMethod
  public void ResizeAnimated(String path, String folder, int h, int w, int q, Promise promise) {

    boolean isAWebP = WebPParser.isAWebP(path);
    boolean isGif = GifParser.isGif(path);
    // temporary for testing
    if(!new File(folder).exists()){
      boolean dirCreated = new File(folder).mkdirs();
      if (!dirCreated)
      promise.reject("failed to create dir");

    }
      
    if(isAWebP){
      FramesData fd = new DecodeWEBP().DecodeWebP2(reactContext, path);  
      // fix delay 
      fd.setListOfBitmaps(new ArrayList<Bitmap>(fixBitmaps(fd.getListOfBitmaps(),h,w,q)));
      byte[] bytes = EncodeToWEBP(fd.getListOfBitmaps(), fd.getListOfDelays());
      String newFilePath  = BytesToFIle(bytes, folder);
      
      if (newFilePath == null)
        promise.reject("failed to create file");

      promise.resolve(newFilePath);
    }else if (isGif){
      android.util.Log.d("ReactNative","Detecetd file is GIF");
      
      Transcoder transcoder = new Transcoder();
      FramesData fd = transcoder.DecodeGif2(path);
      // fix delay
      fd.setListOfBitmaps(new ArrayList<Bitmap>(fixBitmaps(fd.getListOfBitmaps(),h,w,q)));
      byte[] bytes = EncodeToWEBP(fd.getListOfBitmaps(), fd.getListOfDelays());
      String newFilePath  = BytesToFIle(bytes, folder);

      if (newFilePath == null)
        promise.reject("failed to create file");

      promise.resolve(newFilePath);
    }else{
      // ResizeStill(path, folder, promise);
      promise.reject("Not a GIF or WEBP");
    }

  }

  public byte[] EncodeToWEBP(List<Bitmap> bitmaps, List<Integer> delays ){
    WebPEncoder encoder = new WebPEncoder();
    for(int i=0; i<bitmaps.size(); i++){
      encoder.addFrame( new WebPEncoder.FrameBuilder()
      .bitmap(bitmaps.get(i))
      .duration(delays.get(i))
      .offsetX(0)
      .offsetY(0)
      .disposal(true)
      .blending(true)
      .build());
    }
    return encoder.build();
  }

  public List<Bitmap> fixBitmaps(List<Bitmap> bitmaps, int h, int w, int quality) {
    List<Bitmap> fixedBitmaps = new ArrayList<>();
    for (Bitmap bitmap : bitmaps) {

      fixedBitmaps.add(
        compress(
          scalePreserveRatio(bitmap, h, w, Color.TRANSPARENT),
          quality
        )
      );
      
    }
    return fixedBitmaps;
  }

  private String BytesToFIle(byte[] bytes, String folder){
    String file = getNewFileAt(folder, "webp");
    try (FileOutputStream stream = new FileOutputStream(file)) {
      stream.write(bytes);
    }catch (Exception e) {
      return null;
    }
    return file;
  }

  public String getRandomFileName() {
    return UUID.randomUUID().toString();
  }

  public String getNewFileAt(String folder,String ext) {
    // TODO later create all these files in a folder and delete it later
    // String root = reactContext.getCacheDir().getAbsolutePath();
    String root = folder;
    String filename =  root+"/" + getRandomFileName() + "." + ext;
    File newFile = new File(filename);
    return newFile.getAbsolutePath();
  }

  public String saveBitmap(Bitmap bitmap, String file, int quality) {
    try {
      FileOutputStream outputStream = new FileOutputStream(file);
      bitmap.compress(Bitmap.CompressFormat.WEBP, quality, outputStream);
      outputStream.flush();
      outputStream.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return file;
  }

  public Bitmap compress(Bitmap yourBitmap, int q){
    //converted into webp into lowest quality
    ByteArrayOutputStream stream = new ByteArrayOutputStream();
    
    yourBitmap.compress(Bitmap.CompressFormat.WEBP,q,stream);//0=lowest, 100=highest quality
    byte[] byteArray = stream.toByteArray();
    
    BitmapFactory.Options options = new BitmapFactory.Options();
    options.inScaled = true;

    //convert your byteArray into bitmap
    Bitmap yourCompressBitmap = BitmapFactory.decodeByteArray(byteArray,0,byteArray.length,options);
    return yourCompressBitmap;
  }

  public static Bitmap scalePreserveRatio(
      Bitmap imageToScale, 
      int destinationWidth, 
      int destinationHeight, 
      int color) {
    // NOTE 
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
  
}