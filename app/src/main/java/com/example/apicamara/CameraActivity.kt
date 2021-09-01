package com.example.apicamara

import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import com.example.apicamara.databinding.ActivityCameraBinding
import java.io.File
import java.util.concurrent.Executors

class CameraActivity : AppCompatActivity() {

    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startCamera()
    }

    private fun startCamera(){
        val previewConfig = PreviewConfig.Builder().apply {
            //prender flash, autofocus, etc
            setTargetResolution(Size(640,480))
        }.build()

        val preview = Preview(previewConfig)

        preview.setOnPreviewOutputUpdateListener{
            val parent = binding.parent as ViewGroup
            parent.removeView(binding.cameraPreview)
            parent.addView(binding.cameraPreview, 0)

            binding.cameraPreview.setSurfaceTexture(it.surfaceTexture)
        }

        val imageCapture = captureConfig()

        CameraX.bindToLifecycle(this, preview,imageCapture)

    }

    private fun captureConfig():ImageCapture{
        val imageCaptureConfig = ImageCaptureConfig.Builder().apply {
            setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
        }.build()

        val imageCapture = ImageCapture(imageCaptureConfig)

        //Guardar archivo
        binding.captureButton.setOnClickListener {
            val file = File(externalMediaDirs.first(),"${System.currentTimeMillis()}.jpg")

            imageCapture.takePicture(file, executor, object: ImageCapture.OnImageSavedListener{
                override fun onImageSaved(file: File) {
                    val message = "Imagen guardada: ${file.absolutePath}"
                    Log.d("Camera", message)

                    binding.cameraPreview.post{
                        Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                    }
                }

                override fun onError(
                    imageCaptureError: ImageCapture.ImageCaptureError,
                    message: String,
                    cause: Throwable?
                ) {
                    binding.cameraPreview.post{
                        Toast.makeText(applicationContext,message,Toast.LENGTH_LONG).show()
                    }
                }

            })
        }
        return imageCapture
    }
}