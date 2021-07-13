package br.com.zup.academy.modelo

import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.Valid
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
class ChavePix(
    @field:NotNull @Column(nullable = false) val clienteId: UUID,
    @field:NotNull @Enumerated(EnumType.STRING) @Column(nullable = false) val tipoChave: TipoChave,
    @field:NotNull @Enumerated(EnumType.STRING) @Column(nullable = false) val tipoConta: TipoConta,
    @field:NotBlank @Column(nullable = false, unique = true) val valorChave: String,
    @field:Valid @Embedded val conta: DetalhesConta
){
    @Id
    @GeneratedValue
    @Column(length = 16)
    val id: UUID? = null

    @Column(nullable = false)
    val criadoEm: LocalDateTime = LocalDateTime.now()
}
