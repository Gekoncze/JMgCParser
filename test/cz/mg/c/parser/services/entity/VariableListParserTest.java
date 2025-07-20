package cz.mg.c.parser.services.entity;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.CVariable;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.parser.exceptions.ParseException;
import cz.mg.token.test.BracketFactory;
import cz.mg.c.parser.test.TypeUtils;
import cz.mg.collections.list.List;
import cz.mg.test.Assert;
import cz.mg.token.test.TokenFactory;

public @Test class VariableListParserTest {
    public static void main(String[] args) {
        System.out.print("Running " + VariableListParserTest.class.getSimpleName() + " ... ");

        VariableListParserTest test = new VariableListParserTest();
        test.testParseEmpty();
        test.testParseIllegalList();
        test.testParseAnonymous();
        test.testParseSingle();
        test.testParseMultiple();

        System.out.println("OK");
    }

    private final @Service VariableListParser parser = VariableListParser.getInstance();
    private final @Service BracketFactory b = BracketFactory.getInstance();
    private final @Service TokenFactory f = TokenFactory.getInstance();

    private void testParseEmpty() {
        List<CVariable> variables = parser.parse(b.roundBrackets());
        Assert.assertEquals(true, variables.isEmpty());
    }

    private void testParseIllegalList() {
        Assert.assertThatCode(() -> {
            parser.parse(b.roundBrackets(f.symbol(",")));
        }).throwsException(ParseException.class);
    }

    private void testParseAnonymous() {
        List<CVariable> variables = parser.parse(b.roundBrackets(
            f.word("int")
        ));

        Assert.assertEquals(1, variables.count());
        Assert.assertEquals("int", ((CBaseType)variables.getFirst().getType()).getTypename().getName());
        Assert.assertNull(variables.getFirst().getName());
    }

    private void testParseSingle() {
        List<CVariable> variables = parser.parse(b.roundBrackets(
            f.word("int"),
            f.word("a")
        ));

        Assert.assertEquals(1, variables.count());
        Assert.assertEquals("int", ((CBaseType)variables.getFirst().getType()).getTypename().getName());
        Assert.assertEquals("a", variables.getFirst().getName());
    }

    private void testParseMultiple() {
        List<CVariable> variables = parser.parse(b.roundBrackets(
            f.word("int"),
            f.symbol("*"),
            f.word("a"),
            f.symbol(","),
            f.word("float"),
            f.word("b")
        ));

        Assert.assertEquals(2, variables.count());
        Assert.assertEquals("a", variables.getFirst().getName());
        Assert.assertEquals("b", variables.getLast().getName());

        List<CType> types = TypeUtils.flatten(variables.getFirst().getType());
        Assert.assertEquals(CPointerType.class, types.get(0).getClass());
        Assert.assertEquals("int", ((CBaseType)types.get(1)).getTypename().getName());
        Assert.assertEquals("float", ((CBaseType)variables.getLast().getType()).getTypename().getName());
    }
}
