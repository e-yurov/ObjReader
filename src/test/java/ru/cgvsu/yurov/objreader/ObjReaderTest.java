package ru.cgvsu.yurov.objreader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.cgvsu.yurov.math.Vector2f;
import ru.cgvsu.yurov.math.Vector3f;
import ru.cgvsu.yurov.model.Model;
import ru.cgvsu.yurov.objreader.exceptions.ArgumentsSizeException;
import ru.cgvsu.yurov.objreader.exceptions.FaceWordTypeException;

public class ObjReaderTest {
    @Test
    void testTooFewVector3fArguments() {
        ObjReader objReader = new ObjReader();
        try {
            objReader.parseVector3f(new String[]{"3", "2"});
            Assertions.fail();
        } catch (ArgumentsSizeException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 0. Too few arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testTooManyVector3fArguments() {
        ObjReader objReader = new ObjReader();
        objReader.isSoft = false;
        try {
            objReader.parseVector3f(new String[]{"3", "2", "1", "0"});
            Assertions.fail();
        } catch (ArgumentsSizeException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 0. Too many arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testTooManyVector3fArgumentsSoft() {
        ObjReader objReader = new ObjReader();
        Vector3f actual = objReader.parseVector3f(new String[]{"3", "2", "1", "0"});
        Vector3f expected = new Vector3f(3, 2, 1);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testVector3f() {
        ObjReader objReader = new ObjReader();
        Vector3f expected = new Vector3f(2.5F, 8, 0);
        Vector3f actual = objReader.parseVector3f(new String[]{"2,5", "8", "0"});

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testTooFewVector2fArguments() {
        ObjReader objReader = new ObjReader();
        try {
            objReader.parseVector2f(new String[]{"3"});
            Assertions.fail();
        } catch (ArgumentsSizeException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 0. Too few arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testTooManyVector2fArguments() {
        ObjReader objReader = new ObjReader();
        objReader.isSoft = false;
        try {
            objReader.parseVector2f(new String[]{"3", "2", "1"});
            Assertions.fail();
        } catch (ArgumentsSizeException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 0. Too many arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testTooManyVector2fArgumentsSoft() {
        ObjReader objReader = new ObjReader();
        Vector2f actual = objReader.parseVector2f(new String[]{"3", "2", "1"});
        Vector2f expected = new Vector2f(3, 2);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testVector2f() {
        ObjReader objReader = new ObjReader();
        Vector2f expected = new Vector2f(2.5F, 8);
        Vector2f actual = objReader.parseVector2f(new String[]{"2,5", "8"});

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testParseFaceArgumentsSizeException() {
        ObjReader objReader = new ObjReader();
        try {
            objReader.parseFace(new String[]{"1/1", "2/2"});
            Assertions.fail();
        } catch (ArgumentsSizeException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 0. Too few face arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testParseFaceWordTypeException() {
        ObjReader objReader = new ObjReader();
        try {
            objReader.parseFace(new String[]{"1", "2/2", "3/3/3"});
            Assertions.fail();
        } catch (FaceWordTypeException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 0. Several argument types in one polygon.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }


    @Test
    void testParseVertex() {
        Model model = ObjReader.read("v 0.5 0 1.1");
        Vector3f actualVector = model.getVertices().get(0);

        Vector3f expectedVector = new Vector3f(0.5F, 0F, 1.1F);
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedVector, actualVector),
                () -> Assertions.assertEquals(1, model.getVerticesSize()),
                () -> Assertions.assertEquals(0, model.getTextureVerticesSize()),
                () -> Assertions.assertEquals(0, model.getNormalsSize())
        );
    }

    @Test
    void testParseTextureVertex() {
        Model model = ObjReader.read("vt 0 0.7");
        Vector2f actualVector = model.getTextureVertices().get(0);

        Vector2f expectedVector = new Vector2f(0F, 0.7F);
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedVector, actualVector),
                () -> Assertions.assertEquals(0, model.getVerticesSize()),
                () -> Assertions.assertEquals(1, model.getTextureVerticesSize()),
                () -> Assertions.assertEquals(0, model.getNormalsSize())
        );
    }

    @Test
    void testParseNormal() {
        Model model = ObjReader.read("vn 0.0 0 -1.1");
        Vector3f actualVector = model.getNormals().get(0);

        Vector3f expectedVector = new Vector3f(0F, 0F, -1.1F);
        Assertions.assertAll(
                () -> Assertions.assertEquals(expectedVector, actualVector),
                () -> Assertions.assertEquals(0, model.getVerticesSize()),
                () -> Assertions.assertEquals(0, model.getTextureVerticesSize()),
                () -> Assertions.assertEquals(1, model.getNormalsSize())
        );
    }


    @Test
    void testDecimalSeparator() {
        Model model = ObjReader.read("v 0.5 0 1.1");
        Vector3f actual = model.getVertices().get(0);

        Vector3f expected = new Vector3f(0.5F, 0F, 1.1F);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testCommaSeparator() {
        Model model = ObjReader.read("v 0,5 0 1,1");
        Vector3f actual = model.getVertices().get(0);

        Vector3f expected = new Vector3f(0.5F, 0F, 1.1F);
        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testBothSeparators() {
        try {
            Model model = ObjReader.read("v 0.5 0 1,1");
            Assertions.fail();
        } catch (RuntimeException exception) {
            String expectedMessage = "Two different decimal separators used in one file.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testSeparatorInComments() {
        Model model = ObjReader.read("v 0.5 0 1.1#1,6");
        Vector3f actual = model.getVertices().get(0);

        Vector3f expected = new Vector3f(0.5F, 0F, 1.1F);
        Assertions.assertAll(
                () -> Assertions.assertEquals(expected, actual),
                () -> Assertions.assertEquals(1, model.getVerticesSize()),
                () -> Assertions.assertEquals(0, model.getTextureVerticesSize()),
                () -> Assertions.assertEquals(0, model.getNormalsSize())
        );
    }
}
