package com.runvpn.app.core.common

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

@OptIn(ExperimentalContracts::class)
inline fun checkNotNullOrEmpty(value: String?): String {
    contract {
        returns() implies (value != null)
    }
    if (value.isNullOrEmpty()) {
        error("Required string was empty.")
    } else {
        return value
    }
}
