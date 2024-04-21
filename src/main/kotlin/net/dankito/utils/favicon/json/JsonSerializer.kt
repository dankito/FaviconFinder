package net.dankito.utils.favicon.json

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper

object JsonSerializer {

    val default: ObjectMapper by lazy {
        ObjectMapper().apply { // do not use jacksonObjectMapper(), it crashes with Kotlin 1.6
            this.findAndRegisterModules()

            this.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
        }
    }

}