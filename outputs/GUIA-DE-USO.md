# Oficina API SOAP Challenger 1 — Guia de uso

## 1. Visão geral

Esta API gerencia clientes, veículos, serviços, peças e ordens de serviço (OS) de uma oficina mecânica. As rotas administrativas usam autenticação JWT. A rota de acompanhamento da OS é pública, mas requer o documento do cliente.

Base local: `http://localhost:8080`

## 2. Pré-requisitos e execução

- Java 21
- Apache Maven 3.9+

Na raiz do projeto, execute:

```powershell
mvn test
mvn spring-boot:run
```

Se o Maven ainda não estiver no `PATH`, execute com o caminho da instalação:

```powershell
& "C:\Users\Windows\Downloads\apache-maven-3.9.16-bin\apache-maven-3.9.16\bin\mvn.cmd" spring-boot:run
```

A aplicação fica disponível em `http://localhost:8080`.

### Executar com Docker

Com Docker Desktop instalado e em execução, execute na raiz do projeto:

```powershell
docker compose up --build -d
```

Acompanhe a inicialização:

```powershell
docker compose logs -f oficina-api
```

Para interromper os containers:

```powershell
docker compose down
```

O Docker Compose mantém os dados do H2 no volume `h2-data`. Para apagar também os dados locais, execute `docker compose down -v`.

## 3. Banco H2

Abra `http://localhost:8080/h2-console` e use:

| Campo | Valor |
|---|---|
| JDBC URL | `jdbc:h2:mem:oficina` |
| Usuário | `sa` |
| Senha | deixe em branco |

O banco é em memória: os dados são apagados quando a aplicação é interrompida.

## 4. Autenticação no Insomnia

Crie uma requisição `POST` para `/api/auth/login`.

```json
{
  "usuario": "admin",
  "senha": "admin123"
}
```

A resposta traz o campo `token`. Em todas as rotas administrativas, configure **Auth > Bearer Token** no Insomnia e cole esse valor. O token é válido por 120 minutos.

> As credenciais fixas existem apenas para o MVP. Em produção, substitua-as por usuários persistidos e variáveis de ambiente.

## 5. Passo a passo no Insomnia

Este roteiro cria todos os dados necessários e conclui uma ordem de serviço. Execute uma requisição por vez, na sequência.

### Passo 1 — criar uma collection e configurar a URL

1. Abra o **Insomnia**.
2. Clique em **Create** > **Request collection** e dê o nome `Oficina API`.
3. Crie uma requisição. No campo de URL, use sempre `http://localhost:8080` antes do caminho da rota.
4. Alternativamente, importe a collection fornecida: **Import** > **File** > selecione `oficina-api-insomnia-collection.json`.

### Passo 2 — fazer login e copiar o token

1. Crie uma requisição com método **POST**.
2. Informe a URL: `http://localhost:8080/api/auth/login`.
3. Abra **Body** > **JSON** e cole:

```json
{
  "usuario": "admin",
  "senha": "admin123"
}
```

4. Clique em **Send**.
5. Na resposta, copie somente o conteúdo do campo `token` (não copie as aspas).

Exemplo de resposta:

```json
{
  "tipo": "Bearer",
  "token": "eyJ..."
}
```

### Passo 3 — configurar o token nas próximas requisições

Para cada rota administrativa abaixo:

1. Abra a aba **Auth**.
2. Selecione **Bearer Token**.
3. Cole o token copiado no passo anterior.
4. Não coloque manualmente a palavra `Bearer`; o Insomnia faz isso automaticamente.

> As únicas rotas que não exigem token são `/api/auth/login`, `/api/public/...` e `/h2-console`.

### Passo 4 — criar o cliente e copiar o ID

1. Crie uma requisição **POST** para `http://localhost:8080/api/clientes`.
2. Em **Body** > **JSON**, cole:

```json
{
  "documento": "52998224725",
  "nome": "Ana Silva",
  "telefone": "11999999999",
  "email": "ana@example.com"
}
```

3. Em **Auth**, configure o token conforme o passo 3.
4. Clique em **Send**. A resposta deve ter status **201 Created**.
5. Copie o valor do campo `id` da resposta. Ele será chamado de `<clienteId>` nos próximos passos.

### Passo 5 — criar o veículo e copiar o ID

1. Crie uma requisição **POST** para `http://localhost:8080/api/veiculos`.
2. Configure o token.
3. Em **Body** > **JSON**, cole e troque `<clienteId>` pelo ID copiado no passo 4:

```json
{
  "clienteId": "<clienteId>",
  "placa": "ABC1D23",
  "marca": "Toyota",
  "modelo": "Corolla",
  "ano": 2022
}
```

4. Clique em **Send** e copie o campo `id` retornado. Guarde-o como `<veiculoId>`.

### Passo 6 — criar o serviço e copiar o ID

1. Crie uma requisição **POST** para `http://localhost:8080/api/servicos`.
2. Configure o token.
3. Envie:

```json
{
  "descricao": "Troca de óleo",
  "preco": 120.00,
  "minutosEstimados": 30
}
```

4. Copie o campo `id` retornado e guarde como `<servicoId>`.

### Passo 7 — criar a peça e copiar o ID

1. Crie uma requisição **POST** para `http://localhost:8080/api/pecas`.
2. Configure o token.
3. Envie:

```json
{
  "descricao": "Óleo sintético 5W30",
  "precoUnitario": 35.50,
  "estoque": 20
}
```

4. Copie o campo `id` retornado e guarde como `<pecaId>`.

### Passo 8 — criar a OS e copiar o ID

1. Crie uma requisição **POST** para `http://localhost:8080/api/ordens-servico`.
2. Configure o token.
3. Cole o JSON e troque `<servicoId>` e `<pecaId>` pelos IDs que você copiou:

```json
{
  "documento": "52998224725",
  "placa": "ABC1D23",
  "servicos": ["<servicoId>"],
  "pecas": {
    "<pecaId>": 2
  }
}
```

4. Clique em **Send**. O status inicial retornado deve ser `RECEBIDA`.
5. Copie o campo `id` da resposta e guarde como `<osId>`.

### Passo 9 — enviar e aprovar o orçamento

1. Crie uma requisição **POST** para `http://localhost:8080/api/ordens-servico/<osId>/enviar-orcamento`.
2. Configure o token e clique em **Send**. A resposta deve indicar `AGUARDANDO_APROVACAO`.
3. Crie outra requisição **POST** para `http://localhost:8080/api/ordens-servico/<osId>/aprovar`.
4. Configure o token e clique em **Send**. A resposta deve indicar `EM_EXECUCAO`.

### Passo 10 — finalizar e entregar o veículo

1. Crie uma requisição **PATCH** para `http://localhost:8080/api/ordens-servico/<osId>/status`.
2. Configure o token.
3. Em **Body** > **JSON**, envie:

```json
{
  "status": "FINALIZADA"
}
```

4. Clique em **Send**.
5. Envie a mesma requisição novamente, mudando o corpo para:

```json
{
  "status": "ENTREGUE"
}
```

6. A resposta final deve indicar o status `ENTREGUE`.

### Passo 11 — consultar o andamento como cliente

1. Crie uma requisição **GET** para:

```text
http://localhost:8080/api/public/ordens-servico/<osId>?documento=52998224725
```

2. Não configure token nesta requisição.
3. Clique em **Send**. A resposta mostra o status, total e data de criação da OS.

### Passo 12 — conferir a métrica de tempo médio

1. Crie uma requisição **GET** para `http://localhost:8080/api/metricas/tempo-medio`.
2. Configure o token.
3. Clique em **Send** para receber o campo `minutos`.

## 6. Endpoints administrativos

Todas as rotas desta seção exigem `Authorization: Bearer <token>`.

### Clientes

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/clientes` | Cria cliente |
| GET | `/api/clientes` | Lista clientes |
| PUT | `/api/clientes/{clienteId}` | Atualiza cliente |
| DELETE | `/api/clientes/{clienteId}` | Exclui cliente |

```json
{
  "documento": "52998224725",
  "nome": "Ana Silva",
  "telefone": "11999999999",
  "email": "ana@example.com"
}
```

O documento aceita CPF (11 dígitos) ou CNPJ (14 dígitos) válidos. Pontuação é removida automaticamente.

### Veículos

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/veiculos` | Cria veículo |
| GET | `/api/veiculos` | Lista veículos |
| PUT | `/api/veiculos/{veiculoId}` | Atualiza veículo |
| DELETE | `/api/veiculos/{veiculoId}` | Exclui veículo |

```json
{
  "clienteId": "<clienteId>",
  "placa": "ABC1D23",
  "marca": "Toyota",
  "modelo": "Corolla",
  "ano": 2022
}
```

A placa aceita o formato Mercosul ou o formato antigo sem pontuação; ela é normalizada para maiúsculas.

### Serviços

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/servicos` | Cria serviço |
| GET | `/api/servicos` | Lista serviços |
| PUT | `/api/servicos/{servicoId}` | Atualiza serviço |
| DELETE | `/api/servicos/{servicoId}` | Exclui serviço |

```json
{
  "descricao": "Troca de óleo",
  "preco": 120.00,
  "minutosEstimados": 30
}
```

### Peças e insumos

| Método | Rota | Descrição |
|---|---|---|
| POST | `/api/pecas` | Cria peça ou insumo |
| GET | `/api/pecas` | Lista peças e insumos |
| PUT | `/api/pecas/{pecaId}` | Atualiza peça ou estoque |
| DELETE | `/api/pecas/{pecaId}` | Exclui peça |

```json
{
  "descricao": "Óleo sintético 5W30",
  "precoUnitario": 35.50,
  "estoque": 20
}
```

## 7. Ordens de serviço

### Criar OS

`POST /api/ordens-servico`

```json
{
  "documento": "52998224725",
  "placa": "ABC1D23",
  "servicos": ["<servicoId>"],
  "pecas": {
    "<pecaId>": 2
  }
}
```

O total é calculado automaticamente pela soma dos serviços e das peças. A criação também reduz o estoque das peças informadas. Para criar uma OS sem peças, envie `"pecas": {}`.

### Consultar OS

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/ordens-servico` | Lista OS |
| GET | `/api/ordens-servico/{osId}` | Detalha OS |

### Fluxo do orçamento e execução

| Ação | Método e rota | Status resultante |
|---|---|---|
| Criar OS | `POST /api/ordens-servico` | `RECEBIDA` |
| Enviar orçamento | `POST /api/ordens-servico/{osId}/enviar-orcamento` | `AGUARDANDO_APROVACAO` |
| Aprovar orçamento | `POST /api/ordens-servico/{osId}/aprovar` | `EM_EXECUCAO` |
| Finalizar serviço | `PATCH /api/ordens-servico/{osId}/status` | `FINALIZADA` |
| Entregar veículo | `PATCH /api/ordens-servico/{osId}/status` | `ENTREGUE` |

Para alterar o status manualmente, envie:

```json
{
  "status": "FINALIZADA"
}
```

Os estados devem seguir esta sequência: `RECEBIDA` → `EM_DIAGNOSTICO` → `AGUARDANDO_APROVACAO` → `EM_EXECUCAO` → `FINALIZADA` → `ENTREGUE`. Transições fora de ordem retornam erro.

### Acompanhamento do cliente

`GET /api/public/ordens-servico/{osId}?documento=52998224725`

Essa rota não requer JWT. Ela retorna o identificador, status, total e data de criação somente quando o documento corresponde ao cliente da OS.

### Métrica de tempo médio

`GET /api/metricas/tempo-medio`

Retorna o tempo médio, em minutos, das OS que tenham sido iniciadas e finalizadas.

```json
{
  "minutos": 45
}
```

## 8. Respostas e erros comuns

| Código | Significado provável |
|---|---|
| 400 | CPF/CNPJ ou placa inválidos, estoque insuficiente ou transição de status inválida |
| 401/403 | JWT ausente, inválido ou documento não pertence à OS pública |
| 404 | Cliente, veículo, serviço, peça ou OS não encontrado |
| 409/500 | Exemplo: tentativa de cadastrar documento ou placa já existente; verifique a mensagem do servidor |

## 9. Collection do Insomnia

Importe o arquivo `oficina-api-insomnia-collection.json` que acompanha o projeto. Após o login, preencha no ambiente da collection: `token`, `cliente_id`, `veiculo_id`, `servico_id`, `peca_id` e `os_id`.
