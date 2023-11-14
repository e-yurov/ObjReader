package ru.cgvsu.yurov.model;

import ru.cgvsu.yurov.math.Vector2f;
import ru.cgvsu.yurov.math.Vector3f;

import java.util.ArrayList;
import java.util.List;

public class Model {

    private List<Vector3f> vertices = new ArrayList<>();
    private List<Vector2f> textureVertices = new ArrayList<>();
    private List<Vector3f> normals = new ArrayList<>();
    private List<Polygon> polygons = new ArrayList<>();

    public void addVertex(Vector3f vertex) {
        vertices.add(vertex);
    }

    public void addTextureVertex(Vector2f textureVertex) {
        textureVertices.add(textureVertex);
    }

    public void addNormal(Vector3f normal) {
        normals.add(normal);
    }

    public void addPolygon(Polygon polygon) {
        polygons.add(polygon);
    }

    public Polygon getFirstPolygon() {
        return polygons.get(0);
    }


    public int getVerticesSize() {
        return vertices.size();
    }

    public int getTextureVerticesSize() {
        return textureVertices.size();
    }

    public int getNormalsSize() {
        return normals.size();
    }

    public int getPolygonsSize() {
        return polygons.size();
    }

    public List<Vector3f> getVertices() {
        return vertices;
    }

    public List<Vector2f> getTextureVertices() {
        return textureVertices;
    }

    public List<Vector3f> getNormals() {
        return normals;
    }

    public List<Polygon> getPolygons() {
        return polygons;
    }
}
