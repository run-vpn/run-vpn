package com.runvpn.app.core.common.domain.usecases

class CheckIPv4ValidUseCase {

    /**
     * Validate IPv4, IPv6 by regex and returns boolean result.
     */
    operator fun invoke(ip: String): Boolean {
        val ipV4Pattern = "^((25[0-5]|(2[0-4]|1\\d|[1-9]|)\\d)\\.?\\b){4}\$"

        val checkerIPv4 = Regex(ipV4Pattern)
        return checkerIPv4.matches(ip)
    }
}
