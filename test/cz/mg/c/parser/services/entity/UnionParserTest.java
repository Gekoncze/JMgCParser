package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CUnion;
import cz.mg.c.entities.CVariable;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Test class UnionParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + UnionParserTest.class.getSimpleName() + " ... ");

        UnionParserTest test = new UnionParserTest();
        test.testEmpty();
        test.testDeclaration();
        test.testNoVariables();
        test.testAnonymous();
        test.testSingleVariable();
        test.testMultipleVariables();
        test.testInvalid();

        System.out.println("OK");
    }

    private final @Service UnionParser parser = UnionParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testDeclaration() {
        List<Token> input = new List<>(
            new WordToken("union", 0),
            new WordToken("Foo", 10)
        );
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", union.getName());
        Assert.assertNull(union.getVariables());
    }

    private void testNoVariables() {
        List<Token> input = new List<>(
            new WordToken("union", 0),
            new WordToken("Foo", 10),
            b.curlyBrackets()
        );
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(true, union.getVariables().isEmpty());
    }

    private void testAnonymous() {
        List<Token> input = new List<>(
            new WordToken("union", 0),
            b.curlyBrackets()
        );
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertNull(union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(true, union.getVariables().isEmpty());
    }

    private void testSingleVariable() {
        List<Token> input = new List<>(
            new WordToken("union", 0),
            new WordToken("Foo", 10),
            b.curlyBrackets(
                new WordToken("const", 17),
                new WordToken("int", 23),
                new OperatorToken("*", 26),
                new WordToken("bar", 28),
                new SeparatorToken(";", 32)
            )
        );
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(1, union.getVariables().count());
        Assert.assertEquals("bar", union.getVariables().getFirst().getName());
        CVariable variable = union.getVariables().getFirst();
        Assert.assertEquals("int", variable.getType().getTypename().getName());
        Assert.assertEquals(true, variable.getType().isConstant());
        Assert.assertEquals(1, union.getVariables().getFirst().getType().getPointers().count());
    }

    private void testMultipleVariables() {
        List<Token> input = new List<>(
            new WordToken("union", 0),
            new WordToken("Bar", 10),
            b.curlyBrackets(
                new WordToken("int", 20),
                new WordToken("i", 25),
                new SeparatorToken(";", 26),
                new WordToken("long", 30),
                new WordToken("l", 35),
                new SeparatorToken(";", 36),
                new WordToken("short", 45),
                new WordToken("s", 48),
                new SeparatorToken(";", 49)
            )
        );
        CUnion union = parser.parse(new TokenReader(input));
        Assert.assertEquals("Bar", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(3, union.getVariables().count());
        Assert.assertEquals("i", union.getVariables().get(0).getName());
        Assert.assertEquals("l", union.getVariables().get(1).getName());
        Assert.assertEquals("s", union.getVariables().get(2).getName());
    }

    private void testInvalid() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>(
                new WordToken("union", 0),
                new WordToken("FooBar", 10),
                b.curlyBrackets(
                    new WordToken("int", 25),
                    new WordToken("i", 30),
                    new WordToken("iii", 30),
                    new SeparatorToken(";", 35)
                )
            )));
        }).throwsException(ParseException.class);
    }
}
