package com.example.sabacontinuidad.data

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.FlowType
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

private const val SUPABASE_URL =
    "https://jgpskxaixuxkblhfzqbs.supabase.co"

private const val SUPABASE_PUBLISHABLE_KEY =
    "sb_publishable_xhH2hH97suJNtF2X8Ohykw_f79rK1kr"

object SupabaseClientProvider {

    val client by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_PUBLISHABLE_KEY
        ) {
            install(Auth) {
                flowType = FlowType.PKCE
                scheme = "saba"
                host = "login"
            }

            install(Postgrest)
        }
    }
}