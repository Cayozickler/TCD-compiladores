package lps1.b;

import java.util.ArrayList;
import java.util.List;

import lps1.b.ASA.AssignCommandNode;
import lps1.b.ASA.BinaryCommandNode;
import lps1.b.ASA.CommandNode;
import lps1.b.ASA.ComparisonNode;
import lps1.b.ASA.CompositeCommandNode;
import lps1.b.ASA.GetCommandNode;
import lps1.b.ASA.IfCommandNode;
import lps1.b.ASA.PrintCommandNode;
import lps1.b.ASA.ProgramNode;
import lps1.b.ASA.ValueNode;
import lps1.b.ASA.WhileCommandNode;
import lps1.common.CompileException;
import lps1.common.Lexer;
import lps1.common.Token;
import lps1.common.TokenType;

/**
 * PARTE (b)
 * Analisador sintatico descendente recursivo que constroi a ASA.
 * A geracao de C nao aparece aqui: fica nas classes de ASA.java.
 */
public final class ParserB {
    private final Lexer lexer;
    private Token current;

    public ParserB(String source) {
        this.lexer = new Lexer(source);
        this.current = lexer.nextToken();
    }

    public ProgramNode parseProgram() {
        if (isEOF()) {
            error("comando");
        }

        List<CommandNode> commands = new ArrayList<>();
        while (!isEOF()) {
            if (isSymbol("}")) {
                error("comando antes de '}'");
            }
            commands.add(parseCommand());
        }
        expectEOF();
        return new ProgramNode(commands);
    }

    private CommandNode parseCommand() {
        if (isSymbol("=")) {
            return parseAssignCommand();
        } else if (isSymbol("G")) {
            return parseGetCommand();
        } else if (isSymbol("+")) {
            return parseBinaryCommand("+", "+");
        } else if (isSymbol("-")) {
            return parseBinaryCommand("-", "-");
        } else if (isSymbol("*")) {
            return parseBinaryCommand("*", "*");
        } else if (isSymbol("/")) {
            return parseBinaryCommand("/", "/");
        } else if (isSymbol("%")) {
            return parseBinaryCommand("%", "%");
        } else if (isSymbol("P")) {
            return parsePrintCommand();
        } else if (isSymbol("I")) {
            return parseIfCommand();
        } else if (isSymbol("W")) {
            return parseWhileCommand();
        } else if (isSymbol("{")) {
            return parseCompositeCommand();
        } else {
            error("comando valido (=, G, +, -, *, /, %, P, I, W ou {)");
            return null; // inalcançavel
        }
    }

    private CommandNode parseAssignCommand() {
        expectSymbol("=");
        String variable = expectVariable();
        ValueNode value = parseValue();
        return new AssignCommandNode(variable, value);
    }

    private CommandNode parseGetCommand() {
        expectSymbol("G");
        String variable = expectVariable();
        return new GetCommandNode(variable);
    }

    private CommandNode parseBinaryCommand(String commandSymbol, String cOperator) {
        expectSymbol(commandSymbol);
        String variable = expectVariable();
        ValueNode left = parseValue();
        ValueNode right = parseValue();
        return new BinaryCommandNode(variable, left, cOperator, right);
    }

    private CommandNode parsePrintCommand() {
        expectSymbol("P");
        ValueNode value = parseValue();
        return new PrintCommandNode(value);
    }

    private CommandNode parseIfCommand() {
        expectSymbol("I");
        ComparisonNode comparison = parseComparison();
        CommandNode body = parseCommand();
        return new IfCommandNode(comparison, body);
    }

    private CommandNode parseWhileCommand() {
        expectSymbol("W");
        ComparisonNode comparison = parseComparison();
        CommandNode body = parseCommand();
        return new WhileCommandNode(comparison, body);
    }

    private CompositeCommandNode parseCompositeCommand() {
        expectSymbol("{");
        if (isSymbol("}")) {
            error("comando dentro do bloco composto");
        }

        List<CommandNode> commands = new ArrayList<>();
        while (!isSymbol("}")) {
            if (isEOF()) {
                error("'}'");
            }
            commands.add(parseCommand());
        }
        expectSymbol("}");
        return new CompositeCommandNode(commands);
    }

    private ComparisonNode parseComparison() {
        String variable = expectVariable();
        String operator = parseOperator();
        ValueNode value = parseValue();
        return new ComparisonNode(variable, operator, value);
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

    private ValueNode parseValue() {
        if (current.getType() == TokenType.VARIABLE || current.getType() == TokenType.NUMBER) {
            String text = current.getText();
            advance();
            return new ValueNode(text);
        }
        error("valor (variavel ou numero)");
        return null; // inalcançavel
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
