package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CEnum;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class EnumTypeParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + EnumTypeParserTest.class.getSimpleName() + " ... ");

        EnumTypeParserTest test = new EnumTypeParserTest();
        test.testParseEmpty();
        test.testParseNoFields();
        test.testParseAnonymous();
        test.testParseNamed();
        test.testParseComplexConst();
        test.testParseRemainingTokens();

        System.out.println("OK");
    }

    private final @Service EnumTypeParser parser = EnumTypeParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseNoFields() {
        List<Token> tokens = new List<>(
            f.word("enum"),
            f.word("Foo"),
            b.curlyBrackets()
        );
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.getModifiers().isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof CEnum);
        CEnum enom = (CEnum) type.getTypename();
        Assert.assertEquals("Foo", enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(true, enom.getEntries().isEmpty());
    }

    private void testParseAnonymous() {
        List<Token> tokens = new List<>(
            f.word("enum"),
            b.curlyBrackets(
                f.word("foo")
            )
        );
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.getModifiers().isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof CEnum);
        CEnum enom = (CEnum) type.getTypename();
        Assert.assertNull(enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(1, enom.getEntries().count());
        Assert.assertEquals("foo", enom.getEntries().get(0).getName());
        Assert.assertNull(enom.getEntries().get(0).getExpression());
    }

    private void testParseNamed() {
        List<Token> tokens = new List<>(
            f.word("enum"),
            f.word("FooBar"),
            b.curlyBrackets(
                f.word("foo"),
                f.symbol(","),
                f.word("bar")
            )
        );
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.getModifiers().isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof CEnum);
        CEnum enom = (CEnum) type.getTypename();
        Assert.assertEquals("FooBar", enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(2, enom.getEntries().count());
    }

    private void testParseComplexConst() {
        List<Token> tokens = new List<>(
            f.word("const"),
            f.word("enum"),
            f.word("FooBar"),
            b.curlyBrackets(
                f.word("foo"),
                f.symbol("="),
                f.word("1"),
                f.symbol(","),
                f.word("bar"),
                f.symbol("="),
                f.word("2")
            ),
            f.word("const"),
            f.symbol("*"),
            f.word("const")
        );
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(true, type.getModifiers().isConstant());
        Assert.assertEquals(1, type.getPointers().count());
        Assert.assertEquals(true, type.getPointers().getFirst().isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof CEnum);
        CEnum enom = (CEnum) type.getTypename();
        Assert.assertEquals("FooBar", enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(2, enom.getEntries().count());
        Assert.assertEquals("foo", enom.getEntries().get(0).getName());
        List<Token> firstExpression = enom.getEntries().get(0).getExpression();
        Assert.assertNotNull(firstExpression);
        Assert.assertEquals(1, firstExpression.count());
        Assert.assertEquals("bar", enom.getEntries().get(1).getName());
        List<Token> secondExpression = enom.getEntries().get(1).getExpression();
        Assert.assertNotNull(secondExpression);
        Assert.assertEquals(1, secondExpression.count());
    }

    private void testParseRemainingTokens() {
        List<Token> tokens = new List<>(
            f.word("enum"),
            f.word("Foo"),
            b.curlyBrackets(),
            f.word("Foo2")
        );
        TokenReader reader = new TokenReader(tokens);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has());
    }
}