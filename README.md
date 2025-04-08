# CARONTE - Backend

[![Codacy Badge](https://app.codacy.com/project/badge/Grade/2194b57b5519495bbc0ba348cf27a296)](https://app.codacy.com/gh/ISPP-2425-G9/backend/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_grade) [![Codacy Badge](https://app.codacy.com/project/badge/Coverage/2194b57b5519495bbc0ba348cf27a296)](https://app.codacy.com/gh/ISPP-2425-G9/backend/dashboard?utm_source=gh&utm_medium=referral&utm_content=&utm_campaign=Badge_coverage) ![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg) ![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-yellow.svg?logo=conventional-commits)

## Description
Caronte is a backend application developed with Spring Boot that uses a MySQL database to manage and store data related to cars.

## Requirements

- **Java 23**
- **MySQL**
- **Maven**

### Requirements on Ubuntu

1. **Install Java 23:**
   To install Java 23 on Ubuntu, follow these steps:

   ```bash
   sudo apt update
   sudo apt install openjdk-23-jdk
   ```

   Verify the installation with:

   ```bash
   java -version
   ```

2. **Install Maven:**
   To install Maven on Ubuntu:

   ```bash
   sudo apt update
   sudo apt install maven
   ```

   Verify the installation with:

   ```bash
   mvn -v
   ```

### Requirements on Windows

1. **Install Java 23:**
   - Go to the [OpenJDK download page](https://jdk.java.net/23/) and download the installer for Windows.
   - Run the installer and follow the instructions to complete the installation.
   - After installation, add Java’s `bin` directory to the environment variables (in **System Properties** -> **Environment Variables** -> **Path**).

   Verify the installation with:

   ```bash
   java -version
   ```

2. **Install Maven:**
   - Download Maven from the [official Maven page](https://maven.apache.org/download.cgi).
   - Extract the ZIP file and configure the environment variables (in **System Properties** -> **Environment Variables** -> **Path**).

   Verify the installation with:

   ```bash
   mvn -v
   ```

## Installation

### Step 1: Clone the repository

Clone the repository on your machine:

```bash
git clone git@github.com:ISPP-2425-G9/backend.git
cd caronte
```

### Step 2: Configure the database

1. Create the database and user in MySQL (or your preferred database):

    ```bash
    mysql -u root -p
    ```

    ```sql
    CREATE DATABASE caronte;
    CREATE USER 'caronte_user'@'localhost' IDENTIFIED BY 'caronte_password';
    GRANT ALL PRIVILEGES ON caronte.* TO 'caronte_user'@'localhost';
    FLUSH PRIVILEGES;
    ```

### Step 3: Build the project

1. **Install dependencies and build the project:**

    In the terminal, inside the project directory, run:

    ```bash
    mvn clean install
    ```

    This command will download the necessary dependencies and generate the project's JAR file.

### Step 4: Run the application

1. To run the Spring Boot application, use the following command:

    ```bash
    mvn spring-boot:run
    ```

    This will start the server at `http://localhost:8080`.


## Running tests

You can run tests with the following commands:

- If you have maven installed

    ```bash
    mvn clean verify
    ```

- Without using your local maven

     ```bash
    ./mvnw clean verify
    ```

Once you have executed one of these commands, you will have a coverage report in ***./target/site/jacoco/index.html***. Check that file in yout browser to see the results.
