package com.ordersystemtask.june.applicationService.order

import com.ordersystemtask.june.domain.order.entity.OrderEntity
import com.ordersystemtask.june.domain.order.entity.OrderStatus
import com.ordersystemtask.june.domain.order.entity.PaymentMethod
import com.ordersystemtask.june.domain.order.repository.OrderRepository
import com.ordersystemtask.june.domain.store.entity.MenuItemEntity
import com.ordersystemtask.june.domain.store.entity.StoreEntity
import com.ordersystemtask.june.domain.store.entity.StoreStatus
import com.ordersystemtask.june.domain.store.repository.StoreRepository
import com.ordersystemtask.june.domain.user.entity.ReceiveAddressEntity
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.junit.jupiter.api.Assertions

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrderApplicationServiceTests {

    private lateinit var orderRepository: OrderRepository

    private lateinit var sut:OrderApplicationService

    private lateinit var normalUser: UserEntity
    private lateinit var sellerUser:UserEntity

    private lateinit var store: StoreEntity

    @BeforeEach
    fun setup() {

        orderRepository = OrderRepository()
        val storeRepository = StoreRepository()
        val userRepository = UserRepository()

        sut = OrderApplicationService(
            orderRepository = orderRepository,
            userRepository = userRepository,
            storeRepository = storeRepository
        )

        // 일반 유저 생성
        normalUser = userRepository.saveUser(UserEntity.new("email1@gamil.com"))
        normalUser.addReceiveAddress(
            ReceiveAddressEntity.new(
                address = "서울시 강남구",
                ownerName = "홍길동",
                phoneNumber = "010-1234-5678"
            )
        )
        userRepository.saveUser(normalUser)

        // 가게 유저 생성
        sellerUser = userRepository.saveUser(UserEntity.new("email2@gamile.com"))
        sellerUser.changeUserTrait(UserTraitType.Seller)
        sellerUser = userRepository.saveUser(sellerUser)

        // 가게 등록
        store = storeRepository.saveStore(
            StoreEntity.new(
                ownerUserId = sellerUser.userId,
                name = "가게1",
                description = "가게 설명",
            )
        )

        val menuItems = listOf(
            MenuItemEntity.new(
                name = "메뉴 1",
                description = "메뉴 설명 1",
                price = 1000,
            ),
            MenuItemEntity.new(
                name = "메뉴 2",
                description = "메뉴 설명 2",
                price = 2000,
            ),
            MenuItemEntity.new(
                name = "메뉴 3",
                description = "메뉴 설명 3",
                price = 3000,
            ),
        )
        store.addMenuItems(menuItems)
        store.changeStoreStatus(StoreStatus.Open)

        store = storeRepository.saveStore(store)
    }

    private fun prepareOrder() : OrderEntity {
        val orderEntity = sut.order(
            OrderParam(
                userId = normalUser.userId,
                storeId = store.storeId,
                receiveAddressId = normalUser.receiveAddresses[0].receiveAddressId,
                orderMenuItemUnits = listOf(
                    OrderMenuItemUnit(
                        menuItemId = store.menus[0].menuItemId,
                        quantity = 1
                    ),
                    OrderMenuItemUnit(
                        menuItemId = store.menus[1].menuItemId,
                        quantity = 2
                    )
                )
            )
        )

        return orderEntity
    }

    @Test
    fun `주문 생성`() {
        // given, when
        val order = prepareOrder()

        // then
        Assertions.assertEquals(
            order.orderStatus,
            OrderStatus.Pending
        )
        Assertions.assertEquals(
            order.orderedItems.size,
            2
        )
        Assertions.assertEquals(
            order.totalPrice,
            store.menus[0].price * 1 + store.menus[1].price * 2
        )
    }

    @Test
    fun `주문 취소`() {
        // given
        var order = prepareOrder()

        // when
        order = sut.cancelOrder(order.orderId)

        // then
        Assertions.assertEquals(
            order.orderStatus,
            OrderStatus.Canceled
        )
    }

    @Test
    fun `결제 완료`() {
        // given
        var order = prepareOrder()

        // when
        order = sut.tryPay(
            orderId = order.orderId,
            method = PaymentMethod.Card
        )

        // then
        Assertions.assertEquals(
            order.paymentInfo.paymentMethod,
            PaymentMethod.Card
        )

        Assertions.assertEquals(
            order.paymentInfo.paid,
            true
        )
    }

    @Test
    fun `주문 받음`() {
        // given
        var order = prepareOrder()
        order = sut.tryPay(
            orderId = order.orderId,
            method = PaymentMethod.Card
        )

        // when
        order = sut.acceptOrder(order.orderId, sellerUser.userId)

        // then
        Assertions.assertEquals(
            order.orderStatus,
            OrderStatus.Accepted
        )
    }

    @Test
    fun `배달 완료`() {
        // given
        var order = prepareOrder()

        order = sut.tryPay(order.orderId, PaymentMethod.Card)
        order = sut.acceptOrder(order.orderId, sellerUser.userId)

        // when
        order = sut.completeDelivery(order.orderId)

        // then
        Assertions.assertEquals(
            order.orderStatus,
            OrderStatus.Completed
        )
    }
}