# Marsha — Panduan Minggu 1

## 1. Firebase & Firestore (Selasa)
1. Buka https://console.firebase.google.com, klik **Create a project**, beri nama `unmute`. Google Analytics boleh dimatikan. Pastikan paketnya **Spark (gratis)**.
2. Di menu kiri pilih **Build → Firestore Database → Create database**.
   - Lokasi: `asia-southeast2 (Jakarta)`.
   - Pilih **Start in test mode**. Mode ini berlaku 30 hari; catat di laporan bahwa versi rilis butuh aturan yang lebih ketat.
3. Klik ikon **Android** di Project Overview untuk mendaftarkan aplikasi:
   - Package name: `com.unmute.app` (harus sama persis).
   - Klik Register, lalu **Download google-services.json**.
   - Lewati langkah "Add Firebase SDK" karena sudah dipasang di proyek.
4. Taruh file itu di `android/app/google-services.json`, lalu Sync Gradle. File ini sengaja di-.gitignore, jadi bagikan ke Christa dan Ina lewat chat.
5. Untuk server (dipakai Ina/Christa): **Project settings → Service accounts → Generate new private key**, lalu kirim file-nya ke Ina lewat chat. Jangan di-push.
6. Undang tim lewat **Project settings → Users and permissions → Add member**.

## 2. Menguji layar Caption Live (Kamis–Jumat)
1. Jalankan aplikasi di emulator. Layar langsung membuka sesi `test`.
2. Di Firestore Console: **Start collection** `sessions`, lalu Document ID `test` dengan field:
   - `title` (string): `Tes Caption`
   - `status` (string): `LIVE`
   - `classroomId` (string): `dummy`
3. Di dokumen `test`, klik **Start collection** `captions`, lalu Auto-ID dengan field:
   - `seq` (number): `1`
   - `text` (string): `Selamat pagi semuanya`
   - `createdAt` (timestamp): sekarang
4. Tambahkan dokumen lain dengan `seq` 2, 3, dan seterusnya. Caption harus langsung muncul di emulator dan layar menggulir sendiri.
5. Ubah `status` jadi `ENDED`. Pesan "Sesi sudah berakhir" harus muncul.

## 3. Database & route (Selasa & Sabtu)
Proyek Express dibuat Ina. Setelah repo Ina ada:
1. Salin `backend/prisma/schema.prisma` ke proyek Express (6 tabel), isi `DATABASE_URL` di `.env`, lalu jalankan `npx prisma migrate dev --name init`.
2. Salin `backend/src/routes/notes.ts`, lalu pasang di file utama Express: `app.use("/api", notesRouter)`.
3. Uji di Postman: `GET http://localhost:3000/api/classrooms/1/sessions` dan `GET http://localhost:3000/api/sessions/1/notes`.

## 4. Sketsa layar (Rabu)
```
Caption Live            Komponen Pertanyaan       Riwayat / Catatan
┌──────────────────┐   ┌──────────────────┐      ┌──────────────────┐
│← Judul  LIVE A−A+│   │  ❓ 3            │      │ 🔍 cari kata...  │
├──────────────────┤   ├──────────────────┤      ├──────────────────┤
│ caption 1        │   │ Budi · 09.12     │      │ Pertemuan 2      │
│ caption 2        │   │ Apa itu Compose? │      │ 24 Nov · 90 mnt  │
│ caption 3 ▼      │   │   [Sudah dijawab]│      ├──────────────────┤
├──────────────────┤   │ Sari · 09.20     │      │ Transkrip ...    │
│[tulis pertanyaan]│   │ ...              │      │ Pertanyaan:      │
│          [Kirim] │   └──────────────────┘      │ • ... ✓          │
└──────────────────┘                             └──────────────────┘
```
