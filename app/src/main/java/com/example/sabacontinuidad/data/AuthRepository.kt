package com.example.sabacontinuidad.data

import android.app.Activity
import android.content.Context
import com.example.sabacontinuidad.R
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException

class AuthRepository(private val context: Context) {

    private var aplicacion: ISingleAccountPublicClientApplication? = null
    private val scopes = arrayOf("User.Read")

    val listo: Boolean
        get() = aplicacion != null

    fun iniciar(
        cuandoListo: (String?) -> Unit,
        cuandoError: (String) -> Unit
    ) {
        PublicClientApplication.createSingleAccountPublicClientApplication(
            context,
            R.raw.msal_config,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {

                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    aplicacion = application

                    application.getCurrentAccountAsync(
                        object : ISingleAccountPublicClientApplication.CurrentAccountCallback {

                            override fun onAccountLoaded(activeAccount: IAccount?) {
                                cuandoListo(activeAccount?.username)
                            }

                            override fun onAccountChanged(
                                priorAccount: IAccount?,
                                currentAccount: IAccount?
                            ) {
                                cuandoListo(currentAccount?.username)
                            }

                            override fun onError(exception: MsalException) {
                                cuandoError(
                                    exception.message ?: "No se pudo revisar la sesión Microsoft."
                                )
                            }
                        }
                    )
                }

                override fun onError(exception: MsalException) {
                    cuandoError(exception.message ?: "No se pudo iniciar MSAL.")
                }
            }
        )
    }

    @Suppress("DEPRECATION")
    fun iniciarSesion(
        activity: Activity,
        cuandoExito: (String) -> Unit,
        cuandoError: (String) -> Unit,
        cuandoCancela: () -> Unit
    ) {
        val app = aplicacion ?: return

        app.signIn(
            activity,
            null,
            scopes,
            object : AuthenticationCallback {

                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    // Si la organización exige MFA, aquí interviene el Authenticator.
                    cuandoExito(authenticationResult.account.username)
                }

                override fun onError(exception: MsalException) {
                    cuandoError(exception.message ?: "No se pudo iniciar sesión con Microsoft.")
                }

                override fun onCancel() {
                    cuandoCancela()
                }
            }
        )
    }

    fun cerrarSesion(
        cuandoListo: () -> Unit,
        cuandoError: (String) -> Unit
    ) {
        val app = aplicacion
        if (app == null) {
            cuandoListo()
            return
        }

        app.signOut(
            object : ISingleAccountPublicClientApplication.SignOutCallback {

                override fun onSignOut() {
                    cuandoListo()
                }

                override fun onError(exception: MsalException) {
                    cuandoError(exception.message ?: "No se pudo cerrar la sesión Microsoft.")
                }
            }
        )
    }
}