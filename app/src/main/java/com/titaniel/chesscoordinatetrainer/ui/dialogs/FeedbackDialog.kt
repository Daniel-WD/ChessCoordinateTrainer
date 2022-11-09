package com.titaniel.chesscoordinatetrainer.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.android.billingclient.api.ProductDetails
import com.titaniel.chesscoordinatetrainer.R

@Composable
fun FeedbackDialog(
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
    noAdsPurchased: Boolean,
    showNoAdsButton: Boolean,
    purchasableAdProduct: ProductDetails?,
    purchaseNoAds: (ProductDetails) -> Unit
) {
    var feedbackText by remember { mutableStateOf("") }

    AlertDialog(
        confirmButton = {
            Row {
                TextButton(
                    onClick = { onConfirm(feedbackText) }
                ) {
                    Text(text = stringResource(R.string.feedback_confirm).uppercase())
                }
            }
        },
        dismissButton = {
            if (noAdsPurchased) {
                TextButton(
                    onClick = { },
                    enabled = false
                ) {
                    Text(text = stringResource(R.string.feedback_no_ads).uppercase())
                    Icon(modifier = Modifier.padding(start = 4.dp).size(20.dp), painter = painterResource(id = R.drawable.ic_baseline_check_24), contentDescription = null)
                }
            } else if(showNoAdsButton) {
                purchasableAdProduct?.oneTimePurchaseOfferDetails?.formattedPrice?.let { price ->
                    TextButton(
                        onClick = { purchaseNoAds(purchasableAdProduct) }
                    ) {
                        Text(text = stringResource(R.string.feedback_no_ads_with_price, price).uppercase())
                    }
                }
            }
            TextButton(
                onClick = onDismiss
            ) {
                Text(text = stringResource(R.string.feedback_dismiss).uppercase())
            }
        },
        text = {
            Column(modifier = Modifier.wrapContentHeight()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        modifier = Modifier.size(24.dp),
                        painter = painterResource(id = R.drawable.ic_baseline_chat_bubble_24),
                        contentDescription = null,
                        tint = MaterialTheme.colors.onSurface
                    )
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = stringResource(R.string.feedback_title),
                        fontSize = 16.sp,
                        color = MaterialTheme.colors.onSurface
                    )
                }

                BasicTextField(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth()
                        .height(100.dp),
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    decorationBox = {
                        if (feedbackText.isEmpty()) {
                            Text(
                                text = stringResource(R.string.feedback_hint),
                                color = MaterialTheme.colors.onSurface
                            )
                        }

                        it()
                    },
                    textStyle = TextStyle(fontSize = 14.sp, color = MaterialTheme.colors.onSurface),
                    cursorBrush = SolidColor(MaterialTheme.colors.onSurface)
                )
            }
        },
        onDismissRequest = onDismiss,
        title = {}
    )
}

@Composable
fun ThankYouDialog() {
    AlertDialog(
        text = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = "❤ Thank You! ❤",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                color = MaterialTheme.colors.onBackground,
                textAlign = TextAlign.Center
            )
        },
        onDismissRequest = {},
        buttons = {}
    )
}

@Preview
@Composable
fun FeedbackDialogPreview() {
    ThankYouDialog()
}
