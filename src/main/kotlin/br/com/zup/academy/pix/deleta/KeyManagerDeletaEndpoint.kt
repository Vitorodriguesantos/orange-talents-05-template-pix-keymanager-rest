package br.com.zup.academy.pix.deleta

import br.com.zup.academy.KeyDeleteRequest
import br.com.zup.academy.KeyDeleteResponse
import br.com.zup.academy.KeyManagerServiceDeleteGrpc
import br.com.zup.academy.externos.converter
import br.com.zup.academy.validacao.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Inject
import javax.inject.Singleton

@ErrorHandler
@Singleton
class KeyManagerDeletaEndpoint (@Inject val serviceDeletaChave: DeletaChaveService)
    : KeyManagerServiceDeleteGrpc.KeyManagerServiceDeleteImplBase() {
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