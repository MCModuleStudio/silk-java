package org.mcmodule.silk;

public enum SilkErrors {
	NO_ERROR(0, "No error"),
	ENC_INPUT_INVALID_NO_OF_SAMPLES(-1, "Input length is not a multiplum of 10 ms, or length is longer than the packet length"),
	ENC_FS_NOT_SUPPORTED(-2, "Sampling frequency not 8000, 12000, 16000 or 24000 Hertz"),
	ENC_PACKET_SIZE_NOT_SUPPORTED(-3, "Packet size not 20, 40, 60, 80 or 100 ms"),
	ENC_PAYLOAD_BUF_TOO_SHORT(-4, "Allocated payload buffer too short"),
	ENC_INVALID_LOSS_RATE(-5, "Loss rate not between 0 and 100 percent"),
	ENC_INVALID_COMPLEXITY_SETTING(-6, "Complexity setting not valid, use 0, 1 or 2"),
	ENC_INVALID_INBAND_FEC_SETTING(-7, "Inband FEC setting not valid, use 0 or 1"),
	ENC_INVALID_DTX_SETTING(-8, "DTX setting not valid, use 0 or 1"),
	ENC_INTERNAL_ERROR(-9, "Internal encoder error"),
	DEC_INVALID_SAMPLING_FREQUENCY(-10, "Output sampling frequency lower than internal decoded sampling frequency"),
	DEC_PAYLOAD_TOO_LARGE(-11, "Payload size exceeded the maximum allowed 1024 bytes"),
	DEC_PAYLOAD_ERROR(-12, "Payload has bit errors"),
	UNKNOWN_ERROR("Unknown error");

	private final int code;
	private final String reason;

	SilkErrors(int code, String reason) {
		LookupTable.TABLE[-(this.code = code)] = this;
		this.reason = reason;
	}

	SilkErrors(String reason) {
		this.code = 1;
		this.reason = reason;
	}

	public int getCode() {
		return this.code;
	}

	public String getReason() {
		return this.reason;
	}
	
	public static SilkErrors getErrorType(int errno) {
		if (errno > 0 || errno < -12) {
			return UNKNOWN_ERROR;
		}
		return LookupTable.TABLE[-errno];
	}
	
	static class LookupTable {
		private static final SilkErrors[] TABLE = new SilkErrors[13];
	}
}
