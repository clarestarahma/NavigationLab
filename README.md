# Tugas Praktikum 5, 6 - Navigation Compose & State Management (MVVM) 

## Deskripsi Aplikasi
Aplikasi ini merupakan implementasi navigasi antar-layar dan pengelolaan status antarmuka (*state management*) menggunakan **Jetpack Compose** dan pola arsitektur **MVVM (Model-View-ViewModel)**. 

Aplikasi dibangun menggunakan pendekatan *Single-Activity Architecture*, di mana seluruh alur perpindahan halaman ditangani oleh `NavHost` tanpa membuat `Activity` baru. Aplikasi mengintegrasikan empat destinasi utama: **Home**, **Detail**, **Profile**, dan **About**, serta menerapkan prinsip *Unidirectional Data Flow* (UDF) dan *Dependency Injection* secara manual.

---

## Arsitektur & Struktur Layer

Aplikasi ini membagi tanggung jawab ke dalam beberapa *layer* (*Separation of Concerns*):

1. **Data Layer**:
   - `Student`: Data model untuk menyimpan atribut mahasiswa (ID, nama, program studi, angkatan).
   - `StudentLocalDataSource`: Sumber data lokal (*in-memory*) yang menampung daftar mahasiswa *dummy*.
   - `StudentRepository` & `DefaultStudentRepository`: Abstraksi dan implementasi pintu akses data utama (*Single Source of Truth*).

2. **UI & ViewModel Layer**:
   - `HomeUiState` & `HomeViewModel`: Mengelola *state* daftar mahasiswa, status *loading*, dan pesan *error* pada layar `Home`.
   - `DetailUiState` & `DetailViewModel`: Mengelola *state* detail mahasiswa berdasarkan argumen `studentId` yang diterima.
   - `HomeScreen`, `DetailScreen`, `ProfileScreen`, `AboutScreen`: Komponen antarmuka yang bersifat *stateless* (hanya menerima `UiState` dan *event callbacks*).

3. **Navigation Layer**:
   - `NavigationLabApp`: *Root composable* yang menginisialisasi *dependency* (`Repository`), mengarahkan rute `NavHost`, serta menginstansiasi `ViewModel` menggunakan `ViewModelProvider.Factory`.

---

## Fitur Utama & Pengujian

- **Start Destination & List Mahasiswa**: Layar `Home` bertindak sebagai *start destination* yang menampilkan daftar mahasiswa secara interaktif menggunakan `LazyColumn` lengkap dengan batas scroll (`Modifier.weight(1f)`).
- **Navigasi Terparameterisasi**: Menekan tombol pada `StudentCard` akan memicu navigasi ke rute `detail/{studentId}` (misalnya `detail/1034`), di mana `DetailViewModel` mengambil data spesifik dari *repository* secara deklaratif via `LaunchedEffect`.
- **Pengelolaan Back Stack**:
  - Alur navigasi `Home` → `Detail` → `Profile` → `Back` → `Back` telah diuji.
  - Penekanan tombol *Kembali* pertama menghapus `Profile` dari *back stack* (kembali ke `Detail`).
  - Penekanan tombol *Kembali* kedua menghapus `Detail` dari *back stack* (kembali ke `Home`).
- **Rotasi Layar & Konfigurasi**: Aplikasi stabil terhadap perubahan konfigurasi (*screen rotation*). Layar `About` dan `Profile` dilengkapi dengan *vertical scroll* untuk mencegah elemen terpotong pada orientasi lanskap.

---

## Struktur Rute (Routes)

- `Routes.HOME` (`"home"`) $\rightarrow$ Halaman utama yang menampilkan daftar mahasiswa, navigasi ke Profile, dan About.
- `Routes.DETAIL` (`"detail/{studentId}"`) $\rightarrow$ Halaman detail mahasiswa berdasarkan ID yang dikirim.
- `Routes.PROFILE` (`"profile"`) $\rightarrow$ Halaman profil pengguna.
- `Routes.ABOUT` (`"about"`) $\rightarrow$ Halaman informasi aplikasi / biodata pembuat.

---

## Teknologi & Dependensi

- **Bahasa**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Architecture Component**: ViewModel, State Hoisting, UDF
- **Navigation**: Jetpack Navigation Compose (`androidx.navigation:navigation-compose:2.9.8`)
