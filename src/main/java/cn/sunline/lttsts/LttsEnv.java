package cn.sunline.lttsts;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 保存设置的环境变量
 * 
 * @author cuijia
 *
 */
public class LttsEnv {
	private static Logger logger = LoggerFactory.getLogger(LttsEnv.class);

	private final static Properties SETTINGS = new Properties();

	public static void initEnv() {
		FileInputStream fis = null;
		try {
			logger.info("=======env path ====" + LttsConstant.ENV_PATH);
			fis = new FileInputStream(LttsConstant.ENV_PATH);
			
			SETTINGS.load(fis);

			fis.close();
		} catch (IOException e) {
			logger.error("读取环境变量配置文件异常=======" );
			throw new LttsException("9001", "读取环境变量配置文件异常");
		} finally {
			if (fis != null)
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("读取环境变量配置文件关闭异常=======" );
					throw new LttsException("9002", "读取环境变量配置文件关闭异常");
				}
		}
	}

	public static void config(String[] args) throws FileNotFoundException {
		String p = args[2];
		String comments = "";
		String dmb = null;
		switch (args[1].toLowerCase()) {
		case "homepath":
			comments = "homePath";
			break;
		case "servip":
			comments = "servIP";
			dmb = "mqIp";
			break;
		case "servport":
			comments = "servPort";
			dmb = "mqPort";
			break;
		default:
			throw new LttsException("8002", "命令参数不支持");
		}
		SETTINGS.setProperty(comments, p);
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(LttsConstant.ENV_PATH);
			
			SETTINGS.store(fos, "Update " + comments + " value");
			fos.flush();
			fos.close();

			if (dmb != null) {
				FileInputStream fis = new FileInputStream(LttsConstant.DMB_PATH);
				logger.debug("=======DMB.properties path====" + LttsConstant.DMB_PATH);
				Properties dmbProp = new Properties();
				dmbProp.load(fis);
				fis.close();
				dmbProp.setProperty(dmb, p);
				fos = new FileOutputStream(LttsConstant.DMB_PATH);
				dmbProp.store(fos, "Update " + dmb + " value");
				fos.flush();
				fos.close();
				logger.debug("=======更新DMB.properties配置文件");
			}

		} catch (IOException e) {
			logger.error("环境变量配置文件写入异常=======" );
			throw new LttsException("9003", "环境变量配置文件写入异常");
		} finally {
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					logger.error("环境变量配置文件写入关闭异常=======" );
					throw new LttsException("9004", "环境变量配置文件写入关闭异常");
				}
		}

	}

	public static String getHomePath() {
		return SETTINGS.getProperty("homePath");
	}

	public static String getServIP() {
		return SETTINGS.getProperty("servIP");
	}

	public static String getServPort() {
		return SETTINGS.getProperty("servPort");
	}

	public static String getServce() {
		return SETTINGS.getProperty("servce");
	}

	public static String getScen() {
		return SETTINGS.getProperty("scen");
	}

	public static String getDcn() {
		return SETTINGS.getProperty("dcn");
	}

	public static String getTimeout() {
		return SETTINGS.getProperty("timeout");
	}
}
