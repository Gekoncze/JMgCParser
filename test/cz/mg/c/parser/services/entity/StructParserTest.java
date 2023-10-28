package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Anonymous;
import cz.mg.c.parser.entities.Struct;
import cz.mg.c.parser.entities.Variable;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.NameToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;

public @Test class StructParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + StructParserTest.class.getSimpleName() + " ... ");

        StructParserTest test = new StructParserTest();
        test.testEmpty();
        test.testDeclaration();
        test.testNoVariables();
        test.testAnonymous();
        test.testSingleVariable();
        test.testMultipleVariables();
        test.testInvalid();

        System.out.println("OK");
    }

    private final @Service StructParser parser = StructParser.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testDeclaration() {
        List<Token> input = new List<>(
            new NameToken("struct", 0),
            new NameToken("Foo", 10)
        );
        Struct struct = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", struct.getName().getText());
        Assert.assertNull(struct.getVariables());
    }

    private void testNoVariables() {
        List<Token> input = new List<>(
            new NameToken("struct", 0),
            new NameToken("Foo", 10),
            new CurlyBrackets("", 15, new List<>())
        );
        Struct struct = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", struct.getName().getText());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(true, struct.getVariables().isEmpty());
    }

    private void testAnonymous() {
        List<Token> input = new List<>(
            new NameToken("struct", 0),
            new CurlyBrackets("", 15, new List<>())
        );
        Struct struct = parser.parse(new TokenReader(input));
        Assert.assertSame(Anonymous.NAME, struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(true, struct.getVariables().isEmpty());
    }

    private void testSingleVariable() {
        List<Token> input = new List<>(
            new NameToken("struct", 0),
            new NameToken("Foo", 10),
            new CurlyBrackets("", 15, new List<>(
                new NameToken("const", 17),
                new NameToken("int", 23),
                new OperatorToken("*", 26),
                new NameToken("bar", 28),
                new SeparatorToken(";", 32)
            ))
        );
        Struct struct = parser.parse(new TokenReader(input));
        Assert.assertEquals("Foo", struct.getName().getText());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(1, struct.getVariables().count());
        Assert.assertEquals("bar", struct.getVariables().getFirst().getName().getText());
        Variable variable = struct.getVariables().getFirst();
        Assert.assertEquals("int", variable.getType().getTypename().getName().getText());
        Assert.assertEquals(true, variable.getType().isConstant());
        Assert.assertEquals(1, struct.getVariables().getFirst().getType().getPointers().count());
    }

    private void testMultipleVariables() {
        List<Token> input = new List<>(
            new NameToken("struct", 0),
            new NameToken("Bar", 10),
            new CurlyBrackets("", 15, new List<>(
                new NameToken("int", 20),
                new NameToken("i", 25),
                new SeparatorToken(";", 26),
                new NameToken("long", 30),
                new NameToken("l", 35),
                new SeparatorToken(";", 36),
                new NameToken("short", 45),
                new NameToken("s", 48),
                new SeparatorToken(";", 49)
            ))
        );
        Struct struct = parser.parse(new TokenReader(input));
        Assert.assertEquals("Bar", struct.getName().getText());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(3, struct.getVariables().count());
        Assert.assertEquals("i", struct.getVariables().get(0).getName().getText());
        Assert.assertEquals("l", struct.getVariables().get(1).getName().getText());
        Assert.assertEquals("s", struct.getVariables().get(2).getName().getText());
    }

    private void testInvalid() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>(
                new NameToken("struct", 0),
                new NameToken("FooBar", 10),
                new CurlyBrackets("", 20, new List<>(
                    new NameToken("int", 25),
                    new NameToken("i", 30),
                    new NameToken("iii", 30),
                    new SeparatorToken(";", 35)
                ))
            )));
        }).throwsException(ParseException.class);
    }
}
