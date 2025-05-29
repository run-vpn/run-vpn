package com.runvpn.app.data.servers.domain.usecases

import com.runvpn.app.data.servers.domain.entities.ImportedOverSocksConfig
import com.runvpn.app.data.servers.domain.entities.ConfigValidationResult


class ExtractSocks5FromUrlUseCase {

    //  Примеры конфига
    // 1. socks5://user:2gofNqBwZJ0nt9SyQf24@212.8.249.177:1080
    // 2. socks5h://user:2gofNqBwZJ0nt9SyQf24@212.8.249.177:1080
    // 3. user:2gofNqBwZJ0nt9SyQf24@212.8.249.177:1080

    operator fun invoke(config: String): ConfigValidationResult {
        //обработка для вариантов 1,2
        if (config.contains("socks5://") || config.contains("socks5h://")) {
            val configParts = config.split("://", ":", "@")
            if (configParts.size == 5) {
                val username = configParts[1]
                val password = configParts[2]
                val host = configParts[3]
                val port = configParts[4]

                if (isValidIpV4(host) && isValidPort(port)) {
                    return ConfigValidationResult.Success(
                        host = host, ImportedOverSocksConfig(
                            host = host,
                            port = port,
                            username = username,
                            password = password
                        )
                    )
                }
            }
        }

        //обработка для варианта 3
        val configParts = config.split(":", "@")

        if (configParts.size == 4) {
            val username = configParts[0]
            val password = configParts[1]
            val host = configParts[2]
            val port = configParts[3]

            if (isValidIpV4(host) && isValidPort(port)) {
                return ConfigValidationResult.Success(
                    host = host, ImportedOverSocksConfig(
                        host = host,
                        port = port,
                        username = username,
                        password = password
                    )
                )
            }
        }
        return ConfigValidationResult.ConfigInvalid
    }

    private fun isValidIpV4(ip: String): Boolean {
        val groups = ip.split(".")
        if (groups.size != 4) {
            return false
        }
        return try {
            groups.filter { it.isNotEmpty() && !it.startsWith("0") }
                .map { it.toInt() }
                .filter { it in 0..255 }
                .size == 4
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isValidPort(port: String): Boolean = try {
        port.toInt() in (0..65536)
    } catch (e: NumberFormatException) {
        false
    }

}
