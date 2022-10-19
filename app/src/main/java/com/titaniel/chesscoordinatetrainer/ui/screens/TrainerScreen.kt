package com.titaniel.chesscoordinatetrainer.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.titaniel.chesscoordinatetrainer.firebase_logging.FirebaseLogging
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
    val feedbackManager: FeedbackManager,
    val firebaseLogging: FirebaseLogging
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
     * If the coordinate rulers should be shown
     */
    private val _showCoordinateRulers = MutableLiveData(false)
    val showCoordinateRulers: LiveData<Boolean> = _showCoordinateRulers

    /**
     * If pieces should be shown
     */
    private val _showPieces = MutableLiveData(true)
    val showPieces: LiveData<Boolean> = _showPieces

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

        // If notation is searched one
        val correct = _searchedTile.value == notation

        // If correct
        if (correct) {

            // Set new notation
            refreshSearchedTile()
        }

        // Log event
        firebaseLogging.logTileClicked(notation, correct)

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

        // Log feedback dialog send event
        firebaseLogging.logFeedbackDialogSend(feedback)

    }

    /**
     * Dismisses feedback dialog
     */
    fun onDismissFeedbackDialog() {

        // Dismiss dialog
        _feedbackDialogOpen.value = false

        // Log feedback dialog dismiss event
        firebaseLogging.logFeedbackDialogDismiss()
    }

    /**
     * Shows feedback dialog
     */
    fun onShowFeedbackDialog() {

        // Show feedback dialog
        _feedbackDialogOpen.value = true

        // Log feedback dialog open event
        firebaseLogging.logFeedbackDialogOpen()

    }

    /**
     * Toggles board rotation
     */
    fun onRotationChange() {

        // Calc new front color
        val newFrontColor = if (_frontColor.value == ChessColor.BLACK) ChessColor.WHITE else ChessColor.BLACK

        // Set new front color
        _frontColor.value = newFrontColor

        // Log rotation change
        firebaseLogging.logRotationChange(newFrontColor)

    }

    /**
     * Toggles coordinate rulers visibility
     */
    fun onShowCoordinateRulersChange() {

        // Calc new rulers visibility
        val newVisibility = !checkNotNull(_showCoordinateRulers.value)

        // Set visibility
        _showCoordinateRulers.value = newVisibility

        // Log rulers visibility change
        firebaseLogging.logCoordinateRulersVisibilityChange(newVisibility)

    }

    fun onShowPiecesChange() {
        _showPieces.value?.not()?.let {
            _showPieces.value = it
            firebaseLogging.logPiecesVisibilityChange(it)
        }
    }

}

@Composable
fun TrainerWrapper(viewModel: TrainerViewModel = viewModel()) {

    val searchedTile by viewModel.searchedTile.observeAsState("")
    val frontColor by viewModel.frontColor.observeAsState(ChessColor.BLACK)
    val feedbackDialogOpen by viewModel.feedbackDialogOpen.observeAsState(false)
    val thankYouDialogOpen by viewModel.thankYouDialogOpen.observeAsState(false)
    val showCoordinateRulers by viewModel.showCoordinateRulers.observeAsState(false)
    val showPieces by viewModel.showPieces.observeAsState(true)

    TrainerScreen(
        searchedTile,
        frontColor,
        viewModel::onTileClicked,
        feedbackDialogOpen,
        viewModel::onDismissFeedbackDialog,
        viewModel::onShowFeedbackDialog,
        viewModel::onSendFeedback,
        thankYouDialogOpen,
        viewModel::onRotationChange,
        showCoordinateRulers,
        viewModel::onShowCoordinateRulersChange,
        showPieces,
        viewModel::onShowPiecesChange
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
    onRotationChange: () -> Unit,
    showCoordinateRulers: Boolean,
    onShowCoordinateRulersChange: () -> Unit,
    showPieces: Boolean,
    onShowPiecesChange: () -> Unit
) {

    val iconTint = MaterialTheme.colors.onBackground.copy(alpha = 0.5f)

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp)
        ) {

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

                Row {
                    IconButton(onClick = onShowPiecesChange) {
                        Icon(
                            painterResource(id = if (showPieces) R.drawable.ic_baseline_extension_off_24 else R.drawable.ic_baseline_extension_24),
                            contentDescription = null,
                            tint = iconTint
                        )
                    }
                    IconButton(onClick = onRotationChange) {
                        Icon(
                            painterResource(id = R.drawable.ic_baseline_settings_backup_restore_24),
                            contentDescription = null,
                            tint = iconTint
                        )
                    }
                    IconButton(onClick = onShowCoordinateRulersChange) {
                        Icon(
                            painterResource(id = if (showCoordinateRulers) R.drawable.ic_baseline_visibility_off_24 else R.drawable.ic_baseline_visibility_24),
                            contentDescription = null,
                            tint = iconTint
                        )
                    }
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

                Spacer(modifier = Modifier.height(65.dp))

                ChessBoard(boardColorFront, onTileClicked, showCoordinateRulers, showPieces)

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
                tint = MaterialTheme.colors.onBackground.copy(alpha = 0.20f),
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
            onRotationChange = {},
            showCoordinateRulers = true,
            onShowCoordinateRulersChange = {},
            showPieces = false,
            onShowPiecesChange = {}
        )
    }
}