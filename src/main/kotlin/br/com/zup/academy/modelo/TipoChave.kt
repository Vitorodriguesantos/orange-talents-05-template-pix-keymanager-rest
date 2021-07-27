package br.com.zup.academy.modelo

import br.com.zup.academy.validacao.exceptions.BadRequestException
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class TipoChave {
    CPF {
        override fun validate(key: String?) {
            if (key.isNullOrBlank() || !CPFValidator().run {
                    initialize(null)
                    isValid(key, null)
                }) {
                throw BadRequestException("Formato esperado deve ser um CPF válido.")
            }
        }
    }, EMAIL {
        override fun validate(key: String?) {
            if (key.isNullOrBlank() || !key.matches("(?:[a-z0-9!#\$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#\$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])".toRegex())) {
                throw BadRequestException("Formato esperado para email seu@email.com")
            }
        }
    }, PHONE {
        override fun validate(key: String?) {
            if (key.isNullOrBlank() || !key.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())) {
                throw BadRequestException("Formato esperado para telefone +5511200000000")
            }
        }
    }, RANDOM {
        override fun validate(key: String?) {
            if (!key.isNullOrBlank()) throw BadRequestException("Para chaves RANDOMICA não deve ser informada a chave.")
        }
    };

    abstract fun validate(key: String?)
}