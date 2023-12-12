package cz.mg.c.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.CMainEntity;
import cz.mg.c.preprocessor.processors.macro.entities.Macros;
import cz.mg.collections.list.List;
import cz.mg.file.File;

import java.io.*;
import java.nio.file.Path;

public @Test class ParserTest {
    private static final @Mandatory String TEST_FILE = "code.c";

    public static void main(String[] args) {
        System.out.print("Running " + ParserTest.class.getSimpleName() + " ... ");

        ParserTest test = new ParserTest();
        test.testParse();

        System.out.println("OK");
    }

    private final @Service Parser parser = Parser.getInstance();

    private void testParse() {
        String content = readTestFile();
        File file = new File(Path.of("test", "path"), content);
        Macros macros = new Macros();
        List<CMainEntity> entities = parser.parse(file, macros);
        // TODO
    }

    private @Mandatory String readTestFile() {
        InputStream stream = ParserTest.class.getResourceAsStream(TEST_FILE);
        if (stream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                return content.toString();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException("Could not find test file '" + TEST_FILE + "'.");
        }
    }
}
