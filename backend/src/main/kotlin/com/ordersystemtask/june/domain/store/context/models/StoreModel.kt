package com.ordersystemtask.june.domain.store.context.models

import com.ordersystemtask.june.domain.store.entity.StoreStatus
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity(name = "Store")
@Table(name = "store")
class StoreModel(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    val storeId:Long? = null,

    @Column(name = "owner_user_id")
    val ownerUserId:Long = 0L,

    @Column(nullable = false)
    val name:String = "",

    @Lob
    @Column(nullable = false)
    val description:String = "",

    @Enumerated(EnumType.STRING)
    @Column(name = "store_status", nullable = false)
    var storeStatus: StoreStatus = StoreStatus.None,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime,

    @Column(name = "is_deleted", nullable = false)
    var isDeleted: Boolean = false,

    @Column(name = "deleted_at")
    var deletedAt: LocalDateTime?,

    @OneToMany(fetch = FetchType.EAGER, targetEntity = MenuItemModel::class, cascade = [CascadeType.ALL], orphanRemoval = true)
    @JoinColumn(name = "store_id")
    var menuItems: MutableList<MenuItemModel> = mutableListOf(),

)

@Entity(name = "MenuItem")
@Table(name = "menu_item")
class MenuItemModel(
    @Id
    @Column(name = "menu_item_id", columnDefinition = "VARCHAR(36)")
    val menuItemId:String = "",

    @Column(nullable = false)
    val name:String = "",

    @Lob
    @Column(nullable = false)
    val description:String = "",

    @Column(nullable = false)
    val price:Long = 0L,

    @Column(name = "order_index", nullable = false)
    var orderIndex:Int = 0
)
