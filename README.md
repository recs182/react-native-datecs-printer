# THIS IS A WORK IN PROGRESS, DO NOT USE

# react-native-datecs-printer

## Getting started

`$ npm install react-native-datecs-printer --save`

### Mostly automatic installation

`$ react-native link react-native-datecs-printer`

### Manual installation

#### Android

1. Open up `android/app/src/main/java/[...]/MainActivity.java`
  - Add `import com.reactlibrary.RNDatecsPrinterPackage;` to the imports at the top of the file
  - Add `new RNDatecsPrinterPackage()` to the list returned by the `getPackages()` method
2. Append the following lines to `android/settings.gradle`:
  	```
  	include ':react-native-datecs-printer'
  	project(':react-native-datecs-printer').projectDir = new File(rootProject.projectDir, 	'../node_modules/react-native-datecs-printer/android')
  	```
3. Insert the following lines inside the dependencies block in `android/app/build.gradle`:
  	```
      compile project(':react-native-datecs-printer')
  	```


## Usage
```javascript
import DatecsPrinter from 'react-native-datecs-printer';

// TODO: What to do with the module?
RNDatecsPrinter;
```
  
