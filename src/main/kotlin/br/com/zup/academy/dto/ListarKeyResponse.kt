package br.com.zup.academy.dto

import br.com.zup.academy.KeyListarResponse

data class ListarKeyResponse(
    val pixId: String,
    val tipoChave: String,
    val valorChave: String,
    val tipoConta: String,
    val criadaEm: String
){
    constructor(grpcResponse: KeyListarResponse.ChavePix)
            :this(
        grpcResponse.pixId,
        grpcResponse.tipoDeChave.name,
        grpcResponse.valorDaChave,
        grpcResponse.tipoDeConta.name,
        grpcResponse.criadaEm)

}
