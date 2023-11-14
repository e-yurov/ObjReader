package ru.cgvsu.yurov.model;

import ru.cgvsu.yurov.objreader.exceptions.FaceWordIndexException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Polygon {
    private List<Integer> vertexIndices;
    private List<Integer> textureVertexIndices;
    private List<Integer> normalIndices;
    private int lineIndex;

    public Polygon() {
        vertexIndices = new ArrayList<>();
        textureVertexIndices = new ArrayList<>();
        normalIndices = new ArrayList<>();
    }

    public boolean hasTexture() {
        return !textureVertexIndices.isEmpty();
    }

    public void checkIndices(int verticesSize, int textureVerticesSize, int normalsSize) {
        for (int i = 0; i < vertexIndices.size(); i++) {
            int vertexIndex = vertexIndices.get(i);
            if (vertexIndex >= verticesSize || vertexIndex < 0) {
                throw new FaceWordIndexException("vertex", lineIndex, i + 1);
            }
        }

        for (int i = 0; i < textureVertexIndices.size(); i++) {
            int textureVertexIndex = textureVertexIndices.get(i);
            if (textureVertexIndex >= textureVerticesSize || textureVertexIndex < 0) {
                throw new FaceWordIndexException("texture vertex", lineIndex, i + 1);
            }
        }

        for (int i = 0; i < normalIndices.size(); i++) {
            int normalIndex = normalIndices.get(i);
            if (normalIndex >= normalsSize || normalIndex < 0) {
                throw new FaceWordIndexException("normal", lineIndex, i + 1);
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Polygon polygon = (Polygon) o;
        return Objects.equals(vertexIndices, polygon.vertexIndices) &&
                Objects.equals(textureVertexIndices, polygon.textureVertexIndices) &&
                Objects.equals(normalIndices, polygon.normalIndices);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vertexIndices, textureVertexIndices, normalIndices);
    }

    public List<Integer> getVertexIndices() {
        return vertexIndices;
    }

    public void setVertexIndices(List<Integer> vertexIndices) {
        this.vertexIndices = vertexIndices;
    }

    public List<Integer> getTextureVertexIndices() {
        return textureVertexIndices;
    }

    public void setTextureVertexIndices(List<Integer> textureVertexIndices) {
        this.textureVertexIndices = textureVertexIndices;
    }

    public List<Integer> getNormalIndices() {
        return normalIndices;
    }

    public void setNormalIndices(List<Integer> normalIndices) {
        this.normalIndices = normalIndices;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public void setLineIndex(int lineIndex) {
        this.lineIndex = lineIndex;
    }
}
