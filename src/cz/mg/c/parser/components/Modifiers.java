package cz.mg.c.parser.components;

import cz.mg.annotations.requirement.Mandatory;
import cz.mg.c.entities.CTypeModifiers;

public class Modifiers {
    public static @Mandatory CTypeModifiers or(@Mandatory CTypeModifiers a, @Mandatory CTypeModifiers b) {
        return new CTypeModifiers(
            a.isConstant() || b.isConstant(),
            a.isStatic() || b.isStatic()
        );
    }
}