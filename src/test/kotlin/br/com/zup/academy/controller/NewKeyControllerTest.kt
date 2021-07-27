package br.com.zup.academy.controller

import br.com.zup.academy.KeyManagerRequest
import br.com.zup.academy.KeyManagerResponse
import br.com.zup.academy.KeyManagerServiceGrpc
import br.com.zup.academy.dto.KeyAddRequest
import br.com.zup.academy.grpc.GrpcClientFactory
import br.com.zup.academy.validacao.ErrorBody
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import javax.inject.Singleton

@MicronautTest
internal class NewKeyControllerTest(
    private val newKeyController: NewKeyController,
    private val grpcClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub,
){
    @BeforeEach
    internal fun setUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    fun `deve cadastrar uma chave pix`(){

        val request = KeyAddRequest(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "RANDOM",
            "",
            "CONTA_CORRENTE"
        )
        val pixId = UUID.randomUUID().toString()

        Mockito.`when`(grpcClient.adicionar(request.converterGrpc()))
                .thenReturn(KeyManagerResponse.newBuilder()
                    .setId(pixId)
                    .build())

        val response = newKeyController.novaChave(request)

        with(response){
            assertEquals(HttpStatus.CREATED.code, this.status.code)
            assertTrue(response.headers!!["Location"].contains(pixId))
        }
    }

    @Test
    fun `nao deve cadastrar uma chave pix com chave invalida`(){
        val request = KeyAddRequest(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "PHONE",
            "09013125695",
            "CONTA_CORRENTE"
        )

        val response = newKeyController.novaChave(request)
        with(response){
            assertEquals(HttpStatus.BAD_REQUEST.code, this.status.code)
            assertEquals("Formato esperado para telefone +5511200000000",(response.body() as ErrorBody).descricao)
        }
    }

    @Factory
    //vai substituir o gerador de fabrica (GrpcClientFactory) por esse mockito...
    // podendo assim adicionar os comportamentos necessarios
    @Replaces(factory = GrpcClientFactory::class)
    class MockitoPixManagerRegisterClient {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerServiceGrpc.KeyManagerServiceBlockingStub::class.java)
    }
}
