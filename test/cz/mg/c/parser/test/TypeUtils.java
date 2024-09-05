package cz.mg.c.parser.test;

import cz.mg.annotations.classes.Static;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.types.CType;
import cz.mg.c.entities.types.CWrapperType;
import cz.mg.c.parser.components.CTypeChain;
import cz.mg.collections.list.List;

public @Static class TypeUtils {
    public static List<CType> flatten(@Mandatory CTypeChain typeChain) {
        return flatten(typeChain.getFirst());
    }

    public static List<CType> flatten(@Mandatory CType type) {
        List<CType> types = new List<>();
        CType current = type;
        while (current != null) {
            types.addLast(current);
            if (current instanceof CWrapperType wrapper) {
                current = wrapper.getType();
            } else {
                break;
            }
        }
        return types;
    }
}
