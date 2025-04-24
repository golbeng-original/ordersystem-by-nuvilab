package com.ordersystemtask.june.applicationService.store

import com.ordersystemtask.june.domain.store.entity.StoreEntity
import com.ordersystemtask.june.domain.store.repository.StoreRepository
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StoreApplicationServiceTests @Autowired constructor(
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository
) {


    private lateinit var sut:StoreApplicationService

    @BeforeEach
    fun setup() {


        sut = StoreApplicationService(
            storeRepository = storeRepository,
            userRepository = userRepository
        )

        userRepository.saveUser(
            UserEntity.new("email1@email.com")
        )

        val user = userRepository.saveUser(
            UserEntity.new("email2@email.com")
        )

        user.changeUserTrait(UserTraitType.Seller)
        userRepository.saveUser(user)


        val sellerUser = userRepository.saveUser(
            UserEntity.new("email3@email.com")
        )

        storeRepository.saveStore(
            StoreEntity.new(
                ownerUserId = sellerUser.userId,
                name = "가게 이름",
                description = "가게 설명"
            )
        )
    }

    @Test
    fun `가개 개설 성공`() {
        val output = sut.createStore(
            StoreCreationParam(
                ownerUserId = 2L,
                storeName = "가게 이름",
                storeDescription = "가게 설명"
            )
        )

        Assertions.assertTrue(output.storeEntity.storeId >= 1L)
        Assertions.assertEquals(output.storeEntity.name, "가게 이름")

    }

    @Test
    fun `가게 개설 실패(판매자가 아니다)`() {
        Assertions.assertThrows(IllegalArgumentException::class.java) {
            sut.createStore(
                StoreCreationParam(
                    ownerUserId = 1L,
                    storeName = "가게 이름",
                    storeDescription = "가게 설명"
                )
            )
        }
    }

    @Test
    fun `가게에 메뉴 추가`() {

        // given
        val menuItemUnit = StoreUpdateMenuItemUnit(
            name = "메뉴 이름 1",
            description = "메뉴 설명 1",
            price = 1000,
        )

        // when
        val output = sut.addMenuItems(
            AddMenuItemsParam(
                storeId = 1L,
                menuItemUnit = menuItemUnit
            )
        )

        // then
        val store = output.store
        Assertions.assertEquals(store.menus.size, 1)
    }
}