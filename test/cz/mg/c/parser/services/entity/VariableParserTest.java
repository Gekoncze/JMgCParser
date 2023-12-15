package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.constants.Anonymous;
import cz.mg.c.parser.entities.CStruct;
import cz.mg.c.parser.entities.CType;
import cz.mg.c.parser.entities.CVariable;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.tokens.WordToken;
import cz.mg.tokenizer.entities.tokens.NumberToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;
import cz.mg.tokenizer.entities.tokens.SeparatorToken;
import cz.mg.tokenizer.test.TokenValidator;

public @Test class VariableParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + VariableParserTest.class.getSimpleName() + " ... ");

        VariableParserTest test = new VariableParserTest();
        test.testParseEmpty();
        test.testParseSimple();
        test.testParseAnonymous();
        test.testParseArray();
        test.testParseArrays();
        test.testParseArrayExpression();
        test.testParseComplex();
        test.testParseInlineType();
        test.testParseWithType();

        System.out.println("OK");
    }

    private final @Service VariableParser parser = VariableParser.getInstance();
    private final @Service TokenValidator tokenValidator = TokenValidator.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseSimple() {
        TokenReader reader = new TokenReader(new List<>(
            new WordToken("int", 2), new WordToken("foo", 5)
        ));

        CVariable variable = parser.parse(reader);

        Assert.assertEquals("foo", variable.getName().getText());
        Assert.assertEquals(true, variable.getType().getArrays().isEmpty());
        Assert.assertEquals(false, variable.getType().isConstant());
        Assert.assertEquals("int", variable.getType().getTypename().getName().getText());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseAnonymous() {
        TokenReader reader = new TokenReader(new List<>(new WordToken("int", 2)));

        CVariable variable = parser.parse(reader);

        Assert.assertSame(Anonymous.NAME, variable.getName());
        Assert.assertEquals(true, variable.getType().getArrays().isEmpty());
        Assert.assertEquals(false, variable.getType().isConstant());
        Assert.assertEquals("int", variable.getType().getTypename().getName().getText());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseArray() {
        TokenReader reader = new TokenReader(new List<>(
            new WordToken("float", 1),
            new WordToken("bar", 5),
            b.squareBrackets(
                new NumberToken("12", 7)
            )
        ));

        CVariable variable = parser.parse(reader);

        Assert.assertEquals("bar", variable.getName().getText());
        Assert.assertEquals(1, variable.getType().getArrays().count());
        tokenValidator.assertEquals(
            new List<>(new NumberToken("12", 7)),
            variable.getType().getArrays().getFirst().getExpression()
        );
        Assert.assertEquals(false, variable.getType().isConstant());
        Assert.assertEquals("float", variable.getType().getTypename().getName().getText());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseArrays() {
        TokenReader reader = new TokenReader(new List<>(
            new WordToken("double", 1),
            new WordToken("foobar", 5),
            b.squareBrackets(
                new NumberToken("9", 7)
            ),
            b.squareBrackets(
                new NumberToken("3", 7)
            ),
            b.squareBrackets(
                new NumberToken("1", 7)
            )
        ));

        CVariable variable = parser.parse(reader);

        Assert.assertEquals("foobar", variable.getName().getText());
        Assert.assertEquals(3, variable.getType().getArrays().count());
        tokenValidator.assertEquals(
            new List<>(new NumberToken("9", 7)),
            variable.getType().getArrays().get(0).getExpression()
        );
        tokenValidator.assertEquals(
            new List<>(new NumberToken("3", 7)),
            variable.getType().getArrays().get(1).getExpression()
        );
        tokenValidator.assertEquals(
            new List<>(new NumberToken("1", 7)),
            variable.getType().getArrays().get(2).getExpression()
        );
        Assert.assertEquals(false, variable.getType().isConstant());
        Assert.assertEquals("double", variable.getType().getTypename().getName().getText());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseArrayExpression() {
        TokenReader reader = new TokenReader(new List<>(
            new WordToken("float", 1),
            new WordToken("bar", 5),
            b.squareBrackets(
                new NumberToken("12", 7),
                new OperatorToken("+", 8),
                new NumberToken("1.5", 9)
            )
        ));

        CVariable variable = parser.parse(reader);

        Assert.assertEquals("bar", variable.getName().getText());
        Assert.assertEquals(1, variable.getType().getArrays().count());
        tokenValidator.assertEquals(
            new List<>(
                new NumberToken("12", 7),
                new OperatorToken("+", 8),
                new NumberToken("1.5", 9)
            ),
            variable.getType().getArrays().getFirst().getExpression()
        );
        Assert.assertEquals(false, variable.getType().isConstant());
        Assert.assertEquals("float", variable.getType().getTypename().getName().getText());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseComplex() {
        TokenReader reader = new TokenReader(new List<>(
            new WordToken("const", 0),
            new WordToken("float", 6),
            new OperatorToken("*", 11),
            new WordToken("const", 12),
            new WordToken("bar", 20),
            b.squareBrackets(
                new NumberToken("12", 22),
                new OperatorToken("+", 24),
                new NumberToken("1.5", 25)
            )
        ));

        CVariable variable = parser.parse(reader);

        Assert.assertEquals("bar", variable.getName().getText());
        Assert.assertEquals(1, variable.getType().getArrays().count());
        tokenValidator.assertEquals(
            new List<>(
                new NumberToken("12", 22),
                new OperatorToken("+", 24),
                new NumberToken("1.5", 25)
            ),
            variable.getType().getArrays().getFirst().getExpression()
        );
        Assert.assertEquals(true, variable.getType().isConstant());
        Assert.assertEquals("float", variable.getType().getTypename().getName().getText());
        Assert.assertEquals(1, variable.getType().getPointers().count());
        Assert.assertEquals(true, variable.getType().getPointers().getFirst().isConstant());
        reader.readEnd();
    }

    private void testParseInlineType() {
        TokenReader reader = new TokenReader(new List<>(
            new WordToken("const", 0),
            new WordToken("struct", 7),
            b.curlyBrackets(
                new WordToken("int", 15),
                new WordToken("a", 17),
                new SeparatorToken(";", 18)
            ),
            new OperatorToken("*", 20),
            new WordToken("foobar", 22),
            b.squareBrackets(
                new NumberToken("2", 26)
            )
        ));

        CVariable variable = parser.parse(reader);

        Assert.assertEquals("foobar", variable.getName().getText());
        Assert.assertEquals(1, variable.getType().getArrays().count());
        Assert.assertEquals(1, variable.getType().getPointers().count());
        Assert.assertEquals(true, variable.getType().isConstant());
        Assert.assertEquals(true, variable.getType().getTypename() instanceof CStruct);
        Assert.assertSame(Anonymous.NAME, variable.getType().getTypename().getName());
    }

    private void testParseWithType() {
        TokenReader reader = new TokenReader(new List<>(new WordToken("foo", 5)));
        CType type = new CType();

        CVariable variable = parser.parse(reader, type);

        Assert.assertEquals("foo", variable.getName().getText());
        Assert.assertSame(type, variable.getType());
        reader.readEnd();
    }
}
