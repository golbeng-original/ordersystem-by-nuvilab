package com.ordersystemtask.june.applicationService.user

import com.ordersystemtask.june.domain.store.repository.StoreRepository
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserApplicationService(
    private val userRepository: UserRepository
) {
    /**
     * 유저를 판매자로 승격
     */
    fun changeToSeller(userId:Long) : UserEntity {

        val user = userRepository.findUserById(userId)
        require(user != null) {
            "User not found"
        }

        user.changeUserTrait(UserTraitType.Seller)

        return userRepository.saveUser(user)
    }

    /**
     * 유저의 배달 받을 주소를 변경
     */
    fun updateReceiveAddress(userId:Long) {

    }
}