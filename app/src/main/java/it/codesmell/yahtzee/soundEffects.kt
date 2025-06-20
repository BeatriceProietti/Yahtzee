package it.codesmell.yahtzee

import android.content.Context
import android.media.SoundPool
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlin.random.Random



var diceroll1 : Int = 0
var diceroll2 : Int = 0
var diceroll3 : Int = 0

var rng = Random(System.currentTimeMillis())

class soundEffects {

    var enableSounds by mutableStateOf(true)

    val soundPool = SoundPool.Builder()
        .setMaxStreams(5)  // Numero massimo di suoni simultanei
        .build()

    // Mappa per gli ID dei suoni
    val soundMap = mutableMapOf<Int, Int>()

    // Precarico i file dei suoni
    fun loadSounds(context : Context){
        diceroll1 = soundPool.load(context, R.raw.diceroll1, 1)
        diceroll2 = soundPool.load(context, R.raw.diceroll2, 1)
        diceroll3 = soundPool.load(context, R.raw.diceroll3, 1)
    }


    fun btnDown(){
        if(enableSounds){
            Log.d("soundEffects", "btnDown")
            soundPool.play(diceroll3, 1f, 1f, 0, 0, rng.nextDouble(0.9, 1.1).toFloat())
        }
    }

    fun btnUp(){
        if(enableSounds){
            Log.d("soundEffects", "btnDown")
            soundPool.play(diceroll2, 1f, 1f, 0, 0, rng.nextDouble(0.9, 1.1).toFloat())
        }
    }

    fun diceRoll(){
        if(enableSounds){
            Log.d("soundEffects", "btnDown")
            soundPool.play(diceroll1, 1f, 1f, 0, 0, rng.nextDouble(0.7, 1.3).toFloat())
        }
    }

}