package br.com.zup.academy.pix

import br.com.zup.academy.*
import br.com.zup.academy.externos.converter
import br.com.zup.academy.pix.deleta.DeletaChaveService
import br.com.zup.academy.pix.registra.NovaChavePixService
import br.com.zup.academy.validacao.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class KeyManagerEndpoint(
    @Inject val serviceNovaChave: NovaChavePixService,
    @Inject val serviceDeletaChave: DeletaChaveService,
): KeyManagerServiceGrpc.KeyManagerServiceImplBase() {


    override fun adicionar(
        request: KeyManagerRequest?,
        responseObserver: StreamObserver<KeyManagerResponse>?,
    ) {
        val novaChave = request?.converter()
        val aChave = serviceNovaChave.registra(novaChave!!)

        responseObserver?.onNext(KeyManagerResponse.newBuilder()
            .setId(aChave.id.toString())
            .build())
        responseObserver?.onCompleted()
    }

    override fun remover(
        request: KeyDeleteRequest?,
        responseObserver: StreamObserver<KeyDeleteResponse>?,
    ) {
        val aChave = request?.converter()
        serviceDeletaChave.deleta(aChave!!)
        responseObserver?.onNext(KeyDeleteResponse.newBuilder()
            .setMsg("Chave deletada ok!")
            .build())
        responseObserver?.onCompleted()
    }
}