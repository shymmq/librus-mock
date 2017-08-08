package com.wabadaba.librusmock

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.InputStreamResource
import org.springframework.web.bind.annotation.*

@Suppress("unused")
@RestController
class MainController {

    @RequestMapping(path = arrayOf("/OAuth/Token"), method = arrayOf(RequestMethod.POST))
    fun login(@RequestHeader("Authorization") authorizationHeader: String): String {
        return authorizationHeader
    }

    @RequestMapping(path = arrayOf("/2.0/{endpoint}"),
            method = arrayOf(RequestMethod.GET),
            produces = arrayOf("application/json"))
    fun request(@PathVariable("endpoint") endpoint: String): InputStreamResource {
        val stream =  ClassPathResource("$endpoint.json").inputStream
        return InputStreamResource(stream)
    }
}