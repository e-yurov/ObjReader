package ru.cgvsu.yurov.objreader;

import ru.cgvsu.yurov.objreader.exceptions.*;

public class FaceWord {
    private Integer vertexIndex = null;
    private Integer textureVertexIndex = null;
    private Integer normalIndex = null;

    private FaceWord() {}

    public static FaceWord parse(String word, int lineIndex, boolean isSoft) {
        FaceWord faceWord = new FaceWord();

        String[] indices = word.split("/");
        if (indices.length == 0) {
            throw new ArgumentsSizeException(ArgumentsErrorType.FEW_IN_WORD, lineIndex);
        }

        String vertexIndexString = indices[0];
        try {
            faceWord.vertexIndex = Integer.parseInt(vertexIndexString) - 1;
        } catch (NumberFormatException exception) {
            throw new ParsingException("integer", lineIndex);
        }

        if (indices.length > 1) {
            String textureVertexIndexString = indices[1];
            if (!textureVertexIndexString.isEmpty()) {
                try {
                    faceWord.textureVertexIndex = Integer.parseInt(textureVertexIndexString) - 1;
                } catch (NumberFormatException exception) {
                    throw new ParsingException("integer", lineIndex);
                }
            }
        }
        if (indices.length > 2) {
            String normalIndexString = indices[2];
            if (!normalIndexString.isEmpty()) {
                try {
                    faceWord.normalIndex = Integer.parseInt(normalIndexString) - 1;
                } catch (NumberFormatException exception) {
                    throw new ParsingException("integer", lineIndex);
                }
            }
        }

        if (!isSoft && indices.length > 3) {
            throw new ArgumentsSizeException(ArgumentsErrorType.MANY_IN_WORD, lineIndex);
        }
        return faceWord;
    }

    public static FaceWord parse(String word, int lineIndex) {
        return parse(word, lineIndex, true);
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
