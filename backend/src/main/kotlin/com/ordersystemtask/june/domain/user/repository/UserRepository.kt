package com.ordersystemtask.june.domain.user.repository

import com.ordersystemtask.june.domain.user.entity.*
import org.springframework.stereotype.Repository

@Repository
class UserRepository {

    private var _idGenerator:Long = 0L

    private val _users = mutableMapOf<Long, UserEntity>()

    private val _authenticationUsers = mutableMapOf<Long, AuthenticationUserEntity>()

    fun findAuthenticationUserById(userId: Long): AuthenticationUserEntity? {
        return AuthenticationUserEntity(
            userId = userId,
            userTraitType = UserTraitType.Normal,
            oauthProvider = OAuthProvider(
                providerType = OAuthProviderType.Google,
                sub = "sub",
                email = "email",
                refreshToken = "refreshToken"
            )
        )
    }

    fun saveAuthenticationUser(authenticationUserEntity: AuthenticationUserEntity) : AuthenticationUserEntity {
        _authenticationUsers[authenticationUserEntity.userId] = authenticationUserEntity
        return authenticationUserEntity
    }

    fun findUserById(userId: Long): UserEntity? {
        return _users.values.find {
            it.userId == userId
        }
    }

    fun findUserByEmail(email: String): UserEntity? {
        return _users.values.find {
            it.email == email
        }
    }

    fun saveUser(userEntity: UserEntity) : UserEntity {

        // 새로 생성할 유저이다.
        if( userEntity.userId == 0L ) {
            val newUserEntity = UserEntity(
                userId = ++_idGenerator,
                email = userEntity.email,
                userTraitType = userEntity.userTraitType,
                _isDeleted = false
            )

            _users[newUserEntity.userId] = newUserEntity
            return newUserEntity
        }

        _users[userEntity.userId] = userEntity
        return userEntity
    }
}