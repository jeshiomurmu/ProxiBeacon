package com.example.data.model

import androidx.compose.ui.graphics.Color
import com.example.ui.theme.BeaconAmber
import com.example.ui.theme.BeaconBlue
import com.example.ui.theme.BeaconEmerald
import com.example.ui.theme.BeaconPink
import com.example.ui.theme.BeaconViolet
import com.example.ui.theme.TextMuted

enum class BeaconVibe(
    val code: Byte,
    val title: String,
    val emoji: String,
    val description: String,
    val color: Color
) {
    OPEN_TO_CHAT(
        code = 1,
        title = "Open to Chat",
        emoji = "🟢",
        description = "Break time, ready to converse & meet new nomads",
        color = BeaconEmerald
    ),
    COFFEE_RUN(
        code = 2,
        title = "Coffee & Food Run",
        emoji = "☕",
        description = "Grabbing an espresso or lunch, happy to have company",
        color = BeaconAmber
    ),
    BRAINSTORM(
        code = 3,
        title = "Brainstorm / Feedback",
        emoji = "💡",
        description = "Indie hacking, code review, or creative bounce-off",
        color = BeaconViolet
    ),
    COWORK_CHILL(
        code = 4,
        title = "Co-work & Chill",
        emoji = "🤝",
        description = "Working nearby, say hi if on break",
        color = BeaconBlue
    ),
    QUICK_GAME(
        code = 5,
        title = "Quick Game / Stretch",
        emoji = "♟️",
        description = "Down for a game of chess, walk, or quick break",
        color = BeaconPink
    ),
    DEEP_FOCUS(
        code = 6,
        title = "Deep Focus (Passive)",
        emoji = "🎯",
        description = "Headphones on, beacon listening passively",
        color = TextMuted
    );

    companion object {
        fun fromCode(code: Byte): BeaconVibe {
            return entries.find { it.code == code } ?: OPEN_TO_CHAT
        }
    }
}
