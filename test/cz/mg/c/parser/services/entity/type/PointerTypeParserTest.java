package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.CModifier;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.collections.list.List;
import cz.mg.collections.pair.Pair;
import cz.mg.test.Assert;
import cz.mg.test.exceptions.AssertException;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;

public @Test class PointerTypeParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + PointerTypeParserTest.class.getSimpleName() + " ... ");

        PointerTypeParserTest test = new PointerTypeParserTest();
        test.testParseEmpty();
        test.testParseSingle();
        test.testParseSingleConst();
        test.testParseSingleGroup();
        test.testParseSingleGroupConst();
        test.testParseMultiple();
        test.testParseMultipleGroup();
        test.testMixed();
        test.testParseRemainingTokens();

        System.out.println("OK");
    }

    private final @Service PointerTypeParser parser = PointerTypeParser.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertNull(parser.parse(new TokenReader(new List<>())));
        Assert.assertNull(parser.parse(new TokenReader(new List<>(f.symbol("+")))));
        Assert.assertNull(parser.parse(new TokenReader(new List<>(f.doubleQuote("*")))));
        Assert.assertNull(parser.parse(new TokenReader(new List<>(f.word("const")))));
    }

    private void testParseSingle() {
        List<Token> tokens = new List<>(f.symbol("*"));
        Pair<CPointerType, CPointerType> pointers = parser.parse(new TokenReader(tokens));
        Assert.assertNotNull(pointers);
        Assert.assertSame(pointers.getKey(), pointers.getValue());
        Assert.assertNull(pointers.getKey().getType());
        Assert.assertEquals(true, pointers.getKey().getModifiers().isEmpty());
    }

    private void testParseSingleConst() {
        List<Token> tokens = new List<>(f.symbol("*"), f.word("const"));
        Pair<CPointerType, CPointerType> pointers = parser.parse(new TokenReader(tokens));
        Assert.assertNotNull(pointers);
        Assert.assertSame(pointers.getKey(), pointers.getValue());
        Assert.assertEquals(true, pointers.getKey().getModifiers().contains(CModifier.CONST));
    }

    private void testParseSingleGroup() {
        List<Token> tokens = new List<>(f.symbol("***"));
        List<CPointerType> pointers = flatten(parser.parse(new TokenReader(tokens)));
        Assert.assertEquals(3, pointers.count());
        Assert.assertEquals(false, pointers.get(0).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, pointers.get(1).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, pointers.get(2).getModifiers().contains(CModifier.CONST));
    }

    private void testParseSingleGroupConst() {
        List<Token> tokens = new List<>(f.symbol("***"), f.word("const"));
        List<CPointerType> pointers = flatten(parser.parse(new TokenReader(tokens)));
        Assert.assertEquals(3, pointers.count());
        Assert.assertEquals(false, pointers.get(0).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, pointers.get(1).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, pointers.get(2).getModifiers().contains(CModifier.CONST));
    }

    private void testParseMultiple() {
        List<Token> tokens = new List<>(
            f.symbol("*"),
            f.symbol("*"),
            f.word("const"),
            f.symbol("*")
        );
        List<CPointerType> pointers = flatten(parser.parse(new TokenReader(tokens)));
        Assert.assertEquals(3, pointers.count());
        Assert.assertEquals(false, pointers.get(0).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, pointers.get(1).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, pointers.get(2).getModifiers().contains(CModifier.CONST));
    }

    private void testParseMultipleGroup() {
        List<Token> tokens = new List<>(
            f.symbol("***"),
            f.word("const"),
            f.symbol("**")
        );
        List<CPointerType> pointers = flatten(parser.parse(new TokenReader(tokens)));
        Assert.assertEquals(5, pointers.count());
        Assert.assertEquals(false, pointers.get(0).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, pointers.get(1).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(true, pointers.get(2).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, pointers.get(3).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, pointers.get(4).getModifiers().contains(CModifier.CONST));
    }

    private void testMixed() {
        List<Token> tokens = new List<>(
            f.symbol("*"),
            f.word("const"),
            f.word("const"),
            f.symbol("***"),
            f.symbol("*")
        );
        List<CPointerType> pointers = flatten(parser.parse(new TokenReader(tokens)));
        Assert.assertEquals(5, pointers.count());
        Assert.assertEquals(true, pointers.get(0).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, pointers.get(1).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, pointers.get(2).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, pointers.get(3).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, pointers.get(4).getModifiers().contains(CModifier.CONST));
    }

    private void testParseRemainingTokens() {
        List<Token> tokens = new List<>(f.symbol("*"), f.word("foo"));
        TokenReader reader = new TokenReader(tokens);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has());
    }

    private List<CPointerType> flatten(@Optional Pair<CPointerType, CPointerType> pointers) {
        List<CPointerType> pointerList = new List<>();
        CType current = pointers == null ? null : pointers.getKey();
        while (current != null) {
            if (current instanceof CPointerType pointer) {
                pointerList.addLast(pointer);
                current = pointer.getType();
            } else {
                throw new AssertException(
                    "Expected instance of type " + CPointerType.class.getSimpleName()
                        + ", but got " + current.getClass().getSimpleName() + "."
                );
            }
        }
        return pointerList;
    }
}