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