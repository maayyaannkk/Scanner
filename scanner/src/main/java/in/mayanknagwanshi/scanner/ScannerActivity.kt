package `in`.mayanknagwanshi.scanner

import `in`.mayanknagwanshi.scanner.analyser.QrCodeAnalyser
import `in`.mayanknagwanshi.scanner.analyser.TextAnalyser
import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Window
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_scanner.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScannerActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_scanner)

        cameraExecutor = Executors.newSingleThreadExecutor()

        if (intent != null && (intent.hasExtra(FLAG_TEXT) || intent.hasExtra(FLAG_QR))) {
            if (checkCameraPermission()) {
                startCamera(intent.getBooleanExtra(FLAG_QR, false))
            } else {
                requestCameraPermission()
            }
        } else {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun startCamera(isQrCodeAnalyser: Boolean) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .apply {
                    //Set the resolution of the captured image
                    setTargetResolution(Size(480, 360))
                }.build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            val imageAnalysis = ImageAnalysis.Builder()
                .build()
                .also {
                    if (isQrCodeAnalyser)
                        it.setAnalyzer(cameraExecutor, QrCodeAnalyser { rawText ->
                            it.clearAnalyzer()
                            finishWithResult(rawText)
                        })
                    else
                        it.setAnalyzer(cameraExecutor, TextAnalyser { rawText ->
                            it.clearAnalyzer()
                            finishWithResult(rawText)
                        })
                }


            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis
                )

            } catch (exc: Exception) {
                Log.e("ScannerLib", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private fun checkCameraPermission(): Boolean {
        val currentAPIVersion = Build.VERSION.SDK_INT
        return currentAPIVersion < Build.VERSION_CODES.M || ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            CAMERA_CODE_PERMISSIONS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_CODE_PERMISSIONS) {
            if (checkCameraPermission()) {
                startCamera(intent.getBooleanExtra(FLAG_QR, false))
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()

                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun finishWithResult(result: String) {
        val intent = Intent()
        intent.putExtra(RESULT_STRING, result)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    companion object {
        val RESULT_STRING = "result_string"
        private const val CAMERA_CODE_PERMISSIONS = 11
        private const val FLAG_TEXT = "flag_text"
        private const val FLAG_QR = "flag_qr"

        fun startActivityForTextRecognition(activity: Activity, requestCode: Int) {
            val intent = Intent(activity, ScannerActivity::class.java)
            intent.putExtra(FLAG_TEXT, true)
            activity.startActivityForResult(intent, requestCode)
        }

        fun startActivityForTextRecognition(fragment: Fragment, requestCode: Int) {
            val intent = Intent(fragment.activity, ScannerActivity::class.java)
            intent.putExtra(FLAG_TEXT, true)
            fragment.startActivityForResult(intent, requestCode)
        }

        fun startActivityForQrCode(activity: Activity, requestCode: Int) {
            val intent = Intent(activity, ScannerActivity::class.java)
            intent.putExtra(FLAG_QR, true)
            activity.startActivityForResult(intent, requestCode)
        }

        fun startActivityForQrCode(fragment: Fragment, requestCode: Int) {
            val intent = Intent(fragment.activity, ScannerActivity::class.java)
            intent.putExtra(FLAG_QR, true)
            fragment.startActivityForResult(intent, requestCode)
        }
    }
}