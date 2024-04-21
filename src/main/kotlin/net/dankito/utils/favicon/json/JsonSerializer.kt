package net.dankito.utils.favicon.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule

object JsonSerializer {

    val default: ObjectMapper by lazy {
        ObjectMapper().apply { // do not use jacksonObjectMapper(), it crashes with Kotlin 1.6
            this.registerModules(KotlinModule.Builder().build()) // don't use findAndRegisterModules(), it won't work in native mode

            this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

}