package cz.mg.c.parser.services.lists;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.entities.lists.Statement;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.NameToken;
import cz.mg.tokenizer.entities.tokens.NumberToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;

public @Test class StatementParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + StatementParserTest.class.getSimpleName() + " ... ");

        StatementParserTest test = new StatementParserTest();
        test.testParseEmpty();
        test.testParseSingle();
        test.testParseMultiple();
        test.testParseMissingSemicolon();
        test.testParseFakeSemicolon();

        System.out.println("OK");
    }

    private final @Service StatementParser parser = StatementParser.getInstance();

    private void testParseEmpty() {
        List<Token> input = new List<>();
        List<Statement> output = parser.parse(input);
        Assert.assertEquals(true, output.isEmpty());
    }

    private void testParseSingle() {
        List<Token> input = new List<>(
            new NameToken("foo", 1),
            new NameToken("bar", 5),
            new SeparatorToken(";", 8)
        );
        List<Statement> output = parser.parse(input);

        Assert.assertEquals(1, output.count());
        Assert.assertEquals(Statement.class, output.getFirst().getClass());

        Statement statement = output.getFirst();
        Assert.assertEquals(2, statement.getTokens().count());

        Token foo = statement.getTokens().getFirst();
        Assert.assertEquals("foo", foo.getText());
        Assert.assertEquals(1, foo.getPosition());

        Token bar = statement.getTokens().getLast();
        Assert.assertEquals("bar", bar.getText());
        Assert.assertEquals(5, bar.getPosition());
    }

    private void testParseMultiple() {
        List<Token> input = new List<>(
            new NameToken("foo", 1),
            new NameToken("bar", 5),
            new SeparatorToken(";", 8),
            new NumberToken("77", 12),
            new SeparatorToken(";", 14)
        );
        List<Statement> output = parser.parse(input);

        Assert.assertEquals(2, output.count());
        Assert.assertEquals(Statement.class, output.getFirst().getClass());
        Assert.assertEquals(Statement.class, output.getLast().getClass());

        Statement statement = output.getLast();
        Assert.assertEquals(1, statement.getTokens().count());

        Token number = statement.getTokens().getFirst();
        Assert.assertEquals("77", number.getText());
        Assert.assertEquals(12, number.getPosition());
    }

    private void testParseMissingSemicolon() {
        Assert.assertThatCode(() -> {
            List<Token> input = new List<>(
                new NameToken("foo", 1),
                new NameToken("bar", 5)
            );
            parser.parse(input);
        }).throwsException(ParseException.class);
    }

    private void testParseFakeSemicolon() {
        Assert.assertThatCode(() -> {
            List<Token> input = new List<>(
                new NameToken("foo", 1),
                new NameToken("bar", 5),
                new Token(";", 8)
            );
            parser.parse(input);
        }).throwsException(ParseException.class);
    }
}
