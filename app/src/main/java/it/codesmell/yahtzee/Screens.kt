package it.codesmell.yahtzee

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.hardware.SensorEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.breens.beetablescompose.BeeTablesCompose
import it.codesmell.yahtzee.gameLogic
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


// qua mettiamo tutte le schermate dell'app

    @Composable
    fun MainScreen(navController: NavController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Benvenuto allo Yahtzeello")
            composables?.funButton(::provas, "Sbrisculo Aptico", 50)
            composables?.funButton(::switchVibMode, "Cambia mod. vibrazione", 0)
            composables?.funButton({ navController.navigate("GameScreen") }, "Gioca", 0)
            composables?.funButton(::switchTheme, "cambia il tema", 0)
            composables?.funButton({ navController.navigate("Screen2") }, "schermata di prova delle applicazioni", 0)
        }
    }


    @Composable
    fun Screen2(navController: NavController) {
        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val screenHeight = LocalWindowInfo.current.containerSize.height.dp
        val screenWidth = LocalWindowInfo.current.containerSize.width.dp

        val boxWidth = if (isPortrait) screenWidth*0.35f else screenHeight*0.3f
        val boxHeight = if (isPortrait) screenHeight*0.2f else screenWidth*0.2f
        val heightMod = if (isPortrait) 30 else 15
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Screen2: Zilling Off")
            composables?.funButton({navController.navigateUp()}, "Torna Indietro", 0)
            composables?.swappingCards(heightMod)
        }

    }

    @SuppressLint("ConfigurationScreenWidthHeight")
    @Composable
    fun gameScreenContent(navController : NavController){
        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp

        Spacer(Modifier.size(screenHeight*0.03f))
        //Display punteggio -------------------------------------
        Text("123456789")
        //---------------------------------------------------------
        Spacer(Modifier.size(screenHeight*0.03f))

        //Griglia combinazioni -------------------------------------
        val boxWidth = if (isPortrait) screenWidth*0.95f else screenWidth*0.4f
        val boxHeight = if (isPortrait) screenHeight*0.5f else screenHeight*0.85f
        val heightMod = if (isPortrait) 25 else 10

        composables?.swappingCards(heightMod)
        /*Box(
            contentAlignment = Alignment.CenterStart,
            modifier = Modifier
                .height(boxHeight)
                .width(boxWidth)
        ){
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                composables?.combosGrid(1,1,heightMod)
                composables?.combosGrid(2,3,heightMod)
                composables?.combosGrid(1,1,heightMod)
            }
        }*/
        //---------------------------------------------------------
        Spacer(Modifier.size(screenHeight*0.01f))

        //riga dadi -----------------------------------------------
        //compongo la lista di dadi da mandare al composable diceRow
        var dr = IntArray(diceAmount)
        for(i in 0..diceAmount-1){
            dr[i] = gameLogic.dice[i] //i dadi presi da gameLogic, da mandare all'interfaccia
        }
        Column(
            //verticalArrangement = Arrangement.Bottom, //non funziona
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            composables?.diceRow(
                dice = dr.toTypedArray() //soluzione brutta //mando i dadi all'interfaccia
            )
            composables?.funButton({gameLogic.rollSelectedDice()}, "Tira Dadi", 0)
        }
        //---------------------------------------------------------


    }

    @Composable
    fun GameScreen(navController: NavController) {

        if(LocalConfiguration.current.orientation == Configuration.ORIENTATION_PORTRAIT){
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                gameScreenContent(navController)
            }
        }else{
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                gameScreenContent(navController)
            }
        }




    }






// -----

