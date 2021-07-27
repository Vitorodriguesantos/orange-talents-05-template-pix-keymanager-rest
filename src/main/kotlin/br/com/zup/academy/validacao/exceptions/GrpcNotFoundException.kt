package br.com.zup.academy.validacao.exceptions

import java.lang.RuntimeException

class GrpcNotFoundException(override val message: String): RuntimeException()