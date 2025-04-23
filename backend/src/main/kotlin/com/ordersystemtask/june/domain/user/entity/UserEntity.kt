package com.ordersystemtask.june.domain.user.entity

import org.springframework.cglib.core.Local
import java.time.LocalDateTime
import java.util.UUID

enum class UserTraitType(val traitName:String) {
    Normal("normal"),
    Seller("seller")
}

class ReceiveAddressEntity (
    val receiveAddressId:String,
    val ownerName:String,
    val address:String,
    val phoneNumber:String
) {
    companion object {
        fun new(
            ownerName:String,
            address:String,
            phoneNumber:String
        ) : ReceiveAddressEntity {
            return ReceiveAddressEntity(
                receiveAddressId = UUID.randomUUID().toString(),
                ownerName = ownerName,
                address = address,
                phoneNumber = phoneNumber
            )
        }
    }
}

class UserEntity(
    val userId:Long,
    val email:String,
    var _userTrait:UserTraitType = UserTraitType.Normal,
    val createdAt:LocalDateTime = LocalDateTime.now(),
    var _deletedAt:LocalDateTime? = null,
    private val _receiveAddresses:MutableList<ReceiveAddressEntity> = mutableListOf(),
    private var _isDeleted:Boolean = false,
) {
    val userTrait get() = this._userTrait
    val isDeleted get() = this._isDeleted
    val deletedAt get() = this._deletedAt
    val receiveAddresses get() = _receiveAddresses.toList()

    fun changeUserTrait(userTrait:UserTraitType) {
        this._userTrait = userTrait
    }

    fun addReceiveAddress(receiveAddress:ReceiveAddressEntity) {
        _receiveAddresses.add(receiveAddress)
    }

    fun removeReceiveAddress(receiveAddressId:String) {
        _receiveAddresses.removeIf { it.receiveAddressId == receiveAddressId }
    }

    fun delete() {
        if( _isDeleted == true) {
            throw Exception("User is already deleted")
        }

        _isDeleted = true
        _deletedAt = LocalDateTime.now()
    }

    companion object {
        fun new(email:String) : UserEntity {
            return UserEntity(
                userId = 0L,
                email = email
            )
        }
    }
}


enum class OAuthProviderType {
    None,
    Google
}

data class OAuthProvider(
    val providerType:OAuthProviderType,
    val sub:String,
    val email:String,
    val refreshToken:String,
)


class AuthenticationUserEntity(
    val userId:Long,
    val userTrait:UserTraitType,
    var oauthProvider:OAuthProvider
) {
    fun updateRefreshToken(refreshToken:String) {
        oauthProvider = oauthProvider.copy(
            refreshToken = refreshToken
        )
    }
}
