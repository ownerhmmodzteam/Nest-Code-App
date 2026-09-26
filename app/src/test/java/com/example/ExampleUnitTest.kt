package com.example

import com.example.data.catalog.CurriculumCatalog
import com.example.engine.AlgorithmVisualizer
import com.example.engine.CodeSandboxEngine
import com.example.util.XpLevelCalculator
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testCurriculumCatalog_hasAll17Levels() {
        val levels = CurriculumCatalog.getAllLevels()
        assertEquals(17, levels.size)
        assertEquals(0, levels.first().levelNumber)
        assertEquals(16, levels.last().levelNumber)
    }

    @Test
    fun testCodeSandboxEngine_evaluatesJavaScript() {
        val jsCode = "console.log(\"Halo CodeNest\");"
        val result = CodeSandboxEngine.executeCode(jsCode, "javascript")
        assertFalse(result.isError)
        assertTrue(result.output.contains("Halo CodeNest"))
    }

    @Test
    fun testAlgorithmVisualizer_bubbleSortGeneratesSteps() {
        val initial = listOf(5, 1, 4, 2, 8)
        val steps = AlgorithmVisualizer.generateBubbleSortSteps(initial)
        assertTrue(steps.size > 2)
        assertEquals(listOf(1, 2, 4, 5, 8), steps.last().arrayState)
    }

    @Test
    fun testAlgorithmVisualizer_binarySearchFindsTarget() {
        val sorted = listOf(2, 5, 8, 12, 16, 23, 38)
        val steps = AlgorithmVisualizer.generateBinarySearchSteps(sorted, 12)
        assertTrue(steps.any { it.description.contains("Ditemukan") })
    }

    // ==========================================
    // XP & LEVEL 1 - 9999 SYSTEM UNIT TESTS
    // ==========================================
    @Test
    fun testLevelFormula_level1RequiresZeroXp() {
        assertEquals(0L, XpLevelCalculator.requiredXpForLevel(1))
        assertEquals(0L, XpLevelCalculator.requiredXpForLevel(0))
        assertEquals(1, XpLevelCalculator.calculateLevel(0L))
    }

    @Test
    fun testLevelFormula_exactValues() {
        // Level 2 = floor(100 * (2 - 1)^1.5) = 100
        assertEquals(100L, XpLevelCalculator.requiredXpForLevel(2))

        // Level 3 = floor(100 * 2^1.5) = floor(100 * 2.828427) = 282
        assertEquals(282L, XpLevelCalculator.requiredXpForLevel(3))

        // Level 10 = floor(100 * 9^1.5) = floor(100 * 27) = 2700
        assertEquals(2700L, XpLevelCalculator.requiredXpForLevel(10))

        // Level 9999 = floor(100 * 9998^1.5) = 99970001
        val maxReq = XpLevelCalculator.requiredXpForLevel(9999)
        assertEquals(99970001L, maxReq)
    }

    @Test
    fun testLevelCapAt9999() {
        // Any XP >= 99,970,001 must be capped at 9999, NEVER level 10000
        assertEquals(9999, XpLevelCalculator.calculateLevel(99970001L))
        assertEquals(9999, XpLevelCalculator.calculateLevel(100000000L))
        assertEquals(9999, XpLevelCalculator.calculateLevel(100001000L))
        assertEquals(9999, XpLevelCalculator.calculateLevel(200000000L))
    }

    @Test
    fun testLevelCalculationBinarySearch_accuracy() {
        // Just before level 2
        assertEquals(1, XpLevelCalculator.calculateLevel(99L))
        // Exactly level 2
        assertEquals(2, XpLevelCalculator.calculateLevel(100L))
        // Just before level 3
        assertEquals(2, XpLevelCalculator.calculateLevel(281L))
        // Exactly level 3
        assertEquals(3, XpLevelCalculator.calculateLevel(282L))
        // Exactly level 10
        assertEquals(10, XpLevelCalculator.calculateLevel(2700L))
    }

    @Test
    fun testLevelProgress_maxLevelBehavior() {
        val maxProgress = XpLevelCalculator.getLevelProgress(150000000L)
        assertEquals(9999, maxProgress.currentLevel)
        assertTrue(maxProgress.isMaxLevel)
        assertEquals(1.0f, maxProgress.progressFraction, 0.001f)
        assertEquals(0L, maxProgress.remainingXpToNextLevel)
    }
}
