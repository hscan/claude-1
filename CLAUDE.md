# Project Sistema de rota mais curta para entregas feita com Claude

## Tech Stack
- Java Open JDK 25 Zulu LTS
- Spring Boot 4.1.2 com Maven
- - Spring Web
- - Spring Data JPA
- - Flyway Migration
- - H2 Database - Para testes
- - PostgreSQL Driver
- - Springboot Actuator
- - Spring for Apache Kafka

- PostgreSQL rodando em docker (Docker compose, tornar persistente)
- Apache Kafka rodando em docker (Docker compose, mesma rede que o postgreSQL)

## Commands
- `mvn spring-boot:run` (Servidor local, porta 8080)
- `mvn spring-boot:build` (Prepara servidor para rodar no docker, porta 8080)

## Architeture
- model/ - Modelos/Entidades do projeto usando @Entity @Table, etc. Todos os modelos/entidades tem data de criação e data de atualização.
- model/dto/ - Objetos usados para transferir dados para o cliente evitando o modelo/entidade
- service/ - Todas classes de serviços(@Service) do projeto. Concentra as logicas de negócio da aplicação.
- controller/ - Recebe as requisições HTTP(APIs REST), validada dados de entrada e retorna as respostas. Utiliza anotações como @RestController e @RequestMapping
- repository/ - Interfaces que estendem JpaRepository ou CrudRepository do spring data para realizar comunicação com o banco de dados.
- config/ Classes de configuração do Spring Security, beans, CORS ou conecxões
- exception/ - Local para colocar todas as exceptions da aplicação.

- Todas as implementações deverão ter um metodo/classe de teste correspondente