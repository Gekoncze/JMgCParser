package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.CModifier;
import cz.mg.c.entities.CStruct;
import cz.mg.c.entities.types.CArrayType;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.entities.CVariable;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.token.test.BracketFactory;
import cz.mg.c.parser.test.TypeUtils;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.tokens.SymbolToken;
import cz.mg.token.test.TokenFactory;
import cz.mg.token.test.TokenAssertions;

public @Test class VariableParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + VariableParserTest.class.getSimpleName() + " ... ");

        VariableParserTest test = new VariableParserTest();
        test.testParseEmpty();
        test.testParseSimple();
        test.testParseAnonymous();
        test.testParsePointer();
        test.testParseFunctionPointer();
        test.testParseFunctionPointers();
        test.testParseArray();
        test.testParseArrays();
        test.testParseArrayExpression();
        test.testParseArrayOfInlineType();
        test.testParseComplex();
        test.testParseInlineType();
        test.testParseWithType();
        test.testParseWithInitializer();
        test.testParseWithTypeAndInitializer();
        test.testParseBitField();
        test.testParseMultipleSimpleVariables();
        test.testParseMultipleInlineVariables();
        test.testParseMultipleArrayVariables();
        test.testParseMultipleBitFieldVariables();
        test.testParseMultipleVariablesInitializer();

        System.out.println("OK");
    }

    private final @Service VariableParser parser = VariableParser.getInstance();
    private final @Service TokenAssertions assertions = TokenAssertions.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private @Mandatory CVariable single(@Mandatory List<CVariable> variables) {
        Assert.assertEquals(1, variables.count());
        return variables.getFirst();
    }

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseSimple() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("int"), f.word("foo")
        ));

        CVariable variable = single(parser.parse(reader));

        Assert.assertEquals("foo", variable.getName());
        Assert.assertEquals("int", ((CBaseType)variable.getType()).getTypename().getName());
        Assert.assertNull(variable.getExpression());
        reader.readEnd();
    }

    private void testParseAnonymous() {
        TokenReader reader = new TokenReader(new List<>(f.word("int")));

        CVariable variable = single(parser.parse(reader));

        Assert.assertNull(variable.getName());
        Assert.assertEquals("int", ((CBaseType)variable.getType()).getTypename().getName());
        Assert.assertNull(variable.getExpression());
        reader.readEnd();
    }

    private void testParsePointer() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("float"),
            f.symbol("*"),
            f.word("bar")
        ));

        CVariable variable = single(parser.parse(reader));
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("bar", variable.getName());
        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals(CBaseType.class, types.get(1).getClass());
        Assert.assertEquals("float", ((CBaseType)types.get(1)).getTypename().getName());
        reader.readEnd();
    }

    private void testParseFunctionPointer() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("float"),
            b.roundBrackets(
                f.symbol("*"),
                f.word("bar")
            ),
            b.roundBrackets()
        ));

        CVariable variable = single(parser.parse(reader));
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("bar", variable.getName());
        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals(CBaseType.class, types.get(1).getClass());
        Assert.assertEquals(CFunction.class, ((CBaseType)types.get(1)).getTypename().getClass());
        CFunction function = (CFunction) ((CBaseType)types.get(1)).getTypename();
        Assert.assertEquals("float", ((CBaseType)function.getOutput()).getTypename().getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
        reader.readEnd();
    }

    private void testParseFunctionPointers() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("float"),
            b.roundBrackets(
                f.symbol("*"),
                f.symbol("*"),
                f.word("bar")
            ),
            b.roundBrackets()
        ));

        CVariable variable = single(parser.parse(reader));
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("bar", variable.getName());
        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CBaseType.class, types.get(2).getClass());
        Assert.assertEquals(CFunction.class, ((CBaseType)types.get(2)).getTypename().getClass());
    }

    private void testParseArray() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("float"),
            f.word("bar"),
            b.squareBrackets(
                f.number("12")
            )
        ));

        CVariable variable = single(parser.parse(reader));
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("bar", variable.getName());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CBaseType.class, types.get(1).getClass());
        assertions.assertEquals(
            new List<>(f.number("12")),
            ((CArrayType)types.get(0)).getExpression()
        );
        Assert.assertEquals("float", ((CBaseType)types.get(1)).getTypename().getName());
        reader.readEnd();
    }

    private void testParseArrays() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("double"),
            f.word("foobar"),
            b.squareBrackets(
                f.number("9")
            ),
            b.squareBrackets(
                f.number("3")
            ),
            b.squareBrackets(
                f.number("1")
            )
        ));

        CVariable variable = single(parser.parse(reader));
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("foobar", variable.getName());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CArrayType.class, types.get(1).getClass());
        Assert.assertEquals(CArrayType.class, types.get(2).getClass());
        Assert.assertEquals(CBaseType.class, types.get(3).getClass());
        assertions.assertEquals(
            new List<>(f.number("9")),
            ((CArrayType)types.get(0)).getExpression()
        );
        assertions.assertEquals(
            new List<>(f.number("3")),
            ((CArrayType)types.get(1)).getExpression()

        );
        assertions.assertEquals(
            new List<>(f.number("1")),
            ((CArrayType)types.get(2)).getExpression()
        );
        Assert.assertEquals(false, variable.getType().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals("double", ((CBaseType)types.get(3)).getTypename().getName());
        reader.readEnd();
    }

    private void testParseArrayExpression() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("float"),
            f.word("bar"),
            b.squareBrackets(
                f.number("12"),
                f.symbol("+"),
                f.number("1.5")
            )
        ));

        CVariable variable = single(parser.parse(reader));
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("bar", variable.getName());
        assertions.assertEquals(
            new List<>(
                f.number("12"),
                f.symbol("+"),
                f.number("1.5")
            ),
            ((CArrayType)types.get(0)).getExpression()
        );
        Assert.assertEquals("float", ((CBaseType)types.get(1)).getTypename().getName());
        reader.readEnd();
    }

    private void testParseArrayOfInlineType() {
        // struct {int a; int b;}* foo[2];
        TokenReader reader = new TokenReader(new List<>(
            f.word("struct"),
            b.curlyBrackets(
                f.word("int"),
                f.word("a"),
                f.symbol(";"),
                f.word("int"),
                f.word("b"),
                f.symbol(";")
            ),
            f.symbol("*"),
            f.word("foo"),
            b.squareBrackets(
                f.number("2")
            )
        ));

        CVariable variable = single(parser.parse(reader));
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("foo", variable.getName());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CBaseType.class, types.get(2).getClass());
        assertions.assertEquals(
            new List<>(f.number("2")),
            ((CArrayType)types.get(0)).getExpression()
        );
        Assert.assertEquals(true, ((CBaseType)types.get(2)).getTypename() instanceof CStruct);
        reader.readEnd();
    }

    private void testParseComplex() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("const"),
            f.word("float"),
            f.symbol("*"),
            f.word("const"),
            f.word("bar"),
            b.squareBrackets(
                f.number("12"),
                f.symbol("+"),
                f.number("1.5")
            )
        ));

        CVariable variable = single(parser.parse(reader));
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("bar", variable.getName());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CBaseType.class, types.get(2).getClass());
        assertions.assertEquals(
            new List<>(
                f.number("12"),
                f.symbol("+"),
                f.number("1.5")
            ),
            ((CArrayType)types.get(0)).getExpression()
        );
        Assert.assertEquals(true, types.get(1).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, types.get(2).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals("float", ((CBaseType)types.get(2)).getTypename().getName());
        reader.readEnd();
    }

    private void testParseInlineType() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("const"),
            f.word("struct"),
            b.curlyBrackets(
                f.word("int"),
                f.word("a"),
                f.symbol(";")
            ),
            f.symbol("*"),
            f.word("foobar"),
            b.squareBrackets(
                f.number("2")
            )
        ));

        CVariable variable = single(parser.parse(reader));
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("foobar", variable.getName());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CBaseType.class, types.get(2).getClass());
        Assert.assertEquals(true, types.get(2).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(CStruct.class, ((CBaseType)types.get(2)).getTypename().getClass());
        Assert.assertNull(((CBaseType)types.get(2)).getTypename().getName());
        reader.readEnd();
    }

    private void testParseWithType() {
        TokenReader reader = new TokenReader(new List<>(f.word("foo")));
        CType type = new CBaseType();

        CVariable variable = single(parser.parse(reader, new CTypeChain(type)));

        Assert.assertEquals("foo", variable.getName());
        Assert.assertSame(type, variable.getType());
        reader.readEnd();
    }

    private void testParseWithInitializer() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("int"),
            f.word("foo"),
            f.symbol("="),
            f.number("1"),
            f.symbol("+"),
            f.number("2"),
            f.symbol(";")
        ));

        CVariable variable = single(parser.parse(reader));

        Assert.assertEquals("foo", variable.getName());
        Assert.assertEquals("int", ((CBaseType)variable.getType()).getTypename().getName());
        Assert.assertNotNull(variable.getExpression());
        assertions.assertEquals(
            new List<>(f.number("1"), f.symbol("+"), f.number("2")),
            variable.getExpression()
        );
        reader.read(";", SymbolToken.class);
        reader.readEnd();
    }

    private void testParseWithTypeAndInitializer() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("foo"),
            f.symbol("="),
            f.number("1"),
            f.symbol("+"),
            f.number("2")
        ));

        CType type = new CBaseType();

        CVariable variable = single(parser.parse(reader, new CTypeChain(type)));

        Assert.assertEquals("foo", variable.getName());
        Assert.assertSame(type, variable.getType());
        Assert.assertNotNull(variable.getExpression());
        assertions.assertEquals(
            new List<>(f.number("1"), f.symbol("+"), f.number("2")),
            variable.getExpression()
        );
        reader.readEnd();
    }

    private void testParseBitField() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("int"), f.word("foo"), f.symbol(":"), f.number("8")
        ));

        CVariable variable = single(parser.parse(reader));

        Assert.assertEquals("foo", variable.getName());
        Assert.assertEquals(false, variable.getType().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals("int", ((CBaseType)variable.getType()).getTypename().getName());
        Assert.assertEquals(8, variable.getBit());
        Assert.assertNull(variable.getExpression());
        reader.readEnd();
    }

    private void testParseMultipleSimpleVariables() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("int"),
            f.word("foo"),
            f.symbol(","),
            f.word("bar")
        ));

        List<CVariable> variables = parser.parse(reader);

        Assert.assertEquals(2, variables.count());
        Assert.assertEquals("foo", variables.get(0).getName());
        Assert.assertEquals("bar", variables.get(1).getName());
        Assert.assertEquals("int", ((CBaseType)variables.get(0).getType()).getTypename().getName());
        Assert.assertEquals("int", ((CBaseType)variables.get(1).getType()).getTypename().getName());
        reader.readEnd();
    }

    private void testParseMultipleInlineVariables() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("struct"),
            b.curlyBrackets(
                f.word("int"),
                f.word("a"),
                f.symbol(";")
            ),
            f.word("foo"),
            f.symbol(","),
            f.word("bar")
        ));

        List<CVariable> variables = parser.parse(reader);

        Assert.assertEquals(2, variables.count());
        Assert.assertEquals("foo", variables.get(0).getName());
        Assert.assertEquals("bar", variables.get(1).getName());
        Assert.assertEquals(true, variables.get(0).getType() instanceof CBaseType);
        Assert.assertEquals(true, variables.get(1).getType() instanceof CBaseType);
        Assert.assertEquals(true, ((CBaseType)variables.get(0).getType()).getTypename() instanceof CStruct);
        Assert.assertEquals(true, ((CBaseType)variables.get(1).getType()).getTypename() instanceof CStruct);
        reader.readEnd();
    }

    private void testParseMultipleArrayVariables() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("float"),
            f.word("foo"),
            b.squareBrackets(
                f.number("2")
            ),
            f.symbol(","),
            f.word("bar"),
            b.squareBrackets(
                f.number("3")
            )
        ));

        List<CVariable> variables = parser.parse(reader);

        Assert.assertEquals(2, variables.count());
        Assert.assertEquals("foo", variables.get(0).getName());
        Assert.assertEquals("bar", variables.get(1).getName());
        Assert.assertEquals(true, variables.get(0).getType() instanceof CArrayType);
        Assert.assertEquals(true, variables.get(1).getType() instanceof CArrayType);
        assertions.assertEquals(
            new List<>(f.number("2")),
            ((CArrayType)variables.get(0).getType()).getExpression()
        );
        assertions.assertEquals(
            new List<>(f.number("3")),
            ((CArrayType)variables.get(1).getType()).getExpression()
        );
        reader.readEnd();
    }

    private void testParseMultipleBitFieldVariables() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("int"),
            f.word("foo"),
            f.symbol(":"),
            f.number("4"),
            f.symbol(","),
            f.word("bar"),
            f.symbol(":"),
            f.number("8")
        ));

        List<CVariable> variables = parser.parse(reader);

        Assert.assertEquals(2, variables.count());
        Assert.assertEquals("foo", variables.get(0).getName());
        Assert.assertEquals("bar", variables.get(1).getName());
        Assert.assertEquals("int", ((CBaseType)variables.get(0).getType()).getTypename().getName());
        Assert.assertEquals("int", ((CBaseType)variables.get(1).getType()).getTypename().getName());
        Assert.assertEquals(4, variables.get(0).getBit());
        Assert.assertEquals(8, variables.get(1).getBit());
        reader.readEnd();
    }

    private void testParseMultipleVariablesInitializer() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("int"),
            f.word("foo"),
            f.symbol(","),
            f.word("bar"),
            f.symbol("="),
            f.number("7")
        ));

        List<CVariable> variables = parser.parse(reader);

        Assert.assertEquals(2, variables.count());
        Assert.assertEquals("foo", variables.get(0).getName());
        Assert.assertEquals("bar", variables.get(1).getName());
        Assert.assertEquals("int", ((CBaseType)variables.get(0).getType()).getTypename().getName());
        Assert.assertEquals("int", ((CBaseType)variables.get(1).getType()).getTypename().getName());
        Assert.assertNull(variables.get(0).getExpression());
        Assert.assertNotNull(variables.get(1).getExpression());
        reader.readEnd();
    }
}