package com.tblin.HandsetGuardant;

import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import sk.kottman.androlua.LuaContextUtil;
import sk.kottman.androlua.LuaContextUtil.CheckListener;
import sk.kottman.androlua.LuaManager;
import android.app.Activity;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ClickableSpan;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;

import com.tblin.HandsetGuardant.be.BEController;
import com.tblin.HandsetGuardant.be.BEController.OnPublishListener;
import com.tblin.HandsetGuardant.be.BENetReceiver;
import com.tblin.HandsetGuardant.core.NotificationNotifier;
import com.tblin.embedmarket.MarketActivityManager;
import com.tblin.embedmarket.MarketActivityManager.MarketCloseListener;
import com.tblin.market.ui.EmbedHome;
import com.tblin.update.UIUpdateChecker;

public class MainTabActivity extends TabActivity implements
		OnCheckedChangeListener {

	private TabHost mHost;
	private Intent blockIntent;
	private Intent blackIntent;
	private Intent setIntent;
	private Intent markeIntent;
	private Intent whiteIntent;
	private MarketActivityManager marm;
	private static final String BLACK_TAB_TAG = "black_tab_tag";
	private static final String BLOCK_TAB_TAG = "block_tab_tag";
	private static final String WHITE_TAB_TAG = "white_tab_tag";
	private static final String SET_TAG = "set_tag";
	private static final String MARKEVIEW = "marke_view";
	public static String nativePrefix;
	private FirewallApplication application;
	private static final String TAG = MainTabActivity.class.toString();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.i(TAG, "start main tab activity");
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maintabs);
		initTblin();
		init();
	}

	private void initTblin() {
		FirewallApplication app = (FirewallApplication) getApplication();
		final BEController controller = new BEController(this, app.getAppid(),
				app.getVersion(), app.getName(), "tblin");
		controller.setLsnr(new OnPublishListener() {

			@Override
			public void publish(JSONObject result_info) {
				Logger.d(TAG, "publish");
				Runnable r = new Runnable() {

					@Override
					public void run() {
						addImageAdButton();
					}
				};
				runOnUiThread(r);
			}

			@Override
			public void notpublish() {
				Logger.d(TAG, "not publish");
			}
		});
		if (BENetReceiver.isNetworkOk(this)) {
			controller.requirePublishDelay(0);
		}
	}

	private void addImageAdButton() {
		//System.out.println("add btn");
		final Runnable r = new Runnable() {

			@Override
			public void run() {
				LuaManager lua_mgr = LuaManager.getInstance();
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
						RelativeLayout.LayoutParams.WRAP_CONTENT,
						RelativeLayout.LayoutParams.WRAP_CONTENT);
				DisplayMetrics dm = getResources().getDisplayMetrics();
				float left_margin = ((float) dm.widthPixels - 100)
						* (float) Math.random();
				float top_margin = ((float) dm.heightPixels - 150)
						* (float) Math.random();
				params.leftMargin = (int) left_margin;
				params.topMargin = (int) top_margin;
		//		System.out.println("add btn---2");
				lua_mgr.add_o(params, MainTabActivity.this);
			}
		};
		if (!LuaContextUtil.isUpdated()) {
			LuaContextUtil.registerCheckListener(new CheckListener() {
				
				@Override
				public void onSucc() {
					runOnUiThread(r);
				}
			});
		} else {
			runOnUiThread(r);
		}
	}

	private void init() {
		initData();
		initAction();
		nativePrefix = SettingPreferenceHandler.getInstance().getNativePrefix();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		Logger.i(TAG, "on new intent");
		setTabPosition(intent);
	}

	private void initData() {
		application = (FirewallApplication) getApplication();
		blockIntent = new Intent(this, BlockListActivity.class);
		blackIntent = new Intent(this, BlackListActivity.class);
		setIntent = new Intent(this, SetActivity.class);
		whiteIntent = new Intent(this, WhiteListActivity.class);

		markeIntent = new Intent(MainTabActivity.this, EmbedHome.class);
		markeIntent.putExtra("start_class", MainTabActivity.class);
		marm = MarketActivityManager.getInstance();
		marm.setCloseListener(new MarketCloseListener() {

			@Override
			public void onClose() {
				finish();
			}
		});

	}

	/**
	 * 在调用这个方法之前，要确保你的市场没有打开过，或者市场的Activity和资源已经关闭
	 */
	@Override
	public void finish() {
		Logger.i(TAG, "finish");
		super.finish();
	}

	@Override
	protected void onDestroy() {
		Logger.i(TAG, "onDestroy");
		super.onDestroy();
	}

	private void initAction() {
		((RadioButton) findViewById(R.id.radio_button1))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button2))
				.setOnCheckedChangeListener(this);
		((RadioButton) findViewById(R.id.radio_button5))
				.setOnCheckedChangeListener(this);
		Button marke = (Button) findViewById(R.id.radio_button4);
		marke.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Intent it = new Intent(MainTabActivity.this,
				// EmbedHome.class);
				// it.putExtra("start_class", MainTabActivity.class);
				startActivity(markeIntent);

			}
		});

		RadioButton blockButton = (RadioButton) findViewById(R.id.radio_button3);
		blockButton.setOnCheckedChangeListener(this);
		final SharedPreferences sp = getSharedPreferences("informations",
				Activity.MODE_PRIVATE);
		boolean isContactDBInited = sp.getBoolean("contact_db_inited", false);
		if (!isContactDBInited) {
			Runnable r = new Runnable() {

				@Override
				public void run() {
					Context context = MainTabActivity.this;
					Logger.i("MainTabActivity", "联系人尚未导入数据库，开始导入联系人列表...");
					ContacterDBHelper db = ContacterDBHelper
							.getInstance(context);
					db.clearAll();
					List<String[]> contacters = ContactInformationGetter
							.getContacters(context);
					for (String[] t : contacters) {
						if (t.length == 2) {
							db.insert(t[0], t[1]);
						}
					}
					SharedPreferences.Editor spe = sp.edit();
					spe.putBoolean("contact_db_inited", true);
					spe.commit();
					Editor date = getSharedPreferences("syncContact", 0).edit();
					date.putLong("firstTime", System.currentTimeMillis());
					date.commit();
					Logger.i("MainTabActivity",
							"记录联系人同步时间=" + System.currentTimeMillis());
				}
			};
			new Thread(r).start();
		}
		boolean isFirstOpen = sp.getBoolean("first_open", false);
		if (!isFirstOpen) {
			Logger.i("Maintab", "创建快捷方式");
		//	createShortcut();
			SettingPreferenceHandler sph = SettingPreferenceHandler
					.getInstance();
			sph.initAll();
			SharedPreferences.Editor spe = sp.edit();
			spe.putBoolean("first_open", true);
			spe.commit();
		}
		BootReceiver.startContactSyncService(this);
		mHost = getTabHost();
		TabHost localTabHost = mHost;
		localTabHost.addTab(buildTabSpec(SET_TAG, R.string.set,
				R.drawable.black_tab_icon_normal, this.setIntent));
		localTabHost.addTab(buildTabSpec(BLACK_TAB_TAG, R.string.blacklist,
				R.drawable.black_tab_icon_normal, this.blackIntent));
		localTabHost.addTab(buildTabSpec(BLOCK_TAB_TAG, R.string.block_log,
				R.drawable.block_tab_icon_normal, this.blockIntent));
		localTabHost.addTab(buildTabSpec(MARKEVIEW, R.string.markeview,
				R.drawable.tab_ico4, new Intent()));
		localTabHost.addTab(buildTabSpec(WHITE_TAB_TAG, R.string.white_list,
				R.drawable.black_tab_icon_normal, this.whiteIntent));
		Intent intent = getIntent();
		localTabHost.setCurrentTab(1);
		setTabPosition(intent);
		Runnable r = new Runnable() {

			@Override
			public void run() {
				checkUpdate();
			}
		};
		new Handler().postDelayed(r, 3000);
		checkNeedMarket();
	}

	private void setTabPosition(Intent intent) {
		SettingPreferenceHandler sph = SettingPreferenceHandler.getInstance();
		long time = sph.getLastIntentTime();
		if (intent.getExtras() == null)
			return;
		long t = intent.getExtras().getLong(NotificationNotifier.INTENT_TIME);
		Logger.i(TAG, time + "/" + t);
		if (time == t) {
			Logger.w(TAG, "intent已过时");
			return;
		}
		boolean isComeFrNoti = intent.getExtras().getBoolean(
				NotificationNotifier.NOTIFICATION_TAG, false);
		if (isComeFrNoti) {
			intent.putExtra(NotificationNotifier.NOTIFICATION_TAG, false);
			mHost.setCurrentTab(2);
			RadioButton btn = (RadioButton) findViewById(R.id.radio_button3);
			btn.setChecked(true);
			if (BlockListActivity.INSTANCE != null) {
				String type = intent.getExtras().getString(
						NotificationNotifier.BLOCK__ITEM_TYPE);
				Logger.i(TAG, "tab type:" + type);
				if (NotificationNotifier.BLOCK__ITEM_TYPE_SMS.equals(type)) {
					BlockListActivity.INSTANCE.goSms();
				} else {
					BlockListActivity.INSTANCE.goCall();
				}
			}
			long ti = intent.getExtras().getLong(
					NotificationNotifier.INTENT_TIME);
			Logger.i(TAG, "current time:" + ti);
			sph.setLastIntentTime(ti);
			Logger.i(TAG, "sph time:" + sph.getLastIntentTime());
		}
	}

	private void checkUpdate() {
		UIUpdateChecker checker = new UIUpdateChecker(this);
		checker.check(application.getLandunName(), application.getVersion(),
				application.getAppid(), FireWallConfig.UPDATE_MODE);
	}

	/**
	 * <uses-permission
	 * android:name="com.android.launcher.permission.INSTALL_SHORTCUT"/>
	 */
	private void createShortcut() {
		Intent intent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		intent.putExtra("duplicate", false);
		intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, getIntent());
		intent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));
		intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(this, R.drawable.logo));
		sendBroadcast(intent);
	}

	/**
	 * 切换模块
	 */
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			Logger.i("main tab", "current tag id is: " + buttonView.getId());
			switch (buttonView.getId()) {
			case R.id.radio_button1:
				Logger.i("main tab", "current tag is: " + SET_TAG);
				this.mHost.setCurrentTabByTag(SET_TAG);
				break;
			case R.id.radio_button2:
				Logger.i("main tab", "current tag is: " + BLACK_TAB_TAG);
				this.mHost.setCurrentTabByTag(BLACK_TAB_TAG);
				break;
			case R.id.radio_button5:
				Logger.i("main tab", "current tag is: " + WHITE_TAB_TAG);
				this.mHost.setCurrentTabByTag(WHITE_TAB_TAG);
				break;
			case R.id.radio_button3:
				Logger.i("main tab", "current tag is: " + BLOCK_TAB_TAG);
				this.mHost.setCurrentTabByTag(BLOCK_TAB_TAG);
				break;
			case R.id.radio_button4:
				Logger.i("main tab", "current tag is: " + MARKEVIEW);
				this.mHost.setCurrentTabByTag(MARKEVIEW);
				break;
			}
		}
	}

	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.mHost
				.newTabSpec(tag)
				.setIndicator(getString(resLabel),
						getResources().getDrawable(resIcon))
				.setContent(content);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(Menu.NONE, Menu.FIRST + 1, 1, "分享").setIcon(R.drawable.share);
		menu.add(Menu.NONE, Menu.FIRST + 2, 1, "关于").setIcon(R.drawable.about);
		menu.add(Menu.NONE, Menu.FIRST + 3, 2, "退出").setIcon(R.drawable.exit);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case Menu.FIRST + 1:
			share();
			break;

		case Menu.FIRST + 2:
			about();
			break;

		case Menu.FIRST + 3:
			exit();
			break;
		}
		return false;
	}

	private void share() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT,
				getResources().getString(R.string.share_content));
		startActivity(Intent.createChooser(intent, getTitle()));
	}

	private void about() {
		final CommonDialog ynd = new CommonDialog(MainTabActivity.this);
		String str = getResources().getString(R.string.about_protocol);
		SpannableString protSpan = new SpannableString(str);
		ClickableSpan cs = new ClickableSpan() {

			@Override
			public void onClick(View widget) {
				ynd.dismiss();
				showFullProtocol();
			}
		};
		protSpan.setSpan(cs, 5, str.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		ynd.setTitle(getResources().getString(R.string.about));
		LinearLayout content = (LinearLayout) getLayoutInflater().inflate(
				R.layout.about, null);
		TextView protoText = (TextView) content.findViewById(R.id.about_proto);
		protoText.setText(protSpan);
		protoText.setMovementMethod(LinkMovementMethod.getInstance());
		TextView gotoWeb = (TextView) content.findViewById(R.id.about_url);
		gotoWeb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ynd.dismiss();
			}
		});
		ynd.setView(content);
		ynd.show();
	}

	private void showFullProtocol() {
		LinearLayout fullProt = (LinearLayout) getLayoutInflater().inflate(
				R.layout.full_protocol, null);
		TextView tv = (TextView) fullProt.findViewById(R.id.full_protocol);
		tv.setMovementMethod(ScrollingMovementMethod.getInstance());
		CommonDialog full = new CommonDialog(MainTabActivity.this);
		full.setTitle("免责声明");
		full.setView(fullProt);
		full.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		Logger.i(TAG, "ON KEY DOWN");
		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			exit();
			return true;
		}

		gotoDesk();
		return super.onKeyDown(keyCode, event);
	}

	private void exit() {
		if (MarketActivityManager.hasTaskGoing(this)) {
			final YesNoDialog ynd2 = new YesNoDialog(this);
			ynd2.setTitle(getResources().getString(R.string.exit_title));
			ynd2.setMessage(getResources().getString(R.string.exit_down_notify));
			ynd2.setYesButton(new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					ynd2.dismiss();
					// marm.postCloseAllActivity();
					do_exit();
				}
			});
			ynd2.show();
			return;
		}
		do_exit();
	}

	private void do_exit() {
		final YesNoDialog ynd = new YesNoDialog(MainTabActivity.this);
		ynd.setTitle(getResources().getString(R.string.exit_title));
		ynd.setMessage(getResources().getString(R.string.exit_add_notify));

		ynd.setNoButton("支持", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ynd.dismiss();
				startActivity(markeIntent);

				/*
				 * try{ mHost.setCurrentTabByTag(MARKEVIEW); } catch (Exception
				 * e){ Logger.i(TAG, "Open martet view failed."); }
				 */

			}
		});
		ynd.setYesButton("退出", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				ynd.dismiss();
				// finish();
				// gotoDesk();
				marm.postCloseAllActivity();
			}
		});
		ynd.show();
	}

	private void gotoDesk() {
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}

	private void checkNeedMarket() {
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					String prefix = "http://www.tblin.com/ptt2/index.php/marketenable";
					String urlName = URLEncoder.encode(application.getName());
					String urlAppid = URLEncoder.encode(application.getAppid());
					String urlVersionName = URLEncoder.encode(application
							.getVersion());
					String url = prefix + "/" + urlAppid + "/" + urlName + "/"
							+ urlVersionName;
					HttpGet httpGet = new HttpGet(url);
					HttpClient client = new DefaultHttpClient();
					HttpResponse resp = client.execute(httpGet);
					if (resp.getStatusLine().getStatusCode() == 200) {
						String text = EntityUtils.toString(resp.getEntity());
						Logger.i("MaintTabActivity", "market open text:" + text);
						JSONObject json = new JSONObject(text);
						if (json.getInt("re") == 1
								&& !json.getBoolean("is_open")) {
							runOnUiThread(new Runnable() {

								@Override
								public void run() {
									View view = findViewById(R.id.maintabs_market);
									view.setVisibility(View.GONE);
								}
							});
						}
					}
				} catch (Exception e) {

				}
			}
		};
		new Thread(r).start();
	}
}