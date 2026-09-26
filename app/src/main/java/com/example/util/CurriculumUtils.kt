package com.example.util

import com.example.model.RoadmapLevel

/**
 * Utility functions for curriculum data inspection and language formatting.
 */
object CurriculumUtils {

    fun getLanguagesForLevel(level: RoadmapLevel): List<String> {
        val detected = level.modules.flatMap { it.lessons }
            .map { it.codeLanguage.lowercase() }
            .distinct()
            .toMutableList()

        val titleLower = "${level.title} ${level.subtitle}".lowercase()
        if (titleLower.contains("html") && !detected.contains("html")) detected.add("html")
        if (titleLower.contains("css") && !detected.contains("css")) detected.add("css")
        if ((titleLower.contains("javascript") || titleLower.contains("js")) && !detected.contains("javascript")) detected.add("javascript")
        if ((titleLower.contains("typescript") || titleLower.contains("ts")) && !detected.contains("typescript")) detected.add("typescript")
        if (titleLower.contains("python") && !detected.contains("python")) detected.add("python")
        if ((titleLower.contains("c++") || titleLower.contains("cpp")) && !detected.contains("cpp")) detected.add("cpp")
        if ((titleLower.contains("database") || titleLower.contains("sql")) && !detected.contains("sql")) detected.add("sql")
        if ((titleLower.contains("mobile") || titleLower.contains("kotlin") || titleLower.contains("android")) && !detected.contains("kotlin")) detected.add("kotlin")
        if ((titleLower.contains("devops") || titleLower.contains("docker")) && !detected.contains("dockerfile")) detected.add("dockerfile")
        if (titleLower.contains("git") && !detected.contains("bash")) detected.add("bash")

        return detected
    }

    fun formatLanguageDisplayName(langCode: String): String = when (langCode.lowercase()) {
        "cpp" -> "C++"
        "python" -> "Python"
        "javascript" -> "JavaScript"
        "typescript" -> "TypeScript"
        "kotlin" -> "Kotlin"
        "sql" -> "SQL"
        "html" -> "HTML"
        "css" -> "CSS"
        "bash" -> "Bash/CLI"
        "dockerfile" -> "Docker"
        else -> langCode.replaceFirstChar { it.uppercase() }
    }
}
