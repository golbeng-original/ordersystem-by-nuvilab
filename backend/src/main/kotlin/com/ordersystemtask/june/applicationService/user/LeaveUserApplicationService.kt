package com.ordersystemtask.june.applicationService.user

import com.ordersystemtask.june.domain.store.repository.StoreRepository
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class LeaveUserApplicationService(
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository
) {

    /**
     * 유저가 탈퇴
     */
    fun leave(userId:Long) {
        val user = userRepository.findUserById(userId)
        require(user != null) {
            "User not found"
        }

        require(user.isDeleted == false) {
            "already deleted"
        }

        user.delete()

        val stores = storeRepository.findStoreByOwnerUserId(userId)
        stores.forEach {
            it.delete()
            storeRepository.saveStore(it)
        }

        userRepository.saveUser(user)
    }
}