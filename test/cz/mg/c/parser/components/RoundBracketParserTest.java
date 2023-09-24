package cz.mg.c.parser.components;

import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.entities.groups.RoundBrackets;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.BracketToken;

public @Test class RoundBracketParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + RoundBracketParserTest.class.getSimpleName() + " ... ");

        RoundBracketParserTest test = new RoundBracketParserTest();
        test.testParse();

        System.out.println("OK");
    }

    private void testParse() {
        List<Token> input = new List<>(new BracketToken("(", 1), new BracketToken(")", 2));
        List<Token> output = new RoundBracketParser().parse(input);
        Assert.assertEquals(1, output.count());
        Assert.assertEquals(RoundBrackets.class, output.getFirst().getClass());
    }
}
