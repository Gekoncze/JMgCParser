package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Shared;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Entity class CFunction extends CTypename {
    private CType output;
    private List<CVariable> input;
    private List<Token> implementation;

    public CFunction() {
    }

    public CFunction(@Optional String name, CType output, List<CVariable> input, List<Token> implementation) {
        super(name);
        this.output = output;
        this.input = input;
        this.implementation = implementation;
    }

    @Required @Shared
    public CType getOutput() {
        return output;
    }

    public void setOutput(CType output) {
        this.output = output;
    }

    @Required @Part
    public List<CVariable> getInput() {
        return input;
    }

    public void setInput(List<CVariable> input) {
        this.input = input;
    }

    @Optional @Shared
    public List<Token> getImplementation() {
        return implementation;
    }

    public void setImplementation(List<Token> implementation) {
        this.implementation = implementation;
    }
}
