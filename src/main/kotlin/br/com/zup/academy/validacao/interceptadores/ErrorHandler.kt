package br.com.zup.academy.validacao.interceptadores
import io.micronaut.aop.Around

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Around
annotation class ErrorHandler
