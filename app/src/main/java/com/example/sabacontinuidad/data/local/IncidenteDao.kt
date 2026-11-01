package com.example.sabacontinuidad.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
abstract class IncidenteDao {

    @Query("SELECT * FROM incidentes ORDER BY pendiente DESC, fecha DESC")
    abstract fun observarTodos(): Flow<List<IncidenteEntity>>

    @Query("SELECT * FROM incidentes WHERE pendiente = 1")
    abstract suspend fun pendientes(): List<IncidenteEntity>

    @Upsert
    abstract suspend fun upsert(entidad: IncidenteEntity)

    @Upsert
    abstract suspend fun upsertTodos(lista: List<IncidenteEntity>)

    @Query("DELETE FROM incidentes WHERE pendiente = 0")
    abstract suspend fun borrarEnviados()

    @Query("DELETE FROM incidentes WHERE id = :id")
    abstract suspend fun borrar(id: String)

    @Transaction
    open suspend fun reemplazarEnviados(lista: List<IncidenteEntity>) {
        borrarEnviados()
        upsertTodos(lista)
    }
}