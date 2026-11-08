# Demo

Aplicação base em **Spring Boot** (Java), gerada com o Spring Initializr. Sistema de rota mais curta para entregas; hoje expõe o cadastro e a consulta de Centros de Distribuição.

## Tecnologias

- Java 25
- Spring Boot 4.1.2
- Maven (com Maven Wrapper)
- Spring Web MVC (`spring-boot-starter-webmvc`)
- Bean Validation (`spring-boot-starter-validation`)
- Spring Data JPA + PostgreSQL (banco `mercadopreso` em container Docker)
- Flyway (migrations) e H2 (testes)
- Spring Boot Actuator (`spring-boot-starter-actuator`)
- Spring Boot DevTools (reload automático em desenvolvimento)
- Lombok (redução de código boilerplate)
- JUnit / Spring Boot Test para testes

## Estrutura do projeto

```
.
├── pom.xml
├── docker-compose.yml              # PostgreSQL (volume persistente)
├── mvnw / mvnw.cmd                 # Maven Wrapper
├── src
│   ├── main
│   │   ├── java/com/example/demo
│   │   │   ├── DemoApplication.java    # Classe principal (@SpringBootApplication)
│   │   │   ├── model/                  # Entidades (@Entity); BaseEntity com data de criação/atualização
│   │   │   │   └── dto/                # DTOs de entrada/saída (records)
│   │   │   ├── service/                # Regras de negócio (@Service)
│   │   │   ├── controller/             # APIs REST (@RestController)
│   │   │   ├── repository/             # Interfaces Spring Data JPA
│   │   │   ├── exception/              # Exceptions e ApiExceptionHandler
│   │   │   └── config/                 # (reservado) configurações/beans
│   │   └── resources
│   │       ├── application.yaml        # datasource, JPA, Flyway, porta (8080)
│   │       └── db/migration/           # Migrations Flyway (V1__...)
│   └── test                            # Mesma estrutura de pacotes; H2 (modo PostgreSQL)
│       ├── java/com/example/demo
│       └── resources/application.yaml
└── target/                         # Artefatos de build (gerado)
```

## Pré-requisitos

- JDK 25
- Não é necessário instalar o Maven: use o wrapper (`mvnw`).

## Banco de dados

```bash
docker compose up -d      # PostgreSQL 17, banco `mercadopreso`, porta 5432 (postgres/postgres)
```

A conexão pode ser alterada via `DB_URL`, `DB_USER` e `DB_PASSWORD`. O esquema é criado pelo Flyway (`src/main/resources/db/migration`) ao iniciar a aplicação. Requer Docker. Os testes usam H2 e não precisam do container.

## API

### `POST /cd` - cria um Centro de Distribuição

```bash
curl -i -X POST localhost:8080/cd -H "Content-Type: application/json" \
  -d '{"nome":"CD Sao Paulo","latitude":-23.55,"longitude":-46.63}'
```

- `201 Created` + `Location: /cd/{id}` e corpo `{id, nome, latitude, longitude}`.
- `400 Bad Request` (`application/problem+json`, campo `erros`) quando: `nome` vazio ou com mais de 150 caracteres, `latitude` fora de [-90, 90], `longitude` fora de [-180, 180] ou campos ausentes.

### `GET /cd/{id}` - recupera um Centro de Distribuição

```bash
curl -i localhost:8080/cd/1
```

- `200 OK` com `{id, nome, latitude, longitude}`.
- `404 Not Found` (`application/problem+json`) quando o id não existe; `400` quando o id não é numérico.

### `PUT /cd/{id}` - atualiza um Centro de Distribuição

Substitui `nome`, `latitude` e `longitude` (mesmas validações do `POST`); `dataAtualizacao` é renovada.

```bash
curl -i -X PUT localhost:8080/cd/1 -H "Content-Type: application/json" \
  -d '{"nome":"CD Campinas","latitude":-22.9,"longitude":-47.06}'
```

- `200 OK` com `{id, nome, latitude, longitude, dataCriacao, dataAtualizacao}`.
- `404 Not Found` se o id não existe; `400 Bad Request` para dados inválidos ou id não numérico.

### `PATCH /cd/{id}` - atualização parcial

Altera apenas os campos enviados (`nome`, `latitude`, `longitude`); os demais permanecem. Os valores informados seguem as mesmas validações.

```bash
curl -i -X PATCH localhost:8080/cd/1 -H "Content-Type: application/json" -d '{"latitude":-22.9}'
```

- `200 OK` com o centro atualizado.
- `400 Bad Request` para valor inválido, nome em branco, corpo sem nenhum campo (`{}`) ou id não numérico; `404 Not Found` se o id não existe.

### `DELETE /cd/{id}` - remove um Centro de Distribuição

```bash
curl -i -X DELETE localhost:8080/cd/1
```

- `204 No Content` quando removido.
- `404 Not Found` se o id não existe; `400 Bad Request` se o id não é numérico.

## Como executar

```bash
# Windows
mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```

A aplicação sobe em `http://localhost:8080` (porta definida em `application.yaml`).

## Testes

```bash
mvnw.cmd test
```

## Build

```bash
mvnw.cmd clean package
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

## Actuator

Com o Actuator incluído, o endpoint de saúde está disponível em:

```
GET http://localhost:8080/actuator/health
```

Outros endpoints podem ser expostos via `management.endpoints.web.exposure.include` no `application.yaml`.

## Próximos passos

- Adicionar Apache Kafka ao `docker-compose.yml` (mesma rede do PostgreSQL) e a dependência `spring-kafka`.
- Preencher metadados do `pom.xml` (descrição, licença, desenvolvedores).
