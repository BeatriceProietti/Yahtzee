package it.codesmell.yahtzee

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import it.codesmell.yahtzee.ui.theme.YahtzeeTheme

//sborra

var gthis : MainActivity? = null
val gameLogic : GameLogic = GameLogic()

var hfx : hapticEffects? = null
var composables : Composables? = null
var darkTheme by mutableStateOf(true)

private lateinit var mozione : motionManager

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        gthis = this
        hfx = hapticEffects(this)
        composables = Composables()

        mozione = motionManager(this)
        mozione.start()

        setContent {
            //var theme by remember{mutableStateOf(false)}
            YahtzeeTheme(darkTheme = darkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(name = "Moto", modifier = Modifier.padding(innerPadding)) //possiamo lasciare roba fuori dalla navigazione, in modo che rimanga fissa tra le schermate
                    // roba navigazione ---------------------------------------------------------------------------------
                    var navCon = rememberNavController()
                    NavHost(navController = navCon, startDestination = "MainScreen", builder ={
                        //qua i route, animazioni entrata/uscita delle varie schermate

                        composable(
                            route = "MainScreen",
                            //enterTransition = slideInHorizontally{ fullWidth -> fullWidth }
                        ){MainScreen(navCon)} //associa il route "MainScreen" al composable MainScreen

                        composable(
                            route = "Screen2"
                        ){Screen2(navCon)}

                        composable(
                            route = "GameScreen"
                        ){GameScreen(navCon)}

                    })
                    // --------------------------------------------------------------------------------------------------
                }
            }
        }
    }


}



fun provas(){
    //Toast.makeText(gthis, "Szsszzziù!!!!", Toast.LENGTH_SHORT).show()
}

fun switchTheme(){
    darkTheme = !darkTheme
    Toast.makeText(gthis, "darkTheme = $darkTheme", Toast.LENGTH_SHORT).show()
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
        composables?.funButton(::provas, "Sbrisculo", 50)
        composables?.funButton(::switchVibMode, "Cambia mod. vibrazione", 0)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}