package br.com.zup.academy.validacao.interceptadores

import br.com.zup.academy.validacao.exceptions.GrpcNotFoundException
import br.com.zup.academy.validacao.exceptions.GrpcUnavailableException
import io.grpc.Status
import io.grpc.StatusRuntimeException

class GrpcExceptionExecuter {
    companion object {

        fun verificador(e: StatusRuntimeException) {
            val statusCode = e.status.code
            val description = e.status.description
            if (statusCode == Status.UNAVAILABLE.code) throw GrpcUnavailableException("Servidor GRPC indisponível")

            if (statusCode == Status.NOT_FOUND.code) throw GrpcNotFoundException(description!!)
        }
    }
}