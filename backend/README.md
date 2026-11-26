# Unmute Backend

API Express + TypeScript + Prisma (PostgreSQL) + firebase-admin.

## Menyiapkan di laptop sendiri

1. Pasang Node.js (LTS) dan PostgreSQL.
2. Masuk ke folder `backend/` lalu pasang dependensi:

   ```bash
   npm install
   ```

3. Salin `.env.example` menjadi `.env`, lalu isi:
   - `DATABASE_URL` dengan user, password, dan nama database PostgreSQL di laptopmu.
   - `FIREBASE_SERVICE_ACCOUNT_PATH` dengan path file service account firebase-admin
     (file `*firebase-adminsdk*.json` dari grup, taruh di folder `backend/`).
4. Buat tabel di database (6 tabel: users, classrooms, enrollments, sessions,
   session_notes, session_questions):

   ```bash
   npx prisma migrate dev
   ```

5. Nyalakan server (otomatis restart saat file berubah):

   ```bash
   npm run dev
   ```

   Server berjalan di `http://localhost:3000/api`. Emulator Android memakai
   `http://10.0.2.2:3000/api/`.

## Aturan

- Semua route memakai awalan `/api`.
- Format respons: `{ "data": ..., "message": "..." }`.
- Pakai `src/lib/prisma.ts` untuk koneksi database dan `src/lib/firebase.ts`
  untuk firebase-admin (Auth dan Firestore), jangan membuat koneksi baru.
- Setiap orang hanya mengedit file router miliknya di `src/routes/`.
  Semua router sudah didaftarkan di `src/app.ts`.

| File router | Pemilik |
| --- | --- |
| `src/routes/users.ts` | Ina |
| `src/routes/classrooms.ts` | Ina |
| `src/routes/sessions.ts` | Christa |
| `src/routes/questions.ts` | Marsha |
| `src/routes/notes.ts` | Ina (versi dummy dari branch nomor 1) |

- File `.env` dan file service account tidak boleh di-push.
