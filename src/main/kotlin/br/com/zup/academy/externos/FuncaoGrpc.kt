package br.com.zup.academy.externos

import br.com.zup.academy.*
import br.com.zup.academy.KeyBuscarRequest.FiltroCase.*
import br.com.zup.academy.dto.DeletaChavePix
import br.com.zup.academy.dto.Filtro
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
//extension function
fun KeyDeleteRequest.converter() : DeletaChavePix {
    return DeletaChavePix(
        clientId = clienteId,
        pixId = pixId
    )
}
//extension function para verificar qual a forma de busca, visto que no proto foi criado um
//oneof
fun KeyBuscarRequest.converter() : Filtro {
    val filtro = when(filtroCase){
        FILTROPIXID -> filtroPixId.let {
            Filtro.BuscaPorPixId(it.clienteId,it.pixId)
        }
        CHAVE -> Filtro.BuscaPorChave(chave)
        FILTRO_NOT_SET -> Filtro.Invalido()
    }

    return filtro
}