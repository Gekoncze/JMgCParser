package cz.mg.c.parser.components;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.entities.groups.SquareBrackets;
import cz.mg.c.parser.services.SquareBracketParser;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.BracketToken;

public @Test class SquareBracketParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + SquareBracketParserTest.class.getSimpleName() + " ... ");

        SquareBracketParserTest test = new SquareBracketParserTest();
        test.testParse();

        System.out.println("OK");
    }

    private final @Service SquareBracketParser parser = SquareBracketParser.getInstance();

    private void testParse() {
        List<Token> input = new List<>(new BracketToken("[", 1), new BracketToken("]", 2));
        List<Token> output = parser.parse(input);
        Assert.assertEquals(1, output.count());
        Assert.assertEquals(SquareBrackets.class, output.getFirst().getClass());
    }
}
