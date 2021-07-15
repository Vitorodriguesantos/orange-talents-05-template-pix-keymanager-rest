package br.com.zup.academy.externos

import br.com.zup.academy.*
import br.com.zup.academy.dto.DeletaChavePix
import br.com.zup.academy.dto.NovaChavePix
import br.com.zup.academy.modelo.TipoChave
import br.com.zup.academy.modelo.TipoConta

//extension function
fun KeyManagerRequest.converter(): NovaChavePix{
    return NovaChavePix(clienteId = id,
                        tipoChave = when(tipoDeChave) {
                                    TipoDeChave.PreencheDefaultChave -> null
                                    else -> TipoChave.valueOf(tipoDeChave.name)},
                        chave = valorChave,
                        tipoConta = when(tipoDeConta){
                                    TipoDeConta.PreencheDefaultConta -> null
                                    else -> TipoConta.valueOf(tipoDeConta.name)})
}

fun KeyDeleteRequest.converter() : DeletaChavePix {
    return DeletaChavePix(
        clientId = clienteId,
        pixId = pixId
    )
}