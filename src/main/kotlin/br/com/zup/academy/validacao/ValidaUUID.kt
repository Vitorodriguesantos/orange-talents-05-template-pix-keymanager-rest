package br.com.zup.academy.validacao

import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import kotlin.reflect.KClass

@ReportAsSingleViolation
@Constraint(validatedBy = [])
@Pattern(regexp = "^[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}$",
                flags = [Pattern.Flag.CASE_INSENSITIVE])
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD,AnnotationTarget.PROPERTY,AnnotationTarget.CONSTRUCTOR,AnnotationTarget.VALUE_PARAMETER)
annotation class ValidaUUID(
    val message: String = "não é um formato válido de UUID",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)
