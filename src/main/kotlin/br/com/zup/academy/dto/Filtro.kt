package br.com.zup.academy.dto

import br.com.zup.academy.externos.ServicoContasBcbClient
import br.com.zup.academy.repository.ChavePixRepository
import br.com.zup.academy.validacao.ChavePixNaoExistenteException
import br.com.zup.academy.validacao.ValidaUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import org.slf4j.LoggerFactory
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filtro {

    abstract fun filtra(
        repository: ChavePixRepository,
        bcbClient: ServicoContasBcbClient,
    ) : PixKeyDetailsResponse

    @Introspected
    data class BuscaPorPixId(
        @field:NotBlank @field:ValidaUUID val clienteId: String,
        @field:NotBlank @field:ValidaUUID val pixId: String
    ) : Filtro (){
        fun pixIdAsUuid() = UUID.fromString(pixId)
        fun clienteIdAsUuid() = UUID.fromString(clienteId)

        override fun filtra(
            repository: ChavePixRepository,
            bcbClient: ServicoContasBcbClient,
        ): PixKeyDetailsResponse {
            return repository.findById(pixIdAsUuid())
                .filter { it.pertenceAo(clienteIdAsUuid()) }
                .map(PixKeyDetailsResponse::of)
                .orElseThrow{ChavePixNaoExistenteException("Chave Pix não encontrada")}
        }
    }

    @Introspected
    data class BuscaPorChave(@field:NotBlank @Size(max = 77) val chave: String) : Filtro() { // 1

        private val LOGGER = LoggerFactory.getLogger(this::class.java)

        override fun filtra(
            repository: ChavePixRepository,
            bcbClient: ServicoContasBcbClient,
        ): PixKeyDetailsResponse {
            return repository.findByValorChave(chave)
                .map(PixKeyDetailsResponse::of)
                .orElseGet {
                    LOGGER.info("Consultando chave Pix '$chave' no Banco Central do Brasil (BCB)")

                    val response = bcbClient.buscar(chave) // 1
                    when (response.status) { // 1
                        HttpStatus.OK -> response.body()?.converter() // 1
                        else -> throw ChavePixNaoExistenteException("Chave Pix não encontrada") // 1
                    }
                }
        }
    }

    @Introspected
    class Invalido() : Filtro() {

        override fun filtra(repository: ChavePixRepository, bcbClient: ServicoContasBcbClient): PixKeyDetailsResponse {
            throw IllegalArgumentException("Chave Pix inválida ou não informada")
        }
    }

}
