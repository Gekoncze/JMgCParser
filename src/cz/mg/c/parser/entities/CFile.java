package cz.mg.c.parser.entities;

import cz.mg.annotations.classes.Entity;
import cz.mg.annotations.requirement.Required;
import cz.mg.annotations.storage.Part;
import cz.mg.annotations.storage.Value;
import cz.mg.collections.list.List;

import java.nio.file.Path;

public @Entity class CFile {
    private Path path;
    private List<CMainEntity> entities = new List<>();

    public CFile() {
    }

    public CFile(Path path, List<CMainEntity> entities) {
        this.path = path;
        this.entities = entities;
    }

    @Required @Value
    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Required @Part
    public List<CMainEntity> getEntities() {
        return entities;
    }

    public void setEntities(List<CMainEntity> entities) {
        this.entities = entities;
    }
}
