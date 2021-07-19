package br.com.zup.academy.dto

data class CreatePixKeyResponse (
    val keyType: String,
    val key: String,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: String
)
