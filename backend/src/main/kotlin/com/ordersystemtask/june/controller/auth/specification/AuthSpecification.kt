package com.ordersystemtask.june.controller.auth.specification

import com.ordersystemtask.june.applicationService.clients.GoogleAuthClient
import com.ordersystemtask.june.domain.user.entity.AuthenticationUserEntity
import com.ordersystemtask.june.domain.user.entity.OAuthProvider
import com.ordersystemtask.june.domain.user.entity.OAuthProviderType
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.repository.UserRepository
import com.ordersystemtask.june.security.JWTGenerator
import org.springframework.stereotype.Component

data class ProcessAuthResult(
    val user: UserEntity,
    val jwtToken:String
)

@Component
class AuthSpecification(
    private val userRepository: UserRepository,
    private val jwtGenerator: JWTGenerator,
    private val googleAuthClient: GoogleAuthClient
) {

    // 유저 반환하기
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
            userTraitType = user.userTraitType,
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