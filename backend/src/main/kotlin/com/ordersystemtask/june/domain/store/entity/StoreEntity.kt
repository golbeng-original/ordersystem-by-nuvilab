package com.ordersystemtask.june.domain.store.entity

import org.springframework.cglib.core.Local
import java.time.LocalDateTime
import java.util.UUID

enum class StoreStatus {
    None,
    Open,
    Closed,
    Resting
}

data class MenuItemEntity(
    val menuItemId:String,
    val name:String,
    val description:String,
    val price:Long,
    val orderIndex:Int = 0
) {

    companion object {
        fun new(name:String, description:String, price:Long) : MenuItemEntity {
            return MenuItemEntity(
                menuItemId = UUID.randomUUID().toString(),
                name = name,
                description = description,
                price = price
            )
        }
    }
}

class StoreEntity(
    val storeId:Long,
    val ownerUserId:Long,
    val name:String,
    val description:String,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    var _deletedAt:LocalDateTime? = null,
    private var _storeStatus:StoreStatus = StoreStatus.Closed,
    private val _menus:MutableList<MenuItemEntity> = mutableListOf(),
    private var _isDeleted:Boolean = false,
) {
    val menus get() = _menus.sortedBy { it.orderIndex }
    val storeStatus get() = _storeStatus

    val isDeleted get() = _isDeleted
    val deletedAt get() = this._deletedAt

    fun addMenuItem(menuItem:MenuItemEntity) {
        _menus.add(
            menuItem.copy(orderIndex = _menus.size + 1)
        )
    }

    fun addMenuItems(menuItems:List<MenuItemEntity>) {
        _menus.addAll(menuItems)

        _menus.forEachIndexed { index, menuItem ->
            _menus[index] = menuItem.copy(orderIndex = index + 1)
        }
    }

    fun removeMenuItem(menuItemId:String) {
        _menus.removeIf { it.menuItemId == menuItemId }

        _menus.forEachIndexed { index, menuItem ->
            _menus[index] = menuItem.copy(orderIndex = index + 1)
        }
    }

    fun removeAllMenuItem() {
        _menus.clear()
    }

    fun updateMenuItem(menuItem: MenuItemEntity) {
        val index = _menus.indexOfFirst { it.menuItemId == menuItem.menuItemId }

        require(index != -1) {
            "MenuItem not found [menuItem ID : ${menuItem.menuItemId}]"
        }

        _menus[index] = menuItem
    }

    fun changeStoreStatus(storeStatus:StoreStatus) {
        _storeStatus = storeStatus
    }

    fun delete() {
        _isDeleted = true
        _deletedAt = LocalDateTime.now()
    }

    companion object {
        fun new(ownerUserId:Long, name:String, description:String) : StoreEntity {
            return StoreEntity(
                storeId = 0L,
                ownerUserId = ownerUserId,
                name = name,
                description = description,
            )
        }
    }
}