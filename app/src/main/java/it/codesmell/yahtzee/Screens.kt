package it.codesmell.yahtzee

import android.hardware.SensorEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Screen2: Zilling Off")
            composables?.funButton({navController.navigateUp()}, "Torna Indietro", 0)
        }

    }

    @Composable
    fun GameScreen(navController: NavController) {

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Gaming")
            composables?.funButton({navController.navigateUp()}, "Torna Indietro", 0)
            Spacer(Modifier.size(50.dp))

            composables?.combosGrid(1,1)
            composables?.combosGrid(2,3)
            composables?.combosGrid(1,1)

            Text(statusText)
            //riga dadi -----------------------------------------------
            //compongo la lista di dadi da mandare al composable diceRow
            var dr = IntArray(diceAmount)
            for(i in 0..diceAmount-1){
                dr[i] = gameLogic.dice[i] //i dadi presi da gameLogic, da mandare all'interfaccia
            }
            composables?.diceRow(
                dice = dr.toTypedArray() //soluzione brutta //mando i dadi all'interfaccia
            )
            //---------------------------------------------------------

            composables?.funButton({gameLogic.rollSelectedDice()}, "Tira Dadi", 0)

        }

    }




// -----

