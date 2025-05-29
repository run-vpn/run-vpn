package com.runvpn.app.data.connection

interface VpnConnection {

    /**
     * Starts VPN connection.
     */
    fun connect()

    /**
     * Stops VPN connection.
     */
    fun disconnect()

    /**
     * Pauses VPN connection.
     */
    fun pause()
}
