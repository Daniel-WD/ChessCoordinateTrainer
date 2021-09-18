package com.titaniel.chesscoordinatetrainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
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
    private val _searchedTile: MutableLiveData<String> = MutableLiveData(null)
    val searchedTile: LiveData<String> = _searchedTile

    /**
     *
     */
    fun onTileClicked(notation: String) {

    }


}

@Composable
fun Trainer(viewModel: TrainerViewModel = viewModel()) {

    val searchedTile by viewModel.searchedTile.observeAsState("")

    TrainerWrapper(searchedTile)
}

@Composable
fun TrainerWrapper(searchedTile: String) {

    Column(modifier = Modifier.fillMaxSize()) {

        Text(modifier = Modifier.padding(16.dp), text = stringResource(id = R.string.app_name), fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(50.dp))

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {

            Text(text = searchedTile, fontWeight = FontWeight.Medium, fontSize = 40.sp)

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "White", fontSize = 28.sp)

            Spacer(modifier = Modifier.height(20.dp))

            ChessBoard(ChessColor.BLACK)

            Spacer(modifier = Modifier.height(20.dp))

            Text(text = "Black", fontSize = 28.sp)
        }

    }

}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun TrainerPreview() {
    ChessCoordinateTrainerTheme {
        TrainerWrapper("a4")
    }
}