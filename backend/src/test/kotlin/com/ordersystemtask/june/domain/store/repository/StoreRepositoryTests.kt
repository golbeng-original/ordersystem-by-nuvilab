package com.ordersystemtask.june.domain.store.repository

import com.ordersystemtask.june.domain.store.context.StoreDBContext
import com.ordersystemtask.june.domain.store.entity.MenuItemEntity
import com.ordersystemtask.june.domain.store.entity.StoreEntity
import com.ordersystemtask.june.domain.store.entity.StoreStatus
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StoreRepositoryTests @Autowired constructor(
    private val storeDBContext: StoreDBContext,
    private val transactionManager: PlatformTransactionManager
) {

    private var queryStoreId = 0L

    private val sut = StoreRepository(storeDBContext)

    @BeforeAll
    fun setupAll() {

        val status = transactionManager.getTransaction(DefaultTransactionDefinition())

        try {
            var storeEntity = StoreEntity.new(
                ownerUserId = 1L,
                name = "테스트 상점",
                description = "테스트 상점 설명"
            )

            storeEntity.addMenuItem(
                menuItem = MenuItemEntity.new(
                    name = "테스트 메뉴1",
                    description = "테스트 메뉴1 설명",
                    price = 1000L
                )
            )

            storeEntity = sut.saveStore(storeEntity)

            queryStoreId = storeEntity.storeId

            transactionManager.commit(status)
        }
        catch (e: Exception) {
            transactionManager.rollback(status)
        }
    }


    @Test
    fun `상점 조회`() {
        // given, when
        val store = sut.findStoreById(queryStoreId)

        // then
        Assertions.assertNotNull(store)
        Assertions.assertEquals(
            store!!.name,
            "테스트 상점"
        )
        Assertions.assertEquals(
            store.menus.size,
            1
        )

    }

    @Test
    fun `상점 메뉴 추가`() {

        // given
        var store = sut.findStoreById(queryStoreId)

        // when
        store!!.addMenuItem(MenuItemEntity.new(
            name = "테스트 메뉴2",
            description = "테스트 메뉴2 설명",
            price = 2000L
        ))

        store = sut.saveStore(store)

        // then
        Assertions.assertEquals(
            store.menus.size,
            2
        )

        Assertions.assertEquals(
            store.menus[0].name,
            "테스트 메뉴1"
        )
        Assertions.assertEquals(
            store.menus[1].name,
            "테스트 메뉴2"
        )
    }
}