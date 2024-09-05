package cz.mg.c.parser.components;

import cz.mg.annotations.classes.Component;
import cz.mg.annotations.requirement.Mandatory;
import cz.mg.annotations.requirement.Optional;
import cz.mg.c.entities.types.CType;
import cz.mg.c.entities.types.CWrapperType;

public @Component class CTypeChain {
    private @Mandatory CType first;
    private @Mandatory CType last;

    public CTypeChain(@Mandatory CType type) {
        this.first = type;
        this.last = type;
        while (this.last instanceof CWrapperType wrapperType) {
            if (wrapperType.getType() != null) {
                this.last = wrapperType.getType();
            } else {
                break;
            }
        }
    }

    public @Mandatory CType getFirst() {
        return first;
    }

    public @Mandatory CType getLast() {
        return last;
    }

    public void addLast(@Mandatory CType type) {
        if (last instanceof CWrapperType wrapper) {
            if (wrapper.getType() == null) {
                wrapper.setType(type);
                last = type;
            } else {
                throw new IllegalArgumentException(
                    "Cannot append " + type.getClass().getSimpleName()
                        + " to " + last.getClass().getSimpleName()
                        + " because slot is already occupied."
                );
            }
        } else {
            throw new IllegalArgumentException(
                "Cannot append " + type.getClass().getSimpleName()
                    + " to " + last.getClass().getSimpleName() + "."
            );
        }
    }

    public void addFirst(@Optional CTypeChain chain) {
        if (chain != null) {
            if (chain.last instanceof CWrapperType wrapper) {
                if (wrapper.getType() == null) {
                    wrapper.setType(first);
                    first = chain.first;
                } else {
                    throw new IllegalArgumentException(
                        "Cannot prepend " + chain.last.getClass().getSimpleName()
                            + " to " + first.getClass().getSimpleName()
                            + " because slot is already occupied."
                    );
                }
            } else {
                throw new IllegalArgumentException(
                    "Cannot prepend " + chain.last.getClass().getSimpleName()
                        + " to " + first.getClass().getSimpleName() + "."
                );
            }
        }
    }
}
