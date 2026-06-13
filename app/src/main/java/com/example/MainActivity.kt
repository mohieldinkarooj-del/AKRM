package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ui.AkrmApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.AkrmViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: AkrmViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    enableEdgeToEdge()
    checkAndRequestPermissions()

    setContent {
      MyApplicationTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
          AkrmApp(viewModel = viewModel)
        }
      }
    }
  }

  private fun checkAndRequestPermissions() {
    val permissionsNeeded = mutableListOf<String>()
    
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) 
        != PackageManager.PERMISSION_GRANTED) {
      permissionsNeeded.add(Manifest.permission.RECORD_AUDIO)
    }

    if (permissionsNeeded.isNotEmpty()) {
      ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), 101)
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<String>,
    grantResults: IntArray
  ) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    if (requestCode == 101) {
      if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        Toast.makeText(this, "شكراً! تم تمكين المايكروفون للتسجيل العائلي.", Toast.LENGTH_SHORT).show()
      } else {
        Toast.makeText(this, "يمكنك استخدام التطبيق، ولكن لن تتمكن من تسجيل أصوات عائلية مخصصة حتى تفعّل إذن المايكروفون.", Toast.LENGTH_LONG).show()
      }
    }
  }
}

