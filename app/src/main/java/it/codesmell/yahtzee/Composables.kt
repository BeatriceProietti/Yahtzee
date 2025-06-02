package it.codesmell.yahtzee


import android.app.Notification
import android.app.Notification.MessagingStyle.Message
import kotlin.math.roundToInt
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.animateIntSizeAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breens.beetablescompose.BeeTablesCompose
import it.codesmell.yahtzee.ui.theme.YahtzeeTheme
import kotlinx.serialization.descriptors.StructureKind
import android.content.res.Configuration
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Shapes
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.zIndex

//mettiamo qui i composable, per avere un po' di ordine e per averli standardizzati per tutte le schermate
//possiamo fare dei composable ad uso generico, si possono passare le funzioni come argomenti

class Composables {


    //bottone che esegue una funzione senza argomenti
    @Composable           // vv Unit sarebbe void. funzione che prende nulla e restituisce nulla
    fun funButton (onClick : () -> Unit, text : String, depth : Long){
        Button(
            modifier = Modifier
                .pointerInput(Unit) {
                    awaitEachGesture {
                        //evento pressione del tasto
                        val downEvent =
                            awaitPointerEvent(PointerEventPass.Main)
                        downEvent.changes.forEach {
                            if (it.pressed) {
                                hfx?.btnDown(depth)
                                onClick() //eseguo la funzione passata come argomento
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
                                    hfx?.click(0.5f)
                                }
                            }
                        }
                    }
                },
            onClick = {},
            shape = ButtonDefaults.shape,
        ){
            Text(text)
        }
    }


    @Composable
    fun diceRow(
        dice : Array<Int>, //i dadi ricevuti da gameLogic
    ){
        Row(
            Modifier
                .padding(16.dp),
            Arrangement
                .spacedBy(16.dp)
        ){
            for(i in 0..diceAmount-1){
                animationSquare({gameLogic.selectDie(i)},numToDie(dice[i]),i)
            }
        }
    }

    //da rifare con dadi fighi
    fun numToDie(num : Int) : String{
        var newString : String = num.toString()

        if(num == 1){newString = "⚀"}
        else if(num == 2){newString = "⚁"}
        else if(num == 3){newString = "⚂"}
        else if(num == 4){newString = "⚃"}
        else if(num == 5){newString = "⚄"}
        else if(num == 6){newString = "⚅"}
        else if(num == 0){newString = "□"}

        return newString
    }



    //--------------------------------- tabella dei punteggi del gioco


    @Composable
    fun combosGrid(rows: Int, cols: Int, heightMod : Int) {
        FlowRow(
            modifier = Modifier
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            maxItemsInEachRow = rows

        ) {
            val itemModifier = Modifier
                .padding(4.dp)
                .defaultMinSize(minHeight = 40.dp + heightMod.dp, minWidth = 60.dp)
                .fillMaxSize()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
            repeat(rows * cols) {
                Spacer(modifier = itemModifier)
            }
        }
    }

    @Composable
    fun CombosGridComposition(heightMod: Int) {
        Column(
            modifier = Modifier
                .fillMaxSize() // Assicura che occupi tutto il box disponibile
                //.verticalScroll(rememberScrollState()) // Aggiungi scroll se serve
        ) {
            combosGrid(1, 1, heightMod)
            combosGrid(2, 3, heightMod)
            combosGrid(1, 1, heightMod)
        }
    }


    //---------------------------------

    @Composable
    fun animationSquare(onClick : () -> Unit, text : String, index : Int) { //index: indice all'interno di upDice
        val context = LocalContext.current
        var isMoved = selectedDice[index]

        val rotationZ by animateIntAsState( //animazione che si occupa della rotazione
            targetValue = if (isMoved) 180 else 0,
            animationSpec = tween(350),
        )
        // Animazione dell'offset Y/z
        val offsetY = remember { Animatable(0f) }
        val scope = rememberCoroutineScope()

        LaunchedEffect(isMoved) {
            var targetY = if (isMoved) -130f else 0f
            offsetY.animateTo(
                targetY,
                animationSpec = spring(
                    dampingRatio = 0.4f,
                    stiffness = Spring.StiffnessLow
                )
            )
        }

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .offset { IntOffset(0, offsetY.value.roundToInt()) }
                    .size(55.dp)
                    .rotate(rotationZ.toFloat())
                    //.clickable { upDice[index] = !upDice[index] }
                    .pointerInput(Unit) {
                        awaitEachGesture {
                            //evento pressione del tasto
                            val downEvent =
                                awaitPointerEvent(PointerEventPass.Main)
                            downEvent.changes.forEach {
                                if (it.pressed) {
                                    onClick() //eseguo la funzione passata come argomento
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
                                        hfx?.click(0.5f)
                                    }
                                }
                            }
                        }
                    }
            ){
                Text(
                    text = text,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 50.sp
                )
            }
    }

//-------------------------------------


    @Composable
    fun swappingCards(heightMod: Int) {

        val configuration = LocalConfiguration.current
        val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
        var isFirstOnTop by remember { mutableStateOf(true) }
        val screenHeight = configuration.screenHeightDp.dp
        val screenWidth = configuration.screenWidthDp.dp
        val boxWidth = if (isPortrait) screenWidth*0.95f else screenWidth*0.4f
        val boxHeight = if (isPortrait) screenHeight*0.5f else screenHeight*0.85f
        val heightMod = if (isPortrait) 25 else 10

        val firstOffset by animateDpAsState(
            targetValue = if (isFirstOnTop) 0.dp else 20.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ), label = "FirstCardOffset"
        )

        val secondOffset by animateDpAsState(
            targetValue = if (isFirstOnTop) 20.dp else 0.dp,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessMediumLow
            ), label = "SecondCardOffset"
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
                .height(390.dp),// questa deve essere l'altezza di merda
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.CenterStart
            ) {
                // Second grid
                Box(
                    modifier = Modifier
                        .offset(x = secondOffset, y = secondOffset)
                        .zIndex(if (isFirstOnTop) 0f else 1f)
                        .size(boxWidth, boxHeight)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray) // background to keep same look
                        .shadow(2.dp, RectangleShape, clip = true)
                ) {
                    CombosGridComposition(heightMod)
                }

                // First grid
                Box(
                    modifier = Modifier
                        .offset(x = firstOffset, y = firstOffset)
                        .zIndex(if (isFirstOnTop) 1f else 0f)
                        .size(boxWidth, boxHeight)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.LightGray)
                ) {
                    CombosGridComposition(heightMod)
                }
            }
            /*
            Button(
                onClick = { isFirstOnTop = !isFirstOnTop },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Scambia con rimbalzo")
            }*/
        }
    }



    @Composable
    fun GameCards(){



    }

}


