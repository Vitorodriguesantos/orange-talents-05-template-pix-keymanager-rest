package br.com.zup.academy.controller

import br.com.zup.academy.KeyManagerRequest
import br.com.zup.academy.KeyManagerServiceGrpc
import br.com.zup.academy.dto.KeyAddRequest
import br.com.zup.academy.modelo.TipoChave
import br.com.zup.academy.validacao.exceptions.GrpcResponseException
import br.com.zup.academy.validacao.interceptadores.ErrorHandler
import br.com.zup.academy.validacao.interceptadores.GrpcExceptionExecuter
import io.grpc.StatusRuntimeException
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import io.micronaut.http.uri.UriBuilder
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.ConstraintViolationException
import javax.validation.Validator

@Controller
@ErrorHandler
@Validated
class NewKeyController(
    @Inject private val validator: Validator,
    @Inject val grpcClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub,
) {

    @Post("/api/key")
    fun novaChave(@Body keyRequest: KeyAddRequest): HttpResponse<Any> {
        val errors = validator.validate(keyRequest)
        if (errors.isNotEmpty()) throw ConstraintViolationException(errors)

        TipoChave.valueOf(keyRequest.tipoChave).validate(keyRequest.valorChave)

        val request = keyRequest.converterGrpc()

        try {
            val response = grpcClient.adicionar(request)
            return HttpResponse.created<Any?>(UriBuilder.of("/api/key/{id}")
                .expand(mutableMapOf(Pair("id", response.id))))
                .body(object {val id = response.id})
        }catch (e: StatusRuntimeException){
            val descricao = e.status.description
            GrpcExceptionExecuter.verificador(e)
            throw GrpcResponseException(descricao ?: "Falha ao cadastrar nova chave.")
        }
    }
}