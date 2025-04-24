package com.ordersystemtask.june.domain.user.repository

import com.ordersystemtask.june.domain.user.context.UserDBContext
import com.ordersystemtask.june.domain.user.context.models.OAuthProviderModel
import com.ordersystemtask.june.domain.user.context.models.ReceiveAddressModel
import com.ordersystemtask.june.domain.user.context.models.UserModel
import com.ordersystemtask.june.domain.user.entity.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository

@Repository
class UserRepository @Autowired constructor(
    private val context: UserDBContext
) {
    fun findAuthenticationUserById(userId: Long): AuthenticationUserEntity? {

        val userModel = context.findForAuthentication(userId)
        if( userModel === null ) {
            return null
        }

        val oauthProvider = userModel.oauthProvider
        if( oauthProvider == null ) {
            return null
        }

        return convertToAuthenticationUserEntity(userModel)
    }

    fun saveAuthenticationUser(authenticationUserEntity: AuthenticationUserEntity) : AuthenticationUserEntity {

        var userModel = context.findByUserId(authenticationUserEntity.userId)
        require(userModel != null ) {
            "UserModel is null. userId: ${authenticationUserEntity.userId}"
        }

        val oauthProvider = authenticationUserEntity.oauthProvider

        if( userModel.oauthProvider != null ) {
            userModel.oauthProvider!!.refreshToken = oauthProvider.refreshToken

            return convertToAuthenticationUserEntity(userModel)
        }
        else {
            userModel.oauthProvider = OAuthProviderModel(
                user = userModel,
                providerType = oauthProvider.providerType,
                sub = oauthProvider.sub,
                email = oauthProvider.email,
                refreshToken = oauthProvider.refreshToken
            )
        }

        userModel = context.save(userModel)

        return convertToAuthenticationUserEntity(userModel)
    }

    fun findUserById(userId: Long): UserEntity? {
        val model = context.findByUserId(userId)
        if( model == null ) {
            return null
        }

        return convertToUserEntity(model)
    }

    fun findUserByEmail(email: String): UserEntity? {
        val model = context.findByEmail(email)
        if( model == null ) {
            return null
        }

        return convertToUserEntity(model)
    }

    fun saveUser(userEntity: UserEntity) : UserEntity {
        var model = convertToUserModel(userEntity)
        model = context.save(model)

        return convertToUserEntity(model)
    }

    private fun convertToUserEntity(model: UserModel) : UserEntity {
        return UserEntity(
            userId = model.userId!!,
            email = model.email,
            _userTrait = model.trait,
            _isDeleted = model.isDeleted,
            _deletedAt = model.deletedAt,
            _receiveAddresses = model.receiveAddresses.map {
                ReceiveAddressEntity(
                    receiveAddressId = it.receiveAddressId,
                    ownerName = it.ownerName,
                    address = it.address,
                    phoneNumber = it.phoneNumber
                )
            }.toMutableList()
        )
    }

    private fun convertToUserModel(entity: UserEntity) : UserModel {
        val model = UserModel(
            userId = if(entity.userId > 0L) entity.userId else null,
            email = entity.email,
            trait = entity.userTrait,
            createdAt = entity.createdAt,
            isDeleted = entity.isDeleted,
            deletedAt = entity.deletedAt,
            receiveAddresses = entity.receiveAddresses.map {
                ReceiveAddressModel(
                    receiveAddressId = it.receiveAddressId,
                    ownerName = it.ownerName,
                    address = it.address,
                    phoneNumber = it.phoneNumber
                )
            }.toMutableList()
        )

        return model
    }

    private fun convertToAuthenticationUserEntity(model: UserModel) : AuthenticationUserEntity {

        val oauthProvider = model.oauthProvider!!

        return AuthenticationUserEntity(
            userId = model.userId!!,
            userTrait = model.trait,
            oauthProvider = OAuthProvider(
                providerType = oauthProvider.providerType,
                sub = oauthProvider.sub,
                email = oauthProvider.email,
                refreshToken = oauthProvider.refreshToken
            )
        )
    }
}