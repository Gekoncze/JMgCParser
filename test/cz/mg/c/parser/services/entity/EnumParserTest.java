package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Enum;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.NameToken;
import cz.mg.tokenizer.entities.tokens.NumberToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;

public @Test class EnumParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + EnumParserTest.class.getSimpleName() + " ... ");

        EnumParserTest test = new EnumParserTest();
        test.testEmpty();
        test.testDeclaration();
        test.testNoEntries();
        test.testSingleEntry();
        test.testMultipleEntries();
        test.testIllegalEntry();

        System.out.println("OK");
    }

    private final @Mandatory EnumParser parser = EnumParser.getInstance();

    private void testEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testDeclaration() {
        List<Token> input = new List<>(
            new NameToken("enum", 0),
            new NameToken("Nom", 6)
        );
        Enum enom = parser.parse(new TokenReader(input));
        Assert.assertEquals("Nom", enom.getName().getText());
        Assert.assertNull(enom.getEntries());
    }

    private void testNoEntries() {
        List<Token> input = new List<>(
            new NameToken("enum", 0),
            new NameToken("NomNom", 6),
            new CurlyBrackets("", 15, new List<>())
        );
        Enum enom = parser.parse(new TokenReader(input));
        Assert.assertEquals("NomNom", enom.getName().getText());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(true, enom.getEntries().isEmpty());
    }

    private void testSingleEntry() {
        List<Token> input = new List<>(
            new NameToken("enum", 0),
            new NameToken("NomNom", 6),
            new CurlyBrackets("", 15, new List<>(
                new NameToken("NOM", 20)
            ))
        );
        Enum enom = parser.parse(new TokenReader(input));
        Assert.assertEquals("NomNom", enom.getName().getText());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(1, enom.getEntries().count());
        Assert.assertEquals("NOM", enom.getEntries().getFirst().getName().getText());
        Assert.assertNull(enom.getEntries().getFirst().getExpression());
    }

    private void testMultipleEntries() {
        List<Token> input = new List<>(
            new NameToken("enum", 0),
            new NameToken("NomNomNom", 6),
            new CurlyBrackets("", 15, new List<>(
                new NameToken("NOM", 20),
                new OperatorToken("=", 24),
                new NumberToken("22", 26),
                new SeparatorToken(",", 28),
                new NameToken("NOM2", 30)
            ))
        );
        Enum enom = parser.parse(new TokenReader(input));
        Assert.assertEquals("NomNomNom", enom.getName().getText());
        Assert.assertNotNull(enom.getEntries());
        Assert.assertEquals(2, enom.getEntries().count());
        Assert.assertEquals("NOM", enom.getEntries().getFirst().getName().getText());
        Assert.assertNotNull(enom.getEntries().getFirst().getExpression());
        Assert.assertEquals("22", enom.getEntries().getFirst().getExpression().getLast().getText());
        Assert.assertEquals("NOM2", enom.getEntries().getLast().getName().getText());
        Assert.assertNull(enom.getEntries().getLast().getExpression());
    }

    private void testIllegalEntry() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>(
                new NameToken("enum", 0),
                new NameToken("NomNom", 6),
                new CurlyBrackets("", 15, new List<>(
                    new NameToken("NOM", 20),
                    new NameToken("NOM", 25),
                    new NameToken("NOM", 30)
                ))
            )));
        }).throwsException(ParseException.class);
    }
}