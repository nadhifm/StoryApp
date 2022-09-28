package com.nadhifm.storyapp.utils

import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Environment
import android.os.StrictMode
import android.view.LayoutInflater
import android.view.View
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.nadhifm.storyapp.R
import com.nadhifm.storyapp.databinding.LoadingDialogBinding
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)

    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()

    return myFile
}

fun reduceFileImage(file: File): File {
    val bitmap = BitmapFactory.decodeFile(file.path)

    var compressQuality = 100
    var streamLength: Int

    do {
        val bmpStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, bmpStream)
        val bmpPicByteArray = bmpStream.toByteArray()
        streamLength = bmpPicByteArray.size
        compressQuality -= 5
    } while (streamLength > 1000000)

    bitmap.compress(Bitmap.CompressFormat.JPEG, compressQuality, FileOutputStream(file))

    return file
}

fun showErrorSnackbar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(ContextCompat.getColor(view.context, R.color.red))
        .setTextColor(ContextCompat.getColor(view.context, R.color.white))
        .show()
}

fun showSuccessSnackbar(view: View, message: String) {
    Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
        .setBackgroundTint(ContextCompat.getColor(view.context, R.color.green))
        .setTextColor(ContextCompat.getColor(view.context, R.color.white))
        .show()
}

fun createLoadingDialog(context: Context, layoutInflater: LayoutInflater): Dialog {
    val dialogBinding = LoadingDialogBinding.inflate(layoutInflater)
    val loadingDialog = Dialog(context)

    loadingDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    loadingDialog.setContentView(dialogBinding.root)
    loadingDialog.setCancelable(false)

    return loadingDialog
}

fun formatDate(stringDate: String): String {
    val localeID = Locale("in", "ID")
    val dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", localeID)
    val date = dateFormatter.parse(stringDate)
    val stringDateFormatter = SimpleDateFormat("dd MMMM yyyy", localeID)
    return date?.let { stringDateFormatter.format(it) } ?: ""
}

fun bitmapFromURL(context: Context, urlString: String): Bitmap {
    return try {
        /* allow access content from URL internet */
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        /* fetch image data from URL */
        val url = URL(urlString)
        val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val input: InputStream = connection.inputStream
        BitmapFactory.decodeStream(input)
    } catch (e: IOException) {
        BitmapFactory.decodeResource(context.resources, R.drawable.ic_image)
    }
}

fun resizeBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
    val width = bm.width
    val height = bm.height
    val scaleWidth = newWidth.toFloat() / width
    val scaleHeight = newHeight.toFloat() / height

    /* init matrix to resize bitmap */
    val matrix = Matrix()
    matrix.postScale(scaleWidth, scaleHeight)

    /* recreate new bitmap as new defined size */
    val resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)
    bm.recycle()
    return resizedBitmap
}
