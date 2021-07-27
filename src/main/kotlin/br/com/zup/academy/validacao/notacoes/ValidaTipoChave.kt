package br.com.zup.academy.validacao.notacoes

import br.com.zup.academy.modelo.TipoChave
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint

@MustBeDocumented
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidKeyTypeValidator::class])
annotation class ValidaTipoChave(val message: String = "Tipo de chave informado não é válido")

@Singleton
class ValidKeyTypeValidator: ConstraintValidator<ValidaTipoChave, String> {
    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<ValidaTipoChave>,
        context: ConstraintValidatorContext
    ): Boolean {
        if(value.isNullOrBlank())
            return true

        val verificaValores = TipoChave.values().map { tipoChave -> tipoChave.name }

        if(verificaValores.contains(value))
            return true

        return false
    }
}
