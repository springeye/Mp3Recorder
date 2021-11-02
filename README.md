## A recording library, lame based real time mp3 coding
see [lame](http://lame.sourceforge.net/)

[![Android CI](https://github.com/henjue/Mp3Recorder/actions/workflows/main.yml/badge.svg)](https://github.com/henjue/Mp3Recorder/actions/workflows/main.yml)
[![GitHub version](https://badge.fury.io/gh/henjue%2FMp3Recorder.svg)](https://badge.fury.io/gh/henjue%2FMp3Recorder)


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
