package cz.mg.c.parser;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.*;
import cz.mg.c.entities.macro.Macro;
import cz.mg.c.entities.macro.Macros;
import cz.mg.c.entities.types.CArrayType;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.TypeUtils;
import cz.mg.collections.list.List;
import cz.mg.file.File;
import cz.mg.test.Assert;
import cz.mg.test.Assertions;
import cz.mg.token.Position;
import cz.mg.token.Token;
import cz.mg.token.tokens.WordToken;
import cz.mg.token.tokens.brackets.CurlyBrackets;
import cz.mg.token.tokens.brackets.RoundBrackets;
import cz.mg.token.tokens.brackets.SquareBrackets;
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
        List<CType> typedefTypes = TypeUtils.flatten(typedef.getType());
        Assert.assertEquals(CPointerType.class, typedefTypes.get(0).getClass());
        Assert.assertEquals(CBaseType.class, typedefTypes.get(1).getClass());
        Assert.assertEquals(CFunction.class, ((CBaseType)typedefTypes.get(1)).getTypename().getClass());

        CFunction functionPointer = (CFunction) ((CBaseType)typedefTypes.get(1)).getTypename();
        Assert.assertEquals("void", ((CBaseType)functionPointer.getOutput()).getTypename().getName());
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
        Assert.assertEquals("int", ((CBaseType)union.getVariables().getFirst().getType()).getTypename().getName());
        Assert.assertEquals("c", union.getVariables().getLast().getName());
        List<CType> unionTypes = TypeUtils.flatten(union.getVariables().getLast().getType());
        Assert.assertEquals(CArrayType.class, unionTypes.get(0).getClass());
        Assert.assertEquals(CBaseType.class, unionTypes.get(1).getClass());
        Assert.assertEquals("char", ((CBaseType)unionTypes.get(1)).getTypename().getName());
        Assert.assertEquals("4", ((CArrayType)unionTypes.get(0)).getExpression().getFirst().getText());

        CStruct struct = (CStruct) entities.get(3);
        Assert.assertEquals("FooBar", struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(3, struct.getVariables().count());
        Assert.assertEquals("f", struct.getVariables().get(0).getName());
        Assert.assertEquals("d", struct.getVariables().get(1).getName());
        Assert.assertEquals("c", struct.getVariables().get(2).getName());
        Assert.assertEquals("Function", ((CBaseType)struct.getVariables().get(0).getType()).getTypename().getName());
        Assert.assertEquals("Day", ((CBaseType)struct.getVariables().get(1).getType()).getTypename().getName());
        Assert.assertEquals("Color", ((CBaseType)struct.getVariables().get(2).getType()).getTypename().getName());
        Assert.assertEquals(CTypename.class, ((CBaseType)struct.getVariables().get(0).getType()).getTypename().getClass());
        Assert.assertEquals(CEnum.class, ((CBaseType)struct.getVariables().get(1).getType()).getTypename().getClass());
        Assert.assertEquals(CUnion.class, ((CBaseType)struct.getVariables().get(2).getType()).getTypename().getClass());

        CVariable variable = (CVariable) entities.get(4);
        List<CType> variableTypes = TypeUtils.flatten(variable.getType());
        Assert.assertEquals(CArrayType.class, variableTypes.get(0).getClass());
        Assert.assertEquals(CArrayType.class, variableTypes.get(1).getClass());
        Assert.assertEquals(CPointerType.class, variableTypes.get(2).getClass());
        Assert.assertEquals(CBaseType.class, variableTypes.get(3).getClass());
        Assert.assertEquals("variable", variable.getName());
        Assert.assertEquals("FooBar", ((CBaseType)variableTypes.get(3)).getTypename().getName());
        Assert.assertEquals(CStruct.class, ((CBaseType)variableTypes.get(3)).getTypename().getClass());
        Assert.assertEquals(false, variableTypes.get(0).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, variableTypes.get(1).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, variableTypes.get(2).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, variableTypes.get(3).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(1, ((CArrayType)variableTypes.get(0)).getExpression().count());
        Assert.assertEquals(3, ((CArrayType)variableTypes.get(1)).getExpression().count());
        Assert.assertEquals("2", ((CArrayType)variableTypes.get(0)).getExpression().getFirst().getText());
        Assert.assertEquals("5", ((CArrayType)variableTypes.get(1)).getExpression().get(0).getText());
        Assert.assertEquals("+", ((CArrayType)variableTypes.get(1)).getExpression().get(1).getText());
        Assert.assertEquals("1", ((CArrayType)variableTypes.get(1)).getExpression().get(2).getText());

        CFunction function = (CFunction) entities.get(5);
        Assert.assertEquals(2, function.getInput().count());
        List<CType> functionTypes = TypeUtils.flatten(function.getInput().getLast().getType());
        Assert.assertEquals("main", function.getName());
        Assert.assertEquals("int", ((CBaseType)function.getOutput()).getTypename().getName());
        Assert.assertEquals(false, function.getOutput().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(CPointerType.class, functionTypes.get(0).getClass());
        Assert.assertEquals(CBaseType.class, functionTypes.get(1).getClass());
        Assert.assertEquals("argc", function.getInput().getFirst().getName());
        Assert.assertEquals("argv", function.getInput().getLast().getName());
        Assert.assertEquals("int", ((CBaseType)function.getInput().getFirst().getType()).getTypename().getName());
        Assert.assertEquals("char", ((CBaseType)functionTypes.get(1)).getTypename().getName());
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
        Assert.assertEquals("void", ((CBaseType)function.getOutput()).getTypename().getName());
        Assert.assertEquals("float", ((CBaseType)function.getInput().getFirst().getType()).getTypename().getName());
        Assert.assertEquals("double", ((CBaseType)function.getInput().getLast().getType()).getTypename().getName());
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

        Assertions.assertThatCollection(entityNames)
            .withDetails(", ")
            .isEqualTo(new List<>(
                "missing",
                "internalTrue",
                "externalTrue",
                "allDefined",
                "correct",
                "main"
            ));

        List<String> macroNames = new List<>();
        for (Macro macro : macros.getDefinitions()) {
            macroNames.addLast(macro.getName().getText());
        }

        Assertions.assertThatCollection(macroNames)
            .withDetails(", ")
            .isEqualTo(new List<>(
                "EXTERNAL_CONDITION",
                "INTERNAL_CONDITION",
                "AVERAGE",
                "FALSE",
                "TRUE"
            ));

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

        ParseException exception = Assertions.assertThatCode(() -> {
            parser.parse(file);
        }).throwsException(ParseException.class);

        Position position = positionService.find(file.getContent(), exception.getPosition());
        Assert.assertEquals(18, position.getRow());
        Assert.assertEquals(1, position.getColumn());
        Assert.assertEquals(true, exception.getMessage().contains("Invalid struct declaration."));
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