package cn.sunline.lttsts;

import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.sunline.ltts.dmb.DMB;
import cn.sunline.ltts.dmb.model.DMBDestination;
import cn.sunline.ltts.dmb.model.DMBRequest;
import cn.sunline.ltts.dmb.model.DMBResponse;
import cn.sunline.ltts.dmb.util.JsonUtil;

public class LttsDmb {

	private static Logger logger = LoggerFactory.getLogger(LttsDmb.class);

	public static String wrapper(String content) {
			DMB smb = DMB.getInstance();
			DMBRequest req = new DMBRequest();
			// 初始化DMB 服务ID 场景ID
			req.setDestination(new DMBDestination(LttsEnv.getServce(), LttsEnv
					.getScen(), LttsEnv.getDcn()));
			// 设置超时时间 单位毫秒
			req.setTimeout(Long.parseLong(LttsEnv.getTimeout()));
			// 业务流水号
			req.setBizSeqNo(RandomStringUtils.randomNumeric(32));
			// 系统调用流水号
			req.setConsumerSeqNo(RandomStringUtils.randomNumeric(32));
			// 发送内容
			req.setContent(content);
			DMBResponse resObj = smb.sendSync(req);
			logger.debug("响应结果{}", JsonUtil.toJson(resObj));
			return JsonUtil.toJson(resObj);
	}
}
