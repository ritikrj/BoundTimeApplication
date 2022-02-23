package com.rk.timeapplication

import android.app.Notification
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import java.util.*


class TimerService: Service() {
    var timer: CountDownTimer? = null
    var listner: ServiceCallback ?= null
    var duration = 0L
    private var isTimerStarted = false
    lateinit var notification: Notification.Builder
    lateinit var notificationManager: NotificationManager
    var binder:IBinder = ServiceBinder()

    inner class ServiceBinder:Binder(){

       fun getService():TimerService{
          return this@TimerService
       }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBind(p0: Intent?): IBinder? {
        buildNotification()
        return binder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        buildNotification()
      //  val n  = notification.build()
        startForeground(1,notification.build())


        return START_NOT_STICKY
    }

    fun subscribe(listner: ServiceCallback){
        this.listner = listner
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun buildNotification(){
        notification = Notification.Builder(this).also {
            it.setContentTitle("Time task")
            it.setContentText("begining countdown")
            it.setAutoCancel(false)
            it.setChannelId("DEFAULT")
                it.setSmallIcon(R.drawable.ic_launcher_background)
              //  it.setLargeIcon(R.drawable.ic_launcher_background)
            it.build()
        }
        notificationManager = this
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    fun updateNotification(time: Long){
        val n = notification.setContentText(" countDown at ${time}").build()

        notificationManager.notify(1, n)

    }
    fun stopTimer(){
        timer?.cancel()
    }

    fun startTimer(duration: Long){

        if(!isTimerStarted){
            this.duration = duration*1000
            timer = object :CountDownTimer(duration*1000,1000L){
                override fun onTick(time: Long) {
                    listner?.onUpdate(time/1000)
                    updateNotification(time/1000)
                }

                override fun onFinish() {

                }

            }
            timer?.start()
            isTimerStarted = true
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate() {
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        Toast.makeText(this," Service destroyed", Toast.LENGTH_LONG).show()
        Log.d("LOG","service destroyed")
    }




}