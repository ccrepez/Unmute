import express, { NextFunction, Request, Response } from "express";
import cors from "cors";
import notesRouter from "./routes/notes";
import usersRouter from "./routes/users";
import classroomsRouter from "./routes/classrooms";
import sessionsRouter from "./routes/sessions";
import questionsRouter from "./routes/questions";

const app = express();

app.use(cors());
app.use(express.json());

app.get("/api/health", (_req, res) => {
  res.json({ data: { status: "ok" }, message: "Server Unmute berjalan" });
});

app.use("/api", usersRouter);
app.use("/api", classroomsRouter);
app.use("/api", sessionsRouter);
app.use("/api", questionsRouter);
app.use("/api", notesRouter);

app.use((_req: Request, res: Response) => {
  res.status(404).json({ data: null, message: "Endpoint tidak ditemukan" });
});

app.use((err: unknown, _req: Request, res: Response, _next: NextFunction) => {
  console.error(err);
  if (err instanceof SyntaxError && "body" in err) {
    res.status(400).json({ data: null, message: "Format JSON tidak valid" });
    return;
  }
  res.status(500).json({ data: null, message: "Terjadi kesalahan di server" });
});

export default app;
