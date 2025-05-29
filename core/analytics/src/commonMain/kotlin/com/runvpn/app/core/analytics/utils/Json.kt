package com.runvpn.app.core.analytics.utils

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement


internal fun Any?.toJsonElement(): JsonElement = when (this) {
    null -> JsonNull
    is JsonElement -> this
    is Number -> JsonPrimitive(this)
    is Boolean -> JsonPrimitive(this)
    is String -> JsonPrimitive(this)
    is Array<*> -> this.toJsonArray()
    is List<*> -> this.toJsonArray()
    is Map<*, *> -> this.toJsonObject()
    else -> Json.encodeToJsonElement(this)
}

internal fun Array<*>.toJsonArray() = JsonArray(map { it.toJsonElement() })
internal fun Iterable<*>.toJsonArray() = JsonArray(map { it.toJsonElement() })
internal fun Map<*, *>.toJsonObject() = JsonObject(
    mapKeys { it.key.toString() }.mapValues { it.value.toJsonElement() }
)
