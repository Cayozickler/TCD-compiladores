package lps1.a;

import lps1.common.CWriter;
import lps1.common.CompileException;
import lps1.common.Lexer;
import lps1.common.Token;
import lps1.common.TokenType;

/**
 * PARTE (a)
 * Analisador sintatico descendente recursivo com geracao de codigo C
 * misturada aos metodos do parser.
 */
public final class ParserA {
    private final Lexer lexer;
    private final CWriter writer;
    private Token current;

    public ParserA(String source) {
        this.lexer = new Lexer(source);
        this.writer = new CWriter();
        this.current = lexer.nextToken();
        this.writer.indent(); // corpo dentro de int main()
    }

    public String compile() {
        parseProgram();
        expectEOF();
        return CWriter.wrapMain(writer.getCode());
    }

    private void parseProgram() {
        if (isEOF()) {
            error("comando");
        }

        while (!isEOF()) {
            if (isSymbol("}")) {
                error("comando antes de '}'");
            }
            parseCommand();
        }
    }

    private void parseCommand() {
        if (isSymbol("=")) {
            parseAssignCommand();
        } else if (isSymbol("G")) {
            parseGetCommand();
        } else if (isSymbol("+")) {
            parseBinaryCommand("+", "+");
        } else if (isSymbol("-")) {
            parseBinaryCommand("-", "-");
        } else if (isSymbol("*")) {
            parseBinaryCommand("*", "*");
        } else if (isSymbol("/")) {
            parseBinaryCommand("/", "/");
        } else if (isSymbol("%")) {
            parseBinaryCommand("%", "%");
        } else if (isSymbol("P")) {
            parsePrintCommand();
        } else if (isSymbol("I")) {
            parseIfCommand();
        } else if (isSymbol("W")) {
            parseWhileCommand();
        } else if (isSymbol("{")) {
            parseCompositeCommandAsStandaloneBlock();
        } else {
            error("comando valido (=, G, +, -, *, /, %, P, I, W ou {)");
        }
    }

    private void parseAssignCommand() {
        expectSymbol("=");
        String variable = expectVariable();
        String value = parseValue();
        writer.emitLine(variable + " = " + value + ";");
    }

    private void parseGetCommand() {
        expectSymbol("G");
        String variable = expectVariable();
        writer.emitLine("{");
        writer.indent();
        writer.emitLine("gets(str);");
        writer.emitLine("sscanf(str, \"%d\", &" + variable + ");");
        writer.dedent();
        writer.emitLine("}");
    }

    private void parseBinaryCommand(String commandSymbol, String cOperator) {
        expectSymbol(commandSymbol);
        String variable = expectVariable();
        String left = parseValue();
        String right = parseValue();
        writer.emitLine(variable + " = " + left + " " + cOperator + " " + right + ";");
    }

    private void parsePrintCommand() {
        expectSymbol("P");
        String value = parseValue();
        writer.emitLine("printf(\"%d\\n\", " + value + ");");
    }

    private void parseIfCommand() {
        expectSymbol("I");
        String comparison = parseComparison();
        writer.emitLine("if ( " + comparison + " ) {");
        writer.indent();
        parseControlBody();
        writer.dedent();
        writer.emitLine("}");
    }

    private void parseWhileCommand() {
        expectSymbol("W");
        String comparison = parseComparison();
        writer.emitLine("while ( " + comparison + " ) {");
        writer.indent();
        parseControlBody();
        writer.dedent();
        writer.emitLine("}");
    }

    /**
     * Corpo de I/W. Se a LPS1 trouxe um comando composto { ... }, usa os
     * comandos internos dentro do bloco C ja aberto pelo if/while. Se trouxe
     * comando simples, gera apenas esse comando dentro das chaves do if/while.
     */
    private void parseControlBody() {
        if (isSymbol("{")) {
            expectSymbol("{");
            if (isSymbol("}")) {
                error("comando dentro do bloco composto");
            }
            while (!isSymbol("}")) {
                if (isEOF()) {
                    error("'}'");
                }
                parseCommand();
            }
            expectSymbol("}");
        } else {
            parseCommand();
        }
    }

    /**
     * Comando composto quando aparece como comando independente, fora de I/W.
     */
    private void parseCompositeCommandAsStandaloneBlock() {
        expectSymbol("{");
        writer.emitLine("{");
        writer.indent();
        if (isSymbol("}")) {
            error("comando dentro do bloco composto");
        }
        while (!isSymbol("}")) {
            if (isEOF()) {
                error("'}'");
            }
            parseCommand();
        }
        expectSymbol("}");
        writer.dedent();
        writer.emitLine("}");
    }

    private String parseComparison() {
        String variable = expectVariable();
        String operator = parseOperator();
        String value = parseValue();
        return variable + " " + operator + " " + value;
    }

    private String parseOperator() {
        if (isSymbol("=")) {
            advance();
            return "==";
        }
        if (isSymbol("<")) {
            advance();
            return "<";
        }
        if (isSymbol("#")) {
            advance();
            return "!=";
        }
        error("operador de comparacao (=, < ou #)");
        return ""; // inalcançavel
    }

    private String parseValue() {
        if (current.getType() == TokenType.VARIABLE || current.getType() == TokenType.NUMBER) {
            String value = current.getText();
            advance();
            return value;
        }
        error("valor (variavel ou numero)");
        return ""; // inalcançavel
    }

    private String expectVariable() {
        if (current.getType() != TokenType.VARIABLE) {
            error("variavel (letra minuscula)");
        }
        String variable = current.getText();
        advance();
        return variable;
    }

    private void expectSymbol(String symbol) {
        if (!isSymbol(symbol)) {
            error("'" + symbol + "'");
        }
        advance();
    }

    private void expectEOF() {
        if (!isEOF()) {
            error("fim do arquivo");
        }
    }

    private boolean isSymbol(String symbol) {
        return current.getType() == TokenType.SYMBOL && current.getText().equals(symbol);
    }

    private boolean isEOF() {
        return current.getType() == TokenType.EOF;
    }

    private void advance() {
        current = lexer.nextToken();
    }

    private void error(String expected) {
        throw new CompileException(
                "Erro sintatico na linha " + current.getLine() + ", coluna " + current.getColumn() +
                ": esperado " + expected + ", encontrado " + current + ".");
    }
}
