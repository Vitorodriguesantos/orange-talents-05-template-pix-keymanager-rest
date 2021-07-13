package br.com.zup.academy.externos

import br.com.zup.academy.dto.DadosDaContaResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.QueryValue
import io.micronaut.http.client.annotation.Client


@Client("\${endereco.client.itau}")
interface ServicoContasItauClient {

    @Get("/api/v1/clientes/{clienteId}/contas{?tipo}")
    fun buscar(
        @PathVariable clienteId: String,
        @QueryValue tipo: String,
    ): HttpResponse<DadosDaContaResponse>
}