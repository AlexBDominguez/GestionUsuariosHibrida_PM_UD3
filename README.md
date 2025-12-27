 PM_UD3 - Gestión de Usuarios Híbrido

Aplicación Android desarrollada en Kotlin con Jetpack Compose para la gestión de usuarios con sincronización híbrida entre base de datos local y servidor remoto.

- Descripción

Esta aplicación permite gestionar una lista de usuarios con funcionalidad completa de CRUD (Create, Read, Update, Delete) y
sincronización automática con un servidor MockAPI. 
La aplicación funciona tanto online como offline, manteniendo los datos localmente y sincronizando cuando hay conectividad.

1. Características Principales

- Gestión completa de usuarios: Crear, editar, eliminar y visualizar usuarios
- Sincronización híbrida: Base de datos local Room + servidor MockAPI
- Funcionamiento offline: La aplicación funciona sin conexión a internet
- Usuarios de prueba: Generación aleatoria de usuarios para testing
- Detección de movimiento: Añadir usuarios mediante shake del dispositivo
- Interfaz moderna: Desarrollada con Jetpack Compose y Material 3

2. Arquitectura

La aplicación sigue una arquitectura MVVM (Model-View-ViewModel)

3. Componentes Principales

UserDao: Interfaz de acceso a datos Room para operaciones locales
- Consultas de lectura con Flow para actualizaciones en tiempo real
- Operaciones de escritura (insertar, actualizar, eliminar)
- Gestión de usuarios pendientes de sincronización

MockApiService: Cliente Retrofit para comunicación con el servidor
- Endpoints REST para operaciones CRUD
- Comunicación asíncrona con el servidor MockAPI

UserRepository: Coordinador entre datos locales y remotos
- Lógica de sincronización bidireccional
- Gestión de conflictos y duplicados
- Control de estados pendientes (pendingSync, pendingDelete)

UserViewModel: Lógica de presentación y estado de UI
- Gestión del estado de la interfaz de usuario
- Coordinación de operaciones del usuario
- Manejo de eventos y mensajes del sistema

4. Tecnologías Utilizadas

- Kotlin: Lenguaje principal
- Jetpack Compose: Framework de UI moderna
- Room: Base de datos local SQLite
- Retrofit: Cliente HTTP para API REST
- Coroutines Programación asíncrona
- Flow: Streams reactivos

5. Funcionalidades
 
Gestión de Usuarios
- Añadir usuarios manualmente a través de formulario
- Editar información de usuarios existentes
- Eliminar usuarios con confirmación
- Visualización en lista con tarjetas

Usuarios de Prueba
- Generación aleatoria de usuarios de prueba
- Activación por botón o mediante shake del dispositivo
- Pool de 6 usuarios predefinidos con datos únicos

Sincronización
- Sincronización manual mediante botón de refresh
- Subida de cambios locales al servidor
- Descarga de actualizaciones del servidor
- Resolución automática de conflictos

Características Técnicas
- Detección de duplicados por email
- Funcionamiento offline completo
- Persistencia local con Room
- Manejo robusto de errores
- Interfaz responsiva con Snackbar

6. Estructura del Proyecto


app/src/main/java/com/example/pm_ud3/
├── data/
│   ├── local/           # Entidades Room y DAO
│   ├── remote/          # Modelos para API
│   ├── UserRepository   # Interfaz del repositorio
│   └── DefaultUserRepository # Implementación
├── network/
│   └── MockApiService   # Cliente API Retrofit
├── sensors/
│   └── ShakeDetector    # Detección de movimiento
├── ui/
│   ├── views/           # Pantallas Compose
│   └── components/      # Componentes reutilizables
└── viewmodel/
    └── UserViewModel    # Lógica de presentación


7.  Configuración

Requisitos
- Android Studio Arctic Fox o superior
- Kotlin 1.8+
- SDK mínimo: API 24 (Android 7.0)
- SDK objetivo: API 34

API Externa
La aplicación se conecta a MockAPI para la sincronización:
- URL base: `https://69415249686bc3ca81668c95.mockapi.io/`
- Endpoint: `/users`

Permisos
- INTERNET: Para sincronización con servidor
- Sensor acelerómetro: Para detección de shake (opcional)

8. Uso

Controles de la Aplicación
- Botón +: Abrir formulario para nuevo usuario
- Botón Refresh: Sincronizar con servidor
- Botón Person: Añadir usuario de prueba aleatorio
- Botón Papelera: Limpiar base de datos (solo para testing)
- Shake del dispositivo: Añadir usuario de prueba

Flujo de Trabajo
1. Al crear/editar usuarios, se guardan localmente con flag `pendingSync`
2. Al sincronizar, se suben cambios pendientes al servidor
3. Se descargan actualizaciones del servidor
4. Los usuarios eliminados se marcan como `pendingDelete` hasta la sincronización

Estados de Sincronización
- pendingSync = true: Usuario necesita subirse al servidor
- pendingDelete = true: Usuario marcado para eliminación
- ID con "local_": Usuario creado offline, sin ID del servidor

