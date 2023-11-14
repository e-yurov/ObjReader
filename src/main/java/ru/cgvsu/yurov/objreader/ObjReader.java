package ru.cgvsu.yurov.objreader;

import ru.cgvsu.yurov.math.Vector2f;
import ru.cgvsu.yurov.math.Vector3f;
import ru.cgvsu.yurov.model.Model;
import ru.cgvsu.yurov.model.Polygon;
import ru.cgvsu.yurov.objreader.exceptions.*;

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

	private int lineIndex = 0;
	private final Model model = new Model();
	private final DecimalFormat format = new DecimalFormat("0.#");

	protected ObjReader() {}

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
		ObjReader objReader = new ObjReader();
		DecimalFormatSymbols symbols = identifyDecimalSeparator(content);
		objReader.format.setDecimalFormatSymbols(symbols);
		objReader.readModel(content);
		return objReader.model;
	}

	private static DecimalFormatSymbols identifyDecimalSeparator(String content) {
		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		int dotIndex = content.indexOf('.');
		int commaIndex = content.indexOf(',');
		if (dotIndex > -1 && commaIndex > -1) {
			// TODO: change to ignore comments
			throw new RuntimeException("Two different decimal separators used in one file.");
		}
		if (commaIndex > -1) {
			symbols.setDecimalSeparator(',');
		} else {
			symbols.setDecimalSeparator('.');
		}

		return symbols;
	}

	protected void readModel(String content) {
		Scanner scanner = new Scanner(content);
		scanner.useLocale(Locale.ROOT);
		while (scanner.hasNextLine()) {
			lineIndex++;
			String line = scanner.nextLine();

			int commentIndex = line.indexOf(COMMENT_TOKEN);
			if (commentIndex > -1) {
				line = line.substring(0, commentIndex);
			}

			if (line.isBlank()) {
				continue;
			}

			String[] wordsInLine = line.split("\\s+");
			final String token = wordsInLine[0];
			String[] wordsInLineWithoutToken =  Arrays.copyOfRange(wordsInLine, 1, wordsInLine.length);

			switch (token) {
				case OBJ_VERTEX_TOKEN -> model.addVertex(parseVector3f(wordsInLineWithoutToken));
				case OBJ_TEXTURE_TOKEN -> model.addTextureVertex(parseVector2f(wordsInLineWithoutToken));
				case OBJ_NORMAL_TOKEN -> model.addNormal(parseVector3f(wordsInLineWithoutToken));
				case OBJ_FACE_TOKEN -> {
					Polygon polygon = parseFace(wordsInLineWithoutToken);

					if (!model.getPolygons().isEmpty()) {
						Polygon firstPolygon = model.getFirstPolygon();
						if (polygon.hasTexture() != firstPolygon.hasTexture()) {
							throw new TextureException(lineIndex);
						}
					}

					model.addPolygon(polygon);
				}
				// TODO: add group tag support
				default -> throw new TokenException(lineIndex);
			}
		}
	}

	protected Vector2f parseVector2f(final String[] wordsInLineWithoutToken) {
		checkSize(wordsInLineWithoutToken.length, 2);
		try {
			return new Vector2f(
					format.parse(wordsInLineWithoutToken[0]).floatValue(),
					format.parse(wordsInLineWithoutToken[1]).floatValue());

		} catch (ParseException e) {
			throw new ParsingException("float", lineIndex);
		}
	}

	protected Vector3f parseVector3f(final String[] wordsInLineWithoutToken) {
		checkSize(wordsInLineWithoutToken.length, 3);
		try {
			return new Vector3f(
					format.parse(wordsInLineWithoutToken[0]).floatValue(),
					format.parse(wordsInLineWithoutToken[1]).floatValue(),
					format.parse(wordsInLineWithoutToken[2]).floatValue());

		} catch (ParseException e) {
			throw new ParsingException("float", lineIndex);
		}
	}

	protected Polygon parseFace(final String[] wordsInLineWithoutToken) {
		List<FaceWord> faceWords = new ArrayList<>();
		Set<WordType> types = new HashSet<>();

		for (String word : wordsInLineWithoutToken) {
			FaceWord faceWord = FaceWord.parse(word, lineIndex);

			types.add(faceWord.getWordType());
			faceWords.add(faceWord);
		}

		if (faceWords.size() < 3) {
			throw new ArgumentsSizeException(ArgumentsErrorType.FEW_IN_POLYGON, lineIndex);
		}
		if (types.size() > 1) {
			throw new FaceWordTypeException(lineIndex);
		}

		return createPolygon(faceWords.toArray(new FaceWord[0]));
	}

	protected Polygon createPolygon(FaceWord[] faceWords) {
		int verticesSize = model.getVerticesSize();
		int textureVerticesSize = model.getTextureVerticesSize();
		int normalsSize = model.getNormalsSize();

		Polygon polygon = new Polygon();
		List<Integer> vertexIndices = new ArrayList<>();
		List<Integer> textureVertexIndices = new ArrayList<>();
		List<Integer> normalIndices = new ArrayList<>();
		for (int i = 0; i < faceWords.length; i ++) {
			FaceWord faceWord = faceWords[i];
			faceWord.checkIndices(verticesSize, textureVerticesSize, normalsSize, lineIndex, i);

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
