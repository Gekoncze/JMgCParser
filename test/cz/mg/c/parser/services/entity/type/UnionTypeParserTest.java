package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.entities.CType;
import cz.mg.c.entities.CUnion;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.Token;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;
import cz.mg.tokenizer.entities.tokens.WordToken;

public @Test class UnionTypeParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + UnionTypeParserTest.class.getSimpleName() + " ... ");

        UnionTypeParserTest test = new UnionTypeParserTest();
        test.testParseEmpty();
        test.testParseNoFields();
        test.testParseAnonymous();
        test.testParseNamed();
        test.testParseComplexConst();
        test.testParseRemainingTokens();

        System.out.println("OK");
    }

    private final @Service UnionTypeParser parser = UnionTypeParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseNoFields() {
        List<Token> tokens = new List<>(
            new WordToken("union", 0),
            new WordToken("Foo", 7),
            b.curlyBrackets()
        );
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof CUnion);
        CUnion union = (CUnion) type.getTypename();
        Assert.assertEquals("Foo", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(true, union.getVariables().isEmpty());
    }

    private void testParseAnonymous() {
        List<Token> tokens = new List<>(
            new WordToken("union", 0),
            b.curlyBrackets(
                new WordToken("int", 13),
                new WordToken("bar", 17),
                new SeparatorToken(";", 20)
            )
        );
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof CUnion);
        CUnion union = (CUnion) type.getTypename();
        Assert.assertNull(union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(1, union.getVariables().count());
        Assert.assertEquals("int", union.getVariables().get(0).getType().getTypename().getName());
        Assert.assertEquals("bar", union.getVariables().get(0).getName());
    }

    private void testParseNamed() {
        List<Token> tokens = new List<>(
            new WordToken("union", 0),
            new WordToken("Foo", 7),
            b.curlyBrackets(
                new WordToken("int", 13),
                new WordToken("bar", 17),
                new SeparatorToken(";", 20)
            )
        );
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof CUnion);
        CUnion union = (CUnion) type.getTypename();
        Assert.assertEquals("Foo", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(1, union.getVariables().count());
        Assert.assertEquals("int", union.getVariables().get(0).getType().getTypename().getName());
        Assert.assertEquals("bar", union.getVariables().get(0).getName());
    }

    private void testParseComplexConst() {
        List<Token> tokens = new List<>(
            new WordToken("const", 0),
            new WordToken("union", 7),
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
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(true, type.isConstant());
        Assert.assertEquals(1, type.getPointers().count());
        Assert.assertEquals(true, type.getPointers().getFirst().isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof CUnion);
        CUnion union = (CUnion) type.getTypename();
        Assert.assertEquals("FooBar", union.getName());
        Assert.assertNotNull(union.getVariables());
        Assert.assertEquals(2, union.getVariables().count());
        Assert.assertEquals(true, union.getVariables().get(0).getType().isConstant());
        Assert.assertEquals("int", union.getVariables().get(0).getType().getTypename().getName());
        Assert.assertEquals("a", union.getVariables().get(0).getName());
        Assert.assertEquals(false, union.getVariables().get(1).getType().isConstant());
        Assert.assertEquals("int", union.getVariables().get(1).getType().getTypename().getName());
        Assert.assertEquals("b", union.getVariables().get(1).getName());
    }

    private void testParseRemainingTokens() {
        List<Token> tokens = new List<>(
            new WordToken("union", 0),
            new WordToken("Foo", 7),
            b.curlyBrackets(),
            new WordToken("Foo2", 16)
        );
        TokenReader reader = new TokenReader(tokens);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has());
    }
}
