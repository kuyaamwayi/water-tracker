package com.example.watertracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = WaterRepository(this)

        setContent {
            val volume by repository.volumeFlow.collectAsState(initial = 0)
            val streak by repository.streakFlow.collectAsState(initial = 0)
            val scope = rememberCoroutineScope()

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = Color(0xFFF0F8FF)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Water Tracker",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0099CC)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$streak day streak",
                        fontSize = 16.sp,
                        color = Color(0xFFFF5722)
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "${volume}ml",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF003A52)
                    )
                    Text(
                        text = "of ${DAILY_GOAL_ML}ml goal",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    LinearProgressIndicator(
                        progress = (volume.toFloat() / DAILY_GOAL_ML).coerceIn(0f, 1f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(12.dp),
                        color = Color(0xFF0099CC),
                        trackColor = Color(0xFFCCECF7)
                    )
                    Spacer(modifier = Modifier.height(40.dp))
                    Button(
                        onClick = { scope.launch { repository.addBottle() } },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0099CC)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                    ) {
                        Text("+ Log 500ml", fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Or tap your NFC tag to log automatically",
                        fontSize = 12.sp,
                        color = Color(0xFF999999)
                    )
                }
            }
        }
    }
}
