package `in`.mayanknagwanshi.scanner.demo

import `in`.mayanknagwanshi.scanner.ScannerActivity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonScannerQr.setOnClickListener {
            ScannerActivity.startActivityForQrCode(this, REQUEST_CODE_SCANNER)
        }
        buttonScannerText.setOnClickListener {
            ScannerActivity.startActivityForTextRecognition(this, REQUEST_CODE_SCANNER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null && requestCode == REQUEST_CODE_SCANNER && resultCode == Activity.RESULT_OK) {
            val resultText = data.getStringExtra(ScannerActivity.RESULT_STRING) ?: ""
            textView.text = resultText
        }
    }

    companion object {
        private const val REQUEST_CODE_SCANNER = 11
    }
}