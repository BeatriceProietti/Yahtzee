package it.codesmell.yahtzee

import android.widget.Toast
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.NavController

//mettiamo qui i composable, per avere un po' di ordine e per averli standardizzati per tutte le schermate
//possiamo fare dei composable ad uso generico, si possono passare le funzioni come argomenti

class Composables {


    //bottone che esegue una funzione senza argomenti
    @Composable           // vv Unit sarebbe void. funzione che prende nulla e restituisce nulla
    fun btn1(onClick : () -> Unit, text : String, depth : Long){
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

    //bottone che cambia schermata.
    //soluzione un po' brutta, sarebbe ideale trovare un modo di fare qualsiasi funzione con un composable unico
    @Composable
    fun sceneSwitchBtn(navcon : NavController, route : String, text : String, depth : Long){
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
                                Toast.makeText(gthis, "navigo verso $route", Toast.LENGTH_SHORT).show()
                                navcon.navigate(route)
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


    //---------------------------------



}