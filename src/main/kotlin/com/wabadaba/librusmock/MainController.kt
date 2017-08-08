package com.wabadaba.librusmock

import com.beust.klaxon.JsonObject
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.InputStreamResource
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Suppress("unused")
@RestController
class MainController {

    val users = setOf(LoginData("username1", "password1"),
            LoginData("username2", "password2"),
            LoginData("13335", "librus11"))

    @RequestMapping(path = arrayOf("/OAuth/Token"), method = arrayOf(RequestMethod.POST))
    fun login(@ModelAttribute loginData: LoginData): ResponseEntity<String> {
        println(loginData)
        if (users.contains(loginData)) {
            val response = JsonObject()
            response.put("access_token", loginData.username)
            response.put("refresh_token", loginData.username)
            response.put("expires_in", 1864800)
            return ResponseEntity.ok(response.toJsonString())
        } else {
            val response = JsonObject()
            response.put("error", "invalid_grant")
            return ResponseEntity.badRequest()
                    .body(response.toJsonString())
        }
    }

    @RequestMapping(path = arrayOf("/2.0/{endpoint}"),
            method = arrayOf(RequestMethod.GET),
            produces = arrayOf("application/json"))
    fun request(@PathVariable("endpoint") endpoint: String): InputStreamResource {
        val stream = ClassPathResource("$endpoint.json").inputStream
        return InputStreamResource(stream)
    }
}

data class LoginData(
        var username: String = "",
        var password: String = "")
