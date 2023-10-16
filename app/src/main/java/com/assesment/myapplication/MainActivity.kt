package com.assesment.myapplication

import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var downloadButton: Button
    private lateinit var downloadProgressBar: ProgressBar
    private lateinit var downloadDirectory: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        downloadButton = findViewById(R.id.downloadButton)
        downloadProgressBar = findViewById(R.id.downloadProgressBar)
        downloadDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath
        val downloadUrl = /*"https://www.dropbox.com/scl/fi/c6d7afycbjmgham4yxzgp/file_example_MP4_480_1_5MG.mp4?rlkey=esw105rz6lt34js6s7mcdzrmo&dl=0"; */ "https://file-examples.com/storage/feaade38c1651bd01984236/2017/04/file_example_MP4_480_1_5MG.mp4"
        val fileName = "assessment task file.mp4"


    }
}

