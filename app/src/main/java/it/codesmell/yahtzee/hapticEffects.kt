package it.codesmell.yahtzee

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//Classe contenente gli effetti aptici (vibrazioni)


class hapticEffects constructor(mainActivity: MainActivity) {

    var vib : Vibrator? = null

    init{
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) { //se sto su android >= 12. sta cosa falla in base al supporto invece
            vib = mainActivity.getSystemService(Vibrator::class.java) //prendo il servizio vibrazione
        }

    }

//-----------------------------------------------------------------------------------------------------

//Depth: "profondità" del tasto, quanto tempo ci mette ad arrivare a fine corsa (in ms)
    fun btnDown(depth : Long) {
        vib?.vibrate( // ?
            VibrationEffect.startComposition()
                .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE)
                .compose()
        )
        //fa partire una coroutine, in cui attende e poi fa partire la seconda parte dell'effetto
        CoroutineScope(Dispatchers.IO).launch {
            delay(depth)
            withContext(Dispatchers.Main) {
                vib?.cancel()
                vib?.vibrate(
                    VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK)
                        .compose()
                )
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