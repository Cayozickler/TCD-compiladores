package lps1.common;

/**
 * Pequeno utilitario para gerar codigo C identado.
 */
public final class CWriter {
    private final StringBuilder out = new StringBuilder();
    private int indent = 0;

    public void emitLine(String line) {
        for (int i = 0; i < indent; i++) {
            out.append("    ");
        }
        out.append(line).append(System.lineSeparator());
    }

    public void indent() {
        indent++;
    }

    public void dedent() {
        if (indent > 0) {
            indent--;
        }
    }

    public String getCode() {
        return out.toString();
    }

    public static String wrapMain(String body) {
        StringBuilder code = new StringBuilder();
        code.append("#include <stdio.h>\n");
        code.append("\n");
        code.append("int main() {\n");
        code.append("    int a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z;\n");
        code.append("    char str[512]; // auxiliar na leitura com G\n");
        if (body != null && !body.isEmpty()) {
            code.append(body);
        }
        code.append("}\n");
        return code.toString();
    }
}
