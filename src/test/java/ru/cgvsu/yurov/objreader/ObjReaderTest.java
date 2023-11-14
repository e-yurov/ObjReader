package ru.cgvsu.yurov.objreader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.cgvsu.yurov.math.Vector2f;
import ru.cgvsu.yurov.math.Vector3f;
import ru.cgvsu.yurov.objreader.exceptions.ArgumentsSizeException;
import ru.cgvsu.yurov.objreader.exceptions.FaceWordTypeException;

import java.io.File;

public class ObjReaderTest {
    /*@Test
    void test1() {
        ObjReader.read(new File("src/test/resources/SimpleModelsForReaderTests/NonManifold.obj"));
    }*/

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
        try {
            objReader.parseVector3f(new String[]{"3", "2", "1", "0"});
            Assertions.fail();
        } catch (ArgumentsSizeException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 0. Too many arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
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
        try {
            objReader.parseVector2f(new String[]{"3", "2", "1"});
            Assertions.fail();
        } catch (ArgumentsSizeException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 0. Too many arguments.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
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
}
