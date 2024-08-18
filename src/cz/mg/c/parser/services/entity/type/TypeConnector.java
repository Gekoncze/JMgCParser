package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.types.*;
import cz.mg.collections.pair.Pair;

public @Service class TypeConnector {
    private static volatile @Service TypeConnector instance;

    public static @Service TypeConnector getInstance() {
        if (instance == null) {
            synchronized (Service.class) {
                if (instance == null) {
                    instance = new TypeConnector();
                }
            }
        }
        return instance;
    }

    private TypeConnector() {
    }

    public CType connect(
        @Optional Pair<? extends CWrapperType, ? extends CWrapperType> pair,
        @Mandatory CDataType type
    ) {
        if (pair != null) {
            pair.getValue().setType(type);
            return pair.getKey();
        } else {
            return type;
        }
    }

    public CType connect(
        @Optional Pair<? extends CWrapperType, ? extends CWrapperType> firstTypes,
        @Optional Pair<? extends CWrapperType, ? extends CWrapperType> secondTypes,
        @Mandatory CDataType dataType
    ) {
        if (firstTypes != null && secondTypes != null) {
            firstTypes.getValue().setType(secondTypes.getKey());
            secondTypes.getValue().setType(dataType);
            return firstTypes.getKey();
        } else if (firstTypes != null) {
            firstTypes.getValue().setType(dataType);
            return firstTypes.getKey();
        } else if (secondTypes != null) {
            secondTypes.getValue().setType(dataType);
            return secondTypes.getKey();
        } else {
            return dataType;
        }
    }
}
