package br.com.zup.academy.pix.registra

import br.com.zup.academy.*
import br.com.zup.academy.externos.converter
import br.com.zup.academy.validacao.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class KeyManagerEndpoint(
    @Inject val serviceNovaChave: NovaChavePixService
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
}