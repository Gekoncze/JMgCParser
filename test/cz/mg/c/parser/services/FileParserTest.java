package cz.mg.c.parser.services;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.*;
import cz.mg.c.entities.types.CArrayType;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.token.test.BracketFactory;
import cz.mg.c.parser.test.TypeUtils;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.token.test.TokenFactory;

public @Test class FileParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + FileParserTest.class.getSimpleName() + " ... ");

        FileParserTest test = new FileParserTest();
        test.testParseEmpty();
        test.testParseSemicolons();
        test.testParseTypedef();
        test.testParseVariable();
        test.testParseFunction();
        test.testParseFunctionReturningInlineType();
        test.testParseStruct();
        test.testParseUnion();
        test.testParseEnum();
        test.testParseFunctionPointer();
        test.testParseMultiple();

        System.out.println("OK");
    }

    private final @Service FileParser parser = FileParser.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();

    private void testParseEmpty() {
        List<CEntity> entities = parser.parse(new List<>());
        Assert.assertEquals(true, entities.isEmpty());
    }

    private void testParseSemicolons() {
        List<Token> input = new List<>(
            f.symbol(";"),
            f.symbol(";"),
            f.symbol(";")
        );

        List<CEntity> entities = parser.parse(input);
        Assert.assertEquals(true, entities.isEmpty());
    }

    private void testParseTypedef() {
        List<Token> input = new List<>(
            f.word("typedef"),
            f.word("const"),
            f.word("int"),
            f.symbol("*"),
            f.word("IntPtr"),
            f.symbol(";")
        );

        List<CEntity> entities = parser.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof CTypedef);

        CTypedef typedef = (CTypedef) entities.getFirst();
        List<CType> types = TypeUtils.flatten(typedef.getType());
        Assert.assertEquals("IntPtr", typedef.getName());
        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals(CBaseType.class, types.get(1).getClass());
        Assert.assertEquals(true, types.get(1).getModifiers().contains(CModifier.CONST));
    }

    private void testParseVariable() {
        List<Token> input = new List<>(
            f.word("int"),
            f.symbol("*"),
            f.symbol("*"),
            f.word("foo"),
            b.squareBrackets(
                f.number("1"),
                f.symbol("+"),
                f.number("1")
            ),
            f.symbol(";")
        );

        List<CEntity> entities = parser.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof CVariable);

        CVariable variable = (CVariable) entities.getFirst();
        List<CType> types = TypeUtils.flatten(variable.getType());
        Assert.assertEquals("foo", variable.getName());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CPointerType.class, types.get(2).getClass());
        Assert.assertEquals(CBaseType.class, types.get(3).getClass());
        Assert.assertEquals(3, ((CArrayType)types.get(0)).getExpression().count());
        Assert.assertEquals("int", ((CBaseType)types.get(3)).getTypename().getName());
    }

    private void testParseFunction() {
        List<Token> input = new List<>(
            f.word("void"),
            f.symbol("*"),
            f.word("getAddress"),
            b.roundBrackets(
                f.word("foo"),
                f.word("bar"),
                f.symbol(","),
                f.word("const"),
                f.word("int"),
                f.word("constant")
            ),
            b.curlyBrackets(
                f.word("return"),
                f.number("0"),
                f.symbol(";")
            )
        );

        List<CEntity> entities = parser.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof CFunction);

        CFunction function = (CFunction) entities.getFirst();
        List<CType> outputTypes = TypeUtils.flatten(function.getOutput());
        Assert.assertEquals("getAddress", function.getName());
        Assert.assertEquals(CPointerType.class, outputTypes.get(0).getClass());
        Assert.assertEquals(CBaseType.class, outputTypes.get(1).getClass());
        Assert.assertEquals("void", ((CBaseType)outputTypes.get(1)).getTypename().getName());
        Assert.assertEquals(2, function.getInput().count());
        Assert.assertEquals("bar", function.getInput().getFirst().getName());
        Assert.assertEquals("foo", ((CBaseType)function.getInput().getFirst().getType()).getTypename().getName());
        Assert.assertEquals("constant", function.getInput().getLast().getName());
        Assert.assertEquals("int", ((CBaseType)function.getInput().getLast().getType()).getTypename().getName());
        Assert.assertEquals(true, function.getInput().getLast().getType().getModifiers().contains(CModifier.CONST));
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals(3, function.getImplementation().count());
    }

    private void testParseFunctionReturningInlineType() {
        // struct {int a; int b;} foo(){...}
        List<Token> input = new List<>(
            f.word("struct"),
            b.curlyBrackets(
                f.word("int"),
                f.word("a"),
                f.symbol(";"),
                f.word("int"),
                f.word("b"),
                f.symbol(";")
            ),
            f.word("surprise"),
            b.roundBrackets(),
            b.curlyBrackets(
                f.word("typeof"),
                b.roundBrackets(
                    f.word("surprise"),
                    b.roundBrackets()
                ),
                f.word("s"),
                f.symbol(";"),
                f.word("return"),
                f.word("s"),
                f.symbol(";")
            )
        );

        List<CEntity> entities = parser.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof CFunction);

        CFunction function = (CFunction) entities.getFirst();
        List<CType> outputTypes = TypeUtils.flatten(function.getOutput());
        Assert.assertEquals("surprise", function.getName());
        Assert.assertEquals(CBaseType.class, outputTypes.get(0).getClass());
        Assert.assertEquals(true, ((CBaseType)outputTypes.get(0)).getTypename() instanceof CStruct);
        Assert.assertEquals(null, ((CBaseType)outputTypes.get(0)).getTypename().getName());
        Assert.assertEquals(0, function.getInput().count());
        Assert.assertNotNull(function.getImplementation());
        Assert.assertEquals(7, function.getImplementation().count());
    }

    private void testParseStruct() {
        List<Token> input = new List<>(
            f.word("struct"),
            f.word("FooBar"),
            b.curlyBrackets(
                f.word("int"),
                f.word("a"),
                f.symbol(";"),
                f.word("int"),
                f.word("b"),
                f.symbol(";")
            ),
            f.symbol(";")
        );

        List<CEntity> entities = parser.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof CStruct);

        CStruct struct = (CStruct) entities.getFirst();
        Assert.assertEquals("FooBar", struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(2, struct.getVariables().count());
        Assert.assertEquals("a", struct.getVariables().getFirst().getName());
        Assert.assertEquals("b", struct.getVariables().getLast().getName());
        Assert.assertEquals("int", ((CBaseType)struct.getVariables().getFirst().getType()).getTypename().getName());
        Assert.assertEquals("int", ((CBaseType)struct.getVariables().getLast().getType()).getTypename().getName());
    }

    private void testParseUnion() {
        List<Token> input = new List<>(
            f.word("union"),
            f.word("Color"),
            b.curlyBrackets(
                f.word("int"),
                f.word("i"),
                f.symbol(";"),
                f.word("char"),
                f.word("c"),
                b.squareBrackets(
                    f.number("4")
                ),
                f.symbol(";")
            ),
            f.symbol(";")
        );

        List<CEntity> entities = parser.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof CUnion);

        CUnion union = (CUnion) entities.getFirst();
        Assert.assertEquals("Color", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(2, union.getVariables().count());
        List<CType> types = TypeUtils.flatten(union.getVariables().get(1).getType());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CBaseType.class, types.get(1).getClass());
        Assert.assertEquals("i", union.getVariables().get(0).getName());
        Assert.assertEquals("c", union.getVariables().get(1).getName());
        Assert.assertEquals("int", ((CBaseType)union.getVariables().get(0).getType()).getTypename().getName());
        Assert.assertEquals("char", ((CBaseType)types.get(1)).getTypename().getName());
        Assert.assertEquals(1, ((CArrayType)types.get(0)).getExpression().count());
        Assert.assertEquals("4", ((CArrayType)types.get(0)).getExpression().getFirst().getText());
    }

    private void testParseEnum() {
        List<Token> input = new List<>(
            f.word("enum"),
            f.word("Color"),
            b.curlyBrackets(
                f.word("RED"),
                f.symbol("="),
                f.number("0"),
                f.symbol(","),
                f.word("GREEN"),
                f.symbol(","),
                f.word("BLUE"),
                f.symbol(","),
                f.word("ALPHA")
            ),
            f.symbol(";")
        );

        List<CEntity> entities = parser.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof CEnum);

        CEnum enom = (CEnum) entities.getFirst();
        Assert.assertEquals("Color", enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(4, enom.getEntries().count());
        Assert.assertEquals("RED", enom.getEntries().get(0).getName());
        Assert.assertEquals("GREEN", enom.getEntries().get(1).getName());
        Assert.assertEquals("BLUE", enom.getEntries().get(2).getName());
        Assert.assertEquals("ALPHA", enom.getEntries().get(3).getName());

        List<Token> expression = enom.getEntries().get(0).getExpression();
        Assert.assertNotNull(expression);
        Assert.assertNull(enom.getEntries().get(1).getExpression());
        Assert.assertNull(enom.getEntries().get(2).getExpression());
        Assert.assertNull(enom.getEntries().get(3).getExpression());
        Assert.assertEquals(1, expression.count());
        Assert.assertEquals("0", expression.getFirst().getText());
    }

    private void testParseFunctionPointer() {
        List<Token> input = new List<>(
            f.word("void"),
            b.roundBrackets(
                f.symbol("*"),
                f.word("fptr")
            ),
            b.roundBrackets(),
            f.symbol(";")
        );

        List<CEntity> entities = parser.parse(input);
        Assert.assertEquals(1, entities.count());
        Assert.assertEquals(true, entities.getFirst() instanceof CVariable);

        CVariable variable = (CVariable) entities.getFirst();
        List<CType> types = TypeUtils.flatten(variable.getType());
        Assert.assertEquals("fptr", variable.getName());
        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals(CBaseType.class, types.get(1).getClass());
        Assert.assertEquals(CFunction.class, ((CBaseType)types.get(1)).getTypename().getClass());

        CFunction function = (CFunction) ((CBaseType)types.get(1)).getTypename();
        Assert.assertEquals(true, function.getInput().isEmpty());
        Assert.assertEquals("void", ((CBaseType)function.getOutput()).getTypename().getName());
    }

    private void testParseMultiple() {
        List<Token> input = new List<>(
            f.word("typedef"),
            f.word("const"),
            f.word("int"),
            f.symbol("*"),
            f.word("IntPtr"),
            f.symbol(";"),

            f.word("void"),
            f.symbol("*"),
            f.word("getAddress"),
            b.roundBrackets(
                f.word("foo"),
                f.word("bar"),
                f.symbol(","),
                f.word("const"),
                f.word("int"),
                f.word("constant")
            ),
            b.curlyBrackets(
                f.word("return"),
                f.number("0"),
                f.symbol(";")
            ),

            f.word("struct"),
            f.word("FooBar"),
            b.curlyBrackets(
                f.word("int"),
                f.word("a"),
                f.symbol(";"),
                f.word("int"),
                f.symbol("*"),
                f.word("b"),
                f.symbol(";")
            ),
            f.symbol(";"),

            f.word("void"),
            b.roundBrackets(
                f.symbol("*"),
                f.word("fptr")
            ),
            b.roundBrackets(),
            f.symbol(";")
        );

        List<CEntity> entities = parser.parse(input);
        Assert.assertEquals(4, entities.count());
        Assert.assertEquals(true, entities.get(0) instanceof CTypedef);
        Assert.assertEquals(true, entities.get(1) instanceof CFunction);
        Assert.assertEquals(true, entities.get(2) instanceof CStruct);
        Assert.assertEquals(true, entities.get(3) instanceof CVariable);
    }
}