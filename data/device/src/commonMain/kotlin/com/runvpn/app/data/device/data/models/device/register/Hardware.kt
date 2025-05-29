package com.runvpn.app.data.device.data.models.device.register

import kotlinx.serialization.Serializable

@Serializable
data class Hardware (
	val brand : String?,
	val manufacture : String?,
	val name : String?,
	val productName : String?,
	val marketName: String?
)
