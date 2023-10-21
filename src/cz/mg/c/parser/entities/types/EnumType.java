package cz.mg.c.parser.entities.types;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.storage.Part;
import cz.mg.c.parser.entities.EnumEntry;
import cz.mg.collections.list.List;

public @Entity class EnumType extends Type {
    private List<EnumEntry> entries;

    public EnumType() {
    }

    @Optional @Part
    public List<EnumEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<EnumEntry> entries) {
        this.entries = entries;
    }
}
