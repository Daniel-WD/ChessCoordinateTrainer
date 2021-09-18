package com.titaniel.chesscoordinatetrainer.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.titaniel.chesscoordinatetrainer.R
import com.titaniel.chesscoordinatetrainer.ui.board.ChessBoard
import com.titaniel.chesscoordinatetrainer.ui.board.ChessColor
import com.titaniel.chesscoordinatetrainer.ui.theme.ChessCoordinateTrainerTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for Menu screen
 */
@HiltViewModel
class MenuViewModel @Inject constructor() : ViewModel() {

    /**
     *
     */
    fun start() {

    }

}

@Composable
fun Menu(viewModel: MenuViewModel = viewModel()) {
    MenuWrapper(viewModel::start)
}

@Composable
fun MenuWrapper(start: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Image(
            modifier = Modifier.padding(start = 64.dp, end = 64.dp, top = 18.dp),
            painter = painterResource(id = R.drawable.menu_hero),
            contentDescription = null
        )

        Text(text = stringResource(id = R.string.app_name), fontSize = 28.sp, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(50.dp))

        ChessBoard(ChessColor.BLACK)

        Spacer(modifier = Modifier.height(50.dp))

        TextButton(onClick = start) {
            Text(text = stringResource(R.string.menu_btn_start), color = Color.Black, fontSize = 32.sp)
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun MenuPreview() {
    ChessCoordinateTrainerTheme {
        MenuWrapper {}
    }
}