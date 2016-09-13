package cn.sunline.lttsts;

public class LttsTest {

	public static void main(String[] args) {
		// 初始化环境变量
		LttsEnv.initEnv();
		// 解析命令行
		int status = LttsCommand.parseCommand(args);
		// 加载测试案例
		if (status == 0) {
			return;
		} else if (status == 1) {
			// 执行所有测试案例
			LoadJson.newInstance();
		} else if (status == 2) {
			// 执行单个测试案例
			LoadJson.newSingleInstance(args[1]);
		} else {
			System.out.println("error:命令行解析错误");
			return;
		}
		
		//执行测试案例
		new LttsCase().runTestCase();
		
		System.exit(0);
	}

}
