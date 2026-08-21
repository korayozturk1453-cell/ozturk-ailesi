package com.example.ui.components

import android.content.Intent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.util.LoveAndFamilyQuotes

/**
 * Frameless single-line daily note placed directly under the top menu.
 * No box, card, or window container.
 */
@Composable
fun DailyNoteLine(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentQuoteIndex by remember { mutableStateOf((0..LoveAndFamilyQuotes.allQuotes.size - 1).random()) }
    val quote = LoveAndFamilyQuotes.getQuoteForIndex(currentQuoteIndex)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                currentQuoteIndex = (currentQuoteIndex + 1) % LoveAndFamilyQuotes.allQuotes.size
            }
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .testTag("daily_note_line"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.FormatQuote,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.85f),
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))

        AnimatedContent(
            targetState = quote,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "DailyNoteLineTransition",
            modifier = Modifier.weight(1f)
        ) { targetQuote ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "\"${targetQuote.text}\"",
                    fontSize = 12.sp,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                if (targetQuote.authorOrTag.isNotBlank()) {
                    Text(
                        text = " — ${targetQuote.authorOrTag}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                        modifier = Modifier.padding(start = 4.dp),
                        maxLines = 1
                    )
                }
            }
        }

        IconButton(
            onClick = {
                val sendIntent: Intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "\"${quote.text}\" ~ ${quote.authorOrTag}"
                    )
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Paylaş"))
            },
            modifier = Modifier.size(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "Paylaş",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                modifier = Modifier.size(13.dp)
            )
        }
    }
}

/**
 * Minimalist, subtle, elegant daily thought banner without loud badges or large frames.
 */
@Composable
fun InteractiveDailyQuoteBanner(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentQuoteIndex by remember { mutableStateOf((0..LoveAndFamilyQuotes.allQuotes.size - 1).random()) }
    val quote = LoveAndFamilyQuotes.getQuoteForIndex(currentQuoteIndex)

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_quote_banner")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Günün Notu",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {
                            currentQuoteIndex = (currentQuoteIndex + 1) % LoveAndFamilyQuotes.allQuotes.size
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Başka Not",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            val sendIntent: Intent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(
                                    Intent.EXTRA_TEXT,
                                    "\"${quote.text}\" ~ ${quote.authorOrTag}"
                                )
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Paylaş"))
                        },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Paylaş",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            AnimatedContent(
                targetState = quote,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "QuoteTransition"
            ) { targetQuote ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            currentQuoteIndex = (currentQuoteIndex + 1) % LoveAndFamilyQuotes.allQuotes.size
                        }
                        .padding(top = 2.dp)
                ) {
                    Text(
                        text = "\"${targetQuote.text}\"",
                        fontSize = 12.5.sp,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                        lineHeight = 17.sp
                    )
                    Text(
                        text = "— ${targetQuote.authorOrTag}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.65f),
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Clean, subtle small quote line for details or lists.
 */
@Composable
fun InFeedQuoteCard(
    quoteIndex: Int,
    modifier: Modifier = Modifier
) {
    val quote = LoveAndFamilyQuotes.getQuoteForIndex(quoteIndex)
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "\"${quote.text}\" ~ ${quote.authorOrTag}")
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Paylaş"))
            }
            .padding(vertical = 4.dp, horizontal = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.FormatQuote,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
            modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "\"${quote.text}\"",
            fontSize = 11.5.sp,
            fontStyle = FontStyle.Italic,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
            modifier = Modifier.weight(1f),
            maxLines = 2
        )
    }
}
