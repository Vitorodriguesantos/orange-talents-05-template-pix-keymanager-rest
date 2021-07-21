package br.com.zup.academy.dto

import br.com.zup.academy.externos.ServicoContasBcbClient
import br.com.zup.academy.repository.ChavePixRepository

data class PixKeyDetailsResponse(
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: String
){

}
