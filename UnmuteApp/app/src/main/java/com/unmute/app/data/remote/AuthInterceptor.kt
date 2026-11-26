package com.unmute.app.data.remote

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

class AuthInterceptor(
    private val tokenProvider: () -> String? = ::currentFirebaseToken,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        if (original.header(HEADER) != null) return chain.proceed(original)

        val token = runCatching { tokenProvider() }.getOrNull()
        val request = if (token.isNullOrBlank()) {
            original
        } else {
            original.newBuilder().header(HEADER, "Bearer $token").build()
        }
        return chain.proceed(request)
    }

    companion object {
        private const val HEADER = "Authorization"

        private fun currentFirebaseToken(): String? {
            val user = FirebaseAuth.getInstance().currentUser ?: return null
            return Tasks.await(user.getIdToken(false), 10, TimeUnit.SECONDS).token
        }
    }
}
