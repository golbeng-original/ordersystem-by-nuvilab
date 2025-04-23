package com.ordersystemtask.june.domain.store.repository

import com.ordersystemtask.june.domain.store.entity.StoreEntity
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
class StoreRepository {
    private var _idGenerator:Long = 0L
    private val _stores = mutableMapOf<Long, StoreEntity>()

    fun findStoreById(storeId: Long): StoreEntity? {
        return _stores[storeId]
    }

    fun findStoreByOwnerUserId(ownerUserId: Long): List<StoreEntity> {
        return _stores.values.filter {
            it.ownerUserId == ownerUserId
        }
    }

    fun saveStore(storeEntity: StoreEntity): StoreEntity {

        if( storeEntity.storeId == 0L) {
            val newStoreEntity = StoreEntity(
                storeId = ++_idGenerator,
                ownerUserId = storeEntity.ownerUserId,
                name = storeEntity.name,
                description = storeEntity.description,
                _storeStatus = storeEntity.storeStatus,
                _menus = storeEntity.menus.toMutableList()
            )

            _stores[newStoreEntity.storeId] = newStoreEntity
            return newStoreEntity
        }

        _stores[storeEntity.storeId] = storeEntity

        return storeEntity
    }
}