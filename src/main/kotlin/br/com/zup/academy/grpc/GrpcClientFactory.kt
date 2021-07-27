package br.com.zup.academy.grpc

import br.com.zup.academy.KeyManagerServiceBuscarGrpc
import br.com.zup.academy.KeyManagerServiceDeleteGrpc
import br.com.zup.academy.KeyManagerServiceGrpc
import br.com.zup.academy.KeyManagerServiceListarGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory {

    @Singleton
    fun keysCadastroStub(@GrpcChannel("chavepix") channel: ManagedChannel)
    :KeyManagerServiceGrpc.KeyManagerServiceBlockingStub{
        return KeyManagerServiceGrpc.newBlockingStub(channel)
    }

    @Singleton
    fun keyDeleteStub(@GrpcChannel("chavepix") channel: ManagedChannel)
    :KeyManagerServiceDeleteGrpc.KeyManagerServiceDeleteBlockingStub{
        return KeyManagerServiceDeleteGrpc.newBlockingStub(channel)
    }

    @Singleton
    fun keyBuscarStub(@GrpcChannel("chavepix") channel: ManagedChannel)
    :KeyManagerServiceBuscarGrpc.KeyManagerServiceBuscarBlockingStub{
        return KeyManagerServiceBuscarGrpc.newBlockingStub(channel)
    }

    @Singleton
    fun keyListarStub(@GrpcChannel("chavepix") channel: ManagedChannel)
    :KeyManagerServiceListarGrpc.KeyManagerServiceListarBlockingStub{
        return KeyManagerServiceListarGrpc.newBlockingStub(channel)
    }
}