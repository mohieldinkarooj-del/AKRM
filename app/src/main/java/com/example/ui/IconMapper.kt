package com.example.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {
    fun getIcon(name: String): ImageVector {
        return when (name.lowercase()) {
            "brush" -> Icons.Default.Brush
            "school" -> Icons.Default.School
            "sports_esports" -> Icons.Default.SportsEsports
            "home" -> Icons.Default.Home
            "bathtub" -> Icons.Default.Bathtub
            "bedtime" -> Icons.Default.Bedtime
            "restaurant" -> Icons.Default.Restaurant
            "water_drop" -> Icons.Default.WaterDrop
            "wc" -> Icons.Default.Wc
            "airline_seat_flat" -> Icons.Default.AirlineSeatFlat
            "sentiment_very_satisfied", "frequent" -> Icons.Default.SentimentVerySatisfied
            "sentiment_very_dissatisfied" -> Icons.Default.SentimentVeryDissatisfied
            "warning" -> Icons.Default.Warning
            "volume_off" -> Icons.Default.VolumeOff
            "smartphone" -> Icons.Default.Smartphone
            "directions_walk" -> Icons.Default.DirectionsWalk
            "self_improvement" -> Icons.Default.SelfImprovement
            "favorite" -> Icons.Default.Favorite
            "add" -> Icons.Default.Add
            "delete" -> Icons.Default.Delete
            "mic" -> Icons.Default.Mic
            "stop" -> Icons.Default.Stop
            "play_arrow" -> Icons.Default.PlayArrow
            "settings" -> Icons.Default.Settings
            "person" -> Icons.Default.Person
            "sms" -> Icons.Default.Sms
            "help" -> Icons.Default.Help
            else -> Icons.Default.Help
        }
    }

    val availableIcons = listOf(
        "brush", "school", "sports_esports", "home", "bathtub", "bedtime",
        "restaurant", "water_drop", "wc", "airline_seat_flat",
        "sentiment_very_satisfied", "sentiment_very_dissatisfied",
        "warning", "volume_off", "smartphone", "directions_walk", "self_improvement", "favorite"
    )
}
