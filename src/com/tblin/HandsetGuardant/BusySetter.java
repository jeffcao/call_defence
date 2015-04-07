package com.tblin.HandsetGuardant;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class BusySetter {

	public static final String[] TRANSFERS = { "tel:**67*13800000000%23",
		"tel:**67*13999999999%23", "tel:**67*13111111111%23",
		"tel:%23%2367%23" };
	public static final String[] CT_TRANSFERS = { "tel:*901380013800",
		"tel:*9013999999999", "tel:*9013111111111", "tel:*900" };
	public static final int TYPE_ERROR_NUMBER = 0; // 空号
	public static final int TYPE_SIM_DIE = 1; // 停机
	public static final int TYPE_MOBILE_CLOSE = 2; // 关机
	public static final int TYPE_NORMAL = 3; // 忙音
	public static final int TYPE_UNKNOW = -1; // 未知
	public static final String TYPE_ERROR_NUMBER_TEXT = "提示空号";
	public static final String TYPE_SIM_DIE_TEXT = "提示停机";
	public static final String TYPE_MOBILE_CLOSE_TEXT = "提示关机";
	public static final String TYPE_NORMAL_TEXT = "提示忙音";

	/**
	 * 以下方法需要有android.permission.CALL_PHONE权限
	 */

	public static void closeBusyTransfer(Context context) {
		setBusy(TYPE_NORMAL, context);
	}

	public static void setBusy(int type, Context context) {
		if (type < 0 || type > 3) {
			return;
		}
		String data = null;
		int operator = OperatorTypeGetter.getOperatorType(context);
		if (operator == OperatorTypeGetter.UNKNOW) {
			return;
		} else if (operator == OperatorTypeGetter.CT) {
			data = CT_TRANSFERS[type];
		} else {
			data = TRANSFERS[type];
		}
		Intent intent = new Intent(Intent.ACTION_CALL);
		intent.setData(Uri.parse(data));
		context.startActivity(intent);
	}

}
