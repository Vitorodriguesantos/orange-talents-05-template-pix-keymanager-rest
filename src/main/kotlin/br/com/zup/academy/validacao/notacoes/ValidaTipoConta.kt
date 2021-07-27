package br.com.zup.academy.validacao.notacoes

import br.com.zup.academy.modelo.TipoConta
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidAccountTypeValidator::class])
annotation class ValidaTipoConta

@Singleton
class ValidAccountTypeValidator: ConstraintValidator<ValidaTipoConta,String> {
    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<ValidaTipoConta>,
        context: ConstraintValidatorContext
    ): Boolean {
        if (value.isNullOrBlank()) return true

        val verificaValores = TipoConta.values().map { keyType -> keyType.name }

        if (verificaValores.contains(value)) return true

        return false
    }
}
