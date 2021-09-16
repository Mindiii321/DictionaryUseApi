package com.example.dictionaryuseapi

import android.content.Intent
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.dictionaryuseapi.databinding.ActivityMainBinding
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList
import android.media.MediaPlayer
import android.view.inputmethod.EditorInfo
import java.lang.Exception


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding
    var str: String = ""
    var text: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        /* direct search to keyboard btn */
        binding.editSearch.setOnEditorActionListener { v, actionId, event ->
            val handled = false
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val str = binding.editSearch.text.toString()

                binding.progressBar.visibility = View.VISIBLE

                if (str.isNotEmpty()) {
                    sendRequest(str)

                } else {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, "Please enter any word", Toast.LENGTH_SHORT).show()
                }
            }
            handled

        }


    }

    //name place write anything//request receive from name
    private fun sendRequest(name: String) {
        val url =
            "https://api.dictionaryapi.dev/api/v2/entries/en/$name"//request api and than print response
        val stringRequest = StringRequest(Request.Method.GET, url,
            { response ->
                binding.progressBar.visibility = View.GONE

                val type: Type = object : TypeToken<ArrayList<ModelClassItem>>() {

                }.type
                // Log.d(TAG, "sendRequest: $response")
                val gson: ArrayList<ModelClassItem> = Gson().fromJson<ArrayList<ModelClassItem>>(
                    response, type
                )
                val builder = StringBuilder()
                builder.append(gson[0].word[0]).append("\n").append(gson[0].phonetic[0])
                    .append("\n").append(gson[0].phonetics[0]).append("\n")
                    .append(gson[0].phonetics[0].text[0]).append("\n")
                    .append(gson[0].phonetics[0].audio[0]).append("\n")
                    .append(gson[0].meanings[0]).append("\n")
                    .append(gson[0].meanings[0].definitions[0]).append("\n")
                    .append(gson[0].meanings[0].definitions[0].definition[0]).append("\n")
                //set the data in textview
                binding.tvResultName.text = (gson[0].word)
                binding.tvResultPhonetics.text = (gson[0].phonetic)
                binding.tvResultText.text = (gson[0].phonetics[0].text)
                binding.tvResultDefinition.text = (gson[0].meanings[0].definitions[0].definition)
                text = (gson[0].phonetics[0].audio)//link
                // binding.tvResult.text = url


                Log.d(TAG, "gsontest: $builder")
            })
        { error ->
            binding.editSearch.setText("")//clear text edittext
            binding.progressBar.visibility = View.GONE
            Toast.makeText(this, "invalid word Plz fill the right word", Toast.LENGTH_SHORT).show()//invalid word api word match and

        }
        val queue = Volley.newRequestQueue(this)
        queue.add(stringRequest)
    }

    //  this code are using  voice to search data fill
    fun getSpeechInput(view: android.view.View) {

        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, 10)

        } else {
            Toast.makeText(this, "your device does not support voice ", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            10 -> {
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String>()
                    val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
                    binding.editSearch.setText(result?.get(0))
                    // binding.tvResult.text =result?.get(0)
                    result?.get(0)
                        ?.let { sendRequest(it) }//this code are using direct voice to search
                }
            }

        }
    }

    //text to speech code using link
    fun getInputToSpeech(view: View) {
        val audioUrl = "https:$text"
        val player = MediaPlayer()
        player.setAudioStreamType(AudioManager.STREAM_MUSIC)
        try {
            player.setDataSource(audioUrl)
            player.prepare()
            player.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }


        /* text to speech code
          textToSpeech = TextToSpeech(applicationContext, TextToSpeech.OnInitListener {
               if (it == TextToSpeech.SUCCESS) {
                   textToSpeech.language = Locale.US
                   textToSpeech.setSpeechRate(1.0f)
                   textToSpeech.speak(binding.tvResultName.text.toString(),
                       TextToSpeech.QUEUE_ADD,
                       null)
               }
           })*/


    }
}