package cz.mg.c.parser.services.lists;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.entities.brackets.RoundBrackets;
import cz.mg.c.parser.entities.lists.Statement;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Test class BlockStatementParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + BlockStatementParserTest.class.getSimpleName() + " ... ");

        BlockStatementParserTest test = new BlockStatementParserTest();
        test.testParseEmpty();
        test.testParseUnchanged();
        test.testParseSplit();
        test.testParseSplitMultiple();

        System.out.println("OK");
    }

    private final @Service BlockStatementParser parser = BlockStatementParser.getInstance();

    private void testParseEmpty() {
        List<Statement> input = new List<>();
        List<Statement> output = parser.parse(input);
        Assert.assertEquals(true, output.isEmpty());
    }

    private void testParseUnchanged() {
        List<Statement> input = new List<>(
            new Statement(new List<>(
                new NameToken("foo", 0),
                new CurlyBrackets(),
                new RoundBrackets(),
                new NameToken("bar", 0)
            ))
        );
        List<Statement> output = parser.parse(input);
        Assert.assertEquals(1, output.count());
        Assert.assertEquals(4, output.getFirst().getTokens().count());
    }

    private void testParseSplit() {
        List<Statement> input = new List<>(
            new Statement(new List<>(
                new NameToken("foo", 0),
                new RoundBrackets(),
                new CurlyBrackets(),
                new NameToken("bar", 0)
            ))
        );
        List<Statement> output = parser.parse(input);
        Assert.assertEquals(2, output.count());
        Assert.assertEquals(3, output.getFirst().getTokens().count());
        Assert.assertEquals(1, output.getLast().getTokens().count());
    }

    private void testParseSplitMultiple() {
        List<Statement> input = new List<>(
            new Statement(new List<>(
                new NameToken("foo", 0),
                new RoundBrackets(),
                new CurlyBrackets(),
                new NameToken("bar", 0),
                new RoundBrackets(),
                new CurlyBrackets(),
                new NameToken("foobar", 0),
                new RoundBrackets(),
                new CurlyBrackets()
            ))
        );
        List<Statement> output = parser.parse(input);
        Assert.assertEquals(3, output.count());
        Assert.assertEquals(3, output.getFirst().getTokens().count());
        Assert.assertEquals(3, output.get(1).getTokens().count());
        Assert.assertEquals(3, output.getLast().getTokens().count());
    }
}
