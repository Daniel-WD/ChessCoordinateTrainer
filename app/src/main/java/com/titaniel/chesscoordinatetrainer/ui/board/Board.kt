package com.titaniel.chesscoordinatetrainer.ui.board

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

enum class ChessColor {
    BLACK,
    WHITE
}

@Composable
fun ChessBoard(boardColorFront: ChessColor = ChessColor.BLACK, onTileClick: (String) -> Unit = {}) {

    val xAxis = ('a'..'h').let { if (boardColorFront == ChessColor.BLACK) it.reversed() else it }
        .map { it.toString() }

    val yAxis = (1..8).let { if (boardColorFront == ChessColor.WHITE) it.reversed() else it }
        .map { it.toString() }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.onBackground.copy(alpha = 0.14f))
            .padding(6.dp)
    ) {

        yAxis.forEachIndexed { i, yChar ->

            Row {
                xAxis.forEachIndexed { j, xChar ->

                    BoardTile(
                        type = if ((j + i) % 2 == 0) ChessColor.WHITE else ChessColor.BLACK,
                        xChar + yChar,
                        onTileClick
                    )
                }
            }
        }
    }

}

@Composable
fun BoardTile(type: ChessColor, notation: String, onClick: (String) -> Unit) {
    Box(
        modifier = Modifier
            .size(42.dp)
            .background(
                when (type) {
                    ChessColor.BLACK -> if (MaterialTheme.colors.isLight) Color(0xFF989898) else MaterialTheme.colors.background
                    else -> if (MaterialTheme.colors.isLight) Color.White else Color(0xFF5E5E5E)
                }
            )
            .clickable {
                onClick(notation)
            }
    ) {
        //Text(text = notation)
    }
}

@Preview(showBackground = true)
@Composable
fun BoardPreview() {
    ChessBoard()
}