package com.wabadaba.librusmock

import com.beust.klaxon.JsonObject
import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.InputStreamResource
import org.springframework.web.bind.annotation.*

@Suppress("unused")
@RestController
class MainController {

    @RequestMapping(path = arrayOf("/OAuth/Token"), method = arrayOf(RequestMethod.POST))
    fun login(@RequestHeader("Authorization") authorizationHeader: String): String {
        val response = JsonObject()
        response.put("access_token", "mock_access_token")
        response.put("refresh_token", "mock_refresh_token")
        response.put("expires_in", 1864800)
        return response.toJsonString()
    }

    @RequestMapping(path = arrayOf("/2.0/{endpoint}"),
            method = arrayOf(RequestMethod.GET),
            produces = arrayOf("application/json"))
    fun request(@PathVariable("endpoint") endpoint: String): InputStreamResource {
        val stream =  ClassPathResource("$endpoint.json").inputStream
        return InputStreamResource(stream)
    }
}