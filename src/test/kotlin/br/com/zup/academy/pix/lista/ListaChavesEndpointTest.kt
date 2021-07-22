package br.com.zup.academy.pix.lista

import br.com.zup.academy.KeyListarRequest
import br.com.zup.academy.KeyManagerServiceListarGrpc
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
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

@MicronautTest(transactional = false)
internal class ListaChavesEndpointTest(
    private val repository: ChavePixRepository,
    private val grpcClient: KeyManagerServiceListarGrpc.KeyManagerServiceListarBlockingStub,
){

    @BeforeEach
    fun setUp(){
        repository.save(ChavePix(
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

        repository.save(ChavePix(
            clienteId = "c56dfef4-7901-44fb-84e2-a2cefb157890",
            tipoChave = TipoChave.CPF,
            tipoConta = TipoConta.CONTA_CORRENTE,
            valorChave = "02467781054",
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
    fun `deve listar todas as chaves de um cliente`(){

        val dadosChave = repository.findByValorChave("02467781054").get()
        val response = grpcClient.listar(KeyListarRequest.newBuilder()
            .setClienteId(dadosChave.clienteId)
            .build())

        with(response){
            assertEquals(response.chavesCount,2)
            assertEquals(response.getChaves(1).pixId, dadosChave.id.toString())
        }
    }

    @Test
    fun `nao deve listar nenhuma chave quando nao for informado o cliente id`() {

        val oErro = assertThrows<StatusRuntimeException> {
            grpcClient.listar(KeyListarRequest.newBuilder()
                .build())

        }

        with(oErro){
            assertEquals(Status.INVALID_ARGUMENT.code, this.status.code)
            assertEquals("Parametro cliente id obrigatorio",this.status.description)
        }
    }

    @Factory
    class Clients{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerServiceListarGrpc.KeyManagerServiceListarBlockingStub?{
            return KeyManagerServiceListarGrpc.newBlockingStub(channel)
        }
    }

}