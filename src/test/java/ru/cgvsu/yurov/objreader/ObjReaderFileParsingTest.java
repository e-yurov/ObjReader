package ru.cgvsu.yurov.objreader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.cgvsu.yurov.math.Vector2f;
import ru.cgvsu.yurov.math.Vector3f;
import ru.cgvsu.yurov.model.Group;
import ru.cgvsu.yurov.model.Model;
import ru.cgvsu.yurov.model.Polygon;
import ru.cgvsu.yurov.objreader.exceptions.*;

import java.io.File;
import java.util.List;

public class ObjReaderFileParsingTest {
    @Test
    void testTextureMismatch() {
        try {
            Model model = ObjReader.read(new File("src/test/resources/ObjFiles/TextureMismatch.obj"));
            Assertions.fail();
        } catch (TextureException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 12. Texture presence mismatch.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testInvalidToken() {
        try {
            Model model = ObjReader.read(new File("src/test/resources/ObjFiles/InvalidToken.obj"));
            Assertions.fail();
        } catch (TokenException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 7. Invalid line beginning.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testComments() {
        Model model = ObjReader.read(new File("src/test/resources/ObjFiles/TestComments.obj"));
        Vector3f v1 = new Vector3f(0, 1, 1);
        Vector3f v2 = new Vector3f(0.2F, 0.5F, -0.9F);
        Vector3f v3 = new Vector3f(1F, 1F, 1F);

        Vector3f vn1 = new Vector3f(0.5F, 0.5F, 0.5F);
        Vector3f vn2 = new Vector3f(0F, 0F, 0F);
        Vector3f vn3 = new Vector3f(1F, 0F, 0F);

        Polygon f1 = new Polygon();
        f1.setVertexIndices(List.of(0, 1, 2));
        f1.setNormalIndices(List.of(0, 1, 2));

        Polygon f2 = new Polygon();
        f2.setVertexIndices(List.of(2, 1, 0));
        f2.setNormalIndices(List.of(0, 1, 2));

        Assertions.assertAll(
                () -> Assertions.assertEquals(List.of(v1, v2, v3), model.getVertices()),
                () -> Assertions.assertEquals(List.of(vn1, vn2, vn3), model.getNormals()),
                () -> Assertions.assertEquals(List.of(f1, f2), model.getPolygons())
        );
    }

    @Test
    void testGroups() {
        Model model = ObjReader.read(new File("src/test/resources/ObjFiles/GroupTest.obj"));

        Polygon f1 = new Polygon();
        f1.setVertexIndices(List.of(1, 2, 3));
        f1.setTextureVertexIndices(List.of(3, 4, 5));

        List<Group> groups = model.getGroups();
        Group g1Actual = groups.get(0);
        Group g2Actual = groups.get(1);

        Assertions.assertAll(
                () -> Assertions.assertEquals(2, groups.size()),
                () -> Assertions.assertEquals("group0", g1Actual.getName()),
                () -> Assertions.assertEquals("Group01", g2Actual.getName()),

                () -> Assertions.assertEquals(0, g1Actual.getPolygonsSize()),
                () -> Assertions.assertTrue(g1Actual.getPolygons().isEmpty()),

                () -> Assertions.assertEquals(1, g2Actual.getPolygonsSize()),
                () -> Assertions.assertEquals(List.of(f1), g2Actual.getPolygons())
        );
    }

    @Test
    void testGroupNameException() {
        try {
            Model model = ObjReader.read(new File("src/test/resources/ObjFiles/GroupNameExceptionTest.obj"));
            Assertions.fail();
        } catch (GroupNameException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 1. Group must have a name.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }

    @Test
    void testCyrillicAndSpaces() {
        Model model = ObjReader.read(new File("src/test/resources/ObjFiles/Тест кириллица с пробелами.obj"));
    }

    @Test
    void testPolygonBeforeVertices() {
        Model model = ObjReader.read(new File("src/test/resources/ObjFiles/PolygonBeforeVertices.obj"));

        Polygon f = new Polygon();
        f.setVertexIndices(List.of(0, 1, 2));

        Assertions.assertEquals(f, model.getPolygons().get(0));
    }

    @Test
    void testPolygonIndicesOutOfBounds() {
        try {
            Model model = ObjReader.read(new File("src/test/resources/ObjFiles/PolygonVertexIndicesOutOfBounds.obj"));
            Assertions.fail();
        } catch (FaceWordIndexException exception) {
            String expectedMessage = "Error parsing OBJ file on line: 5. Exception in face argument 3: vertex index out of bounds.";
            Assertions.assertEquals(expectedMessage, exception.getMessage());
        }
    }
}
