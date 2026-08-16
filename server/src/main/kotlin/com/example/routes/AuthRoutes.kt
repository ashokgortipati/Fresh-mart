package com.example.routes

import com.example.models.*
import com.example.service.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(jwtSecret: String, jwtIssuer: String, jwtAudience: String) {
    val authService = AuthService(jwtSecret, jwtIssuer, jwtAudience)

    route("/api/auth") {
        post("/register") {
            val request = call.receive<UserRegistrationRequest>()
            
            // Validation (simplified for now)
            if (request.email.isBlank() || !request.email.contains("@")) {
                return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid email format"))
            }
            if (request.password.length < 8) {
                return@post call.respond(HttpStatusCode.BadRequest, ErrorResponse("Password too short"))
            }

            val user = authService.registerUser(request)
            if (user != null) {
                val token = authService.generateToken(user)
                call.respond(HttpStatusCode.Created, AuthResponse(user, token))
            } else {
                call.respond(HttpStatusCode.Conflict, ErrorResponse("User with this email already exists"))
            }
        }

        post("/login") {
            val request = call.receive<LoginRequest>()
            val user = authService.authenticateUser(request.email, request.password)

            if (user != null) {
                val token = authService.generateToken(user)
                call.respond(HttpStatusCode.OK, AuthResponse(user, token))
            } else {
                call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Invalid email or password"))
            }
        }

        authenticate("auth-jwt") {
            get("/me") {
                val principal = call.principal<JWTPrincipal>()
                val userId = principal?.payload?.getClaim("userId")?.asInt()
                
                if (userId != null) {
                    val user = authService.getUserById(userId)
                    if (user != null) {
                        call.respond(HttpStatusCode.OK, user)
                    } else {
                        call.respond(HttpStatusCode.NotFound, ErrorResponse("User not found"))
                    }
                } else {
                    call.respond(HttpStatusCode.Unauthorized, ErrorResponse("Unauthorized"))
                }
            }

            post("/logout") {
                // For JWT, logout is usually handled on client side by deleting the token.
                // We can respond success.
                call.respond(HttpStatusCode.OK, SuccessResponse("Logged out successfully"))
            }
        }

        post("/forgot-password") {
            val request = call.receive<ForgotPasswordRequest>()
            // Mock implementation: always return success
            call.respond(HttpStatusCode.OK, SuccessResponse("Password reset email sent (mock)"))
        }

        post("/reset-password") {
            val request = call.receive<ResetPasswordRequest>()
            // Mock implementation: always return success
            call.respond(HttpStatusCode.OK, SuccessResponse("Password reset successfully (mock)"))
        }
    }
}
