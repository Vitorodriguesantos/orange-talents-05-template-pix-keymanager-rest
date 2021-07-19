package br.com.zup.academy.externos

import br.com.zup.academy.dto.CreatePixKeyRequest
import br.com.zup.academy.dto.CreatePixKeyResponse
import br.com.zup.academy.dto.DeletePixKeyRequest
import br.com.zup.academy.dto.DeletePixKeyResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Delete
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("\${endereco.client.bcb}")
interface ServicoContasBcbClient {

    @Post(
        "/api/v1/pix/keys",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun cadastrar(@Body pixBcbRequest: CreatePixKeyRequest)
        : HttpResponse<CreatePixKeyResponse>

    @Delete(
        "/api/v1/pix/keys/{key}",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML]
    )
    fun deletar(
        @PathVariable key: String,
        @Body deletePixKeyRequest: DeletePixKeyRequest
    )
        : HttpResponse<DeletePixKeyResponse>
}