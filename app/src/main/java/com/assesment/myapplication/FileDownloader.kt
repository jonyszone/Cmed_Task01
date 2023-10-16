package com.assesment.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object FileDownloader {
    private val executor: Executor = Executors.newSingleThreadExecutor()

    fun downloadFile(context: Context, url: String, filename: String, progressBar: ProgressBar) {
        executor.execute(DownloadFileTask(context, url, filename, progressBar))
    }

    private class DownloadFileTask(
        private val context: Context,
        private val url: String,
        private val filename: String,
        private val progressBar: ProgressBar
    ) : Runnable {

        @SuppressLint("UnspecifiedRegisterReceiverFlag")
        override fun run() {
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
            val request = DownloadManager.Request(Uri.parse(url))
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
            request.setDescription("Downloading $filename")
            request.setDestinationInExternalFilesDir(
                context,
                Environment.DIRECTORY_DOWNLOADS,
                filename
            )
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            // Register a BroadcastReceiver to monitor the download progress and completion
            val downloadId = downloadManager.enqueue(request)
            val receiver = object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == intent.action) {
                        // showDownloadNotification(context, channelId, filename)
                    }
                }
            }

            // Register the BroadcastReceiver
            context.registerReceiver(
                receiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )

            var connection: HttpURLConnection? = null
            var output: OutputStream? = null
            var input: InputStream? = null
            try {
                val url = URL(url)
                connection = url.openConnection() as HttpURLConnection
                connection.connect()

                if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                    showError("Download failed")
                    return
                }

                val fileLength = connection.contentLength
                input = connection.inputStream
                output = context.openFileOutput(filename, Context.MODE_PRIVATE)

                val data = ByteArray(1024)
                var total: Long = 0
                var count: Int
                while (input.read(data).also { count = it } != -1) {
                    total += count.toLong()
                    output.write(data, 0, count)

                    val progress = ((total * 100 / fileLength).toInt())
                    updateProgress(progress)
                }
                showSuccess("Download completed")
            } catch (e: Exception) {
                e.printStackTrace()
                showError("Download failed")
            } finally {
                try {
                    output?.close()
                    input?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                connection?.disconnect()
            }
        }

        private fun updateProgress(progress: Int) {
            Handler(Looper.getMainLooper()).post { progressBar.progress = progress }
        }

        private fun showError(message: String) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }

        private fun showSuccess(message: String) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
