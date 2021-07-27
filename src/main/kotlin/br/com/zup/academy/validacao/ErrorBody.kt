package br.com.zup.academy.validacao

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
class ErrorBody(
    val codigoStatus: Int,
    val nome: String,
    val descricao: String,
    val detalhes: List<ErrorDetails>?
)

class ErrorDetails(val descricao: String)

