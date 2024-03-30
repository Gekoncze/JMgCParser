package cz.mg.c.parser.services.bracket;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;
import cz.mg.tokenizer.test.TokenValidator;

public @Test class BracketParsersTest {
    public static void main(String[] args) {
        System.out.print("Running " + BracketParsersTest.class.getSimpleName() + " ... ");

        BracketParsersTest test = new BracketParsersTest();
        test.testParseEmpty();
        test.testParseOne();
        test.testParse();

        System.out.println("OK");
    }

    private final @Service BracketParsers parsers = BracketParsers.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenValidator validator = TokenValidator.getInstance();

    private void testParseEmpty() {
        List<Token> tokens = parsers.parse(new List<>());
        Assert.assertEquals(true, tokens.isEmpty());
    }

    private void testParseOne() {
        List<Token> input = new List<>(
            f.symbol("{"),
            f.symbol("}")
        );

        List<Token> output = parsers.parse(input);
        validator.assertEquals(
            new List<>(b.curlyBrackets()),
            output
        );
    }

    private void testParse() {
        List<Token> input = new List<>(
            f.word("void"),
            f.word("fooBar"),
            f.symbol("("),
            f.word("struct"),
            f.symbol("{"),
            f.symbol("}"),
            f.word("parameter"),
            f.symbol(")"),
            f.symbol("{"),
            f.word("if"),
            f.symbol("("),
            f.word("true"),
            f.symbol(")"),
            f.word("return"),
            f.symbol(";"),
            f.symbol("}")
        );

        List<Token> output = parsers.parse(input);
        validator.assertEquals(
            new List<>(
                f.word("void"),
                f.word("fooBar"),
                b.roundBrackets(
                    f.word("struct"),
                    b.curlyBrackets(),
                    f.word("parameter")
                ),
                b.curlyBrackets(
                    f.word("if"),
                    b.roundBrackets(
                        f.word("true")
                    ),
                    f.word("return"),
                    f.symbol(";")
                )
            ),
            output
        );
    }
}