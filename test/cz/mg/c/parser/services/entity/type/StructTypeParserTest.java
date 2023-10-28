package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Anonymous;
import cz.mg.c.parser.entities.Struct;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.entities.brackets.CurlyBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.NameToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;

public @Test class StructTypeParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + StructTypeParserTest.class.getSimpleName() + " ... ");

        StructTypeParserTest test = new StructTypeParserTest();
        test.testParseEmpty();
        test.testParseNoFields();
        test.testParseAnonymous();
        test.testParseNamed();
        test.testParseComplexConst();
        test.testParseRemainingTokens();

        System.out.println("OK");
    }

    private final @Service StructTypeParser parser = StructTypeParser.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseNoFields() {
        List<Token> tokens = new List<>(
            new NameToken("struct", 0),
            new NameToken("Foo", 7),
            new CurlyBrackets("", 11, new List<>())
        );
        Type type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof Struct);
        Struct struct = (Struct) type.getTypename();
        Assert.assertEquals("Foo", struct.getName().getText());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(true, struct.getVariables().isEmpty());
    }

    private void testParseAnonymous() {
        List<Token> tokens = new List<>(
            new NameToken("struct", 0),
            new CurlyBrackets("", 11, new List<>(
                new NameToken("int", 13),
                new NameToken("bar", 17),
                new SeparatorToken(";", 20)
            ))
        );
        Type type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof Struct);
        Struct struct = (Struct) type.getTypename();
        Assert.assertSame(Anonymous.NAME, struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(1, struct.getVariables().count());
        Assert.assertEquals("int", struct.getVariables().get(0).getType().getTypename().getName().getText());
        Assert.assertEquals("bar", struct.getVariables().get(0).getName().getText());
    }

    private void testParseNamed() {
        List<Token> tokens = new List<>(
            new NameToken("struct", 0),
            new NameToken("Foo", 7),
            new CurlyBrackets("", 11, new List<>(
                new NameToken("int", 13),
                new NameToken("bar", 17),
                new SeparatorToken(";", 20)
            ))
        );
        Type type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof Struct);
        Struct struct = (Struct) type.getTypename();
        Assert.assertEquals("Foo", struct.getName().getText());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(1, struct.getVariables().count());
        Assert.assertEquals("int", struct.getVariables().get(0).getType().getTypename().getName().getText());
        Assert.assertEquals("bar", struct.getVariables().get(0).getName().getText());
    }

    private void testParseComplexConst() {
        List<Token> tokens = new List<>(
            new NameToken("const", 0),
            new NameToken("struct", 7),
            new NameToken("FooBar", 14),
            new CurlyBrackets("", 20, new List<>(
                new NameToken("const", 22),
                new NameToken("int", 25),
                new NameToken("a", 30),
                new SeparatorToken(";", 35),
                new NameToken("int", 40),
                new NameToken("b", 45),
                new SeparatorToken(";", 50)
            )),
            new NameToken("const", 55),
            new OperatorToken("*", 60),
            new NameToken("const", 65)
        );
        Type type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(true, type.isConstant());
        Assert.assertEquals(1, type.getPointers().count());
        Assert.assertEquals(true, type.getPointers().getFirst().isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof Struct);
        Struct struct = (Struct) type.getTypename();
        Assert.assertEquals("FooBar", struct.getName().getText());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(2, struct.getVariables().count());
        Assert.assertEquals(true, struct.getVariables().get(0).getType().isConstant());
        Assert.assertEquals("int", struct.getVariables().get(0).getType().getTypename().getName().getText());
        Assert.assertEquals("a", struct.getVariables().get(0).getName().getText());
        Assert.assertEquals(false, struct.getVariables().get(1).getType().isConstant());
        Assert.assertEquals("int", struct.getVariables().get(1).getType().getTypename().getName().getText());
        Assert.assertEquals("b", struct.getVariables().get(1).getName().getText());
    }

    private void testParseRemainingTokens() {
        List<Token> tokens = new List<>(
            new NameToken("struct", 0),
            new NameToken("Foo", 7),
            new CurlyBrackets("", 11, new List<>()),
            new NameToken("Foo2", 16)
        );
        TokenReader reader = new TokenReader(tokens);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has());
    }
}
