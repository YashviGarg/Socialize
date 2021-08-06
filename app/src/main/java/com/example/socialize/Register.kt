package com.example.socialize

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_register.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*


class Register : AppCompatActivity() {
    var mAuth:FirebaseAuth?=null
    val TAG:String="RegisterActivity"
    var img:ImageView?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        mAuth = FirebaseAuth.getInstance()
        signInAnonymously()

        img=findViewById(R.id.profileImageRegister)

        img!!.setOnClickListener(View.OnClickListener {
            checkPermission()
        })




    }

    private fun signInAnonymously() {
        mAuth!!.signInAnonymously().addOnCompleteListener(this,{ task ->
            Log.d(TAG, "signInAnonymously: " + task.isSuccessful.toString())
        })
    }

    fun registerOnClick(view: View){
        SaveImageInFirebase()
    }

    val READIMAGE:Int=253
    fun checkPermission(){
        if(Build.VERSION.SDK_INT>=23){
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),READIMAGE)
                return
            }
        }
        loadImage()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            READIMAGE->{
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(applicationContext,"Access your image",Toast.LENGTH_SHORT).show()
                    loadImage()
                }else{
                    Toast.makeText(applicationContext,"Cannot access your image",Toast.LENGTH_SHORT).show()
                }
            }
            else->super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }

    }



    val PICK_IMAGE_CODE = 123
    fun loadImage(){
        val intent = Intent(Intent.ACTION_PICK,
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent,PICK_IMAGE_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==PICK_IMAGE_CODE && data!=null && resultCode== RESULT_OK){
            val selectedImage=data.data
            val filePathColumn=arrayOf(MediaStore.Images.Media.DATA)
            val cursor = getContentResolver().query(selectedImage!!,filePathColumn,null,null,null)
            cursor!!.moveToFirst()
            val columnIndex=cursor.getColumnIndex(filePathColumn[0])
            val picturePath = cursor.getString(columnIndex)
            Log.d(TAG, "onActivityResult: gto so far")
            cursor.close()
            img!!.setImageBitmap(BitmapFactory.decodeFile(picturePath))
        }
    }
    fun SplitString(email:String):String{
        val split= email.split("@")
        return split[0]
    }
    fun SaveImageInFirebase(){
        var currentUser =mAuth!!.currentUser
        val email:String=currentUser!!.email.toString()
        val storage=FirebaseStorage.getInstance()
        val storgaRef=storage.getReferenceFromUrl("gs://socialize-91b6d.appspot.com")
        val df=SimpleDateFormat("ddMMyyHHmmss")
        val dataobj=Date()
        val imagePath= SplitString(email) + "."+ df.format(dataobj)+ ".jpg"
        val ImageRef=storgaRef.child("images/"+imagePath )
        img!!.isDrawingCacheEnabled=true
        img!!.buildDrawingCache()

        val drawable=img!!.drawable as BitmapDrawable
        val bitmap=drawable.bitmap
        val baos= ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data= baos.toByteArray()
        val uploadTask=ImageRef.putBytes(data)
        uploadTask.addOnFailureListener{
            Toast.makeText(applicationContext,"fail to upload",Toast.LENGTH_LONG).show()
        }.addOnSuccessListener { taskSnapshot ->

            var DownloadURL= taskSnapshot.storage.downloadUrl.toString()!!

            // TODO: register to datavase
            Log.d(TAG, "SaveImageInFirebase: "+DownloadURL)
            //http://127.0.0.1/register.php?first_name=%22mdhl%22&email=%22madhl@gmail.com%22&password=%2287654%22&picture_path=%22home/u.png

            val url = "https://192.168.1.10/register.php?first_name="+nameTextRegister.text+"&email="+emailAddressTextRegister.text+"&password="+ passwordTextRegister.text+"&picture_path="+DownloadURL
            Log.d(TAG, "SaveImageInFirebase: "+url)
            var t1 = thread(url)
            t1.start()
        }


    }

    class thread():Thread(){
        var url:URL?=null
        constructor(str:String):this(){
            this.url = URL(str)

        }
        override fun run() {
            try {

                val urlConnect=url!!.openConnection() as HttpURLConnection
                urlConnect.connectTimeout=7000

                var inString= ConvertStreamToString(urlConnect.inputStream)
                //Cannot access to ui
                try{
                    var json= JSONObject(inString)
                    Log.d("InsideThread", "doInBackground: "+json.getString("msg"))
                    if(json.getString("msg")=="user is added"){
                        //do something
                    }



                }catch (ex:Exception){
                    Log.d("Error1", "run: "+ex.toString())
                }
            }catch (ex:Exception){
                Log.d("Error2", "run: "+ex.toString())
            }

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
    }



}