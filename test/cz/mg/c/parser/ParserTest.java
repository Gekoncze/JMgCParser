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

        Enum enom = (Enum) entities.get(1);
        Assert.assertEquals("Day", enom.getName().getText());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(7, enom.getEntries().count());
        Assert.assertEquals("MONDAY", enom.getEntries().get(0).getName().getText());
        Assert.assertEquals("TUESDAY", enom.getEntries().get(1).getName().getText());
        Assert.assertEquals("WEDNESDAY", enom.getEntries().get(2).getName().getText());
        Assert.assertEquals("THURSDAY", enom.getEntries().get(3).getName().getText());
        Assert.assertEquals("FRIDAY", enom.getEntries().get(4).getName().getText());
        Assert.assertEquals("SATURDAY", enom.getEntries().get(5).getName().getText());
        Assert.assertEquals("SUNDAY", enom.getEntries().get(6).getName().getText());

        Union union = (Union) entities.get(2);
        Assert.assertEquals("Color", union.getName().getText());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(2, union.getVariables().count());
        Assert.assertEquals("i", union.getVariables().getFirst().getName().getText());
        Assert.assertEquals("int", union.getVariables().getFirst().getType().getTypename().getName().getText());
        Assert.assertEquals("c", union.getVariables().getLast().getName().getText());
        Assert.assertEquals("char", union.getVariables().getLast().getType().getTypename().getName().getText());
        Assert.assertEquals(1, union.getVariables().getLast().getType().getArrays().count());
        Assert.assertEquals(
            "4",
            union.getVariables().getLast().getType().getArrays().getFirst().getExpression().getFirst().getText()
        );
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
