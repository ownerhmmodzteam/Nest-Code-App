package com.example.data.catalog

import com.example.model.*

object CurriculumLevelsMid {

    fun getLevels(): List<RoadmapLevel> {
        return listOf(
            level6Frontend,
            level7Backend,
            level8Database,
            level9FullStack,
            level10Mobile
        )
    }

    // LEVEL 6: FRONTEND DEVELOPMENT
    private val level6Frontend = RoadmapLevel(
        id = 6,
        levelNumber = 6,
        title = "Frontend Frameworks",
        subtitle = "React, State Management & Modern UI",
        description = "Membangun SPA (Single Page Applications) berskala besar dengan React, Component-Driven Architecture, Hooks (useState, useEffect, useMemo), dan State Management.",
        icon = "react",
        colorHex = 0xFF61DAFB,
        estimatedHours = 28,
        modules = listOf(
            CourseModule(
                id = "mod-6-1",
                levelId = 6,
                title = "React Core, Hooks & Component Lifecycle",
                description = "Declarative UI, Virtual DOM, unidirectional data flow, dan hooks interaktif.",
                lessons = listOf(
                    Lesson(
                        id = "react-01",
                        levelId = 6,
                        moduleId = "mod-6-1",
                        title = "State, Props, dan Efek Samping di React",
                        durationMinutes = 30,
                        conceptExplanation = """
                            React menggunakan paradigma Declarative UI: tampilan antarmuka adalah refleksi murni dari State saat itu.
                            
                            Dua konsep fundamental:
                            1. State (useState): Data lokal komponen yang jika nilainya berubah, React otomatis me-render ulang UI.
                            2. Props: Parameter yang dikirim dari parent component ke child component (read-only).
                            3. Side Effects (useEffect): Menangani interaksi dengan dunia luar (seperti API fetch, timer, atau manipulasi DOM langsung).
                        """.trimIndent(),
                        codeSnippet = """
                            import React, { useState, useEffect } from 'react';
                            
                            export function CodeCounter() {
                              const [count, setCount] = useState(0);
                              
                              useEffect(() => {
                                console.log(`Tombol telah ditekan ${'$'}{count} kali`);
                              }, [count]);
                              
                              return (
                                <div className="p-4 bg-slate-900 rounded-lg text-white">
                                  <h2 className="text-xl font-bold">Latihan Coding: {count}</h2>
                                  <button 
                                    onClick={() => setCount(prev => prev + 1)}
                                    className="mt-2 px-4 py-2 bg-cyan-500 rounded font-semibold">
                                    Tambah Latihan
                                  </button>
                                </div>
                              );
                            }
                        """.trimIndent(),
                        codeLanguage = "typescript",
                        lineByLine = listOf(
                            LineExplanation("const [count, setCount] = useState(0);", "Inisialisasi state reaktif dengan nilai awal 0."),
                            LineExplanation("useEffect(() => {...}, [count]);", "Efek samping yang berjalan setiap kali variable count diperbarui."),
                            LineExplanation("onClick={() => setCount(...)}", "Event handler yang memicu re-render UI secara otomatis.")
                        ),
                        practiceChallenge = "Tulis handler untuk mereset counter kembali ke angka 0 saat tombol reset ditekan.",
                        starterCode = "function resetCounter() {\n  setCount(0);\n}",
                        expectedKeywordsOrOutput = "setCount(0)",
                        summary = "React membuat pembuatan UI kompleks menjadi modular, terprediksi, dan mudah di-maintain.",
                        nextLessonId = "backend-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-6-1",
                    title = "Kuis React Architecture",
                    levelId = 6,
                    xpReward = 70,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-6-1",
                            question = "Kapan callback di dalam useEffect(fn, []) dengan dependency array kosong akan dieksekusi?",
                            options = listOf(
                                "Hanya satu kali saat komponen pertama kali di-mount ke layar",
                                "Setiap kali ada state apapun yang berubah",
                                "Setiap milidetik",
                                "Hanya saat komponen di-unmount"
                            ),
                            correctIndex = 0,
                            explanation = "Dependency array kosong [] memberitahu React bahwa effect ini hanya dijalankan satu kali saat mount.",
                            conceptDoc = "React Hooks: useEffect dependency guide"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 7: BACKEND DEVELOPMENT
    private val level7Backend = RoadmapLevel(
        id = 7,
        levelNumber = 7,
        title = "Backend & REST API",
        subtitle = "Node.js, Express, FastAPI & API Security",
        description = "Membangun sistem server yang handal: RESTful API, HTTP routing, middleware validasi, otentikasi JWT, password hashing bcrypt, dan rate-limiting.",
        icon = "server",
        colorHex = 0xFF10B981,
        estimatedHours = 26,
        modules = listOf(
            CourseModule(
                id = "mod-7-1",
                levelId = 7,
                title = "REST API Architecture & Authentication",
                description = "Mendesain endpoint RESTful, status code, middleware, dan pengamanan endpoint.",
                lessons = listOf(
                    Lesson(
                        id = "backend-01",
                        levelId = 7,
                        moduleId = "mod-7-1",
                        title = "Membangun Secure REST API dengan Express",
                        durationMinutes = 30,
                        conceptExplanation = """
                            Backend bertanggung jawab atas logika bisnis, validasi keamanan, dan komunikasi database.
                            
                            Prinsip REST API:
                            - GET /api/courses: Mengambil daftar resource
                            - POST /api/courses: Membuat resource baru
                            - PUT/PATCH /api/courses/:id: Memperbarui resource
                            - DELETE /api/courses/:id: Menghapus resource
                            
                            Keamanan Backend Wajib:
                            - Jangan pernah simpan password plaintext (gunakan bcrypt hashing dengan salt).
                            - Gunakan JWT (JSON Web Tokens) bertanda tangan kriptografis untuk sesi login.
                        """.trimIndent(),
                        codeSnippet = """
                            const express = require('express');
                            const app = express();
                            
                            app.use(express.json()); // Middleware parsing body
                            
                            // Endpoint GET dengan filter
                            app.get('/api/v1/lessons', (req, res) => {
                              const lessons = [
                                { id: 1, title: 'HTML Basics', level: 'Beginner' },
                                { id: 2, title: 'CSS Flexbox', level: 'Beginner' }
                              ];
                              res.status(200).json({ success: true, count: lessons.length, data: lessons });
                            });
                            
                            app.listen(3000, () => console.log('Server berjalan di port 3000'));
                        """.trimIndent(),
                        codeLanguage = "javascript",
                        lineByLine = listOf(
                            LineExplanation("app.use(express.json());", "Middleware untuk mem-parsing payload JSON request yang masuk."),
                            LineExplanation("res.status(200).json(...)", "Mengirim response HTTP standar status 200 OK dengan payload terstruktur.")
                        ),
                        practiceChallenge = "Tambahkan status code 404 jika resource pelajaran tidak ditemukan oleh server.",
                        starterCode = "if (!lesson) {\n  res.status(404).json({ error: 'Lesson not found' });\n}",
                        expectedKeywordsOrOutput = "res.status(404)",
                        summary = "Backend yang solid memvalidasi setiap input klien dan mengembalikan status code HTTP yang akurat.",
                        nextLessonId = "db-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-7-1",
                    title = "Kuis REST API & Backend Security",
                    levelId = 7,
                    xpReward = 75,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-7-1",
                            question = "Status code HTTP manakah yang paling tepat untuk menandakan request tidak memiliki token otentikasi yang valid?",
                            options = listOf("200 OK", "401 Unauthorized", "404 Not Found", "500 Internal Error"),
                            correctIndex = 1,
                            explanation = "HTTP 401 Unauthorized secara eksplisit menandakan request membutuhkan kredensial autentikasi yang valid.",
                            conceptDoc = "HTTP Status Code Standards"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 8: DATABASE
    private val level8Database = RoadmapLevel(
        id = 8,
        levelNumber = 8,
        title = "Databases & SQL",
        subtitle = "Relational Modeling, PostgreSQL & Indexing",
        description = "Desain skema database yang tangguh: Relational SQL (PostgreSQL, MySQL, SQLite), Primary & Foreign Keys, Normalisasi 3NF, Complex JOINs, Transaksi ACID, dan Indexing.",
        icon = "database",
        colorHex = 0xFF8B5CF6,
        estimatedHours = 22,
        modules = listOf(
            CourseModule(
                id = "mod-8-1",
                levelId = 8,
                title = "Relational Modeling & Query Optimization",
                description = "Membuat tabel, relasi one-to-many, join multi-tabel, dan indexing performa tinggi.",
                lessons = listOf(
                    Lesson(
                        id = "db-01",
                        levelId = 8,
                        moduleId = "mod-8-1",
                        title = "Relasi Tabel & Complex SQL JOINs",
                        durationMinutes = 25,
                        conceptExplanation = """
                            Database relasional (RDBMS) menyimpan entitas dalam tabel yang terhubung melalui Foreign Key (kunci asing).
                            
                            Jenis JOIN Utama:
                            - INNER JOIN: Hanya mengambil baris yang memiliki kecocokan di kedua tabel.
                            - LEFT JOIN: Mengambil semua data dari tabel kiri beserta data yang cocok di tabel kanan.
                            
                            Transaksi ACID menjamin bahwa operasi finansial atau transfer saldo berlangsung sukses total atau dibatalkan sepenuhnya (Rollback) jika ada eror.
                        """.trimIndent(),
                        codeSnippet = """
                            -- Membuat Relasi Pelajaran dan Hasil Kuis
                            SELECT 
                              users.username,
                              lessons.title AS lesson_title,
                              quiz_scores.score,
                              quiz_scores.completed_at
                            FROM users
                            INNER JOIN quiz_scores ON users.id = quiz_scores.user_id
                            INNER JOIN lessons ON quiz_scores.lesson_id = lessons.id
                            WHERE quiz_scores.score >= 80
                            ORDER BY quiz_scores.score DESC;
                        """.trimIndent(),
                        codeLanguage = "sql",
                        lineByLine = listOf(
                            LineExplanation("INNER JOIN quiz_scores ON users.id = ...", "Menggabungkan tabel pengguna dengan rekam jejak nilai kuis."),
                            LineExplanation("WHERE quiz_scores.score >= 80", "Menyaring hanya pengguna yang lulus dengan nilai memuaskan."),
                            LineExplanation("ORDER BY quiz_scores.score DESC", "Mengurutkan skor tertinggi di posisi paling atas.")
                        ),
                        practiceChallenge = "Tulis klausa WHERE SQL untuk memfilter pengguna yang memiliki skor minimal 70.",
                        starterCode = "SELECT * FROM quiz_scores WHERE score >= 70;",
                        expectedKeywordsOrOutput = "WHERE score >=",
                        summary = "Relasi yang ternormalisasi mencegah redundansi data dan menjaga integritas referensial.",
                        nextLessonId = "fullstack-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-8-1",
                    title = "Kuis Database & ACID Transactions",
                    levelId = 8,
                    xpReward = 65,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-8-1",
                            question = "Huruf 'A' pada prinsip database ACID merupakan singkatan dari apa?",
                            options = listOf("Accuracy", "Atomicity", "Availability", "Authentication"),
                            correctIndex = 1,
                            explanation = "Atomicity memastikan bahwa seluruh instruksi dalam satu transaksi berhasil dieksekusi bersama, atau tidak sama sekali.",
                            conceptDoc = "RDBMS: ACID Guarantees"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 9: FULL STACK DEVELOPMENT
    private val level9FullStack = RoadmapLevel(
        id = 9,
        levelNumber = 9,
        title = "Full Stack Integration",
        subtitle = "End-to-End System, Auth & Deployment",
        description = "Menggabungkan seluruh komponen: Frontend React, Backend REST API, Database PostgreSQL, Secure Auth, CORS, dan Continuous Deployment ke Cloud.",
        icon = "layers",
        colorHex = 0xFFA855F7,
        estimatedHours = 32,
        modules = listOf(
            CourseModule(
                id = "mod-9-1",
                levelId = 9,
                title = "Arsitektur Full Stack & Data Flow",
                description = "Menghubungkan Client ke Server dengan aman, menangani State global, dan validasi data ganda.",
                lessons = listOf(
                    Lesson(
                        id = "fullstack-01",
                        levelId = 9,
                        moduleId = "mod-9-1",
                        title = "Arsitektur End-to-End Full Stack Web",
                        durationMinutes = 35,
                        conceptExplanation = """
                            Pada aplikasi Full Stack profesional, data mengalir melalui beberapa lapisan keamanan:
                            1. Client Form (Validasi UI awal untuk UX cepat)
                            2. Transport Layer (Enkripsi HTTPS)
                            3. API Gateway / Middleware (CORS check, Rate Limiting, JWT validation)
                            4. Controller & Service Layer (Validasi bisnis backend ketat)
                            5. Database Layer (Prepared Statements / ORM untuk mencegah SQL Injection)
                        """.trimIndent(),
                        codeSnippet = """
                            // Controller Backend yang melayani Frontend:
                            export async function handleCompleteLesson(req, res) {
                              const userId = req.user.id; // Didapat dari decoded JWT token
                              const { lessonId, xpEarned } = req.body;
                              
                              const updated = await db.user.update({
                                where: { id: userId },
                                data: {
                                  xp: { increment: xpEarned },
                                  completedLessons: { push: lessonId }
                                }
                              });
                              
                              return res.json({ success: true, newXp: updated.xp });
                            }
                        """.trimIndent(),
                        codeLanguage = "typescript",
                        lineByLine = listOf(
                            LineExplanation("const userId = req.user.id;", "Mengidentifikasi user terotentikasi dari token terenkripsi, bukan dari input body mentah."),
                            LineExplanation("xp: { increment: xpEarned }", "Atomic update di level database untuk mencegah race condition.")
                        ),
                        practiceChallenge = "Tulis struktur JSON respons yang mengembalikan status sukses dan xp terbaru.",
                        starterCode = "return res.json({ success: true, xp: 250 });",
                        expectedKeywordsOrOutput = "success: true",
                        summary = "Full stack developer memahami gambaran besar bagaimana kode di browser berinteraksi harmonis dengan database di cloud.",
                        nextLessonId = "mobile-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-9-1",
                    title = "Kuis Full Stack Architecture",
                    levelId = 9,
                    xpReward = 80,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-9-1",
                            question = "Mengapa validasi form di sisi Client saja TIDAK PERNAH cukup untuk keamanan aplikasi?",
                            options = listOf(
                                "Karena browser tidak mendukung regex",
                                "Karena penyerang dapat mem-bypass browser dan menembak langsung ke API menggunakan tools seperti Curl atau Postman",
                                "Karena JavaScript terlalu lambat",
                                "Karena server tidak bisa membaca JSON"
                            ),
                            correctIndex = 1,
                            explanation = "Client-side validation hanya untuk kenyamanan pengguna; Server-side validation adalah pertahanan keamanan sejati yang mutlak wajib.",
                            conceptDoc = "Full Stack Defense: Never Trust the Client"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 10: MOBILE DEVELOPMENT
    private val level10Mobile = RoadmapLevel(
        id = 10,
        levelNumber = 10,
        title = "Mobile Engineering",
        subtitle = "Android, Kotlin & Jetpack Compose",
        description = "Pengembangan aplikasi mobile native modern: Lifecycle Android, Jetpack Compose Declarative UI, Coroutines & Flow, Room Database lokal, MVVM, dan Material Design 3.",
        icon = "cellphone",
        colorHex = 0xFF22C55E,
        estimatedHours = 30,
        modules = listOf(
            CourseModule(
                id = "mod-10-1",
                levelId = 10,
                title = "Jetpack Compose & Reactive Mobile State",
                description = "Declarative UI di Android dengan Kotlin, State Hoisting, Recomposition, dan Room DB.",
                lessons = listOf(
                    Lesson(
                        id = "mobile-01",
                        levelId = 10,
                        moduleId = "mod-10-1",
                        title = "Dasar Jetpack Compose & State Management",
                        durationMinutes = 30,
                        conceptExplanation = """
                            Jetpack Compose merevolusi pengembangan UI di Android dengan pendekatan Kotlin deklaratif murni tanpa XML.
                            
                            Konsep Kunci:
                            - @Composable: Anotasi yang mengubah fungsi Kotlin biasa menjadi elemen UI visual.
                            - remember & mutableStateOf: Menyimpan state lokal yang memicu Recomposition saat nilainya diperbarui.
                            - ViewModel & StateFlow: Memisahkan state bisnis dari siklus hidup Activity agar aman saat layar diputar (Configuration Changes).
                        """.trimIndent(),
                        codeSnippet = """
                            @Composable
                            fun LessonCard(
                                title: String, 
                                xpReward: Int, 
                                isCompleted: Boolean,
                                onStartClick: () -> Unit
                            ) {
                                Card(
                                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = title, style = MaterialTheme.typography.titleMedium)
                                            Text(text = "+${'$'}xpReward XP", color = MaterialTheme.colorScheme.primary)
                                        }
                                        Button(onClick = onStartClick) {
                                            Text(if (isCompleted) "Review" else "Mulai")
                                        }
                                    }
                                }
                            }
                        """.trimIndent(),
                        codeLanguage = "kotlin",
                        lineByLine = listOf(
                            LineExplanation("@Composable fun LessonCard(...)", "Mendefinisikan komponen UI deklaratif yang reusable."),
                            LineExplanation("Button(onClick = onStartClick)", "Menerapkan State Hoisting: event klik diteruskan ke caller."),
                            LineExplanation("colors = CardDefaults.cardColors(...)", "Menggunakan token warna Material Theme 3 adaptif.")
                        ),
                        practiceChallenge = "Tulis Composable sederhana yang menampilkan nama app 'CodeNest' di dalam Text.",
                        starterCode = "@Composable\nfun AppHeader() {\n    Text(text = \"CodeNest\")\n}",
                        expectedKeywordsOrOutput = "Text(text = \"CodeNest\")",
                        summary = "Compose memangkas boilerplate code hingga 60% dibanding sistem View XML lawas.",
                        nextLessonId = "cpp-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-10-1",
                    title = "Kuis Android Jetpack Compose",
                    levelId = 10,
                    xpReward = 80,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-10-1",
                            question = "Mengapa logika bisnis dan state data disarankan diletakkan di ViewModel dibanding langsung di dalam Composable?",
                            options = listOf(
                                "Agar UI tidak perlu di-compile",
                                "Karena ViewModel bertahan melewati configuration changes (seperti rotasi layar) dan mudah di-unit test",
                                "Agar warna tombol berubah otomatis",
                                "Karena Room Database hanya bisa diakses dari XML"
                            ),
                            correctIndex = 1,
                            explanation = "ViewModel didesain untuk bertahan hidup melewati perubahan konfigurasi Android dan memisahkan UI dari Business Logic (MVVM).",
                            conceptDoc = "Android Architecture Guide: ViewModel & UI State"
                        )
                    )
                )
            )
        )
    )
}
