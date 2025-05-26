package it.codesmell.yahtzee

import android.os.Build
import android.os.VibrationEffect
import android.os.VibrationEffect.EFFECT_CLICK
import android.os.VibrationEffect.EFFECT_HEAVY_CLICK
import android.os.Vibrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//Classe contenente gli effetti aptici (vibrazioni)


class hapticEffects constructor(mainActivity: MainActivity) {

    var vib : Vibrator? = null
    var hasRichHaptics : Boolean = false

    init{
        vib = mainActivity.getSystemService(Vibrator::class.java) //prendo il servizio vibrazione

        if(vib?.areAllPrimitivesSupported() == true){
            hasRichHaptics = true
        }

    }

//-----------------------------------------------------------------------------------------------------

//Depth: "profondità" del tasto, quanto tempo ci mette ad arrivare a fine corsa (in ms)
    fun btnDown(depth : Long) {

        if(hasRichHaptics == true){
            vib?.vibrate(
                    VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE)
                        .compose()
            )
        }else if(depth > 0){
            vib?.vibrate(VibrationEffect.createOneShot(depth, 48))
        }
        //fa partire un altro thread, che attende e poi fa partire la seconda parte dell'effetto
        CoroutineScope(Dispatchers.IO).launch {
            delay(depth)
            withContext(Dispatchers.Main) {
                vib?.cancel()
                if(hasRichHaptics == true) {
                    vib?.vibrate(
                        VibrationEffect.startComposition()
                            .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK)
                            .compose()
                    )
                }else{
                    vib?.vibrate(VibrationEffect.createPredefined(EFFECT_HEAVY_CLICK))
                }
            }
        }
    }

    fun click(intensity : Float) {
        vib?.vibrate( // ?
            VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK, intensity)
                .compose()
        )
    }




}