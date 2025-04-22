package com.ordersystemtask.june.security

import com.google.gson.Gson
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.util.Base64

class JWTGeneratorTests {
    val sut = JWTGenerator("12345678901234567890123456789012")

    @Test
    fun `JWT 토큰 생성`() {

        val jwtToken = sut.generate(1, "test-access-token", 60)

        val elements = jwtToken.split(".")
        Assertions.assertEquals(elements.size, 3)

        val bytes = Base64.getDecoder().decode(elements[1])
        val content = String(bytes, Charsets.UTF_8)

        Assertions.assertDoesNotThrow {
            Gson().fromJson(content, Map::class.java)
        }
    }

    @Test
    fun `JWT 토큰 검즘`() {
        val jwtToken = sut.generate(1, "test-access-token", 60)

        val claims = sut.deserialize(jwtToken)

        Assertions.assertNotNull(claims)

        Assertions.assertEquals(claims!!.userId, 1L)
        Assertions.assertEquals(claims.accessToken, "test-access-token")
        Assertions.assertEquals(claims.isExpired, false)
    }

}