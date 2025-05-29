package com.runvpn.app.android.vpn.wireguard

import com.wireguard.android.backend.Tunnel


class WireGuardTunnel : Tunnel {
    interface OnVpnStateListener {
        fun onVpnStateChanged(newState: Tunnel.State)
    }

    private var mListener: OnVpnStateListener? = null
    fun setOnVpnStateListener(listener: OnVpnStateListener?) {
        mListener = listener
    }

    override fun getName(): String {
        return "wgpreconf"
    }

    override fun onStateChange(newState: Tunnel.State) {
        if (mListener != null) mListener!!.onVpnStateChanged(newState)
    }
}


