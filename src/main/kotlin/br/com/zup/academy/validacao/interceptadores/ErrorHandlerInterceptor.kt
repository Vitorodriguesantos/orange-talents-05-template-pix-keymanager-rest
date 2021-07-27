package br.com.zup.academy.validacao.interceptadores

import br.com.zup.academy.validacao.ErrorBody
import br.com.zup.academy.validacao.ErrorDetails
import br.com.zup.academy.validacao.MakerConstraintViolation
import br.com.zup.academy.validacao.exceptions.BadRequestException
import br.com.zup.academy.validacao.exceptions.GrpcNotFoundException
import br.com.zup.academy.validacao.exceptions.GrpcResponseException
import br.com.zup.academy.validacao.exceptions.GrpcUnavailableException
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpResponseFactory
import io.micronaut.http.HttpStatus
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
@InterceptorBean(ErrorHandler::class)
class ErrorHandlerInterceptor: MethodInterceptor<Any, Any> {
    override fun intercept(context: MethodInvocationContext<Any, Any>): Any? {
        try {
            return context.proceed()
        } catch (ex: Exception) {
            when(ex) {
                is ConstraintViolationException -> {
                    return MakerConstraintViolation.responseBody(ex)
                }
                is BadRequestException -> {
                    return HttpResponse.badRequest(ErrorBody(
                        codigoStatus = HttpStatus.BAD_REQUEST.code,
                        nome = HttpStatus.BAD_REQUEST.name,
                        descricao = ex.message,
                        detalhes = null
                    ))
                }
                is GrpcResponseException -> {
                    return HttpResponseFactory.INSTANCE.status(HttpStatus.UNPROCESSABLE_ENTITY,
                        ErrorBody(
                            codigoStatus = HttpStatus.UNPROCESSABLE_ENTITY.code,
                            nome = HttpStatus.UNPROCESSABLE_ENTITY.name,
                            descricao = "Erro ao comunicar o gerenciador de chave Pix",
                            detalhes = listOf(ErrorDetails(descricao = ex.message))
                        )
                    )
                }
                is GrpcUnavailableException -> {
                    return HttpResponse.serverError(ErrorBody(
                        codigoStatus = HttpStatus.INTERNAL_SERVER_ERROR.code,
                        nome = HttpStatus.INTERNAL_SERVER_ERROR.name,
                        descricao = ex.message,
                        detalhes = null
                    ))
                }
                is GrpcNotFoundException -> {
                    return HttpResponse.notFound(ErrorBody(
                        codigoStatus = HttpStatus.NOT_FOUND.code,
                        nome = HttpStatus.NOT_FOUND.name,
                        descricao = ex.message,
                        detalhes = null
                    ))
                }
                else -> {
                    return HttpResponse.serverError(ErrorBody(
                        codigoStatus = HttpStatus.INTERNAL_SERVER_ERROR.code,
                        nome = HttpStatus.INTERNAL_SERVER_ERROR.name,
                        descricao = "Ooops, erro inesperado. Por favor comunique o desenvolvedor.",
                        detalhes = null
                    ))
                }
            }
        }

        return null
    }
}