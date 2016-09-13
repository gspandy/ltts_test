package cn.sunline.lttsts;

import java.io.FileNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class LttsCommand {

	private static Logger logger = LoggerFactory.getLogger(LttsCommand.class);

	public static int parseCommand(String[] args) {
		if (args.length > 3 || args.length == 0) {
			throw new LttsException("8001", "参数个数不正确");
		}
		int iRun = 0;
		boolean secParam = false;
		boolean secRun = false;

		switch (args[0].toLowerCase()) {
		// 配置信息
		case "config":
			secParam = true;
			break;
		case "run":
			secRun = true;
			break;
		case "show":
			ShowCommand();
			break;
		case "help":
			HelpCommand();
			break;
		default:
			throw new LttsException("8002", "命令参数不支持");
		}

		/*
		 * 解析设置二级命令
		 */
		if (secParam) {
			if (args.length != 3)
				throw new LttsException("8002", "设置环境变量参数个数不正确");
			try {
				LttsEnv.config(args);
			} catch (FileNotFoundException e) {
				logger.error("读取环境变量配置文件不存在");
				throw new LttsException("8003", "设置环境变量参数个数不正确");
			}
		}

		/*
		 * 解析运行命令
		 */
		if (secRun) {
			if (args.length != 2)
				throw new LttsException("8002", "设置环境变量参数个数不正确");
			if ("all".equals(args[1].toLowerCase())) {
				iRun = 1;
			} else {
				iRun = 2;
			}
		}

		return iRun;

	}

	private static void HelpCommand() {
		System.out.println("参数列表：");
		System.out.println("  help                                                      --查看帮助信息");
		System.out.println("  show                                                     --显示设置的配置信息");
		System.out.println("  config                                                   --设置测试环境信息");
		System.out.println("    config homepath 案例项目路径            --设置本地测试案例路径");
		System.out.println("    config servip 与核心通讯dmb地址         --设置与核心通讯dmb地址");
		System.out.println("    config servport 与核心通讯dmb端口号  --设置与核心通讯dmb端口号（默认值为63792）");
		System.out.println("  run [ all | 案例编号 ]                                --执行测试项目目录下测试案例");
		System.out.println("    run all                                                  --执行测试项目目录下所有测试案例");
		System.out.println("    run 案例编号                                         --执行单个测试案例");
	}

	private static void ShowCommand() {
		System.out.println("环境变量：");
		System.out.println("  homePath(测试案例路径)  --" + LttsEnv.getHomePath());
		System.out.println("  servIP(DMB服务地址)        --" + LttsEnv.getServIP());
		System.out.println("  servPort(DMB服务端口)     --" + LttsEnv.getServPort());
		System.out.println("  servce(DMB服务ID)           --" + LttsEnv.getServce());
		System.out.println("  scen(DMB场景ID)              --" + LttsEnv.getScen());
		System.out.println("  dcn(DMB dcn)                  --" + LttsEnv.getDcn());
		System.out.println("  timeout(DMB超时时间)     --" + LttsEnv.getTimeout());
	}
}
