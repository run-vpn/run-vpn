package com.runvpn.app.data.servers.domain.usecases

import co.touchlab.kermit.Logger
import com.runvpn.app.data.servers.domain.ServersRepository
import com.runvpn.app.data.servers.domain.entities.SuggestedServers
import com.runvpn.app.data.settings.domain.SuggestedServersMode
import com.runvpn.app.data.settings.domain.repositories.UserSettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * 1. Check favorite is empty. If not - return it
 * 2. Check all server by lastConnectedTime. If result is not empty - return it.
 * 3. If step 1 and 2 are not executed, then return first 3 servers, unique by country.
 */
class GetSuggestedServersUseCase(
    private val userSettingsRepository: UserSettingsRepository,
    private val serversRepository: ServersRepository,
) {
    companion object {
        private const val DEFAULT_LIST_SIZE = 3
        private val logger = Logger.withTag("GetSuggestedServersUseCase")
    }

    operator fun invoke(): Flow<SuggestedServers> = combine(
        serversRepository.allServers,
        userSettingsRepository.suggestedServersMode
    ) { all, mode ->
        val favoriteServers = all.filter { it.isFavorite }

        when (mode) {
            SuggestedServersMode.NONE ->
                return@combine SuggestedServers(
                    SuggestedServersMode.NONE,
                    listOf()
                )

            SuggestedServersMode.AUTO -> {
                val suggestedServers = if (favoriteServers.isNotEmpty()) {
                    SuggestedServers(
                        mode,
                        servers = favoriteServers
                    )
                } else {
                    SuggestedServers(
                        mode,
                        all.groupBy { it.country }
                            .asSequence()
                            .sortedByDescending { it.value.size }
                            .take(DEFAULT_LIST_SIZE)
                            .map { it.value.first() }
                            .toList()
                    )
                }
                return@combine suggestedServers
            }

            SuggestedServersMode.FAVORITES -> {
                return@combine SuggestedServers(
                    mode,
                    servers = favoriteServers
                )
            }

            SuggestedServersMode.RECENT -> {
                val filteredByLastConnectionTime = all
                    .filter { it.lastConnectionTime != null }
                    .sortedByDescending { it.lastConnectionTime }

                return@combine SuggestedServers(
                    mode,
                    servers = filteredByLastConnectionTime
                )
            }

            SuggestedServersMode.RECOMMENDED -> {
                val default = all
                    .groupBy { it.country }
                    .asSequence()
                    .sortedByDescending { it.value.size }
                    .map { it.value.first() }
                    .toList()
                return@combine SuggestedServers(
                    SuggestedServersMode.RECOMMENDED,
                    servers = default
                )
            }

        }
    }
}
