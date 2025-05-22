package it.codesmell.yahtzee

import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import it.codesmell.yahtzee.ui.theme.YahtzeeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//sborra


var gthis : MainActivity? = null
var hfx : hapticEffects? = null

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        hfx = hapticEffects(this)
        gthis = this

        setContent {
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier
                            .padding(innerPadding)
                    )
                    MyScreenContent()
                }
            }
        }
    }


}

@Composable
fun MyScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        btn1()
    }
}


@Composable
fun btn1(){
    Button(
        modifier = Modifier
            .pointerInput(Unit) {
                awaitEachGesture {
                    //evento pressione del tasto
                    val downEvent =
                        awaitPointerEvent(PointerEventPass.Main)
                    downEvent.changes.forEach {
                        if (it.pressed) {
                            Toast.makeText(gthis, "Palle Premute", Toast.LENGTH_SHORT).show()
                            hfx?.btnDown(50)
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
                                Toast.makeText(gthis, "Palle Spremute", Toast.LENGTH_SHORT).show()
                                hfx?.click(0.5f)
                            }
                        }
                    }
                }
            },
        onClick = {},
        shape = ButtonDefaults.shape,
    ){
        Text("Tasto Laico")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    YahtzeeTheme {
        Greeting("Moto")
    }
}