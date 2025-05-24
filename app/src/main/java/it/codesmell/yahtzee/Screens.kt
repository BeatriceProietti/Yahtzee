package it.codesmell.yahtzee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import it.codesmell.yahtzee.gameLogic

val gameLogic : GameLogic = GameLogic()

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
            composables?.funButton(::provas, "Zill Off", 300)
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
            composables?.funButton(::provas, "Zill Off", 300)
            composables?.funButton({navController.navigateUp()}, "Torna Indietro", 0)
            Spacer(Modifier.size(50.dp))
            composables?.diceRow(
                die1 = gameLogic.die1.toString(),
                die2 = "0",
                die3 = "0",
                die4 = "0",
                die5 = "0"
            )

            composables?.funButton({gameLogic.rollDie(6)}, "Tira Dadi", 0)

        }

    }




// -----

