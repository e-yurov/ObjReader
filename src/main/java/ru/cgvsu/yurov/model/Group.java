package ru.cgvsu.yurov.model;

public class Group extends Model {
    private final String name;

    public Group(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
