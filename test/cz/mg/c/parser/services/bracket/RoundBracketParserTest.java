package cz.mg.c.parser.services.bracket;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.brackets.RoundBrackets;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class RoundBracketParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + RoundBracketParserTest.class.getSimpleName() + " ... ");

        RoundBracketParserTest test = new RoundBracketParserTest();
        test.testParse();

        System.out.println("OK");
    }

    private final @Service RoundBracketParser parser = RoundBracketParser.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParse() {
        List<Token> input = new List<>(f.bracket("("), f.bracket(")"));
        List<Token> output = parser.parse(input);
        Assert.assertEquals(1, output.count());
        Assert.assertEquals(RoundBrackets.class, output.getFirst().getClass());
    }
}
