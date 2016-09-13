package cn.sunline.lttsts;

public final class LttsException extends RuntimeException {

	private static final long serialVersionUID = 5109872190040926890L;
	// 错误码
	private String errCode;
	// 错误信息
	private String errMsg;

	public LttsException() {
		super();
	}

	public LttsException(String errCode) {
		super(errCode);
		this.setErrCode(errCode);
	}

	public LttsException(String errCode, String errMsg) {
		super(errMsg);
		this.setErrCode(errCode);
		this.setErrMsg(errMsg);
	}

	public String getErrCode() {
		return errCode;
	}

	public void setErrCode(String errCode) {
		this.errCode = errCode;
	}

	public String getErrMsg() {
		return errMsg;
	}

	public void setErrMsg(String errMsg) {
		this.errMsg = errMsg;
	}
}
