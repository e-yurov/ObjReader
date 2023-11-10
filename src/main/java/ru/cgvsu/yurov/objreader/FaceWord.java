package ru.cgvsu.yurov.objreader;

import ru.cgvsu.yurov.objreader.exceptions.ObjReaderException;
import ru.cgvsu.yurov.objreader.exceptions.ParsingException;

public class FaceWord {
    private int commentIndex = -1;

    private Integer vertexIndex;
    private Integer textureVertexIndex;
    private Integer normalIndex;

    public static FaceWord parse(String word, int lineIndex) {
        FaceWord faceWord = new FaceWord();

        String[] indices = word.split("/");
        switch (indices.length) {
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
        }

        return faceWord;
    }
}
