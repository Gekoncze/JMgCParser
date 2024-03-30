package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CEnum;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class EnumParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + EnumParserTest.class.getSimpleName() + " ... ");

        EnumParserTest test = new EnumParserTest();
        test.testEmpty();
        test.testDeclaration();
        test.testNoEntries();
        test.testAnonymous();
        test.testSingleEntry();
        test.testMultipleEntries();
        test.testIllegalEntry();

        System.out.println("OK");
    }

    private final @Service EnumParser parser = EnumParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testDeclaration() {
        List<Token> input = new List<>(
            f.word("enum"),
            f.word("Nom")
        );
        CEnum enom = parser.parse(new TokenReader(input));
        Assert.assertEquals("Nom", enom.getName());
        Assert.assertNull(enom.getEntries());
    }

    private void testNoEntries() {
        List<Token> input = new List<>(
            f.word("enum"),
            f.word("NomNom"),
            b.curlyBrackets()
        );
        CEnum enom = parser.parse(new TokenReader(input));
        Assert.assertEquals("NomNom", enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(true, enom.getEntries().isEmpty());
    }

    private void testAnonymous() {
        List<Token> input = new List<>(
            f.word("enum"),
            b.curlyBrackets()
        );
        CEnum enom = parser.parse(new TokenReader(input));
        Assert.assertNull(enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(true, enom.getEntries().isEmpty());
    }

    private void testSingleEntry() {
        List<Token> input = new List<>(
            f.word("enum"),
            f.word("NomNom"),
            b.curlyBrackets(
                f.word("NOM")
            )
        );
        CEnum enom = parser.parse(new TokenReader(input));
        Assert.assertEquals("NomNom", enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(1, enom.getEntries().count());
        Assert.assertEquals("NOM", enom.getEntries().getFirst().getName());
        Assert.assertNull(enom.getEntries().getFirst().getExpression());
    }

    private void testMultipleEntries() {
        List<Token> input = new List<>(
            f.word("enum"),
            f.word("NomNomNom"),
            b.curlyBrackets(
                f.word("NOM"),
                f.symbol("="),
                f.number("22"),
                f.symbol(","),
                f.word("NOM2")
            )
        );
        CEnum enom = parser.parse(new TokenReader(input));
        Assert.assertEquals("NomNomNom", enom.getName());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(2, enom.getEntries().count());
        Assert.assertEquals("NOM", enom.getEntries().getFirst().getName());
        Assert.assertNotNull(enom.getEntries().getFirst().getExpression());
        Assert.assertEquals("22", enom.getEntries().getFirst().getExpression().getLast().getText());
        Assert.assertEquals("NOM2", enom.getEntries().getLast().getName());
        Assert.assertNull(enom.getEntries().getLast().getExpression());
    }

    private void testIllegalEntry() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>(
                f.word("enum"),
                f.word("NomNom"),
                b.curlyBrackets(
                    f.word("NOM"),
                    f.word("NOM"),
                    f.word("NOM")
                )
            )));
        }).throwsException(ParseException.class);
    }
}