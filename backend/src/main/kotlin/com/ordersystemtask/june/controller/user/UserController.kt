package com.ordersystemtask.june.controller.user

import com.ordersystemtask.june.applicationService.user.LeaveUserApplicationService
import com.ordersystemtask.june.applicationService.user.UserApplicationService
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import com.ordersystemtask.june.domain.user.repository.UserRepository
import com.ordersystemtask.june.security.JwtUserDetails
import org.apache.coyote.Response
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
    private val LOGGER = LoggerFactory.getLogger(this.javaClass.name)

    @GetMapping("")
    fun getUser(
        @AuthenticationPrincipal userDetails: JwtUserDetails
    ) : ResponseEntity<GetUserResponse> {
       try {
            val user = userApplicationService.getUser(userDetails.user.userId)

            val userViewModel = convertUserToViewModel(user)

            return ResponseEntity
                .ok()
                .body(
                    GetUserResponse(userViewModel)
                )
       }
       catch (e:Exception) {
           LOGGER.error(e.message)

           return ResponseEntity
               .internalServerError()
               .build()
       }
    }


    @PatchMapping("")
    fun updateUser(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
        @RequestBody request:UpdateUserRequest
    ) : ResponseEntity<UpdateUserResponse> {
        try {
            val user = userApplicationService.changeToSeller(userDetails.user.userId)

            val userViewModel = convertUserToViewModel(user)

            return ResponseEntity
                .ok()
                .body(
                    UpdateUserResponse(userViewModel)
                )
        }
        catch(e:Exception) {
            LOGGER.error(e.message)

            return ResponseEntity
                .internalServerError()
                .build()
        }
    }

    @GetMapping("/{userId}")
    fun getUser(
        @PathVariable userId:Long
    ) : ResponseEntity<GetUserResponse> {
        try {
            val user = userApplicationService.getUser(userId)

            val userViewModel = convertUserToViewModel(user)

            return ResponseEntity
                .ok()
                .body(
                    GetUserResponse(userViewModel)
                )
        }
        catch (e:Exception) {
            LOGGER.error(e.message)

            return ResponseEntity
                .internalServerError()
                .build()
        }
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
}