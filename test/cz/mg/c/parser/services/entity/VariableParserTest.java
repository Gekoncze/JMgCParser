package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.entities.Variable;
import cz.mg.c.parser.entities.brackets.SquareBrackets;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.TokenValidator;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.tokenizer.entities.tokens.NameToken;
import cz.mg.tokenizer.entities.tokens.NumberToken;
import cz.mg.tokenizer.entities.tokens.OperatorToken;

public @Test class VariableParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + VariableParserTest.class.getSimpleName() + " ... ");

        VariableParserTest test = new VariableParserTest();
        test.testParseEmpty();
        test.testParseSimple();
        test.testParseArray();
        test.testParseArrays();
        test.testParseArrayExpression();
        test.testParseComplex();

        System.out.println("OK");
    }

    private final @Service VariableParser parser = VariableParser.getInstance();
    private final @Service TokenValidator validator = TokenValidator.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseSimple() {
        TokenReader reader = new TokenReader(new List<>(
            new NameToken("int", 2), new NameToken("foo", 5)
        ));

        Variable variable = parser.parse(reader);

        Assert.assertEquals("foo", variable.getName().getText());
        Assert.assertEquals(true, variable.getArrays().isEmpty());
        Assert.assertEquals(false, variable.getType().isConstant());
        Assert.assertEquals("int", variable.getType().getTypename().getText());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseArray() {
        TokenReader reader = new TokenReader(new List<>(
            new NameToken("float", 1),
            new NameToken("bar", 5),
            new SquareBrackets("", 6, new List<>(
                new NumberToken("12", 7)
            ))
        ));

        Variable variable = parser.parse(reader);

        Assert.assertEquals("bar", variable.getName().getText());
        Assert.assertEquals(1, variable.getArrays().count());
        validator.assertEquals(
            new List<>(new NumberToken("12", 7)),
            variable.getArrays().getFirst().getExpression()
        );
        Assert.assertEquals(false, variable.getType().isConstant());
        Assert.assertEquals("float", variable.getType().getTypename().getText());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseArrays() {
        TokenReader reader = new TokenReader(new List<>(
            new NameToken("double", 1),
            new NameToken("foobar", 5),
            new SquareBrackets("", 6, new List<>(
                new NumberToken("9", 7)
            )),
            new SquareBrackets("", 6, new List<>(
                new NumberToken("3", 7)
            )),
            new SquareBrackets("", 6, new List<>(
                new NumberToken("1", 7)
            ))
        ));

        Variable variable = parser.parse(reader);

        Assert.assertEquals("foobar", variable.getName().getText());
        Assert.assertEquals(3, variable.getArrays().count());
        validator.assertEquals(
            new List<>(new NumberToken("9", 7)),
            variable.getArrays().get(0).getExpression()
        );
        validator.assertEquals(
            new List<>(new NumberToken("3", 7)),
            variable.getArrays().get(1).getExpression()
        );
        validator.assertEquals(
            new List<>(new NumberToken("1", 7)),
            variable.getArrays().get(2).getExpression()
        );
        Assert.assertEquals(false, variable.getType().isConstant());
        Assert.assertEquals("double", variable.getType().getTypename().getText());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseArrayExpression() {
        TokenReader reader = new TokenReader(new List<>(
            new NameToken("float", 1),
            new NameToken("bar", 5),
            new SquareBrackets("", 6, new List<>(
                new NumberToken("12", 7),
                new OperatorToken("+", 8),
                new NumberToken("1.5", 9)
            ))
        ));

        Variable variable = parser.parse(reader);

        Assert.assertEquals("bar", variable.getName().getText());
        Assert.assertEquals(1, variable.getArrays().count());
        validator.assertEquals(
            new List<>(
                new NumberToken("12", 7),
                new OperatorToken("+", 8),
                new NumberToken("1.5", 9)
            ),
            variable.getArrays().getFirst().getExpression()
        );
        Assert.assertEquals(false, variable.getType().isConstant());
        Assert.assertEquals("float", variable.getType().getTypename().getText());
        Assert.assertEquals(true, variable.getType().getPointers().isEmpty());
        reader.readEnd();
    }

    private void testParseComplex() {
        TokenReader reader = new TokenReader(new List<>(
            new NameToken("const", 0),
            new NameToken("float", 6),
            new OperatorToken("*", 11),
            new NameToken("const", 12),
            new NameToken("bar", 20),
            new SquareBrackets("", 21, new List<>(
                new NumberToken("12", 22),
                new OperatorToken("+", 24),
                new NumberToken("1.5", 25)
            ))
        ));

        Variable variable = parser.parse(reader);

        Assert.assertEquals("bar", variable.getName().getText());
        Assert.assertEquals(1, variable.getArrays().count());
        validator.assertEquals(
            new List<>(
                new NumberToken("12", 22),
                new OperatorToken("+", 24),
                new NumberToken("1.5", 25)
            ),
            variable.getArrays().getFirst().getExpression()
        );
        Assert.assertEquals(true, variable.getType().isConstant());
        Assert.assertEquals("float", variable.getType().getTypename().getText());
        Assert.assertEquals(1, variable.getType().getPointers().count());
        Assert.assertEquals(true, variable.getType().getPointers().getFirst().isConstant());
        reader.readEnd();
    }
}
