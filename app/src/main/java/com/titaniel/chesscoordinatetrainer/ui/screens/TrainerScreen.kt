package com.titaniel.chesscoordinatetrainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.titaniel.chesscoordinatetrainer.R
import com.titaniel.chesscoordinatetrainer.feedback.FeedbackManager
import com.titaniel.chesscoordinatetrainer.ui.board.ChessBoard
import com.titaniel.chesscoordinatetrainer.ui.board.ChessColor
import com.titaniel.chesscoordinatetrainer.ui.dialogs.FeedbackDialog
import com.titaniel.chesscoordinatetrainer.ui.dialogs.ThankYouDialog
import com.titaniel.chesscoordinatetrainer.ui.theme.ChessCoordinateTrainerTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Trainer screen
 */
@HiltViewModel
class TrainerViewModel @Inject constructor(
    val feedbackManager: FeedbackManager
) : ViewModel() {

    companion object {

        /**
         * Thank you dialog show duration after sending feedback, in ms.
         */
        private const val THANK_YOU_DIALOG_SHOW_DURATION = 1000L

    }

    /**
     * Notation of currently searched tile
     */
    private val _searchedTile = MutableLiveData<String>()
    val searchedTile: LiveData<String> = _searchedTile

    /**
     * Current board orientation
     */
    private val _frontColor = MutableLiveData(ChessColor.WHITE)
    val frontColor: LiveData<ChessColor> = _frontColor

    /**
     * If feedback dialog is shown
     */
    private val _feedbackDialogOpen = MutableLiveData(false)
    val feedbackDialogOpen: LiveData<Boolean> = _feedbackDialogOpen

    /**
     * If thank you dialog is shown
     */
    private val _thankYouDialogOpen = MutableLiveData(false)
    val thankYouDialogOpen: LiveData<Boolean> = _thankYouDialogOpen

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

    /**
     * Sends feedback, closes feedback dialog and show thank you dialog for
     * [THANK_YOU_DIALOG_SHOW_DURATION].
     */
    fun onSendFeedback(feedback: String) {

        // Send feedback
        feedbackManager.send(feedback)

        // Dismiss feedback dialog
        _feedbackDialogOpen.value = false

        // Open thank you dialog
        _thankYouDialogOpen.value = true

        viewModelScope.launch {

            // Wait for a time
            delay(THANK_YOU_DIALOG_SHOW_DURATION)

            // Dismiss thank you dialog
            _thankYouDialogOpen.value = false
        }

    }

    /**
     * Dismisses feedback dialog
     */
    fun onDismissFeedbackDialog() {

        // Dismiss dialog
        _feedbackDialogOpen.value = false
    }

    /**
     * Shows feedback dialog
     */
    fun onShowFeedbackDialog() {

        // Show feedback dialog
        _feedbackDialogOpen.value = true

    }

    /**
     * Changes board rotation
     */
    fun onRotationChange() {

        // Change front color
        _frontColor.value = if(_frontColor.value == ChessColor.BLACK) ChessColor.WHITE else ChessColor.BLACK

    }

}

@Composable
fun TrainerWrapper(viewModel: TrainerViewModel = viewModel()) {

    val searchedTile by viewModel.searchedTile.observeAsState("")
    val frontColor by viewModel.frontColor.observeAsState(ChessColor.BLACK)
    val feedbackDialogOpen by viewModel.feedbackDialogOpen.observeAsState(false)
    val thankYouDialogOpen by viewModel.thankYouDialogOpen.observeAsState(false)

    TrainerScreen(
        searchedTile,
        frontColor,
        viewModel::onTileClicked,
        feedbackDialogOpen,
        viewModel::onDismissFeedbackDialog,
        viewModel::onShowFeedbackDialog,
        viewModel::onSendFeedback,
        thankYouDialogOpen,
        viewModel::onRotationChange
    )
}

@Composable
fun TrainerScreen(
    searchedTile: String,
    boardColorFront: ChessColor,
    onTileClicked: (String) -> Unit,
    feedbackDialogOpen: Boolean,
    onDismissFeedbackDialog: () -> Unit,
    onShowFeedbackDialog: () -> Unit,
    onConfirmFeedback: (String) -> Unit,
    thankYouDialogOpen: Boolean,
    onRotationChange: () -> Unit
) {

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier.fillMaxSize().padding(bottom = 40.dp)) {

            Row(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .height(56.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    fontWeight = FontWeight.Medium,
                    fontSize = 18.sp
                )

                IconButton(onClick = onRotationChange) {
                    Icon(
                        painterResource(id = R.drawable.ic_baseline_settings_backup_restore_24),
                        contentDescription = null,
                        tint = Color(0xFF8F8F8F)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
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

        IconButton(
            modifier = Modifier
                .padding(4.dp)
                .align(Alignment.BottomEnd),
            onClick = onShowFeedbackDialog
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_chat_bubble_24),
                tint = Color(0xFFD4D4D4),
                contentDescription = null
            )
        }

    }


    if (feedbackDialogOpen) {
        FeedbackDialog(onConfirm = onConfirmFeedback, onDismiss = onDismissFeedbackDialog)
    }

    if (thankYouDialogOpen) {
        ThankYouDialog()
    }

}

@Preview(showBackground = true, widthDp = 360, heightDp = 640)
@Composable
fun TrainerPreview() {
    ChessCoordinateTrainerTheme {
        TrainerScreen(
            "a4",
            ChessColor.BLACK,
            onTileClicked = {},
            feedbackDialogOpen = false,
            onDismissFeedbackDialog = {},
            onShowFeedbackDialog = {},
            onConfirmFeedback = {},
            thankYouDialogOpen = false,
            onRotationChange = {}
        )
    }
}