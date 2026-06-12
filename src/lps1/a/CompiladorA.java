package lps1.a;

import lps1.common.CompileException;
import lps1.common.FileUtil;

/**
 * PARTE (a)
 * Programa principal: le codigo LPS1 de um arquivo ou da entrada padrao
 * e imprime o codigo C gerado na saida padrao.
 */
public final class CompiladorA {
    private CompiladorA() {
    }

    public static void main(String[] args) {
        try {
            String source = FileUtil.readInput(args);
            String cCode = new ParserA(source).compile();
            System.out.println(cCode);
        } catch (CompileException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
        } catch (Exception ex) {
            System.err.println("Erro ao ler/compilar o arquivo: " + ex.getMessage());
            System.exit(1);
        }
    }
}
