import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
            urlString = "http://" + urlString;
        }
        URI uri = new URI(urlString);
        URL url = uri.toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(true);  // Seguir redirecionamentos

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
        boolean htmlTagFound = false;
        int maxDepth = -1;
        String deepestText = null;

        Pattern pattern = Pattern.compile("<(/)?(\\w+)(\\s.*?)?>|([^<]+)");
        Matcher matcher = pattern.matcher(htmlContent);

        while (matcher.find() && !malformed) {
            if (matcher.group(4) != null) {  
                String text = matcher.group(4).trim();
                if (!text.isEmpty() && stack.size() > maxDepth) {
                    maxDepth = stack.size();
                    deepestText = text;
                }
                continue;
            }

            String tagName = matcher.group(2).toLowerCase();
            boolean isClosing = matcher.group(1) != null;

            if (tagName.startsWith("!") || isSelfClosing(tagName)) {
                continue;
            }

            if (!htmlTagFound) {
                if (!isClosing && "html".equals(tagName)) {
                    htmlTagFound = true;
                    stack.push(tagName);
                } else {
                    malformed = true; 
                }
                continue;
            }

            if (isClosing) {
                if (stack.isEmpty() || !stack.pop().equals(tagName)) {
                    malformed = true;
                }
            } else {
                stack.push(tagName);
            }
        }

        if (!malformed && stack.isEmpty() && htmlTagFound) {
            System.out.println(deepestText != null ? deepestText : "");
        } else {
            System.out.println("malformed HTML");
        }
    }

    private static boolean isSelfClosing(String tagName) {
        return tagName.matches("img|br|meta|link|hr|input|area|base|col|command|embed|keygen|param|source|track|wbr");
    }
}