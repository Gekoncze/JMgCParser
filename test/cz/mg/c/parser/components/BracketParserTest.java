package cz.mg.c.parser.components;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.brackets.Brackets;
import cz.mg.c.parser.entities.brackets.RoundBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.*;

public @Test class BracketParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + BracketParserTest.class.getSimpleName() + " ... ");

        BracketParserTest test = new BracketParserTest();
        test.testParseEmpty();
        test.testParseNoGroup();
        test.testParseOnlyGroup();
        test.testParseMissingLeftBracket();
        test.testParseMissingRightBracket();
        test.testParseNested();
        test.testParseExistingGroups();

        System.out.println("OK");
    }

    private final @Mandatory BracketParser parser = new BracketParser("test", "(", ")", RoundBrackets::new);

    private void testParseEmpty() {
        Assert.assertEquals(true, parser.parse(new List<>()).isEmpty());
    }

    private void testParseNoGroup() {
        List<Token> input = new List<>(new NameToken("foo", 7));
        List<Token> output = parser.parse(input);
        Assert.assertEquals(1, output.count());
        Assert.assertEquals(7, output.getFirst().getPosition());
        Assert.assertEquals("foo", output.getFirst().getText());
    }

    private void testParseOnlyGroup() {
        List<Token> input = new List<>(new BracketToken("(", 3), new BracketToken(")", 4));
        List<Token> output = parser.parse(input);
        Assert.assertEquals(1, output.count());
        Assert.assertEquals(RoundBrackets.class, output.getFirst().getClass());
        Assert.assertEquals(3, output.getFirst().getPosition());
        Assert.assertEquals("", output.getFirst().getText());
    }

    private void testParseMissingLeftBracket() {
        ParseException exception = Assert.assertThatCode(() -> {
            List<Token> input = new List<>(new BracketToken(")", 4));
            parser.parse(input);
        }).throwsException(ParseException.class);
        Assert.assertEquals("Missing left test parenthesis.", exception.getMessage());
        Assert.assertEquals(4, exception.getPosition());
    }

    private void testParseMissingRightBracket() {
        ParseException exception = Assert.assertThatCode(() -> {
            List<Token> input = new List<>(new BracketToken("(", 3));
            parser.parse(input);
        }).throwsException(ParseException.class);
        Assert.assertEquals("Missing right test parenthesis.", exception.getMessage());
        Assert.assertEquals(3, exception.getPosition());
    }

    private void testParseNested() {
        List<Token> input = new List<>(
            new OperatorToken("+", 0),
            new BracketToken("(", 1),
            new NameToken("foo", 2),
            new BracketToken("(", 5),
            new DoubleQuoteToken("(", 6),
            new BracketToken(")", 9),
            new NameToken("bar", 10),
            new BracketToken(")", 13),
            new OperatorToken("*", 14)
        );

        List<Token> output = parser.parse(input);
        Assert.assertEquals(3, output.count());

        Assert.assertEquals("+", output.getFirst().getText());
        Assert.assertEquals(0, output.getFirst().getPosition());

        Assert.assertEquals("*", output.getLast().getText());
        Assert.assertEquals(14, output.getLast().getPosition());

        RoundBrackets level1 = (RoundBrackets) output.get(1);
        Assert.assertEquals("", level1.getText());
        Assert.assertEquals(1, level1.getPosition());
        Assert.assertEquals(3, level1.getTokens().count());

        Assert.assertEquals("foo", level1.getTokens().getFirst().getText());
        Assert.assertEquals(2, level1.getTokens().getFirst().getPosition());

        Assert.assertEquals("bar", level1.getTokens().getLast().getText());
        Assert.assertEquals(10, level1.getTokens().getLast().getPosition());

        RoundBrackets level2 = (RoundBrackets) level1.getTokens().get(1);
        Assert.assertEquals("", level2.getText());
        Assert.assertEquals(5, level2.getPosition());
        Assert.assertEquals(1, level2.getTokens().count());

        Assert.assertEquals("(", level2.getTokens().getFirst().getText());
        Assert.assertEquals(6, level2.getTokens().getFirst().getPosition());
    }

    private void testParseExistingGroups() {
        List<Token> input = new List<>(
            new BracketToken("(", 3),
            new BracketToken(")", 4),
            new Brackets("", 10, new List<>(
                new BracketToken("(", 12),
                new BracketToken(")", 13)
            ))
        );

        List<Token> output = parser.parse(input);
        Assert.assertEquals(2, output.count());

        Assert.assertEquals(RoundBrackets.class, output.getFirst().getClass());
        Assert.assertEquals(3, output.getFirst().getPosition());
        Assert.assertEquals("", output.getFirst().getText());

        Brackets brackets = (Brackets) output.getLast();
        Assert.assertEquals(1, brackets.getTokens().count());

        Assert.assertEquals(RoundBrackets.class, brackets.getTokens().getFirst().getClass());
        Assert.assertEquals(12, brackets.getTokens().getFirst().getPosition());
        Assert.assertEquals("", brackets.getTokens().getFirst().getText());
    }
}
