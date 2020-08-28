# Scanner
Android library to scan QR codes and text recognition.
This library is a wrapper built on top of CameraX and MLKit making it easier and faster to implement it for common use cases.

# Download [![](https://jitpack.io/v/maayyaannkk/Scanner.svg)](https://jitpack.io/#maayyaannkk/Scanner)

Add this to your project's `build.gradle`

```groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

And add this to your module's `build.gradle` 

```groovy
dependencies {
	implementation 'com.github.maayyaannkk:Scanner:x.y.z'
}
```

change `x.y.z` to version in [![](https://jitpack.io/v/maayyaannkk/Scanner.svg)](https://jitpack.io/#maayyaannkk/Scanner)

## Usage

For full example, please refer to `app` module

No need to request or define camera permission, library will do that.
### Start the scanner activity

The simplest way to start the QR code scanner is
```kotlin
ScannerActivity.startActivityForQrCode(this, 1213)
```
or to start the text recognition
```kotlin
ScannerActivity.startActivityForTextRecognition(this, 1213)
```
Receive result
```kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == 1213 && resultCode == Activity.RESULT_OK) {
            val resultText = data.getStringExtra(ScannerActivity.RESULT_STRING) ?: ""
        }
    }
```
