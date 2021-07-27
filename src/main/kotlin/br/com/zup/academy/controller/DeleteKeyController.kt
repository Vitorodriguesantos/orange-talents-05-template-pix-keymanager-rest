package br.com.zup.academy.controller

import br.com.zup.academy.KeyDeleteRequest
import br.com.zup.academy.KeyManagerServiceDeleteGrpc
import br.com.zup.academy.dto.DeleteKeyRequest
import br.com.zup.academy.validacao.exceptions.GrpcResponseException
import br.com.zup.academy.validacao.interceptadores.ErrorHandler
import br.com.zup.academy.validacao.interceptadores.GrpcExceptionExecuter
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.ConstraintViolationException
import javax.validation.Validator

@Controller
@ErrorHandler
@Validated
class DeleteKeyController(
    @Inject private val grpcClient: KeyManagerServiceDeleteGrpc.KeyManagerServiceDeleteBlockingStub,
    @Inject private val validador: Validator
) {

    @Delete("/api/key/{pixId}")
    fun deletaChave(
        @PathVariable pixId: String,
        clienteId: String,
    ):HttpResponse<Any>{
        val deleteKeyRequest = DeleteKeyRequest(pixId,clienteId)
        val errors = validador.validate(deleteKeyRequest)
        if (errors.isNotEmpty())
            throw ConstraintViolationException(errors)

        try {
            val request = grpcClient.remover(KeyDeleteRequest.newBuilder()
                .setPixId(pixId)
                .setClienteId(clienteId)
                .build())
            return HttpResponse.noContent()
        }catch (e: StatusRuntimeException){
            val descricao = e.status.description
            GrpcExceptionExecuter.verificador(e)
            throw GrpcResponseException(descricao ?: "Falha ao remover a chave.")
        }

    }

}