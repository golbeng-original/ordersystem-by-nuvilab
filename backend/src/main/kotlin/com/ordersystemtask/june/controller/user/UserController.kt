package com.ordersystemtask.june.controller.user

import com.ordersystemtask.june.applicationService.user.AddReceiveAddressData
import com.ordersystemtask.june.applicationService.user.LeaveUserApplicationService
import com.ordersystemtask.june.applicationService.user.UserApplicationService
import com.ordersystemtask.june.domain.user.entity.ReceiveAddressEntity
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import com.ordersystemtask.june.security.JwtUserDetails
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/user")
class UserController(
    private val userApplicationService: UserApplicationService,
    private val leaveUserApplicationService: LeaveUserApplicationService
) {
    @GetMapping("")
    fun getUser(
        @AuthenticationPrincipal userDetails: JwtUserDetails
    ) : GetUserResponse {
        val user = userApplicationService.getUser(userDetails.userId)

        return GetUserResponse(
            convertUserToViewModel(user)
        )
    }


    @PatchMapping("")
    fun updateUser(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
        @RequestBody request:UpdateUserRequest
    ) : UpdateUserResponse {
        val userTrait = when(request.userTrait) {
            UserTraitViewModelType.Normal -> UserTraitType.Normal
            UserTraitViewModelType.Seller -> UserTraitType.Seller
        }

        val user = userApplicationService.changeUserTrait(userDetails.userId, userTrait)

        return UpdateUserResponse(
            convertUserToViewModel(user)
        )
    }

    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId:Long
    ) : GetUserResponse {
        val user = userApplicationService.getUser(userId)

        return GetUserResponse(
            convertUserToViewModel(user)
        )
    }

    @GetMapping("/addresses")
    fun getUserAddresses(
        @AuthenticationPrincipal userDetails: JwtUserDetails
    ) : GetReceiveAddressesResponse {
        val addresses = userApplicationService.getReceiveAddresses(
            userDetails.userId
        )

        val addressViewModels = addresses.map {
            convertReceiveAddressToViewModel(it)
        }

        return GetReceiveAddressesResponse(
            addressViewModels
        )
    }

    @PostMapping("/addresses")
    fun addUserAddress(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
        @RequestBody request: AddReceiveAddressRequest
    ) : AddReceiveAddressResponse {
        val newAddress = userApplicationService.addReceiveAddress(
            userDetails.userId,
            AddReceiveAddressData(
                name = request.name,
                address = request.address,
                phoneNumber = request.phoneNumber
            )
        )

        return AddReceiveAddressResponse(
            convertReceiveAddressToViewModel(newAddress)
        )
    }


    private fun convertUserToViewModel(user:UserEntity) : UserViewModel {
        return UserViewModel(
            userId = user.userId,
            email = user.email,
            userTrait = when(user.userTrait) {
                UserTraitType.Normal -> UserTraitViewModelType.Normal
                UserTraitType.Seller -> UserTraitViewModelType.Seller
            }
        )
    }

    private fun convertReceiveAddressToViewModel(address: ReceiveAddressEntity) : ReceiveAddressViewModel {
        return ReceiveAddressViewModel(
            addressId = address.receiveAddressId,
            name = address.ownerName,
            address = address.address,
            phoneNumber = address.phoneNumber
        )
    }
}