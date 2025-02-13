import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

public class TestHtml {

    private static int testsPassed = 0;
    private static int testsFailed = 0;

    public static void main(String[] args) {
        runTest("<html><body><div><p>Deepest text</p></div></body></html>", "Deepest text", "Test 1 - Well-formed HTML with deep text");
        runTest("<html><head><title>Title</title></head><body></body></html>", "Title", "Test 2 - Well-formed HTML with no text");
        runTest("<html><body><div><p>Text</div></p></body></html>", "malformed HTML", "Test 3 - Malformed HTML with incorrect closing");
        runTest("<body><p>Missing html tag</p></body>", "malformed HTML", "Test 4 - HTML missing <html> tag");
        runTest("<html><body><br><hr><img src='img.jpg'><p>Text</p></body></html>", "Text", "Test 5 - HTML with self-closing tags");
        runTest("Just some text, no tags at all.", "malformed HTML", "Test 6 - Plain text, no tags");
        runTest("", "malformed HTML", "Test 7 - Empty HTML content");

        System.out.println("\nTests passed: " + testsPassed);
        System.out.println("Tests failed: " + testsFailed);
    }

    private static void runTest(String inputHtml, String expectedOutput, String testName) {
        ByteArrayOutputStream outputCapture = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outputCapture));

        HtmlAnalyzer.parseHtml(inputHtml);

        System.setOut(originalOut);
        String output = outputCapture.toString().trim();

        if (output.equals(expectedOutput)) {
            System.out.println("[✓] " + testName);
            testsPassed++;
        } else {
            System.out.println("[✗] " + testName);
            System.out.println("    Expected: \"" + expectedOutput + "\"");
            System.out.println("    Got:      \"" + output + "\"");
            testsFailed++;
        }
    }
}
