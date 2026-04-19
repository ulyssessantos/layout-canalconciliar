# Payroll Office-stamper (Spring Boot)

Projeto Spring Boot para gerar relatório de folha de pagamento em `.docx` usando **Office-stamper**, preservando formatação e suportando **múltiplas linhas de funcionários**.

## Tecnologias

- Java 21
- Spring Boot 3.3.4
- Office-stamper 2.9.0
- Docx4j 11.5.6

## Como executar

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080`.

## Endpoint

`POST /api/payroll/report` (multipart/form-data)

Partes obrigatórias:

- `template`: arquivo `.docx` com placeholders do Office-stamper
- `payload`: JSON com os dados da folha

### Exemplo de payload

```json
{
  "companyName": "Canal Conciliar Ltda",
  "payrollReference": "03/2026",
  "generationDate": "2026-04-19",
  "responsibleName": "Marina Souza",
  "employees": [
    {
      "employeeName": "Ana Lima",
      "role": "Analista Financeira",
      "workedHours": 176,
      "grossSalary": 8500.00,
      "deductions": 1420.70,
      "netSalary": 7079.30
    },
    {
      "employeeName": "Carlos Silva",
      "role": "Desenvolvedor Java",
      "workedHours": 176,
      "grossSalary": 9800.00,
      "deductions": 1960.00,
      "netSalary": 7840.00
    }
  ]
}
```

### Exemplo de chamada (curl)

```bash
curl -X POST "http://localhost:8080/api/payroll/report" \
  -H "Accept: application/vnd.openxmlformats-officedocument.wordprocessingml.document" \
  -F "template=@/caminho/payroll-template.docx" \
  -F 'payload={"companyName":"Canal Conciliar Ltda","payrollReference":"03/2026","generationDate":"2026-04-19","responsibleName":"Marina Souza","employees":[{"employeeName":"Ana Lima","role":"Analista Financeira","workedHours":176,"grossSalary":8500.00,"deductions":1420.70,"netSalary":7079.30},{"employeeName":"Carlos Silva","role":"Desenvolvedor Java","workedHours":176,"grossSalary":9800.00,"deductions":1960.00,"netSalary":7840.00}]};type=application/json' \
  --output folha-pagamento.docx
```

## Como montar o template `.docx`

No Word, use placeholders SpEL do Office-stamper.

### Cabeçalho

- Empresa: `${companyName}`
- Competência: `${payrollReference}`
- Data de geração: `${generationDate}`
- Responsável: `${responsibleName}`

### Tabela com múltiplas linhas

1. Crie uma tabela com os títulos de coluna.
2. Na primeira linha de dados da tabela, use placeholders:
   - `${employeeName}`
   - `${role}`
   - `${workedHours}`
   - `${grossSalary}`
   - `${deductions}`
   - `${netSalary}`
3. Adicione um comentário na linha da tabela com o comando de repetição da coleção:
   - `repeatTableRow(employees)`

Assim o Office-stamper repete a linha para cada item de `employees` mantendo toda a formatação da linha original.

## Estrutura do projeto

- `PayrollOfficeStamperApplication`: bootstrap Spring Boot.
- `PayrollReportController`: endpoint HTTP para receber template + dados.
- `PayrollReportService`: integração com `OfficeStampers.docxStamper()`.
- `PayrollReportRequest` / `PayrollEmployeeLine`: modelos de dados da folha.
