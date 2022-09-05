
### PUBLISHING in Progress...
# Refactoring codes and writting instructions. Do not install right now.

# react-native-image-resizer-animated

## Getting started

`$ npm i react-native-image-resizer-animated`

### Mostly automatic installation

`$ react-native link react-native-image-resizer-animated`

### Manual installation


#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNNativeToastLibraryPackage;` to the imports at the top of the file
  - Add `new RNNativeToastLibraryPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-native-toast-library'
  	project(':react-native-native-toast-library').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-native-toast-library/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-native-toast-library')
  	```


## Usage
```javascript
import RNNativeToastLibrary from 'react-native-native-toast-library';

// TODO: What to do with the module?
RNNativeToastLibrary;
```
  