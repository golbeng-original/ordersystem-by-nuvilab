package com.ordersystemtask.june.controller.store

import com.ordersystemtask.june.applicationService.store.AddMenuItemsParam
import com.ordersystemtask.june.applicationService.store.StoreApplicationService
import com.ordersystemtask.june.applicationService.store.StoreCreationParam
import com.ordersystemtask.june.applicationService.store.StoreUpdateMenuItemUnit
import com.ordersystemtask.june.domain.store.entity.MenuItemEntity
import com.ordersystemtask.june.domain.store.entity.StoreEntity
import com.ordersystemtask.june.domain.store.entity.StoreStatus
import com.ordersystemtask.june.security.JwtUserDetails
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@RequestMapping("/stores")
class StoreController(
    private val storeApplicationService: StoreApplicationService
) {
    private val LOGGER = LoggerFactory.getLogger(this::javaClass.name)

    @GetMapping("")
    fun getStores(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
    ) : ResponseEntity<GetStoresResponse> {
        try {
            val stores = storeApplicationService.getStores()

            val storeViewModels = stores.map {
                converterStoreToViewModel(it)
            }

            return ResponseEntity
                .ok()
                .body(
                    GetStoresResponse(storeViewModels)
                )
        }
        catch (e:Exception) {
            LOGGER.error(e.message)

            return ResponseEntity
                .internalServerError()
                .build()
        }
    }

    @GetMapping("/{storeId}")
    fun getStore(
        @PathVariable storeId:Long
    ) : ResponseEntity<GetStoreResponse> {
        try {
            val store = storeApplicationService.getStore(storeId)

            val storeViewModel = converterStoreToViewModel(store)

            return ResponseEntity
                .ok()
                .body(
                    GetStoreResponse(storeViewModel)
                )
        }
        catch (e:Exception) {
            LOGGER.error(e.message)

            return ResponseEntity
                .internalServerError()
                .build()
        }
    }

    @PostMapping("")
    @PreAuthorize("hasRole('SELLER')")
    fun createStore(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
        @RequestBody request:CreateStoreRequest
    ) : ResponseEntity<CreateStoreResponse> {
        try {
            val output = storeApplicationService.createStore(
                StoreCreationParam(
                    ownerUserId = userDetails.user.userId,
                    storeName = request.name,
                    storeDescription = request.desc
                )
            )

            val storeViewModel = converterStoreToViewModel(output.storeEntity)

            return ResponseEntity
                .created(URI.create("/stores/${1}"))
                .body(CreateStoreResponse(storeViewModel))
        }
        catch (e:Exception) {
            LOGGER.error(e.message)

            return ResponseEntity
                .internalServerError()
                .build()
        }
    }

    @PatchMapping("/{storeId}")
    @PreAuthorize("hasRole('SELLER')")
    fun updateStoreStatus(
        @AuthenticationPrincipal userDetails: JwtUserDetails,
        @PathVariable storeId:Long,
        @RequestBody request:UpdateStoreStatusRequest
    ) : ResponseEntity<UpdateStoreStatusResponse> {
        try {

            val storeStatus = when(request.status) {
                StoreStatusViewModelType.Open -> StoreStatus.Open
                StoreStatusViewModelType.Closed -> StoreStatus.Closed
                StoreStatusViewModelType.Resting -> StoreStatus.Resting
            }

            val store = storeApplicationService.updateStoreStatus(
                storeId = storeId,
                storeStatus = storeStatus
            )

            val storeViewModel = converterStoreToViewModel(store)
            return ResponseEntity
                .ok()
                .body(
                    UpdateStoreStatusResponse(storeViewModel)
                )
        }
        catch (e:Exception) {
            LOGGER.error(e.message)

            return ResponseEntity
                .internalServerError()
                .build()
        }
    }

    @GetMapping("/{storeId}/menus")
    fun getMenus(
        @PathVariable storeId:Long
    ) : ResponseEntity<GetMenuItemsResponse> {
        try {
            val store = storeApplicationService.getStore(storeId)

            val menuItemViewModels = store.menus.map {
                convertMenuToViewModel(it)
            }

            return ResponseEntity
                .ok()
                .body(
                    GetMenuItemsResponse(menuItemViewModels)
                )
        }
        catch (e:Exception) {
            LOGGER.error(e.message)

            return ResponseEntity
                .internalServerError()
                .build()
        }
    }

    @PostMapping("/{storeId}/menus")
    @PreAuthorize("hasRole('SELLER')")
    fun addMenu(
        @PathVariable storeId:Long,
        @RequestBody request:AddMenuRequest
    ) : ResponseEntity<AddMenuResponse> {
        try {
            val newMenuItemUnit = StoreUpdateMenuItemUnit(
                name = request.name,
                description = request.desc,
                price = request.price
            )

            val output = storeApplicationService.addMenuItems(
                AddMenuItemsParam(storeId, newMenuItemUnit)
            )

            return ResponseEntity
                .created(URI.create("/stores/${output.store.storeId}"))
                .body(
                    AddMenuResponse(
                        convertMenuToViewModel(output.newMenuItem)
                    )
                )
        }
        catch (e:Exception) {
            LOGGER.error(e.message)

            return ResponseEntity
                .internalServerError()
                .build()
        }
    }

    private fun converterStoreToViewModel(store: StoreEntity) : StoreViewModel {
        return StoreViewModel(
            storeId = store.storeId,
            ownerUserId = store.ownerUserId,
            name = store.name,
            desc = store.description,
            status = when(store.storeStatus) {
                StoreStatus.Open -> StoreStatusViewModelType.Open
                StoreStatus.Closed-> StoreStatusViewModelType.Closed
                StoreStatus.Resting -> StoreStatusViewModelType.Resting
                else -> throw Error("Store status is not valid")
            },
            menus = store.menus.map {
                convertMenuToViewModel(it)
            }
        )
    }

    private fun convertMenuToViewModel(menu: MenuItemEntity) : MenuItemViewModel {
        return MenuItemViewModel(
            menuItemId = menu.menuItemId,
            name = menu.name,
            desc = menu.description,
            price = menu.price
        )
    }

}