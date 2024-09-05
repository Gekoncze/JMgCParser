package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
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
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.c.parser.test.TypeUtils;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.tokens.SymbolToken;
import cz.mg.tokenizer.test.TokenFactory;
import cz.mg.tokenizer.test.TokenValidator;

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
        test.testParseComplex();
        test.testParseInlineType();
        test.testParseWithType();
        test.testParseWithInitializer();
        test.testParseWithTypeAndInitializer();
        test.testParseBitField();

        System.out.println("OK");
    }

    private final @Service VariableParser parser = VariableParser.getInstance();
    private final @Service TokenValidator tokenValidator = TokenValidator.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseSimple() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("int"), f.word("foo")
        ));

        CVariable variable = parser.parse(reader);

        Assert.assertEquals("foo", variable.getName());
        Assert.assertEquals("int", ((CBaseType)variable.getType()).getTypename().getName());
        Assert.assertNull(variable.getExpression());
        reader.readEnd();
    }

    private void testParseAnonymous() {
        TokenReader reader = new TokenReader(new List<>(f.word("int")));

        CVariable variable = parser.parse(reader);

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

        CVariable variable = parser.parse(reader);
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

        CVariable variable = parser.parse(reader);
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

        CVariable variable = parser.parse(reader);
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

        CVariable variable = parser.parse(reader);
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("bar", variable.getName());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CBaseType.class, types.get(1).getClass());
        tokenValidator.assertEquals(
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

        CVariable variable = parser.parse(reader);
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("foobar", variable.getName());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CArrayType.class, types.get(1).getClass());
        Assert.assertEquals(CArrayType.class, types.get(2).getClass());
        Assert.assertEquals(CBaseType.class, types.get(3).getClass());
        tokenValidator.assertEquals(
            new List<>(f.number("9")),
            ((CArrayType)types.get(0)).getExpression()
        );
        tokenValidator.assertEquals(
            new List<>(f.number("3")),
            ((CArrayType)types.get(1)).getExpression()

        );
        tokenValidator.assertEquals(
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

        CVariable variable = parser.parse(reader);
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("bar", variable.getName());
        tokenValidator.assertEquals(
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

        CVariable variable = parser.parse(reader);
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("bar", variable.getName());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CBaseType.class, types.get(2).getClass());
        tokenValidator.assertEquals(
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

        CVariable variable = parser.parse(reader);
        List<CType> types = TypeUtils.flatten(variable.getType());

        Assert.assertEquals("foobar", variable.getName());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CBaseType.class, types.get(2).getClass());
        Assert.assertEquals(true, types.get(2).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(CStruct.class, ((CBaseType)types.get(2)).getTypename().getClass());
        Assert.assertNull(((CBaseType)types.get(2)).getTypename().getName());
    }

    private void testParseWithType() {
        TokenReader reader = new TokenReader(new List<>(f.word("foo")));
        CType type = new CBaseType();

        CVariable variable = parser.parse(reader, new CTypeChain(type));

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

        CVariable variable = parser.parse(reader);

        Assert.assertEquals("foo", variable.getName());
        Assert.assertEquals("int", ((CBaseType)variable.getType()).getTypename().getName());
        Assert.assertNotNull(variable.getExpression());
        tokenValidator.assertEquals(
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

        CVariable variable = parser.parse(reader, new CTypeChain(type));

        Assert.assertEquals("foo", variable.getName());
        Assert.assertSame(type, variable.getType());
        Assert.assertNotNull(variable.getExpression());
        tokenValidator.assertEquals(
            new List<>(f.number("1"), f.symbol("+"), f.number("2")),
            variable.getExpression()
        );
        reader.readEnd();
    }

    private void testParseBitField() {
        TokenReader reader = new TokenReader(new List<>(
            f.word("int"), f.word("foo"), f.symbol(":"), f.number("8")
        ));

        CVariable variable = parser.parse(reader);

        Assert.assertEquals("foo", variable.getName());
        Assert.assertEquals(false, variable.getType().getModifiers().contains(CModifier.CONST));
        Assert.assertEquals("int", ((CBaseType)variable.getType()).getTypename().getName());
        Assert.assertEquals(8, variable.getBit());
        Assert.assertNull(variable.getExpression());
        reader.readEnd();
    }
}