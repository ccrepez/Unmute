import { Router } from "express";
import { Role, User } from "@prisma/client";
import prisma from "../lib/prisma";
import { requireAuth, verifyFirebaseToken } from "../middleware/auth";

const router = Router();

function toProfile(user: User) {
  return {
    id: user.id,
    firebaseUid: user.firebaseUid,
    name: user.name,
    email: user.email,
    role: user.role,
  };
}

router.post("/users/me", verifyFirebaseToken, async (req, res) => {
  const { uid, email: tokenEmail } = req.firebase!;
  const name = typeof req.body?.name === "string" ? req.body.name.trim() : "";
  const role = req.body?.role;
  const email = tokenEmail ?? (typeof req.body?.email === "string" ? req.body.email.trim() : "");

  if (!name) {
    res.status(400).json({ data: null, message: "Nama wajib diisi" });
    return;
  }
  if (role !== Role.DOSEN && role !== Role.MAHASISWA) {
    res.status(400).json({ data: null, message: "Role harus DOSEN atau MAHASISWA" });
    return;
  }
  if (!email) {
    res.status(400).json({ data: null, message: "Email tidak ditemukan di akun Firebase" });
    return;
  }

  const existing = await prisma.user.findUnique({ where: { firebaseUid: uid } });
  if (existing) {
    const updated = await prisma.user.update({ where: { id: existing.id }, data: { name } });
    res.json({ data: toProfile(updated), message: "Profil diperbarui" });
    return;
  }

  const emailOwner = await prisma.user.findUnique({ where: { email } });
  if (emailOwner) {
    res.status(409).json({ data: null, message: "Email sudah dipakai akun lain" });
    return;
  }

  const user = await prisma.user.create({
    data: { firebaseUid: uid, email, name, role },
  });
  res.status(201).json({ data: toProfile(user), message: "Profil berhasil disimpan" });
});

router.get("/users/me", requireAuth, async (req, res) => {
  res.json({ data: req.user, message: "OK" });
});

export default router;
