package br.com.zup.academy.pix.busca

import br.com.zup.academy.*
import br.com.zup.academy.dto.RetornoDetalhesChavePix
import br.com.zup.academy.externos.ServicoContasBcbClient
import br.com.zup.academy.externos.converter
import br.com.zup.academy.repository.ChavePixRepository
import br.com.zup.academy.validacao.ErrorHandler
import io.grpc.stub.StreamObserver
import javax.inject.Singleton

@ErrorHandler
@Singleton
class KeyManagerBuscaEndpoint(
    private val repository: ChavePixRepository,
    private val bcbClient: ServicoContasBcbClient
):KeyManagerServiceBuscarGrpc.KeyManagerServiceBuscarImplBase() {

    override fun buscar(
        request: KeyBuscarRequest,
        responseObserver: StreamObserver<KeyBuscarResponse>,
    ) {
        val aChave = request.converter()
        val dadosChave = aChave.filtra(repository,bcbClient)
        responseObserver.onNext(RetornoDetalhesChavePix.converter(dadosChave))
        responseObserver.onCompleted()

    }

}