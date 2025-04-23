package com.ordersystemtask.june.applicationService.store

import com.ordersystemtask.june.domain.store.entity.MenuItemEntity
import com.ordersystemtask.june.domain.store.entity.StoreEntity
import com.ordersystemtask.june.domain.store.entity.StoreStatus
import com.ordersystemtask.june.domain.store.repository.StoreRepository
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import com.ordersystemtask.june.domain.user.repository.UserRepository
import org.springframework.stereotype.Service

data class StoreCreationParam(
    val ownerUserId:Long,
    val storeName:String,
    val storeDescription:String,
)

data class StoreCreationOutput(
    val userEntity:UserEntity,
    val storeEntity:StoreEntity,
)

data class UpdateMenuItemsParam(
    val storeId:Long,
    val menuItemUnits:List<StoreUpdateMenuItemUnit>
)

data class UpdateMenuItemsOutput(
    val storeEntity:StoreEntity
)

data class StoreUpdateMenuItemUnit(
    val name:String,
    val description:String,
    val price:Long,
)

data class StoreUpdateMenuItemOutput(
    val storeEntity:StoreEntity
)

@Service
class StoreApplicationService(
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository,
) {
    /**
     * 가게 개설
     */
    fun createStore(param:StoreCreationParam) : StoreCreationOutput {

        val user = userRepository.findUserById(param.ownerUserId)
        require(user != null) {
            "User not found"
        }

        require(user.isDeleted == false) {
            "already deleted"
        }

        require(user.userTrait == UserTraitType.Seller) {
            "not seller"
        }

        val store = storeRepository.saveStore(
            StoreEntity.new(
                ownerUserId = user.userId,
                name = param.storeName,
                description = param.storeDescription,
            )
        )

        return StoreCreationOutput(
            userEntity = user,
            storeEntity = store
        )
    }

    /**
     * 가게 삭제
     */
    fun removeStore(storeId:Long) {
        val store = storeRepository.findStoreById(storeId)
        require(store != null) {
            "Store not found"
        }

        store.delete()

        storeRepository.saveStore(store)
    }

    /**
     * 가게 메뉴 수정
     */
    fun updateMenuItems(param:UpdateMenuItemsParam) : StoreUpdateMenuItemOutput{

        val (storeId, menuItemUnits) = param

        val store = storeRepository.findStoreById(storeId)
        require(store != null) {
            "Store not found"
        }

        store.removeAllMenuItem()

        val menuItems = menuItemUnits.map {
            MenuItemEntity.new(
                name = it.name,
                description = it.description,
                price = it.price
            )
        }

        store.addMenuItems(menuItems)

        storeRepository.saveStore(store)

        return StoreUpdateMenuItemOutput(
            storeEntity = store
        )
    }

    /**
     * 가게 상태 변경
     */
    fun updateStoreStatus(storeId:Long, storeStatus:StoreStatus) {
        val store = storeRepository.findStoreById(storeId)
        require(store != null) {
            "Store not found"
        }

        store.changeStoreStatus(storeStatus)

        storeRepository.saveStore(store)
    }
}