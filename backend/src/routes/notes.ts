import { Router } from "express";

const router = Router();

router.get("/classrooms/:id/sessions", (_req, res) => {
  const now = Date.now();
  res.json({
    data: [
      {
        id: "dummy-session-2",
        title: "Pertemuan 2: Jetpack Compose",
        status: "ENDED",
        startedAt: new Date(now - 2 * 86_400_000).toISOString(),
        endedAt: new Date(now - 2 * 86_400_000 + 90 * 60_000).toISOString(),
      },
      {
        id: "dummy-session-1",
        title: "Pertemuan 1: Pengenalan Kotlin",
        status: "ENDED",
        startedAt: new Date(now - 9 * 86_400_000).toISOString(),
        endedAt: new Date(now - 9 * 86_400_000 + 100 * 60_000).toISOString(),
      },
    ],
    message: "OK",
  });
});

router.get("/sessions/:id/notes", (_req, res) => {
  res.json({
    data: {
      title: "Pertemuan 2: Jetpack Compose",
      transcript:
        "Selamat pagi semuanya. Hari ini kita belajar Jetpack Compose. " +
        "Compose adalah cara baru membuat tampilan Android dengan fungsi Kotlin.",
      captionCount: 2,
      questions: [
        { text: "Apa bedanya Compose dengan XML?", isAnswered: true },
        { text: "Apakah Compose bisa dipakai di iOS?", isAnswered: false },
      ],
    },
    message: "OK",
  });
});

export default router;
