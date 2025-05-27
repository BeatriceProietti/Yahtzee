package it.codesmell.yahtzee


import kotlin.math.roundToInt
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

//mettiamo qui i composable, per avere un po' di ordine e per averli standardizzati per tutte le schermate
//possiamo fare dei composable ad uso generico, si possono passare le funzioni come argomenti

class Composables {


    //bottone che esegue una funzione senza argomenti
    @Composable           // vv Unit sarebbe void. funzione che prende nulla e restituisce nulla
    fun funButton (onClick : () -> Unit, text : String, depth : Long){
        Button(
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitEachGesture {
                        //evento pressione del tasto
                        val downEvent =
                            awaitPointerEvent(PointerEventPass.Main)
                        downEvent.changes.forEach {
                            if (it.pressed) {
                                hfx?.btnDown(depth)
                                onClick() //eseguo la funzione passata come argomento
                            }
                        }
                        //loop che aspetta che il tasto venga rilasciato
                        var allUp = false
                        while (!allUp) {
                            val event =
                                awaitPointerEvent(PointerEventPass.Main)
                            if (event.changes.all { it.pressed.not() }) {
                                allUp = true
                                event.changes.forEach {
                                    hfx?.click(0.5f)
                                }
                            }
                        }
                    }
                },
            onClick = {},
            shape = ButtonDefaults.shape,
        ){
            Text(text)
        }
    }


    @Composable
    fun diceRow(
        dice : Array<Int> //i dadi ricevuti da gameLogic
    ){
        Row(
            Modifier
                .padding(16.dp),
            Arrangement
                .spacedBy(16.dp)
        ){
            for(i in 0..diceAmount-1){
                funButton({gameLogic.selectDie(i)},numToDie(dice[i]),0)
            }
        }
    }

    //da rifare con dadi fighi
    fun numToDie(num : Int) : String{
        var newString : String = num.toString()
        /*
        if(num == 1){newString = "⚀"}
        else if(num == 2){newString = "⚁"}
        else if(num == 3){newString = "⚂"}
        else if(num == 4){newString = "⚃"}
        else if(num == 5){newString = "⚄"}
        else if(num == 6){newString = "⚅"}
        */
        return newString
    }

    //---------------------------------

    @Composable
    fun animationSquare( // il dado
        color: Color = Color.Cyan,
        logTag: String = "ClickableSquare",
        logMessage: String = "Square clicked!"
    ) {
        val context = LocalContext.current
        var isMoved by remember { mutableStateOf(false) }

        val rotationZ by animateIntAsState( //animazione che si occupa della rotazione
            targetValue = if (isMoved) 360 else 0,
            animationSpec = tween(800),
        )
        // Animazione dell'offset Y/z
        val offsetY = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(isMoved) {
            var targetY = if (isMoved) -290f else 0f
            offsetY.animateTo(
                targetY,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )

            Log.d("duce2", "booooh$rotationZ")
        }


        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Box(
                modifier = Modifier
                    .offset { IntOffset(0, offsetY.value.roundToInt()) }
                    .size(100.dp)
                    .rotate(rotationZ.toFloat())
                    .clickable { isMoved = !isMoved }
                    .background(color)
            )
        }
    }
}