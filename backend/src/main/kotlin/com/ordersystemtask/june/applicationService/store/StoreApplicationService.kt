package com.ordersystemtask.june.applicationService.store

import com.ordersystemtask.june.domain.store.entity.MenuItemEntity
import com.ordersystemtask.june.domain.store.entity.StoreEntity
import com.ordersystemtask.june.domain.store.entity.StoreStatus
import com.ordersystemtask.june.domain.store.repository.StoreRepository
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import com.ordersystemtask.june.domain.user.repository.UserRepository
import jakarta.transaction.Transactional
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

data class AddMenuItemsParam(
    val storeId:Long,
    val menuItemUnit:StoreUpdateMenuItemUnit
)

data class AddMenuItemOutput(
    val store:StoreEntity,
    val newMenuItem:MenuItemEntity
)

data class StoreUpdateMenuItemUnit(
    val name:String,
    val description:String,
    val price:Long,
)

@Service
class StoreApplicationService(
    private val userRepository: UserRepository,
    private val storeRepository: StoreRepository,
) {
    /**
     * 가게 리스트 가져오기
     */
    fun getStores() : List<StoreEntity> {
        val stores = storeRepository.findAll()
        return stores
    }

    /**
     * 가게 가져오기
     */
    fun getStore(storeId:Long) : StoreEntity {
        val store = storeRepository.findStoreById(storeId)
        require( store != null ) {
            "Store not found"
        }

        return store
    }

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
    @Transactional
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
    @Transactional
    fun addMenuItems(param:AddMenuItemsParam) : AddMenuItemOutput {

        val (storeId, menuItemUnits) = param

        var store = storeRepository.findStoreById(storeId)
        require(store != null) {
            "Store not found"
        }

        val alreadyExistsMenuItemIds = store.menus.map { it.menuItemId }

        store.addMenuItem(
            MenuItemEntity.new(
                name = menuItemUnits.name,
                description = menuItemUnits.description,
                price = menuItemUnits.price
            )
        )

        store = storeRepository.saveStore(store)

        val newMenuItem = store.menus.find { alreadyExistsMenuItemIds.contains(it.menuItemId).not() }
        require(newMenuItem != null ) {
            "MenuItem Add Error"
        }

        return AddMenuItemOutput(
            store = store,
            newMenuItem = newMenuItem
        )
    }

    /**
     * 가게 상태 변경
     */
    @Transactional
    fun updateStoreStatus(storeId:Long, storeStatus:StoreStatus) : StoreEntity {
        val store = storeRepository.findStoreById(storeId)
        require(store != null) {
            "Store not found"
        }

        store.changeStoreStatus(storeStatus)

        return storeRepository.saveStore(store)
    }
}