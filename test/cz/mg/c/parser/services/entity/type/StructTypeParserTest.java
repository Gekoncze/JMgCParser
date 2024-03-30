package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CStruct;
import cz.mg.c.entities.CType;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.c.parser.test.BracketFactory;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.Token;
import cz.mg.tokenizer.test.TokenFactory;

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
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        Assert.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()));
        }).throwsException(ParseException.class);
    }

    private void testParseNoFields() {
        List<Token> tokens = new List<>(
            f.word("struct"),
            f.word("Foo"),
            b.curlyBrackets()
        );
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.getModifiers().isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof CStruct);
        CStruct struct = (CStruct) type.getTypename();
        Assert.assertEquals("Foo", struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(true, struct.getVariables().isEmpty());
    }

    private void testParseAnonymous() {
        List<Token> tokens = new List<>(
            f.word("struct"),
            b.curlyBrackets(
                f.word("int"),
                f.word("bar"),
                f.separator(";")
            )
        );
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.getModifiers().isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof CStruct);
        CStruct struct = (CStruct) type.getTypename();
        Assert.assertNull(struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(1, struct.getVariables().count());
        Assert.assertEquals("int", struct.getVariables().get(0).getType().getTypename().getName());
        Assert.assertEquals("bar", struct.getVariables().get(0).getName());
    }

    private void testParseNamed() {
        List<Token> tokens = new List<>(
            f.word("struct"),
            f.word("Foo"),
            b.curlyBrackets(
                f.word("int"),
                f.word("bar"),
                f.separator(";")
            )
        );
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(false, type.getModifiers().isConstant());
        Assert.assertEquals(true, type.getPointers().isEmpty());
        Assert.assertEquals(true, type.getTypename() instanceof CStruct);
        CStruct struct = (CStruct) type.getTypename();
        Assert.assertEquals("Foo", struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(1, struct.getVariables().count());
        Assert.assertEquals("int", struct.getVariables().get(0).getType().getTypename().getName());
        Assert.assertEquals("bar", struct.getVariables().get(0).getName());
    }

    private void testParseComplexConst() {
        List<Token> tokens = new List<>(
            f.word("const"),
            f.word("struct"),
            f.word("FooBar"),
            b.curlyBrackets(
                f.word("const"),
                f.word("int"),
                f.word("a"),
                f.separator(";"),
                f.word("int"),
                f.word("b"),
                f.separator(";")
            ),
            f.word("const"),
            f.operator("*"),
            f.word("const")
        );
        CType type = parser.parse(new TokenReader(tokens));
        Assert.assertEquals(true, type.getModifiers().isConstant());
        Assert.assertEquals(1, type.getPointers().count());
        Assert.assertEquals(true, type.getPointers().getFirst().isConstant());
        Assert.assertEquals(true, type.getTypename() instanceof CStruct);
        CStruct struct = (CStruct) type.getTypename();
        Assert.assertEquals("FooBar", struct.getName());
        Assert.assertNotNull(struct.getVariables());
        Assert.assertEquals(2, struct.getVariables().count());
        Assert.assertEquals(true, struct.getVariables().get(0).getType().getModifiers().isConstant());
        Assert.assertEquals("int", struct.getVariables().get(0).getType().getTypename().getName());
        Assert.assertEquals("a", struct.getVariables().get(0).getName());
        Assert.assertEquals(false, struct.getVariables().get(1).getType().getModifiers().isConstant());
        Assert.assertEquals("int", struct.getVariables().get(1).getType().getTypename().getName());
        Assert.assertEquals("b", struct.getVariables().get(1).getName());
    }

    private void testParseRemainingTokens() {
        List<Token> tokens = new List<>(
            f.word("struct"),
            f.word("Foo"),
            b.curlyBrackets(),
            f.word("Foo2")
        );
        TokenReader reader = new TokenReader(tokens);
        parser.parse(reader);
        Assert.assertEquals(true, reader.has());
    }
}