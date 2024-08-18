package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.types.CDataType;
import cz.mg.c.entities.types.CType;
import cz.mg.c.entities.types.CWrapperType;

/**
 * Class for unwrapping data types from chain.
 */
public @Service class TypeUnwrapper {
    private static volatile @Service TypeUnwrapper instance;

    public static @Service TypeUnwrapper getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TypeUnwrapper();
                }
            }
        }
        return instance;
    }

    private TypeUnwrapper() {
    }

    public @Mandatory CDataType unwrap(@Mandatory CType type) {
        while (type instanceof CWrapperType wrapper) {
            type = wrapper.getType();
        }

        if (type == null) {
            throw new IllegalStateException("Missing inner type for wrapper type.");
        }

        if (type instanceof CDataType dataType) {
            return dataType;
        } else {
            throw new IllegalStateException("Last wrapped inner type should be data type.");
        }
    }
}
