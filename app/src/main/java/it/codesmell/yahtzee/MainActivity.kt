package it.codesmell.yahtzee

//import DataBase
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.intl.Locale
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import it.codesmell.yahtzee.dao.TableScore
import it.codesmell.yahtzee.dao.TableScoreDatabase
import it.codesmell.yahtzee.ui.theme.YahtzeeTheme
import kotlinx.coroutines.launch

//sborras

var gthis : MainActivity? = null
val gameLogic : GameLogic = GameLogic()

var hfx : hapticEffects? = null
var composables : Composables? = null
var darkTheme by mutableStateOf(true)

lateinit var mozione : motionManager

class MainActivity : ComponentActivity() {

    // DB building
    private val db by lazy{
        Room.databaseBuilder(
            applicationContext,
            TableScoreDatabase::class.java,
            "scores.db"
        ).build()
    }

    // ViewModel factory - creating a ViewModel with parameters
    private val viewModel by viewModels<ScoreListViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory{
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ScoreListViewModel(db.dao) as T
                }
            }
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
/*
            //insert di prova
            lifecycleScope.launch {
                val exampleScore = TableScore(
                    date = "2025-06-16",
                    aces = 3,
                    twos = 6,
                    threes = 9,
                    fours = 8,
                    fives = 10,
                    sixes = 12,
                    bonusUpperSection = 35,
                    threeOfAKind = 20,
                    fourOfAKind = 25,
                    fullHouse = 25,
                    smallStraight = 30,
                    largeStraight = 40,
                    chance = 23,
                    yahtzee = 50,
                    yahtzeeBonus = 100,
                    finalScore = 200
                )

                db.dao.storeTable(exampleScore)

                val exampleScore2 = TableScore(
                    date = "2025-06-10",
                    aces = 3,
                    twos = 6,
                    threes = 9,
                    fours = 8,
                    fives = 10,
                    sixes = 12,
                    bonusUpperSection = 35,
                    threeOfAKind = 20,
                    fourOfAKind = 25,
                    fullHouse = 25,
                    smallStraight = 30,
                    largeStraight = 40,
                    chance = 23,
                    yahtzee = 50,
                    yahtzeeBonus = 100,
                    finalScore = 700
                )

                db.dao.storeTable(exampleScore2)
            }
 */
        }

        gthis = this
        hfx = hapticEffects(this)
        composables = Composables()


        setContent {
            //var theme by remember{mutableStateOf(false)}
            YahtzeeTheme(darkTheme = darkTheme) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding -> Modifier.padding(innerPadding) //possiamo lasciare roba fuori dalla navigazione, in modo che rimanga fissa tra le schermate
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
                        ){Screen2(navCon, viewModel, onEvent = viewModel::onEvent)}

                        composable(
                            route = "GameScreen"
                        ){GameScreen(gameLogic, navCon)}
                        composable(
                            route = "OptionScreen"
                        ){OptionScreen(navCon)}

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
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}