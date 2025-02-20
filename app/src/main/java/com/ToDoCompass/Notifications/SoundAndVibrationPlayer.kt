package com.ToDoCompass.Notifications

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.ToDoCompass.LogicAndData.Constants.Companion.defaultNotificationType
import com.ToDoCompass.LogicAndData.Constants.Companion.silentNotificationType
import com.ToDoCompass.LogicAndData.StringConstants.Companion.NULL_STRING
import com.ToDoCompass.LogicAndData.toSoundUri
import com.ToDoCompass.LogicAndData.toVibrationPattern
import com.ToDoCompass.database.NotifType

class SoundAndVibrationPlayer(
    context : Context
) {
    val context = context
    val handler = Handler(Looper.getMainLooper())
    
    
    private var mediaPlayer: MediaPlayer? = null
    
    val vibratorManager =
        context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
    val vibrator =
        vibratorManager.defaultVibrator
    val amplitude = VibrationEffect.DEFAULT_AMPLITUDE
    
    var loopVolume = 0.1f
    var loopVibrationAmplitude = 10
    
    var beLooping : Boolean = false
    
    private fun hasntPlayedLongEnough() : Boolean{
        return (persistentLengthMilliSeconds > SystemClock.elapsedRealtime()-startTimeMillis)
    }
    
    private val loopRunnable = Runnable{
        if (hasntPlayedLongEnough()){
            mediaPlayer?.start()
        }
        else {
            stopPlayback()
        }
    }
    
    fun playNotificationSound(notifType : NotifType){
        stopPlayback()
        persistentLengthMilliSeconds = 0
        if (notifType.respectSystem){
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            when (audioManager.ringerMode) {
                AudioManager.RINGER_MODE_NORMAL -> {
                        startPersistentAlarm(notifType)
                    }
                AudioManager.RINGER_MODE_VIBRATE -> {
                        val vibrationPattern = notifType.vibrationPatternString
                        val persistentLength = notifType.persistentLength
                        startPersistentAlarm(silentNotificationType.copy(
                            vibrationPatternString = vibrationPattern,
                            persistentLength = persistentLength
                        )
                        )
                    }
                AudioManager.RINGER_MODE_SILENT -> null
                else -> null
            }
        }
        else {
            startPersistentAlarm(notifType)
        }
        
        
        //val vibrationPattern = notifType.vibrationPatternString.toVibrationPattern()
    }
    
    fun startPersistentAlarm(
        notifType: NotifType,
        delay : Long = 7000)
    {
        startTimeMillis = SystemClock.elapsedRealtime()
        persistentLengthMilliSeconds = notifType.persistentLength.toLong()*1000L
        if (notifType.soundUriString != NULL_STRING){
            if (notifType.rampUp){
                increaseVolumeRunnable = createIncreaseVolumeRunnable(delay = delay)
                increaseVolumeRunnable?.let{
                    handler.postDelayed(it, delay)
                }
            }
            playSoundWithLooping(notifType)
        }
        // Remove old increaseVolumeRunnable
        
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S &&
            isGoodVibrationPattern(notifType.vibrationPatternString.toVibrationPattern())
        ) {
            val vibrationPattern = notifType.vibrationPatternString.toVibrationPattern()
            vibrateRunnable = createVibrateRunnable(vibrator, vibrationPattern)
            vibrateRunnable?.let{
                handler.postDelayed(it, 0)
            }
            
        }
    }
    
    fun playSoundWithLooping(
        notifType : NotifType = defaultNotificationType,
        soundUri : Uri? = notifType.soundUriString.toSoundUri(),
    ) {
        stopSound()
        handler.removeCallbacks(loopRunnable)
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build()
            )
            try {
                setDataSource(
                    context,
                    soundUri ?:RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                )
                setOnPreparedListener {
                    isLooping = false
                    if (notifType.rampUp) {
                        setVolume(loopVolume, loopVolume)
                    }
                    val durationOfSound = duration
                    if (persistentLengthMilliSeconds < durationOfSound){
                        persistentLengthMilliSeconds = durationOfSound.toLong()
                    }
                    // Start playing the notification sound when prepared
                    start()
                    
                    //Log.v("player playSound", "duration = $duration")
                }
                isLooping = false
                prepare() // Prepare asynchronously to avoid blocking the UI thread
                isLooping = false
            } catch (e: Exception) {
                e.printStackTrace()
                // Release the MediaPlayer if an error occurs during initialization
                release()
                mediaPlayer = null
            }
            
            setOnCompletionListener {
                if (notifType.rampUp){
                    setVolume(loopVolume, loopVolume)
                }
                var delay = 500L
                try {
                    delay = calculateLoopDelayFromDuration(duration)
                }
                catch(e : Exception){}
                //Loop via handler
                handler.postDelayed(loopRunnable, delay)
            }
        }
    }
    
    fun vibratePatternOnce(vibrationPattern : LongArray) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && isGoodVibrationPattern(vibrationPattern)) {
            val vibratorManager =
                context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator // Vibration vibrationPattern: vibrate for 100ms, then pause for 200ms, then vibrate for 300ms
            val amplitude = VibrationEffect.DEFAULT_AMPLITUDE // Use default vibration amplitude
            
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val effect = VibrationEffect.createWaveform(
                    /* timings = */ vibrationPattern,
                    /* repeat = */ amplitude
                )
                vibrator.vibrate(effect)
            } else {
                // For older devices without VibrationEffect API
                vibrator.vibrate(vibrationPattern, -1)
            }
        }
    }
    
    private var increaseVolumeRunnable : Runnable? = null
    
    private var vibrateRunnable : Runnable? = null
    
    private var startTimeMillis : Long = 0L
    private var persistentLengthMilliSeconds : Long = 0
    
    
    
    private fun createVibrateRunnable(
        vibrator: Vibrator,
        vibrationPattern: LongArray,
        startAmplitude : Int = 50,
        step : Int = 50,
        delay : Long = 0
    ): Runnable {
        vibrateRunnable?.let{
            handler.removeCallbacks(it)
        }
        var vibrateIndex = 0
        loopVibrationAmplitude = startAmplitude
        return Runnable {
            try{
                loopVibrationAmplitude += step
                loopVibrationAmplitude = loopVibrationAmplitude.coerceAtMost(255)
                val amplitudeArray = vibrationPattern.map{
                    it-> loopVibrationAmplitude
                }.toIntArray()
                val effect = VibrationEffect.createWaveform(
                    /* timings = */ vibrationPattern,
                    /* amplitudes = */
                    /* repeat = */ -1
                )
                vibrator.vibrate(effect)
            }
            catch (e : Exception){
                //Log.v("createVibrateRunnable", " error $e")
                mediaPlayer?.release()
            }
            //Log.v("createVibrateRunnable", "${vibrationPattern.sum()}")
            if (hasntPlayedLongEnough()){
                vibrateRunnable?.let{
                    handler.postDelayed(it, vibrationPattern.sum()+1000)
                }
            }
        }
    }
    
    private fun createIncreaseVolumeRunnable(
        startVolume: Float = 0.2f,
        step: Float = 0.2f,
        delay: Long = 7000
    ) : Runnable {
        increaseVolumeRunnable?.let{
            handler.removeCallbacks(it)
        }
        loopVolume = startVolume
        return Runnable {
            if (loopVolume < 1.0f) {
                //Log.v("soundAndVibrationPLayer loudening", "increasing volume loopVolume = $loopVolume")
                loopVolume += step
                loopVolume = loopVolume.coerceAtMost(1f)
                //mediaPlayer?.setVolume(loopVolume, loopVolume) ?:{loopVolume -=step}
                //handler.postDelayed(this, 3000)
            }
            increaseVolumeRunnable?.let{
                handler.postDelayed(it, delay)
            }
        }
    }
    /*
    private fun createIncreaseVolumeRunnable(startVolume : Float = 0.1f, step : Float = 0.5f): Runnable {
        
        val _increaseVolumeRunnable = object : Runnable {
            var currentVolume = startVolume
            override fun run() {
                try{
                    if (currentVolume < 1.0f && mediaPlayer?.isLooping == true) {
                        Log.v("soundAndVibrationPLayer loudening", "increasing volume currentVolume = $currentVolume")
                        currentVolume += step
                        currentVolume.coerceAtMost(1f)
                        mediaPlayer?.setVolume(currentVolume, currentVolume) ?:{currentVolume -=step}
                        //handler.postDelayed(this, 3000)
                    }
                }
                catch (e : Exception){
                    //Log.v("soundAndVibrationPLayer loudening", "increaseVol runnable error")
                    mediaPlayer?.release()
                }
                increaseVolumeRunnable?.let{
                    handler.postDelayed(it, 5000)
                }
            }
        }
        return _increaseVolumeRunnable
    }
    */
    
    fun stopSound() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
    
    fun stopPlayback() {
        increaseVolumeRunnable?.let{
            handler.removeCallbacks(it)
        }
        vibrateRunnable?.let{
            handler.removeCallbacks(it)
        }
        stopSound()
        vibrator.cancel()
    }
    
    fun isGoodVibrationPattern(vibrationPattern : LongArray) : Boolean {
        var sum = 0L
        vibrationPattern.forEach {
            sum += it
            if (it<0) return false
        }
        if (sum == 0L) return false
        else return true
    }
    
    
    private fun calculateLoopDelayFromDuration(duration : Int) : Long{
        if (duration < 500) return 1600
        if (duration < 1000) return 1800
        if (duration < 2000) return 1100
        if (duration < 3000) return 800
        if (duration < 5000) return 400
        else return 100
    }
}