package com.runvpn.app.feature.subscription


fun String.toPriceFormat(current: String): String? {
    val numericString = this.replace(("""[^0-9$-,. ]""").toRegex(), "")
    val s = numericString.replace("""[$, ]""".toRegex(), ".")
    if (s.startsWith("0") && s.length > 1 && !s.startsWith("0.")) return null
    if (this.length > current.length && current.contains(".") && s.endsWith(".")) return null
    if (s.contains(".") && s.split(".")[1].length > 2) return null
    if (s.contains(".") && s.split(".").size != 2) return null
    return s
}
