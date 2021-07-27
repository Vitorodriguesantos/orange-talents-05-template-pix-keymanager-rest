package br.com.zup.academy.controller

import br.com.zup.academy.KeyBuscarRequest
import br.com.zup.academy.KeyDeleteRequest
import br.com.zup.academy.KeyManagerServiceBuscarGrpc
import br.com.zup.academy.dto.BuscarKeyRequest
import br.com.zup.academy.dto.BuscarKeyResponse
import br.com.zup.academy.validacao.exceptions.GrpcResponseException
import br.com.zup.academy.validacao.interceptadores.ErrorHandler
import br.com.zup.academy.validacao.interceptadores.GrpcExceptionExecuter
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.ConstraintViolationException
import javax.validation.Validator

@Controller
@ErrorHandler
@Validated
class BuscarKeyController(
    @Inject private val grpcClient: KeyManagerServiceBuscarGrpc.KeyManagerServiceBuscarBlockingStub,
    @Inject private val validador: Validator,
) {
    @Get("/api/key/{clienteId}/{pixId}")
    fun buscaChave(
        @PathVariable clienteId: String,
        @PathVariable pixId: String,
    ): HttpResponse<Any>{
        val buscarKeyRequest = BuscarKeyRequest(clienteId,pixId)
        val errors = validador.validate(buscarKeyRequest)
        if (errors.isNotEmpty())
            throw ConstraintViolationException(errors)
        try {
            val response = grpcClient.buscar(KeyBuscarRequest.newBuilder()
                .setFiltroPixId(KeyBuscarRequest.FiltroPixId.newBuilder()
                    .setPixId(pixId)
                    .setClienteId(clienteId)
                    .build())
                .build())

            return HttpResponse.ok(BuscarKeyResponse(response))
        }catch (e: StatusRuntimeException){
            val descricao = e.status.description
            GrpcExceptionExecuter.verificador(e)
            throw GrpcResponseException(descricao ?: "Falha ao buscar chave.")
        }
    }
}