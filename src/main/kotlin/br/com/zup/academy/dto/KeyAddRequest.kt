package br.com.zup.academy.dto

import br.com.zup.academy.KeyManagerRequest
import br.com.zup.academy.TipoDeChave
import br.com.zup.academy.TipoDeConta
import br.com.zup.academy.validacao.notacoes.ValidaTipoChave
import br.com.zup.academy.validacao.notacoes.ValidaTipoConta
import br.com.zup.academy.validacao.notacoes.ValidaUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class KeyAddRequest(
    @field:NotBlank @field:ValidaUUID val clienteId: String,
    @field:NotBlank @field:ValidaTipoChave val tipoChave: String,
    val valorChave: String?,
    @field:NotBlank @field:ValidaTipoConta val tipoConta: String,
) {
    fun converterGrpc(): KeyManagerRequest {
        return KeyManagerRequest.newBuilder()
            .setId(clienteId)
            .setTipoDeChave(TipoDeChave.valueOf(tipoChave))
            .setValorChave(valorChave)
            .setTipoDeConta(TipoDeConta.valueOf(tipoConta))
            .build()
    }
}
