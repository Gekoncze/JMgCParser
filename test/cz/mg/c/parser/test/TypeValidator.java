package cz.mg.c.parser.test;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.types.*;
import cz.mg.test.exceptions.AssertException;

public @Service class TypeValidator {
    private static volatile @Service TypeValidator instance;

    public static @Service TypeValidator getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TypeValidator();
                }
            }
        }
        return instance;
    }

    private TypeValidator() {
    }

    public @Mandatory NameType nameType(@Mandatory Type type) {
        return type(type, NameType.class);
    }

    public @Mandatory FunctionType functionType(@Mandatory Type type) {
        return type(type, FunctionType.class);
    }

    public @Mandatory StructType structType(@Mandatory Type type) {
        return type(type, StructType.class);
    }

    public @Mandatory UnionType unionType(@Mandatory Type type) {
        return type(type, UnionType.class);
    }

    private <T extends Type> @Mandatory T type(@Mandatory Type type, Class<T> typeClass) {
        if (typeClass.isInstance(type)) {
            return (T) type;
        } else {
            throw new AssertException(
                "Expected " + typeClass.getSimpleName() + ", but got " + type.getClass().getSimpleName() + "."
            );
        }
    }
}
