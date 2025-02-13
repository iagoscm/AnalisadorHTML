import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;

public class HtmlAnalyzer {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("Usage: java HtmlAnalyzer <URL>");
            System.exit(1);
        }

        try {
            String htmlContent = fetchHtml(args[0]);
            parseHtml(htmlContent);
        } catch (IOException | URISyntaxException e) {
            System.out.println("URL connection error");
        }
    }

    private static String fetchHtml(String urlString) throws IOException, URISyntaxException {
        URI uri = new URI(urlString);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IOException("HTTP error: " + connection.getResponseCode());
        }

        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }
        }
        return content.toString();
    }

    static void parseHtml(String htmlContent) {
        Stack<String> stack = new Stack<>();
        boolean malformed = false;
        boolean firstTagFound = false;
        boolean htmlClosed = false;
        int maxDepth = -1;
        String deepestText = null;

        for (String line : htmlContent.split("\n")) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Se já fechou a tag <html>, qualquer conteúdo extra é inválido.
            if (htmlClosed) {
                malformed = true;
                break;
            }

            if (!firstTagFound) {
                // A primeira linha não vazia deve ser a tag de abertura <html>
                if (!isOpeningTag(line) || !"html".equals(getTagName(line))) {
                    malformed = true;
                    break;
                }
                stack.push("html");
                firstTagFound = true;
                continue;
            }

            if (isTag(line)) {
                if (isClosingTag(line)) {
                    if (stack.isEmpty()) {
                        malformed = true;
                        break;
                    }
                    String tag = getTagName(line);
                    String expectedTag = stack.pop();
                    if (tag == null || !tag.equals(expectedTag)) {
                        malformed = true;
                        break;
                    }
                    if (stack.isEmpty() && "html".equals(tag)) {
                        htmlClosed = true;
                    }
                } else { // tag de abertura
                    String tag = getTagName(line);
                    if (tag == null || !isValidTag(tag)) {
                        malformed = true;
                        break;
                    }
                    stack.push(tag);
                }
            } else {
                // Linha de texto: atualiza o texto mais profundo, considerando o tamanho da pilha.
                int depth = stack.size();
                if (depth > maxDepth) {
                    maxDepth = depth;
                    deepestText = line;
                }
            }
        }

        if (malformed || !htmlClosed || !stack.isEmpty()) {
            System.out.println("malformed HTML");
        } else {
            System.out.println(deepestText != null ? deepestText : "");
        }
    }

    private static boolean isTag(String line) {
        return line.startsWith("<") && line.endsWith(">");
    }

    private static boolean isOpeningTag(String line) {
        return isTag(line) && !line.startsWith("</");
    }

    private static boolean isClosingTag(String line) {
        return isTag(line) && line.startsWith("</");
    }

    private static String getTagName(String line) {
        try {
            String tagContent = line.substring(line.startsWith("</") ? 2 : 1, line.length() - 1).trim();
            return isValidTag(tagContent) ? tagContent : null;
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private static boolean isValidTag(String tag) {
        return tag.matches("^[A-Za-z]+$");
    }
}
