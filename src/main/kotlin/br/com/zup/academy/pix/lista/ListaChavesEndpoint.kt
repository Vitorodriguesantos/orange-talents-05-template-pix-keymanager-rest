package br.com.zup.academy.pix.lista

import br.com.zup.academy.*
import br.com.zup.academy.repository.ChavePixRepository
import br.com.zup.academy.validacao.ErrorHandler
import io.grpc.stub.StreamObserver
import java.lang.IllegalArgumentException
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorHandler
class ListaChavesEndpoint(
    @Inject private val repository: ChavePixRepository
) : KeyManagerServiceListarGrpc.KeyManagerServiceListarImplBase(){

    override fun listar(
        request: KeyListarRequest,
        responseObserver: StreamObserver<KeyListarResponse>,
    ) {
        if(request.clienteId.isNullOrBlank())
            throw IllegalArgumentException("Parametro cliente id obrigatorio")

        val clienteId = request.clienteId
        val asChaves = repository.findAllByClienteId(clienteId).map {
            KeyListarResponse.ChavePix.newBuilder()
                .setPixId(it.id.toString())
                .setTipoDeChave(TipoDeChave.valueOf(it.tipoChave.name))
                .setValorDaChave(it.valorChave)
                .setTipoDeConta(TipoDeConta.valueOf(it.tipoConta.name))
                .setCriadaEm(it.criadoEm.toString())
                .build()
        }

        responseObserver.onNext(KeyListarResponse.newBuilder()
            .setClienteId(clienteId.toString())
            .addAllChaves(asChaves)
            .build())

        responseObserver.onCompleted()
    }

}