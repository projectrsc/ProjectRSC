package org.darkquest.config;

public enum LoginStatus {
	ACCEPTED(0), ACCEPTED_ADMIN(99), INVALID_CREDENTIALS(2), ALREADY_LOGGED_IN(3),
	CLIENT_UPDATED(4), REJECTED_SESSION(5), FAILED_TO_DECODE(7), TOO_MANY_CHARS(8),
	OWNER_LOGGED_IN(9), MEMBER_ACCOUNT_NEEDED(10), TEMP_DISABLED(12), ACCOUNT_FROZEN(13),
	NEED_TO_SET_NAME(14), WORLD_FULL(15), PERM_DISABLED(16), LOGIN_ATTEMPTS_EXCEEDED(17);

	private byte code;

	LoginStatus(int code) {
		this.code = (byte) code;
	}
	
	public byte getCode() {
		return code;
	}
	
}