package br.com.zup.academy.pix.registra

import br.com.zup.academy.KeyManagerRequest
import br.com.zup.academy.KeyManagerServiceGrpc
import br.com.zup.academy.TipoDeChave
import br.com.zup.academy.TipoDeConta
import br.com.zup.academy.dto.DadosDaContaResponse
import br.com.zup.academy.dto.InstituicaoResponse
import br.com.zup.academy.dto.TitularResponse
import br.com.zup.academy.externos.ServicoContasItauClient
import br.com.zup.academy.modelo.ChavePix
import br.com.zup.academy.modelo.DetalhesConta
import br.com.zup.academy.modelo.TipoChave
import br.com.zup.academy.modelo.TipoConta
import br.com.zup.academy.repository.ChavePixRepository
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Bean
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.experimental.theories.suppliers.TestedOn
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class KeyManagerEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub,
){
    @Inject
    lateinit var itauClient: ServicoContasItauClient

    @BeforeEach
    fun setUp(){
        //preparando cenario
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma chave pix`(){

        val dadosDaContaResponse = DadosDaContaResponse(
            "CONTA_CORRENTE",
            InstituicaoResponse("ITAU","xxx"),
            "0000",
            "545001-1",
            TitularResponse("Juscilek","14501932122")
        )

        //cenario
        Mockito.`when`(itauClient.buscar(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponse))

        val response = grpcClient.adicionar(KeyManagerRequest.newBuilder()
            .setId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.CELULAR)
            .setValorChave("+55988888888")
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build())

        with(response){
            Assertions.assertNotNull(id)
        }
    }

    @Test
    fun `nao deve cadastrar uma chave quando ja existir`(){

        //cenario
        val aChave = repository.save(ChavePix(
            clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890",
            tipoChave = TipoChave.CELULAR,
            tipoConta = TipoConta.CONTA_CORRENTE,
            valorChave = "+5534999999999",
            DetalhesConta(
                "Itau",
                "Vitor",
                "1112223334",
                "0000",
                "1111322"
            )))

        //ação -> cadastrar um registro duplicado
        val oErro = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(KeyManagerRequest.newBuilder()
                .setId(aChave.clienteId)
                .setTipoDeChave(TipoDeChave.CELULAR)
                .setValorChave(aChave.valorChave)
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        //validação
        with(oErro){
            Assertions.assertEquals(Status.ALREADY_EXISTS.code,this.status.code)
            Assertions.assertEquals("Chave pix '+5534999999999' ja esta cadastrada",this.status.description)
        }

    }

    @Test
    fun `nao deve cadastrar uma chave com formato invalido`(){

        //ação -> cadastrar um registro duplicado
        val oErro = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(KeyManagerRequest.newBuilder()
                .setId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoDeChave(TipoDeChave.CPF)
                .setValorChave("+5514900001111")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        //validação
        with(oErro){
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,this.status.code)
            Assertions.assertEquals("registra.novaChave: A chave passada 'CPF' nao é valida",this.status.description)
        }

    }

    @Test
    fun `nao deve cadastrar chave com cliente itau invalido`(){

        //cenario
        Mockito.`when`(itauClient.buscar(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.notFound())

        //ação -> cadastrar um registro duplicado
        val oErro = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(KeyManagerRequest.newBuilder()
                .setId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoDeChave(TipoDeChave.CELULAR)
                .setValorChave("+5534988887777")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        //validação
        with(oErro){
            Assertions.assertEquals(Status.FAILED_PRECONDITION.code,this.status.code)
            Assertions.assertEquals("Cliente não encontrado",this.status.description)
        }

    }

    @Factory
    class Clients{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerServiceGrpc.KeyManagerServiceBlockingStub?{
            return KeyManagerServiceGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ServicoContasItauClient::class)
    fun enderecoMock():ServicoContasItauClient{
        return Mockito.mock(ServicoContasItauClient::class.java)
    }
}