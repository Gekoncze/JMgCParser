package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.storage.Part;
import cz.mg.collections.list.List;

public @Entity class CEnum extends CTypename {
    private List<CEnumEntry> entries;

    public CEnum() {
    }

    public CEnum(String name, List<CEnumEntry> entries) {
        super(name);
        this.entries = entries;
    }

    @Optional @Part
    public List<CEnumEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<CEnumEntry> entries) {
        this.entries = entries;
    }
}
