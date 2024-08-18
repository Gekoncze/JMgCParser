package cz.mg.c.parser.services.entity.type;

import cz.mg.annotations.classes.Service;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.types.*;
import cz.mg.collections.list.List;
import cz.mg.collections.list.ListItem;
import cz.mg.collections.pair.Pair;

/**
 * Class for connecting types into chain.
 */
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

    /**
     * Connects given types together.
     * Types are connected in parameter order, where earlier types are outer types and later types are inner types.
     * Null types are skipped.
     * @param types connected wrapper types
     * @param dataType data type
     */
    public CType connect(
        @Optional Pair<? extends CWrapperType, ? extends CWrapperType> types,
        @Mandatory CDataType dataType
    ) {
        if (types != null) {
            types.getValue().setType(dataType);
            return types.getKey();
        } else {
            return dataType;
        }
    }

    /**
     * Connects given types together.
     * Types are connected in parameter order, where earlier types are outer types and later types are inner types.
     * Null types are skipped.
     * @param firstTypes connected wrapper types
     * @param secondTypes connected wrapper types
     * @param dataType data type
     * @return resulting outer type
     */
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

    /**
     * Connects given types together.
     * @param types list of wrapper types to connect
     * @return pair where key is first type in chain and value is last type in chain
     */
    public <W extends CWrapperType> @Optional Pair<W, W> connect(List<W> types) {
        if (types.isEmpty()) {
            return null;
        }

        if (types.count() == 1) {
            return new Pair<>(types.getFirst(), types.getFirst());
        }

        for (
            ListItem<W> item = types.getFirstItem().getNextItem();
            item != null;
            item = item.getNextItem()
        ) {
            W previous = item.getPreviousItem().get();
            W current = item.get();
            previous.setType(current);
        }

        return new Pair<>(types.getFirst(), types.getLast());
    }
}
