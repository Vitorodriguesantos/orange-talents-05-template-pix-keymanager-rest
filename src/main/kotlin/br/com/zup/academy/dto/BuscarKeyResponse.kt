package br.com.zup.academy.dto

import br.com.zup.academy.KeyBuscarResponse

data class BuscarKeyResponse(
    val pixId: String,
    val clienteId: String,
    val chavePix: DadosChavePix,
) {
    constructor(keyBuscarResponse: KeyBuscarResponse)
            : this(
        keyBuscarResponse.pixId,
        keyBuscarResponse.clienteId,
        DadosChavePix(
            keyBuscarResponse.chavePix.tipoChave.name,
            keyBuscarResponse.chavePix.chave,
            DadosContaPix(
                keyBuscarResponse.chavePix.conta.tipoConta.name,
                keyBuscarResponse.chavePix.conta.instituicaoNome,
                keyBuscarResponse.chavePix.conta.instituicaoIspb,
                keyBuscarResponse.chavePix.conta.titularNome,
                keyBuscarResponse.chavePix.conta.titularCpf,
                keyBuscarResponse.chavePix.conta.agencia,
                keyBuscarResponse.chavePix.conta.numero
            ),
            keyBuscarResponse.chavePix.criadaEm
        )
    )
}

data class DadosChavePix(
    val tipoChave: String,
    val valorChave: String,
    val conta: DadosContaPix,
    val criadaEm: String,
)

data class DadosContaPix(
    val tipoConta: String,
    val instituicao: String,
    val ISPB: String,
    val titular: String,
    val CPF: String,
    val agencia: String,
    val numeroConta: String,
)
