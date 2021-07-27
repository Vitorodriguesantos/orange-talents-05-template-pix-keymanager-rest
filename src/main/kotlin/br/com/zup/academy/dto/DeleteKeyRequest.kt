package br.com.zup.academy.dto

import br.com.zup.academy.validacao.notacoes.ValidaUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class DeleteKeyRequest(
    @field:NotBlank @field:ValidaUUID
    val pixId: String,
    @field:NotBlank @field:ValidaUUID
    val clienteId: String,
)
