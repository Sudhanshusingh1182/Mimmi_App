package com.example.mimmiapp

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val apiUrl = "https://meme-api.com/gimme/2"
    private val client = OkHttpClient()
    private lateinit var imageView: ImageView
    private lateinit var btnLoadMeme: Button
    private lateinit var btnShareMeme: Button
    private lateinit var progressBar: ProgressBar
    var currentImageurl : String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.memeImageView)
        btnLoadMeme = findViewById(R.id.NextButton)
        progressBar = findViewById(R.id.progressBar)
        btnShareMeme = findViewById(R.id.ShareButton)
        btnLoadMeme.setOnClickListener {
            loadMeme()
        }

        btnShareMeme.setOnClickListener {
            shareMeme()
        }
    }

    private fun loadMeme() {
        progressBar.visibility = View.VISIBLE
        val request = Request.Builder()
            .url(apiUrl)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()

                try {
                    val jsonResponse = JSONObject(responseData)
                    val memesArray = jsonResponse.getJSONArray("memes")
                    val memeObject = memesArray.getJSONObject(0) // Choose the first meme

                    val memeUrl = memeObject.getString("url")

                    runOnUiThread {
                        // Use Glide to load the meme image into the ImageView
                        Glide.with(this@MainActivity)
                            .load(memeUrl)
                            .listener(object : RequestListener<Drawable> {

                                override fun onLoadFailed(
                                    e: com.bumptech.glide.load.engine.GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    progressBar.visibility = View.GONE
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable,
                                    model: Any,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    progressBar.visibility = View.GONE
                                    currentImageurl = memeUrl
                                    return false
                                }
                            })
                            .into(imageView)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }

     fun shareMeme(){
         val intent = Intent(Intent.ACTION_SEND)
         intent.type = "text/plain"
         intent.putExtra(Intent.EXTRA_TEXT ,"Hey ,Checkout this cool meme I got from reddit $currentImageurl")
         //create a chooser
         val chooser = Intent.createChooser(intent ,"Share this meme using ...")
         startActivity(chooser)
     }

    fun nextMeme(){
        loadMeme()
    }
}
