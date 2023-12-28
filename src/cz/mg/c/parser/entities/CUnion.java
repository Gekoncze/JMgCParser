package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.storage.Part;
import cz.mg.collections.list.List;

public @Entity class CUnion extends CTypename {
    private List<CVariable> variables;

    public CUnion() {
    }

    public CUnion(@Optional String name, List<CVariable> variables) {
        super(name);
        this.variables = variables;
    }

    @Optional @Part
    public List<CVariable> getVariables() {
        return variables;
    }

    public void setVariables(List<CVariable> variables) {
        this.variables = variables;
    }
}
