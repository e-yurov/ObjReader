package ru.cgvsu.yurov.objreader;

import ru.cgvsu.yurov.objreader.exceptions.ObjReaderException;
import ru.cgvsu.yurov.objreader.exceptions.ParsingException;

public class FaceWord {
    private int commentIndex = Integer.MAX_VALUE;

    private Integer vertexIndex = null;
    private Integer textureVertexIndex = null;
    private Integer normalIndex = null;

    private static final String COMMENT_TOKEN = "#";

    private FaceWord() {}

    public static FaceWord parse(String word, int lineIndex) {
        FaceWord faceWord = new FaceWord();

        String[] indices = word.split("/");
        if (indices.length == 0) {
            throw new ParsingException(lineIndex);
        }

        if (indices.length > 0) {
            String vertexIndexString = indices[0];
            /*String toParse = vertexIndexString.contains(COMMENT_TOKEN) ?
                    vertexIndexString.split(COMMENT_TOKEN)[0] :
                    vertexIndexString;*/
            //String toParse = faceWord.trimComment(vertexIndexString, 0);
            /*if () {
                faceWord.vertexIndex = Integer.parseInt();
                //String vertexIndexStringSplitted = vertexIndexString.split(COMMENT_TOKEN)[0];
            } else {
                faceWord.vertexIndex = Integer.parseInt()
            }*/
            faceWord.vertexIndex = Integer.parseInt(vertexIndexString) - 1;
        }
        if (indices.length > 1) {
            String textureVertexIndexString = indices[1];
            //String toParse = faceWord.trimComment(textureVertexIndexString, 1);

            /*if (toParse.isEmpty() || faceWord.commentIndex < 1) {
                faceWord.textureVertexIndex = null;
            } else {
                faceWord.textureVertexIndex = Integer.parseInt(toParse);
            }*/

            if (!textureVertexIndexString.isEmpty()) {
                faceWord.textureVertexIndex = Integer.parseInt(textureVertexIndexString) - 1;
            }
            //faceWord.textureVertexIndex = toParse.isEmpty() ? null : Integer.parseInt(toParse);
        }
        if (indices.length > 2) {
            String normalIndexString = indices[2];
            //String toParse = faceWord.trimComment(normalIndexString, 2);

            /*if (toParse.isEmpty() || faceWord.commentIndex < 2) {
                faceWord.normalIndex = null;
            } else {
                faceWord.normalIndex = Integer.parseInt(toParse);
            }*/

            if (!normalIndexString.isEmpty()) {
                faceWord.normalIndex = Integer.parseInt(normalIndexString) - 1;
            }
        }

        /*if (indices.length > 3) {
            if (indices[3].startsWith(COMMENT_TOKEN)) {
                faceWord.commentIndex = Math.min(faceWord.commentIndex, 3);
            } else {
                throw new ParsingException(lineIndex);
            }
        }*/
        /*switch (indices.length) {
            case 1 -> {
                String vertexIndexString = indices[0];
                if (vertexIndexString.isEmpty()) {
                    throw new ParsingException(lineIndex);
                }
                if (vertexIndexString.charAt(0) == '#') {
                    faceWord.commentIndex = 0;
                }
            }
            case 2 -> {
                String vertexIndexString = indices[0];
                String textureVertexIndexString = indices[0];

            }
        }*/

        return faceWord;
    }

    private String trimComment(String toTrim, int argumentIndex) {
        String result;
        if (toTrim.contains(COMMENT_TOKEN)) {
            commentIndex = Math.min(commentIndex, argumentIndex);
            result = toTrim.split(COMMENT_TOKEN)[0];
        } else {
            result = toTrim;
        }

        return result;
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
        return WordType.VERTEX_TEXTURE;
    }

    public void checkIndices(int verticesSize, int textureVerticesSize, int normalsSize, int lineIndex) {
        if (vertexIndex >= verticesSize) {
            throw new ObjReaderException("Vertex index ot of bounds", lineIndex);
        }
        if (textureVertexIndex != null && textureVertexIndex >= textureVerticesSize) {
            throw new ObjReaderException("Texture vertex index ot of bounds", lineIndex);
        }
        if (normalIndex != null && normalIndex >= normalsSize) {
            throw new ObjReaderException("Normal index ot of bounds", lineIndex);
        }
    }

    public int getCommentIndex() {
        return commentIndex;
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
