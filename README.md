# CARONTE - Backend

## Descripción
Caronte es una aplicación backend desarrollada con Spring Boot que utiliza una base de datos MySQL para gestionar y almacenar datos relacionados con coches.

## Requisitos

- **Java 23**
- **MySQL**
- **Maven**

### Requisitos en Ubuntu

1. **Instalar Java 23:**
   Para instalar Java 23 en Ubuntu, sigue estos pasos:

   ```bash
   sudo apt update
   sudo apt install openjdk-23-jdk
   ```

   Verifica la instalación con:

   ```bash
   java -version
   ```

2. **Instalar Maven:**
   Para instalar Maven en Ubuntu:

   ```bash
   sudo apt update
   sudo apt install maven
   ```

   Verifica la instalación con:

   ```bash
   mvn -v
   ```

### Requisitos en Windows

1. **Instalar Java 23:**
   - Ve a la [página de descargas de OpenJDK](https://jdk.java.net/23/) y descarga el instalador para Windows.
   - Ejecuta el instalador y sigue las instrucciones para completar la instalación.
   - Después de instalarlo, agrega el directorio `bin` de Java a las variables de entorno (en **System Properties** -> **Environment Variables** -> **Path**).

   Verifica la instalación con:

   ```bash
   java -version
   ```

2. **Instalar Maven:**
   - Descarga Maven desde la [página oficial de Maven](https://maven.apache.org/download.cgi).
   - Extrae el archivo ZIP y configura las variables de entorno (en **System Properties** -> **Environment Variables** -> **Path**).

   Verifica la instalación con:

   ```bash
   mvn -v
   ```

## Instalación

### Paso 1: Clonar el repositorio

Clona el repositorio en tu máquina:

```bash
git clone git@github.com:ISPP-2425-G9/backend.git
cd caronte
```

### Paso 2: Configurar la base de datos

1. Crea la base de datos y el usuario en MySQL (o tu base de datos preferida):

    ```bash
    mysql -u root -p
    ```

    ```sql
    CREATE DATABASE caronte;
    CREATE USER 'caronte_user'@'localhost' IDENTIFIED BY 'caronte_password';
    GRANT ALL PRIVILEGES ON caronte.* TO 'caronte_user'@'localhost';
    FLUSH PRIVILEGES;
    ```

### Paso 3: Construir el proyecto

1. **Instalar dependencias y construir el proyecto**:

    En la terminal, dentro del directorio del proyecto, ejecuta:

    ```bash
    mvn clean install
    ```

    Este comando descargará las dependencias necesarias y generará el archivo JAR del proyecto.

### Paso 4: Ejecutar la aplicación

1. Para ejecutar la aplicación Spring Boot, utiliza el siguiente comando:

    ```bash
    mvn spring-boot:run
    ```

    Esto iniciará el servidor en `http://localhost:8080`.

