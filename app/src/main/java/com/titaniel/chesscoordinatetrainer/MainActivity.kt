package com.titaniel.chesscoordinatetrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.titaniel.chesscoordinatetrainer.ui.screens.Menu
import com.titaniel.chesscoordinatetrainer.ui.screens.Trainer
import com.titaniel.chesscoordinatetrainer.ui.theme.ChessCoordinateTrainerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ChessCoordinateTrainerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {

                    Trainer()

                }
            }
        }
    }
}