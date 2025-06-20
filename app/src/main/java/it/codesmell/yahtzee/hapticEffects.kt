package it.codesmell.yahtzee

import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_HEAVY_CLICK
import android.os.Vibrator
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//Classe contenente gli effetti aptici (vibrazioni)


class hapticEffects constructor(mainActivity: MainActivity) {

    var vib : Vibrator? = null
    var hapticsMode by mutableStateOf("Standard")

    init{
        vib = mainActivity.getSystemService(Vibrator::class.java) //prendo il servizio vibrazione

        if(vib?.areAllPrimitivesSupported() == true){
            hapticsMode = "Rich"
        }

    }

//-----------------------------------------------------------------------------------------------------

//Depth: "profondità" del tasto, quanto tempo ci mette ad arrivare a fine corsa (in ms)
    fun btnDown(depth : Long) {

        if(hapticsMode == "Rich"){
            vib?.vibrate(
                    VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE)
                        .compose()
            )
        }else if(depth > 0 && hapticsMode == "Standard"){
            vib?.vibrate(VibrationEffect.createOneShot(depth, 48))
        }
        //fa partire un altro thread, che attende e poi fa partire la seconda parte dell'effetto
        CoroutineScope(Dispatchers.IO).launch {
            delay(depth)
            withContext(Dispatchers.Main) {
                vib?.cancel()
                if(hapticsMode == "Rich") {
                    vib?.vibrate(
                        VibrationEffect.startComposition()
                            .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK)
                            .compose()
                    )
                }else if(hapticsMode == "Standard"){
                    vib?.vibrate(VibrationEffect.createPredefined(EFFECT_HEAVY_CLICK))
                }
            }
        }
    }

    fun click(intensity : Float) {
        if(hapticsMode == "Rich" || hapticsMode == "Standard" )
        vib?.vibrate( // ?
            VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, intensity)
                .compose()
        )
    }




}