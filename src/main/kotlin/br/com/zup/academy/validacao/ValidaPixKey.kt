package br.com.zup.academy.validacao

import br.com.zup.academy.dto.NovaChavePix
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import kotlin.reflect.KClass

@MustBeDocumented
@Target(AnnotationTarget.CLASS,AnnotationTarget.TYPE)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ValidaPixKeyValidator::class])
annotation class ValidaPixKey(
    val message: String = "A chave passada '\${validatedValue.tipoChave}' nao é valida",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []

)
@Singleton
class ValidaPixKeyValidator:ConstraintValidator<ValidaPixKey,NovaChavePix> {

    override fun isValid(
        value: NovaChavePix?,
        annotationMetadata: AnnotationValue<ValidaPixKey>,
        context: ConstraintValidatorContext,
    ): Boolean {
        if(value?.tipoChave == null){
            return false
        }
        return value.tipoChave.validaChave(value.chave)
    }
}
