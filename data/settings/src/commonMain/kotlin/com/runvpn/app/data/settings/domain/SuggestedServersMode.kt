package com.runvpn.app.data.settings.domain

enum class SuggestedServersMode {
    NONE,
    AUTO,
    FAVORITES,
    RECENT,
    RECOMMENDED;

    companion object {
        val DEFAULT: SuggestedServersMode
            get() = AUTO
    }
}
