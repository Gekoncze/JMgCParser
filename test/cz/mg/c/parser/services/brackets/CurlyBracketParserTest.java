package cz.mg.c.parser.services.brackets;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.BracketToken;

public @Test class CurlyBracketParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + CurlyBracketParserTest.class.getSimpleName() + " ... ");

        CurlyBracketParserTest test = new CurlyBracketParserTest();
        test.testParse();

        System.out.println("OK");
    }

    private final @Service CurlyBracketParser parser = CurlyBracketParser.getInstance();

    private void testParse() {
        List<Token> input = new List<>(new BracketToken("{", 1), new BracketToken("}", 2));
        List<Token> output = parser.parse(input);
        Assert.assertEquals(1, output.count());
        Assert.assertEquals(CurlyBrackets.class, output.getFirst().getClass());
    }
}
