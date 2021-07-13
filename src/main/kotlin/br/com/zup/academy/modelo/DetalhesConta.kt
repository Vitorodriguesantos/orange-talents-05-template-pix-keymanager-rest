package br.com.zup.academy.modelo

import javax.persistence.Embeddable
import javax.validation.constraints.NotBlank

@Embeddable
data class DetalhesConta(
    @field:NotBlank val instituicao: String,
    @field:NotBlank val nomeTitular: String,
    @field:NotBlank val cpfTitular: String,
    @field:NotBlank val agencia: String,
    @field:NotBlank val numeroConta: String,
)
