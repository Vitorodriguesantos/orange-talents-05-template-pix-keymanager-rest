package br.com.zup.academy.validacao.exceptions

import java.lang.RuntimeException

class GrpcUnavailableException(override val message: String):RuntimeException()
