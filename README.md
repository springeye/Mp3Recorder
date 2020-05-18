## A recording library, lame based real time mp3 coding
see [lame](http://lame.sourceforge.net/)

## Latest Version: 
[![Download](https://api.bintray.com/packages/henjue/maven/Mp3Recorder/images/download.svg) ](https://bintray.com/henjue/maven/Mp3Recorder/_latestVersion)
# Use On Gradle
root build.gradle
```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
#on build.gradle
```gradle
	dependencies {
	        implementation 'com.github.henjue:Mp3Recorder:Tag'
	}
```
## Java Code Use
```java
mRecorder = new Mp3Recorder(file);
mRecorder.startRecording();
```
