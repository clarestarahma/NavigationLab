# Tugas Praktikum Modul 5 - Navigation Compose

## Deskripsi Aplikasi
Aplikasi ini merupakan implementasi navigasi antar-layar menggunakan **Jetpack Compose Navigation**. Aplikasi dibangun menggunakan arsitektur *Single-Activity*, di mana perpindahan halaman dilakukan dengan menavigasi *Composable functions* (`NavHost`) tanpa membuat `Activity` baru. Terdapat empat halaman (*destination*) utama: **Home**, **Detail**, **Profile**, dan **About**.

## Penjelasan Implementasi Tugas dan Challenge

Aplikasi ini telah ditambahkan empat *destination* utama yaitu Home, Detail, Profile, dan About yang semuanya diimplementasikan menggunakan Navigation Compose dalam satu `MainActivity`. Pengerjaan tugas utama meliputi pembuatan `AboutScreen` sebagai *composable* baru dengan route "about" pada `NavHost`, serta penambahan tombol "Buka About" di `HomeScreen`. Pada layar About, telah ditambahkan tombol Kembali yang berfungsi dengan baik menggunakan *callback* `navController.popBackStack()`. Selain itu, argumen `studentId` yang dikirim dari Home ke Detail telah diubah nilainya menjadi 1034 dan berhasil dirender dengan benar pada `DetailScreen`.

Untuk bagian Challenge Opsional, telah ditambahkan tombol baru pada `DetailScreen` untuk menavigasi langsung ke halaman Profile. Berdasarkan pengujian alur Home → Detail → Profile → Back → Back, *back stack* berjalan sesuai urutan tumpukannya. Saat tombol Kembali ditekan pertama kali, layar Profile dihapus (*pop*) dari tumpukan teratas sehingga tampilan kembali ke Detail. Saat tombol Kembali ditekan kedua kalinya, layar Detail ikut terhapus sehingga aplikasi kembali ke halaman utama (Home). Terakhir, aplikasi juga telah diuji dengan melakukan rotasi layar (perubahan konfigurasi) dan terbukti aman dari *crash*. Terdapat penambahan *scroll* vertikal pada bagian About karena ketika dirotasi ke bentuk lanskap, terdapat elemen yang tertutup.

## Struktur Navigasi (Route)
- `Routes.HOME` ("home") -> Halaman utama (*start destination*).
- `Routes.DETAIL` ("detail/{studentId}") -> Menerima argumen integer (1034).
- `Routes.PROFILE` ("profile") -> Menampilkan profil aplikasi.
- `Routes.ABOUT` ("about") -> Menampilkan biodata pembuat (dilengkapi *vertical scroll*).

## Teknologi yang Digunakan
- **Bahasa:** Kotlin
- **UI Toolkit:** Jetpack Compose
- **Navigasi:** Jetpack Navigation Compose (`androidx.navigation:navigation-compose:2.9.8`)