## Installation :  
```bash
npm i react-native-image-resizer-animated
```  
I did not check about the compatibility and types safety.  
You can create an issue so that I can update accordingly.  
You might need to link manually or link command if you are on older version of React Native.  


## Description :  
I have built this for my project. There was no module to resize the animated images (gif and animated webp). So I created this one and shared to help others who are seeking for such functionality in React Native.
  
## Usage :  
 ``` js
 import RNResize from react-native-image-resizer-animated
 // for animated
 RNResize.ResizeAnimated(sourceFile, destination_folder_path,hight,width,quality).then(res=>"file://"+res)
 // for static 
 RNResize.ResizeStatic(sourceFile, destination_folder_path,hight,width,quality).then(res=>"file://"+res)
``` 

### TODO   
( for now it seems that nobody would need this module, So I am leaving these TODOs but you can create an issue for below features or any other functionality. )
- Resizing modes - fit, cover, stretch
- Attach extra information - absolute path, uri, scale, file size
- Converter - convert from one file to another
- Set output image type  

### Note :
If you want to ask or suggest something regarding this module create an issue or ping me on
Twitter: https://twitter.com/heyKSR
