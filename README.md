# Sistema de Impressão Elgin em Java

## Sobre o projeto

Projeto acadêmico desenvolvido em Java para realizar operações de impressão utilizando uma impressora térmica Elgin por meio de uma biblioteca DLL e da biblioteca JNA.

O sistema possui autenticação de usuário, menu interativo e funções para comunicação com a impressora.

## Tecnologias utilizadas

- Java
- JNA (Java Native Access)
- Biblioteca DLL da impressora Elgin
- IntelliJ IDEA

## Funcionalidades

- Login com limite de três tentativas.
- Configuração e abertura de conexão com a impressora.
- Fechamento de conexão.
- Impressão de texto.
- Impressão de QR Code.
- Impressão de código de barras.
- Impressão de XML SAT.
- Impressão de XML de cancelamento SAT.
- Abertura de gaveta.
- Sinal sonoro.
- Avanço de papel.

## Opções do menu

| Opção | Operação |
|---|---|
| 1 | Configurar conexão com a impressora |
| 2 | Abrir conexão |
| 3 | Fechar conexão |
| 4 | Imprimir texto |
| 5 | Imprimir QR Code |
| 6 | Imprimir código de barras |
| 7 | Imprimir XML SAT |
| 8 | Imprimir XML de cancelamento SAT |
| 9 | Abrir gaveta |
| 10 | Emitir sinal sonoro |
| 11 | Avançar papel |
| 0 | Sair do sistema |

## Como executar

1. Tenha o JDK instalado.
2. Abra o projeto no IntelliJ IDEA.
3. Configure a biblioteca `jna-5.15.0.jar` nas dependências do projeto.
4. Disponibilize a DLL `E1_Impressora01.dll` na pasta `lib`.
5. Execute a classe `Main.java`.
6. Informe as credenciais de acesso.

## Login para testes

- **Usuário:** `admin`
- **Senha:** `1234`

## Arquivos XML

Os arquivos XML utilizados nos testes devem ficar dentro da pasta `arquivos`, na raiz do projeto.

## Observação

As operações reais de impressão dependem de uma impressora compatível conectada e configurada corretamente.

## Repositório GitHub

[Projeto Impressora Java](https://github.com/gabrielferreirafigueiredo9-boop/Projeto-Impressora-Java.)

## Autores

- Gabriel Ferreira Figueredo
- Thiago Oliveira de Jesus
- Davy Ezequiel da Silva
- Luís Fernando Oliveira da Silva
- Maria Clara Barbosa