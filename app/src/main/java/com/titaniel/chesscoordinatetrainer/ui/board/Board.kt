package com.titaniel.chesscoordinatetrainer.ui.board

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.titaniel.chesscoordinatetrainer.R


/**
 * Tile size
 */
private val TILE_SIZE = 42.dp

/**
 * Board border thickness
 */
private val BORDER_THICKNESS = 6.dp

/**
 * Chess color enum
 */
enum class ChessColor {
    BLACK,
    WHITE
}

/**
 * Maps each piece notation to its respective icon
 */
private val pieceIdByNotation = mapOf(
    "a1" to R.drawable.ic_rook_white,
    "b1" to R.drawable.ic_knight_white,
    "c1" to R.drawable.ic_bishop_white,
    "d1" to R.drawable.ic_queen_white,
    "e1" to R.drawable.ic_king_white,
    "f1" to R.drawable.ic_bishop_white,
    "g1" to R.drawable.ic_knight_white,
    "h1" to R.drawable.ic_rook_white,
    "a2" to R.drawable.ic_pawn_white,
    "b2" to R.drawable.ic_pawn_white,
    "c2" to R.drawable.ic_pawn_white,
    "d2" to R.drawable.ic_pawn_white,
    "e2" to R.drawable.ic_pawn_white,
    "f2" to R.drawable.ic_pawn_white,
    "g2" to R.drawable.ic_pawn_white,
    "h2" to R.drawable.ic_pawn_white,
    "a8" to R.drawable.ic_rook_black,
    "b8" to R.drawable.ic_knight_black,
    "c8" to R.drawable.ic_bishop_black,
    "d8" to R.drawable.ic_queen_black,
    "e8" to R.drawable.ic_king_black,
    "f8" to R.drawable.ic_bishop_black,
    "g8" to R.drawable.ic_knight_black,
    "h8" to R.drawable.ic_rook_black,
    "a7" to R.drawable.ic_pawn_black,
    "b7" to R.drawable.ic_pawn_black,
    "c7" to R.drawable.ic_pawn_black,
    "d7" to R.drawable.ic_pawn_black,
    "e7" to R.drawable.ic_pawn_black,
    "f7" to R.drawable.ic_pawn_black,
    "g7" to R.drawable.ic_pawn_black,
    "h7" to R.drawable.ic_pawn_black
)

@Composable
fun ChessBoard(boardColorFront: ChessColor = ChessColor.BLACK, onTileClick: (String) -> Unit = {}, showCoordinateRulers: Boolean = false, showPieces: Boolean = true) {

    val xAxis = ('a'..'h').let { if (boardColorFront == ChessColor.BLACK) it.reversed() else it }
        .map { it.toString() }

    val yAxis = (1..8).let { if (boardColorFront == ChessColor.WHITE) it.reversed() else it }
        .map { it.toString() }

    Column {

        if(showCoordinateRulers) {

            Row(modifier = Modifier.padding(start = TILE_SIZE/2 + BORDER_THICKNESS)) {

                xAxis.forEach { letter ->

                    Box(
                        modifier = Modifier.size(width = TILE_SIZE, height = TILE_SIZE / 2 + 4.dp)
                    ) {

                        Text(
                            modifier = Modifier.align(Alignment.Center),
                            text = letter
                        )
                    }

                }
            }
        } else {

            // MVP HACK
            Spacer(modifier = Modifier.height(TILE_SIZE / 2 + 4.dp))

        }

        Row {

            if(showCoordinateRulers) {

                Column(modifier = Modifier.padding(top = BORDER_THICKNESS)) {

                    yAxis.forEach { number ->

                        Box(
                            modifier = Modifier.size(width = TILE_SIZE / 2, height = TILE_SIZE)
                        ) {

                            Text(
                                modifier = Modifier.align(Alignment.Center),
                                text = number
                            )
                        }

                    }

                }
            }


            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.onBackground.copy(alpha = 0.15f))
                    .padding(BORDER_THICKNESS)
            ) {

                yAxis.forEachIndexed { i, yChar ->

                    Row {
                        xAxis.forEachIndexed { j, xChar ->

                            BoardTile(
                                type = if ((j + i) % 2 == 0) ChessColor.WHITE else ChessColor.BLACK,
                                xChar + yChar,
                                onTileClick,
                                showPieces
                            )
                        }
                    }
                }
            }
        }
    }


}

@Composable
fun BoardTile(type: ChessColor, notation: String, onClick: (String) -> Unit, showAvailablePiece: Boolean) {
    Box(
        modifier = Modifier
            .size(TILE_SIZE)
            .background(
                when (type) {
                    ChessColor.BLACK -> if (MaterialTheme.colors.isLight) Color(0xFFCACACA) else Color(
                        0xFF414141
                    )
                    else -> if (MaterialTheme.colors.isLight) Color.White else Color(0xFF646464)
                }
            )
            .clickable {
                onClick(notation)
            }
    ) {
        if(showAvailablePiece) pieceIdByNotation[notation]?.let { pieceId ->
            Image(
                modifier = Modifier
                    .padding(1.dp),
                painter = painterResource(id = pieceId),
                contentDescription = null
            )
        }

//        Text(text = notation)
    }
}

@Preview(showBackground = true)
@Composable
fun BoardPreview() {
    ChessBoard()
}