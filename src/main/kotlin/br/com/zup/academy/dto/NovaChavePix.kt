package br.com.zup.academy.dto

import br.com.zup.academy.modelo.ChavePix
import br.com.zup.academy.modelo.DetalhesConta
import br.com.zup.academy.modelo.TipoChave
import br.com.zup.academy.modelo.TipoConta
import br.com.zup.academy.validacao.ValidaPixKey
import br.com.zup.academy.validacao.ValidaUUID
import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidaPixKey
@Introspected
data class NovaChavePix(
    @ValidaUUID
    @field:NotBlank
    val clienteId: String?,
    @field:NotNull
    val tipoChave: TipoChave?,
    @Size(max = 77)
    val chave: String?,
    @field:NotNull
    val tipoConta: TipoConta?
) {
    fun converterBcb(conta: DetalhesConta): CreatePixKeyRequest {
        return CreatePixKeyRequest(
            keyType = tipoChave!!.name,
            key = chave!!,
            bankAccount = BankAccount(
                participant = conta.instituicao,
                branch = conta.agencia,
                accountNumber = conta.numeroConta,
                accountType = if(TipoConta.valueOf(tipoConta!!.name).equals("CONTA_CORRENTE")){
                    "CACC"
                }else "SVGS"),
            Owner(
                type = if (TipoChave.valueOf(tipoChave!!.name).equals("CNPJ")) {
                    "LEGAL_PERSON"
                } else "NATURAL_PERSON",
                name = conta.nomeTitular,
                taxIdNumber = conta.cpfTitular))}

    fun converterBanco(bcbResponse: CreatePixKeyResponse,conta: DetalhesConta): ChavePix {
        return ChavePix(
            clienteId = clienteId!!,
            tipoChave = TipoChave.valueOf(tipoChave!!.name),
            tipoConta = TipoConta.valueOf(tipoConta!!.name),
            valorChave = if (this.tipoChave == TipoChave.RANDOM) {
                bcbResponse.key
            } else {
                this.chave!! },
            conta = conta
        )
    }

    /*    fun converter(conta: DetalhesConta): ChavePix {
           return ChavePix(clienteId = UUID.fromString(clienteId),
               tipoChave = TipoChave.valueOf(tipoChave!!.name),
               tipoConta = TipoConta.valueOf(tipoConta!!.name),
               valorChave = if (this.tipoChave == TipoChave.ALEATORIA) {
                   UUID.randomUUID().toString()
               } else {
                   this.chave!! },
           conta)
       } ----> ALTERAÇÕES PARA BCB*/
}