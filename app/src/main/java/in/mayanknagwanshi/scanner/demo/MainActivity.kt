package `in`.mayanknagwanshi.scanner.demo

import `in`.mayanknagwanshi.scanner.ScannerActivity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buttonScanner.setOnClickListener {
            ScannerActivity.startActivityForTextRecognition(this, REQUEST_CODE_SCANNER)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SCANNER) {

        }
    }

    companion object {
        private const val REQUEST_CODE_SCANNER = 11
    }
}