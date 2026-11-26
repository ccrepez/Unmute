import { NextFunction, Request, Response } from "express";
import { Role } from "@prisma/client";
import { firebaseAuth } from "../lib/firebase";
import prisma from "../lib/prisma";

export interface AuthUser {
  id: string;
  firebaseUid: string;
  name: string;
  email: string;
  role: Role;
}

export interface FirebaseIdentity {
  uid: string;
  email?: string;
}

declare global {
  namespace Express {
    interface Request {
      firebase?: FirebaseIdentity;
      user?: AuthUser;
      userId?: string;
      role?: Role;
    }
  }
}

type TokenResult =
  | { ok: true; identity: FirebaseIdentity }
  | { ok: false; message: string };

async function decodeToken(req: Request): Promise<TokenResult> {
  const header = req.headers.authorization;
  if (!header || !header.startsWith("Bearer ")) {
    return { ok: false, message: "Token tidak ada. Silakan login terlebih dahulu" };
  }
  const token = header.slice("Bearer ".length).trim();
  if (!token) {
    return { ok: false, message: "Token tidak ada. Silakan login terlebih dahulu" };
  }
  try {
    const decoded = await firebaseAuth.verifyIdToken(token);
    return { ok: true, identity: { uid: decoded.uid, email: decoded.email } };
  } catch {
    return { ok: false, message: "Token tidak valid atau sudah kedaluwarsa" };
  }
}

export async function verifyFirebaseToken(req: Request, res: Response, next: NextFunction) {
  const result = await decodeToken(req);
  if (!result.ok) {
    res.status(401).json({ data: null, message: result.message });
    return;
  }
  req.firebase = result.identity;
  next();
}

export async function requireAuth(req: Request, res: Response, next: NextFunction) {
  const result = await decodeToken(req);
  if (!result.ok) {
    res.status(401).json({ data: null, message: result.message });
    return;
  }
  req.firebase = result.identity;

  const user = await prisma.user.findUnique({ where: { firebaseUid: result.identity.uid } });
  if (!user) {
    res.status(403).json({
      data: null,
      message: "Profil belum terdaftar. Lengkapi registrasi terlebih dahulu",
    });
    return;
  }

  req.user = {
    id: user.id,
    firebaseUid: user.firebaseUid,
    name: user.name,
    email: user.email,
    role: user.role,
  };
  req.userId = user.id;
  req.role = user.role;
  next();
}

export function requireRole(...roles: Role[]) {
  return (req: Request, res: Response, next: NextFunction) => {
    if (!req.role || !roles.includes(req.role)) {
      res.status(403).json({ data: null, message: "Kamu tidak punya akses untuk aksi ini" });
      return;
    }
    next();
  };
}
