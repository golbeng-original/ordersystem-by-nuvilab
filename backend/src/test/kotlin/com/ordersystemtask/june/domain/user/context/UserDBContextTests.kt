package com.ordersystemtask.june.domain.user.context

import com.ordersystemtask.june.domain.user.context.models.OAuthProviderModel
import com.ordersystemtask.june.domain.user.context.models.UserModel
import com.ordersystemtask.june.domain.user.entity.OAuthProviderType
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Rollback
import java.time.LocalDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserDBContextTests @Autowired constructor(
    private val userDBContext: UserDBContext
) {

    @Test
    @Rollback(false)
    fun `UserModel 저장 - 1`() {
        val userModel = UserModel(
            email = "email@email.com",
            trait = UserTraitType.Normal,
            createdAt = LocalDateTime.now(),
        )

        val newUserModel = userDBContext.save(userModel)
    }

    @Test
    @Rollback(false)
    fun `UserModel 저장 - 2`() {
        val userModel = UserModel(
            email = "email3@email.com",
            trait = UserTraitType.Normal,
            createdAt = LocalDateTime.now(),
        )

        val oauthProvider = OAuthProviderModel(
            providerType = OAuthProviderType.Google,
            user = userModel,
            sub = "sub",
            email = "email3@email.com",
            refreshToken = "asdfsdf"
        )

        userModel.oauthProvider = oauthProvider

        val newUserModel = userDBContext.save(userModel)
    }

    @Test
    fun `UserModel 조회`() {
        val userModel = userDBContext.findByUserId(2)

        println(userModel?.oauthProvider)
    }
}