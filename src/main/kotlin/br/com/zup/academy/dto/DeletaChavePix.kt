package br.com.zup.academy.dto

import br.com.zup.academy.validacao.ValidaUUID
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

class DeletaChavePix(
    @ValidaUUID
    @field:NotBlank
    val clientId: String,
    @field:NotNull
    val pixId: String,
)
