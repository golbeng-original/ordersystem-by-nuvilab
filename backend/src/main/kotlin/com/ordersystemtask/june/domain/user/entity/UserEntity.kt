package com.ordersystemtask.june.domain.user.entity

import java.time.LocalDateTime
import java.util.Date

/*
유저 행동 정리
- 탈퇴
- 닉네임 변경
- 이메일
- refreshToken 관리
 */

enum class OAuthProviderType(val providerName:String) {
    Google("google"),
}

enum class UserTraitType(val traitName:String) {
    Normal("normal"),
    Supplier("supplier"),
}

data class OAuthProvider(
    val providerType:OAuthProviderType,
    val sub:String,
    val email:String,
    val refreshToken:String,
)

class UserEntity(
    val userId:Long,
    val email:String,
    val userTraitType:UserTraitType,
    private val createdAt:LocalDateTime = LocalDateTime.now(),
    private var _isDeleted:Boolean = false,
) {
    val isDeleted:Boolean
        get() = this._isDeleted

    fun delete() {
        if( _isDeleted == true) {
            throw Exception("User is already deleted")
        }

        _isDeleted = true
    }

    companion object {
        fun new(email:String) : UserEntity {
            return UserEntity(
                userId = 0L,
                email = email,
                userTraitType = UserTraitType.Normal
            )
        }
    }
}

class AuthenticationUserEntity(
    val userId:Long,
    val userTraitType:UserTraitType,
    var oauthProvider:OAuthProvider
) {
    fun updateRefreshToken(refreshToken:String) {
        oauthProvider = oauthProvider.copy(
            refreshToken = refreshToken
        )
    }
}
