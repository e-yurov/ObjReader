package ru.cgvsu.yurov.objreader;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.cgvsu.yurov.math.Vector3f;
import ru.cgvsu.yurov.model.Model;
import ru.cgvsu.yurov.model.Polygon;
import ru.cgvsu.yurov.objreader.exceptions.TextureException;
import ru.cgvsu.yurov.objreader.exceptions.TokenException;

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
}
