package com.titaniel.chesscoordinatetrainer.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.titaniel.chesscoordinatetrainer.R

@Composable
fun FeedbackDialog(onConfirm: (String) -> Unit, onDismiss: () -> Unit) {

    // Feedback text
    var feedbackText by remember { mutableStateOf("") }

    AlertDialog(
        confirmButton = {
            TextButton(
                onClick = { onConfirm(feedbackText) }
            ) {
                Text(text = stringResource(R.string.feedback_confirm).uppercase())
            }
        },
        dismissButton = {
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
                        tint = Color.Black
                    )
                    Text(
                        modifier = Modifier.padding(top = 10.dp),
                        text = stringResource(R.string.feedback_title),
                        fontSize = 16.sp,
                        color = Color.Black
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
                            Text(text = stringResource(R.string.feedback_hint))
                        }

                        it()
                    },
                    textStyle = TextStyle(fontSize = 14.sp)
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
                color = Color.Black,
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
