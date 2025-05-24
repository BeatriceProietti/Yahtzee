package it.codesmell.yahtzee

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

// qua mettiamo tutte le schermate dell'app


    @Composable
    fun MainScreen(navController: NavController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("MainScreen, palle estremamente spigolari")
            composables?.funButton(::provas, "Sbrisculo Aptico", 50)
            composables?.funButton(::switchVibMode, "Cambia mod. vibrazione", 0)
            composables?.funButton({navController.navigate("Screen2")}, "Vai a schermata 2s", 0)
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


// -----

