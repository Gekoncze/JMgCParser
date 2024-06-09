package cz.mg.c.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.*;
import cz.mg.token.tokens.brackets.CurlyBrackets;
import cz.mg.token.tokens.brackets.RoundBrackets;
import cz.mg.token.tokens.brackets.SquareBrackets;
import cz.mg.c.entities.macro.Macro;
import cz.mg.c.entities.macro.Macros;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.file.File;
import cz.mg.test.Assert;
import cz.mg.token.Position;
import cz.mg.token.Token;
import cz.mg.token.tokens.WordToken;
import cz.mg.tokenizer.services.PositionService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

public @Test class CParserTest {
    private static final @Mandatory String TEST_FILE_DEFINITIONS = "definitions.c";
    private static final @Mandatory String TEST_FILE_DECLARATIONS = "declarations.c";
    private static final @Mandatory String TEST_FILE_PREPROCESSING = "preprocessing.c";
    private static final @Mandatory String TEST_FILE_BROKEN = "broken.c";

    public static void main(String[] args) {
        System.out.print("Running " + CParserTest.class.getSimpleName() + " ... ");

        CParserTest test = new CParserTest();
        test.testParseDefinitions();
        test.testParseDeclarations();
        test.testParsePreprocessing();
        test.testParseError();

        System.out.println("OK");
    }

    private final @Service PositionService positionService = PositionService.getInstance();

    private void testParseDefinitions() {
        String content = readTestFile(TEST_FILE_DEFINITIONS);
        File file = new File(Path.of(TEST_FILE_DEFINITIONS), content);
        Macros macros = new Macros();

        CParser parser = new CParser(macros);
        CFile cFile = parser.parse(file);
        Assert.assertEquals(file.getPath(), cFile.getPath());

        List<CEntity> entities = cFile.getEntities();

        Assert.assertEquals(6, entities.count());
        Assert.assertEquals(true, entities.get(0) instanceof CTypedef);
        Assert.assertEquals(true, entities.get(1) instanceof CEnum);
        Assert.assertEquals(true, entities.get(2) instanceof CUnion);
        Assert.assertEquals(true, entities.get(3) instanceof CStruct);
        Assert.assertEquals(true, entities.get(4) instanceof CVariable);
        Assert.assertEquals(true, entities.get(5) instanceof CFunction);

        CTypedef typedef = (CTypedef) entities.get(0);
        Assert.assertEquals(true, typedef.getType().getTypename() instanceof CFunction);
        Assert.assertEquals(0, typedef.getType().getArrays().count());
        Assert.assertEquals(1, typedef.getType().getPointers().count());
        Assert.assertEquals(false, typedef.getType().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, typedef.getType().getPointers().getFirst().isConstant());

        CFunction functionPointer = (CFunction) typedef.getType().getTypename();
        Assert.assertEquals("void", functionPointer.getOutput().getTypename().getName());
        Assert.assertEquals(0, functionPointer.getInput().count());

        CEnum enom = (CEnum) entities.get(1);
        Assert.assertEquals("Day", enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(7, enom.getEntries().count());
        Assert.assertEquals("MONDAY", enom.getEntries().get(0).getName());
        Assert.assertEquals("TUESDAY", enom.getEntries().get(1).getName());
        Assert.assertEquals("WEDNESDAY", enom.getEntries().get(2).getName());
        Assert.assertEquals("THURSDAY", enom.getEntries().get(3).getName());
        Assert.assertEquals("FRIDAY", enom.getEntries().get(4).getName());
        Assert.assertEquals("SATURDAY", enom.getEntries().get(5).getName());
        Assert.assertEquals("SUNDAY", enom.getEntries().get(6).getName());

        CUnion union = (CUnion) entities.get(2);
        Assert.assertEquals("Color", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(2, union.getVariables().count());
        Assert.assertEquals("i", union.getVariables().getFirst().getName());
        Assert.assertEquals("int", union.getVariables().getFirst().getType().getTypename().getName());
        Assert.assertEquals("c", union.getVariables().getLast().getName());
        Assert.assertEquals("char", union.getVariables().getLast().getType().getTypename().getName());
        Assert.assertEquals(1, union.getVariables().getLast().getType().getArrays().count());
        Assert.assertEquals(
            "4",
            union.getVariables().getLast().getType().getArrays().getFirst().getExpression().getFirst().getText()
        );

        CStruct struct = (CStruct) entities.get(3);
        Assert.assertEquals("FooBar", struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(3, struct.getVariables().count());
        Assert.assertEquals("f", struct.getVariables().get(0).getName());
        Assert.assertEquals("d", struct.getVariables().get(1).getName());
        Assert.assertEquals("c", struct.getVariables().get(2).getName());
        Assert.assertEquals("Function", struct.getVariables().get(0).getType().getTypename().getName());
        Assert.assertEquals("Day", struct.getVariables().get(1).getType().getTypename().getName());
        Assert.assertEquals("Color", struct.getVariables().get(2).getType().getTypename().getName());
        Assert.assertEquals(CTypename.class, struct.getVariables().get(0).getType().getTypename().getClass());
        Assert.assertEquals(CEnum.class, struct.getVariables().get(1).getType().getTypename().getClass());
        Assert.assertEquals(CUnion.class, struct.getVariables().get(2).getType().getTypename().getClass());

        CVariable variable = (CVariable) entities.get(4);
        Assert.assertEquals("variable", variable.getName());
        Assert.assertEquals("FooBar", variable.getType().getTypename().getName());
        Assert.assertEquals(CStruct.class, variable.getType().getTypename().getClass());
        Assert.assertEquals(1, variable.getType().getPointers().count());
        Assert.assertEquals(2, variable.getType().getArrays().count());
        Assert.assertEquals(true, variable.getType().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, variable.getType().getPointers().getFirst().isConstant());
        Assert.assertEquals(1, variable.getType().getArrays().getFirst().getExpression().count());
        Assert.assertEquals(3, variable.getType().getArrays().getLast().getExpression().count());
        Assert.assertEquals("2", variable.getType().getArrays().getFirst().getExpression().getFirst().getText());
        Assert.assertEquals("5", variable.getType().getArrays().getLast().getExpression().get(0).getText());
        Assert.assertEquals("+", variable.getType().getArrays().getLast().getExpression().get(1).getText());
        Assert.assertEquals("1", variable.getType().getArrays().getLast().getExpression().get(2).getText());

        CFunction function = (CFunction) entities.get(5);
        Assert.assertEquals("main", function.getName());
        Assert.assertEquals("int", function.getOutput().getTypename().getName());
        Assert.assertEquals(true, function.getOutput().getArrays().isEmpty());
        Assert.assertEquals(true, function.getOutput().getPointers().isEmpty());
        Assert.assertEquals(false, function.getOutput().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(2, function.getInput().count());
        Assert.assertEquals("argc", function.getInput().getFirst().getName());
        Assert.assertEquals("argv", function.getInput().getLast().getName());
        Assert.assertEquals("int", function.getInput().getFirst().getType().getTypename().getName());
        Assert.assertEquals("char", function.getInput().getLast().getType().getTypename().getName());
        Assert.assertEquals(0, function.getInput().getFirst().getType().getPointers().count());
        Assert.assertEquals(1, function.getInput().getLast().getType().getPointers().count());
        Assert.assertEquals(0, function.getInput().getFirst().getType().getArrays().count());
        Assert.assertEquals(0, function.getInput().getLast().getType().getArrays().count());
        Assert.assertEquals(false, function.getInput().getFirst().getType().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, function.getInput().getLast().getType().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, function.getInput().getLast().getType().getPointers().getFirst().isConstant());
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals("printf", function.getImplementation().getFirst().getText());
    }

    private void testParseDeclarations() {
        String content = readTestFile(TEST_FILE_DECLARATIONS);
        File file = new File(Path.of(TEST_FILE_DECLARATIONS), content);
        Macros macros = new Macros();

        CParser parser = new CParser(macros);
        CFile cFile = parser.parse(file);
        Assert.assertEquals(file.getPath(), cFile.getPath());

        List<CEntity> entities = cFile.getEntities();

        Assert.assertEquals(5, entities.count());
        Assert.assertEquals(true, entities.get(0) instanceof CEnum);
        Assert.assertEquals(true, entities.get(1) instanceof CUnion);
        Assert.assertEquals(true, entities.get(2) instanceof CStruct);
        Assert.assertEquals(true, entities.get(3) instanceof CFunction);
        Assert.assertEquals(true, entities.get(4) instanceof CFunction);

        CEnum enom = (CEnum) entities.get(0);
        Assert.assertEquals("MyEnum", enom.getName());
        Assert.assertNull(enom.getEntries());

        CUnion union = (CUnion) entities.get(1);
        Assert.assertEquals("MyUnion", union.getName());
        Assert.assertNull(union.getVariables());

        CStruct struct = (CStruct) entities.get(2);
        Assert.assertEquals("MyStruct", struct.getName());
        Assert.assertNull(struct.getVariables());

        CFunction function = (CFunction) entities.get(3);
        Assert.assertEquals("myFunction", function.getName());
        Assert.assertNull(function.getImplementation());
        Assert.assertEquals("void", function.getOutput().getTypename().getName());
        Assert.assertEquals("float", function.getInput().getFirst().getType().getTypename().getName());
        Assert.assertEquals("double", function.getInput().getLast().getType().getTypename().getName());
        Assert.assertNull(function.getInput().getFirst().getName());
        Assert.assertNull(function.getInput().getLast().getName());
    }

    private void testParsePreprocessing() {
        String content = readTestFile(TEST_FILE_PREPROCESSING);
        File file = new File(Path.of(TEST_FILE_PREPROCESSING), content);
        Macros macros = new Macros();
        Macro externalCondition = new Macro(new WordToken("EXTERNAL_CONDITION", -1), null, new List<>());
        macros.getDefinitions().addLast(externalCondition);

        CParser parser = new CParser(macros);
        CFile cFile = parser.parse(file);
        Assert.assertEquals(file.getPath(), cFile.getPath());

        List<CEntity> entities = cFile.getEntities();

        List<String> entityNames = new List<>();
        for (CEntity entity : entities) {
            if (entity instanceof CNamed) {
                entityNames.addLast(((CNamed) entity).getName());
            }
        }

        Assert.assertThatCollections(
            new List<>(
                "missing",
                "internalTrue",
                "externalTrue",
                "allDefined",
                "correct",
                "main"
            ),
            entityNames
        ).verbose(", ").areEqual();

        List<String> macroNames = new List<>();
        for (Macro macro : macros.getDefinitions()) {
            macroNames.addLast(macro.getName().getText());
        }

        Assert.assertThatCollections(
            new List<>(
                "EXTERNAL_CONDITION",
                "INTERNAL_CONDITION",
                "AVERAGE",
                "FALSE",
                "TRUE"
            ),
            macroNames
        ).verbose(", ").areEqual();


        Assert.assertSame(externalCondition, macros.getDefinitions().get(0));

        Macro internalCondition = macros.getDefinitions().get(1);
        Assert.assertEquals("INTERNAL_CONDITION", internalCondition.getName().getText());
        Assert.assertNull(internalCondition.getParameters());
        Assert.assertEquals(true, internalCondition.getTokens().isEmpty());

        Macro average = macros.getDefinitions().get(2);
        Assert.assertEquals("AVERAGE", average.getName().getText());
        Assert.assertNotNull(average.getParameters());
        Assert.assertEquals("x", average.getParameters().getFirst().getText());
        Assert.assertEquals("y", average.getParameters().getLast().getText());
        Assert.assertEquals("((x+y)/2)", concat(average.getTokens()));

        Macro falseMacro = macros.getDefinitions().get(3);
        Assert.assertEquals("FALSE", falseMacro.getName().getText());
        Assert.assertNull(falseMacro.getParameters());
        Assert.assertEquals(1, falseMacro.getTokens().count());
        Assert.assertEquals("0", falseMacro.getTokens().getFirst().getText());

        Macro trueMacro = macros.getDefinitions().get(4);
        Assert.assertEquals("TRUE", trueMacro.getName().getText());
        Assert.assertNull(trueMacro.getParameters());
        Assert.assertEquals(1, trueMacro.getTokens().count());
        Assert.assertEquals("1", trueMacro.getTokens().getFirst().getText());

        CEnum enom = (CEnum) entities.get(4);
        Assert.assertEquals("correct", enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(1, enom.getEntries().count());
        Assert.assertEquals("ANSWER", enom.getEntries().getFirst().getName());
        Assert.assertNotNull(enom.getEntries().getFirst().getExpression());
        Assert.assertEquals("((8+4)/2)", concat(enom.getEntries().getFirst().getExpression()));
    }

    private void testParseError() {
        String content = readTestFile(TEST_FILE_BROKEN);
        File file = new File(Path.of(TEST_FILE_BROKEN), content);
        Macros macros = new Macros();
        CParser parser = new CParser(macros);

        ParseException exception = Assert.assertThatCode(() -> {
            parser.parse(file);
        }).throwsException(ParseException.class);

        Position position = positionService.find(file.getContent(), exception.getPosition());
        Assert.assertEquals(18, position.getRow());
        Assert.assertEquals(8, position.getColumn());
    }

    private @Mandatory String concat(@Mandatory List<Token> tokens) {
        StringBuilder concatenated = new StringBuilder();
        for (Token token : tokens) {
            if (token instanceof RoundBrackets) {
                concatenated
                    .append("(")
                    .append(concat(((RoundBrackets) token).getTokens()))
                    .append(")");
            } else if (token instanceof SquareBrackets) {
                concatenated
                    .append("[")
                    .append(concat(((SquareBrackets) token).getTokens()))
                    .append("]");
            } else if (token instanceof CurlyBrackets) {
                concatenated
                    .append("{")
                    .append(concat(((CurlyBrackets) token).getTokens()))
                    .append("}");
            } else {
                concatenated.append(token.getText());
            }
        }
        return concatenated.toString();
    }

    private @Mandatory String readTestFile(@Mandatory String name) {
        InputStream stream = CParserTest.class.getResourceAsStream(name);
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