# installation : 
```npm i react-native-image-resizer-animated```

# Description :
I have built this for my project. There was no module to resize the animated images (gif and animated webp). So I created this one and shared to help others who are seeking for such functionality in React Native.

# Usage:
 ``` js
// for animated
 RNResize.ResizeAnimated(sourceFile, destination_folder_path,hight,width,quality).then(res=>"file://"+res)
 // for static
 RNResize.ResizeStatic(sourceFile, destination_folder_path,hight,width,quality).then(res=>"file://"+res)

 # Note :
 If it helped you then you can let me know or if want to ask or suggest something regarding this module create and issue

 Twitter: https://twitter.com/heyKSR
