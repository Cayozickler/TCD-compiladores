package lps1.b;

import lps1.b.ASA.ProgramNode;
import lps1.common.CompileException;
import lps1.common.FileUtil;

/**
 * PARTE (b)
 * Programa principal: le codigo LPS1, constroi a ASA e imprime o codigo C
 * gerado pelos metodos da propria ASA.
 */
public final class CompiladorB {
    private CompiladorB() {
    }

    public static void main(String[] args) {
        try {
            String source = FileUtil.readInput(args);
            ProgramNode program = new ParserB(source).parseProgram();
            System.out.println(program.toCCode());
        } catch (CompileException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (Exception ex) {
            System.err.println("Erro ao ler/compilar o arquivo: " + ex.getMessage());
            System.exit(1);
        }
    }
}
