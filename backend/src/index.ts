import "dotenv/config";
import app from "./app";

const PORT = Number(process.env.PORT) || 3000;

app.listen(PORT, () => {
  console.log(`Server Unmute berjalan di http://localhost:${PORT}/api`);
});
