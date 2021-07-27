package br.com.zup.academy.validacao
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.MutableHttpResponse
import javax.validation.ConstraintViolationException

class MakerConstraintViolation {
    companion object {
        fun responseBody(exception: ConstraintViolationException): MutableHttpResponse<ErrorBody> {
            val fieldMessages = exception.constraintViolations.map {
                    constraint -> ErrorDetails(descricao = "${constraint.propertyPath.last()} inválido. ${constraint.message}")
            }

            return HttpResponse.badRequest(
                ErrorBody(
                    HttpStatus.BAD_REQUEST.code,
                    nome = HttpStatus.BAD_REQUEST.name,
                    descricao = "Campos preenchidos de forma incorreta.",
                    detalhes = fieldMessages
                ))
        }
    }
}