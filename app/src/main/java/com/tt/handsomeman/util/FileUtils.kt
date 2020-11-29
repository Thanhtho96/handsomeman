package com.tt.handsomeman.util

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import com.tt.handsomeman.BuildConfig
import com.tt.handsomeman.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    @Throws(IOException::class)
    private fun createTempImage(context: Context): Pair<File, String> {
        val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val timeStamp =
                SimpleDateFormat(
                        Constants.dateTimeSecondFileNamePattern,
                        Locale.getDefault()
                ).format(Date())
        val imageFileName =
                "${Constants.FILE_TYPE.IMAGE.fileSuffix}${timeStamp}_"
        val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",  /* suffix */
                storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        return Pair(image, image.absolutePath)
    }

    @JvmStatic
    fun onSelectFromGalleryResult(activity: Activity, data: Uri?, fileType: Constants.FILE_TYPE): String? {
        if (data != null) {
            val storageDir =
                    when (fileType) {
                        Constants.FILE_TYPE.IMAGE -> {
                            activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                        }
                        Constants.FILE_TYPE.PDF -> {
                            activity.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
                        }
                    }

            val currentPhotoPath = getFileNameFromUri(activity, data)?.let {
                "$storageDir/$it"
            }

            val inputStream = activity.contentResolver.openInputStream(data)!!
            currentPhotoPath?.also {
                inputStream.use { input ->
                    val file = File(it)
                    FileOutputStream(file).use { output ->
                        val buffer =
                                ByteArray(4 * 1024) // or other buffer size
                        var read: Int
                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                        }
                        output.flush()
                    }
                    if (fileType == Constants.FILE_TYPE.PDF) {
                        val fileSize = (File(it).length() / 1024F / 1024F).toString().toFloat()
                        if (fileSize > Constants.MAX_UPLOAD_FILE_SIZE) {
                            Toast.makeText(activity, activity.getString(R.string.file_too_large), Toast.LENGTH_SHORT).show()
                            return null
                        }
                    }
                }
            }
            return currentPhotoPath
        }
        return null
    }

    @JvmStatic
    fun dispatchTakePictureIntent(
            activity: Activity,
            fragment: Fragment?,
            requestCode: Int
    ): String? {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(activity.packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            var fileUri: String? = null
            try {
                createTempImage(activity).also {
                    photoFile = it.first
                    fileUri = it.second
                }
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                        activity,
                        BuildConfig.APPLICATION_ID + Constants.fileProviderExtension,
                        photoFile!!
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                if (fragment != null) {
                    fragment.startActivityForResult(
                            takePictureIntent,
                            requestCode
                    )
                } else {
                    activity.startActivityForResult(
                            takePictureIntent,
                            requestCode
                    )
                }
            }
            return fileUri
        }
        return null
    }

    private fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        if (ContentResolver.SCHEME_FILE == uri.scheme) {
            uri.path?.also {
                val file = File(it)
                fileName = file.name
            }
        } else if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val returnCursor: Cursor? = context.contentResolver.query(uri, null, null, null, null)
            if (returnCursor != null && returnCursor.moveToFirst()) {
                val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                fileName = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return fileName
    }
}