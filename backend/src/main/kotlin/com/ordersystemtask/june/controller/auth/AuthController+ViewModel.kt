package com.ordersystemtask.june.controller.auth

data class AuthorizeRequest(
    val authorizeCode: String
)

data class AuthorizeResponse(
    val userId:Long,
    val email:String,
    val jwtToken:String
)