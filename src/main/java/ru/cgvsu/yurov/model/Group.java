package ru.cgvsu.yurov.model;

import java.util.ArrayList;
import java.util.List;

public class Group {
    private final String name;
    private final List<Polygon> polygons = new ArrayList<>();

    public Group(String name) {
        this.name = name;
    }

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    public String getName() {
        return name;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }

    public int getPolygonsSize() {
        return polygons.size();
    }
}
