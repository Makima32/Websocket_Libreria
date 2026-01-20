# API Library Sockets

## Descripción
Servidor web de bajo nivel desarrollado en Java utilizando Sockets TCP. El sistema implementa de forma manual el protocolo HTTP/1.1 para gestionar un sistema CRUD (Create, Read, Update, Delete) de libros, con persistencia en una base de datos MySQL.

## Arquitectura del Software
El proyecto sigue el patrón **DAO (Data Access Object)** para desacoplar la lógica de acceso a datos de la infraestructura de red.

### Componentes principales:
* **Main**: Punto de entrada. Gestiona el `ServerSocket`, el ciclo de vida de la conexión y el enrutamiento de peticiones.
* **DbConector**: Encapsulado de la conexión JDBC y verificación de estado del motor de base de datos.
* **BookDAO**: Implementación de las operaciones SQL mediante `PreparedStatement`.
* **Book**: Clase POJO que representa la entidad del dominio.
* **JsonParser**: Utilidad de análisis sintáctico para el procesamiento de cadenas JSON mediante manipulación de strings.



## Requisitos e Instalación
1.  **Base de Datos**: MySQL/MariaDB (vía XAMPP o similar).
2.  **Configuración inicial**: Ejecutar el script incluido llamado `Script_CreacionDB` en el gestor de base de datos para generar la estructura de la base de datos `library_db` y la tabla `books`.
3.  **Dependencias**: Es necesario incluir el driver JDBC de MySQL (`mysql-connector-java`) en el classpath del proyecto para permitir la comunicación entre Java y el servidor de base de datos.

## Especificación de la API

| Método | Endpoint | Acción |
| :--- | :--- | :--- |
| GET | `/books` | Retorna lista completa en formato JSON. |
| GET | `/books/{id}` | Retorna un objeto único por identificador. |
| POST | `/books` | Registra un nuevo libro. |
| PUT | `/books/{id}` | Actualiza los campos de un libro existente. |
| DELETE | `/books/{id}` | Elimina el registro del sistema. |



## Detalles de Implementación Técnica
* **Manejo de Protocolo**: Implementación manual de la lectura de cabeceras HTTP, con especial énfasis en el procesamiento de `Content-Length` para asegurar la lectura íntegra del payload.
* **Serialización JSON**: El servidor construye las respuestas mediante concatenación de cadenas siguiendo el estándar JSON, eliminando la necesidad de librerías de terceros.
* **Códigos de Estado**: Gestión de respuestas estándar del protocolo: `200 OK`, `201 Created`, `404 Not Found`, `405 Method Not Allowed` y `500 Internal Server Error`.
