package br.com.zup.academy.pix.deleta

import br.com.zup.academy.dto.DeletaChavePix
import br.com.zup.academy.dto.DeletePixKeyRequest
import br.com.zup.academy.externos.ServicoContasBcbClient
import br.com.zup.academy.externos.ServicoContasItauClient
import br.com.zup.academy.repository.ChavePixRepository
import br.com.zup.academy.validacao.ChavePixExistenteException
import br.com.zup.academy.validacao.ChavePixNaoExistenteException
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class DeletaChaveService(
    @Inject val repository: ChavePixRepository,
    @Inject val bcbClient: ServicoContasBcbClient,
) {

    private val LOGGER = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun deleta(@Valid aChave: DeletaChavePix) {

        val possivelChave = repository.findById(UUID.fromString(aChave.pixId))
        if(possivelChave.isEmpty){
            throw ChavePixNaoExistenteException("Id ${aChave.pixId} não encontrado")
        }
        if(!possivelChave.get().clienteId.toString().equals(aChave.clientId)){
            throw IllegalArgumentException("A chave não pertence ao usuario")
        }
        repository.deleteById(possivelChave.get().id!!)
        LOGGER.info("A CHAVEEEE -> "+possivelChave.get().valorChave)
        //chamanda serviço bcb
        val deleteResponse = bcbClient.deletar(
            possivelChave.get().valorChave,
            DeletePixKeyRequest(
                possivelChave.get().valorChave,
                possivelChave.get().conta.instituicao
            )
        )
        LOGGER.info("CODEE -> "+deleteResponse.status)
        //abortar transação caso
        if(deleteResponse.status != HttpStatus.OK){
            throw IllegalStateException("Falha ao deletar chave no Banco Central")
        }
    }

}
