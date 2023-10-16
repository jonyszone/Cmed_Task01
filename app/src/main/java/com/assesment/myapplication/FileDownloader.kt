package com.assesment.myapplication

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.Toast
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executor
import java.util.concurrent.Executors

object FileDownloader {
    // for downloading a tasks
    private val executor: Executor = Executors.newSingleThreadExecutor()

    private class DownloadFileTask internal constructor(
        private val context: Context,
        private val url: String?,
        private val filename: String?,
        private val progressBar: ProgressBar
    ) :
        Runnable {
        override fun run() {
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
                    if (fileLength > 0) {
                        val progress = (total * 100 / fileLength).toInt()
                        updateProgress(progress)
                    }
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

        fun downloadFile(context: Context, url: String?, filename: String?, progressBar: ProgressBar) {
            executor.execute(DownloadFileTask(context, url, filename, progressBar))
        }

        private fun updateProgress(progress: Int) {
            Handler(Looper.getMainLooper()).post { progressBar.progress = progress }
        }

        private fun showError(message: String) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        private fun showSuccess(message: String) {
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(
                    context,
                    message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}