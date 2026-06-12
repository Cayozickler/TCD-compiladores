package lps1.b;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lps1.common.CWriter;

/**
 * PARTE (b)
 * ASA = Arvore Sintatica Abstrata da LPS1.
 * A geracao de codigo C fica nos metodos das classes da ASA.
 */
public final class ASA {
    private ASA() {
    }

    public abstract static class Node {
        public abstract void generateC(CWriter writer);
    }

    public static final class ProgramNode extends Node {
        private final List<CommandNode> commands;

        public ProgramNode(List<CommandNode> commands) {
            this.commands = Collections.unmodifiableList(new ArrayList<>(commands));
        }

        @Override
        public void generateC(CWriter writer) {
            writer.indent();
            for (CommandNode command : commands) {
                command.generateC(writer);
            }
            writer.dedent();
        }

        public String toCCode() {
            CWriter writer = new CWriter();
            generateC(writer);
            return CWriter.wrapMain(writer.getCode());
        }
    }

    public abstract static class CommandNode extends Node {
    }

    public static final class ValueNode extends Node {
        private final String text;

        public ValueNode(String text) {
            this.text = text;
        }

        public String asC() {
            return text;
        }

        @Override
        public void generateC(CWriter writer) {
            writer.emitLine(text);
        }
    }

    public static final class ComparisonNode extends Node {
        private final String variable;
        private final String operator;
        private final ValueNode value;

        public ComparisonNode(String variable, String operator, ValueNode value) {
            this.variable = variable;
            this.operator = operator;
            this.value = value;
        }

        public String asC() {
            return variable + " " + operator + " " + value.asC();
        }

        @Override
        public void generateC(CWriter writer) {
            writer.emitLine(asC());
        }
    }

    public static final class AssignCommandNode extends CommandNode {
        private final String variable;
        private final ValueNode value;

        public AssignCommandNode(String variable, ValueNode value) {
            this.variable = variable;
            this.value = value;
        }

        @Override
        public void generateC(CWriter writer) {
            writer.emitLine(variable + " = " + value.asC() + ";");
        }
    }

    public static final class GetCommandNode extends CommandNode {
        private final String variable;

        public GetCommandNode(String variable) {
            this.variable = variable;
        }

        @Override
        public void generateC(CWriter writer) {
            writer.emitLine("{");
            writer.indent();
            writer.emitLine("gets(str);");
            writer.emitLine("sscanf(str, \"%d\", &" + variable + ");");
            writer.dedent();
            writer.emitLine("}");
        }
    }

    public static final class BinaryCommandNode extends CommandNode {
        private final String variable;
        private final ValueNode left;
        private final String operator;
        private final ValueNode right;

        public BinaryCommandNode(String variable, ValueNode left, String operator, ValueNode right) {
            this.variable = variable;
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        public void generateC(CWriter writer) {
            writer.emitLine(variable + " = " + left.asC() + " " + operator + " " + right.asC() + ";");
        }
    }

    public static final class PrintCommandNode extends CommandNode {
        private final ValueNode value;

        public PrintCommandNode(ValueNode value) {
            this.value = value;
        }

        @Override
        public void generateC(CWriter writer) {
            writer.emitLine("printf(\"%d\\n\", " + value.asC() + ");");
        }
    }

    public static final class IfCommandNode extends CommandNode {
        private final ComparisonNode comparison;
        private final CommandNode body;

        public IfCommandNode(ComparisonNode comparison, CommandNode body) {
            this.comparison = comparison;
            this.body = body;
        }

        @Override
        public void generateC(CWriter writer) {
            writer.emitLine("if ( " + comparison.asC() + " ) {");
            writer.indent();
            emitControlBody(writer, body);
            writer.dedent();
            writer.emitLine("}");
        }
    }

    public static final class WhileCommandNode extends CommandNode {
        private final ComparisonNode comparison;
        private final CommandNode body;

        public WhileCommandNode(ComparisonNode comparison, CommandNode body) {
            this.comparison = comparison;
            this.body = body;
        }

        @Override
        public void generateC(CWriter writer) {
            writer.emitLine("while ( " + comparison.asC() + " ) {");
            writer.indent();
            emitControlBody(writer, body);
            writer.dedent();
            writer.emitLine("}");
        }
    }

    public static final class CompositeCommandNode extends CommandNode {
        private final List<CommandNode> commands;

        public CompositeCommandNode(List<CommandNode> commands) {
            this.commands = Collections.unmodifiableList(new ArrayList<>(commands));
        }

        @Override
        public void generateC(CWriter writer) {
            writer.emitLine("{");
            writer.indent();
            generateContents(writer);
            writer.dedent();
            writer.emitLine("}");
        }

        private void generateContents(CWriter writer) {
            for (CommandNode command : commands) {
                command.generateC(writer);
            }
        }
    }

    private static void emitControlBody(CWriter writer, CommandNode body) {
        if (body instanceof CompositeCommandNode) {
            ((CompositeCommandNode) body).generateContents(writer);
        } else {
            body.generateC(writer);
        }
    }
}
