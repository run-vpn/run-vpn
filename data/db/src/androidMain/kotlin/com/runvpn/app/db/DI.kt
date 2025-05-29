package com.runvpn.app.db

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.runvpn.app.db.cache.Database
import org.koin.dsl.module

actual val databaseModule = module {

    single<SqlDriver> {
        AndroidSqliteDriver(
            schema = Database.Schema,
            context = get(),
            name = "Database.db"
        )
    }

    single { Database(get()) }

    single { get<Database>().dbServerQueries }
    single { get<Database>().dbCustomVpnConfigQueries }
    single { get<Database>().dbDnsServerQueries }
}
