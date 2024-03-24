package com.abdullahsajjad.i212477

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import android.Manifest
import android.hardware.Camera
import android.view.Surface
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.Date

class TakePicture : AppCompatActivity() {

    private lateinit var dialog: AlertDialog
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService

    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    private var camera: Camera? = null
    private var isPreviewing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_picture)

        var close = findViewById<ImageView>(R.id.close)
        var btn = findViewById<CardView>(R.id.clickpicture)
        var switchtovid = findViewById<CardView>(R.id.swtichtovid)
        var switchtogallery = findViewById<CardView>(R.id.switchtogallery)

        dialog = AlertDialog.Builder(this)
            .setMessage("Uploading Picture...")
            .setCancelable(false)
            .create()

        cameraExecutor = Executors.newSingleThreadExecutor()

        /*if (allPermissionsGranted()) {
            openCamera()
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }*/

        close.setOnClickListener {
            finish()
        }

        btn.setOnClickListener {
            takePicture()
        }

        switchtogallery.setOnClickListener{
            val intent = Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        switchtovid.setOnClickListener{
            startActivity(
                Intent(this,
                    TakeVideo::class.java)
            );
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == 0 && resultCode == -1 && data != null) {
            dialog.show()
            val uri = data.data
            dialog.dismiss()
            dialog.setMessage("Picture Uploaded Successfully!")
            dialog.setCancelable(true)
            val resultIntent = Intent()
            resultIntent.putExtra("imageUri", uri)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun openCamera() {
        try {
            camera = Camera.open()
            setCameraDisplayOrientation()
            //camera?.setPreviewDisplay(previewView.holder)
            startPreview()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setCameraDisplayOrientation() {
        val info = Camera.CameraInfo()
        Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info)
        val rotation = windowManager.defaultDisplay.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }
        var result: Int
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360
            result = (360 - result) % 360 // compensate the mirror
        } else {
            result = (info.orientation - degrees + 360) % 360
        }
        camera?.setDisplayOrientation(result)
    }

    private fun startPreview() {
        if (!isPreviewing) {
            camera?.startPreview()
            isPreviewing = true
        }
    }

    private fun stopPreview() {
        if (isPreviewing) {
            camera?.stopPreview()
            isPreviewing = false
        }
    }

    private fun releaseCamera() {
        camera?.release()
        camera = null
    }

    private fun takePicture() {
        camera?.takePicture(null, null, Camera.PictureCallback { data, _ ->
            val pictureFile = getOutputMediaFile()
            if (pictureFile == null) {
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
                return@PictureCallback
            }
            try {
                val fos = FileOutputStream(pictureFile)
                fos.write(data)
                fos.close()
                Toast.makeText(this, "Image saved: ${pictureFile.absolutePath}", Toast.LENGTH_SHORT).show()
                startPreview()
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getOutputMediaFile(): File? {
        val mediaStorageDir = File(getExternalFilesDir(null), "Pictures")
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null
            }
        }
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return File(mediaStorageDir.path + File.separator + "IMG_$timeStamp.jpg")
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                openCamera()
            } else {
                Toast.makeText(
                    this,
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        releaseCamera()
    }
}