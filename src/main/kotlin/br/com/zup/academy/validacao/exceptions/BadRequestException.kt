package br.com.zup.academy.validacao.exceptions

import java.lang.RuntimeException

class BadRequestException(override val message: String) : RuntimeException()
