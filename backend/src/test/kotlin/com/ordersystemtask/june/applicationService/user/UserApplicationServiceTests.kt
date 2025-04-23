package com.ordersystemtask.june.applicationService.user

import com.ordersystemtask.june.domain.store.repository.StoreRepository
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.junit.jupiter.api.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserApplicationServiceTests {

    private lateinit var sut:UserApplicationService



    @BeforeAll
    fun setupAll() {
        val userRepository = UserRepository()
        val storeRepository = StoreRepository()

        sut = UserApplicationService(
            userRepository = userRepository,
        )

        userRepository.saveUser(
            UserEntity.new("email1@eamil.com")
        )

        var user = userRepository.saveUser(
            UserEntity.new("email2@eamil.com")
        )
        user.changeUserTrait(UserTraitType.Seller)
        userRepository.saveUser(user)
    }

    @Test
    fun `유저를 판매자로 승격`() {

        val user = sut.changeToSeller(1L)

        Assertions.assertEquals(user.userTrait, UserTraitType.Seller)
    }
}