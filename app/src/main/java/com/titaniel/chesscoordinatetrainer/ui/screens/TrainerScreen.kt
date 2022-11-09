package com.titaniel.chesscoordinatetrainer.ui.screens

import android.app.Application
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ProductDetails
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.titaniel.chesscoordinatetrainer.R
import com.titaniel.chesscoordinatetrainer.feedback.FeedbackManager
import com.titaniel.chesscoordinatetrainer.firebase_config.FirebaseConfig
import com.titaniel.chesscoordinatetrainer.firebase_logging.FirebaseLogging
import com.titaniel.chesscoordinatetrainer.no_ads.NoAdsInteractor
import com.titaniel.chesscoordinatetrainer.ui.InterstitialAd
import com.titaniel.chesscoordinatetrainer.ui.board.ChessBoard
import com.titaniel.chesscoordinatetrainer.ui.board.ChessColor
import com.titaniel.chesscoordinatetrainer.ui.dialogs.FeedbackDialog
import com.titaniel.chesscoordinatetrainer.ui.dialogs.ThankYouDialog
import com.titaniel.chesscoordinatetrainer.ui.interstitialFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Trainer screen
 */
@HiltViewModel
class TrainerViewModel @Inject constructor(
    private val feedbackManager: FeedbackManager,
    val firebaseLogging: FirebaseLogging,
    private val firebaseConfig: FirebaseConfig,
    private val noAdsInteractor: NoAdsInteractor,
    app: Application
) : AndroidViewModel(app) {

    companion object {
        private const val THANK_YOU_DIALOG_SHOW_DURATION = 1000L
    }

    private val _searchedTile = MutableLiveData<String>()
    val searchedTile: LiveData<String> = _searchedTile

    private val _frontColor = MutableLiveData(ChessColor.WHITE)
    val frontColor: LiveData<ChessColor> = _frontColor

    private val _showCoordinateRulers = MutableLiveData(false)
    val showCoordinateRulers: LiveData<Boolean> = _showCoordinateRulers

    private val _showPieces = MutableLiveData(true)
    val showPieces: LiveData<Boolean> = _showPieces

    private val _feedbackDialogOpen = MutableLiveData(false)
    val feedbackDialogOpen: LiveData<Boolean> = _feedbackDialogOpen

    private val _thankYouDialogOpen = MutableLiveData(false)
    val thankYouDialogOpen: LiveData<Boolean> = _thankYouDialogOpen

    private val _showInterstitial = MutableLiveData(false)
    val showInterstitial: LiveData<Boolean> = _showInterstitial

    private val _nextInterstitial = MutableLiveData<InterstitialAd?>(null)
    val nextInterstitial: LiveData<InterstitialAd?> = _nextInterstitial

    private val interstitialFlow =
        interstitialFlow(app, app.getString(R.string.interstitial_ad_id))

    val showNoAdsButton = firebaseConfig.showNoAdsButton

    private var incorrectTileCount = 0
    private var correctTileCount = 0

    private val showIncorrectAd
        get() = firebaseConfig.showIncorrectAds && incorrectTileCount >= firebaseConfig.incorrectAddThreshold
    private val showCorrectAd
        get() = firebaseConfig.showCorrectAds && correctTileCount >= firebaseConfig.correctAdThreshold
    private val showAds
        get() = noAdsPurchased.value != true

    val noAdsPurchased = noAdsInteractor.isPurchased.asLiveData()

    val purchasableAdProduct =
        noAdsPurchased.switchMap { purchased -> if (purchased.not()) noAdsInteractor.productDetails.asLiveData() else emptyFlow<ProductDetails>().asLiveData() }

    init {
        refreshSearchedTile()
        viewModelScope.launch {
            _nextInterstitial.value = interstitialFlow.first()
            noAdsInteractor.isPurchased.collect {
                print(it)
            }
        }
    }

    private fun refreshSearchedTile() {
        _searchedTile.value = randomChessNotation(_searchedTile.value)
    }

    private fun randomChessNotation(previous: String? = null): String {
        val xValue = ('a'..'h').map { it.toString() }.random()
        val yValue = (1..8).map { it.toString() }.random()
        return (xValue + yValue).let { if (it == previous) randomChessNotation(previous) else it }
    }

    fun onTileClicked(notation: String) {
        val correct = _searchedTile.value == notation
        if (correct) {
            refreshSearchedTile()
            correctTileCount += 1
        } else {
            incorrectTileCount += 1
        }
        when {
            showAds && showIncorrectAd -> {
                _showInterstitial.value = true
                incorrectTileCount = 0
                firebaseLogging.logTriggerInterstitial()
            }
            showAds && showCorrectAd -> {
                _showInterstitial.value = true
                correctTileCount = 0
                firebaseLogging.logTriggerInterstitial()
            }
        }
        firebaseLogging.logTileClicked(notation, correct)
    }

    fun onSendFeedback(feedback: String) {
        feedbackManager.send(feedback)
        _feedbackDialogOpen.value = false
        viewModelScope.launch {
            _thankYouDialogOpen.value = true
            delay(THANK_YOU_DIALOG_SHOW_DURATION)
            _thankYouDialogOpen.value = false
        }
        firebaseLogging.logFeedbackDialogSend(feedback)
    }

    fun onDismissFeedbackDialog() {
        _feedbackDialogOpen.value = false
        firebaseLogging.logFeedbackDialogDismiss()
    }

    fun onShowFeedbackDialog() {
        _feedbackDialogOpen.value = true
        firebaseLogging.logFeedbackDialogOpen()
    }

    fun onRotationChange() {
        val newFrontColor =
            if (_frontColor.value == ChessColor.BLACK) ChessColor.WHITE else ChessColor.BLACK
        _frontColor.value = newFrontColor
        firebaseLogging.logRotationChange(newFrontColor)
    }

    fun onShowCoordinateRulersChange() {
        val newVisibility = !checkNotNull(_showCoordinateRulers.value)
        _showCoordinateRulers.value = newVisibility
        firebaseLogging.logCoordinateRulersVisibilityChange(newVisibility)
    }

    fun onShowPiecesChange() {
        _showPieces.value?.not()?.let {
            _showPieces.value = it
            firebaseLogging.logPiecesVisibilityChange(it)
        }
    }

    fun onShowInterstitial() {
        _showInterstitial.value = false
        viewModelScope.launch { _nextInterstitial.value = interstitialFlow.first() }
    }

}

@Composable
fun TrainerWrapper(
    viewModel: TrainerViewModel = viewModel(),
    presentInterstitial: (InterstitialAd) -> Unit,
    startPurchaseFlow: (ProductDetails) -> BillingResult?
) {

    val searchedTile by viewModel.searchedTile.observeAsState("")
    val frontColor by viewModel.frontColor.observeAsState(ChessColor.WHITE)
    val feedbackDialogOpen by viewModel.feedbackDialogOpen.observeAsState(false)
    val thankYouDialogOpen by viewModel.thankYouDialogOpen.observeAsState(false)
    val showCoordinateRulers by viewModel.showCoordinateRulers.observeAsState(false)
    val showPieces by viewModel.showPieces.observeAsState(true)
    val noAdsPurchased by viewModel.noAdsPurchased.observeAsState(false)
    val purchasableAdProduct by viewModel.purchasableAdProduct.observeAsState()

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
        viewModel::onShowPiecesChange,
        noAdsPurchased,
        purchasableAdProduct,
        startPurchaseFlow,
        viewModel.firebaseLogging, // todo
        viewModel.showNoAdsButton
    )

    val showInterstitial by viewModel.showInterstitial.observeAsState(false)
    val nextInterstitial by viewModel.nextInterstitial.observeAsState()

    if (showInterstitial) {
        nextInterstitial?.let {
            InterstitialAd(
                interstitialAd = it,
                presentInterstitial = presentInterstitial,
                onShow = viewModel::onShowInterstitial
            )
        }
    }
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
    onShowPiecesChange: () -> Unit,
    noAdsPurchased: Boolean,
    purchasableAdProduct: ProductDetails?,
    startPurchaseFlow: (ProductDetails) -> BillingResult?,
    firebaseLogging: FirebaseLogging,
    showNoAdsButton: Boolean
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
                verticalArrangement = Arrangement.Center // space even
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

        Column(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        ) {
            IconButton(
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(4.dp),
                onClick = onShowFeedbackDialog
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_baseline_chat_bubble_24),
                    tint = MaterialTheme.colors.onBackground.copy(alpha = 0.20f),
                    contentDescription = null
                )
            }

//            BannerAd(modifier = Modifier, id = stringResource(id = R.string.banner_ad_id))
        }

    }

    if (feedbackDialogOpen) {
        FeedbackDialog(
            onConfirm = onConfirmFeedback,
            onDismiss = onDismissFeedbackDialog,
            noAdsPurchased = noAdsPurchased,
            purchasableAdProduct = purchasableAdProduct,
            purchaseNoAds = {
                startPurchaseFlow(it)
                firebaseLogging.logPurchaseNoAdsClick()
            },
            showNoAdsButton = showNoAdsButton
        )
    }

    if (thankYouDialogOpen) {
        ThankYouDialog()
    }

}

//@Preview(showBackground = true, widthDp = 360, heightDp = 640)
//@Composable
//fun TrainerPreview() {
////    ChessCoordinateTrainerTheme {
////        TrainerScreen(
////            "a4",
////            ChessColor.BLACK,
////            onTileClicked = {},
////            feedbackDialogOpen = false,
////            onDismissFeedbackDialog = {},
////            onShowFeedbackDialog = {},
////            onConfirmFeedback = {},
////            thankYouDialogOpen = false,
////            onRotationChange = {},
////            showCoordinateRulers = true,
////            onShowCoordinateRulersChange = {},
////            showPieces = false,
////            onShowPiecesChange = {},
////            purchasableAdProduct = null,
////            startPurchaseFlow = { null },
////            noAdsPurchased = true
////        )
////    }
//}