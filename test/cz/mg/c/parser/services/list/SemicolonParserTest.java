package cz.mg.c.parser.services.list;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class SemicolonParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + SemicolonParserTest.class.getSimpleName() + " ... ");

        SemicolonParserTest test = new SemicolonParserTest();
        test.testParseEmpty();
        test.testParseSingle();
        test.testParseMultiple();
        test.testParseMissingSemicolon();
        test.testParseFakeSemicolon();

        System.out.println("OK");
    }

    private final @Service SemicolonParser parser = SemicolonParser.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        List<Token> input = new List<>();
        List<List<Token>> output = parser.parse(input);
        Assert.assertEquals(true, output.isEmpty());
    }

    private void testParseSingle() {
        List<Token> input = new List<>(
            f.word("foo"),
            f.word("bar"),
            f.separator(";")
        );
        List<List<Token>> output = parser.parse(input);

        Assert.assertEquals(1, output.count());

        List<Token> group = output.getFirst();
        Assert.assertEquals(2, group.count());

        Token foo = group.getFirst();
        Assert.assertEquals("foo", foo.getText());

        Token bar = group.getLast();
        Assert.assertEquals("bar", bar.getText());
    }

    private void testParseMultiple() {
        List<Token> input = new List<>(
            f.word("foo"),
            f.word("bar"),
            f.separator(";"),
            f.number("77"),
            f.separator(";")
        );
        List<List<Token>> output = parser.parse(input);

        Assert.assertEquals(2, output.count());

        List<Token> group = output.getLast();
        Assert.assertEquals(1, group.count());

        Token number = group.getFirst();
        Assert.assertEquals("77", number.getText());
    }

    private void testParseMissingSemicolon() {
        Assert.assertThatCode(() -> {
            List<Token> input = new List<>(
                f.word("foo"),
                f.word("bar")
            );
            parser.parse(input);
        }).throwsException(ParseException.class);
    }

    private void testParseFakeSemicolon() {
        Assert.assertThatCode(() -> {
            List<Token> input = new List<>(
                f.word("foo"),
                f.word("bar"),
                f.plain(";")
            );
            parser.parse(input);
        }).throwsException(ParseException.class);
    }
}