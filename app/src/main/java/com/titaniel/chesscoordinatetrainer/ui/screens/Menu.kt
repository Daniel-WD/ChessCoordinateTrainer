package com.titaniel.chesscoordinatetrainer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import com.titaniel.chesscoordinatetrainer.ui.theme.ChessCoordinateTrainerTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import javax.inject.Inject

@HiltViewModel
class MenuViewModel @Inject constructor() : ViewModel() {

    val name: String = "Hello World!"

}

@Composable
fun Menu() {
    Column {



    }
}



@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun DefaultPreview() {
    ChessCoordinateTrainerTheme {
        Menu()
    }
}