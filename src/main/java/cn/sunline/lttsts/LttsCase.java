package cn.sunline.lttsts;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public final class LttsCase {
	private static Logger logger = LoggerFactory.getLogger(LttsCase.class);
	private long total = 0; // 总案例数
	private long fails = 0; // 失败数据
	private long times = 0; // 总耗时
	private List<String> failList = new LinkedList<String>();
	private List<TestEntry> resultDetails = new LinkedList<TestEntry>();

	public void runTestCase() {
		long startTime = System.currentTimeMillis();
		Map<String, String> testCases = LoadJson.getInstance();
		for (Entry<String, String> set : testCases.entrySet()) {
			long caseTime = System.currentTimeMillis();
			String result = LttsConstant.SUCCESS;
			String testCase = set.getKey();
			// 跳过断言的案例
			if (testCase.endsWith("_resp.json")) {
				continue;
			}
			this.total++;
			logger.debug("测试案例编号" + testCase);
			logger.debug("测试案例报文" + set.getValue());
			String response = LttsDmb.wrapper(set.getValue());
			logger.debug("测试案例反馈报文" + response);
			LttsBody body = JSON.parseObject(response, LttsBody.class);

			// 获取对应的断言案例
			String assertCase = testCase.split("\\.")[0] + "_resp.json";
			logger.debug("加载断言案例" + assertCase);
			// 如果设置了断言，按照断言判断测试结果；
			// 否则按照默认逻辑，报文返回是否成功做为正确与否的结果
			if (testCases.containsKey(assertCase)) {
				logger.debug("匹配到断言案例" + assertCase);

				JSONObject assertCaseBody = JSON.parseObject(testCases
						.get(assertCase));
				JSONObject respContent = JSON.parseObject(body.getContent());

				for (Object a : assertCaseBody.keySet()) {
					logger.debug("加载断言要素匹配" + a.toString());
					logger.debug("反馈报文内容" + respContent.toString());
					Matcher key = Pattern.compile(
							"\"" + a.toString() + "\":\"(.+?)\"").matcher(
							respContent.toString());
					logger.debug("key数量======" + key.groupCount());
					if (key.find()) {
						logger.debug("key值为======" + key.group(0));
						logger.debug("key值为======" + key.group(1));

						if (!assertCaseBody.get(a).toString()
								.equals(key.group(1).toString()))
							result = LttsConstant.ERROR;
					} else {
						logger.error("断言要素不存在" + a.toString());
						throw new LttsException("9909", "断言要素不存在"
								+ a.toString());
					}
				}
			} else {
				if ("F".equals(body.getStatus())) {
					result = LttsConstant.ERROR;
				}
			}

			resultDetails.add(new TestEntry(set.getKey(), result, System
					.currentTimeMillis() - caseTime, body.getRetMsg()));

			if (LttsConstant.ERROR.equals(result)) {
				fails++;
				failList.add(set.getKey());
			}
		}

		this.times = System.currentTimeMillis() - startTime;

		printDocu(startTime);
	}

	/**
	 * 输出测试报告
	 */
	private void printDocu(long systemtime) {
		DateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(systemtime);

		System.out.println("================================================");
		System.out.println("		案例执行时间: " + formatter.format(calendar.getTime()));
		System.out.println("		执行总案例数: " + this.total + ", 失败数: " + this.fails
				+ ", 耗时: " + this.times);
		System.out.println("================================================");
		System.out.println("		失败案例列表");
		for (String caseName : failList)
			System.out.println("				" + caseName);
		System.out.println("================================================");

		// 报告文件
		String filePath = LttsEnv.getHomePath() + "/"
				+ LttsConstant.REPORT_FILE;
		String file = filePath + "/" + formatter.format(calendar.getTime())
				+ ".txt";
		logger.debug("报告文件:" + file);
		File dire = new File(filePath);
		logger.debug("报告文件夹:" + filePath);
		File reportFile = new File(file);
		try {
			if (!dire.exists())
				dire.mkdir();

			reportFile.createNewFile();
		} catch (IOException e2) {
			logger.error("创建文件夹失败:" + e2.getMessage());
			throw new LttsException("9902", "创建文件夹失败:" + e2.getMessage());
		}

		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(file);
			String seplit = "================================================"
					+ LttsConstant.NEWLINE;
			fos.write(seplit.getBytes());
			fos.write(("		执行总案例数: " + this.total + ", 失败数: " + this.fails
					+ ", 耗时: " + this.times + LttsConstant.NEWLINE).getBytes());
			fos.write(seplit.getBytes());
			fos.write(("		失败案例列表" + LttsConstant.NEWLINE).getBytes());
			for (String caseName : failList)
				fos.write(("				" + caseName + LttsConstant.NEWLINE).getBytes());
			fos.write(seplit.getBytes());
			fos.write(("		详细案例列表" + LttsConstant.NEWLINE).getBytes());
			fos.write(("				测试案例名称 			测试结果	耗时		错误信息" + LttsConstant.NEWLINE)
					.getBytes());
			for (TestEntry entry : resultDetails)
				fos.write(("			 	" + entry.getTestName() + "		"
						+ entry.getStatus() + "		" + entry.getActultime()
						+ "		" + entry.getErrMsg() + LttsConstant.NEWLINE)
						.getBytes());
			fos.write(seplit.getBytes());
			fos.flush();

		} catch (FileNotFoundException e1) {
			logger.error("文件不存在" + e1.getMessage());
			throw new LttsException("9902", "文件不存在" + e1.getMessage());
		} catch (IOException e) {
			logger.error("文件写入失败");
			throw new LttsException("9902", "写入失败" + e.getMessage());
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

	}

	private class TestEntry {
		private String testName;
		private String status;
		private String errMsg;
		private long actultime = 0;

		public String getTestName() {
			return testName;
		}

		public String getStatus() {
			return status;
		}

		public long getActultime() {
			return actultime;
		}

		public String getErrMsg() {
			return errMsg;
		}

		public TestEntry(String testName, String status, long actultime,
				String errMsg) {
			this.testName = testName;
			this.status = status;
			this.actultime = actultime;
			this.errMsg = errMsg;
		}
	}
}
