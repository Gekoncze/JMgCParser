package cz.mg.c.parser.components;

import cz.mg.annotations.classes.Test;
import cz.mg.c.entities.types.CArrayType;
import cz.mg.c.entities.types.CBaseType;
import cz.mg.c.entities.types.CPointerType;
import cz.mg.c.entities.types.CType;
import cz.mg.test.Assert;

public @Test class CTypeChainTest {
    public static void main(String[] args) {
        System.out.print("Running " + CTypeChainTest.class.getSimpleName() + " ... ");

        CTypeChainTest test = new CTypeChainTest();
        test.testConstructor();
        test.testBaseType();
        test.testPointerType();
        test.testArrayType();

        System.out.println("OK");
    }

    private void testConstructor() {
        CArrayType arrayType = new CArrayType();
        CPointerType pointerType = new CPointerType();
        CBaseType baseType = new CBaseType();

        arrayType.setType(pointerType);
        pointerType.setType(baseType);

        CTypeChain chain = new CTypeChain(arrayType);

        Assert.assertSame(arrayType, chain.getFirst());
        Assert.assertSame(baseType, chain.getLast());
    }

    private void testBaseType() {
        CBaseType baseType = new CBaseType();
        CTypeChain chain = new CTypeChain(baseType);

        Assert.assertSame(baseType, chain.getFirst());
        Assert.assertSame(baseType, chain.getLast());

        Assert.assertThatCode(() -> {
            chain.addLast(new CBaseType());
        }).throwsException(IllegalArgumentException.class);

        Assert.assertSame(baseType, chain.getFirst());
        Assert.assertSame(baseType, chain.getLast());
    }

    private void testPointerType() {
        CPointerType pointerType = new CPointerType();
        CTypeChain chain = new CTypeChain(pointerType);

        Assert.assertSame(pointerType, chain.getFirst());
        Assert.assertSame(pointerType, chain.getLast());

        CType baseType = new CBaseType();
        chain.addLast(baseType);

        Assert.assertSame(pointerType, chain.getFirst());
        Assert.assertSame(baseType, chain.getLast());
        Assert.assertSame(baseType, pointerType.getType());

        CPointerType outerPointerType = new CPointerType();
        chain.addFirst(new CTypeChain(outerPointerType));

        Assert.assertSame(outerPointerType, chain.getFirst());
        Assert.assertSame(baseType, chain.getLast());
        Assert.assertSame(baseType, pointerType.getType());
        Assert.assertSame(pointerType, outerPointerType.getType());
    }

    private void testArrayType() {
        CArrayType arrayType = new CArrayType();
        CTypeChain chain = new CTypeChain(arrayType);

        Assert.assertSame(arrayType, chain.getFirst());
        Assert.assertSame(arrayType, chain.getLast());

        CType baseType = new CBaseType();
        chain.addLast(baseType);

        Assert.assertSame(arrayType, chain.getFirst());
        Assert.assertSame(baseType, chain.getLast());
        Assert.assertSame(baseType, arrayType.getType());

        CArrayType outerArrayType = new CArrayType();
        chain.addFirst(new CTypeChain(outerArrayType));

        Assert.assertSame(outerArrayType, chain.getFirst());
        Assert.assertSame(baseType, chain.getLast());
        Assert.assertSame(baseType, arrayType.getType());
        Assert.assertSame(arrayType, outerArrayType.getType());
    }
}