package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.constants.Anonymous;
import cz.mg.c.parser.entities.Struct;
import cz.mg.c.parser.entities.Type;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;
import cz.mg.tokenizer.entities.tokens.WordToken;

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
    private final @Service BracketFactory b = BracketFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseNoFields() {
        List<Token> tokens = new List<>(
            new WordToken("struct", 0),
            new WordToken("Foo", 7),
            b.curlyBrackets()
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
            new WordToken("struct", 0),
            b.curlyBrackets(
                new WordToken("int", 13),
                new WordToken("bar", 17),
                new SeparatorToken(";", 20)
            )
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
            new WordToken("struct", 0),
            new WordToken("Foo", 7),
            b.curlyBrackets(
                new WordToken("int", 13),
                new WordToken("bar", 17),
                new SeparatorToken(";", 20)
            )
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
            new WordToken("const", 0),
            new WordToken("struct", 7),
            new WordToken("FooBar", 14),
            b.curlyBrackets(
                new WordToken("const", 22),
                new WordToken("int", 25),
                new WordToken("a", 30),
                new SeparatorToken(";", 35),
                new WordToken("int", 40),
                new WordToken("b", 45),
                new SeparatorToken(";", 50)
            ),
            new WordToken("const", 55),
            new OperatorToken("*", 60),
            new WordToken("const", 65)
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
            new WordToken("struct", 0),
            new WordToken("Foo", 7),
            b.curlyBrackets(),
            new WordToken("Foo2", 16)
        );
        TokenReader reader = new TokenReader(tokens);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has());
    }
}
