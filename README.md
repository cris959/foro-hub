# ForoHub - Challenge Backend Alura Latam

Este proyecto es una API REST construida con **Spring Boot** para gestionar un foro de discusiones.
Permite la creación de tópicos, registro de usuarios con roles específicos y la gestión de respuestas.

```mermaid
classDiagram
class Usuario {
+Long id
+String nombre
+String email
+String password
+List~Perfil~ perfiles
}
class Perfil {
+Long id
+PerfilNombre nombre
}
class Curso {
+Long id
+String nombre
+String categoria
}
class Topico {
+Long id
+String titulo
+String mensaje
+LocalDateTime fechaCreacion
+StatusTopico status
+Usuario autor
+Curso curso
+List~Respuesta~ respuestas
}
class Respuesta {
+Long id
+String mensaje
+LocalDateTime fechaCreacion
+Usuario autor
+Topico topico
+Boolean solucion
}
class PerfilNombre {
<<enumeration>>
ROLE_ADMIN
ROLE_USER
}
class StatusTopico {
<<enumeration>>
NO_RESPONDIDO
NO_SOLUCIONADO
SOLUCIONADO
CERRADO
}

    Usuario "*" --> "*" Perfil : tiene
    Topico "*" --> "1" Usuario : creado por
    Topico "*" --> "1" Curso : pertenece a
    Respuesta "*" --> "1" Usuario : autor
    Respuesta "*" --> "1" Topico : pertenece a
    Topico "1" --> "*" Respuesta : tiene
```

```mermaid
graph TD
%% Inicio del proceso
User((Usuario)) -->|1. Publica Tópico| TC[TopicoController]
TC -->|DTO Registro| TS[TopicoService]
TS -->|Verifica Curso/Autor| TR[(TopicoRepository)]
TR -->|Guarda| DB[(Base de Datos)]

    %% Flujo de Respuesta
    User -->|2. Responde Tópico| RC[RespuestaController]
    RC -->|DTO RegistroRespuesta| RS[RespuestaService]
    RS -->|Relaciona con TopicoID| RR[(RespuestaRepository)]
    RR -->|Persiste Respuesta| DB
    
    %% Flujo de Visualización
    User -->|3. Consulta Detalle| RC
    RC -->|Busca por ID Topico + Paginación| RS
    RS -->|Query Paginada| RR
    RR -->|Entidades| RS
    RS -->|Mapea a DTO Retorno| RM[RespuestaMapper]
    RM -->|JSON Paginado| User

    %% Flujo de Solución
    User -->|4. Marca Solución| RC
    RC -->|ID Respuesta| RS
    RS -->|1. Desmarca otras soluciones| RR
    RS -->|2. Setea solucion=true| RR
    RS -->|3. Cambia Status a SOLUCIONADO| TR
    TR & RR -->|Commit Transaction| DB
```

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'primaryColor': '#ffffff', 'edgeLabelBackground':'#ffffff', 'tertiaryColor': '#f9f9f9'}}}%%
graph LR
%% ESTILOS AVANZADOS
    classDef roles fill:#333,color:#fff,stroke:#000,stroke-width:2px,rx:20,ry:20
    classDef public fill:#e8f5e9,stroke:#2e7d32,stroke-width:1px,stroke-dasharray: 5 5
    classDef adminItem fill:#fff3e0,stroke:#ef6c00,stroke-width:1px,rx:10,ry:10
    classDef userItem fill:#e3f2fd,stroke:#1565c0,stroke-width:1px,rx:10,ry:10
    classDef sharedItem fill:#f3e5f5,stroke:#7b1fa2,stroke-width:1px,rx:10,ry:10

%% ACCESO PÚBLICO (Superior)
    subgraph Puerta[Acceso Público]
        direction LR
        Login((Login))
        Docs((Docs))
    end
    class Puerta,Login,Docs public

%% NÚCLEO DE ROLES
    subgraph Roles[Sistema de Identidad]
        direction TB
        ADMIN[ADMIN Rol]
        USER[USER Rol]
    end
    class ADMIN roles
    class USER roles

%% PERMISOS EXCLUSIVOS ADMIN (Izquierda)
    subgraph Gestion[Administración]
        direction TB
        U_Mng[Usuarios]
        C_Mng[Cursos]
        P_Mng[Perfiles]
        T_Mod[Moderación]
    end
    class Gestion,U_Mng,C_Mng,P_Mng,T_Mod adminItem

%% PERMISOS COMPARTIDOS (Derecha)
    subgraph Foro[Actividad del Foro]
        direction TB
        T_Create[Crear Tópico]
        R_Create[Responder]
        Read[Consultar Todo]
    end
    class Foro,T_Create,R_Create,Read sharedItem

%% FLUJO DE CONEXIONES
    Login --> ADMIN
    Login --> USER

%% Conexiones Admin
    ADMIN --- U_Mng
    ADMIN --- C_Mng
    ADMIN --- P_Mng
    ADMIN --- T_Mod

%% Conexiones USER y ADMIN (Compartidas)
    ADMIN ==> Foro
    USER ==> Foro
```