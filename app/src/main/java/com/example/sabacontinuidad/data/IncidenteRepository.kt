package com.example.sabacontinuidad.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

private val Context.dataStore by preferencesDataStore(name = "saba_incidentes")

class IncidenteRepository(private val context: Context) {

    private val clave = stringPreferencesKey("incidentes_json")

    val incidentes: Flow<List<Incidente>> =
        context.dataStore.data.map { prefs -> desdeJson(prefs[clave]) }

    suspend fun guardar(incidente: Incidente) {
        context.dataStore.edit { prefs ->
            val lista = desdeJson(prefs[clave]).toMutableList()
            lista.add(0, incidente)
            prefs[clave] = aJson(lista)
        }
    }

    private fun aJson(lista: List<Incidente>): String {
        val arreglo = JSONArray()
        lista.forEach { i ->
            arreglo.put(
                JSONObject()
                    .put("id", i.id)
                    .put("equipo", i.equipo)
                    .put("area", i.area)
                    .put("problema", i.problema)
                    .put("descripcion", i.descripcion)
                    .put("fecha", i.fecha)
            )
        }
        return arreglo.toString()
    }

    private fun desdeJson(texto: String?): List<Incidente> {
        if (texto.isNullOrBlank()) return emptyList()
        val arreglo = JSONArray(texto)
        return List(arreglo.length()) { n ->
            val o = arreglo.getJSONObject(n)
            Incidente(
                id = o.getLong("id"),
                equipo = o.getString("equipo"),
                area = o.getString("area"),
                problema = o.getString("problema"),
                descripcion = o.getString("descripcion"),
                fecha = o.getLong("fecha")
            )
        }
    }
}