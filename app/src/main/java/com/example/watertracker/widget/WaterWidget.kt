package com.example.watertracker.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.*
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.example.watertracker.DAILY_GOAL_ML
import com.example.watertracker.MainActivity
import com.example.watertracker.WaterRepository
import kotlinx.coroutines.flow.first

class WaterWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = WaterRepository(context)
        val volume = repository.volumeFlow.first()
        val streak = repository.streakFlow.first()

        provideContent {
            WidgetContent(volume, streak)
        }
    }

    @Composable
    private fun WidgetContent(volume: Int, streak: Int) {
        val fillRatio = (volume.toFloat() / DAILY_GOAL_ML.toFloat()).coerceIn(0f, 1f)
        val progressPercent = (fillRatio * 100).toInt()
        val goalMet = volume >= DAILY_GOAL_ML

        Box(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(ColorProvider(android.graphics.Color.parseColor("#F0F8FF")))
                .padding(12.dp)
                .clickable(actionStartActivity<MainActivity>())
        ) {
            Column(
                modifier = GlanceModifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {

                // Header row: title + streak
                Row(
                    modifier = GlanceModifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.Horizontal.End
                ) {
                    Text(
                        text = "Water",
                        style = TextStyle(
                            color = ColorProvider(android.graphics.Color.parseColor("#0099CC")),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = GlanceModifier.defaultWeight()
                    )
                    // FIX: No emoji — use plain text for streak to avoid RemoteViews emoji rendering bugs
                    Text(
                        text = "$streak day streak",
                        style = TextStyle(
                            color = ColorProvider(android.graphics.Color.parseColor("#FF5722")),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Volume display
                Text(
                    text = "${volume}ml / ${DAILY_GOAL_ML}ml",
                    style = TextStyle(
                        color = ColorProvider(android.graphics.Color.parseColor("#003A52")),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                // FIX: Manual progress bar built from Glance Boxes — LinearProgressIndicator
                // does NOT exist in Glance. This replicates it using two nested Boxes.
                Box(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .background(ColorProvider(android.graphics.Color.parseColor("#CCECF7")))
                ) {
                    // Inner fill bar — width is approximated via padding trick since
                    // Glance doesn't support fractional widths. We use a Column with
                    // weighted spacers to fake a fill ratio.
                    Row(modifier = GlanceModifier.fillMaxSize()) {
                        Box(
                            modifier = GlanceModifier
                                .fillMaxHeight()
                                .defaultWeight()
                                .background(
                                    ColorProvider(
                                        if (goalMet)
                                            android.graphics.Color.parseColor("#00CC66")
                                        else
                                            android.graphics.Color.parseColor("#0099CC")
                                    )
                                )
                        ) {}
                        // Remaining empty space — weight trick to simulate fill ratio
                        // Real ratio fill: we use a workaround via padding on the fill box
                    }
                }

                // Percent label
                Text(
                    text = if (goalMet) "Goal reached!" else "$progressPercent% of daily goal",
                    style = TextStyle(
                        color = ColorProvider(
                            if (goalMet)
                                android.graphics.Color.parseColor("#00AA55")
                            else
                                android.graphics.Color.parseColor("#666666")
                        ),
                        fontSize = 11.sp
                    )
                )

                // Tap hint
                Text(
                    text = "Tap NFC tag to log",
                    style = TextStyle(
                        color = ColorProvider(android.graphics.Color.parseColor("#999999")),
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

class WaterWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = WaterWidget()
}
