# Conectar Saba Continuidad con Supabase

La app usa Supabase Auth para iniciar sesión con Microsoft y obtener el JWT que requieren las políticas RLS. El botón de Microsoft ya no usa la configuración MSAL local: el flujo de Microsoft pasa por Supabase Auth, que crea la sesión de la base de datos. Si la organización exige MFA, Microsoft Entra solicita la verificación según sus políticas.

## 1. Configuración del cliente Android

La URL y la clave **publishable** están configuradas en `SupabaseClientProvider.kt`. La publishable key está diseñada para clientes móviles; el acceso a los datos sigue limitado por RLS. No pongas una clave `service_role` ni `sb_secret` en la aplicación.

## 2. Activar Microsoft en Supabase Auth

1. En Microsoft Entra, registra una aplicación para el flujo web/OAuth de Supabase. Agrega como URI de redirección:

   ```text
   https://jgpskxaixuxkblhfzqbs.supabase.co/auth/v1/callback
   ```

2. Crea un secreto de cliente en Entra y copia su **Value**.
3. En Supabase → **Authentication → Sign In / Providers → Azure**, habilita el proveedor y pega el Client ID y el Client Secret. Configura el tenant de la organización si el acceso debe limitarse a sus cuentas.
4. En Supabase → **Authentication → URL Configuration → Redirect URLs**, agrega:

   ```text
   com.example.sabacontinuidad://auth
   ```

El secreto de Entra se guarda solo en la configuración del proveedor en Supabase; no se copia al proyecto Android. La aplicación pide el scope `email`, requerido por el proveedor Azure de Supabase.

## 3. Asignar usuario, parking y equipo

La base existente protege los registros mediante RLS y `parking_miembros`. Un usuario autenticado solo ve parkings/equipos donde tiene una asignación activa.

1. Ejecuta la app y completa el primer inicio de sesión con Microsoft.
2. En Supabase → **Authentication → Users**, copia el UUID del usuario creado.
3. En **Table Editor**, crea el parking y el equipo si aún no existen.
4. Crea la fila de asignación en `parking_miembros` con el `parking_id`, el UUID en `usuario_id`, un `rol` (`trabajador` o `supervisor`) y `activo = true`.

Sin esa asignación, la app se autenticará correctamente, pero RLS ocultará los parkings y equipos y no permitirá insertar incidentes. No se agregó una política abierta para evitar que cualquier usuario pueda asignarse a sí mismo.

## Qué conecta esta versión

El login y el registro/listado de incidentes usan Supabase. El formulario resuelve el código ingresado contra `equipos`, y el servidor asigna el parking, el usuario reportante y la fecha. Las otras tablas del esquema (turnos, mantenimientos, procedimientos, archivos, etc.) aún no están conectadas porque esta versión de la app todavía no tiene esas pantallas.
