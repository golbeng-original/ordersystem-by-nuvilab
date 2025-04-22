package com.ordersystemtask.june.domain.store.entity

enum class StoreStatus {
    Open,
    Closed,
    Resting
}

class MenuItem(
    val menuItemId:Long,
    val name:String,
    val description:String,
    private var _price:Long,
    private var _orderIndex:Int
) {
    val price get() = _price

    val orderIndex get() = _orderIndex

    fun updatePrice(price:Long) {
        require(_price >= 0) {
            "Price must be greater than 0"
        }

        this._price = price
    }
}

class StoreEntity(
    val storeId:Long,
    val ownerUserId:Long,
    val name:String,
    val description:String,
    private var _storeStatus:StoreStatus = StoreStatus.Closed,
    private val _menus:MutableList<MenuItem> = mutableListOf()
) {
    val menus get() = _menus.sortedByDescending { it.orderIndex }
    val storeStatus get() = _storeStatus

    fun addMenuItem(menuItem:MenuItem) {
        _menus.add(menuItem)
    }

    fun removeMenuItem(menuItemId:Long) {
        _menus.removeIf { it.menuItemId == menuItemId }
    }

    fun updateMenuItem(menuItem: MenuItem) {
        val index = _menus.indexOfFirst { it.menuItemId == menuItem.menuItemId }

        require(index != -1) {
            "MenuItem not found [menuItem ID : ${menuItem.menuItemId}]"
        }

        _menus[index] = menuItem
    }

    fun updateStoreStatus(storeStatus:StoreStatus) {
        _storeStatus = storeStatus
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