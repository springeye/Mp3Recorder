## A recording library, lame based real time mp3 coding
see [lame](http://lame.sourceforge.net/)

[![Android CI](https://github.com/henjue/android_lame/actions/workflows/main.yml/badge.svg)](https://github.com/henjue/android_lame/actions/workflows/main.yml)
[![GitHub version](https://badge.fury.io/gh/henjue%2Fandroid_lame.svg)](https://badge.fury.io/gh/henjue%2Fandroid_lame)


# on build.gradle
```gradle
	dependencies {
	        implementation 'com.github.henjue:android_lame:Tag'
	}
```
## Java Code Use
```kotlin
val mRecorder = com.github.henjue.lame.Mp3Recorder(file);
mRecorder.startRecording();
```
