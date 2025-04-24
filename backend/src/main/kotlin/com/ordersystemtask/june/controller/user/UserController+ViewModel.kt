package com.ordersystemtask.june.controller.user

/// region commonViewModel
enum class UserTraitViewModelType {
    Normal,
    Seller
}

data class ReceiveAddressViewModel(
    val addressId:String,
    val name:String,
    val address:String,
    val phoneNumber:String,
)

data class UserViewModel(
    val userId:Long,
    val email:String,
    val userTrait:UserTraitViewModelType,
)
/// endregion

/// region GetUser
data class GetUserResponse(
    val user: UserViewModel
)
/// endregion

/// region UpdateUser
data class UpdateUserRequest(
    val userTrait:UserTraitViewModelType
)

data class UpdateUserResponse(
    val user: UserViewModel
)
/// endregion

/// region GetReceiveAddresses
data class GetReceiveAddressesResponse(
    val addresses:List<ReceiveAddressViewModel>
)
/// endregion

/// region AddReceiveAddress
data class AddReceiveAddressRequest(
    val name:String,
    val address:String,
    val phoneNumber:String,
)

data class AddReceiveAddressResponse(
    val address:ReceiveAddressViewModel
)
/// endregion