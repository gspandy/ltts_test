package cn.sunline.lttsts;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LoadJson {
	private static Logger logger = LoggerFactory.getLogger(LoadJson.class);

	// 单测试案例
	private static String TESTCASE = null;
	private static String TESTASSERTCASE = null;

	// 加载单测试案例结束标志
	private static boolean singleTestCase = false;

	// 加载单测试案例断言案例结束标志
	private static boolean singleAssertCase = false;

	// 测试案例
	private static final Map<String, String> jsons = new TreeMap<String, String>(
			new Comparator<String>() {
				public int compare(String obj1, String obj2) {
					// 降序排序
					return obj2.compareTo(obj1);
				}
			});

	/**
	 * 加载路径下所有测试案例
	 */
	public static void newInstance() {
		logger.debug("单元测试案例加载路径================" + LttsEnv.getHomePath());
		Path path = Paths.get(LttsEnv.getHomePath());
		try {
			Files.walkFileTree(path, new FindJsonVisitor());
		} catch (IOException e) {
			logger.error("文件读取失败" + path.toString());
			throw new LttsException("9904", "文件读取失败" + path.toString());
		}
	}

	/**
	 * 加载单个测试案例
	 * 
	 * @param testCase
	 */
	public static void newSingleInstance(String testCase) {
		TESTCASE = testCase + ".json";
		TESTASSERTCASE = testCase + "_resp.json";
		newInstance();
	}

	public static Map<String, String> getInstance() {
		return jsons;
	}

	private static class FindJsonVisitor extends SimpleFileVisitor<Path> {
		/**
		 * 遍历文件
		 */
		public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
			String filePath = file.toString();
			String fileName = file.getFileName().toString();
			if (fileName.endsWith(".json")) {
				if (TESTCASE != null && fileName.equals(TESTCASE)) {
					readFile(file, fileName);
					singleTestCase = true;
					if (singleTestCase && singleAssertCase) {
						return FileVisitResult.TERMINATE;
					} else {
						return FileVisitResult.CONTINUE;
					}
				} else if (TESTCASE != null && fileName.equals(TESTASSERTCASE)) {
					readFile(file, fileName);
					singleAssertCase = true;
					if (singleTestCase && singleAssertCase) {
						return FileVisitResult.TERMINATE;
					} else {
						return FileVisitResult.CONTINUE;
					}
				} else if (TESTCASE == null) {
					readFile(file, fileName);
				}
			} else {
				logger.warn("存在非json结束的文件" + filePath);
			}
			return FileVisitResult.CONTINUE;
		}

		/**
		 * 读取测试案例
		 * 
		 * @param path
		 * @param fileName
		 */
		private void readFile(Path path, String fileName) {
			String filePath = path.toString();
			if (Files.isReadable(path)) {
				try {
					BufferedReader br = Files.newBufferedReader(path,
							Charset.forName(LttsConstant.FILE_CHAR));
					char[] readBuff = new char[LttsConstant.FILE_SIZE];
					int readNum = br.read(readBuff);
					if (readNum != -1) {
						jsons.put(fileName, String.valueOf(readBuff).trim());
						logger.debug("已加载测试案例" + fileName + "读取案例文件大小"
								+ readNum);
						logger.debug("案例内容" + String.valueOf(readBuff).trim());
					} else {
						logger.error("文件太大" + filePath + "读取的大小为" + readNum);
						throw new LttsException("9901", "文件太大" + filePath
								+ "读取的大小为" + readNum);
					}
				} catch (IOException e) {
					logger.error("无法读取文件" + filePath);
					throw new LttsException("9902", "无法读取文件" + filePath);
				}
			} else {
				logger.error("文件权限不正确，不可读" + filePath);
				throw new LttsException("9903", "文件权限不正确，不可读" + filePath);
			}
		}
	}
}
