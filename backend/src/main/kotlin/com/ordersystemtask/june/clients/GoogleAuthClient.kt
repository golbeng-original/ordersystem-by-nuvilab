package com.ordersystemtask.june.clients

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import java.net.URI
import java.net.URLEncoder
import java.util.Base64

data class AuthorizationResponse(
    val accessToken: String,
    val refreshToken: String,
    val sub:String,
    val email:String,
    val expireSeconds:Long
)

data class ResolveAccessTokenResponse(
    val accessToken: String,
    val expireSeconds:Long
)

data class GoogleOAuthTokenSuccessPayload(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("refresh_token")
    val refreshToken: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    @SerializedName("id_token")
    val idToken:String
)

data class GoogleOAuthResolveTokenSuccessPayload(
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("expires_in")
    val expiresIn: Int
)

data class GoogleOAuthTokenErrorPayload(
    @SerializedName("error")
    val error: String,
    @SerializedName("error_description")
    val errorDescription: String
)

data class GoogleOauthIdTokenPayload(
    val iss:String,
    val azp:String,
    val aud:String,
    val sub:String,
    val email:String,
    val name:String,
    val picture:String
)

@Component
@ConfigurationProperties("oauth-provider.google")
class GoogleOAuthConfig(
    var clientId: String = "",
    var clientSecret: String = ""
)

class GoogleAuthClient(
    private val config: GoogleOAuthConfig
) {

    companion object {
        const val REDIRECT_URI = "http://localhost:8080/auth/callback"
        const val SCOPE = "email profile openid"
    }

    private val _authorizationUrl = "https://accounts.google.com/o/oauth2/v2/auth"
    private val _requestTokenUrl = "https://oauth2.googleapis.com/token"
    private val _clientId = config.clientId
    private val _clientSecretKey = config.clientSecret

    private val _client = okhttp3.OkHttpClient()

    fun generateAuthorizationUrl() : URI {
        val stringBuilder = StringBuilder()
        stringBuilder.append(_authorizationUrl)
        stringBuilder.append("?")
        stringBuilder.append("client_id="+URLEncoder.encode(_clientId, Charsets.UTF_8))
        stringBuilder.append("&")
        stringBuilder.append("scope="+URLEncoder.encode("email profile openid", Charsets.UTF_8))
        stringBuilder.append("&")
        stringBuilder.append("response_type=code")
        stringBuilder.append("&")
        stringBuilder.append("access_type=offline")
        stringBuilder.append("&")
        stringBuilder.append("prompt=consent")
        stringBuilder.append("&")
        stringBuilder.append("redirect_uri="+URLEncoder.encode(REDIRECT_URI, Charsets.UTF_8))

        return URI.create(stringBuilder.toString())
    }

    /**
     * Authorization Code를 accessToken, refreshToken으로 교환
     */
    fun requestAuthorization(authorizationCode:String) : AuthorizationResponse {

        try {
            val requestBody = okhttp3.FormBody.Builder().run {
                add("grant_type", "authorization_code")
                add("client_id", _clientId)
                add("client_secret", _clientSecretKey)
                add("code", authorizationCode)
                add("redirect_uri", REDIRECT_URI)
                build()
            }

            val request = okhttp3.Request.Builder().run {
                url(_requestTokenUrl)
                header("Content-Type", "application/x-www-form-urlencoded")
                post(requestBody)
                build()
            }

            val response = _client
                .newCall(request)
                .execute()

            val body = response.body.string()

            if( response.isSuccessful == false ) {
                val errorPayload = Gson()
                    .fromJson(
                        body,
                        GoogleOAuthTokenErrorPayload::class.java
                    )

                throw Exception(errorPayload.error)
            }

            val successPayload = Gson().fromJson(
                body,
                GoogleOAuthTokenSuccessPayload::class.java
            )

            val idTokenPayload = this.parsingIdToken(successPayload.idToken)

            return AuthorizationResponse(
                accessToken = successPayload.accessToken,
                refreshToken = successPayload.refreshToken,
                sub = idTokenPayload.sub,
                email = idTokenPayload.email,
                expireSeconds = successPayload.expiresIn.toLong()
            )
        }
        catch (e:Exception) {
            throw e
        }
    }

    /**
     * AccessToken 검증
     */
    fun vaildateAccessToken(accessToken:String) {
        // TODO : NotImplemented
    }

    fun resolveAccessToken(refreshToken:String) : ResolveAccessTokenResponse {
        val requestBody = okhttp3.FormBody.Builder().run {
            add("grant_type", "refresh_token")
            add("client_id", _clientId)
            add("client_secret", _clientSecretKey)
            add("refresh_token", refreshToken)
            build()
        }

        val request = okhttp3.Request.Builder().run {
            url(_requestTokenUrl)
            header("Content-Type", "application/x-www-form-urlencoded")
            post(requestBody)
            build()
        }

        val response = _client
            .newCall(request)
            .execute()

        val body = response.body.string()

        if( response.isSuccessful == false ) {
            val errorPayload = Gson()
                .fromJson(
                    body,
                    GoogleOAuthTokenErrorPayload::class.java
                )

            throw Exception(errorPayload.error)
        }

        val successPayload = Gson().fromJson(
            body,
            GoogleOAuthResolveTokenSuccessPayload::class.java
        )

        return ResolveAccessTokenResponse(
            accessToken = successPayload.accessToken,
            expireSeconds = successPayload.expiresIn.toLong()
        )
    }

    private fun parsingIdToken(idToken:String) : GoogleOauthIdTokenPayload {
        val idTokenElements = idToken.split('.')

        val payloadBytes = Base64.getDecoder().decode(idTokenElements[1])
        val payloadContent = String(payloadBytes, Charsets.UTF_8)

        val payload = Gson().fromJson(
            payloadContent,
            GoogleOauthIdTokenPayload::class.java
        )

        return payload
    }
}