package com.ordersystemtask.june.controller.user

enum class UserTraitViewModelType {
    Normal,
    Seller
}

data class UserViewModel(
    val userId:Long,
    val email:String,
    val userTrait:UserTraitViewModelType,
)

data class GetUserResponse(
    val user: UserViewModel
)

data class UpdateUserRequest(
    val userTrait:UserTraitViewModelType
)

data class UpdateUserResponse(
    val user: UserViewModel
)