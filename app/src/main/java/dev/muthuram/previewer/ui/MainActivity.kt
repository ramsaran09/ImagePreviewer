package dev.muthuram.previewer.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.yalantis.ucrop.UCrop
import dev.muthuram.previewer.R
import dev.muthuram.previewer.databinding.ActivityMainBinding
import dev.muthuram.previewer.helper.observeLiveData
import dev.muthuram.previewer.helper.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private val binding by viewBinding(ActivityMainBinding::inflate)

    private val requestPermissionLauncher by lazy {
        registerForActivityResult(
            ActivityResultContracts.RequestPermission(),
            viewModel::permissionResult
        )
    }

    private val requestPictureLauncher by lazy {
        registerForActivityResult(
            ActivityResultContracts.TakePicture()
        ) {
            viewModel.pictureResult(this,it)
        }
    }

    private val requestDocumentLauncher by lazy {
        registerForActivityResult(
            ActivityResultContracts.GetContent()
        ) { photoUri ->
            viewModel.documentResult(this, photoUri)
        }
    }

    private val requestActivityResultLauncher by lazy {
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            viewModel::processActivityResult
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpUi()
        setUpListeners()
    }

    private fun setUpUi() {
        requestPermissionLauncher
        requestPictureLauncher
        requestDocumentLauncher
        requestActivityResultLauncher
        viewModel.checkPermission.observeLiveData(this, ::checkAndRequestPermission)
        viewModel.pickDocument.observeLiveData(this, ::onPickDocument)
        viewModel.error.observeLiveData(this, ::showToast)
        viewModel.croppedImage.observeLiveData(this, ::processImageCrop)
    }

    private fun setUpListeners() {
        binding.uiIvCamera.setOnClickListener {
            viewModel.checkPermission(CAPTURE_IMAGE_FROM_CAMERA)
        }
        binding.uiIvGallery.setOnClickListener {
            viewModel.checkPermission(CAPTURE_IMAGE_FROM_GALLERY)
        }
    }

    private fun checkAndRequestPermission(requestCode: String) {
        if (requestCode == CAPTURE_IMAGE_FROM_CAMERA) {
            requestCameraPermission()
        } else requestGalleryPermission()
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.permissionResult(true)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun requestGalleryPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            viewModel.permissionResult(true)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    private fun onPickDocument(requestCode: String) {
        if (requestCode == CAPTURE_IMAGE_FROM_CAMERA) {
            getImageFromCamera()
        } else getImageFromGallery()
    }

    private fun getImageFromCamera() {
        val capturedImgFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            getString(R.string.file_path)
        )
        val captureImgUri = FileProvider.getUriForFile(
            this,
            "${getString(R.string.authority_string)}.fileprovider",
            capturedImgFile
        )
        viewModel.setUriForCamera(captureImgUri)
        requestPictureLauncher.launch(captureImgUri)
    }

    private fun getImageFromGallery() {
        requestDocumentLauncher.launch("image/*")
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun processImageCrop(uris: Pair<Uri,Uri>) {
        Log.d("MainActivity", "processImageCrop: ${uris.first} : ${uris.second}")
        UCrop.of(uris.first, uris.second)
            .withMaxResultSize(1000, 1000)
            .start(this)
    }

    private fun showCroppedImage(uri: Uri?) {
        Glide.with(this).load(uri).into(binding.uiIvImagePreview)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri = UCrop.getOutput(data!!)
            Log.d("MainActivity", "processActivityResult: $resultUri")
            showCroppedImage(resultUri)
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError = UCrop.getError(data!!)
            Log.d("MainActivity", "processActivityResult: $cropError")
        }
    }

    companion object {
        const val CAPTURE_IMAGE_FROM_CAMERA = "1001"
        const val CAPTURE_IMAGE_FROM_GALLERY = "1002"
    }
}