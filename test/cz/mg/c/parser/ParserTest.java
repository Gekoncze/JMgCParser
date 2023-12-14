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
    private static final @Mandatory String TEST_FILE_DEFINITIONS = "definitions.c";
    private static final @Mandatory String TEST_FILE_DECLARATIONS = "declarations.c";

    public static void main(String[] args) {
        System.out.print("Running " + ParserTest.class.getSimpleName() + " ... ");

        ParserTest test = new ParserTest();
        test.testParseDefinitions();
        test.testParseDeclarations();

        System.out.println("OK");
    }

    private final @Service Parser parser = Parser.getInstance();

    private void testParseDefinitions() {
        String content = readTestFile(TEST_FILE_DEFINITIONS);
        File file = new File(Path.of(TEST_FILE_DEFINITIONS), content);
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

        Struct struct = (Struct) entities.get(3);
        Assert.assertEquals("FooBar", struct.getName().getText());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(3, struct.getVariables().count());
        Assert.assertEquals("f", struct.getVariables().get(0).getName().getText());
        Assert.assertEquals("d", struct.getVariables().get(1).getName().getText());
        Assert.assertEquals("c", struct.getVariables().get(2).getName().getText());
        Assert.assertEquals("Function", struct.getVariables().get(0).getType().getTypename().getName().getText());
        Assert.assertEquals("Day", struct.getVariables().get(1).getType().getTypename().getName().getText());
        Assert.assertEquals("Color", struct.getVariables().get(2).getType().getTypename().getName().getText());
        Assert.assertEquals(Typename.class, struct.getVariables().get(0).getType().getTypename().getClass());
        Assert.assertEquals(Enum.class, struct.getVariables().get(1).getType().getTypename().getClass());
        Assert.assertEquals(Union.class, struct.getVariables().get(2).getType().getTypename().getClass());

        Variable variable = (Variable) entities.get(4);
        Assert.assertEquals("variable", variable.getName().getText());
        Assert.assertEquals("FooBar", variable.getType().getTypename().getName().getText());
        Assert.assertEquals(Struct.class, variable.getType().getTypename().getClass());
        Assert.assertEquals(1, variable.getType().getPointers().count());
        Assert.assertEquals(2, variable.getType().getArrays().count());
        Assert.assertEquals(true, variable.getType().isConstant());
        Assert.assertEquals(true, variable.getType().getPointers().getFirst().isConstant());
        Assert.assertEquals(1, variable.getType().getArrays().getFirst().getExpression().count());
        Assert.assertEquals(3, variable.getType().getArrays().getLast().getExpression().count());
        Assert.assertEquals("2", variable.getType().getArrays().getFirst().getExpression().getFirst().getText());
        Assert.assertEquals("5", variable.getType().getArrays().getLast().getExpression().get(0).getText());
        Assert.assertEquals("+", variable.getType().getArrays().getLast().getExpression().get(1).getText());
        Assert.assertEquals("1", variable.getType().getArrays().getLast().getExpression().get(2).getText());

        Function function = (Function) entities.get(5);
        Assert.assertEquals("main", function.getName().getText());
        Assert.assertEquals("int", function.getOutput().getTypename().getName().getText());
        Assert.assertEquals(true, function.getOutput().getArrays().isEmpty());
        Assert.assertEquals(true, function.getOutput().getPointers().isEmpty());
        Assert.assertEquals(false, function.getOutput().isConstant());
        Assert.assertEquals(2, function.getInput().count());
        Assert.assertEquals("argc", function.getInput().getFirst().getName().getText());
        Assert.assertEquals("argv", function.getInput().getLast().getName().getText());
        Assert.assertEquals("int", function.getInput().getFirst().getType().getTypename().getName().getText());
        Assert.assertEquals("char", function.getInput().getLast().getType().getTypename().getName().getText());
        Assert.assertEquals(0, function.getInput().getFirst().getType().getPointers().count());
        Assert.assertEquals(1, function.getInput().getLast().getType().getPointers().count());
        Assert.assertEquals(0, function.getInput().getFirst().getType().getArrays().count());
        Assert.assertEquals(0, function.getInput().getLast().getType().getArrays().count());
        Assert.assertEquals(false, function.getInput().getFirst().getType().isConstant());
        Assert.assertEquals(false, function.getInput().getLast().getType().isConstant());
        Assert.assertEquals(false, function.getInput().getLast().getType().getPointers().getFirst().isConstant());
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals("printf", function.getImplementation().getFirst().getText());
    }

    private void testParseDeclarations() {
        String content = readTestFile(TEST_FILE_DECLARATIONS);
        File file = new File(Path.of(TEST_FILE_DECLARATIONS), content);
        Macros macros = new Macros();

        List<CMainEntity> entities = parser.parse(file, macros);
        Assert.assertEquals(5, entities.count());
        Assert.assertEquals(true, entities.get(0) instanceof Enum);
        Assert.assertEquals(true, entities.get(1) instanceof Union);
        Assert.assertEquals(true, entities.get(2) instanceof Struct);
        Assert.assertEquals(true, entities.get(3) instanceof Function);
        Assert.assertEquals(true, entities.get(4) instanceof Function);

        Enum enom = (Enum) entities.get(0);
        Assert.assertEquals("MyEnum", enom.getName().getText());
        Assert.assertNull(enom.getEntries());

        Union union = (Union) entities.get(1);
        Assert.assertEquals("MyUnion", union.getName().getText());
        Assert.assertNull(union.getVariables());

        Struct struct = (Struct) entities.get(2);
        Assert.assertEquals("MyStruct", struct.getName().getText());
        Assert.assertNull(struct.getVariables());

        Function function = (Function) entities.get(3);
        Assert.assertEquals("myFunction", function.getName().getText());
        Assert.assertNull(function.getImplementation());
        Assert.assertEquals("void", function.getOutput().getTypename().getName().getText());
        Assert.assertEquals("float", function.getInput().getFirst().getType().getTypename().getName().getText());
        Assert.assertEquals("double", function.getInput().getLast().getType().getTypename().getName().getText());
        Assert.assertSame(Anonymous.NAME, function.getInput().getFirst().getName());
        Assert.assertSame(Anonymous.NAME, function.getInput().getLast().getName());
    }

    private @Mandatory String readTestFile(@Mandatory String name) {
        InputStream stream = ParserTest.class.getResourceAsStream(name);
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
            throw new RuntimeException("Could not find test file '" + name + "'.");
        }
    }
}
