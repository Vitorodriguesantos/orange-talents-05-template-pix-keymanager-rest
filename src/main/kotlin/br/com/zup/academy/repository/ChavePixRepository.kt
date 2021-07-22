package br.com.zup.academy.repository

import br.com.zup.academy.modelo.ChavePix
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface ChavePixRepository : JpaRepository<ChavePix,UUID>{

    fun existsByValorChave(chave: String?): Boolean
    fun findByValorChave(chave: String): Optional<ChavePix>
    fun findAllByClienteId(clienteId: String): List<ChavePix>


}
