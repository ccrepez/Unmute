import crypto from "crypto";
import { Router } from "express";
import { Prisma, Role } from "@prisma/client";
import prisma from "../lib/prisma";
import { requireAuth, requireRole } from "../middleware/auth";

const router = Router();

const JOIN_CODE_ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ";
const JOIN_CODE_LENGTH = 6;
const MAX_CODE_ATTEMPTS = 5;

function generateJoinCode(): string {
  let code = "";
  for (let i = 0; i < JOIN_CODE_LENGTH; i++) {
    code += JOIN_CODE_ALPHABET[crypto.randomInt(JOIN_CODE_ALPHABET.length)];
  }
  return code;
}

function isUniqueConstraintError(err: unknown): boolean {
  return err instanceof Prisma.PrismaClientKnownRequestError && err.code === "P2002";
}

router.post("/classrooms", requireAuth, requireRole(Role.DOSEN), async (req, res) => {
  const name = typeof req.body?.name === "string" ? req.body.name.trim() : "";
  const courseCode = typeof req.body?.courseCode === "string" ? req.body.courseCode.trim() : "";

  if (!name || !courseCode) {
    res.status(400).json({ data: null, message: "Nama kelas dan kode mata kuliah wajib diisi" });
    return;
  }

  for (let attempt = 0; attempt < MAX_CODE_ATTEMPTS; attempt++) {
    try {
      const classroom = await prisma.classroom.create({
        data: {
          name,
          courseCode,
          joinCode: generateJoinCode(),
          lecturerId: req.userId!,
        },
      });
      res.status(201).json({
        data: { id: classroom.id, joinCode: classroom.joinCode },
        message: "Kelas berhasil dibuat",
      });
      return;
    } catch (err) {
      if (isUniqueConstraintError(err) && attempt < MAX_CODE_ATTEMPTS - 1) {
        continue;
      }
      throw err;
    }
  }
});

router.post("/classrooms/join", requireAuth, requireRole(Role.MAHASISWA), async (req, res) => {
  const rawCode = typeof req.body?.joinCode === "string" ? req.body.joinCode : "";
  const joinCode = rawCode.trim().toUpperCase();

  if (!joinCode) {
    res.status(400).json({ data: null, message: "Kode kelas wajib diisi" });
    return;
  }

  const classroom = await prisma.classroom.findUnique({ where: { joinCode } });
  if (!classroom) {
    res.status(404).json({ data: null, message: "Kode kelas tidak ditemukan" });
    return;
  }

  const existing = await prisma.enrollment.findUnique({
    where: {
      classroomId_userId: { classroomId: classroom.id, userId: req.userId! },
    },
  });
  if (existing) {
    res.status(409).json({ data: null, message: "Kamu sudah tergabung di kelas ini" });
    return;
  }

  await prisma.enrollment.create({
    data: { classroomId: classroom.id, userId: req.userId! },
  });

  res.status(201).json({
    data: { classroomId: classroom.id },
    message: "Berhasil bergabung ke kelas",
  });
});

export default router;
