package com.wabadaba.librusmock

import com.beust.klaxon.JsonObject
import com.google.android.gcm.server.Message
import com.google.android.gcm.server.Sender
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.InputStreamResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest


@Suppress("unused")
@RestController
class MainController {

    private val apiKey = "AIzaSyAmdPaNNNmY1f9N6VKy436etqibtQURJzY"

    private var lastRegId: String? = null

    private var tokenValid: Boolean = true

    val users = setOf(LoginData("username1", "password1"),
            LoginData("username2", "password2"),
            LoginData("13335", "librus11"))

    @RequestMapping(path = arrayOf("/OAuth/Token"), method = arrayOf(RequestMethod.POST))
    fun login(@RequestHeader refresh_token: String?,
              @ModelAttribute loginData: LoginData): ResponseEntity<*> {
        println(loginData)
        if (refresh_token != null) {
            tokenValid = true
            return ResponseEntity.ok("Token refreshed successfully")
        }
        return if (users.contains(loginData)) {
            val response = JsonObject()
            response.put("access_token", loginData.username)
            response.put("refresh_token", loginData.username)
            response.put("expires_in", 1864800)
            ResponseEntity.ok(response.toJsonString())
        } else {
            val response = JsonObject()
            response.put("error", "invalid_grant")
            ResponseEntity.badRequest()
                    .body(response.toJsonString())
        }
    }

    @RequestMapping(path = arrayOf("/2.0/PushDevices"), method = arrayOf(RequestMethod.POST))
    fun pushDevice(@RequestBody registrationId: RegistrationID): ResponseEntity<String> {
        lastRegId = registrationId.device
        println("Saved reg id : $lastRegId")
        return ResponseEntity.ok("kthxbye")
    }

    @RequestMapping(path = arrayOf("/sendNotification"))
    fun sendNotification() {
        val sender = Sender(apiKey)
        val message = Message.Builder()
                .addData("message", "(test)Ogłoszenia. Usunięto ogłoszenie")
                .addData("objectType", "SchoolNotices")
                .addData("title", "Synergia")
                .addData("userId", "3u")
                .addData("collapse_key", "do_not_collapsee")
                .build()
        println("Sending message to reg id : $lastRegId")
        sender.send(message, lastRegId, 1)
    }

    @RequestMapping(path = arrayOf("/expireToken"))
    fun setTokenValid() {
        tokenValid = false
        println("token expired")
    }

    @RequestMapping(path = arrayOf("/2.0/**"),
            method = arrayOf(RequestMethod.GET),
            produces = arrayOf("application/json"))
    fun request(request: HttpServletRequest,
                @RequestHeader(value = "Authorization") authHeader: String): ResponseEntity<InputStreamResource> {
        if (!tokenValid) {
            println("Returning token expired message")
            return ResponseEntity.badRequest()
                    .body(InputStreamResource(ClassPathResource("/tokenExpired.json").inputStream))
        }
        assert(authHeader.startsWith("Bearer "))
        val token = authHeader.drop(7)
        try {
            val path = "/$token${request.requestURI.drop(4)}.json"
            println("Trying to load file from path: $path")
            val stream = ClassPathResource(path).inputStream
            return ResponseEntity.ok(InputStreamResource(stream))
        } catch (e: Exception) {
            println("Fallback to /main")
            val path = "/main${request.requestURI.drop(4)}.json"
            val stream = ClassPathResource(path).inputStream
            return ResponseEntity.ok(InputStreamResource(stream))
        }
    }
}

data class RegistrationID(
        var provider: String = "",
        var device: String = "")

data class LoginData(
        var username: String = "",
        var password: String = "")
