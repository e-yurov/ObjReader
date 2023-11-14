package ru.cgvsu.yurov.objreader;

import ru.cgvsu.yurov.math.Vector2f;
import ru.cgvsu.yurov.math.Vector3f;
import ru.cgvsu.yurov.model.Group;
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
	private static final String OBJ_GROUP_TOKEN = "g";
	private static final String COMMENT_TOKEN = "#";

	private int lineIndex = 0;
	private final Model model = new Model();
	private Group currentGroup = null;

	private final DecimalFormat format = new DecimalFormat("0.#");
	private Character decimalSeparator = null;

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
		objReader.readModel(content);
		return objReader.model;
	}

	protected void readModel(String content) {
		Scanner scanner = new Scanner(content);
		scanner.useLocale(Locale.ROOT);
		while (scanner.hasNextLine()) {
			lineIndex++;
			String line = handleLine(scanner.nextLine());

			if (line.isBlank()) {
				continue;
			}

			String[] wordsInLine = line.split("\\s+");
			final String token = wordsInLine[0];
			String[] wordsInLineWithoutToken =  Arrays.copyOfRange(wordsInLine, 1, wordsInLine.length);

			switch (token) {
				case OBJ_VERTEX_TOKEN -> handleVertex(wordsInLineWithoutToken);
				case OBJ_TEXTURE_TOKEN -> handleTextureVertex(wordsInLineWithoutToken);
				case OBJ_NORMAL_TOKEN -> handleNormal(wordsInLineWithoutToken);
				case OBJ_FACE_TOKEN -> handleFace(wordsInLineWithoutToken);
				case OBJ_GROUP_TOKEN -> handleGroup(wordsInLineWithoutToken);
				default -> throw new TokenException(lineIndex);
			}
		}

		if (currentGroup != null) {
			model.addGroup(currentGroup);
		}

		int verticesSize = model.getVerticesSize();
		int textureVerticesSize = model.getTextureVerticesSize();
		int normalsSize = model.getNormalsSize();
		for (Polygon polygon: model.getPolygons()) {
			polygon.checkIndices(verticesSize, textureVerticesSize, normalsSize);
		}
	}

	private String handleLine(String line) {
		int commentIndex = line.indexOf(COMMENT_TOKEN);
		if (commentIndex > -1) {
			line = line.substring(0, commentIndex);
		}

		int dotIndex = line.indexOf('.');
		int commaIndex = line.indexOf(',');

		if (dotIndex > -1 && commaIndex > -1) {
			throw new RuntimeException("Two different decimal separators used in one file.");
		}

		if (decimalSeparator != null) {
			if (dotIndex > -1 && decimalSeparator == ',') {
				throw new RuntimeException("Two different decimal separators used in one file.");
			}
			if (commaIndex > -1 && decimalSeparator == '.') {
				throw new RuntimeException("Two different decimal separators used in one file.");
			}

			return line;
		}

		DecimalFormatSymbols symbols = new DecimalFormatSymbols();
		if (commaIndex > -1) {
			symbols.setDecimalSeparator(',');
			decimalSeparator = ',';
			format.setDecimalFormatSymbols(symbols);
		}
		if (dotIndex > -1){
			symbols.setDecimalSeparator('.');
			decimalSeparator = '.';
			format.setDecimalFormatSymbols(symbols);
		}

		return line;
	}

	private void handleVertex(String[] wordsInLineWithoutToken) {
		Vector3f vertex = parseVector3f(wordsInLineWithoutToken);
		model.addVertex(vertex);
		if (currentGroup != null) {
			currentGroup.addVertex(vertex);
		}
	}

	private void handleTextureVertex(String[] wordsInLineWithoutToken) {
		Vector2f textureVertex = parseVector2f(wordsInLineWithoutToken);
		model.addTextureVertex(textureVertex);
		if (currentGroup != null) {
			currentGroup.addTextureVertex(textureVertex);
		}
	}

	private void handleNormal(String[] wordsInLineWithoutToken) {
		Vector3f normal = parseVector3f(wordsInLineWithoutToken);
		model.addNormal(normal);
		if (currentGroup != null) {
			currentGroup.addNormal(normal);
		}
	}

	private void handleFace(String[] wordsInLineWithoutToken) {
		Polygon polygon = parseFace(wordsInLineWithoutToken);

		if (!model.getPolygons().isEmpty()) {
			Polygon firstPolygon = model.getFirstPolygon();
			if (polygon.hasTexture() != firstPolygon.hasTexture()) {
				throw new TextureException(lineIndex);
			}
		}

		model.addPolygon(polygon);
		if (currentGroup != null) {
			currentGroup.addPolygon(polygon);
		}
	}

	private void handleGroup(String[] wordsInLineWithoutToken) {
		if (wordsInLineWithoutToken.length == 0) {
			throw new GroupNameException(lineIndex);
		}

		if (currentGroup != null) {
			model.addGroup(currentGroup);
		}

		StringBuilder sb = new StringBuilder();
		for (String s : wordsInLineWithoutToken) {
			sb.append(s);
		}
		currentGroup = new Group(sb.toString());
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
		Polygon polygon = new Polygon();
		List<Integer> vertexIndices = new ArrayList<>();
		List<Integer> textureVertexIndices = new ArrayList<>();
		List<Integer> normalIndices = new ArrayList<>();
		for (int i = 0; i < faceWords.length; i ++) {
			FaceWord faceWord = faceWords[i];

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
		polygon.setLineIndex(lineIndex);

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
