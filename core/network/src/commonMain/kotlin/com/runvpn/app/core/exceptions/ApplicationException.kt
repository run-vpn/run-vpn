package com.runvpn.app.core.exceptions


/**
 * Main class for all application's exceptions
 */
abstract class ApplicationException(cause: Throwable?) : Exception(cause)

/**
 * This version of application doesn't supports, need update app
 */
class NeedUpdateException(private val msg: String? = null) : ApplicationException(null) {
    override val message: String?
        get() = msg
}

/**
 * No access to data
 */
class UnauthorizedException(cause: Throwable?) : ApplicationException(cause)

/**
 * Received a response from the server, but it is invalid - 4xx, 5xx
 */
class ServerException(cause: Throwable?, override val message: String? = null) :
    ApplicationException(cause)

/**
 * Failed to connect to the server and the problem is most likely on the client
 */
class NoInternetException(cause: Throwable?) : ApplicationException(cause)

/**
 * Timeout error
 */
class NoServerResponseException(cause: Throwable?) : ApplicationException(cause)

/**
 *  Problems parsing json or lack of fields
 */
class DeserializationException(cause: Throwable?) : ApplicationException(cause)

/**
 * Some unknown issue
 */
class UnknownException(cause: Throwable?, override val message: String) :
    ApplicationException(cause)
