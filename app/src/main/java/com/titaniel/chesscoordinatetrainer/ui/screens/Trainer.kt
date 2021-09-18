package com.titaniel.chesscoordinatetrainer.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.titaniel.chesscoordinatetrainer.R
import com.titaniel.chesscoordinatetrainer.ui.board.ChessBoard
import com.titaniel.chesscoordinatetrainer.ui.board.ChessColor
import com.titaniel.chesscoordinatetrainer.ui.theme.ChessCoordinateTrainerTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for Trainer screen
 */
@HiltViewModel
class TrainerViewModel @Inject constructor() : ViewModel() {

    /**
     * Notation of currently searched tile
     */
    private val _searchedTile: MutableLiveData<String> = MutableLiveData()
    val searchedTile: LiveData<String> = _searchedTile

    /**
     * Current board orientation
     */
    private val _frontColor: MutableLiveData<ChessColor> = MutableLiveData(ChessColor.BLACK)
    val frontColor: LiveData<ChessColor> = _frontColor

    init {
        refreshSearchedTile()
    }

    /**
     * Refreshes value of [_searchedTile]
     */
    private fun refreshSearchedTile() {
        _searchedTile.value = randomChessNotation(_searchedTile.value)
    }

    /**
     * Returns random chess notation
     */
    private fun randomChessNotation(previous: String? = null): String {

        // Calc random x axis value
        val xValue = ('a'..'h').map { it.toString() }.random()

        // Calc random y axis value
        val yValue = (1..8).map { it.toString() }.random()

        // Return combined values
        return (xValue + yValue).let { if (it == previous) randomChessNotation(previous) else it }
    }

    /**
     * Refreshes searched notation if [notation] equals it. Otherwise does nothing.
     */
    fun onTileClicked(notation: String) {

        // If notation is correct...
        if (_searchedTile.value == notation) {

            // Set new notation
            refreshSearchedTile()
        }

    }


}

@Composable
fun Trainer(viewModel: TrainerViewModel = viewModel()) {

    val searchedTile by viewModel.searchedTile.observeAsState("")
    val frontColor by viewModel.frontColor.observeAsState(ChessColor.BLACK)

    TrainerWrapper(searchedTile, frontColor, viewModel::onTileClicked)
}

@Composable
fun TrainerWrapper(
    searchedTile: String,
    boardColorFront: ChessColor,
    onTileClicked: (String) -> Unit
) {

    Column(modifier = Modifier.fillMaxSize()) {

        Text(
            modifier = Modifier
                .padding(top = 16.dp, start = 16.dp)
                .weight(0.15f),
            text = stringResource(id = R.string.app_name),
            fontWeight = FontWeight.Medium,
            fontSize = 18.sp
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(0.85f), horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Text(
                text = searchedTile,
                fontWeight = FontWeight.Medium,
                fontSize = 40.sp
            )

            Column(
                modifier = Modifier.padding(top = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    modifier = Modifier.padding(bottom = 10.dp),
                    text = when (boardColorFront) {
                        ChessColor.BLACK -> stringResource(R.string.trainer_color_white)
                        else -> stringResource(R.string.trainer_color_black)
                    },
                    fontSize = 28.sp
                )

                ChessBoard(boardColorFront, onTileClicked)

                Text(
                    modifier = Modifier.padding(top = 10.dp),
                    text = when (boardColorFront) {
                        ChessColor.BLACK -> stringResource(id = R.string.trainer_color_black)
                        else -> stringResource(id = R.string.trainer_color_white)
                    },
                    fontSize = 28.sp
                )
            }

        }

    }

}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun TrainerPreview() {
    ChessCoordinateTrainerTheme {
        TrainerWrapper("a4", ChessColor.BLACK) {}
    }
}