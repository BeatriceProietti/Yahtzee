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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.codesmell.yahtzee.ui.theme.YahtzeeTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//sborra

var gthis : MainActivity? = null
var hfx : hapticEffects? = null
var composables : Composables? = null


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        gthis = this
        hfx = hapticEffects(this)
        composables = Composables()
        //val screens = Screens()

        setContent {
            // roba navigazione ---------------------------------------------------------------------------------
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "Screen2", builder ={
                composable("MainScreen"){MainScreen(navController)} //associa il route "MainScreen" al composable MainScreen
                composable("Screen2"){Screen2(navController)}
            })
            // --------------------------------------------------------------------------------------------------
            YahtzeeTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->


                    Greeting(name = "Moto", modifier = Modifier.padding(innerPadding))
                    MainScreen(navController)
                }
            }
        }
    }


}

fun provas(){
    //Toast.makeText(gthis, "Szsszzziù!!!!", Toast.LENGTH_SHORT).show()
}

fun switchVibMode(){ //alterna tra le API di vibrazione, per provare
    hfx?.hasRichHaptics = !hfx!!.hasRichHaptics //"!!" devo capire bene che è, lo vuole kotlin
    Toast.makeText(gthis, "hasRichHaptics = " + hfx?.hasRichHaptics, Toast.LENGTH_SHORT).show()
}

@Composable
fun MyScreenContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        composables?.btn1(::provas, "Sbrisculo", 50)
        composables?.btn1(::switchVibMode, "Cambia mod. vibrazione", 0)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}