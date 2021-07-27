package br.com.zup.academy.controller

import br.com.zup.academy.KeyDeleteRequest
import br.com.zup.academy.KeyDeleteResponse
import br.com.zup.academy.KeyManagerServiceDeleteGrpc
import br.com.zup.academy.KeyManagerServiceGrpc
import br.com.zup.academy.dto.DeleteKeyRequest
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
import javax.inject.Singleton

@MicronautTest
internal class DeleteKeyControllerTest(
    private val deleteKeyController: DeleteKeyController,
    private val grpcClient: KeyManagerServiceDeleteGrpc.KeyManagerServiceDeleteBlockingStub
){
    @BeforeEach
    internal fun setUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    fun `deve deletar uma chave pix existente`(){
        val request = DeleteKeyRequest(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "c56dfef4-7901-44fb-84e2-a2cefb157890"
        )

        Mockito.`when`(grpcClient.remover(KeyDeleteRequest.newBuilder()
            .setClienteId(request.clienteId)
            .setPixId(request.pixId)
            .build()))
            .thenReturn(KeyDeleteResponse.newBuilder()
                .setMsg("Deletado ok")
            .build())

        val response = deleteKeyController.deletaChave(request.pixId,request.clienteId)

        with(response){
            assertEquals(HttpStatus.NO_CONTENT.code, this.status.code)
        }
    }

    @Test
    fun `nao deve deletar uma chave com pixId invalido`(){
        val request = DeleteKeyRequest(
            "",
            "c56dfef4-7901-44fb-84e2-a2cefb157890"
        )

        Mockito.`when`(grpcClient.remover(KeyDeleteRequest.newBuilder()
            .setClienteId(request.clienteId)
            .setPixId(request.pixId)
            .build()))
            .thenReturn(KeyDeleteResponse.newBuilder()
                .setMsg("Deletado ok")
                .build())

        val response = deleteKeyController.deletaChave(request.pixId,request.clienteId)

        with(response){
            assertEquals(HttpStatus.BAD_REQUEST.code, this.status.code)
            assertEquals("Campos preenchidos de forma incorreta.",(this.body() as ErrorBody).descricao)
        }
    }

    @Factory
    //vai substituir o gerador de fabrica (GrpcClientFactory) por esse mockito...
    // podendo assim adicionar os comportamentos necessarios
    @Replaces(factory = GrpcClientFactory::class)
    class MockitoPixManagerDeleteClient {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerServiceDeleteGrpc.KeyManagerServiceDeleteBlockingStub::class.java)
    }
}