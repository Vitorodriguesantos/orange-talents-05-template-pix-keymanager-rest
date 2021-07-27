package br.com.zup.academy.controller

import br.com.zup.academy.*
import br.com.zup.academy.dto.BuscarKeyRequest
import br.com.zup.academy.dto.DeleteKeyRequest
import br.com.zup.academy.grpc.GrpcClientFactory
import br.com.zup.academy.validacao.ErrorBody
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpStatus
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class BuscarKeyControllerTest(
    private val buscarKeyController: BuscarKeyController,
    private val grpcClient: KeyManagerServiceBuscarGrpc.KeyManagerServiceBuscarBlockingStub,
){
    @BeforeEach
    internal fun setUp() {
        Mockito.reset(grpcClient)
    }

    @Test
    fun `deve buscar uma chave pix`(){
        val request = DeleteKeyRequest(
            "4d4e9b98-3203-454a-aba9-1f8426ae946e",
            "c56dfef4-7901-44fb-84e2-a2cefb157890"
        )

        Mockito.`when`(grpcClient.buscar(KeyBuscarRequest.newBuilder()
            .setFiltroPixId(KeyBuscarRequest.FiltroPixId.newBuilder()
                .setClienteId(request.clienteId)
                .setPixId(request.pixId)
                .build())
            .build()))
            .thenReturn(KeyBuscarResponse.newBuilder()
                .setPixId("4d4e9b98-3203-454a-aba9-1f8426ae946e")
                .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setChavePix(KeyBuscarResponse.ChavePix.newBuilder()
                    .setTipoChave(TipoDeChave.RANDOM)
                    .setChave("6782472e-690c-49bd-a1f1-b3752bbe03fa")
                    .setConta(KeyBuscarResponse.ChavePix.Conta.newBuilder()
                        .setTipoConta(TipoDeConta.CONTA_POUPANCA)
                        .setInstituicaoNome("60701190")
                        .setInstituicaoIspb("60701190")
                        .setTitularNome("Rafael M C Ponte")
                        .setTitularCpf("02467781054")
                        .setAgencia("0001")
                        .setNumero("291900")
                        .build())
                    .setCriadaEm("2021-07-22T10:54:27.740970")
                    .build())
                .build())

        val response = buscarKeyController.buscaChave(request.clienteId,request.pixId)

        with(response){
            assertEquals(HttpStatus.OK.code, this.status.code)
        }
    }

    @Test
    fun `nao deve buscar uma chave pix com cliente id invalido`(){
        val request = KeyBuscarRequest.newBuilder()
            .setFiltroPixId(KeyBuscarRequest.FiltroPixId.newBuilder()
                .setPixId("4d4e9b98-3203-454a-aba9-1f8426ae946e")
                .setClienteId("4d4e9b98-3203-454a-aba9-1f8426ae946f")
                .build())

        val responseGrpc = StatusRuntimeException(Status.NOT_FOUND.withDescription("Chave Pix não encontrada"))

        Mockito.`when`(grpcClient.buscar(request.build()))
            .thenThrow(responseGrpc)

        val response = buscarKeyController.buscaChave(request.filtroPixIdBuilder.clienteId,request.filtroPixIdBuilder.pixId)

        with(response){
            assertEquals(HttpStatus.NOT_FOUND.code, this.status.code)
            assertEquals("Chave Pix não encontrada",(this.body() as ErrorBody).descricao)
        }
    }

    @Factory
    //vai substituir o gerador de fabrica (GrpcClientFactory) por esse mockito...
    // podendo assim adicionar os comportamentos necessarios
    @Replaces(factory = GrpcClientFactory::class)
    class MockitoPixManagerBuscarClient {
        @Singleton
        fun stubMock() = Mockito.mock(KeyManagerServiceBuscarGrpc.KeyManagerServiceBuscarBlockingStub::class.java)
    }
}