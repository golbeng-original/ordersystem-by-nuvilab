package com.ordersystemtask.june.domain.order.context.models

import com.ordersystemtask.june.domain.order.entity.DeliveryInfo
import com.ordersystemtask.june.domain.order.entity.OrderStatus
import com.ordersystemtask.june.domain.order.entity.PaymentMethod
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(name = "Order")
@Table(name = "`order`")
class OrderModel(

    @Id
    @Column(name = "order_id", columnDefinition = "VARCHAR(36)")
    val orderId:String,

    @Column(name = "store_id")
    val storeId:Long,

    @Column(name = "order_user_id")
    val orderUserId:Long,

    @Column(name = "order_status")
    var orderStatus: OrderStatus,

    @Column(name = "ordered_at")
    var orderedAt: LocalDateTime,

    @Column(name = "completed_at")
    var completedAt: LocalDateTime?,

    @Embedded
    var paymentInfo: PaymentInfoModel,

    @Embedded
    var deliveryInfo: DeliveryInfoModel,

    @OneToMany(mappedBy = "order", fetch = FetchType.EAGER, targetEntity = OrderedItemModel::class, cascade = [CascadeType.ALL], orphanRemoval = true)
    var orderedItems: MutableList<OrderedItemModel> = mutableListOf()
)

@Embeddable
class PaymentInfoModel(

    @Column(name = "payment_method")
    val paymentMethod: PaymentMethod,

    @Column(name = "paid", nullable = false)
    val paid: Boolean,

    @Column(name = "paid_at")
    val paidAt: LocalDateTime?,
)

@Embeddable
class DeliveryInfoModel(

    @Column(name = "receiver_name")
    val receiverName:String,

    @Column(name = "address")
    val address: String,

    @Column(name = "phone_number")
    val phoneNumber: String,
)

@Embeddable
class OrderedItemId(
    @Column(name = "order_id")
    val orderId:String,

    @Column(name = "menu_item_id")
    val menuItemId:String
)

@Entity(name = "OrderedItem")
@Table(name = "ordered_item")
class OrderedItemModel(

    @EmbeddedId
    val id: OrderedItemId,

    @ManyToOne
    @MapsId("orderId")
    @JoinColumn(name = "order_id")
    val order: OrderModel,

    @Column(name = "menu_name")
    val menuName:String,

    @Column(name = "unit_price")
    val unitPrice:Long,

    @Column(name = "quantity")
    val quantity:Int,
)


