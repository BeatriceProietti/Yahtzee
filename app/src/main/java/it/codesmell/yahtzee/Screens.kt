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
            composables?.btn1(::provas, "Sbrisculo Aptico", 50)
            composables?.btn1(::switchVibMode, "Cambia mod. vibrazione", 0)
            composables?.sceneSwitchBtn(navController, "Screen2", "Vai a schermata 2", 0)
        }
    }

    @Composable
    fun Screen2(navController: NavController) {

        fun navBack(){
            navController.navigateUp()
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Screen2: Zilling Off")
            composables?.btn1(::provas, "Zill Off", 300)
            composables?.btn1(::navBack, "Torna Indietro", 0)
            //composables?.sceneSwitchBtn(navController, "MainScreen", "Vai alla schermata principale", 0)
        }

    }


// -----

