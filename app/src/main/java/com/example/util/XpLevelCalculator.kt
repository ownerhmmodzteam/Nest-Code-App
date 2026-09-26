package com.example.util

import kotlin.math.floor
import kotlin.math.pow

data class LevelProgress(
    val currentLevel: Int,
    val currentXp: Long,
    val currentLevelMinXp: Long,
    val nextLevelMinXp: Long,
    val progressFraction: Float,
    val remainingXpToNextLevel: Long,
    val isMaxLevel: Boolean
)

object XpLevelCalculator {

    const val MIN_LEVEL = 1
    const val MAX_LEVEL = 9999

    val MILESTONE_TITLES: Map<Int, String> = mapOf(
        1 to "Code Novice",
        2 to "Novice Coder",
        5 to "Beginner Badge",
        10 to "First Milestone",
        25 to "Learner Badge",
        50 to "Dedicated Learner",
        100 to "CodeNest Explorer",
        250 to "Code Craftsman",
        500 to "Advanced Learner",
        1000 to "CodeNest Master",
        2500 to "Elite Learner",
        5000 to "Veteran Engineer",
        7500 to "Grandmaster Architect",
        9000 to "Legendary Coder",
        9999 to "CodeNest Apex"
    )

    /**
     * Exact required XP formula:
     * requiredXpForLevel(level) = floor(100 * (level - 1)^1.5)
     * Level 1 = 0 XP
     * Level 9999 = absolute maximum (~99,970,004 XP)
     */
    fun requiredXpForLevel(level: Int): Long {
        if (level <= MIN_LEVEL) return 0L
        val effectiveLevel = level.coerceAtMost(MAX_LEVEL)
        val base = (effectiveLevel - 1).toDouble()
        return floor(100.0 * base.pow(1.5)).toLong()
    }

    /**
     * Calculate Level from total XP using logarithmic Binary Search.
     * Guaranteed to return an integer between 1 and 9999.
     * Never returns Level 10000.
     */
    fun calculateLevel(xp: Long): Int {
        if (xp <= 0L) return MIN_LEVEL
        val maxLevelXp = requiredXpForLevel(MAX_LEVEL)
        if (xp >= maxLevelXp) return MAX_LEVEL

        var low = MIN_LEVEL
        var high = MAX_LEVEL
        var ans = MIN_LEVEL

        while (low <= high) {
            val mid = (low + high) / 2
            val req = requiredXpForLevel(mid)
            if (req <= xp) {
                ans = mid
                low = mid + 1
            } else {
                high = mid - 1
            }
        }

        return ans.coerceIn(MIN_LEVEL, MAX_LEVEL)
    }

    /**
     * Compute comprehensive progress details for UI presentation.
     */
    fun getLevelProgress(xp: Long): LevelProgress {
        val currentLevel = calculateLevel(xp)
        val isMax = currentLevel >= MAX_LEVEL

        if (isMax) {
            val maxReq = requiredXpForLevel(MAX_LEVEL)
            return LevelProgress(
                currentLevel = MAX_LEVEL,
                currentXp = xp,
                currentLevelMinXp = maxReq,
                nextLevelMinXp = maxReq,
                progressFraction = 1.0f,
                remainingXpToNextLevel = 0L,
                isMaxLevel = true
            )
        }

        val curReq = requiredXpForLevel(currentLevel)
        val nextReq = requiredXpForLevel(currentLevel + 1)
        val diff = (nextReq - curReq).coerceAtLeast(1L)
        val earnedInLevel = (xp - curReq).coerceAtLeast(0L)
        val progress = (earnedInLevel.toFloat() / diff.toFloat()).coerceIn(0.0f, 1.0f)
        val remaining = (nextReq - xp).coerceAtLeast(0L)

        return LevelProgress(
            currentLevel = currentLevel,
            currentXp = xp,
            currentLevelMinXp = curReq,
            nextLevelMinXp = nextReq,
            progressFraction = progress,
            remainingXpToNextLevel = remaining,
            isMaxLevel = false
        )
    }

    fun getMilestoneTitle(level: Int): String {
        return MILESTONE_TITLES[level] ?: when {
            level >= 9999 -> "CodeNest Apex"
            level >= 5000 -> "Veteran Engineer"
            level >= 1000 -> "CodeNest Master"
            level >= 500 -> "Advanced Learner"
            level >= 100 -> "CodeNest Explorer"
            level >= 25 -> "Learner"
            level >= 5 -> "Beginner"
            else -> "Code Novice"
        }
    }
}
