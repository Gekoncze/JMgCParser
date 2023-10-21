package cz.mg.c.parser.entities.types;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Shared;
import cz.mg.c.parser.entities.Variable;
import cz.mg.collections.list.List;

public @Entity class FunctionType extends Type {
    private Type output;
    private List<Variable> input;

    public FunctionType() {
    }

    @Required @Shared
    public Type getOutput() {
        return output;
    }

    public void setOutput(Type output) {
        this.output = output;
    }

    @Required @Part
    public List<Variable> getInput() {
        return input;
    }

    public void setInput(List<Variable> input) {
        this.input = input;
    }
}
