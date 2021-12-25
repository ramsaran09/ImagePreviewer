package dev.muthuram.previewer.ui

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import androidx.activity.result.ActivityResult
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dev.muthuram.previewer.models.DataHolder
import dev.muthuram.previewer.utils.ERROR_FILE_PATH
import dev.muthuram.previewer.utils.convertBitmapToFile
import dev.muthuram.previewer.utils.getBitmap
import java.io.File
import java.util.*

class MainViewModel : ViewModel() {

    private val _checkPermission = MutableLiveData<String>()
    private val _pickDocument = MutableLiveData<String>()
    private val _croppedImage = MutableLiveData<Pair<Uri,Uri>>()
    private val _error = MutableLiveData<String>()
    private val selectedDocument = DataHolder<Uri>()

    val checkPermission : LiveData<String> = _checkPermission
    val pickDocument : LiveData<String> = _pickDocument
    val croppedImage : LiveData<Pair<Uri,Uri>> = _croppedImage
    val error : LiveData<String> = _error

    fun setUriForCamera(imageUri: Uri) {
        selectedDocument.set(imageUri)
    }

    fun checkPermission(requestCode : String) {
        _checkPermission.value = requestCode
    }

    fun permissionResult(result : Boolean) {
        if (result) {
            _pickDocument.value = _checkPermission.value
        }
    }

    fun pictureResult(context: Context,isSuccess : Boolean) {
        if (isSuccess) {
            selectedDocument.get()?.let { processCapturedImage(context,it) }
        }
    }

    private fun processCapturedImage(context : Context,path:  Uri) {
        val capturedBitmap: Bitmap? = getBitmap(context, path)
        /*We are storing the above bitmap at different location where we can access it.*/
        val capturedImgFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            UUID.randomUUID().toString() + "_capturedImg.jpg")

        if (capturedBitmap != null) {
            convertBitmapToFile(capturedImgFile, capturedBitmap)
        }
        /*We have to again create a new file where we will save the processed image.*/
        val croppedImgFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            UUID.randomUUID().toString() + "_capturedImg.jpg")
        _croppedImage.value = Uri.fromFile(capturedImgFile) to Uri.fromFile(croppedImgFile)
    }

    fun documentResult(context: Context, sourceUri: Uri) {
        val selectedBitmap: Bitmap? = getBitmap(context, sourceUri)
        /*We are storing the above bitmap at different location where we can access it.*/
        val selectedImgFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            UUID.randomUUID().toString() + "_selectedImg.jpg")

        if (selectedBitmap != null) {
            convertBitmapToFile(selectedImgFile, selectedBitmap)
        }
        /*We have to again create a new file where we will save the processed image.*/
        val croppedImgFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            UUID.randomUUID().toString() + "_selectedImg.jpg")
        _croppedImage.value = Uri.fromFile(selectedImgFile) to Uri.fromFile(croppedImgFile)
    }

    fun processActivityResult(result : ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null) {
               // _croppedImage.value = result.data.getS
            } else {
                _error.value = ERROR_FILE_PATH
            }
        } else {
            _error.value = ERROR_FILE_PATH
        }
    }
}