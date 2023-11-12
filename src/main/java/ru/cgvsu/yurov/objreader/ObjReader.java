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
import java.util.*;

public class ObjReader {

	private static final String OBJ_VERTEX_TOKEN = "v";
	private static final String OBJ_TEXTURE_TOKEN = "vt";
	private static final String OBJ_NORMAL_TOKEN = "vn";
	private static final String OBJ_FACE_TOKEN = "f";
	private static final String COMMENT_TOKEN = "#";

	public static Model readOld(String fileContent) {
		Model result = new Model();

		int lineIndex = 0;
		Scanner scanner = new Scanner(fileContent);
		scanner.useLocale(Locale.ROOT);
		while (scanner.hasNextLine()) {
			final String line = scanner.nextLine();
			ArrayList<String> wordsInLine = new ArrayList<>(Arrays.asList(line.split("\\s+")));
			if (wordsInLine.isEmpty()) {
				continue;
			}

			final String token = wordsInLine.get(0);
			wordsInLine.remove(0);

			lineIndex++;
			switch (token) {
				// Для структур типа вершин методы написаны так, чтобы ничего не знать о внешней среде.
				// Они принимают только то, что им нужно для работы, а возвращают только то, что могут создать.
				// Исключение - индекс строки. Он прокидывается, чтобы выводить сообщение об ошибке.
				// Могло быть иначе. Например, метод parseVertex мог вместо возвращения вершины принимать вектор вершин
				// модели или сам класс модели, работать с ним.
				// Но такой подход может привести к большему количеству ошибок в коде. Например, в нем что-то может
				// тайно сделаться с классом модели.
				// А еще это портит читаемость
				// И не стоит забывать про тесты. Чем проще вам задать данные для теста, проверить, что метод рабочий,
				// тем лучше.
				case OBJ_VERTEX_TOKEN -> result.vertices.add(parseVertex(wordsInLine, lineIndex));
				case OBJ_TEXTURE_TOKEN -> result.textureVertices.add(parseTextureVertex(wordsInLine, lineIndex));
				case OBJ_NORMAL_TOKEN -> result.normals.add(parseNormal(wordsInLine, lineIndex));
				case OBJ_FACE_TOKEN -> result.polygons.add(parseFace(wordsInLine, lineIndex));
				case COMMENT_TOKEN -> {}
				default -> throw new ParsingException(lineIndex);
			}
		}

		return result;
	}

	private Model model = new Model();
	private int lineIndex = 0;

	public static Model read(File file) {
		String content;
		try {
			content = Files.readString(Path.of(file.getAbsolutePath()));
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
		return read(content);
	}

	public static Model read(String content) {
		ObjReader objReader = new ObjReader();
		objReader.read3(content);
		return objReader.model;
	}

	private Model read3(String fileContent) {
		//int lineIndex = 0;
		Scanner scanner = new Scanner(fileContent);
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
			/*if (wordsInLine.isEmpty()) {
				continue;
			}*/

			final String token = wordsInLine.get(0);
			wordsInLine.remove(0);

			lineIndex++;
			switch (token) {
				case OBJ_VERTEX_TOKEN -> model.addVertex(parseVertex(wordsInLine, lineIndex));
						//result.vertices.add(parseVertex(wordsInLine, lineIndex));
				case OBJ_TEXTURE_TOKEN -> model.addTextureVertex(parseTextureVertex(wordsInLine, lineIndex));
						//result.textureVertices.add(parseTextureVertex(wordsInLine, lineIndex));
				case OBJ_NORMAL_TOKEN -> model.addNormal(parseNormal(wordsInLine, lineIndex));
						//result.normals.add(parseNormal(wordsInLine, lineIndex));
				case OBJ_FACE_TOKEN -> {
					//model.addPolygon(parseFace(wordsInLine, lineIndex));
					Polygon polygon = parseFace(wordsInLine, lineIndex);

					if (!model.polygons.isEmpty()) {
						Polygon firstPolygon = model.getFirstPolygon();
						if (polygon.hasTexture() != firstPolygon.hasTexture()) {
							throw new ObjReaderException("Texture mismatch", lineIndex);
						}
					}
				}
						//result.polygons.add(parseFace(wordsInLine, lineIndex));
				case COMMENT_TOKEN -> {}
				default -> throw new ParsingException(lineIndex);
			}
		}

		return null;
		//return result;
	}

	// Всем методам кроме основного я поставил модификатор доступа protected, чтобы обращаться к ним в тестах
	protected static Vector3f parseVertex(final ArrayList<String> wordsInLineWithoutToken, int lineIndex) {
		checkSize(wordsInLineWithoutToken, 3, lineIndex);
		try {
			/*if (wordsInLineWithoutToken.size() > 3) {
				if (!wordsInLineWithoutToken.get(3).startsWith("#")) {
					throw new ObjReaderException("Too many vertex arguments.", lineIndex);
				}
			}*/

			return new Vector3f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)),
					Float.parseFloat(wordsInLineWithoutToken.get(2)));

		} catch(NumberFormatException e) {
			throw new ParsingException(lineIndex);
		}
	}

	protected static Vector2f parseTextureVertex(final ArrayList<String> wordsInLineWithoutToken, int lineIndex) {
		checkSize(wordsInLineWithoutToken, 2, lineIndex);
		try {
			return new Vector2f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)));

		} catch(NumberFormatException e) {
			throw new ParsingException(lineIndex);
		}
	}

	protected static Vector3f parseNormal(final ArrayList<String> wordsInLineWithoutToken, int lineIndex) {
		checkSize(wordsInLineWithoutToken, 3, lineIndex);
		try {
			return new Vector3f(
					Float.parseFloat(wordsInLineWithoutToken.get(0)),
					Float.parseFloat(wordsInLineWithoutToken.get(1)),
					Float.parseFloat(wordsInLineWithoutToken.get(2)));

		} catch(NumberFormatException e) {
			throw new ParsingException(lineIndex);
		}
	}

	protected static Polygon parseFace(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
		ArrayList<Integer> onePolygonVertexIndices = new ArrayList<>();
		ArrayList<Integer> onePolygonTextureVertexIndices = new ArrayList<>();
		ArrayList<Integer> onePolygonNormalIndices = new ArrayList<>();

		for (String s : wordsInLineWithoutToken) {
			parseFaceWord(s, onePolygonVertexIndices, onePolygonTextureVertexIndices, onePolygonNormalIndices, lineInd);
		}

		Polygon result = new Polygon();
		result.setVertexIndices(onePolygonVertexIndices);
		result.setTextureVertexIndices(onePolygonTextureVertexIndices);
		result.setNormalIndices(onePolygonNormalIndices);
		return result;
	}

	// Обратите внимание, что для чтения полигонов я выделил еще один вспомогательный метод.
	// Это бывает очень полезно и с точки зрения структурирования алгоритма в голове, и с точки зрения тестирования.
	// В радикальных случаях не бойтесь выносить в отдельные методы и тестировать код из одной-двух строчек.
	protected static void parseFaceWord(
			String wordInLine,
			ArrayList<Integer> onePolygonVertexIndices,
			ArrayList<Integer> onePolygonTextureVertexIndices,
			ArrayList<Integer> onePolygonNormalIndices,
			int lineInd) {
		try {
			String[] wordIndices = wordInLine.split("/");
			switch (wordIndices.length) {
				case 1 -> {
					onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
				}
				case 2 -> {
					onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
					onePolygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]) - 1);
				}
				case 3 -> {
					onePolygonVertexIndices.add(Integer.parseInt(wordIndices[0]) - 1);
					onePolygonNormalIndices.add(Integer.parseInt(wordIndices[2]) - 1);
					if (!wordIndices[1].equals("")) {
						onePolygonTextureVertexIndices.add(Integer.parseInt(wordIndices[1]) - 1);
					}
				}
				default -> {
					throw new ObjReaderException("Invalid element size.", lineInd);
				}
			}

		} catch(NumberFormatException e) {
			throw new ObjReaderException("Failed to parse int value.", lineInd);

		} catch(IndexOutOfBoundsException e) {
			throw new ObjReaderException("Too few arguments.", lineInd);
		}
	}

	protected void parseFace2(final ArrayList<String> wordsInLineWithoutToken, int lineInd) {
		List<FaceWord> faceWords = new ArrayList<>();
		Set<WordType> types = new HashSet<>();
		for (String s : wordsInLineWithoutToken) {
			FaceWord faceWord = parseFaceWord2(s, lineInd);
			faceWord.checkIndices(model.getVerticesSize(), model.getTextureVerticesSize(),
					model.getNormalsSize(), lineIndex);

			types.add(faceWord.getWordType());
			faceWords.add(faceWord);
		}

		if (faceWords.size() < 3) {
			throw new ArgumentsSizeException(ArgumentsErrorType.FEW, lineInd);
		}
		if (types.size() > 1) {
			throw new ObjReaderException("Face words type mismatch", lineInd);
		}
	}

	protected static FaceWord parseFaceWord2(String word, int lineIndex) {
		FaceWord faceWord = FaceWord.parse(word, lineIndex);
		return faceWord;
	}

	private static void checkSize(ArrayList<String> wordsInLineWithoutToken, int vectorSize, int lineInd) {
		int wordCount = wordsInLineWithoutToken.size();

		if (wordCount == vectorSize) {
			return;
		}

		if (wordCount < vectorSize) {
			throw new ArgumentsSizeException(ArgumentsErrorType.FEW, lineInd);
		}

		/*if (wordsInLineWithoutToken.get(vectorSize).equals(COMMENT_TOKEN)) {
			return;
		}*/
		throw new ArgumentsSizeException(ArgumentsErrorType.MANY, lineInd);
	}
}
