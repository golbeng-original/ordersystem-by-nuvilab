package com.ordersystemtask.june.applicationService.user

import com.ordersystemtask.june.domain.store.repository.StoreRepository
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LeaveUserApplicationServiceTests {

    private lateinit var userRepository: UserRepository

    private lateinit var sut: LeaveUserApplicationService

    @BeforeEach
    fun setup() {
        userRepository = UserRepository()
        val storeRepository = StoreRepository()

        sut = LeaveUserApplicationService(
            userRepository = userRepository,
            storeRepository = storeRepository
        )

        userRepository.saveUser(
            UserEntity.new("email1@eamil.com")
        )

    }

    @Test
    fun `유저 탈퇴`() {
        sut.leave(1L)
    }

    @Test
    fun `유저 탈퇴 (이미 탈퇴)`() {

        val user = userRepository.findUserById(1L)
        user?.delete()
        userRepository.saveUser(user!!)

        Assertions.assertThrows(IllegalArgumentException::class.java) {
            sut.leave(2L)
        }
    }
}