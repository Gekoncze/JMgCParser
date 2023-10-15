package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.storage.Part;
import cz.mg.collections.list.List;

public @Entity class Enum extends Typename implements CMainEntity {
    private List<EnumEntry> entries;

    public Enum() {
    }

    @Optional @Part
    public List<EnumEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<EnumEntry> entries) {
        this.entries = entries;
    }
}
