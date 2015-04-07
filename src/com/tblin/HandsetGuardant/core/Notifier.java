package com.tblin.HandsetGuardant.core;

import android.content.Context;

public interface Notifier {

	void notifyUserIfNeed(Context context, String msg, String blockItemType);

}
