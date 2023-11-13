package ru.cgvsu.yurov.objreader;

import ru.cgvsu.yurov.objreader.exceptions.*;

public class FaceWord {
    private Integer vertexIndex = null;
    private Integer textureVertexIndex = null;
    private Integer normalIndex = null;

    private FaceWord() {}

    public static FaceWord parse(String word, int lineIndex) {
        FaceWord faceWord = new FaceWord();

        String[] indices = word.split("/");
        if (indices.length == 0) {
            throw new ArgumentsSizeException(ArgumentsErrorType.FEW, lineIndex);
        }

        String vertexIndexString = indices[0];
        faceWord.vertexIndex = Integer.parseInt(vertexIndexString) - 1;

        if (indices.length > 1) {
            String textureVertexIndexString = indices[1];
            if (!textureVertexIndexString.isEmpty()) {
                faceWord.textureVertexIndex = Integer.parseInt(textureVertexIndexString) - 1;
            }
        }
        if (indices.length > 2) {
            String normalIndexString = indices[2];
            if (!normalIndexString.isEmpty()) {
                faceWord.normalIndex = Integer.parseInt(normalIndexString) - 1;
            }
        }

        if (indices.length > 3) {
            throw new ArgumentsSizeException(ArgumentsErrorType.MANY, lineIndex);
        }
        return faceWord;
    }

    public WordType getWordType() {
        if (vertexIndex == null) {
            return null;
        }

        if (textureVertexIndex != null) {
            if (normalIndex != null) {
                return WordType.VERTEX_TEXTURE_NORMAL;
            }
            return WordType.VERTEX_TEXTURE;
        }

        if (normalIndex != null) {
            return WordType.VERTEX_NORMAL;
        }
        return WordType.VERTEX;
    }

    public void checkIndices(int verticesSize, int textureVerticesSize, int normalsSize,
                             int lineIndex, int wordIndex) {
        if (vertexIndex >= verticesSize) {
            throw new FaceWordIndexException("vertex", lineIndex, wordIndex);
        }
        if (textureVertexIndex != null && textureVertexIndex >= textureVerticesSize) {
            throw new FaceWordIndexException("texture vertex", lineIndex, wordIndex);
        }
        if (normalIndex != null && normalIndex >= normalsSize) {
            throw new FaceWordIndexException("normal", lineIndex, wordIndex);
        }
    }

    public Integer getVertexIndex() {
        return vertexIndex;
    }

    public Integer getTextureVertexIndex() {
        return textureVertexIndex;
    }

    public Integer getNormalIndex() {
        return normalIndex;
    }
}
