package com.ordersystemtask.june.controller.store

/// region Basis ViewModels

enum class StoreStatusViewModelType {
    Open,
    Closed,
    Resting
}

data class MenuItemViewModel(
    val menuItemId:String,
    val name:String,
    val desc:String,
    val price:Long
)

data class StoreViewModel(
    val storeId: Long,
    val ownerUserId:Long,
    val name: String,
    val desc: String,
    val status:StoreStatusViewModelType,
    val menus:List<MenuItemViewModel> = listOf(),
)

/// endregion

/// region GetStores
data class GetStoresResponse(
    val stores:List<StoreViewModel>
)

/// endregion

/// region GetStore
data class GetStoreResponse(
    val store:StoreViewModel
)
/// endregion

/// region CreateStore
data class CreateStoreRequest(
    val name:String,
    val desc:String
)
data class CreateStoreResponse(
    val store:StoreViewModel
)

/// endregion

/// region UpdateStoreStatus
data class UpdateStoreStatusRequest(
    val status:StoreStatusViewModelType
)

data class UpdateStoreStatusResponse(
    val store:StoreViewModel
)
/// endregion

/// region GetMenuItems
data class GetMenuItemsResponse(
    val menus:List<MenuItemViewModel>
)

/// endregion

/// region AddMenuItems
data class AddMenuRequest(
    val name:String,
    val desc:String,
    val price:Long
)

data class AddMenuResponse(
    val menu:MenuItemViewModel
)

/// endregion