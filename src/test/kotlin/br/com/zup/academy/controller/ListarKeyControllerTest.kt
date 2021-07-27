package br.com.zup.academy.controller

import br.com.zup.academy.*
import br.com.zup.academy.grpc.GrpcClientFactory
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.inject.Singleton

@MicronautTest
internal class ListarKeyControllerTest(
    private val listarKeyController: ListarKeyController,
    private val grpcClient: KeyManagerServiceListarGrpc.KeyManagerServiceListarBlockingStub,
){
    @BeforeEach
    internal fun setUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    fun `deve listar as chaves de um cliente id`(){

        Mockito.`when`(grpcClient.listar(KeyListarRequest.newBuilder()
            .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .build()))
            .thenReturn(KeyListarResponse.newBuilder().addChaves(
                            KeyListarResponse.ChavePix.newBuilder()
                                .setPixId("4d4e9b98-3203-454a-aba9-1f8426ae946e")
                                .setTipoDeChave(TipoDeChave.valueOf("RANDOM"))
                                .setValorDaChave("6782472e-690c-49bd-a1f1-b3752bbe03fa")
                                .setTipoDeConta(TipoDeConta.valueOf("CONTA_CORRENTE"))
                                .setCriadaEm("2021-07-22T10:54:27.740970"))
                                .build())

        val response = listarKeyController.listarChaves("c56dfef4-7901-44fb-84e2-a2cefb157890")

        with(response){
            assertEquals(HttpStatus.OK.code,this.status.code)
            assertTrue(body.isPresent)
        }
    }


    @Factory
    //vai substituir o gerador de fabrica (GrpcClientFactory) por esse mockito...
    // podendo assim adicionar os comportamentos necessarios
    @Replaces(factory = GrpcClientFactory::class)
    class MockitoPixManagerListarClient {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerServiceListarGrpc.KeyManagerServiceListarBlockingStub::class.java)
    }
}