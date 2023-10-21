package cz.mg.c.parser.test;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.parser.entities.types.NameType;
import cz.mg.c.parser.entities.types.FunctionType;
import cz.mg.c.parser.entities.types.StructType;
import cz.mg.c.parser.entities.types.Type;
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
        if (type instanceof NameType) {
            return (NameType) type;
        } else {
            throw new AssertException(
                "Expected " + NameType.class.getSimpleName() + ", but got " + type.getClass().getSimpleName() + "."
            );
        }
    }

    public @Mandatory FunctionType functionType(@Mandatory Type type) {
        if (type instanceof FunctionType) {
            return (FunctionType) type;
        } else {
            throw new AssertException(
                "Expected " + FunctionType.class.getSimpleName() + ", but got " + type.getClass().getSimpleName() + "."
            );
        }
    }

    public @Mandatory StructType structType(@Mandatory Type type) {
        if (type instanceof StructType) {
            return (StructType) type;
        } else {
            throw new AssertException(
                "Expected " + StructType.class.getSimpleName() + ", but got " + type.getClass().getSimpleName() + "."
            );
        }
    }
}
