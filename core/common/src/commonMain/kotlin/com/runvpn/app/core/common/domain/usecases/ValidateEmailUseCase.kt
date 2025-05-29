package com.runvpn.app.core.common.domain.usecases

class ValidateEmailUseCase {

    companion object {
        private const val PATTERN_EMAIL_ADDRESS =
            "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                    "\\@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
    }

    operator fun invoke(email: String?) =
        (!email.isNullOrEmpty() && email.matches(PATTERN_EMAIL_ADDRESS.toRegex()))

}
