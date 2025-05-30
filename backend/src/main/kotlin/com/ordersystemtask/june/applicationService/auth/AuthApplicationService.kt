package com.ordersystemtask.june.applicationService.auth

import com.ordersystemtask.june.clients.GoogleAuthClient
import com.ordersystemtask.june.domain.user.entity.AuthenticationUserEntity
import com.ordersystemtask.june.domain.user.entity.OAuthProvider
import com.ordersystemtask.june.domain.user.entity.OAuthProviderType
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.repository.UserRepository
import com.ordersystemtask.june.security.JWTGenerator
import org.springframework.stereotype.Service
import java.net.URI

data class ProcessAuthResult(
    val user: UserEntity,
    val jwtToken:String
)

@Service
class AuthApplicationService(
    private val userRepository: UserRepository,
    private val jwtGenerator: JWTGenerator,
    private val googleAuthClient: GoogleAuthClient
) {

    fun getGoogleOauthLoginUrl() : URI {
        return googleAuthClient.generateAuthorizationUrl()
    }

    /**
     * OAuth 인증 코드로 인증 처리
     * - 자동 회원가입 처리
     */
    fun processAuthByOAuthAuthenticationCode(code:String) : ProcessAuthResult {
        val response = googleAuthClient.requestAuthorization(code)

        var entity = userRepository.findUserByEmail(response.email)
        if( entity == null ) {
            entity = createUser(response.email, response.sub, response.refreshToken)
        }
        else {
            updateRefreshToken(entity.userId, response.refreshToken)
        }

        return ProcessAuthResult(
            user = entity,
            jwtToken = jwtGenerator.generate(
                userId = entity.userId,
                accessToken = response.accessToken,
                expireSeconds = response.expireSeconds
            )
        )
    }

    // 유저 생성
    private fun createUser(email:String, sub:String, refreshToken:String) : UserEntity {

        var user = UserEntity.new(email)
        user = userRepository.saveUser(user)

        val authenticationUser = AuthenticationUserEntity(
            userId = user.userId,
            userTrait = user.userTrait,
            oauthProvider = OAuthProvider(
                providerType = OAuthProviderType.Google,
                sub = sub,
                email = email,
                refreshToken = refreshToken
            )
        )
        userRepository.saveAuthenticationUser(authenticationUser)


        return user
    }

    private fun updateRefreshToken(userId:Long, refreshToken:String) {
        val authenticationUser = userRepository.findAuthenticationUserById(userId);
        if( authenticationUser == null ) {
            throw IllegalArgumentException("Authtication User not found")
        }

        authenticationUser.updateRefreshToken(refreshToken)
        userRepository.saveAuthenticationUser(authenticationUser)
    }
}