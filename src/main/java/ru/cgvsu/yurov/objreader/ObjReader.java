package ru.cgvsu.yurov.objreader;

import ru.cgvsu.yurov.math.Vector2f;
import ru.cgvsu.yurov.math.Vector3f;
import ru.cgvsu.yurov.model.Model;
import ru.cgvsu.yurov.model.Polygon;
import ru.cgvsu.yurov.objreader.exceptions.ArgumentsErrorType;
import ru.cgvsu.yurov.objreader.exceptions.ArgumentsSizeException;
import ru.cgvsu.yurov.objreader.exceptions.ObjReaderException;
import ru.cgvsu.yurov.objreader.exceptions.ParsingException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.*;

public class ObjReader {
	private static final String OBJ_VERTEX_TOKEN = "v";
	private static final String OBJ_TEXTURE_TOKEN = "vt";
	private static final String OBJ_NORMAL_TOKEN = "vn";
	private static final String OBJ_FACE_TOKEN = "f";
	private static final String COMMENT_TOKEN = "#";

	private Model model = new Model();
	private int lineIndex = 0;
	private DecimalFormat format = new DecimalFormat("0.#");

	public static Model read(File file) {
		String content;
		try {
			content = Files.readString(Path.of(file.getCanonicalPath()));
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

		return read(content);
	}

	public static Model read(String content) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		int dotIndex = content.indexOf('.');
		int commaIndex = content.indexOf(',');
		if (dotIndex > -1 && commaIndex > -1) {
			throw new RuntimeException();
		}
		if (dotIndex > -1) {
			symbols.setDecimalSeparator('.');
		} else {
			symbols.setDecimalSeparator(',');
		}

		ObjReader objReader = new ObjReader();
		objReader.format.setDecimalFormatSymbols(symbols);
		objReader.readModel(content);
		return objReader.model;
	}

	private void readModel(String content) {
		Scanner scanner = new Scanner(content);
		scanner.useLocale(Locale.ROOT);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();

			int commentIndex = line.indexOf(COMMENT_TOKEN);
			if (commentIndex > -1) {
				line = line.substring(0, commentIndex);
			}

			if (line.isBlank()) {
				continue;
			}
			ArrayList<String> wordsInLine = new ArrayList<>(Arrays.asList(line.split("\\s+")));


			final String token = wordsInLine.get(0);
			wordsInLine.remove(0);

			lineIndex++;
			switch (token) {
				case OBJ_VERTEX_TOKEN -> model.addVertex(parseVertex(wordsInLine));
				case OBJ_TEXTURE_TOKEN -> model.addTextureVertex(parseTextureVertex(wordsInLine));
				case OBJ_NORMAL_TOKEN -> model.addNormal(parseNormal(wordsInLine));
				case OBJ_FACE_TOKEN -> {
					Polygon polygon = parseFace(wordsInLine);

					if (!model.polygons.isEmpty()) {
						Polygon firstPolygon = model.getFirstPolygon();
						if (polygon.hasTexture() != firstPolygon.hasTexture()) {
							throw new ObjReaderException("Texture mismatch", lineIndex);
						}
					}
				}
				default -> throw new ParsingException(lineIndex);
			}
		}
	}

	protected Vector3f parseVertex(final ArrayList<String> wordsInLineWithoutToken) {
		checkSize(wordsInLineWithoutToken.size(), 3);
		try {
			return new Vector3f(
					format.parse(wordsInLineWithoutToken.get(0)).floatValue(),
					format.parse(wordsInLineWithoutToken.get(1)).floatValue(),
					format.parse(wordsInLineWithoutToken.get(2)).floatValue());

		} catch (ParseException e) {
			throw new ParsingException(lineIndex);
        }
    }

	protected Vector2f parseTextureVertex(final ArrayList<String> wordsInLineWithoutToken) {
		checkSize(wordsInLineWithoutToken.size(), 2);
		try {
			return new Vector2f(
					format.parse(wordsInLineWithoutToken.get(0)).floatValue(),
					format.parse(wordsInLineWithoutToken.get(1)).floatValue());

		} catch (ParseException e) {
			throw new ParsingException(lineIndex);
		}
	}

	protected Vector3f parseNormal(final ArrayList<String> wordsInLineWithoutToken) {
		checkSize(wordsInLineWithoutToken.size(), 3);
		try {
			return new Vector3f(
					format.parse(wordsInLineWithoutToken.get(0)).floatValue(),
					format.parse(wordsInLineWithoutToken.get(1)).floatValue(),
					format.parse(wordsInLineWithoutToken.get(2)).floatValue());

		} catch (ParseException e) {
			throw new ParsingException(lineIndex);
		}
	}
	protected Polygon parseFace(final ArrayList<String> wordsInLineWithoutToken) {
		List<FaceWord> faceWords = new ArrayList<>();
		Set<WordType> types = new HashSet<>();

		for (String word : wordsInLineWithoutToken) {
			FaceWord faceWord = FaceWord.parse(word, lineIndex);

			types.add(faceWord.getWordType());
			faceWords.add(faceWord);
		}

		if (faceWords.size() < 3) {
			throw new ArgumentsSizeException(ArgumentsErrorType.FEW, lineIndex);
		}
		if (types.size() > 1) {
			throw new ObjReaderException("Face words type mismatch", lineIndex);
		}

		return createPolygon(faceWords);
	}

	private Polygon createPolygon(List<FaceWord> faceWords) {
		int verticesSize = model.getVerticesSize();
		int textureVerticesSize = model.getTextureVerticesSize();
		int normalsSize = model.getNormalsSize();

		Polygon polygon = new Polygon();
		ArrayList<Integer> vertexIndices = new ArrayList<>();
		ArrayList<Integer> textureVertexIndices = new ArrayList<>();
		ArrayList<Integer> normalIndices = new ArrayList<>();
		for (FaceWord faceWord: faceWords) {
			faceWord.checkIndices(verticesSize, textureVerticesSize, normalsSize, lineIndex);
			Integer vertexIndex = faceWord.getVertexIndex();
			if (vertexIndex != null) {
				vertexIndices.add(vertexIndex);
			}
			Integer textureVertexIndex = faceWord.getTextureVertexIndex();
			if (textureVertexIndex != null) {
				textureVertexIndices.add(textureVertexIndex);
			}
			Integer normalIndex = faceWord.getNormalIndex();
			if (normalIndex != null) {
				normalIndices.add(normalIndex);
			}
		}
		polygon.setVertexIndices(vertexIndices);
		polygon.setTextureVertexIndices(textureVertexIndices);
		polygon.setNormalIndices(normalIndices);

		return polygon;
	}

	private void checkSize(int wordCount, int vectorSize) {
		if (wordCount == vectorSize) {
			return;
		}

		if (wordCount < vectorSize) {
			throw new ArgumentsSizeException(ArgumentsErrorType.FEW, lineIndex);
		}
		throw new ArgumentsSizeException(ArgumentsErrorType.MANY, lineIndex);
	}
}
