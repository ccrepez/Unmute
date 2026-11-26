package com.unmute.app.data.repository

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.unmute.app.data.model.SaveProfileRequest
import com.unmute.app.data.model.UserProfile
import com.unmute.app.data.remote.ApiErrorParser
import com.unmute.app.data.remote.RetrofitClient
import com.unmute.app.data.remote.UserApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

object CurrentUserStore {
    private val _profile = MutableStateFlow<UserProfile?>(null)
    val profile: StateFlow<UserProfile?> = _profile.asStateFlow()

    fun set(profile: UserProfile?) {
        _profile.value = profile
    }
}

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val api: UserApiService = RetrofitClient.userApi,
) {
    val isSignedIn: Boolean get() = auth.currentUser != null

    suspend fun register(name: String, email: String, password: String, role: String): Result<UserProfile> =
        withContext(Dispatchers.IO) {
            val created = runCatching { auth.createUserWithEmailAndPassword(email, password).await() }
            created.exceptionOrNull()?.let { return@withContext Result.failure(Exception(firebaseMessage(it))) }

            runCatching {
                val response = api.saveProfile(SaveProfileRequest(name = name, role = role))
                response.data ?: error(response.message.ifBlank { "Gagal menyimpan profil" })
            }.onSuccess { profile ->
                CurrentUserStore.set(profile)
            }.recoverCatching { e ->
                runCatching { auth.currentUser?.delete()?.await() }
                auth.signOut()
                throw Exception(ApiErrorParser.message(e, "Gagal menyimpan profil"))
            }
        }

    suspend fun login(email: String, password: String): Result<UserProfile> = withContext(Dispatchers.IO) {
        val signedIn = runCatching { auth.signInWithEmailAndPassword(email, password).await() }
        signedIn.exceptionOrNull()?.let { return@withContext Result.failure(Exception(firebaseMessage(it))) }

        fetchProfile().onFailure { auth.signOut() }
    }

    suspend fun restoreSession(): Result<UserProfile> = withContext(Dispatchers.IO) {
        if (auth.currentUser == null) return@withContext Result.failure(Exception("Belum login"))
        fetchProfile()
    }

    fun logout() {
        auth.signOut()
        CurrentUserStore.set(null)
    }

    private suspend fun fetchProfile(): Result<UserProfile> = runCatching {
        val response = api.getProfile()
        response.data ?: error(response.message.ifBlank { "Profil tidak ditemukan" })
    }.onSuccess { profile ->
        CurrentUserStore.set(profile)
    }.recoverCatching { e ->
        throw Exception(ApiErrorParser.message(e, "Gagal mengambil profil"))
    }

    private fun firebaseMessage(e: Throwable): String = when (e) {
        is FirebaseAuthWeakPasswordException -> "Password minimal 6 karakter"
        is FirebaseAuthUserCollisionException -> "Email sudah terdaftar, silakan login"
        is FirebaseAuthInvalidUserException -> "Akun tidak ditemukan"
        is FirebaseAuthInvalidCredentialsException -> "Email atau password salah"
        is FirebaseNetworkException -> "Tidak ada koneksi internet"
        else -> e.message ?: "Terjadi kesalahan, coba lagi"
    }
}
