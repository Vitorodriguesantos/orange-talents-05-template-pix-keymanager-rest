package br.com.zup.academy.externos

import br.com.zup.academy.dto.*
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${endereco.client.bcb}")
interface ServicoContasBcbClient {

    //consumes -> retorno
    //produces -> requisição

    ////cadastrar chave bcb
    @Post(
        "/api/v1/pix/keys",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML])
    fun cadastrar(@Body pixBcbRequest: CreatePixKeyRequest): HttpResponse<CreatePixKeyResponse>

    /////// deletar chave bcb
    @Delete(
        "/api/v1/pix/keys/{key}",
        consumes = [MediaType.APPLICATION_XML],
        produces = [MediaType.APPLICATION_XML])
    fun deletar(
        @PathVariable key: String,
        @Body deletePixKeyRequest: DeletePixKeyRequest
    ): HttpResponse<DeletePixKeyResponse>

    //// buscar chave bcb
    @Get(
        "/api/v1/pix/keys/{key}",
        consumes = [MediaType.APPLICATION_XML])
    fun buscar(@PathVariable key: String):HttpResponse<PixKeyDetailsResponse>

}