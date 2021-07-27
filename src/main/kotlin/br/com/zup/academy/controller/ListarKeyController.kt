package br.com.zup.academy.controller

import br.com.zup.academy.KeyListarRequest
import br.com.zup.academy.KeyManagerServiceListarGrpc
import br.com.zup.academy.dto.ListarKeyResponse
import br.com.zup.academy.validacao.exceptions.GrpcResponseException
import br.com.zup.academy.validacao.interceptadores.ErrorHandler
import br.com.zup.academy.validacao.interceptadores.GrpcExceptionExecuter
import br.com.zup.academy.validacao.notacoes.ValidaUUID
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.MutableHttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.validation.Validated

@Controller
@ErrorHandler
@Validated
class ListarKeyController(
    private val grpcClient: KeyManagerServiceListarGrpc.KeyManagerServiceListarBlockingStub
){
    @Get("/api/key/{clienteId}")
    fun listarChaves(@PathVariable @ValidaUUID clienteId: String)
    :MutableHttpResponse<List<ListarKeyResponse>>{

        try {
        val response = grpcClient.listar(KeyListarRequest.newBuilder()
            .setClienteId(clienteId)
            .build())
        return HttpResponse.ok(response.chavesList.map(::ListarKeyResponse))

        }catch (e: StatusRuntimeException){
            val description = e.status.description
            GrpcExceptionExecuter.verificador(e)
            throw GrpcResponseException(description ?: "Erro inesperado, contate o desenvolvedor.")
        }
    }
}