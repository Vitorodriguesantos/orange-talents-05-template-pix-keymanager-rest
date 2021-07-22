package br.com.zup.academy.modelo

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class ChavePix(
    @field:NotNull @Column(nullable = false) val clienteId: String,
    @field:NotNull @Enumerated(EnumType.STRING) @Column(nullable = false) val tipoChave: TipoChave,
    @field:NotNull @Enumerated(EnumType.STRING) @Column(nullable = false) val tipoConta: TipoConta,
    @field:NotBlank @Column(nullable = false, unique = true) val valorChave: String,
    @field:Valid @Embedded val conta: DetalhesConta
){
    //função para verificar se chave pertence ao cliente id passado
    fun pertenceAo(clienteId: UUID) = this.clienteId.equals(clienteId.toString())

    @Id
    @GeneratedValue
    @Column(columnDefinition = "BINARY(16)")
    val id: UUID? = null

    @Column(nullable = false)
    val criadoEm: LocalDateTime = LocalDateTime.now()
}
