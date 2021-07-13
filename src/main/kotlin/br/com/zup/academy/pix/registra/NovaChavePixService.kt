package br.com.zup.academy.pix.registra

import br.com.zup.academy.dto.NovaChavePix
import br.com.zup.academy.externos.ServicoContasItauClient
import br.com.zup.academy.modelo.ChavePix
import br.com.zup.academy.repository.ChavePixRepository
import br.com.zup.academy.validacao.ChavePixExistenteException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.lang.IllegalStateException
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class NovaChavePixService(@Inject val repository: ChavePixRepository,
                          @Inject val itauClient: ServicoContasItauClient) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun registra(@Valid novaChave: NovaChavePix): ChavePix {

        if(repository.existsByValorChave(novaChave.chave)){
            //criar a classe de erro.
            throw ChavePixExistenteException("Chave pix '${novaChave.chave}' existente")
        }

        val response = itauClient.buscar(novaChave.clienteId!!, novaChave.tipoConta!!.name)
        val conta = response.body()?.converter() ?: throw IllegalStateException("Cliente não encontrado")

        val chave = novaChave.converter(conta)
        repository.save(chave)

        return chave
    }

}
