package it.codesmell.yahtzee

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Dp
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
        die0 : Int,
        die1 : Int,
        die2 : Int,
        die3 : Int,
        die4 : Int
    ){
        Row(
            Modifier
                .padding(16.dp),
            Arrangement
                .spacedBy(16.dp)
        ){
            funButton({gameLogic.selectDie(0)},numToDie(die0),0)
            funButton({gameLogic.selectDie(1)},numToDie(die1),0)
            funButton({gameLogic.selectDie(2)},numToDie(die2),0)
            funButton({gameLogic.selectDie(3)},numToDie(die3),0)
            funButton({gameLogic.selectDie(4)},numToDie(die4),0)
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
    fun animationSquare(
        size: Dp = 100.dp,
        color: Color = Color.Cyan,
        logTag: String = "ClickableSquare",
        logMessage: String = "Square clicked!"
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(color)
                .clickable {
                    Log.d(logTag, logMessage)
                }
        )
        }
    }