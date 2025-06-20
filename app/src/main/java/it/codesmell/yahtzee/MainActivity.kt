package it.codesmell.yahtzee

//import DataBase
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import it.codesmell.yahtzee.dao.TableScoreDatabase
import it.codesmell.yahtzee.ui.theme.YahtzeeTheme
import androidx.core.content.edit

//sborras

var gthis : MainActivity? = null
val gameLogic : GameLogic = GameLogic()

var hfx : hapticEffects? = null
var sfx : soundEffects? = null
var composables : Composables? = null
var darkTheme by mutableStateOf(true)

lateinit var mozione : motionManager


class MainActivity : ComponentActivity() {

    // DB building
    val db by lazy{
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
        gameLogic.dao = db.dao

        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        }

        gthis = this
        hfx = hapticEffects(this)
        sfx = soundEffects()
        sfx?.loadSounds(applicationContext)
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

/* non funziona nulla
fun applySettings(context : Context){
    context.getSharedPreferences("hapticsMode", Context.MODE_PRIVATE).edit() {
        putString("enableSounds", hfx?.hapticsMode?:"Rich")
    }
    context.getSharedPreferences("darkTheme", Context.MODE_PRIVATE).edit() {
        putBoolean("enableSounds", darkTheme)
    }
    context.getSharedPreferences("enableSounds", Context.MODE_PRIVATE).edit() {
        putBoolean("enableSounds", sfx?.enableSounds?:true)
    }
}*/

fun provas(){
    //Toast.makeText(gthis, "Szsszzziù!!!!", Toast.LENGTH_SHORT).show()
}

fun switchTheme(){
    darkTheme = !darkTheme
}

fun switchVibMode(){ //alterna tra le API di vibrazione
    if(hfx?.hapticsMode == "Rich") hfx?.hapticsMode = "Standard"
    else if(hfx?.hapticsMode == "Standard") hfx?.hapticsMode = "Off"
    else if(hfx?.hapticsMode == "Off") hfx?.hapticsMode = "Rich"
    else hfx?.hapticsMode = "Standard"
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}