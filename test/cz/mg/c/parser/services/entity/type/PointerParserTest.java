package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CPointer;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class PointerParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + PointerParserTest.class.getSimpleName() + " ... ");

        PointerParserTest test = new PointerParserTest();
        test.testParseEmpty();
        test.testParseSingle();
        test.testParseSingleConst();
        test.testParseSingleGroup();
        test.testParseSingleGroupConst();
        test.testParseMultiple();
        test.testParseMultipleGroup();
        test.testMixed();
        test.testParseRemainingTokens();

        System.out.println("OK");
    }

    private final @Service PointerParser parser = PointerParser.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertEquals(true, parser.parse(new TokenReader(new List<>())).isEmpty());
        Assert.assertEquals(true, parser.parse(new TokenReader(new List<>(f.symbol("+")))).isEmpty());
        Assert.assertEquals(true, parser.parse(new TokenReader(new List<>(f.doubleQuote("*")))).isEmpty());
        Assert.assertEquals(true, parser.parse(new TokenReader(new List<>(f.word("const")))).isEmpty());
    }

    private void testParseSingle() {
        List<Token> tokens = new List<>(f.symbol("*"));
        List<CPointer> pointers = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(1, pointers.count());
        Assert.assertEquals(false, pointers.getFirst().isConstant());
    }

    private void testParseSingleConst() {
        List<Token> tokens = new List<>(f.symbol("*"), f.word("const"));
        List<CPointer> pointers = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(1, pointers.count());
        Assert.assertEquals(true, pointers.getFirst().isConstant());
    }

    private void testParseSingleGroup() {
        List<Token> tokens = new List<>(f.symbol("***"));
        List<CPointer> pointers = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(3, pointers.count());
        Assert.assertEquals(false, pointers.get(0).isConstant());
        Assert.assertEquals(false, pointers.get(1).isConstant());
        Assert.assertEquals(false, pointers.get(2).isConstant());
    }

    private void testParseSingleGroupConst() {
        List<Token> tokens = new List<>(f.symbol("***"), f.word("const"));
        List<CPointer> pointers = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(3, pointers.count());
        Assert.assertEquals(false, pointers.get(0).isConstant());
        Assert.assertEquals(false, pointers.get(1).isConstant());
        Assert.assertEquals(true, pointers.get(2).isConstant());
    }

    private void testParseMultiple() {
        List<Token> tokens = new List<>(
            f.symbol("*"),
            f.symbol("*"),
            f.word("const"),
            f.symbol("*")
        );
        List<CPointer> pointers = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(3, pointers.count());
        Assert.assertEquals(false, pointers.get(0).isConstant());
        Assert.assertEquals(true, pointers.get(1).isConstant());
        Assert.assertEquals(false, pointers.get(2).isConstant());
    }

    private void testParseMultipleGroup() {
        List<Token> tokens = new List<>(
            f.symbol("***"),
            f.word("const"),
            f.symbol("**")
        );
        List<CPointer> pointers = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(5, pointers.count());
        Assert.assertEquals(false, pointers.get(0).isConstant());
        Assert.assertEquals(false, pointers.get(1).isConstant());
        Assert.assertEquals(true, pointers.get(2).isConstant());
        Assert.assertEquals(false, pointers.get(3).isConstant());
        Assert.assertEquals(false, pointers.get(4).isConstant());
    }

    private void testMixed() {
        List<Token> tokens = new List<>(
            f.symbol("*"),
            f.word("const"),
            f.word("const"),
            f.symbol("***"),
            f.symbol("*")
        );
        List<CPointer> pointers = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(5, pointers.count());
        Assert.assertEquals(true, pointers.get(0).isConstant());
        Assert.assertEquals(false, pointers.get(1).isConstant());
        Assert.assertEquals(false, pointers.get(2).isConstant());
        Assert.assertEquals(false, pointers.get(3).isConstant());
        Assert.assertEquals(false, pointers.get(4).isConstant());
    }

    private void testParseRemainingTokens() {
        List<Token> tokens = new List<>(f.symbol("*"), f.word("foo"));
        TokenReader reader = new TokenReader(tokens);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has());
    }
}