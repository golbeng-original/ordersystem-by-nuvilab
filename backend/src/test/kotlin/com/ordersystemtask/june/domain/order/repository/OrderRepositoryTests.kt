package com.ordersystemtask.june.domain.order.repository

import com.ordersystemtask.june.domain.order.context.OrderDBContext
import com.ordersystemtask.june.domain.order.entity.DeliveryInfo
import com.ordersystemtask.june.domain.order.entity.OrderEntity
import com.ordersystemtask.june.domain.order.entity.OrderStatus
import com.ordersystemtask.june.domain.order.entity.OrderedItemInfo
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import java.util.UUID

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderRepositoryTests @Autowired constructor(
    private val orderDBContext: OrderDBContext,
) {
    private var queryOrderId = ""
    private val sut = OrderRepository(orderDBContext)

    @BeforeAll
    fun setupAll() {
        var order = OrderEntity.new(
            orderUserId = 1L,
            storeId = 1L,
            deliveryInfo = DeliveryInfo(
                address = "서울시 강남구",
                receiverName = "홍길동",
                phoneNumber = "010-1234-5678"
            )
        )

        order.addOrderItem(OrderedItemInfo(
            menuItemId = UUID.randomUUID().toString(),
            menuName = "테스트 메뉴1",
            unitPrice = 1000L,
            quantity = 2,
        ))

        order = sut.save(order)
        queryOrderId = order.orderId
    }

    @Test
    fun `주문 조회`() {
        val order = sut.findById(queryOrderId)

        Assertions.assertNotNull(order)
        Assertions.assertEquals(
            order!!.orderedItems.size,
            1
        )
        Assertions.assertEquals(
            order.orderStatus,
            OrderStatus.Pending
        )
    }

    @Test
    fun `최근 주문 가져오기`() {
        // given
        val latestOrder = OrderEntity.new(
            orderUserId = 1L,
            storeId = 2L,
            deliveryInfo = DeliveryInfo(
                address = "서울시 강남구",
                receiverName = "홍길동",
                phoneNumber = "010-1234-5678"
            )
        )

        latestOrder.addOrderItem(OrderedItemInfo(
            menuItemId = UUID.randomUUID().toString(),
            menuName = "테스트 메뉴",
            unitPrice = 1000L,
            quantity = 2,
        ))

        sut.save(latestOrder)

        // when
        val findOrder = sut.findLatestByOrderUserId(1)

        // then
        Assertions.assertNotNull(findOrder)
        Assertions.assertEquals(
            findOrder!!.storeId,
            2
        )

    }

    @Test
    fun `주문 메뉴 추가`() {
        // given
        var order = sut.findById(queryOrderId)
        order!!.addOrderItem(OrderedItemInfo(
            menuItemId = UUID.randomUUID().toString(),
            menuName = "테스트 메뉴2",
            unitPrice = 2000L,
            quantity = 1,
        ))

        order = sut.save(order)

        // then
        Assertions.assertEquals(
            order.orderedItems.size,
            2
        )
    }
}