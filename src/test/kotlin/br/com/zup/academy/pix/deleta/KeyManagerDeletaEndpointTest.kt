package br.com.zup.academy.pix.deleta

import br.com.zup.academy.KeyDeleteRequest
import br.com.zup.academy.KeyManagerServiceDeleteGrpc
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
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.*
import java.util.*

@MicronautTest(transactional = false)
internal class KeyManagerDeletaEndpointTest(
    val repository: ChavePixRepository,
    val grpcCliente: KeyManagerServiceDeleteGrpc.KeyManagerServiceDeleteBlockingStub,
){
    lateinit var aChavePix: ChavePix

    @BeforeEach
    fun setUp(){
        //preparando cenario
       aChavePix = repository.save(chave(
           tipo = TipoChave.EMAIL,
           chave = "vitor@zup.com.br",
           clienteId = UUID.randomUUID()))
    }

    @AfterEach
    fun terminate(){
        repository.deleteAll()
    }

    @Test
    fun `deve deletar uma chave pix`(){

        val resposta = grpcCliente.remover(KeyDeleteRequest.newBuilder()
            .setClienteId(aChavePix.clienteId.toString())
            .setPixId(aChavePix.id.toString())
            .build())

        //validacao
        with(resposta){
            Assertions.assertEquals("Chave deletada ok!",this.msg)
        }

    }

    @Test
    fun `nao deve deletar uma chave quando nao existir`(){

        val oUUID = UUID.randomUUID().toString()

        val oErro = assertThrows<StatusRuntimeException> {
            grpcCliente.remover(KeyDeleteRequest.newBuilder()
            .setClienteId(aChavePix.clienteId.toString())
            .setPixId(oUUID)
            .build())}

        with(oErro){
            Assertions.assertEquals("Id ${oUUID} não encontrado",oErro.status.description)
            Assertions.assertEquals(Status.NOT_FOUND.code,this.status.code)
        }
    }

    @Test
    fun `nao deve deletar uma chave quando nao pertencer ao cliente`(){

        val oUUID = UUID.randomUUID().toString()

        val oErro = assertThrows<StatusRuntimeException> {
            grpcCliente.remover(KeyDeleteRequest.newBuilder()
                .setClienteId(oUUID)
                .setPixId(aChavePix.id.toString())
                .build())
        }

        with(oErro){
            Assertions.assertEquals("A chave não pertence ao usuario",this.status.description)
            Assertions.assertEquals(Status.INVALID_ARGUMENT.code,this.status.code)
        }
    }


    @Factory
    class Clients{
        @Bean
        fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel): KeyManagerServiceDeleteGrpc.KeyManagerServiceDeleteBlockingStub?{
            return KeyManagerServiceDeleteGrpc.newBlockingStub(channel)
        }
    }

    private fun chave(
        tipo: TipoChave,
        chave: String = UUID.randomUUID().toString(),
        clienteId: UUID = UUID.randomUUID()
    ): ChavePix {
        return ChavePix(
            clienteId = clienteId,
            tipoChave = tipo,
            valorChave = chave,
            tipoConta = TipoConta.CONTA_CORRENTE,
            conta = DetalhesConta(
                instituicao = "UNIBANCO ITAU",
                nomeTitular = "Rafael Ponte",
                cpfTitular = "12345678900",
                agencia = "1218",
                numeroConta = "123456"
            )
        )
    }
}