package com.example.data.catalog

import com.example.model.*

object CurriculumCatalog {

    fun getAllLevels(): List<RoadmapLevel> {
        return listOf(
            level0Fundamentals,
            level1Html,
            level2Css,
            level3Js,
            level4Git,
            level5TypeScript
        ) + CurriculumLevelsMid.getLevels() + CurriculumLevelsAdvanced.getLevels()
    }

    fun findLessonById(lessonId: String): Lesson? {
        for (level in getAllLevels()) {
            for (module in level.modules) {
                val found = module.lessons.find { it.id == lessonId }
                if (found != null) return found
            }
        }
        return null
    }

    fun findLevelById(levelId: Int): RoadmapLevel? {
        return getAllLevels().find { it.id == levelId }
    }

    fun findQuizById(quizId: String): QuizDefinition? {
        for (level in getAllLevels()) {
            for (module in level.modules) {
                if (module.quiz?.id == quizId) return module.quiz
            }
        }
        return null
    }

    fun findProjectById(projectId: String): ProjectDefinition? {
        for (level in getAllLevels()) {
            if (level.project?.id == projectId) return level.project
        }
        return null
    }

    // LEVEL 0: COMPUTER & PROGRAMMING FUNDAMENTALS
    private val level0Fundamentals = RoadmapLevel(
        id = 0,
        levelNumber = 0,
        title = "Fundamentals",
        subtitle = "Computer & Programming Basics",
        description = "Pahami cara kerja komputer, terminal, arsitektur internet (HTTP, DNS, IP), logika algoritma, dan problem solving tanpa asumsi pengetahuan sebelumnya.",
        icon = "computer",
        colorHex = 0xFF38BDF8,
        estimatedHours = 12,
        modules = listOf(
            CourseModule(
                id = "mod-0-1",
                levelId = 0,
                title = "Arsitektur Komputer & Internet",
                description = "Hardware, Operating System, Terminal, Client-Server, HTTP/HTTPS, dan DNS.",
                lessons = listOf(
                    Lesson(
                        id = "fund-01",
                        levelId = 0,
                        moduleId = "mod-0-1",
                        title = "Bagaimana Komputer Mengeksekusi Program?",
                        durationMinutes = 15,
                        conceptExplanation = """
                            Komputer pada dasarnya adalah mesin pemroses instruksi logika berkecepatan tinggi. 
                            Komponen utamanya terdiri dari:
                            1. CPU (Central Processing Unit): Otak pemikir yang menjalankan instruksi biner (0 dan 1).
                            2. RAM (Memory): Tempat penyimpanan sementara saat program aktif berjalan.
                            3. Storage (SSD/HDD): Tempat penyimpanan data permanen (file, source code).
                            
                            Ketika kamu menulis source code, kode tersebut harus diubah menjadi Machine Code melalui Compiler (seperti C++ atau Rust) atau dijalankan baris demi baris oleh Interpreter (seperti Python atau JavaScript).
                        """.trimIndent(),
                        codeSnippet = """
                            # Simulasi algoritma sederhana:
                            name = "Programmer Muda"
                            print("Selamat datang di dunia coding, " + name)
                        """.trimIndent(),
                        codeLanguage = "python",
                        lineByLine = listOf(
                            LineExplanation("name = \"Programmer Muda\"", "Mendeklarasikan variable bernama 'name' dan menyimpannya di memory RAM."),
                            LineExplanation("print(\"Selamat datang...\" + name)", "Instruksi CPU untuk menampilkan teks ke console output.")
                        ),
                        practiceChallenge = "Ubah nilai variable name menjadi nama panggilanmu, lalu jalankan program!",
                        starterCode = "name = \"Budi\"\nprint(\"Halo, \" + name)",
                        expectedKeywordsOrOutput = "Halo",
                        summary = "Source code disimpan di storage, dimuat ke RAM, dan dieksekusi CPU sebagai instruksi biner.",
                        nextLessonId = "fund-02"
                    ),
                    Lesson(
                        id = "fund-02",
                        levelId = 0,
                        moduleId = "mod-0-1",
                        title = "Client, Server, dan Cara Kerja Web",
                        durationMinutes = 20,
                        conceptExplanation = """
                            Ketika kamu membuka browser (Client) dan mengetik google.com:
                            1. DNS Lookup: Browser menanyakan IP address server tujuan (seperti mencari nomor telepon dari buku kontak).
                            2. HTTP Request: Client mengirimkan surat permintaan (Request) lewat protokol HTTP/HTTPS.
                            3. Server Processing: Komputer server menerima request, memeriksa database jika perlu, lalu merespons.
                            4. HTTP Response: Server mengirim balik file HTML, CSS, JavaScript, atau data JSON.
                            5. Browser Rendering: Browser merender kode tersebut menjadi visual yang kamu lihat.
                        """.trimIndent(),
                        codeSnippet = """
                            // Contoh Fetch API untuk request data dari Server:
                            fetch("https://api.example.com/status")
                              .then(response => response.json())
                              .then(data => console.log("Status server:", data.status));
                        """.trimIndent(),
                        codeLanguage = "javascript",
                        lineByLine = listOf(
                            LineExplanation("fetch(\"https://api.example.com/status\")", "Client mengirim HTTP GET request ke server tujuan."),
                            LineExplanation(".then(response => response.json())", "Menerjemahkan payload jawaban server dari format JSON."),
                            LineExplanation(".then(data => console.log(...))", "Menampilkan data hasil response ke console browser.")
                        ),
                        practiceChallenge = "Lengkapi script untuk mencetak pesan sukses ketika server merespons 200 OK.",
                        starterCode = "const statusCode = 200;\nif (statusCode === 200) {\n  console.log(\"Koneksi Berhasil!\");\n}",
                        expectedKeywordsOrOutput = "Koneksi Berhasil!",
                        summary = "Arsitektur web berbasis Client-Server Request-Response menggunakan protokol HTTP aman (HTTPS).",
                        nextLessonId = "html-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-0-1",
                    title = "Kuis Fundamental Komputer & Web",
                    levelId = 0,
                    xpReward = 50,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-0-1",
                            question = "Komponen komputer manakah yang menyimpan instruksi program saat sedang aktif dijalankan?",
                            options = listOf("Storage SSD", "RAM (Random Access Memory)", "Monitor", "Power Supply"),
                            correctIndex = 1,
                            explanation = "RAM menyimpan data dan instruksi sementara yang sedang diakses CPU secara cepat saat runtime.",
                            conceptDoc = "Computer Architecture: Memory Hierarchy"
                        ),
                        QuizQuestion(
                            id = "q-0-2",
                            question = "Apa fungsi utama dari protokol DNS di internet?",
                            options = listOf(
                                "Mengubah nama domain (seperti google.com) menjadi IP Address numerik",
                                "Mengenkripsi password pengguna",
                                "Mempercepat download video",
                                "Membuat database otomatis"
                            ),
                            correctIndex = 0,
                            explanation = "DNS (Domain Name System) memetakan nama domain ramah manusia ke alamat IP server tempat aplikasi di-hosting.",
                            conceptDoc = "Networking: DNS & IP Resolution"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 1: HTML
    private val level1Html = RoadmapLevel(
        id = 1,
        levelNumber = 1,
        title = "HTML5",
        subtitle = "Semantic Structure & Web Foundation",
        description = "Kuasai fondasi web modern: tag, elemen, struktur semantic dokumen, media, tabel, form interaktif, aksesibilitas (a11y), dan SEO dasar.",
        icon = "code",
        colorHex = 0xFFF97316,
        estimatedHours = 14,
        modules = listOf(
            CourseModule(
                id = "mod-1-1",
                levelId = 1,
                title = "Struktur & Elemen Semantic HTML",
                description = "Tag dasar, heading, paragraf, link, media, dan struktur halaman ramah SEO.",
                lessons = listOf(
                    Lesson(
                        id = "html-01",
                        levelId = 1,
                        moduleId = "mod-1-1",
                        title = "Struktur Dokumen HTML5 & Semantic Elements",
                        durationMinutes = 20,
                        conceptExplanation = """
                            HTML (HyperText Markup Language) adalah kerangka dari setiap halaman web di internet.
                            Setiap dokumen HTML diawali dengan deklarasi <!DOCTYPE html>.
                            
                            Elemen Semantic memberikan arti jelas bagi browser dan screen-reader:
                            - <header>: Bagian kepala halaman atau navigasi
                            - <main>: Konten utama halaman yang unik
                            - <section> & <article>: Pembagian bab atau artikel mandiri
                            - <footer>: Bagian kaki halaman berisi copyright dan info kontak
                        """.trimIndent(),
                        codeSnippet = """
                            <!DOCTYPE html>
                            <html lang="id">
                            <head>
                              <meta charset="UTF-8">
                              <title>Profil Developer</title>
                            </head>
                            <body>
                              <header>
                                <h1>Halo, Saya Rian!</h1>
                                <p>Junior Software Engineer</p>
                              </header>
                              <main>
                                <article>
                                  <h2>Tentang Saya</h2>
                                  <p>Saya sedang belajar coding di CodeNest.</p>
                                </article>
                              </main>
                            </body>
                            </html>
                        """.trimIndent(),
                        codeLanguage = "html",
                        lineByLine = listOf(
                            LineExplanation("<!DOCTYPE html>", "Memberitahu browser bahwa dokumen ini menggunakan standar HTML5 modern."),
                            LineExplanation("<header>...</header>", "Elemen semantic untuk membungkus judul dan identitas halaman."),
                            LineExplanation("<article>...</article>", "Elemen semantic untuk konten yang dapat berdiri sendiri.")
                        ),
                        practiceChallenge = "Tambahkan elemen <button>Hubungi Saya</button> di dalam tag <header>.",
                        starterCode = "<h1>Profil</h1>\n<p>Software Engineer</p>\n<button>Hubungi Saya</button>",
                        expectedKeywordsOrOutput = "Hubungi Saya",
                        summary = "Semantic HTML membuat situs web lebih mudah dipahami mesin pencari (SEO) dan ramah disabilitas (Accessibility).",
                        nextLessonId = "css-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-1-1",
                    title = "Kuis Semantic HTML5",
                    levelId = 1,
                    xpReward = 60,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-1-1",
                            question = "Tag semantic manakah yang paling tepat untuk membungkus postingan blog atau berita mandiri?",
                            options = listOf("<div>", "<article>", "<span>", "<aside>"),
                            correctIndex = 1,
                            explanation = "<article> dirancang khusus untuk konten sindikasi atau artikel mandiri yang bermakna utuh tanpa bergantung konteks luar.",
                            conceptDoc = "HTML5 Semantics: Article vs Div"
                        )
                    )
                )
            )
        ),
        project = ProjectDefinition(
            id = "proj-1",
            title = "Personal Developer Landing Page",
            levelId = 1,
            difficulty = "Beginner",
            brief = "Bangun halaman portofolio developer satu halaman menggunakan struktur HTML5 semantic penuh, form kontak, dan daftar skill.",
            requirements = listOf(
                "Memiliki struktur <!DOCTYPE html>, <html>, <head>, dan <body>",
                "Menggunakan tag semantic <header>, <nav>, <main>, <section>, dan <footer>",
                "Memiliki form kontak dengan input nama, email, dan tombol submit",
                "Menyertakan alt text pada semua elemen gambar untuk aksesibilitas"
            ),
            hints = listOf(
                "Gunakan tag <fieldset> dan <label> untuk merapikan form",
                "Pastikan attribute type='email' digunakan untuk validasi browser otomatis"
            ),
            starterCode = """
                <!DOCTYPE html>
                <html lang="id">
                <head>
                  <title>Portofolio Saya</title>
                </head>
                <body>
                  <!-- Buat struktur semantic di sini -->
                  <header>
                    <h1>Portofolio Keren</h1>
                  </header>
                </body>
                </html>
            """.trimIndent(),
            language = "html",
            validationRules = listOf("<header", "<main", "<footer", "<form", "<button")
        )
    )

    // LEVEL 2: CSS
    private val level2Css = RoadmapLevel(
        id = 2,
        levelNumber = 2,
        title = "CSS & Layouts",
        subtitle = "Styling, Flexbox, Grid & Responsive UI",
        description = "Desain antarmuka web modern: Box Model, Flexbox, CSS Grid, media queries untuk mobile-first responsive design, animasi transisi, dan variabel tema CSS.",
        icon = "palette",
        colorHex = 0xFF06B6D4,
        estimatedHours = 18,
        modules = listOf(
            CourseModule(
                id = "mod-2-1",
                levelId = 2,
                title = "Box Model, Flexbox & CSS Grid",
                description = "Pemahaman mendalam tentang margin, padding, flex container, alignment, dan grid template.",
                lessons = listOf(
                    Lesson(
                        id = "css-01",
                        levelId = 2,
                        moduleId = "mod-2-1",
                        title = "Menguasai Modern Flexbox Layout",
                        durationMinutes = 25,
                        conceptExplanation = """
                            Flexbox adalah sistem layout 1 dimensi yang sangat kuat untuk mengatur posisi, perataan (alignment), dan distribusi ruang antar elemen secara dinamis.
                            
                            Property kunci pada parent container:
                            - display: flex; (mengaktifkan konteks fleksibel)
                            - justify-content: center | space-between | space-around; (mengatur posisi pada sumbu utama / main-axis)
                            - align-items: center | stretch | flex-start; (mengatur posisi pada sumbu silang / cross-axis)
                            - gap: 16px; (jarak bersih antar item tanpa perlu hack margin)
                        """.trimIndent(),
                        codeSnippet = """
                            .card-container {
                              display: flex;
                              justify-content: space-between;
                              align-items: center;
                              gap: 16px;
                              padding: 20px;
                              background-color: #0f172a;
                              border-radius: 12px;
                            }
                            .card-item {
                              flex: 1;
                              padding: 16px;
                              background: #1e293b;
                              color: #f8fafc;
                              border-radius: 8px;
                            }
                        """.trimIndent(),
                        codeLanguage = "css",
                        lineByLine = listOf(
                            LineExplanation("display: flex;", "Mengubah elemen menjadi flex container fleksibel."),
                            LineExplanation("justify-content: space-between;", "Menyebarkan kartu dengan jarak maksimal yang merata."),
                            LineExplanation("gap: 16px;", "Memberikan ruang spasi 16px antar kartu secara konsisten.")
                        ),
                        practiceChallenge = "Buat CSS rule untuk menengahkan teks dan tombol di dalam .hero-banner menggunakan flexbox.",
                        starterCode = ".hero-banner {\n  display: flex;\n  flex-direction: column;\n  align-items: center;\n  justify-content: center;\n  height: 200px;\n}",
                        expectedKeywordsOrOutput = "display: flex",
                        summary = "Flexbox menyelesaikan masalah alignment dan distribusi elemen web secara elegan dan responsif.",
                        nextLessonId = "js-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-2-1",
                    title = "Kuis Flexbox & Responsive Styling",
                    levelId = 2,
                    xpReward = 60,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-2-1",
                            question = "Manakah CSS property yang mengatur perataan item sepanjang main-axis pada flex container?",
                            options = listOf("align-items", "justify-content", "flex-wrap", "content-align"),
                            correctIndex = 1,
                            explanation = "justify-content mengontrol perataan di sepanjang main-axis (horizontal jika flex-direction: row).",
                            conceptDoc = "CSS Layouts: Flexbox Main Axis"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 3: JAVASCRIPT
    private val level3Js = RoadmapLevel(
        id = 3,
        levelNumber = 3,
        title = "JavaScript",
        subtitle = "Logic, DOM, Events & Async/Await",
        description = "Bahasa pemrograman interaktif web: variabel ES6 (const/let), tipe data, loop, high-order functions, DOM manipulation, asynchronous Promises, dan Fetch API.",
        icon = "javascript",
        colorHex = 0xFFFACC15,
        estimatedHours = 24,
        modules = listOf(
            CourseModule(
                id = "mod-3-1",
                levelId = 3,
                title = "Asynchronous JavaScript & REST API Integration",
                description = "Mempelajari Callbacks, Promises, async/await, error handling try-catch, dan HTTP fetch.",
                lessons = listOf(
                    Lesson(
                        id = "js-01",
                        levelId = 3,
                        moduleId = "mod-3-1",
                        title = "Async/Await & Modern Fetch API",
                        durationMinutes = 30,
                        conceptExplanation = """
                            JavaScript adalah bahasa single-threaded yang menggunakan Event Loop untuk menangani tugas berat (seperti memuat data dari server) tanpa membekukan tampilan aplikasi.
                            
                            Konsep Async/Await:
                            - Keyword 'async' sebelum fungsi menandakan bahwa fungsi tersebut akan mengembalikan Promise.
                            - Keyword 'await' menunda eksekusi kode selanjutnya sampai Promise selesai berhasil (resolved) atau gagal (rejected).
                            - Selalu bungkus blok await di dalam 'try...catch' untuk menangkap potensi kegagalan jaringan!
                        """.trimIndent(),
                        codeSnippet = """
                            async function loadUserProfile(userId) {
                              try {
                                const response = await fetch(`https://api.github.com/users/${'$'}{userId}`);
                                if (!response.ok) {
                                  throw new Error(`HTTP error! status: ${'$'}{response.status}`);
                                }
                                const user = await response.json();
                                console.log(`Nama: ${'$'}{user.name}, Repos: ${'$'}{user.public_repos}`);
                                return user;
                              } catch (error) {
                                console.error("Gagal mengambil data user:", error.message);
                              }
                            }
                            loadUserProfile("torvalds");
                        """.trimIndent(),
                        codeLanguage = "javascript",
                        lineByLine = listOf(
                            LineExplanation("async function loadUserProfile(userId)", "Mendefinisikan fungsi asynchronous yang bekerja tanpa memblokir UI."),
                            LineExplanation("const response = await fetch(...)", "Menunggu response jaringan dari endpoint server."),
                            LineExplanation("const user = await response.json()", "Parsing payload response stream menjadi objek JavaScript murni.")
                        ),
                        practiceChallenge = "Tulis fungsi async getStatus() yang mengembalikan string 'Server Online' setelah delay.",
                        starterCode = "async function getStatus() {\n  return 'Server Online';\n}\ngetStatus().then(res => console.log(res));",
                        expectedKeywordsOrOutput = "Server Online",
                        summary = "Async/Await membuat kode asynchronous asynchronous terlihat bersih dan mudah dibaca layaknya kode synchronous biasa.",
                        nextLessonId = "git-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-3-1",
                    title = "Kuis JavaScript Modern & Asynchronous",
                    levelId = 3,
                    xpReward = 75,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-3-1",
                            question = "Apa fungsi dari blok 'catch' ketika menggunakan async/await?",
                            options = listOf(
                                "Mengulang eksekusi fungsi 3 kali secara otomatis",
                                "Menangkap error atau penolakan Promise jika terjadi kegagalan jaringan",
                                "Memaksa server mengembalikan response 200 OK",
                                "Menutup koneksi browser"
                            ),
                            correctIndex = 1,
                            explanation = "Blok try-catch menangkap runtime exception atau Promise rejection sehingga aplikasi tidak crash.",
                            conceptDoc = "JavaScript Error Handling & Promises"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 4: GIT & GITHUB
    private val level4Git = RoadmapLevel(
        id = 4,
        levelNumber = 4,
        title = "Git & GitHub",
        subtitle = "Version Control & Team Collaboration",
        description = "Sistem kontrol versi standar industri: commit, branch, merge, pull request, resolusi merge conflict, .gitignore, dan open-source collaboration workflow.",
        icon = "source-branch",
        colorHex = 0xFFF43F5E,
        estimatedHours = 10,
        modules = listOf(
            CourseModule(
                id = "mod-4-1",
                levelId = 4,
                title = "Git Workflow & Branching Strategy",
                description = "Perintah esensial git init, add, commit, branch, checkout/switch, rebase, dan remote sync.",
                lessons = listOf(
                    Lesson(
                        id = "git-01",
                        levelId = 4,
                        moduleId = "mod-4-1",
                        title = "Arsitektur Git & Siklus Hidup File",
                        durationMinutes = 20,
                        conceptExplanation = """
                            Git mencatat riwayat perubahan file layaknya time-machine untuk programmer.
                            
                            Tiga area utama dalam Git lokal:
                            1. Working Directory: File yang sedang kamu edit di laptop.
                            2. Staging Area (Index): Wadah persiapan file yang dipilih untuk disimpan pada snapshot berikutnya (git add).
                            3. Repository (.git): Database snapshot yang tersimpan aman secara permanen setelah di-commit (git commit).
                            
                            Setelah tersimpan di repo lokal, kita mendorong (git push) snapshot tersebut ke remote server seperti GitHub untuk kolaborasi tim.
                        """.trimIndent(),
                        codeSnippet = """
                            # Inisialisasi repo baru
                            git init
                            
                            # Memeriksa status file yang berubah
                            git status
                            
                            # Memindahkan file ke Staging Area
                            git add index.html style.css
                            
                            # Merekam snapshot perubahan dengan pesan jelas
                            git commit -m "feat: tambahkan navigasi responsif dan dark mode"
                            
                            # Membuat branch fitur baru
                            git checkout -b feature/user-auth
                        """.trimIndent(),
                        codeLanguage = "bash",
                        lineByLine = listOf(
                            LineExplanation("git add index.html style.css", "Menambahkan file tertentu ke Staging Area sebelum di-commit."),
                            LineExplanation("git commit -m \"...\"", "Mencatat perubahan ke history lokal dengan hash kriptografi SHA unik."),
                            LineExplanation("git checkout -b feature/...", "Membuat dan berpindah ke cabang (branch) isolasi baru agar main branch tetap stabil.")
                        ),
                        practiceChallenge = "Tulis perintah Git untuk menambahkan seluruh file di direktori saat ini ke staging area.",
                        starterCode = "git add .",
                        expectedKeywordsOrOutput = "git add",
                        summary = "Gunakan branch fitur untuk bereksperimen tanpa mengganggu kestabilan kode produksi.",
                        nextLessonId = "ts-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-4-1",
                    title = "Kuis Git Essentials",
                    levelId = 4,
                    xpReward = 50,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-4-1",
                            question = "Perintah Git apa yang digunakan untuk membuat branch baru sekaligus langsung berpindah ke branch tersebut?",
                            options = listOf("git branch new-feature", "git checkout -b new-feature", "git merge new-feature", "git push new-feature"),
                            correctIndex = 1,
                            explanation = "Flag -b pada 'git checkout -b <name>' (atau 'git switch -c <name>') membuat branch baru dan langsung mengaktifkannya.",
                            conceptDoc = "Git Branching Workflows"
                        )
                    )
                )
            )
        )
    )

    // LEVEL 5: TYPESCRIPT
    private val level5TypeScript = RoadmapLevel(
        id = 5,
        levelNumber = 5,
        title = "TypeScript",
        subtitle = "Static Typing & Enterprise Scalability",
        description = "Tingkatkan keamanan kode JavaScript dengan sistem tipe statis: Interfaces, Types, Generics, Enums, Type Narrowing, dan Utility Types.",
        icon = "code-tags-check",
        colorHex = 0xFF3B82F6,
        estimatedHours = 16,
        modules = listOf(
            CourseModule(
                id = "mod-5-1",
                levelId = 5,
                title = "Type System & Generics",
                description = "Mencegah bug 'undefined is not a function' sebelum kode dijalankan di browser.",
                lessons = listOf(
                    Lesson(
                        id = "ts-01",
                        levelId = 5,
                        moduleId = "mod-5-1",
                        title = "Interfaces & Generics di TypeScript",
                        durationMinutes = 25,
                        conceptExplanation = """
                            TypeScript adalah superset dari JavaScript yang menambahkan Type Checking saat compile time.
                            Dengan tipe yang ketat, kamu mendapatkan autocomplete cerdas di IDE dan mencegah bug sepele.
                            
                            Konsep Kunci:
                            - interface / type: Kontrak bentuk objek data.
                            - Generics (<T>): Membuat fungsi dan class yang fleksibel namun tetap aman terhadap tipe data apa pun yang dilewatkan.
                        """.trimIndent(),
                        codeSnippet = """
                            interface ApiResponse<T> {
                              status: number;
                              success: boolean;
                              data: T;
                            }
                            
                            interface UserProfile {
                              id: string;
                              username: string;
                              xp: number;
                            }
                            
                            function formatResponse<T>(payload: ApiResponse<T>): string {
                              return `Status: ${'$'}{payload.status} | Success: ${'$'}{payload.success}`;
                            }
                        """.trimIndent(),
                        codeLanguage = "typescript",
                        lineByLine = listOf(
                            LineExplanation("interface ApiResponse<T>", "Interface generic dengan tipe variabel parameter T."),
                            LineExplanation("data: T;", "Field data akan menyesuaikan tipe yang dispesifikasikan saat pemanggilan."),
                            LineExplanation("function formatResponse<T>(...)", "Fungsi type-safe yang menerima ApiResponse jenis apa pun.")
                        ),
                        practiceChallenge = "Definisikan interface Course dengan property id (number) dan title (string).",
                        starterCode = "interface Course {\n  id: number;\n  title: string;\n}\nconst c: Course = { id: 1, title: 'TypeScript' };",
                        expectedKeywordsOrOutput = "interface Course",
                        summary = "TypeScript menangkap bug logika saat proses development sebelum sampai ke tangan pengguna akhir.",
                        nextLessonId = "react-01"
                    )
                ),
                quiz = QuizDefinition(
                    id = "quiz-5-1",
                    title = "Kuis TypeScript Type Safety",
                    levelId = 5,
                    xpReward = 60,
                    questions = listOf(
                        QuizQuestion(
                            id = "q-5-1",
                            question = "Apa keunggulan utama menggunakan Generics (<T>) di TypeScript?",
                            options = listOf(
                                "Membuat kode berjalan 10x lebih cepat di CPU",
                                "Memungkinkan komponen bekerja dengan berbagai tipe data tanpa kehilangan type safety",
                                "Menghilangkan kebutuhan compile ke JavaScript",
                                "Mengompres ukuran file otomatis"
                            ),
                            correctIndex = 1,
                            explanation = "Generics memungkinkan penulisan fungsi dan struktur data reusable yang tetap menjaga integritas tipe data.",
                            conceptDoc = "TypeScript Documentation: Generics"
                        )
                    )
                )
            )
        )
    )
}
