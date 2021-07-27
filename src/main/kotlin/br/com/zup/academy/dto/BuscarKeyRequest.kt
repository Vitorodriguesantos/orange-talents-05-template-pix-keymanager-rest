package br.com.zup.academy.dto

import br.com.zup.academy.validacao.notacoes.ValidaUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class BuscarKeyRequest(
    @field:NotBlank @field:ValidaUUID
    val clienteId: String,
    @field:NotBlank @field:ValidaUUID
    val pixId: String,
)
