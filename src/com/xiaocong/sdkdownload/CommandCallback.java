package com.xiaocong.sdkdownload;

public interface CommandCallback {
	void onReadLine(String line);

	void onReadError(String line);

	void onCommandFinish();
}
