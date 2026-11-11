import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class AppTest {

    @Test
    public void mainPrintsHelloMessage() throws Exception {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {
            System.setOut(new PrintStream(output));
            App.main(new String[0]);
        } finally {
            System.setOut(originalOut);
        }

        assertEquals(
            "Hello from my Maven project!" + System.lineSeparator(),
            output.toString("UTF-8")
        );
    }
}
