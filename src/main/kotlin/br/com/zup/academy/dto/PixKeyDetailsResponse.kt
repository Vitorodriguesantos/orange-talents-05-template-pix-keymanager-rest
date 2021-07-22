package br.com.zup.academy.dto

import br.com.zup.academy.externos.ServicoContasBcbClient
import br.com.zup.academy.modelo.ChavePix
import br.com.zup.academy.repository.ChavePixRepository
import java.util.*

data class PixKeyDetailsResponse(
    val pixId: UUID? = null,
    val clienteId: String? = null,
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: String
){

    companion object{
        fun of(chave: ChavePix): PixKeyDetailsResponse {
            return PixKeyDetailsResponse(
                chave.id,
                chave.clienteId,
                chave.tipoChave.name,
                chave.valorChave,
                BankAccount(
                    chave.conta.instituicao,
                    chave.conta.agencia,
                    chave.conta.numeroConta,
                    chave.tipoConta.name
                ),
                Owner(
                    "NATURAL_PERSON",
                    chave.conta.nomeTitular,
                    chave.conta.cpfTitular
                ),
                chave.criadoEm.toString()
            )
        }
    }

    fun converter(): PixKeyDetailsResponse {
        return PixKeyDetailsResponse(
            pixId = pixId,
            clienteId = clienteId,
            keyType = this.keyType,
            key = this.key,
            bankAccount = BankAccount(
                participant = bankAccount.participant,
                branch = bankAccount.branch,
                accountNumber = bankAccount.accountNumber,
                accountType = bankAccount.accountType
            ),
            owner = Owner(
                type = owner.type,
                name = owner.name,
                taxIdNumber = owner.taxIdNumber
            ),
            createdAt = this.createdAt
        )
    }


}
