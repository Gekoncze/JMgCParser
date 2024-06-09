package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CStruct;
import cz.mg.c.entities.types.CType;
import cz.mg.c.entities.CVariable;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
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
        Assert.assertEquals(true, variable.getType().getArrays().isEmpty());
        Assert.assertEquals(false, variable.getType().getModifiers().isConstant());
        Assert.assertEquals("int", variable.getType().getTypename().getName());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        Assert.assertNull(variable.getExpression());
        reader.readEnd();
    }

    private void testParseAnonymous() {
        TokenReader reader = new TokenReader(new List<>(f.word("int")));

        CVariable variable = parser.parse(reader);

        Assert.assertNull(variable.getName());
        Assert.assertEquals(true, variable.getType().getArrays().isEmpty());
        Assert.assertEquals(false, variable.getType().getModifiers().isConstant());
        Assert.assertEquals("int", variable.getType().getTypename().getName());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        Assert.assertNull(variable.getExpression());
        reader.readEnd();
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

        Assert.assertEquals("bar", variable.getName());
        Assert.assertEquals(1, variable.getType().getArrays().count());
        tokenValidator.assertEquals(
            new List<>(f.number("12")),
            variable.getType().getArrays().getFirst().getExpression()
        );
        Assert.assertEquals(false, variable.getType().getModifiers().isConstant());
        Assert.assertEquals("float", variable.getType().getTypename().getName());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
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

        Assert.assertEquals("foobar", variable.getName());
        Assert.assertEquals(3, variable.getType().getArrays().count());
        tokenValidator.assertEquals(
            new List<>(f.number("9")),
            variable.getType().getArrays().get(0).getExpression()
        );
        tokenValidator.assertEquals(
            new List<>(f.number("3")),
            variable.getType().getArrays().get(1).getExpression()
        );
        tokenValidator.assertEquals(
            new List<>(f.number("1")),
            variable.getType().getArrays().get(2).getExpression()
        );
        Assert.assertEquals(false, variable.getType().getModifiers().isConstant());
        Assert.assertEquals("double", variable.getType().getTypename().getName());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
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

        Assert.assertEquals("bar", variable.getName());
        Assert.assertEquals(1, variable.getType().getArrays().count());
        tokenValidator.assertEquals(
            new List<>(
                f.number("12"),
                f.symbol("+"),
                f.number("1.5")
            ),
            variable.getType().getArrays().getFirst().getExpression()
        );
        Assert.assertEquals(false, variable.getType().getModifiers().isConstant());
        Assert.assertEquals("float", variable.getType().getTypename().getName());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
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

        Assert.assertEquals("bar", variable.getName());
        Assert.assertEquals(1, variable.getType().getArrays().count());
        tokenValidator.assertEquals(
            new List<>(
                f.number("12"),
                f.symbol("+"),
                f.number("1.5")
            ),
            variable.getType().getArrays().getFirst().getExpression()
        );
        Assert.assertEquals(true, variable.getType().getModifiers().isConstant());
        Assert.assertEquals("float", variable.getType().getTypename().getName());
        Assert.assertEquals(1, variable.getType().getPointers().count());
        Assert.assertEquals(true, variable.getType().getPointers().getFirst().isConstant());
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

        Assert.assertEquals("foobar", variable.getName());
        Assert.assertEquals(1, variable.getType().getArrays().count());
        Assert.assertEquals(1, variable.getType().getPointers().count());
        Assert.assertEquals(true, variable.getType().getModifiers().isConstant());
        Assert.assertEquals(true, variable.getType().getTypename() instanceof CStruct);
        Assert.assertNull(variable.getType().getTypename().getName());
    }

    private void testParseWithType() {
        TokenReader reader = new TokenReader(new List<>(f.word("foo")));
        CType type = new CType();

        CVariable variable = parser.parse(reader, type);

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
        Assert.assertEquals("int", variable.getType().getTypename().getName());
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

        CType type = new CType();

        CVariable variable = parser.parse(reader, type);

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
        Assert.assertEquals(true, variable.getType().getArrays().isEmpty());
        Assert.assertEquals(false, variable.getType().getModifiers().isConstant());
        Assert.assertEquals("int", variable.getType().getTypename().getName());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        Assert.assertEquals(8, variable.getBit());
        Assert.assertNull(variable.getExpression());
        reader.readEnd();
    }
}