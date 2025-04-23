package com.ordersystemtask.june.domain.store.context

import com.ordersystemtask.june.domain.store.context.models.StoreModel
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Component

@Component
interface StoreDBContext : JpaRepository<StoreModel, Long>{

    fun findByStoreId(storeId: Long): StoreModel?

    fun findByOwnerUserId(ownerUserId: Long): List<StoreModel>
}