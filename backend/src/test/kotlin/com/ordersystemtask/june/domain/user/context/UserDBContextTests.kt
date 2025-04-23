package com.ordersystemtask.june.domain.user.context

import com.ordersystemtask.june.domain.user.context.models.OAuthProviderModel
import com.ordersystemtask.june.domain.user.context.models.UserModel
import com.ordersystemtask.june.domain.user.entity.OAuthProviderType
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.annotation.Rollback
import java.time.LocalDateTime

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserDBContextTests @Autowired constructor(
    private val userDBContext: UserDBContext
) {
    private var queryUserId: Long = 0L

    @BeforeAll
    fun setupAll() {
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
        queryUserId = newUserModel.userId!!
    }

    @Test
    fun `UserModel 저장 - 1`() {
        val userModel = UserModel(
            email = "email@email.com",
            trait = UserTraitType.Normal,
            createdAt = LocalDateTime.now(),
        )

        val newUserModel = userDBContext.save(userModel)
        println("id = ${newUserModel.userId}")
    }

    @Test
    fun `UserModel 저장 - 2`() {
        val userModel = UserModel(
            email = "email2@email.com",
            trait = UserTraitType.Normal,
            createdAt = LocalDateTime.now(),
        )

        var newUserModel = userDBContext.save(userModel)

        val oauthProvider = OAuthProviderModel(
            providerType = OAuthProviderType.Google,
            user = newUserModel,
            sub = "sub",
            email = "email2@email.com",
            refreshToken = "12342342"
        )

        newUserModel.oauthProvider = oauthProvider
        newUserModel =userDBContext.save(newUserModel)

        val loadOauthProvider = newUserModel.oauthProvider
        Assertions.assertNotNull(loadOauthProvider)

        Assertions.assertEquals(loadOauthProvider!!.userId, newUserModel.userId)
    }

    @Test
    fun `UserModel 조회`() {
        val userModel = userDBContext.findByUserId(queryUserId)

        val oauthProvider = userModel?.oauthProvider
        Assertions.assertNotNull(oauthProvider)

        Assertions.assertEquals(
            oauthProvider!!.userId,
            userModel.userId
        )
        Assertions.assertEquals(
            oauthProvider.refreshToken,
            "asdfsdf"
        )
    }
}