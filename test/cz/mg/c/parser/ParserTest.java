package cz.mg.c.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.*;
import cz.mg.c.parser.entities.Enum;
import cz.mg.c.preprocessor.processors.macro.entities.Macros;
import cz.mg.collections.list.List;
import cz.mg.file.File;
import cz.mg.test.Assert;

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
        Assert.assertEquals(6, entities.count());
        Assert.assertEquals(true, entities.get(0) instanceof Typedef);
        Assert.assertEquals(true, entities.get(1) instanceof Enum);
        Assert.assertEquals(true, entities.get(2) instanceof Union);
        Assert.assertEquals(true, entities.get(3) instanceof Struct);
        Assert.assertEquals(true, entities.get(4) instanceof Variable);
        Assert.assertEquals(true, entities.get(5) instanceof Function);

        Typedef typedef = (Typedef) entities.get(0);
        Assert.assertEquals(true, typedef.getType().getTypename() instanceof Function);
        Assert.assertEquals(0, typedef.getType().getArrays().count());
        Assert.assertEquals(1, typedef.getType().getPointers().count());
        Assert.assertEquals(false, typedef.getType().isConstant());
        Assert.assertEquals(false, typedef.getType().getPointers().getFirst().isConstant());

        Function functionPointer = (Function) typedef.getType().getTypename();
        Assert.assertEquals("void", functionPointer.getOutput().getTypename().getName().getText());
        Assert.assertEquals(0, functionPointer.getInput().count());
    }

    private @Mandatory String readTestFile() {
        InputStream stream = ParserTest.class.getResourceAsStream(TEST_FILE);
        if (stream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                    content.append("\n");
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
