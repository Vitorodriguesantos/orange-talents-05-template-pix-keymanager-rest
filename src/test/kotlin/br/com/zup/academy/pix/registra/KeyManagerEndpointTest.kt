package br.com.zup.academy.pix.registra

import br.com.zup.academy.KeyManagerRequest
import br.com.zup.academy.KeyManagerServiceGrpc
import br.com.zup.academy.TipoDeChave
import br.com.zup.academy.TipoDeConta
import br.com.zup.academy.dto.*
import br.com.zup.academy.externos.ServicoContasBcbClient
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
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject

@MicronautTest(transactional = false)
internal class KeyManagerEndpointTest(
    val repository: ChavePixRepository,
    val grpcClient: KeyManagerServiceGrpc.KeyManagerServiceBlockingStub,
){
    @Inject
    lateinit var itauClient: ServicoContasItauClient

    @Inject
    lateinit var bcbClient: ServicoContasBcbClient

    @BeforeEach
    fun setUp(){
        //preparando cenario
        repository.deleteAll()
    }

    @Test
    fun `deve cadastrar uma chave pix`(){

        val dadosDaContaResponseItau = DadosDaContaResponse(
            "CONTA_CORRENTE",
            InstituicaoResponse("ITAU","60701190"),
            "0001",
            "291900",
            TitularResponse("Rafael M C Ponte","02467781054")
        )

        val dadosDaContaResponseBcb = CreatePixKeyResponse(
            keyType = "PHONE",
            key = "+5534997990088",
            bankAccount = BankAccount("60701190","0001","291900","SVGS"),
            owner = Owner("NATURAL_PERSON","Rafael M C Ponte","02467781054"),
            createdAt = LocalDateTime.now().toString())

        //cenario
        Mockito.`when`(itauClient.buscar(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponseItau))

        Mockito.`when`(bcbClient.cadastrar(CreatePixKeyRequest(
            "PHONE",
            "+5534997990088",
            BankAccount("60701190","0001","291900","SVGS"),
            Owner("NATURAL_PERSON","Rafael M C Ponte","02467781054"))))
            .thenReturn(HttpResponse.created(dadosDaContaResponseBcb))

        //acao
        val response = grpcClient.adicionar(KeyManagerRequest.newBuilder()
            .setId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setTipoDeChave(TipoDeChave.PHONE)
            .setValorChave("+5534997990088")
            .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
            .build())

        //validacao
        with(response){
            Assertions.assertNotNull(id)
            Assertions.assertTrue(repository.existsById(UUID.fromString(id)))
        }
    }

    @Test
    fun `nao deve cadastrar uma chave quando ja existir`(){

        //cenario
        val aChave = repository.save(ChavePix(
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

        //ação -> cadastrar um registro duplicado
        val oErro = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(KeyManagerRequest.newBuilder()
                .setId(aChave.clienteId.toString())
                .setTipoDeChave(TipoDeChave.PHONE)
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
                .setTipoDeChave(TipoDeChave.PHONE)
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

    @Test
    fun `nao deve cadastrar uma chave que nao foi cadastrada no BCB`(){

        val dadosDaContaResponseItau = DadosDaContaResponse(
            "CONTA_CORRENTE",
            InstituicaoResponse("ITAU","60701190"),
            "0001",
            "291900",
            TitularResponse("Rafael M C Ponte","02467781054")
        )

        //cenario
        Mockito.`when`(itauClient.buscar(
            "c56dfef4-7901-44fb-84e2-a2cefb157890",
            "CONTA_CORRENTE"))
            .thenReturn(HttpResponse.ok(dadosDaContaResponseItau))

        Mockito.`when`(bcbClient.cadastrar(CreatePixKeyRequest(
            "PHONE",
            "+5534997990088",
            BankAccount("60701190","0001","291900","SVGS"),
            Owner("NATURAL_PERSON","Rafael M C Ponte","02467781054"))))
            .thenReturn(HttpResponse.badRequest())

        //acao
        val oErro = assertThrows<StatusRuntimeException> {
            grpcClient.adicionar(KeyManagerRequest.newBuilder()
                .setId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setTipoDeChave(TipoDeChave.PHONE)
                .setValorChave("+5534997990088")
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .build())
        }

        //validacao
        with(oErro){
            Assertions.assertEquals("Falha ao registra chave no Banco Central",this.status.description)
            Assertions.assertEquals(Status.FAILED_PRECONDITION.code, this.status.code)
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
    fun enderecoMockItau():ServicoContasItauClient{
        return Mockito.mock(ServicoContasItauClient::class.java)
    }

    @MockBean(ServicoContasBcbClient::class)
    fun enderecoMockBcb():ServicoContasBcbClient{
        return Mockito.mock(ServicoContasBcbClient::class.java)
    }
}