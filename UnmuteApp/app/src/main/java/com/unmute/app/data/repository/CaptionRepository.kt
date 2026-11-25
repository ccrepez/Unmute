package com.unmute.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.unmute.app.data.model.Caption
import com.unmute.app.data.model.LiveSession
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CaptionRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
) {

    fun observeCaptions(sessionId: String): Flow<List<Caption>> = callbackFlow {
        val registration = db.collection("sessions").document(sessionId)
            .collection("captions")
            .orderBy("seq", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val captions = snapshot?.documents.orEmpty().mapNotNull { doc ->
                    val text = doc.getString("text") ?: return@mapNotNull null
                    Caption(id = doc.id, seq = doc.getLong("seq") ?: 0L, text = text)
                }
                trySend(captions)
            }
        awaitClose { registration.remove() }
    }

    fun observeSession(sessionId: String): Flow<LiveSession?> = callbackFlow {
        val registration = db.collection("sessions").document(sessionId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val session = snapshot?.takeIf { it.exists() }?.let {
                    LiveSession(
                        title = it.getString("title") ?: "",
                        status = it.getString("status") ?: "LIVE",
                        classroomId = it.getString("classroomId") ?: "",
                    )
                }
                trySend(session)
            }
        awaitClose { registration.remove() }
    }
}
