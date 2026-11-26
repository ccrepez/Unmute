import fs from "fs";
import path from "path";
import { App, cert, getApps, initializeApp, ServiceAccount } from "firebase-admin/app";
import { getAuth } from "firebase-admin/auth";
import { getFirestore } from "firebase-admin/firestore";

function loadServiceAccount(): ServiceAccount {
  const rawPath = process.env.FIREBASE_SERVICE_ACCOUNT_PATH;
  if (!rawPath) {
    throw new Error("FIREBASE_SERVICE_ACCOUNT_PATH belum diisi di file .env");
  }
  const fullPath = path.resolve(process.cwd(), rawPath);
  if (!fs.existsSync(fullPath)) {
    throw new Error(`File service account tidak ditemukan: ${fullPath}`);
  }
  return JSON.parse(fs.readFileSync(fullPath, "utf8")) as ServiceAccount;
}

const existing = getApps();

export const firebaseApp: App = existing.length
  ? existing[0]
  : initializeApp({ credential: cert(loadServiceAccount()) });

export const firebaseAuth = getAuth(firebaseApp);
export const firestore = getFirestore(firebaseApp);

export default firebaseApp;
