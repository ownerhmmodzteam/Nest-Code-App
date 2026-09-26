package com.example

import com.example.data.catalog.CurriculumCatalog
import com.example.util.CurriculumUtils
import org.junit.Assert.assertTrue
import org.junit.Test

class CurriculumFilterTest {

    @Test
    fun testLevelSearchByNumber() {
        val allLevels = CurriculumCatalog.getAllLevels()

        val level0 = allLevels.find { it.levelNumber == 0 }
        val level1 = allLevels.find { it.levelNumber == 1 }

        assertTrue(level0 != null)
        assertTrue(level1 != null)
        assertTrue(allLevels.size >= 17)
    }

    @Test
    fun testLanguageExtractionAndFiltering() {
        val allLevels = CurriculumCatalog.getAllLevels()

        // Test Python course detection
        val pythonCourses = allLevels.filter { level ->
            val langs = CurriculumUtils.getLanguagesForLevel(level)
            langs.contains("python")
        }
        assertTrue("Should detect courses with Python", pythonCourses.isNotEmpty())

        // Test C++ course detection
        val cppCourses = allLevels.filter { level ->
            val langs = CurriculumUtils.getLanguagesForLevel(level)
            langs.contains("cpp")
        }
        assertTrue("Should detect Level 11 as C++", cppCourses.any { it.levelNumber == 11 })

        // Test SQL / Database course detection
        val sqlCourses = allLevels.filter { level ->
            val langs = CurriculumUtils.getLanguagesForLevel(level)
            langs.contains("sql")
        }
        assertTrue("Should detect Level 8 as SQL", sqlCourses.any { it.levelNumber == 8 })

        // Test HTML course detection
        val htmlCourses = allLevels.filter { level ->
            val langs = CurriculumUtils.getLanguagesForLevel(level)
            langs.contains("html")
        }
        assertTrue("Should detect Level 1 as HTML", htmlCourses.any { it.levelNumber == 1 })
    }

    @Test
    fun testFormatLanguageDisplayName() {
        assertTrue(CurriculumUtils.formatLanguageDisplayName("cpp") == "C++")
        assertTrue(CurriculumUtils.formatLanguageDisplayName("python") == "Python")
        assertTrue(CurriculumUtils.formatLanguageDisplayName("sql") == "SQL")
        assertTrue(CurriculumUtils.formatLanguageDisplayName("javascript") == "JavaScript")
        assertTrue(CurriculumUtils.formatLanguageDisplayName("kotlin") == "Kotlin")
    }
}
