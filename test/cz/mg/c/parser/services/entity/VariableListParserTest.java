package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.constants.Anonymous;
import cz.mg.c.parser.entities.CVariable;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Test class VariableListParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + VariableListParserTest.class.getSimpleName() + " ... ");

        VariableListParserTest test = new VariableListParserTest();
        test.testParseEmpty();
        test.testParseIllegalList();
        test.testParseAnonymous();
        test.testParseSingle();
        test.testParseMultiple();

        System.out.println("OK");
    }

    private final @Service VariableListParser parser = VariableListParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();

    private void testParseEmpty() {
        List<CVariable> variables = parser.parse(b.roundBrackets());
        Assert.assertEquals(true, variables.isEmpty());
    }

    private void testParseIllegalList() {
        Assert.assertThatCode(() -> {
            parser.parse(b.roundBrackets(new SeparatorToken(",", 0)));
        }).throwsException(ParseException.class);
    }

    private void testParseAnonymous() {
        List<CVariable> variables = parser.parse(b.roundBrackets(
            new WordToken("int", 0)
        ));
        Assert.assertEquals(1, variables.count());
        Assert.assertEquals("int", variables.getFirst().getType().getTypename().getName().getText());
        Assert.assertSame(Anonymous.NAME, variables.getFirst().getName());
    }

    private void testParseSingle() {
        List<CVariable> variables = parser.parse(b.roundBrackets(
            new WordToken("int", 0),
            new WordToken("a", 5)
        ));
        Assert.assertEquals(1, variables.count());
        Assert.assertEquals("int", variables.getFirst().getType().getTypename().getName().getText());
        Assert.assertEquals("a", variables.getFirst().getName().getText());
    }

    private void testParseMultiple() {
        List<CVariable> variables = parser.parse(b.roundBrackets(
            new WordToken("int", 0),
            new OperatorToken("*", 4),
            new WordToken("a", 5),
            new SeparatorToken(",", 6),
            new WordToken("float", 8),
            new WordToken("b", 15)
        ));
        Assert.assertEquals(2, variables.count());
        Assert.assertEquals("int", variables.getFirst().getType().getTypename().getName().getText());
        Assert.assertEquals("a", variables.getFirst().getName().getText());
        Assert.assertEquals(1, variables.getFirst().getType().getPointers().count());
        Assert.assertEquals("float", variables.getLast().getType().getTypename().getName().getText());
        Assert.assertEquals("b", variables.getLast().getName().getText());
        Assert.assertEquals(0, variables.getLast().getType().getPointers().count());
    }
}
