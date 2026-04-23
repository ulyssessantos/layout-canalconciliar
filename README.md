# Template Renderer (Node.js + Docxtemplater)

Serviço HTTP em Node.js para gerar **DOCX / PPTX / XLSX** a partir de templates Office usando dados JSON.

Pensado para integração com uma aplicação externa (ex.: **Spring Boot**) que envia os dados e recebe o documento preenchido em resposta.

## Recursos

- Suporte a templates:
  - `.docx`
  - `.pptx`
  - `.xlsx`
- `.pdf` (saída via conversão de DOCX renderizado)
- Placeholders simples
- Loops
- Condições
- Upload de template via multipart
- Resposta binária com o arquivo renderizado

## Stack

- Node.js
- Express
- [docxtemplater](https://docxtemplater.com/)
- angular-expressions (usado pelo parser `docxtemplater/expressions.js`)
- pizzip
- multer

## Instalação

```bash
npm install
```

## Execução

```bash
npm start
```

Servidor sobe por padrão em `http://localhost:3000`.

## Endpoints

### `GET /health`

Healthcheck básico.

Resposta:

```json
{ "status": "ok" }
```

### `POST /render`

Gera um documento a partir de template + dados JSON.

#### Formato esperado

`multipart/form-data` com:

- `template`: arquivo `.docx`, `.pptx` ou `.xlsx`
- `data`: string JSON (ou campos JSON no body)
- `outputName` (opcional): nome do arquivo de saída
- `outputFormat` (opcional): use `pdf` para converter DOCX preenchido em PDF
- `templateType`/`templateExtension` (opcional): `docx`, `pptx` ou `xlsx` (útil quando upload vem como `application/zip` sem extensão no nome)


> Dica de integração Spring Boot: se o multipart enviar o arquivo como `application/zip` ou sem extensão no nome original, envie também `templateType=docx` (ou `pptx`/`xlsx`) para identificação correta do template.

#### Exemplo com cURL

```bash
curl -X POST "http://localhost:3000/render" \
  -F "template=@./templates/contrato.docx" \
  -F 'data={
    "cliente": {"nome": "Maria", "documento": "123.456.789-00"},
    "itens": [
      {"descricao": "Serviço A", "valor": 1500},
      {"descricao": "Serviço B", "valor": 2300}
    ],
    "total": 3800,
    "aprovado": true
  }' \
  --output contrato-preenchido.docx
```


#### Exemplo para retorno em PDF

```bash
curl -X POST "http://localhost:3000/render" \
  -F "template=@./templates/contrato.docx" \
  -F 'data={"cliente":{"nome":"Maria"},"aprovado":true}' \
  -F "outputFormat=pdf" \
  --output contrato-preenchido.pdf
```

## Sintaxe de template (Docxtemplater)

> Observação: exemplos abaixo usam delimitadores `{` e `}`.

### Placeholder simples

```text
Cliente: {cliente.nome}
Documento: {cliente.documento}
```

### Loop

```text
{#itens}
- {descricao}: R$ {valor}
{/itens}
```

### Condição

```text
{#aprovado}
Pedido aprovado.
{/aprovado}

{^aprovado}
Pedido pendente.
{/aprovado}
```

## Integração com Spring Boot (visão geral)

Fluxo recomendado:

1. Spring Boot recebe a solicitação de negócio.
2. Spring Boot monta o JSON de dados.
3. Spring Boot envia `multipart/form-data` para `POST /render`, anexando:
   - template
   - data (JSON)
4. Serviço Node retorna binário do arquivo pronto.
5. Spring Boot devolve o arquivo ao cliente final ou persiste em storage.

## Tratamento de erros

- `400`: payload inválido, tipo de arquivo não suportado ou erro de upload
- `422`: erro de renderização do template (placeholder inválido, estrutura inconsistente etc.)
- `500`: erro interno inesperado

## Observações

- Limite de upload de arquivo: **20 MB** (configurado no multer).
- Para conversão DOCX -> PDF, o ambiente precisa ter **LibreOffice (`soffice`)** disponível no PATH.
- Conversão para PDF é suportada apenas quando o template de entrada é DOCX.
- Fidelidade visual do PDF depende das fontes e versão do LibreOffice no servidor.
- Para ambientes produtivos, recomenda-se adicionar:
  - autenticação/autorização entre serviços
  - observabilidade (logs estruturados, tracing e métricas)
  - fila assíncrona se houver alto volume
  - timeout/retry no cliente Spring Boot
