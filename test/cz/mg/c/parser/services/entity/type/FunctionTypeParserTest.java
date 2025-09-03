package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CFunction;
import cz.mg.c.entities.CModifier;
import cz.mg.c.entities.CTypename;
import cz.mg.c.entities.types.*;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.c.parser.components.TokenReader;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.test.Assertions;
import cz.mg.token.test.BracketFactory;
import cz.mg.c.parser.test.TypeUtils;
import cz.mg.collections.list.List;
import cz.mg.collections.set.Set;
import cz.mg.test.Assert;
import cz.mg.token.test.TokenFactory;

public @Test class FunctionTypeParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + FunctionTypeParserTest.class.getSimpleName() + " ... ");

        FunctionTypeParserTest test = new FunctionTypeParserTest();
        test.testParseEmpty();
        test.testParseNoInputNoOutput();
        test.testParseMultiInputMultiPointer();
        test.testParseConstAndArray();

        System.out.println("OK");
    }

    private final @Service FunctionTypeParser parser = FunctionTypeParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        Assertions.assertThatCode(() -> {
            parser.parse(new TokenReader(new List<>()), createVoid());
        }).throwsException(ParseException.class);
    }

    private void testParseNoInputNoOutput() {
        CTypeChain output = createVoid();

        List<CType> types = TypeUtils.flatten(
            parser.parse(new TokenReader(new List<>(
                b.roundBrackets(
                    f.symbol("*"),
                    f.word("fooptr")
                ),
                b.roundBrackets()
            )), output)
        );

        Assert.assertEquals(2, types.count());
        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals(false, types.get(0).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(CBaseType.class, types.get(1).getClass());
        Assert.assertEquals(CFunction.class, ((CBaseType)types.get(1)).getTypename().getClass());

        CFunction function = (CFunction) ((CBaseType)types.get(1)).getTypename();
        Assert.assertSame(output.getFirst(), function.getOutput());
        Assert.assertEquals("fooptr", function.getName());
        Assert.assertEquals(true, function.getInput().isEmpty());
    }

    private void testParseMultiInputMultiPointer() {
        CTypeChain output = createVoid();

        List<CType> types = TypeUtils.flatten(
            parser.parse(new TokenReader(new List<>(
                b.roundBrackets(
                    f.symbol("**"),
                    f.word("fooptrptr")
                ),
                b.roundBrackets(
                    f.word("int"),
                    f.symbol(","),
                    f.word("int")
                )
            )), output)
        );

        Assert.assertEquals(3, types.count());
        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CBaseType.class, types.get(2).getClass());
        Assert.assertEquals(CFunction.class, ((CBaseType)types.get(2)).getTypename().getClass());

        CFunction function = (CFunction) ((CBaseType)types.get(2)).getTypename();
        Assert.assertSame(output.getFirst(), function.getOutput());
        Assert.assertEquals("fooptrptr", function.getName());
        Assert.assertEquals(2, function.getInput().count());
    }

    private void testParseConstAndArray() {
        CTypeChain output = createVoid();

        List<CType> types = TypeUtils.flatten(
            parser.parse(new TokenReader(new List<>(
                b.roundBrackets(
                    f.symbol("*"),
                    f.word("const"),
                    f.symbol("*"),
                    f.word("fooptrptrarr"),
                    b.squareBrackets(
                        f.number("3")
                    )
                ),
                b.roundBrackets(
                    f.word("int"),
                    f.word("foo"),
                    f.symbol(","),
                    f.word("int"),
                    f.word("bar")
                )
            )), output)
        );

        Assert.assertEquals(4, types.count());
        Assert.assertEquals(CArrayType.class, types.get(0).getClass());
        Assert.assertEquals(CPointerType.class, types.get(1).getClass());
        Assert.assertEquals(CPointerType.class, types.get(2).getClass());
        Assert.assertEquals(CBaseType.class, types.get(3).getClass());
        Assert.assertEquals(1, ((CArrayType)types.get(0)).getExpression().count());
        Assert.assertEquals("3", ((CArrayType)types.get(0)).getExpression().getFirst().getText());
        Assert.assertEquals(true, types.get(1).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(false, types.get(2).getModifiers().contains(CModifier.CONST));
        Assert.assertEquals(CFunction.class, ((CBaseType)types.get(3)).getTypename().getClass());

        CFunction function = (CFunction) ((CBaseType)types.get(3)).getTypename();
        Assert.assertSame(output.getFirst(), function.getOutput());
        Assert.assertEquals("fooptrptrarr", function.getName());
        Assert.assertEquals(2, function.getInput().count());
    }

    private @Mandatory CTypeChain createVoid() {
        return new CTypeChain(new CBaseType(new CTypename("void"), new Set<>()));
    }
}