package com.ordersystemtask.june.domain.store.repository

import com.ordersystemtask.june.domain.store.context.StoreDBContext
import com.ordersystemtask.june.domain.store.context.models.MenuItemModel
import com.ordersystemtask.june.domain.store.context.models.StoreModel
import com.ordersystemtask.june.domain.store.entity.MenuItemEntity
import com.ordersystemtask.june.domain.store.entity.StoreEntity
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service

@Repository
class StoreRepository @Autowired constructor(
    private val context: StoreDBContext
) {
    fun findStoreById(storeId: Long): StoreEntity? {
        val model = context.findByStoreId(storeId)
        if( model == null ) {
            return null
        }

        return convertToEntity(model)
    }

    fun findStoreByOwnerUserId(ownerUserId: Long): List<StoreEntity> {
        val models = context.findByOwnerUserId(ownerUserId)

        return models.map {
            convertToEntity(it)
        }
    }

    fun saveStore(storeEntity: StoreEntity): StoreEntity {
        val model = convertToModel(storeEntity)

        val savedModel = context.save(model)
        return convertToEntity(savedModel)
    }

    fun convertToModel(entity: StoreEntity) : StoreModel {

        val model = StoreModel(
            storeId = if(entity.storeId > 0) entity.storeId else null,
            ownerUserId = entity.ownerUserId,
            name = entity.name,
            description = entity.description,
            createdAt = entity.createdAt,
            deletedAt = entity.deletedAt,
            storeStatus = entity.storeStatus,
            menuItems = entity.menus.map {
                MenuItemModel(
                    menuItemId = it.menuItemId,
                    name = it.name,
                    description = it.description,
                    price = it.price,
                    orderIndex = it.orderIndex
                )
            }.toMutableList()
        )

        return model
    }

    fun convertToEntity(model: StoreModel) : StoreEntity {
        val entity = StoreEntity(
            storeId = model.storeId!!,
            ownerUserId = model.ownerUserId,
            name = model.name,
            description = model.description,
            createdAt = model.createdAt,
            _deletedAt = model.deletedAt,
            _storeStatus = model.storeStatus,
            _isDeleted = model.isDeleted,
            _menus = model.menuItems.map {
                MenuItemEntity(
                    menuItemId = it.menuItemId,
                    name = it.name,
                    description = it.description,
                    price = it.price,
                    orderIndex = it.orderIndex
                )
            }.toMutableList()
        )

        return entity
    }
}