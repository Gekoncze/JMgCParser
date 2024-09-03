package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.types.CArrayType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.collections.pair.Pair;
import cz.mg.test.Assert;
import cz.mg.test.exceptions.AssertException;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class ArrayTypeParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + ArrayTypeParserTest.class.getSimpleName() + " ... ");

        ArrayTypeParserTest test = new ArrayTypeParserTest();
        test.testParseEmpty();
        test.testParseSingle();
        test.testParseMultiple();
        test.testParseRemainingTokens();

        System.out.println("OK");
    }

    private final @Service ArrayTypeParser parser = ArrayTypeParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertNull(parser.parse(new TokenReader(new List<>())));
        Assert.assertNull(parser.parse(new TokenReader(new List<>(f.word("foo")))));
    }

    private void testParseSingle() {
        List<Token> tokens = new List<>(b.squareBrackets(f.number("5")));
        Pair<CArrayType, CArrayType> arrays = parser.parse(new TokenReader(tokens));
        Assert.assertNotNull(arrays);
        Assert.assertSame(arrays.getKey(), arrays.getValue());
        Assert.assertEquals(1, arrays.getKey().getExpression().count());
        Assert.assertEquals("5", arrays.getKey().getExpression().getFirst().getText());
    }

    private void testParseMultiple() {
        List<Token> tokens = new List<>(
            b.squareBrackets(),
            b.squareBrackets(f.number("7")),
            b.squareBrackets(
                f.number("1"),
                f.symbol("+"),
                f.number("3")
            )
        );
        List<CArrayType> arrays = flatten(parser.parse(new TokenReader(tokens)));
        Assert.assertEquals(3, arrays.count());
        Assert.assertEquals(0, arrays.get(0).getExpression().count());
        Assert.assertEquals(1, arrays.get(1).getExpression().count());
        Assert.assertEquals(3, arrays.get(2).getExpression().count());
    }

    private void testParseRemainingTokens() {
        List<Token> tokens = new List<>(b.squareBrackets(), f.word("foo"));
        TokenReader reader = new TokenReader(tokens);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has());
        Assert.assertEquals("foo", reader.read().getText());
    }

    private List<CArrayType> flatten(@Optional Pair<CArrayType, CArrayType> pointers) {
        List<CArrayType> pointerList = new List<>();
        CType current = pointers == null ? null : pointers.getKey();
        while (current != null) {
            if (current instanceof CArrayType array) {
                pointerList.addLast(array);
                current = array.getType();
            } else {
                throw new AssertException(
                    "Expected instance of type " + CArrayType.class.getSimpleName()
                        + ", but got " + current.getClass().getSimpleName() + "."
                );
            }
        }
        return pointerList;
    }
}