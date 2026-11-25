package com.unmute.app.data.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class QuestionRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {
    suspend fun submitQuestion(
        sessionId: String,
        studentId: String,
        studentName: String,
        text: String,
    ) {
        db.collection("sessions").document(sessionId)
            .collection("questions")
            .add(
                mapOf(
                    "studentId" to studentId,
                    "studentName" to studentName,
                    "text" to text,
                    "isAnswered" to false,
                    "createdAt" to FieldValue.serverTimestamp(),
                )
            )
            .await()
    }

    companion object {
        const val TEST_STUDENT_ID = "test-student"
        const val TEST_STUDENT_NAME = "Mahasiswa Tes"
    }
}
