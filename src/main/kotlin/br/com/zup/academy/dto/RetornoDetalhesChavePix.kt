package br.com.zup.academy.dto

import br.com.zup.academy.*

object RetornoDetalhesChavePix {
    fun converter(
        dadosChave: PixKeyDetailsResponse,
        dadosRequisicao: BuscaChavePix,
    ): KeyBuscarResponse {
        return KeyBuscarResponse.newBuilder()
            .setPixId(dadosRequisicao.pixId)
            .setClienteId(dadosRequisicao.clienteId)
            .setChavePix(KeyBuscarResponse.ChavePix.newBuilder()
                .setTipoChave(TipoDeChave.valueOf(dadosChave.keyType))
                .setChave(dadosChave.key)
                .setConta(KeyBuscarResponse.ChavePix.Conta.newBuilder()
                    .setTipoConta(if(dadosChave.bankAccount.accountType == "CACC"){
                        TipoDeConta.CONTA_CORRENTE
                    }else TipoDeConta.CONTA_POUPANCA)
                    .setInstituicaoNome(dadosChave.bankAccount.participant)
                    .setInstituicaoIspb(dadosChave.bankAccount.participant)
                    .setTitularNome(dadosChave.owner.name)
                    .setTitularCpf(dadosChave.owner.taxIdNumber)
                    .setAgencia(dadosChave.bankAccount.branch)
                    .setNumero(dadosChave.bankAccount.accountNumber))
                .setCriadaEm(dadosChave.createdAt)
                ).build()
    }
}
