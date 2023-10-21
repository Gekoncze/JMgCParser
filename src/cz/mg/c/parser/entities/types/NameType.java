package cz.mg.c.parser.entities.types;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Link;
import cz.mg.annotations.storage.Value;
import cz.mg.tokenizer.entities.tokens.NameToken;

public @Entity class NameType extends Type {
    private NameToken typename;
    private boolean constant;

    @Required @Link
    public NameToken getTypename() {
        return typename;
    }

    public void setTypename(NameToken typename) {
        this.typename = typename;
    }

    @Required @Value
    public boolean isConstant() {
        return constant;
    }

    public void setConstant(boolean constant) {
        this.constant = constant;
    }
}
