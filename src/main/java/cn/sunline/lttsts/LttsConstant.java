package cn.sunline.lttsts;

public final class LttsConstant {
	public static final String FILE_CHAR = "UTF-8";
	public static final int FILE_SIZE = 5120;

	//public static final String HOME_PATH = LttsConstant.class.getClassLoader().getResource("").getPath();
	public static final String HOME_PATH = System.getenv("TEST_HOME")+"/";
	public static final String ENV_SETTING = "env.properties";
	public static final String DMB_SETTING = "dmb-client.properties";
	public static final String REPORT_FILE = "report";

	/*
	 * public static final String ENV_PATH = Thread.currentThread()
	 * .getContextClassLoader().getResource(ENV_SETTING).getPath();
	 * 
	 * public static final String DMB_PATH = Thread.currentThread()
	 * .getContextClassLoader().getResource(DMB_SETTING).getPath();
	 */
	public static final String ENV_PATH = HOME_PATH + ENV_SETTING;
	public static final String DMB_PATH = HOME_PATH + DMB_SETTING;

	public static final String SUCCESS = "OK";
	public static final String ERROR = "FAILED";
	public static final String NEWLINE = "\r\n";
}
