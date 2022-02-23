package com.rk.timeapplication

import android.app.ActivityManager
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.rk.timeapplication.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity(), ServiceCallback {
    lateinit var connection: ServiceConnection

     var binder: IBinder? = null
     var service: TimerService? = null
     lateinit var binding: ActivityMainBinding
     var startTimerAfterWards = false

     init {
         createServiceConnection()
     }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
      ///  createServiceConnection()
        createNotificationChannel()
        binding.button.setOnClickListener {
            try {
            if(service == null){
                startTimerAfterWards = true
                startService()}else
                {
               startTimer()
            }}
            catch (e:Exception){
                Toast.makeText(this@MainActivity," Exeption on start Timer", Toast.LENGTH_LONG).show()
            }
        }

        binding.stopService.setOnClickListener {
            service?.stopTimer()
            service?.listner = null

           stopService()
            service = null
        }


    }


    override fun onStart() {
        super.onStart()

       startService()
    }

//    override fun onStop() {
//        try{
//        super.onStop()
//        unbindService(connection)
//        }catch (e:java.lang.Exception){
//            Toast.makeText(this@MainActivity," Service not running", Toast.LENGTH_LONG).show()
//        }
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun createNotificationChannel(){

            // Create the NotificationChannel
            val name = "DEFAULT"
            val descriptionText = "DEFAULT"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(name, "DEFAULT", importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)

    }

    fun createServiceConnection(){
        connection = object: ServiceConnection{
            override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
                binder = p1
                service = (p1 as TimerService.ServiceBinder).getService()
                Log.d("TAG", "service connected")
                try {

               // service?.startTimer(binding?.editTextNumber.text.toString().toLong())
                service?.subscribe(this@MainActivity)}
                catch (e:Exception){
                    Toast.makeText(this@MainActivity," Exeption on start Timer", Toast.LENGTH_LONG).show()
                }
                if(startTimerAfterWards){
                    //startTimerAfterWards = false
                    startTimer()
                    startTimerAfterWards = false
                }

            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                Log.d("TAG", "service disconnected")
               Toast.makeText(this@MainActivity, "Error in service connection",Toast.LENGTH_LONG).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun startService(){
        Intent(this, TimerService::class.java).also {
           ContextCompat.startForegroundService(this,it)
        }.also {
            bindService(it, connection, BIND_AUTO_CREATE)
        }

    }

    fun startTimer(){
        service?.startTimer(binding.editTextNumber.text.toString().toLong())}


    fun stopService(){
        try{
         Intent(this, TimerService::class.java).also {
            unbindService(connection)
            service?.stopService(it)
         }

        }catch (e:java.lang.Exception){
            Toast.makeText(this@MainActivity," Service not running", Toast.LENGTH_LONG).show()
        }
    }

    override fun onUpdate(time: Long) {
        val post = Handler(mainLooper).post { binding.textView.text = time.toString() }

    }
}