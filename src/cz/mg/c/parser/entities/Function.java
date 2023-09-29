package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Optional;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Shared;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;
import cz.mg.tokenizer.entities.Token;

public @Entity class Function extends Typename implements CMainEntity {
    private Type output;
    private List<Variable> input;
    private List<Token> implementation = new List<>();

    public Function() {
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

    @Optional @Value
    public List<Token> getImplementation() {
        return implementation;
    }

    public void setImplementation(List<Token> implementation) {
        this.implementation = implementation;
    }
}
