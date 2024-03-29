package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CTypeModifiers;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class TypeModifiersParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + TypeModifiersParserTest.class.getSimpleName() + " ... ");

        TypeModifiersParserTest test = new TypeModifiersParserTest();
        test.testParseEmpty();
        test.testParseSingle();
        test.testParseMultiple();
        test.testParseRemainingTokens();
        test.testParseNoRemainingTokens();

        System.out.println("OK");
    }

    private final @Service TypeModifiersParser parser = TypeModifiersParser.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        assertEquals(
            false, false,
            parser.parse(new TokenReader(new List<>()))
        );
    }

    private void testParseSingle() {
        assertEquals(
            true, false,
            parser.parse(new TokenReader(new List<>(f.word("const"))))
        );

        assertEquals(
            false, true,
            parser.parse(new TokenReader(new List<>(f.word("static"))))
        );

        assertEquals(
            false, false,
            parser.parse(new TokenReader(new List<>(f.doubleQuote("const"))))
        );

        assertEquals(
            false, false,
            parser.parse(new TokenReader(new List<>(f.number("0"))))
        );
    }

    private void testParseMultiple() {
        assertEquals(
            true, true,
            parser.parse(new TokenReader(new List<>(
                f.word("const"),
                f.word("static")
            )))
        );

        assertEquals(
            true, false,
            parser.parse(new TokenReader(new List<>(
                f.word("const"),
                f.word("const"),
                f.word("const")
            )))
        );

        assertEquals(
            true, true,
            parser.parse(new TokenReader(new List<>(
                f.word("static"),
                f.word("const"),
                f.word("static")
            )))
        );

        assertEquals(
            false, false,
            parser.parse(new TokenReader(new List<>(
                f.word("foo"),
                f.word("bar"),
                f.word("const")
            )))
        );

        assertEquals(
            true, false,
            parser.parse(new TokenReader(new List<>(
                f.word("const"),
                f.word("foo"),
                f.word("bar")
            )))
        );

        assertEquals(
            false, false,
            parser.parse(new TokenReader(new List<>(
                f.word("foo"),
                f.word("bar"),
                f.word("foobar")
            )))
        );
    }

    private void testParseRemainingTokens() {
        List<Token> input = new List<>(
            f.word("const"),
            f.word("foo")
        );
        TokenReader reader = new TokenReader(input);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has("foo"));
    }

    private void testParseNoRemainingTokens() {
        List<Token> input = new List<>(
            f.word("const"),
            f.word("const")
        );
        TokenReader reader = new TokenReader(input);
        parser.parse(reader);
        Assert.assertEquals(false, reader.has());
    }

    private void assertEquals(boolean constant, boolean isStatic, @Mandatory CTypeModifiers modifiers) {
        Assert.assertEquals(constant, modifiers.isConstant());
        Assert.assertEquals(isStatic, modifiers.isStatic());
    }
}
