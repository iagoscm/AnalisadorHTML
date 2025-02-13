import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestHtmlPilha {

    public static void main(String[] args) {
        int totalTests = 0;
        int passedTests = 0;

        totalTests++; 
        if (testValidHtmlTitle()) {
            passedTests++;
        }

        totalTests++; 
        if (testValidHtmlBody()) {
            passedTests++;
        }

        totalTests++; 
        if (testMultipleDeepestText()) {
            passedTests++;
        }

        totalTests++; 
        if (testMalformedHtmlMissingClosing()) {
            passedTests++;
        }

        totalTests++; 
        if (testMalformedHtmlExtraContentAfterHtml()) {
            passedTests++;
        }

        totalTests++; 
        if (testMalformedHtmlMismatchedTags()) {
            passedTests++;
        }

        System.out.println("\nResumo dos testes:");
        System.out.println("Testes executados: " + totalTests);
        System.out.println("Testes aprovados: " + passedTests);
        System.out.println("Testes reprovados: " + (totalTests - passedTests));
    }

    /**
     * Captura a saída do método parseHtml da classe HtmlAnalyzer para um dado conteúdo HTML.
     */
    private static String captureOutput(String htmlContent) {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream tempOut = new PrintStream(baos);
        System.setOut(tempOut);
        try {
            HtmlAnalyzer.parseHtml(htmlContent);
        } finally {
            System.setOut(originalOut);
        }
        return baos.toString().trim();
    }

    /**
     * Testa HTML válido onde o nível mais profundo é o título.
     * Exemplo:
     * <html>
     *   <head>
     *     <title>
     *       Este é o título.
     *     </title>
     *   </head>
     *   <body>
     *     Este é o corpo.
     *   </body>
     * </html>
     * Resultado esperado: "Este é o título."
     */
    private static boolean testValidHtmlTitle() {
        String html = "<html>\n" +
                      "  <head>\n" +
                      "    <title>\n" +
                      "      Este é o título.\n" +
                      "    </title>\n" +
                      "  </head>\n" +
                      "  <body>\n" +
                      "    Este é o corpo.\n" +
                      "  </body>\n" +
                      "</html>";
        String expected = "Este é o título.";
        String result = captureOutput(html);
        if (!expected.equals(result)) {
            System.out.println("testValidHtmlTitle FAILED: esperado: '" + expected + "', obtido: '" + result + "'");
            return false;
        }
        System.out.println("testValidHtmlTitle PASSED");
        return true;
    }

    /**
     * Testa HTML válido simples com apenas o corpo.
     * Exemplo:
     * <html>
     *   <body>
     *     Texto no corpo.
     *   </body>
     * </html>
     * Resultado esperado: "Texto no corpo."
     */
    private static boolean testValidHtmlBody() {
        String html = "<html>\n" +
                      "  <body>\n" +
                      "    Texto no corpo.\n" +
                      "  </body>\n" +
                      "</html>";
        String expected = "Texto no corpo.";
        String result = captureOutput(html);
        if (!expected.equals(result)) {
            System.out.println("testValidHtmlBody FAILED: esperado: '" + expected + "', obtido: '" + result + "'");
            return false;
        }
        System.out.println("testValidHtmlBody PASSED");
        return true;
    }

    /**
     * Testa quando há dois trechos de texto no mesmo nível máximo de profundidade.
     * O programa deve retornar o primeiro encontrado.
     */
    private static boolean testMultipleDeepestText() {
        String html = "<html>\n" +
                      "  <head>\n" +
                      "    <title>\n" +
                      "      Primeiro texto.\n" +
                      "    </title>\n" +
                      "    <style>\n" +
                      "      Segundo texto.\n" +
                      "    </style>\n" +
                      "  </head>\n" +
                      "  <body>\n" +
                      "    Corpo.\n" +
                      "  </body>\n" +
                      "</html>";
        // Os dois trechos estão em 3 níveis de profundidade, portanto deve retornar "Primeiro texto."
        String expected = "Primeiro texto.";
        String result = captureOutput(html);
        if (!expected.equals(result)) {
            System.out.println("testMultipleDeepestText FAILED: esperado: '" + expected + "', obtido: '" + result + "'");
            return false;
        }
        System.out.println("testMultipleDeepestText PASSED");
        return true;
    }

    /**
     * Testa HTML mal-formado por faltar fechamento de uma tag.
     * Resultado esperado: "malformed HTML"
     */
    private static boolean testMalformedHtmlMissingClosing() {
        String html = "<html>\n" +
                      "  <head>\n" +
                      "    <title>\n" +
                      "      Título sem fechamento adequado.\n" +
                      "    </title>\n" +
                      "  <!-- Falta o fechamento da tag head -->\n" +
                      "  <body>\n" +
                      "    Corpo.\n" +
                      "  </body>\n" +
                      "</html>";
        String expected = "malformed HTML";
        String result = captureOutput(html);
        if (!expected.equals(result)) {
            System.out.println("testMalformedHtmlMissingClosing FAILED: esperado: '" + expected + "', obtido: '" + result + "'");
            return false;
        }
        System.out.println("testMalformedHtmlMissingClosing PASSED");
        return true;
    }

    /**
     * Testa HTML mal-formado por haver conteúdo extra após a tag de fechamento </html>.
     * Resultado esperado: "malformed HTML"
     */
    private static boolean testMalformedHtmlExtraContentAfterHtml() {
        String html = "<html>\n" +
                      "  <head>\n" +
                      "    <title>\n" +
                      "      Título\n" +
                      "    </title>\n" +
                      "  </head>\n" +
                      "  <body>\n" +
                      "    Corpo.\n" +
                      "  </body>\n" +
                      "</html>\n" +
                      "Texto extra";
        String expected = "malformed HTML";
        String result = captureOutput(html);
        if (!expected.equals(result)) {
            System.out.println("testMalformedHtmlExtraContentAfterHtml FAILED: esperado: '" + expected + "', obtido: '" + result + "'");
            return false;
        }
        System.out.println("testMalformedHtmlExtraContentAfterHtml PASSED");
        return true;
    }

    /**
     * Testa HTML mal-formado com tags incompatíveis (ex.: fechamento de </div> sem abertura correspondente).
     * Resultado esperado: "malformed HTML"
     */
    private static boolean testMalformedHtmlMismatchedTags() {
        String html = "<html>\n" +
                      "  <body>\n" +
                      "    Texto\n" +
                      "  </div>\n" +
                      "</html>";
        String expected = "malformed HTML";
        String result = captureOutput(html);
        if (!expected.equals(result)) {
            System.out.println("testMalformedHtmlMismatchedTags FAILED: esperado: '" + expected + "', obtido: '" + result + "'");
            return false;
        }
        System.out.println("testMalformedHtmlMismatchedTags PASSED");
        return true;
    }
}
