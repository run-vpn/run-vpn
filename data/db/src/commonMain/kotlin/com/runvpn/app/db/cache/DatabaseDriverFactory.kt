package com.runvpn.app.db.cache

import app.cash.sqldelight.db.SqlDriver


expect class DatabaseDriverFactory {
    fun createDriver(): SqlDriver
}
