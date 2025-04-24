package com.ordersystemtask.june.security

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import java.util.*


data class JwtClaims(
    val userId:Long,
    val accessToken:String,
    val isExpired:Boolean
)

/**
 * 서버 자체적인 JWT Token 발급
 * - HMAC SHA256 알고리즘 사용
 * - secretKeyPlainText 길이 32자 이상이여야 한다.
 */
class JWTGenerator(
    val secretKeyPlainText:String
) {
    private val secretKey = Keys.hmacShaKeyFor(secretKeyPlainText.toByteArray())
    private val logger = LoggerFactory.getLogger(this.javaClass.name)

    fun generate(userId:Long, accessToken:String, expireSeconds:Long) : String {
        val now = Date()
        val jwtToken = Jwts.builder().run {
            setSubject(userId.toString())
            claim("accessToken", accessToken)
            setIssuedAt(now)
            setExpiration(Date(now.time + (expireSeconds * 1000)))
            signWith(secretKey)
            compact()
        }

        return jwtToken
    }

    fun deserialize(jwtToken:String) : JwtClaims? {
        try {
            val parser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()

            val claims = parser.parseClaimsJws(jwtToken).body

            val expiration = claims.expiration

            return JwtClaims(
                userId = claims.subject.toLong(),
                accessToken = claims["accessToken"].toString(),
                isExpired = expiration.before(Date())
            )
        }
        catch (e: Exception) {
            logger.error(e.message)
            return null
        }
    }

}