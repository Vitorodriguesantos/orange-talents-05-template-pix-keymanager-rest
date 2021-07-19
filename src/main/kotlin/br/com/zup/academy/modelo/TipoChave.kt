package br.com.zup.academy.modelo

import io.micronaut.validation.validator.constraints.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChave {
    CPF {
        override fun validaChave(chave: String?): Boolean {
            if(chave.isNullOrBlank()){
                return false
            }
            if(!chave.matches("[0-9]+".toRegex())){
                return false
            }
            return CPFValidator().run{
                initialize(null)
                isValid(chave,null)
            }
        }
    },
    PHONE {
        override fun validaChave(chave: String?): Boolean {
            if(chave.isNullOrBlank()){
                return false
            }
            return chave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL {
        override fun validaChave(chave: String?): Boolean {
            if(chave.isNullOrBlank()){
                return false
            }
            return EmailValidator().run {
                initialize(null)
                isValid(chave,null)
            }
        }
    },
    RANDOM {
        override fun validaChave(chave: String?) = chave.isNullOrBlank() //se estiver preenchida retorna false
    };

    abstract fun validaChave(chave: String?):Boolean
}