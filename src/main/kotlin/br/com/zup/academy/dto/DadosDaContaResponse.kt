package br.com.zup.academy.dto

import br.com.zup.academy.modelo.DetalhesConta

data class DadosDaContaResponse(
    val tipo: String,
    val instituicao: InstituicaoResponse,
    val agencia: String,
    val numero: String,
    val titular: TitularResponse
) {
    fun converter(): DetalhesConta {
        return DetalhesConta(instituicao = instituicao.ispb,
                            nomeTitular = titular.nome,
                            cpfTitular = titular.cpf,
                            agencia = agencia,
                            numeroConta = numero)
    }
}

data class TitularResponse(val nome: String, val cpf: String)
data class InstituicaoResponse(val nome: String, val ispb: String)
