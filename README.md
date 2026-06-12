# Compilador LPS1 - Projeto B - Compiladores

Este projeto implementa, em Java, um pequeno compilador para a linguagem **LPS1**.
A entrada é um programa em LPS1 e a saída é o código equivalente em C impresso na saída padrão.

## O que foi implementado

- Analisador léxico da linguagem LPS1.
- Analisador sintático descendente recursivo.
- Geração de código C para todos os comandos exigidos.
- Parte **(a)**: geração de código C misturada aos métodos do analisador sintático.
- Parte **(b)**: construção da ASA e geração de código C nos métodos da ASA.
- Dois exemplos de entrada em LPS1.
- Quatro arquivos de saída C gerados:
  - exemplo 1 pela parte (a)
  - exemplo 2 pela parte (a)
  - exemplo 1 pela parte (b)
  - exemplo 2 pela parte (b)

## Estrutura do projeto

```text
LPS1_Compilador_ProjetoB/
├── README.md
├── CAPA.txt
├── .gitignore
├── examples/
│   ├── exemplo1.lps1
│   └── exemplo2.lps1
├── outputs/
│   ├── exemplo1_parte_a.c
│   ├── exemplo2_parte_a.c
│   ├── exemplo1_parte_b.c
│   └── exemplo2_parte_b.c
└── src/
    └── lps1/
        ├── common/
        │   ├── CWriter.java
        │   ├── CompileException.java
        │   ├── FileUtil.java
        │   ├── Lexer.java
        │   ├── Token.java
        │   └── TokenType.java
        ├── a/
        │   ├── CompiladorA.java
        │   └── ParserA.java
        └── b/
            ├── ASA.java
            ├── CompiladorB.java
            └── ParserB.java
```

O arquivo `sources.txt` é gerado pelo comando de compilação e não precisa ser entregue.

## Como compilar

No terminal, dentro da pasta do projeto:

```bash
rm -rf out
mkdir -p out
find src -name "*.java" -print > sources.txt
javac -encoding UTF-8 -d out @sources.txt
```

No Windows, se o comando `find` não funcionar, compile assim:

```bash
javac -encoding UTF-8 -d out src/lps1/common/*.java src/lps1/a/*.java src/lps1/b/*.java
```

Use a mesma versão de JDK/JRE para compilar e executar. Se o `javac` for mais novo que o `java` disponível no terminal, uma alternativa é gerar classes compatíveis com Java 8:

```bash
javac -encoding UTF-8 --release 8 -d out @sources.txt
```

## Como executar a parte (a)

```bash
java -cp out lps1.a.CompiladorA examples/exemplo1.lps1
java -cp out lps1.a.CompiladorA examples/exemplo2.lps1
```

Para salvar a saída C em arquivo:

```bash
java -cp out lps1.a.CompiladorA examples/exemplo1.lps1 > outputs/exemplo1_parte_a.c
java -cp out lps1.a.CompiladorA examples/exemplo2.lps1 > outputs/exemplo2_parte_a.c
```

## Como executar a parte (b)

```bash
java -cp out lps1.b.CompiladorB examples/exemplo1.lps1
java -cp out lps1.b.CompiladorB examples/exemplo2.lps1
```

Para salvar a saída C em arquivo:

```bash
java -cp out lps1.b.CompiladorB examples/exemplo1.lps1 > outputs/exemplo1_parte_b.c
java -cp out lps1.b.CompiladorB examples/exemplo2.lps1 > outputs/exemplo2_parte_b.c
```

## Gramática considerada

```text
Program          ::= Command { Command }
Command          ::= AssignCommand | GetCommand | AddCommand | SubCommand |
                     MultCommand | DivCommand | ModCommand | PrintCommand |
                     IfCommand | WhileCommand | CompositeCommand
AssignCommand    ::= "=" Variable Value
GetCommand       ::= "G" Variable
AddCommand       ::= "+" Variable Value Value
SubCommand       ::= "-" Variable Value Value
MultCommand      ::= "*" Variable Value Value
DivCommand       ::= "/" Variable Value Value
ModCommand       ::= "%" Variable Value Value
PrintCommand     ::= "P" Value
Comparison       ::= Variable Operator Value
Operator         ::= "=" | "<" | "#"
IfCommand        ::= "I" Comparison Command
WhileCommand     ::= "W" Comparison Command
CompositeCommand ::= "{" Command { Command } "}"
Value            ::= Variable | Number
```

## Tradução dos operadores

```text
LPS1  C
=     ==   quando usado em comparação
#     !=
<     <
```

No comando de atribuição, `=` vira atribuição normal em C.

## O que faz o segundo exemplo?

O segundo exemplo lê um número `n` e testa se existe algum divisor de `n` entre `2` e `n - 1`.
Na lógica do programa entregue no enunciado:

- imprime `0` quando o número é primo;
- imprime `1` quando o número é composto.

Observação: o exemplo presume entradas maiores que 1.

## Mensagens de erro

As mensagens de erro indicam linha, coluna, o que era esperado e o token encontrado.
Exemplo:

```text
Erro sintatico na linha 3, coluna 5: esperado valor (variavel ou numero), encontrado '}'.
```

## Observação sobre o código C gerado

O enunciado usa `gets(str)`. Por isso, o gerador também emite `gets(str)` para ficar fiel ao modelo pedido.
Em compiladores C modernos, `gets` pode gerar alerta porque é uma função antiga/insegura
