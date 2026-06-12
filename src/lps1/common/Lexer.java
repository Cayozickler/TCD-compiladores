package lps1.common;

/**
 * Analisador lexico da linguagem LPS1.
 *
 * Regras consideradas:
 * - Variavel: uma letra minuscula de 'a' ate 'z'.
 * - Numero: um digito de '0' ate '9'.
 * - Simbolos/comandos: = G + - * / % P I W { } < #.
 * - Espacos, quebras de linha e tabulacoes sao ignorados.
 */
public final class Lexer {
    private final String input;
    private int index;
    private int line;
    private int column;

    public Lexer(String input) {
        this.input = input == null ? "" : input;
        this.index = 0;
        this.line = 1;
        this.column = 1;
    }

    public Token nextToken() {
        skipWhitespace();

        if (index >= input.length()) {
            return new Token(TokenType.EOF, "", line, column);
        }

        char ch = input.charAt(index);
        int tokenLine = line;
        int tokenColumn = column;
        advance();

        if (ch >= 'a' && ch <= 'z') {
            return new Token(TokenType.VARIABLE, String.valueOf(ch), tokenLine, tokenColumn);
        }

        if (ch >= '0' && ch <= '9') {
            return new Token(TokenType.NUMBER, String.valueOf(ch), tokenLine, tokenColumn);
        }

        if (isSymbol(ch)) {
            return new Token(TokenType.SYMBOL, String.valueOf(ch), tokenLine, tokenColumn);
        }

        throw new CompileException(
                "Erro lexico na linha " + tokenLine + ", coluna " + tokenColumn +
                ": esperado variavel minuscula, numero ou simbolo valido, encontrado '" + ch + "'.");
    }

    private boolean isSymbol(char ch) {
        return ch == '=' || ch == 'G' || ch == '+' || ch == '-' || ch == '*' ||
               ch == '/' || ch == '%' || ch == 'P' || ch == 'I' || ch == 'W' ||
               ch == '{' || ch == '}' || ch == '<' || ch == '#';
    }

    private void skipWhitespace() {
        while (index < input.length()) {
            char ch = input.charAt(index);
            if (Character.isWhitespace(ch)) {
                advance();
            } else {
                break;
            }
        }
    }

    private void advance() {
        if (index >= input.length()) {
            return;
        }

        char ch = input.charAt(index);
        index++;

        if (ch == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
    }
}
