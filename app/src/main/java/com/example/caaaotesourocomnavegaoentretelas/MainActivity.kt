package com.example.caaaotesourocomnavegaoentretelas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.caaaotesourocomnavegaoentretelas.ui.theme.CaçaAoTesouroComNavegaçãoEntreTelasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CaçaAoTesouroComNavegaçãoEntreTelasTheme {
                TreasureHuntApp()
            }
        }
    }
}

@Composable
fun TreasureHuntApp() {
    val navController = rememberNavController()
    var startTime by remember { mutableLongStateOf(0L) }
    var totalTime by remember { mutableLongStateOf(0L) }

    NavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500))
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            ) + fadeIn(animationSpec = tween(500))
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(500)
            ) + fadeOut(animationSpec = tween(500))
        }
    ) {
        composable("home") {
            HomeScreen(onStart = {
                startTime = System.currentTimeMillis()
                navController.navigate("clue1")
            })
        }
        composable("clue1") {
            ClueScreen(
                title = "Pista 1",
                clue = "O que tem que ser quebrado para ser usado?",
                answer = "ovo",
                onNext = { navController.navigate("clue2") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("clue2") {
            ClueScreen(
                title = "Pista 2",
                clue = "Quanto mais seca, mais molhada fica?",
                answer = "toalha",
                onNext = { navController.navigate("clue3") },
                onBack = { navController.popBackStack() }
            )
        }
        composable("clue3") {
            ClueScreen(
                title = "Pista 3",
                clue = "O que é que corre, mas não tem pernas?",
                answer = "rio",
                onNext = {
                    totalTime = (System.currentTimeMillis() - startTime) / 1000
                    navController.navigate("treasure") {
                        popUpTo("home") { inclusive = false }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable("treasure") {
            TreasureScreen(
                totalTime = totalTime,
                onRestart = {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun HomeScreen(onStart: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Caça ao Tesouro",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = onStart) {
                Text("Iniciar caça ao tesouro")
            }
        }
    }
}

@Composable
fun ClueScreen(
    title: String,
    clue: String,
    answer: String,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    var userAnswer by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = clue,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            OutlinedTextField(
                value = userAnswer,
                onValueChange = {
                    userAnswer = it
                    showError = false
                },
                label = { Text("Sua resposta") },
                isError = showError,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (showError) {
                Text(
                    text = "Resposta incorreta! Tente novamente.",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = onBack) {
                    Text("Voltar")
                }
                Button(onClick = {
                    if (userAnswer.trim().lowercase() == answer.lowercase()) {
                        onNext()
                    } else {
                        showError = true
                    }
                }) {
                    Text("Próxima Pista")
                }
            }
        }
    }
}

@Composable
fun TreasureScreen(totalTime: Long, onRestart: () -> Unit) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Parabéns!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Você encontrou o tesouro!",
                fontSize = 22.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Tempo total: $totalTime segundos",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            Button(onClick = onRestart) {
                Text("Recomeçar")
            }
        }
    }
}
