package com.example.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.data.DatabaseFactory.dbQuery
import com.example.data.Users
import com.example.models.User
import com.example.models.UserRegistrationRequest
import org.jetbrains.exposed.sql.*
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class AuthService(
    private val jwtSecret: String,
    private val jwtIssuer: String,
    private val jwtAudience: String
) {
    suspend fun registerUser(request: UserRegistrationRequest): User? = dbQuery {
        val existing = Users.select { Users.email eq request.email }.singleOrNull()
        if (existing != null) return@dbQuery null

        val hashedPassword = BCrypt.hashpw(request.password, BCrypt.gensalt())
        val now = System.currentTimeMillis()
        
        val insertStatement = Users.insert {
            it[fullName] = request.fullName
            it[email] = request.email
            it[phone] = request.phone
            it[passwordHash] = hashedPassword
            it[createdAt] = now
            it[updatedAt] = now
        }

        insertStatement.resultedValues?.singleOrNull()?.let { rowToUser(it) }
    }

    suspend fun authenticateUser(email: String, password: String): User? = dbQuery {
        val userRow = Users.select { Users.email eq email }.singleOrNull() ?: return@dbQuery null
        val hashedPassword = userRow[Users.passwordHash]

        if (BCrypt.checkpw(password, hashedPassword)) {
            rowToUser(userRow)
        } else {
            null
        }
    }

    suspend fun getUserById(id: Int): User? = dbQuery {
        Users.select { Users.id eq id }
            .map { rowToUser(it) }
            .singleOrNull()
    }

    fun generateToken(user: User): String {
        return JWT.create()
            .withAudience(jwtAudience)
            .withIssuer(jwtIssuer)
            .withClaim("email", user.email)
            .withClaim("userId", user.id)
            .withExpiresAt(Date(System.currentTimeMillis() + 3600000 * 24)) // 24 hours
            .sign(Algorithm.HMAC256(jwtSecret))
    }

    private fun rowToUser(row: ResultRow) = User(
        id = row[Users.id],
        fullName = row[Users.fullName],
        email = row[Users.email],
        phone = row[Users.phone],
        createdAt = row[Users.createdAt],
        updatedAt = row[Users.updatedAt]
    )
}
