package com.ordersystemtask.june.applicationService.user

import com.ordersystemtask.june.domain.user.entity.ReceiveAddressEntity
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

data class AddReceiveAddressData(
    val name:String,
    val address:String,
    val phoneNumber:String,
)

@Service
class UserApplicationService(
    private val userRepository: UserRepository
) {

    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    /**
     * 유저 가져오기
     */
    @Transactional(readOnly = true)
    fun getUser(userId:Long) : UserEntity {
        try {
            val user = userRepository.findUserById(userId)
            require(user != null) {
                "User not found"
            }

            return user
        }
        catch (e: Exception) {
            logger.error(e.message)
            throw e
        }

    }

    /**
     * 유저를 판매자로 승격
     */
    @Transactional
    fun changeUserTrait(userId:Long, userTraitType: UserTraitType) : UserEntity {
        try {
            val user = userRepository.findUserById(userId)
            require(user != null) {
                "User not found"
            }

            user.changeUserTrait(userTraitType)

            return userRepository.saveUser(user)
        }
        catch(e:Exception) {
            logger.error(e.message)
            throw e
        }
    }

    @Transactional(readOnly = true)
    fun getReceiveAddresses(userId:Long) : List<ReceiveAddressEntity> {
        try {
            val user = userRepository.findUserById(userId)
            require(user != null) {
                "User not found"
            }

            return user.receiveAddresses
        }
        catch (e: Exception) {
            logger.error(e.message)
            throw e
        }
    }

    /**
     * 유저의 배달 받을 주소 추가
     */
    @Transactional
    fun addReceiveAddress(
        userId:Long,
        addReceiveAddressData:AddReceiveAddressData
    ) : ReceiveAddressEntity {
        try {
            var user = userRepository.findUserById(userId)
            require(user != null) {
                "User not found"
            }

            val previousAddressIds = user.receiveAddresses.map { it.receiveAddressId }

            user.addReceiveAddress(
                ReceiveAddressEntity.new(
                    ownerName = addReceiveAddressData.name,
                    address = addReceiveAddressData.address,
                    phoneNumber = addReceiveAddressData.phoneNumber
                )
            )

            user = userRepository.saveUser(user)

            val newAddress =  user.receiveAddresses
                .filterNot { it.receiveAddressId in previousAddressIds }
                .firstOrNull()

            require(newAddress != null) {
                "주소지 추가가 안됨"
            }

            return newAddress
        }
        catch (e: Exception) {
            logger.error(e.message)
            throw e
        }


    }
}