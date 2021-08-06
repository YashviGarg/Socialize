package com.example.socialize

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


class MyViewModel(): ViewModel() {
    var url:URL?=null
    var TAG:String="ViewModel"
    constructor(str:String):this(){
        this.url = URL(str)
    }

    fun execute() = viewModelScope.launch {
        onPreExecute()
        val result = doInBackground() // runs in background thread without blocking the Main Thread
        onPostExecute(result)
    }

    private suspend fun doInBackground(): String = withContext(Dispatchers.IO) { // to run code in Background Thread
        // do async work

        try {

            val urlConnect=url!!.openConnection() as HttpURLConnection
            urlConnect.connectTimeout=7000

            var inString= ConvertStreamToString(urlConnect.inputStream)
            //Cannot access to ui
            try{
                var json= JSONObject(inString)
                Log.d(TAG, "doInBackground: "+json.getString("msg"))
                if(json.getString("msg")=="user is added"){
                    //do something
                }



            }catch (ex:Exception){}
        }catch (ex:Exception){}

        return@withContext "SomeResult"
    }

    fun ConvertStreamToString(inputStream: InputStream):String{

        val bufferReader= BufferedReader(InputStreamReader(inputStream))
        var line:String
        var AllString:String=""

        try {
            do{
                line=bufferReader.readLine()
                if(line!=null){
                    AllString+=line
                }
            }while (line!=null)
            inputStream.close()
        }catch (ex:Exception){}



        return AllString
    }
    // Runs on the Main(UI) Thread
    private fun onPreExecute() {
        // show progress
    }

    // Runs on the Main(UI) Thread
    private fun onPostExecute(result: String) {
        // hide progress
    }


}


