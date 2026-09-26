package com.example.data.catalog

import com.example.model.*

object CurriculumLevelsAdvanced {

    fun getLevels(): List<RoadmapLevel> {
        return listOf(
            level11Cpp,
            level12Python,
            level13Dsa,
            level14SoftwareEng,
            level15Security,
            level16DevOps
        )
    }

    // LEVEL 11: C / C++
    private val level11Cpp = RoadmapLevel(
        id = 11,
        levelNumber = 11,
        title = "C / C++ Systems",
        subtitle = "Pointers, Memory, Stack/Heap & STL",
        description = "Menyelami kinerja perangkat keras: alokasi memori manual, Pointer, Reference, Struct, OOP modern C++, Stack vs Heap, dan Standard Template Library (STL).",
        icon = "cpu-64-bit",
        colorHex = 0xFF6366F1,
        estimatedHours = 32,
        modules = listOf(
            CourseModule(
                id = "mod-11-1",
                levelId = 11,
                title = "Pointer & Manajemen Memori Low-Level",
                description = "Memahami alamat memori (&), dereference (*), memory leak, dan smart pointers.",
                lessons = listOf(
                    Lesson(
                        id = "cpp-01",
                        levelId = 11,
                        moduleId = "mod-11-1",
                        title = "Misteri Pointer & Alamat Memori di C++",
                        durationMinutes = 35,
                        conceptExplanation = """
                            Pointer adalah variabel khusus yang menyimpan ALAMAT MEMORI RAM dari variabel lain, bukan menyimpan nilainya langsung.
                            
                            Dua operator esensial:
                            - Address-of (&): Mengambil alamat memori tempat variabel berada.
                            - Dereference (*): Mengakses atau mengubah nilai yang berada di alamat yang ditunjuk oleh pointer.
                            
                            Memori Komputer terbagi dua:
                            - Stack: Sangat cepat, ukuran terbatas, dibersihkan otomatis saat fungsi selesai.
                            - Heap: Alokasi dinamis manual (malloc/new). Wajib dibebaskan (free/delete) agar tidak terjadi Memory Leak!
                        """.trimIndent(),
                        codeSnippet = """
                            #include <iostream>
                            
                            int main() {
                                int score = 95;
                                int* ptr = &score; // ptr menyimpan alamat score
                                
                                std::cout << "Nilai awal score: " << score << std::endl;
                                std::cout << "Alamat memori: " << ptr << std::endl;
                                
                                // Mengubah nilai lewat dereference pointer:
                                *ptr = 100;
                                std::cout << "Nilai baru score: " << score << std::endl;
                                
                                return 0;
                            }
                        """.trimIndent(),
                        codeLanguage = "cpp",
                        lineByLine = listOf(
                            LineExplanation("int* ptr = &score;", "ptr bertipe pointer-to-int yang menyimpan alamat dari variabel score."),
                            LineExplanation("*ptr = 100;", "Dereference operator * mengakses sel memori score dan mengubah nilainya menjadi 100.")
                        ),
                        practiceChallenge = "Tulis deklarasi pointer ptrScore yang menunjuk ke variabel score.",
                        starterCode = "int score = 42;\nint* ptrScore = &score;",
                        expectedKeywordsOrOutput = "int* ptrScore = &score;",
                        summary = "Pointer memberikan kontrol absolut atas perangkat keras, fondasi sistem operasi dan game engine performa tinggi.",
                        nextLessonId = "py-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-11-1",
                    title = "Kuis C++ Pointers & Memory",
                    levelId = 11,
                    xpReward = 85,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-11-1",
                            question = "Apa yang terjadi jika memori yang dialokasikan di Heap tidak dibebaskan (deallocated) setelah program selesai?",
                            options = listOf(
                                "Memory Leak, RAM akan terus terkonsumsi hingga sistem melambat atau crash",
                                "CPU akan otomatis mati",
                                "Kode di-compile ulang otomatis",
                                "Nilai pointer berubah menjadi 0"
                            ),
                            correctIndex = 0,
                            explanation = "Memory leak terjadi saat memori dialokasikan namun referensinya hilang tanpa pernah dibebaskan.",
                            conceptDoc = "C++ Systems: Heap Allocation & Memory Leaks"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 12: PYTHON
    private val level12Python = RoadmapLevel(
        id = 12,
        levelNumber = 12,
        title = "Python Engineering",
        subtitle = "OOP, Data Structures & Automation",
        description = "Kekuatan Python modern: List Comprehensions, Generator, Decorator, Object-Oriented Programming (OOP), web scraping, dan automasi skrip harian.",
        icon = "language-python",
        colorHex = 0xFFF59E0B,
        estimatedHours = 24,
        modules = listOf(
            CourseModule(
                id = "mod-12-1",
                levelId = 12,
                title = "Pythonic Patterns & Automasi Data",
                description = "Menulis kode Python yang bersih, efisien, idiomatik, dan mudah dibaca.",
                lessons = listOf(
                    Lesson(
                        id = "py-01",
                        levelId = 12,
                        moduleId = "mod-12-1",
                        title = "List Comprehensions & Pythonic OOP",
                        durationMinutes = 25,
                        conceptExplanation = """
                            Python mengutamakan keterbacaan (Zen of Python: 'Readability counts').
                            
                            Fitur Kuat Python:
                            - List Comprehension: Cara ringkas dan cepat untuk membuat list baru dari iterasi yang ada.
                            - OOP Modern: Menggunakan class dengan method khusus __init__ (konstruktor) dan __repr__.
                            - Exception Handling: Menangani error jaringan atau file dengan 'try...except...finally'.
                        """.trimIndent(),
                        codeSnippet = """
                            # Contoh List Comprehension & Class
                            class Student:
                                def __init__(self, name: str, xp: int):
                                    self.name = name
                                    self.xp = xp
                                    
                            students = [
                                Student("Alif", 150),
                                Student("Bella", 320),
                                Student("Citra", 80)
                            ]
                            
                            # Filter siswa dengan XP di atas 100 secara elegan:
                            top_students = [s.name for s in students if s.xp >= 100]
                            print("Top Students:", top_students)
                        """.trimIndent(),
                        codeLanguage = "python",
                        lineByLine = listOf(
                            LineExplanation("def __init__(self, name: str, xp: int):", "Konstruktor inisialisasi atribut objek Student."),
                            LineExplanation("[s.name for s in students if s.xp >= 100]", "List comprehension yang memfilter dan memetakan data dalam satu baris ekspresif.")
                        ),
                        practiceChallenge = "Buat list comprehension untuk menghasilkan kuadrat dari angka 1 hingga 5.",
                        starterCode = "squares = [x**2 for x in range(1, 6)]\nprint(squares)",
                        expectedKeywordsOrOutput = "x**2 for x in range",
                        summary = "Pythonic code memungkinkan kamu menyelesaikan masalah kompleks dengan kode yang singkat dan elegan.",
                        nextLessonId = "dsa-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-12-1",
                    title = "Kuis Python Idioms",
                    levelId = 12,
                    xpReward = 75,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-12-1",
                            question = "Manakah ekspresi list comprehension Python yang benar untuk mengambil bilangan genap dari list numbers?",
                            options = listOf(
                                "[x for x in numbers if x % 2 == 0]",
                                "numbers.filter(x => x % 2 == 0)",
                                "[for x in numbers: x if even]",
                                "select x from numbers where x % 2 = 0"
                            ),
                            correctIndex = 0,
                            explanation = "[expr for item in iterable if condition] adalah sintaks standar Python list comprehension.",
                            conceptDoc = "Python Documentation: List Comprehensions"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 13: DATA STRUCTURE & ALGORITHM
    private val level13Dsa = RoadmapLevel(
        id = 13,
        levelNumber = 13,
        title = "Data Structures & Algorithms",
        subtitle = "Big-O, Trees, Graphs & Dynamic Programming",
        description = "Fondasi problem solving kelas dunia: Analisis kompleksitas Big-O O(1) hingga O(2^n), Linked List, Binary Search Tree, Graph BFS/DFS, Sorting, dan Memoization.",
        icon = "graph",
        colorHex = 0xFFEC4899,
        estimatedHours = 36,
        modules = listOf(
            CourseModule(
                id = "mod-13-1",
                levelId = 13,
                title = "Analisis Kompleksitas & Binary Search",
                description = "Menghitung waktu eksekusi (Time Complexity) dan pemakaian memori (Space Complexity).",
                lessons = listOf(
                    Lesson(
                        id = "dsa-01",
                        levelId = 13,
                        moduleId = "mod-13-1",
                        title = "Big-O Notation & Logarithmic Binary Search",
                        durationMinutes = 35,
                        conceptExplanation = """
                            Big-O mengukur bagaimana waktu eksekusi program bertumbuh seiring membesarnya input data (N).
                            
                            Perbandingan Kompleksitas:
                            - O(1) Constant: Waktu konstan (mengambil array by index).
                            - O(log N) Logarithmic: Membagi masalah jadi dua setiap langkah (Binary Search).
                            - O(N) Linear: Looping satu kali melewati N item (Linear Search).
                            - O(N log N): Sorting efisien (Merge Sort, Quick Sort).
                            - O(N^2) Quadratic: Nested loop bersarang (Bubble Sort).
                            
                            Binary Search pada 1.000.000 data terurut hanya membutuhkan maksimal ~20 perbandingan!
                        """.trimIndent(),
                        codeSnippet = """
                            function binarySearch(arr, target) {
                              let left = 0;
                              let right = arr.length - 1;
                              
                              while (left <= right) {
                                const mid = Math.floor((left + right) / 2);
                                if (arr[mid] === target) return mid; // Ketemu!
                                
                                if (arr[mid] < target) {
                                  left = mid + 1; // Cari di separuh kanan
                                } else {
                                  right = mid - 1; // Cari di separuh kiri
                                }
                              }
                              return -1; // Target tidak ada
                            }
                            
                            const sortedData = [2, 5, 8, 12, 16, 23, 38, 56, 72, 91];
                            console.log("Index:", binarySearch(sortedData, 23));
                        """.trimIndent(),
                        codeLanguage = "javascript",
                        lineByLine = listOf(
                            LineExplanation("let mid = Math.floor((left + right) / 2);", "Menentukan titik tengah array."),
                            LineExplanation("if (arr[mid] < target) left = mid + 1;", "Mengabaikan seluruh separuh kiri karena nilainya pasti lebih kecil.")
                        ),
                        practiceChallenge = "Berapa banyak langkah maksimal binary search pada 1024 data terurut? Tulis jawabannya dalam console.",
                        starterCode = "// 2^10 = 1024, maka log2(1024) = 10\nconsole.log(10);",
                        expectedKeywordsOrOutput = "10",
                        summary = "Algoritma yang efisien mengubah program dari macet berjam-jam menjadi selesai dalam milidetik.",
                        nextLessonId = "swe-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-13-1",
                    title = "Kuis Big-O & Searching Algorithms",
                    levelId = 13,
                    xpReward = 90,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-13-1",
                            question = "Berapa Time Complexity terburuk (Worst Case) dari algoritma Binary Search?",
                            options = listOf("O(1)", "O(log N)", "O(N)", "O(N^2)"),
                            correctIndex = 1,
                            explanation = "Binary Search selalu membagi ruang pencarian menjadi setengah pada setiap langkah, menghasilkan kompleksitas O(log N).",
                            conceptDoc = "Algorithm Analysis: Binary Search Complexity"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 14: SOFTWARE ENGINEERING
    private val level14SoftwareEng = RoadmapLevel(
        id = 14,
        levelNumber = 14,
        title = "Software Engineering",
        subtitle = "Clean Code, SOLID & Clean Architecture",
        description = "Menjadi Software Engineer profesional: 5 Prinsip SOLID, Clean Architecture, Refactoring, Unit & Integration Testing, Code Review, dan CI/CD automation.",
        icon = "shield-check",
        colorHex = 0xFF14B8A6,
        estimatedHours = 28,
        modules = listOf(
            CourseModule(
                id = "mod-14-1",
                levelId = 14,
                title = "Prinsip SOLID & Clean Architecture",
                description = "Menulis kode yang mudah dirawat, mudah dites, dan fleksibel menghadapi perubahan bisnis.",
                lessons = listOf(
                    Lesson(
                        id = "swe-01",
                        levelId = 14,
                        moduleId = "mod-14-1",
                        title = "Menguasai 5 Prinsip Desain SOLID",
                        durationMinutes = 30,
                        conceptExplanation = """
                            SOLID adalah akronim 5 prinsip desain berorientasi objek yang dirumuskan Robert C. Martin:
                            - S (Single Responsibility): Sebuah class hanya boleh memiliki satu alasan untuk berubah.
                            - O (Open/Closed): Terbuka untuk ekstensi (fitur baru), namun tertutup untuk modifikasi kode lama.
                            - L (Liskov Substitution): Subtipe harus dapat menggantikan tipe induknya tanpa merusak fungsionalitas.
                            - I (Interface Segregation): Klien tidak boleh dipaksa bergantung pada method yang tidak digunakannya.
                            - D (Dependency Inversion): Modul tingkat tinggi tidak boleh bergantung pada modul tingkat rendah; keduanya harus bergantung pada abstraksi.
                        """.trimIndent(),
                        codeSnippet = """
                            // Contoh Dependency Inversion Principle (DIP):
                            interface NotificationService {
                                fun send(recipient: String, message: String)
                            }
                            
                            class EmailNotification : NotificationService {
                                override fun send(recipient: String, message: String) {
                                    println("Mengirim email ke ${'$'}recipient: ${'$'}message")
                                }
                            }
                            
                            // High-level class bergantung pada abstraksi, bukan implementasi konkret!
                            class CourseEnrollment(private val notifier: NotificationService) {
                                fun enroll(studentEmail: String, courseName: String) {
                                    // Logika pendaftaran...
                                    notifier.send(studentEmail, "Selamat datang di ${'$'}courseName")
                                }
                            }
                        """.trimIndent(),
                        codeLanguage = "kotlin",
                        lineByLine = listOf(
                            LineExplanation("interface NotificationService", "Abstraksi murni yang memutuskan keterikatan erat (loose coupling)."),
                            LineExplanation("class CourseEnrollment(private val notifier: NotificationService)", "Menerapkan Dependency Injection; mudah diuji dengan mock/fake service.")
                        ),
                        practiceChallenge = "Implementasikan interface NotificationService dengan class SmsNotification.",
                        starterCode = "class SmsNotification : NotificationService {\n    override fun send(recipient: String, message: String) {\n        println(\"SMS sent\")\n    }\n}",
                        expectedKeywordsOrOutput = "override fun send",
                        summary = "SOLID mencegah timbulnya Technical Debt dan membuat codebase siap diskalakan ke jutaan baris kode.",
                        nextLessonId = "sec-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-14-1",
                    title = "Kuis SOLID Principles",
                    levelId = 14,
                    xpReward = 85,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-14-1",
                            question = "Prinsip SOLID mana yang menyatakan sebuah class harus memiliki satu dan hanya satu tanggung jawab utama?",
                            options = listOf(
                                "Single Responsibility Principle (SRP)",
                                "Open/Closed Principle (OCP)",
                                "Liskov Substitution Principle (LSP)",
                                "Interface Segregation Principle (ISP)"
                            ),
                            correctIndex = 0,
                            explanation = "SRP menegaskan bahwa setiap class atau modul harus fokus menangani satu bagian logika aplikasi saja.",
                            conceptDoc = "Clean Architecture: Single Responsibility Principle"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 15: CYBERSECURITY FUNDAMENTALS
    private val level15Security = RoadmapLevel(
        id = 15,
        levelNumber = 15,
        title = "Cybersecurity Defensive",
        subtitle = "OWASP Top 10, Cryptography & Secure Coding",
        description = "Keamanan aplikasi dari perspektif defensif: OWASP Top 10, sanitasi input, mitigasi SQL Injection, XSS, CSRF, enkripsi AES/RSA, password hashing Argon2/Bcrypt, dan secure token storage.",
        icon = "security",
        colorHex = 0xFFEF4444,
        estimatedHours = 26,
        modules = listOf(
            CourseModule(
                id = "mod-15-1",
                levelId = 15,
                title = "Mitigasi OWASP Top 10 & Sanitasi Data",
                description = "Mencegah celah injeksi database, cross-site scripting, dan kebocoran credential.",
                lessons = listOf(
                    Lesson(
                        id = "sec-01",
                        levelId = 15,
                        moduleId = "mod-15-1",
                        title = "Pertahanan Terhadap SQL Injection & XSS",
                        durationMinutes = 30,
                        conceptExplanation = """
                            Keamanan bukan fitur tambahan, melainkan pola pikir sejak baris kode pertama ditulis.
                            
                            1. SQL Injection (SQLi):
                            Terjadi saat input pengguna digabungkan langsung (string concatenation) ke dalam query database.
                            Solusi Mutlak: Selalu gunakan Parameterized Queries (Prepared Statements) atau ORM yang aman.
                            
                            2. Cross-Site Scripting (XSS):
                            Terjadi saat script JavaScript berbahaya milik penyerang berhasil diinjeksikan ke browser pengguna lain.
                            Solusi: Sanitasi semua input dan encode data sebelum dirender ke HTML. Terapkan header Content-Security-Policy (CSP).
                        """.trimIndent(),
                        codeSnippet = """
                            // ❌ SANGAT BERBAHAYA (Rentan SQL Injection):
                            // const query = "SELECT * FROM users WHERE email = '" + userInput + "'";
                            
                            // ✅ CARA AMAN (Parameterized Query / Prepared Statement):
                            const secureQuery = "SELECT id, username, email FROM users WHERE email = ?";
                            db.execute(secureQuery, [userInput], (err, results) => {
                              if (err) throw err;
                              console.log("Data aman:", results);
                            });
                        """.trimIndent(),
                        codeLanguage = "javascript",
                        lineByLine = listOf(
                            LineExplanation("const secureQuery = \"... WHERE email = ?\";", "Tanda tanya ? adalah placeholder parameter yang diperlakukan murni sebagai teks data oleh database engine, bukan perintah eksekusi."),
                            LineExplanation("db.execute(secureQuery, [userInput], ...)", "Database driver melakukan escape otomatis terhadap karakter berbahaya.")
                        ),
                        practiceChallenge = "Tulis contoh query SQL yang menggunakan parameter placeholder ? agar aman dari SQL Injection.",
                        starterCode = "const query = \"SELECT * FROM accounts WHERE user_id = ?\";",
                        expectedKeywordsOrOutput = "user_id = ?",
                        summary = "Never Trust User Input! Selalu sanitasi dan gunakan parameterized queries di seluruh endpoint backend.",
                        nextLessonId = "devops-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-15-1",
                    title = "Kuis Defensive Security & OWASP",
                    levelId = 15,
                    xpReward = 95,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-15-1",
                            question = "Cara paling efektif untuk mencegah serangan SQL Injection di backend adalah?",
                            options = listOf(
                                "Menggunakan Prepared Statements / Parameterized Queries",
                                "Memblokir semua huruf kapital pada input",
                                "Mengganti database ke format txt",
                                "Memasang antivirus di laptop pengguna"
                            ),
                            correctIndex = 0,
                            explanation = "Prepared statements memisahkan instruksi SQL dari data masukan pengguna, sehingga karakter kutip atau instruksi SQL penyerang tidak pernah dieksekusi.",
                            conceptDoc = "OWASP Secure Coding: SQL Injection Defense"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 16: DEVOPS & DEPLOYMENT
    private val level16DevOps = RoadmapLevel(
        id = 16,
        levelNumber = 16,
        title = "DevOps & Cloud",
        subtitle = "Linux, Docker, CI/CD & Production",
        description = "Membawa aplikasi ke dunia nyata: Perintah Linux esensial, containerisasi Docker, Reverse Proxy Nginx, SSL/TLS Let's Encrypt, CI/CD pipeline automation, dan cloud monitoring.",
        icon = "cloud-check",
        colorHex = 0xFF0284C7,
        estimatedHours = 28,
        modules = listOf(
            CourseModule(
                id = "mod-16-1",
                levelId = 16,
                title = "Containerisasi Docker & CI/CD",
                description = "Membuat Dockerfile, multi-stage build, docker-compose, dan pipeline otomatis.",
                lessons = listOf(
                    Lesson(
                        id = "devops-01",
                        levelId = 16,
                        moduleId = "mod-16-1",
                        title = "Docker Containerization & Production Deployment",
                        durationMinutes = 30,
                        conceptExplanation = """
                            'It works on my machine!' Masalah klasik dipecahkan oleh Docker dengan mengemas aplikasi beserta seluruh dependensinya ke dalam Container yang terisolasi dan konsisten di lingkungan apapun.
                            
                            Konsep Kunci Docker:
                            - Dockerfile: Resep cetak biru instruksi pembangunan image.
                            - Image: Snapshot executable read-only aplikasi.
                            - Container: Instansiasi aktif yang berjalan dari suatu image.
                            - CI/CD: Pipeline otomatis (GitHub Actions) yang menjalankan unit tests dan men-deploy ke server saat branch main di-merge.
                        """.trimIndent(),
                        codeSnippet = """
                            # Production Dockerfile Multi-Stage Build:
                            FROM node:20-alpine AS builder
                            WORKDIR /app
                            COPY package*.json ./
                            RUN npm ci
                            COPY . .
                            RUN npm run build
                            
                            # Tahap Runner yang sangat ringan:
                            FROM node:20-alpine AS runner
                            WORKDIR /app
                            COPY --from=builder /app/dist ./dist
                            COPY --from=builder /app/node_modules ./node_modules
                            
                            EXPOSE 3000
                            CMD ["node", "dist/main.js"]
                        """.trimIndent(),
                        codeLanguage = "dockerfile",
                        lineByLine = listOf(
                            LineExplanation("FROM node:20-alpine AS builder", "Menggunakan base image alpine minimalis untuk menghemat ukuran."),
                            LineExplanation("COPY --from=builder ...", "Multi-stage build hanya menyalin hasil build produksi, membuang file dev yang tidak diperlukan.")
                        ),
                        practiceChallenge = "Tulis instruksi Dockerfile untuk mengekspos port 8080 pada container.",
                        starterCode = "EXPOSE 8080",
                        expectedKeywordsOrOutput = "EXPOSE 8080",
                        summary = "Containerisasi dan CI/CD menjamin deployment aplikasi stabil, cepat, dan zero-downtime.",
                        nextLessonId = null
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-16-1",
                    title = "Kuis DevOps & Docker",
                    levelId = 16,
                    xpReward = 100,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-16-1",
                            question = "Apa manfaat utama teknik Multi-Stage Build dalam Dockerfile?",
                            options = listOf(
                                "Menghasilkan ukuran Docker Image akhir yang jauh lebih kecil dan aman",
                                "Membuat website terbuka untuk umum tanpa domain",
                                "Mengubah kode Python menjadi C++ otomatis",
                                "Menghapus kebutuhan RAM di server"
                            ),
                            correctIndex = 0,
                            explanation = "Multi-stage build memisahkan tahap kompilasi dan runtime sehingga artefak build development tidak ikut terbawa ke image produksi.",
                            conceptDoc = "Docker Best Practices: Multi-stage Builds"
                        )
                    )
                )
            )
        )
    )

    // Daily Challenges Catalog
    val dailyChallenges = listOf(
        DailyChallenge(
            id = "daily-1",
            title = "Palindrom Checker",
            prompt = "Periksa apakah kata 'kasurrusak' merupakan kata palindrom (dibaca sama dari depan maupun belakang).",
            starterCode = """
                function isPalindrome(str) {
                  const reversed = str.split('').reverse().join('');
                  return str === reversed;
                }
                console.log(isPalindrome("kasurrusak"));
            """.trimIndent(),
            language = "javascript",
            expectedOutput = "true",
            xpReward = 40
        ),
        DailyChallenge(
            id = "daily-2",
            title = "Two Sum Challenge",
            prompt = "Temukan dua angka dalam array [2, 7, 11, 15] yang jumlahnya sama dengan 9.",
            starterCode = """
                function twoSum(nums, target) {
                  const map = new Map();
                  for (let i = 0; i < nums.length; i++) {
                    const complement = target - nums[i];
                    if (map.has(complement)) return [map.get(complement), i];
                    map.set(nums[i], i);
                  }
                  return [];
                }
                console.log(twoSum([2, 7, 11, 15], 9));
            """.trimIndent(),
            language = "javascript",
            expectedOutput = "[0, 1]",
            xpReward = 50
        )
    )

    // Badges & Achievements
    val achievementsList = listOf(
        AchievementItem("first_step", "Langkah Pertama", "Menyelesaikan lesson pertama di CodeNest", 20, "🚀"),
        AchievementItem("curious_mind", "Pikiran Kritis", "Menyelesaikan quiz pertama dengan skor sempurna", 30, "🧠"),
        AchievementItem("code_runner", "Code Practitioner", "Menjalankan kode di playground lebih dari 5 kali", 40, "⚡"),
        AchievementItem("streak_champion", "Konsistensi Baja", "Menjaga streak belajar selama 3 hari berturut-turut", 60, "🔥"),
        AchievementItem("project_architect", "Software Builder", "Menyelesaikan submission project pertama", 100, "🏗️"),
        AchievementItem("cyber_guardian", "Cyber Guardian", "Menyelesaikan modul Cybersecurity Pertahanan", 150, "🛡️")
    )
}
