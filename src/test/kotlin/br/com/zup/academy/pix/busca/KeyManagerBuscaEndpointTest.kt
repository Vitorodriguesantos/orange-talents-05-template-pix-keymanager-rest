package br.com.zup.academy.pix.busca

import br.com.zup.academy.KeyBuscarRequest
import br.com.zup.academy.KeyManagerServiceBuscarGrpc
import br.com.zup.academy.externos.ServicoContasBcbClient
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class KeyManagerBuscaEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerServiceBuscarGrpc.KeyManagerServiceBuscarBlockingStub,
    ){

    @Inject
    lateinit var bcbClient: ServicoContasBcbClient

    lateinit var chaveCadastrada: ChavePix

    //cadastrando uma chave para consultar
    @BeforeEach
    fun setUp(){
        //preparando cenario
        chaveCadastrada = repository.save(ChavePix(
            clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890",
            tipoChave = TipoChave.PHONE,
            tipoConta = TipoConta.CONTA_CORRENTE,
            valorChave = "+5534999999999",
            DetalhesConta(
                "60701190",
                "Rafael M C Ponte",
                "02467781054",
                "0001",
                "291900"
            )))
    }

    @AfterEach
    fun after(){
        repository.deleteAll()
    }

    @Test
    fun `deve buscar uma chave pix pelo seu valor`(){

        val response = grpcClient.buscar(KeyBuscarRequest.newBuilder()
            .setChave(chaveCadastrada.valorChave)
            .build())

        with(response){
            assertEquals(chaveCadastrada.clienteId.toString(),this.clienteId)
            assertEquals(chaveCadastrada.id.toString(),this.pixId)
        }
    }

    @Test
    fun `deve buscar uma chave pix pelo cliente id e pix id`(){
        //cenario
        val dadosChave = repository.findByValorChave("+5534999999999").get()

        //acao
        val response = grpcClient.buscar(KeyBuscarRequest.newBuilder()
            .setFiltroPixId(KeyBuscarRequest.FiltroPixId.newBuilder()
                .setClienteId(dadosChave.clienteId.toString())
                .setPixId(dadosChave.id.toString())
                .build())
            .build())

        //validacao
        with(response){
           assertEquals(dadosChave.valorChave,this.chavePix.chave)
           assertEquals(dadosChave.tipoChave.name,this.chavePix.tipoChave.name)
        }
    }

    @Test
    fun `nao deve buscar uma chave com valor inexistente`() {

        val chaveAleatoria = UUID.randomUUID().toString()

        Mockito.`when`(bcbClient.buscar(chaveAleatoria))
            .thenReturn(HttpResponse.notFound())

        val oErro = assertThrows<StatusRuntimeException> {
            grpcClient.buscar(KeyBuscarRequest.newBuilder()
                .setChave(chaveAleatoria)
                .build())
        }

        with(oErro){
            assertEquals(Status.NOT_FOUND.code,this.status.code)
            assertEquals("Chave Pix não encontrada",this.status.description)
        }
    }

    @Test
    fun `nao deve buscar uma chave que nao pertence ao cliente id`(){

        val dadosChave = repository.findByValorChave("+5534999999999").get()
        val clienteAleatorio = UUID.randomUUID().toString()

        val oErro = assertThrows<StatusRuntimeException> {
            grpcClient.buscar(KeyBuscarRequest.newBuilder()
                .setFiltroPixId(KeyBuscarRequest.FiltroPixId.newBuilder()
                    .setClienteId(clienteAleatorio)
                    .setPixId(dadosChave.id.toString())
                    .build())
                .build())
        }

        with(oErro){
            assertEquals(Status.NOT_FOUND.code,this.status.code)
            assertEquals("Chave Pix não encontrada",this.status.description)
        }
    }

    @Test
    fun `nao deve buscar uma chave com id invalido`(){
        val dadosChave = repository.findByValorChave("+5534999999999").get()
        val chaveAleatoria = UUID.randomUUID().toString()

        val oErro = assertThrows<StatusRuntimeException> {
            grpcClient.buscar(KeyBuscarRequest.newBuilder()
                .setFiltroPixId(KeyBuscarRequest.FiltroPixId.newBuilder()
                    .setClienteId(dadosChave.id.toString())
                    .setPixId(chaveAleatoria)
                    .build())
                .build())
        }

        with(oErro){
            assertEquals(Status.NOT_FOUND.code,this.status.code)
            assertEquals("Chave Pix não encontrada",this.status.description)
        }
    }

    @Test
    fun `nao deve buscar uma chave quando nao for passado nenhum parametro`(){

        val oErro = assertThrows<StatusRuntimeException> {
            grpcClient.buscar(KeyBuscarRequest.newBuilder()
                .build())
        }

        with(oErro){
            assertEquals(Status.INVALID_ARGUMENT.code,this.status.code)
            assertEquals("Chave Pix inválida ou não informada",this.status.description)
        }
    }

    @Factory
    class Clients{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerServiceBuscarGrpc.KeyManagerServiceBuscarBlockingStub?{
            return KeyManagerServiceBuscarGrpc.newBlockingStub(channel)
        }
    }

    @MockBean(ServicoContasBcbClient::class)
    fun enderecoMockBcb():ServicoContasBcbClient{
        return Mockito.mock(ServicoContasBcbClient::class.java)
    }
}