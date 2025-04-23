package com.ordersystemtask.june.domain.user.repository

import com.ordersystemtask.june.domain.user.context.UserDBContext
import com.ordersystemtask.june.domain.user.entity.AuthenticationUserEntity
import com.ordersystemtask.june.domain.user.entity.OAuthProvider
import com.ordersystemtask.june.domain.user.entity.OAuthProviderType
import com.ordersystemtask.june.domain.user.entity.UserEntity
import com.ordersystemtask.june.domain.user.entity.UserTraitType
import jakarta.persistence.EntityManager
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionDefinition
import org.springframework.transaction.support.DefaultTransactionStatus

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserRepositoryTests @Autowired constructor(
    private val userDBContext: UserDBContext,
    private val transactionManager: PlatformTransactionManager
) {
    private var testUserId:Long = 0L
    private val sut = UserRepository(userDBContext)

    @BeforeAll
    fun setupAll() {

        val status = transactionManager.getTransaction(DefaultTransactionDefinition())

        try {
            var user = UserEntity.new("test1@email.com")
            user = sut.saveUser(user)

            val authenticationUser = AuthenticationUserEntity.new(
                userId = user.userId,
                userTrait = user.userTrait,
                oauthProvider = OAuthProvider(
                    providerType = OAuthProviderType.Google,
                    sub = "sub",
                    email = user.email
                )
            )

            authenticationUser.updateRefreshToken("abcd")

            sut.saveAuthenticationUser(authenticationUser)

            testUserId = user.userId

            transactionManager.commit(status)
        }
        catch(e: Exception) {
            transactionManager.rollback(status)
        }
    }

    @Test
    fun `UserEntity 조회`() {
        val user = sut.findUserById(testUserId)

        Assertions.assertNotNull(user)

        Assertions.assertEquals(
            user!!.email,
            "test1@email.com"
        )
        Assertions.assertEquals(
            user.userTrait,
            UserTraitType.Normal
        )
    }

    @Test
    fun `AuthenticationUserEntity 조회`() {
        val authenticationUser = sut.findAuthenticationUserById(testUserId)

        Assertions.assertNotNull(authenticationUser)

        Assertions.assertEquals(
            authenticationUser!!.userId,
            testUserId
        )
        Assertions.assertEquals(
            authenticationUser.userTrait,
            UserTraitType.Normal
        )
        Assertions.assertEquals(
            authenticationUser.oauthProvider.refreshToken,
            "abcd"
        )
    }
}