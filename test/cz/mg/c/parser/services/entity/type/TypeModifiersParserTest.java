package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CModifier;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.collections.set.Set;
import cz.mg.collections.set.Sets;
import cz.mg.test.Assert;
import cz.mg.token.Token;
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

    private final @Service ModifiersParser parser = ModifiersParser.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        verify(
            Sets.create(),
            parser.parse(new TokenReader(new List<>()))
        );
    }

    private void testParseSingle() {
        verify(
            Sets.create(CModifier.CONST),
            parser.parse(new TokenReader(new List<>(f.word("const"))))
        );

        verify(
            Sets.create(CModifier.STATIC),
            parser.parse(new TokenReader(new List<>(f.word("static"))))
        );

        verify(
            Sets.create(),
            parser.parse(new TokenReader(new List<>(f.doubleQuote("const"))))
        );

        verify(
            Sets.create(),
            parser.parse(new TokenReader(new List<>(f.number("0"))))
        );
    }

    private void testParseMultiple() {
        verify(
            Sets.create(CModifier.CONST, CModifier.STATIC),
            parser.parse(new TokenReader(new List<>(
                f.word("const"),
                f.word("static")
            )))
        );

        verify(
            Sets.create(CModifier.CONST),
            parser.parse(new TokenReader(new List<>(
                f.word("const"),
                f.word("const"),
                f.word("const")
            )))
        );

        verify(
            Sets.create(CModifier.CONST, CModifier.STATIC),
            parser.parse(new TokenReader(new List<>(
                f.word("static"),
                f.word("const"),
                f.word("static")
            )))
        );

        verify(
            Sets.create(),
            parser.parse(new TokenReader(new List<>(
                f.word("foo"),
                f.word("bar"),
                f.word("const")
            )))
        );

        verify(
            Sets.create(CModifier.CONST),
            parser.parse(new TokenReader(new List<>(
                f.word("const"),
                f.word("foo"),
                f.word("bar")
            )))
        );

        verify(
            Sets.create(),
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

    private void verify(@Mandatory Set<CModifier> expectations, Set<CModifier> reality) {
        Assert.assertEquals(expectations.count(), reality.count());
        for (CModifier expectation : expectations) {
            Assert.assertEquals(true, reality.contains(expectation));
        }
    }
}