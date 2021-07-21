package br.com.zup.academy.dto

import br.com.zup.academy.externos.ServicoContasBcbClient
import br.com.zup.academy.repository.ChavePixRepository
import br.com.zup.academy.validacao.ChavePixNaoExistenteException
import io.micronaut.http.HttpStatus
import java.lang.IllegalStateException
import java.util.*

data class BuscaChavePix(
    val clienteId: String,
    val pixId: String,
) {
    //função para converter os dados de buscar uma chave
    fun converter(chave: BuscaChavePix, repository: ChavePixRepository, bcbClient: ServicoContasBcbClient): PixKeyDetailsResponse {
        val possivelChave = repository.findById(UUID.fromString(chave.pixId))
        if(possivelChave.isEmpty){
            throw ChavePixNaoExistenteException("Chave Pix não cadastrada")
        }
        if(possivelChave.get().clienteId != UUID.fromString(chave.clienteId)){
            throw IllegalStateException("Chave não pertence ao usuario")
        }
        val retornoBcb = bcbClient.buscar(possivelChave.get().valorChave)
        if(retornoBcb.status != HttpStatus.OK){
            throw IllegalStateException("Chave não cadastrada no Banco Central")
        }
        val dadosChave = retornoBcb.body()
        val pixKeyDetailsResponse = PixKeyDetailsResponse(
            keyType = dadosChave.keyType, key = dadosChave.key,
            bankAccount = BankAccount(
                participant = dadosChave.bankAccount.participant,
                branch = dadosChave.bankAccount.branch,
                accountNumber = dadosChave.bankAccount.accountNumber,
                accountType = dadosChave.bankAccount.accountType),
            owner = Owner(
                type = dadosChave.owner.type,
                name = dadosChave.owner.name,
                taxIdNumber = dadosChave.owner.taxIdNumber),
            createdAt = dadosChave.createdAt
        )
        return pixKeyDetailsResponse
    }
}
