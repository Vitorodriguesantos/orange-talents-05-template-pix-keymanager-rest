package br.com.zup.academy.dto

import br.com.zup.academy.modelo.ChavePix
import br.com.zup.academy.modelo.DetalhesConta
import br.com.zup.academy.modelo.TipoChave
import br.com.zup.academy.modelo.TipoConta
import br.com.zup.academy.validacao.ValidaPixKey
import br.com.zup.academy.validacao.ValidaUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidaPixKey
@Introspected
data class NovaChavePix(
    @ValidaUUID
    @field:NotBlank
    val clienteId: String?,
    @field:NotNull
    val tipoChave: TipoChave?,
    @Size(max = 77)
    val chave: String?,
    @field:NotNull
    val tipoConta: TipoConta?
) {
    fun converter(conta: DetalhesConta): ChavePix {
        return ChavePix(clienteId = clienteId.toString(),
            tipoChave = TipoChave.valueOf(tipoChave!!.name),
            tipoConta = TipoConta.valueOf(tipoConta!!.name),
            valorChave = if (this.tipoChave == TipoChave.ALEATORIA) {
                UUID.randomUUID().toString()
            } else {
                this.chave!! },
        conta)
    }
}