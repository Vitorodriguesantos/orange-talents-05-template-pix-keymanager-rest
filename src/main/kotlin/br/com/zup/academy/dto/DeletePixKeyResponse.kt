package br.com.zup.academy.dto

data class DeletePixKeyResponse(
    val key: String,
    val participant: String,
    val deletedAt: String
)
