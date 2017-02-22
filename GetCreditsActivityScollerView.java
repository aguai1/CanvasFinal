/**
 * GetCreditsActivity.java
 * Copyright 2013 Dingtone Inc.
 *
 * All right reserved.
 *
 * Created on 2013-9-24 17:11:03
 */

package me.dingtone.app.im.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.Html;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import de.greenrobot.event.EventBus;
import me.dingtone.app.im.ad.AdConfig;
import me.dingtone.app.im.ad.AdConfig.VIDEOLISTTYPE;
import me.dingtone.app.im.ad.AdManager;
import me.dingtone.app.im.ad.AdManager.InterstitialManangerListener;
import me.dingtone.app.im.ad.AdMobVpnManager;
import me.dingtone.app.im.ad.KiipManager;
import me.dingtone.app.im.ad.KiipManager.KiipEventListener;
import me.dingtone.app.im.ad.MediabrixManager;
import me.dingtone.app.im.ad.MediabrixManager.MediabrixManagerListener;
import me.dingtone.app.im.ad.VideoAdManager;
import me.dingtone.app.im.ad.videoffer.dialog.VideoOfferManager;
import me.dingtone.app.im.adinterface.AdConst;
import me.dingtone.app.im.adinterface.AdNotifier;
import me.dingtone.app.im.adinterface.AdProviderType;
import me.dingtone.app.im.adinterface.InterstitialEventListener;
import me.dingtone.app.im.adinterface.NativeAd;
import me.dingtone.app.im.adinterface.NativeAdEventListener;
import me.dingtone.app.im.antifraud.AntiFraudMgr;
import me.dingtone.app.im.appwall.NativeADOfferProvider;
import me.dingtone.app.im.core.R;
import me.dingtone.app.im.datatype.BOOL;
import me.dingtone.app.im.datatype.BannerInfo;
import me.dingtone.app.im.datatype.DTGetAdListResponse;
import me.dingtone.app.im.datatype.DTGetCheckinLevelResponse;
import me.dingtone.app.im.datatype.DTGetDoDailyCheckinResponse;
import me.dingtone.app.im.datatype.DTGetVirtualProductListResponse;
import me.dingtone.app.im.datatype.DTQueryHasMadeCallResponse;
import me.dingtone.app.im.datatype.DTQueryHasPurchasedCreditsResponse;
import me.dingtone.app.im.datatype.OfferTip;
import me.dingtone.app.im.datatype.enums.DTRESTCALL_TYPE;
import me.dingtone.app.im.dialog.CustomAlertDialog;
import me.dingtone.app.im.dialog.FeelingLuckyAdDialog;
import me.dingtone.app.im.dialog.FeelingLuckyDialog;
import me.dingtone.app.im.dialog.FeelingLuckyDialogListener;
import me.dingtone.app.im.dialog.OfferTipDialog;
import me.dingtone.app.im.dialog.WaitingAdListener;
import me.dingtone.app.im.event.AdShowEvent;
import me.dingtone.app.im.event.FeelingLuckyAdListEvent;
import me.dingtone.app.im.event.RefreshBalanceForWatchVideoEvent;
import me.dingtone.app.im.event.VPNDisconnectedEvent;
import me.dingtone.app.im.event.VPNOnGetCreditsEvent;
import me.dingtone.app.im.event.VPNconnectedEvent;
import me.dingtone.app.im.event.VpnDialogOpenEvent;
import me.dingtone.app.im.event.VpnTimerEvent;
import me.dingtone.app.im.headimg.FacebookHeadImageFetcher;
import me.dingtone.app.im.headimg.FacebookHeadImageFetcher.Shape;
import me.dingtone.app.im.log.DTLog;
import me.dingtone.app.im.lottery.events.LotteryBackEvent;
import me.dingtone.app.im.lottery.events.LotterySettingLoadedEvent;
import me.dingtone.app.im.lottery.models.LotteryConfig;
import me.dingtone.app.im.lottery.models.LotterySyncInfo;
import me.dingtone.app.im.lottery.views.activitys.LotteryActivity;
import me.dingtone.app.im.manager.AppConfig;
import me.dingtone.app.im.manager.DTApplication;
import me.dingtone.app.im.manager.DtAppInfo;
import me.dingtone.app.im.manager.Event;
import me.dingtone.app.im.manager.UIMgr;
import me.dingtone.app.im.manager.coupon.CheckinManager;
import me.dingtone.app.im.manager.coupon.CheckinManager.UIJobForCheckinLevelResponse;
import me.dingtone.app.im.manager.coupon.CheckinManager.UIJobForCheckinResponse;
import me.dingtone.app.im.manager.coupon.CheckinPushManager;
import me.dingtone.app.im.superofferwall.DTSuperOfferWallObject;
import me.dingtone.app.im.superofferwall.OfferTipManager;
import me.dingtone.app.im.superofferwall.OfferTipManager.OfferTipListener;
import me.dingtone.app.im.superofferwall.OfferTipManager.OfferWallTipConfigListener;
import me.dingtone.app.im.superofferwall.SuperOfferwallMgr;
import me.dingtone.app.im.tp.TpClient;
import me.dingtone.app.im.tracker.ActionType;
import me.dingtone.app.im.tracker.ActionView;
import me.dingtone.app.im.tracker.CategoryType;
import me.dingtone.app.im.tracker.DTTracker;
import me.dingtone.app.im.tracker.PersonalActionType;
import me.dingtone.app.im.tracker.PersonalLabelType;
import me.dingtone.app.im.uikit.DTAlert;
import me.dingtone.app.im.userwakeup.UserWakeUpNotificationType;
import me.dingtone.app.im.userwakeup.UserWakeUpPushReceiver;
import me.dingtone.app.im.util.Assert;
import me.dingtone.app.im.util.BroadcastAction;
import me.dingtone.app.im.util.CommonUtil;
import me.dingtone.app.im.util.DTSystemContext;
import me.dingtone.app.im.util.DTTimer;
import me.dingtone.app.im.util.DTTimer.DTTimerListener;
import me.dingtone.app.im.util.DialogUtil;
import me.dingtone.app.im.util.DtUtil;
import me.dingtone.app.im.util.Global;
import me.dingtone.app.im.util.LocationHelper.LocationHelperListener;
import me.dingtone.app.im.util.PingNetworkType;
import me.dingtone.app.im.util.SharedPreferencesUtil;
import me.dingtone.app.im.util.SharedPreferencesUtilCallFrequency;
import me.dingtone.app.im.util.SharedPreferencesUtilCheckin;
import me.dingtone.app.im.util.SharedPreferencesUtilUserWakeUp;
import me.dingtone.app.im.util.ToolsForAll;
import me.dingtone.app.im.util.ToolsForCallRate;
import me.dingtone.app.im.util.ToolsForConnect;
import me.dingtone.app.im.util.ToolsForTime;
import me.dingtone.app.im.util.VPNChecker;
import me.dingtone.app.im.view.CustomProgressDialog;
import me.dingtone.app.im.view.ad.ADBanner;
import me.dingtone.app.im.view.guide.GuideContainer;
import me.dingtone.app.im.view.guide.GuideView;
//import me.dingtone.app.vpn.http.bean.AdReportBean;
//import me.dingtone.app.vpn.utils.SharedPreferencesUtilForVpn;


/**
 * @author Maris
 *
 */
public class GetCreditsActivity extends DTActivity implements OnClickListener, Event,
		OfferWallTipConfigListener {

	//切换拆分模式和非拆分模式
	public static final boolean IS_GETCREDITS_SPLIT = true;
	public static final String TAG_LOCAL_PUSH_PAYPAL_PENDING = "paypal_pending";
	private static final int REQUESTCOADE_CHECK_IN_ACTIVITY = 123;
	private static final int REQUESTCOADE_FEELING_LUCKY_ACTIVITY = 124;

	private FeelingLuckyAdDialog mFeelingLuckyLoadingAdDialog;
	private boolean isADBannerShow;


	public static void showSpecifiedAd(Context context, int adType) {

		Intent intent = new Intent(context, GetCreditsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(GetCreditsActivity.INTENT_AD_TYPE, adType);
		context.startActivity(intent);
	}

	public static void showSurveyAd(Context context, int adType) {

		Intent intent = new Intent(context, GetCreditsActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(GetCreditsActivity.INTENT_AD_TYPE, adType);
		context.startActivity(intent);
	}

	private void backAction() {
		DTTracker.getInstance().sendMyEvent(PersonalActionType.GET_CREIDTS,
				PersonalLabelType.GET_CREDITS_BACK_BTN, 0L);
		AdManager.getInstance().resetCurrentOfferAdType();

		if (checkShowedCompleteAnOfferDialog()) {
			this.finish();
		}
		VideoAdManager.getInstance().cancelAutoPlay();
		AdManager.getInstance().cancelAllVideos();
	}

	@Override
	public void onBackPressed() {


		if (GetCreditsUtils.isDingCreditConnected()) {
			GetCreditsUtils.OnGetCreditsActivityBack(GetCreditsActivity.this);
		} else {
			backAction();
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.get_credits_back) {
			if (GetCreditsUtils.isDingCreditConnected()) {
				GetCreditsUtils.OnGetCreditsActivityBack(GetCreditsActivity.this);
			} else {
				backAction();
			}
		} else if (id == R.id.get_credits_help) {
//			//Test obo 测试offerdialog
//			if (DTLog.DBG && true) {
//				if (VideoOfferManager.getInstance().canShowOffer()) {
//					VideoOfferManager.getInstance().showOfferDialog(this);
//				}
//				return;
//			}

			DTTracker.getInstance().sendMyEvent(PersonalActionType.GET_CREIDTS,
					PersonalLabelType.GET_CREDITS_HELP_BTN, 0L);
			goToHelpWebView();
		} /*else if (id == R.id.get_credits_tip) {
			DTTracker.getInstance().sendMyEvent(PersonalActionType.GET_CREIDTS,
					PersonalLabelType.GET_CREDITS_EASY_WAY, 0L);
			if (activityStatus == START) {
				DialogUtil.ShowDialogForGetCreditsTip(this);
			}
		}*/ else if (id == R.id.get_credits_video) {
            /**START "用户点Watch Videos远远多于点Complete an Offer……" 问题的 A/B Test 观看视频**/
            if(mTestType==AppConfig.TEST_TYPE_A){
                DTLog.d(tag, "earn credit a test click video");
                DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_PAGE_CLICK_WATCH_VIDEO_A,null,0);
            }else if(mTestType==AppConfig.TEST_TYPE_B){
                DTLog.d(tag, "earn credit b test click video");
                DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_PAGE_CLICK_WATCH_VIDEO_B,null,0);
            }
            /**END **/

			adShowPlace = ADSHOWPLACE.WATCH_VIDEO;


			int canShowVideo = GetCreditsUtils.GetCanShowVideo(DTApplication.getInstance());
			DTLog.i(tag, "click video & canshowvideo : " + canShowVideo);
			Crashlytics.log( tag + " watchvideo " + CommonUtil.getHeapUsageInfo());

			// 开始本次视频播放
			VideoAdManager.getInstance().clickVideo();
			//为1显示 之前的逻辑，为0 显示新的逻辑
			if (canShowVideo == 1) {
				ShowNativeClickVideo();
			} else {
				ShowNewClickVideo();
			}

			saveClickGetVideoCreditButtonTime();

			if (!SharedPreferencesUtilUserWakeUp.hasClickedWatchVideo()) {
				SharedPreferencesUtilUserWakeUp.setHasClickedWatchVideo();
			}

        } else if (id == R.id.get_credits_offer) {
            /**START "用户点Watch Videos远远多于点Complete an Offer……" 问题的 A/B Test 做任务**/
            if(mTestType==AppConfig.TEST_TYPE_A){
                DTLog.d(tag, "earn credit a test click offer");
                DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_PAGE_CLICK_COMPLETE_OFFERS_A,null,0);
            }else if(mTestType==AppConfig.TEST_TYPE_B){
                DTLog.d(tag, "earn credit b test click offer");
                DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_PAGE_CLICK_COMPLETE_OFFERS_B,null,0);
            }
            /**END **/
            if (ToolsForConnect.CheckNetworkStatusForLogined(this)) {
                clickOfferWallBtn();
                saveClickOtherCreditButtonTime();
            }

			if (!SharedPreferencesUtilUserWakeUp.hasClickedCompletedOffer()) {
				SharedPreferencesUtilUserWakeUp.setHasClickedCompleteOffer();
			}
        } /*else if (id == R.id.get_credits_order_error_btn) {
            DTTracker.getInstance().sendEvent(CategoryType.GET_CREDITS,
					ActionType.GET_CREDITS_TO_REPORT, null, 0);
			DTLog.i(tag, "onClick...get_credits_order_error_btn");
			ToolsForEmail.SendEmailForGetCredits(this, BillingMgr.getInstance()
					.getBillingDBList());
		}*/ else if (id == R.id.activity_earn_free_credits_daily_checkin) {
			/*if (checkInAnimation != null) {
				checkInAnimation.cancel();
			}
			if (checkInHandImg != null) {
				checkInHandImg.clearAnimation();
			}*/
			DtAppInfo.getInstance().setClickedCheckInIconTime(new Date().getTime());
			startActivityForResult(new Intent(this, CheckinActivity.class),REQUESTCOADE_CHECK_IN_ACTIVITY);
			saveClickOtherCreditButtonTime();
			DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_CLICK_DAILY_CHECKIN,null,0);
		} else if (id == R.id.activity_earn_free_credits_feeling_lucky) {
			Crashlytics.log( tag + " feelinglucky " + CommonUtil.getHeapUsageInfo());
			handleOnClickingFeelingLucky();
			saveClickOtherCreditButtonTime();
            SharedPreferencesUtil.setLastFeelingLuckyClickTime(ToolsForTime.getCurrentTimeAsString());
            setUpHandIndicatorAnimation();
			DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_CLICK_FEELING_LUCKY,null,0);
		} else if (id == R.id.activity_earn_free_credits_share) {
			startActivity(new Intent(this, InviteCreidtActivity.class));
			DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_CLICK_INVITE_FRIEND,null,0);
			if (!SharedPreferencesUtilUserWakeUp.hasClickedInvitedFriendToGetCredit()) {
				SharedPreferencesUtilUserWakeUp.setHasClickedInviteFriendToGetCredit();
			}
		} else if (id == R.id.activity_earn_free_credits_more_offers) {
			if (ToolsForConnect.CheckHasNetwork(this)
					&& !DtAppInfo.getInstance().isChinaVersion()) {
				startActivity(new Intent(this, MoreOffersAndSurveysActivity.class));
				DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,
						ActionType.ENTER_MORE_OFFERS_AND_SURVEY, null, 0);

			}
			DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_CLICK_MORE_OFFERS,null,0);
		}else if (id == R.id.get_credits_app_wall){
			saveClickOtherCreditButtonTime();
			Intent intent = new Intent(this ,AppWallEnterActivity.class);
			intent.putExtra(AppWallEnterActivity.COME_FROM_CHAT,false);
			this.startActivity(intent);
			SharedPreferencesUtil.setLastKazooClickTime(ToolsForTime.getCurrentTimeAsString());
			DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_CLICK_KAZOO_LINK,null,0);
		} else if(id == R.id.activity_earn_free_credits_daily_lottery || id == R.id.ll_lottery_tip) {
			//入口关闭了不显示彩票入口
			//但如果彩票流程没走完的话则继续可以进入
			if (!AppConfig.getInstance().isShowDailyLottery() && !LotteryConfig.getInstance().isNotFinishLottery()) {
				return;
			}

			// 跳转到彩票界面
			if ( LotteryActivity.startLotteryActivity(this,false) ) {
				removeAdListeners();
			}
			mIvDailyLotteryNew.setVisibility(View.GONE);
			LotteryConfig.getInstance().setHaveEnteredLottery(true);
			LotteryConfig.getInstance().save();
		}
	}

    private void ShowNewClickVideo() {
		if (GetCreditsUtils.isDingCreditConnected()) {
			DTTracker.getInstance().sendEventV2(CategoryType.VPN2, ActionType.VPN_CLICK_VIDEO_SHOW_ADMOB, null, 0);
			GetCreditsUtils.addAdReport(GetCreditsUtils.AD_TYPE_FULLS, GetCreditsUtils.AD_EVT_CLICK, "admob_vpn", null, null, null, null);
//            AdManager.getInstance().showAdmobInterstitial(GetCreditsActivity.this, new InterstitialManangerListener() {
//                @Override
//                public void onInterstitialSuccessful(int AdCode, int type) {
//                    DTTracker.getInstance().sendEventV2(CategoryType.VPN2, ActionType.VPN_SHOW_ADMOB_SHOW_ADMOB_SUCCESS,null,0);
//                    DTLog.i(tag, "showAdmobInterstitial onInterstitialSuccessful");
//                }
//
//                @Override
//                public void onInterstitialFailed() {
//                    DTLog.i(tag,"showAdmobInterstitial onInterstitialFailed");
//                    DTTracker.getInstance().sendEventV2(CategoryType.VPN2,ActionType.VPN_SHOW_ADMOB_SHOW_ADMOB_FAILUE,null,0);
//                    Intent intent=new Intent(GetCreditsActivity.this,SuperofferwallActivity.class);
//                    startActivity(intent);
//                }
//            });
			AdMobVpnManager.getInstance().showAdmob(GetCreditsActivity.this, AdProviderType.AD_PROVIDER_TYPE_ADMOB);
			DTLog.i(tag, "click video & dingvpn is connected");


		} else {
			ShowNativeClickVideo();
		}
	}

	private void ShowNativeClickVideo() {
		DTLog.i(tag, "click video & dingvpn is disconnected");
		DTTracker.getInstance().sendMyEvent(PersonalActionType.GET_CREIDTS,
				PersonalLabelType.GET_CREDITS_WATCH_VEDIO, 0L);
		if (watchVideoProgressDialog != null) {
			DTLog.w(tag, "The progress dialog is showing");
			return;
		}
		DTTracker.getInstance().sendEvent(CategoryType.GET_CREDITS,
				ActionType.GET_CREDITS_TO_VIDEO, null, 0);

		if (VPNChecker.getInsance().isLocalCountryVPNConnected()) {
			DTTracker.getInstance().sendEventV2(CategoryType.VPN_TIP, ActionType.VPN_GET_CREDIT_TO_VIDEO, null, 0);
		}
		if (GetCreditsUtils.isDingCreditConnected()) {
			DTTracker.getInstance().sendEventV2(CategoryType.VPN2, ActionType.VPN_GET_CREDIT_TO_VIDEO, null, 0);
		}
		DTLog.i(tag, "onClick...get_credits_video");
		if (ToolsForConnect.CheckNetworkStatusForLogined(this)) {
			if (isNetworkWifi) {
				stopAnimateWatchVideoImage();

				if (AdManager.getInstance().isVideoAvailable()
						&& !AdManager.getInstance().hasCachedVideoShowed()) {
					DTLog.i(tag, "video has cached and did not show, show now.");
					AdManager.getInstance().startVideoByAdType(AdManager.getInstance().getCachedVideo());
				} else {
					VideoOfferManager.getInstance().requestOffers();
					AdManager.getInstance().startAdVideo();
					showVideoLoadingDialog();
				}
			} else {
//				showKiipAd();
				showEndAd();
			}
		}
	}



	public static final String INTENT_JSON_ACTION = "jsonAction";
	public static final String INTENT_MIN_CREDIT = "Credit";
	public static final String INTENT_AD_TYPE = "adType";  // int


	public static final String TAG_SECRETARY = "secretary_watch";
	public static final String TAG_SECRETARY_VEDIO = "secretary_watch_vedio";
	public static final String TAG_SECRETARY_AD = "secretary_watch_ad";

	public static final String EXTRA_FROM = "extra_from_screen";
	public static final String EXTRA_FROM_CHECKIN = "checkin";
	public static final String EXTRA_FROM_SECRETARY = "secretary";
	public static final String EXTRA_FROM_BOSSPUSH = "bosspush";
	public static final String EXTRA_FROM_LOTTERY = "lottery";
	public static final String INTENT_SHOW_AD_TAPJOY_FROM_BOSS = "show_tapjoy_ad_from_boss";

	private String mSecretaryOper;

	public static final String INTENT_EXTRA_CREDIT_COUNT = "credits";
	public static final String INTENT_EXTRA_GIFT_SENDER_NAME = "name";

	private final int PAYPAL_REQUEST_CODE = 111;
	private static int Help_RequestCode = 112;

	private static String tag = "GetCreditsActivity";

	private int activityStatus = START;

	public static final int START = 0;
	public static final int DESTROY = 1;

	private LinearLayout backBtn, helpBtn;
	private TextView myBalanceText; /*tipText,*//* mFeelingLuckyText*/
	;
	private LinearLayout videoBtn, offerBtn;
	private ImageView videoIV, offerIV;
	private TextView videoTV, offerTV;
	private CustomProgressDialog watchVideoProgressDialog;
	// timer
	private DTTimer watchVideoTimer, creditsRefreshTimer ,refreshBalanceTimer;

	private DTGetVirtualProductListResponse mProductListInfo = null;
	//checkin
	/*private ImageView checkInHandImg;
	private LinearLayout checkinLayout;
	private TranslateAnimation checkInAnimation;*/

	private RelativeLayout checkinLayout;
	private RelativeLayout mFeelingLuckyText;
	private RelativeLayout mMoreOfferAndSurveys;
	// Share
	private RelativeLayout mShareLayout;

    // Hand Indicator
    private View checkinIndicator;
    private View feelingLuckyIndicator;
    private View kazoolinkIndicator;
    private AnimationSet animBlink;

	//no video dialog
	private Dialog noVideoDialog;

	// offer item
	private LinearLayout offerLayout;
	private ImageView offerItemPhoto;
	private TextView offerItemTitle, offerItemText, offerItemCredits;
	private DTSuperOfferWallObject bannerOfferItem;
	private Timer mRefreshBannerTimer;
	private ADBanner mADBanner;
	private DTActivity mActivity;


	private InterstitialManangerListener mInterstitialListener;

	private NativeAdEventResultListener mNativeAdEventListener;


	//private TextView mEarn20CreditsInviteFriendTV = null;


	private OfferTipListener mOfferTipListener = null;

	private DTTimer mCheckVideoAvailabilityTimer = null;

	private boolean isNetworkWifi = false;

	private LocationHelperListener mLocationHelpListener;


	private boolean isQueryPurchaseFromGift = false;


	private long mClickGetVideoCreditButtonTime = 0;

	private long mClickOtherCreditButtonTime = 0;

	private boolean isVideoCreditToTheAccount = false;

	private Dialog mCompleteAnOfferDialog;

	//private RelativeLayout inviteFriendsLayout;

	private View appWallLayout ,divideForAppWall ,mDividerForLottery;

	//Lottery start
	private RelativeLayout mDailyLotteryLayout;

	private ImageView mIvDailyLotteryNew;

	private boolean isShowAdTip = false;

	private boolean isShowVPN = false;

	private CustomAlertDialog lottery12HoursDialog = null;

	// 播放指定个数视频之后 跳转到offerwall逻辑改为显示推荐offer
//	private static final int MININ_VIDEO_OFFER_PLAY_COUNT = 10;

	//Lottery end

	private DTSuperOfferWallObject mAvalibleOffer;

	private enum ADSHOWPLACE {
		WATCH_VIDEO,
		FEELING_LUCKY
	}

	private ADSHOWPLACE adShowPlace = ADSHOWPLACE.WATCH_VIDEO;

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(
					BroadcastAction.REFRESH_SUPEROFFERWALL)) {
				mHandler.sendEmptyMessage(REFRESH_BANNER_OFFER_ITEM);
			} else if (intent.getAction().equals(
					BroadcastAction.GET_BALANCE_SUCCESS)) {
				DTLog.i(tag, "mBroadcastReceiver...GET_BALANCE_SUCCESS");
				mHandler.sendEmptyMessage(REFRESH_MY_BALANCE);

			} else if (intent.getAction().equals(BroadcastAction.APP_LOGIN_SUCCESS)) {

				DTLog.i(tag, "mBroadcastReceiver...APP_LOGIN_SUCCESS");
				getAdList();
				if (!DtAppInfo.getInstance().isChinaVersion()) {

					mHandler.sendEmptyMessage(REFRESH_BANNER_OFFER_ITEM);
					SuperOfferwallMgr.getInstance().prepareSuperofferwallList();
				}


			} else if (intent.getAction().equals(BroadcastAction.APP_CONNECTED_WITH_SERVER)) {

				DTLog.d(tag, " receive connected with server notiifcaiton");
				//AdManager.getInstance().reInitTapjoy(GetCreditsActivity.this);

			}
		}
	};

	private final int REFRESH_GET_MY_BALANCE = 3;
	private final int REFRESH_MY_BALANCE = 5;
	//if no video
	private final int SHOW_NO_VIDEO = 63;
	// error
	private final int ERROR_TIMEOUT = 7;
	private final int ERROR_INVALID_RECEIPT = 8;
	// ad
	private final int REFRESH_BANNER_OFFER_ITEM = 13;
	private final int RESPONSE_QUERY_MADE_CALL = 15;
	private final int RESPONSE_QUERY_PURCHASED_CREDITS = 16;

	private final int EXCEEDQUOTA = 17;
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
				case REFRESH_BANNER_OFFER_ITEM:
					if (!DtAppInfo.getInstance().isChinaVersion()) {
						//SuperOfferwallMgr.getInstance().prepareSuperofferwallList();
						offerItemLayoutTaskForRefresh();
					}
					break;
				case REFRESH_GET_MY_BALANCE:
					requestMyBalance();
					break;
				case REFRESH_MY_BALANCE:
					setMyBalanceText();
					break;
				case ERROR_TIMEOUT:
					Toast.makeText(GetCreditsActivity.this,
							R.string.more_get_credits_error_not_connect,
							Toast.LENGTH_LONG).show();
					break;
				case ERROR_INVALID_RECEIPT:
					Toast.makeText(GetCreditsActivity.this,
							R.string.more_get_credits_error_invalid,
							Toast.LENGTH_LONG).show();
					break;
				case SHOW_NO_VIDEO:
					if (noVideoDialog != null && noVideoDialog.isShowing()) {
						noVideoDialog.dismiss();
						noVideoDialog = null;
						showSuperOfferWall();
					}
					break;

			}
		}
	};

	private boolean mIsFeelingLuckyLoadingTimeOut = false;

	private ArrayList<Integer> mEndAdTypeList;

	private int mCurrentAdTypeIndex;

	private boolean isInBlackListCloseLoadingAd;

	private LinearLayout llLotteryTip;
	private ScrollView scrollView;
	private String mFromScreen;

	NativeADOfferProvider mFBNativeOfferProvider;

	public void onCreate(Bundle savedInstanceState) {

		DTLog.i(tag, "onCreate");

		DTLog.i("Performance", "Enter in get credit UI");

		super.onCreate(savedInstanceState);
		GetCreditsUtils.IsGetCreditsActivityExits = true;

		// Init the lottery config
		LotteryConfig.getInstance().read();
		mActivity = this;

		Crashlytics.log("GetCreditActivity " + CommonUtil.getHeapUsageInfo());
		DTTracker.getInstance().sendEventV2(CategoryType.APP_WALL, ActionType.APP_WALL_ENTER_CREDITS, null, 0);

		DTTracker.getInstance().sendView(ActionView.GET_CREDITS);
		DTTracker.getInstance().sendEvent(CategoryType.GET_CREDITS, ActionType.GET_CREDITS_VIEW, null, 0);
		setContentView(R.layout.activity_earn_free_credits);
		adjustResize(this);//for statusbar translucent;
		registerReceiver(mBroadcastReceiver, new IntentFilter(BroadcastAction.REFRESH_SUPEROFFERWALL));
		registerReceiver(mBroadcastReceiver, new IntentFilter(BroadcastAction.GET_BALANCE_SUCCESS));
		registerReceiver(mBroadcastReceiver, new IntentFilter(BroadcastAction.APP_LOGIN_SUCCESS));
		registerReceiver(mBroadcastReceiver, new IntentFilter(BroadcastAction.APP_CONNECTED_WITH_SERVER));

		UIMgr.getInstance().registerActivityEvent(DTRESTCALL_TYPE.DTRESTCALL_TYPE_QUERY_HAS_MADE_CALL, this);
		UIMgr.getInstance().registerActivityEvent(DTRESTCALL_TYPE.DTRESTCALL_TYPE_GET_ADLIST, this);
		boolean isFirstGoIntoGetCreditView = DtAppInfo.getInstance()
				.isFirstGoIntoGetCreditView();
		if (isFirstGoIntoGetCreditView) {
			DtAppInfo.getInstance().setFirstGoIntoGetCreditView(false);
		}

        /**START "用户点Watch Videos远远多于点Complete an Offer……" 问题的 A/B Test 判断是哪种类型**/
        mTestType=AppConfig.getInstance().getTestType();
        if (mTestType==AppConfig.TEST_TYPE_A) {
            DTLog.d(tag, "earn credit a test");
            DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_PAGE_OPEN_A,null,0);
        } else if (mTestType==AppConfig.TEST_TYPE_B) {
            DTLog.d(tag, "earn credit b test");
            DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_PAGE_OPEN_B,null,0);
        }
        /**END**/
        // ad
        AdManager.getInstance().initAdManager(this, adNotifier);
//		getAdList();
		// UI
		initView();
		handleIntent();


		// super offer wall
		if (!DtAppInfo.getInstance().isChinaVersion()) {
			SuperOfferwallMgr.getInstance().initialize();
		}

		FacebookHeadImageFetcher.setImageShape(Shape.Rectangle);

		getUserlevel();


		DTSuperOfferWallObject offer = SuperOfferwallMgr.getInstance().getAdInHouseBannerOffer(BannerInfo.PLACEMENT_TYPE_ENTER_GET_CREDITS);
		if (offer != null) {
			isShowAdTip = AdManager.getInstance().showInHouseOffer(this, offer, BannerInfo.PLACEMENT_TYPE_ENTER_GET_CREDITS);
		} else {
			OfferTipManager.getInstance().getOfferWallTipConfig(this);
		}

		getAdList();
		if (DTSystemContext.getNetworkType() == PingNetworkType.WIFI) {
			if (AdConfig.getInstance().canShowMediabrix(VIDEOLISTTYPE.FELLING_LUCKY)) {
				if (AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_MEDIABRIX, VIDEOLISTTYPE.FELLING_LUCKY)) {
					MediabrixManager.getInstace().loadViews(DTApplication.getInstance().getCurrentActivity());
				}
			}
		}

//		queryPurchaseCreditFromServer(false);

		EventBus.getDefault().register(this);

		TpClient.getInstance().getMyBalance();

		/**
		 * 显示vpn浮窗
		 */
		///added by gaojs, ---begin---
		GetCreditsUtils.enterGetCredit(DTApplication.getInstance());
		///added by gaojs, ---begin---



//		DTApplication.getInstance().getHandler().postDelayed(new Runnable() {
//			@Override
//			public void run() {
//				/**
//				 * DingVpn的处理逻辑
//				 */
//				OfferEvent installedOE=new OfferEvent();
//				installedOE.isOpen=true;
//				installedOE.storeId="com.baidu.vpn";
//				installedOE.vpnIp="209.36.6.35";
//				DTOfferHelperUtil.saveVpnOffer(installedOE);
//			}
//		},4000);
		ViewTreeObserver viewTreeObserver = findViewById(R.id.layout_get_credits).getViewTreeObserver();
		viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
			@Override
			public void onGlobalLayout() {
				showGuide(GUIDE_TYPE.TYPE_VEDIO);
				findViewById(R.id.layout_get_credits).getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
    }

	private void changeOfferSettingOfCountry() {

		if (DtAppInfo.getInstance().isChinaVersion()) {
			videoIV.setImageResource(R.drawable.icon_getcredit_vip);
			offerIV.setImageResource(R.drawable.icon_getcredit_vip);
			videoTV.setText(R.string.more_get_credits_video_cn);
			offerTV.setText(R.string.more_get_credits_offer_cn);
			videoBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					AdManager.getInstance().showOfferWallByAdType(AdProviderType.AD_PROVIDER_TYPE_DIANLE, GetCreditsActivity.this);
				}
			});

			AdManager.getInstance().setChinaOfferWallWeightList();
			offerBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					AdManager.getInstance().showOfferWall(GetCreditsActivity.this);

				}
			});
		} else {
			isNetworkWifi = DTSystemContext.getNetworkType() == PingNetworkType.WIFI;
			if (isNetworkWifi) {
				videoTV.setText(R.string.more_get_credits_video);
			} else {
				videoTV.setText(R.string.more_get_credits_video_3g);
			}
			videoIV.setImageResource(R.drawable.icon_watch_videos);
			offerIV.setImageResource(R.drawable.icon_offer);
			offerTV.setText(R.string.more_get_credits_offer);
		}

    }

	private int mTestType = AppConfig.TEST_TYPE_A;

    /**
     * 用户点Watch Videos远远多于点Complete an Offer……" 问题的 A/B Test 动态修改布局
     */
    private void changeABTestLayout(){
        if (mTestType==AppConfig.TEST_TYPE_B) {//test B 布局切换
            DTLog.d(tag, "change abtest b layout");
            //TO-DO onDestroy 内存优化
            //改变背景
            videoBtn.setBackgroundResource(R.drawable.bg_ellipse_green);
            offerBtn.setBackgroundResource(R.drawable.bg_ellipse_blue);
            //显示底部阴影
            offerTV.setBackgroundResource(R.drawable.bg_ellipse_half_deep_blue);
            videoTV.setBackgroundResource(R.drawable.bg_ellipse_half_deep_green);
            //替换图标
            videoIV.setImageResource(R.drawable.icon_credit_video_yellow);
            offerIV.setImageResource(R.drawable.icon_credit_offer_yellow);
            //显示文字
            videoBtn.findViewById(R.id.get_credits_video_img_txt).setVisibility(View.VISIBLE);
            offerBtn.findViewById(R.id.get_credits_offer_img_txt).setVisibility(View.VISIBLE);
        }else{
			DTLog.d(tag, "change abtest  layout");
			//默认布局
            ///TO-DO onDestroy 内存优化
            //改变背景
            videoBtn.setBackgroundResource(R.drawable.bg_ellipse_blue);
            offerBtn.setBackgroundResource(R.drawable.bg_ellipse_green);
            //显示底部阴影
            offerTV.setBackgroundResource(R.color.transparent);
            videoTV.setBackgroundResource(R.color.transparent);
            //替换图标
            videoIV.setImageResource(R.drawable.icon_watch_videos);
            offerIV.setImageResource(R.drawable.icon_offer);
            //显示文字
            videoBtn.findViewById(R.id.get_credits_video_img_txt).setVisibility(View.GONE);
            offerBtn.findViewById(R.id.get_credits_offer_img_txt).setVisibility(View.GONE);
        }
    }
    private void startAnimateWatchVideoImage() {

		if (videoIV == null) {
			return;
		}

		if (videoIV.getAnimation() != null) {
			return;
		}


		Animation fadeIn = new AlphaAnimation(0.3f, 1);
		//fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
		fadeIn.setDuration(1000);
		fadeIn.setRepeatCount(Animation.INFINITE);
		fadeIn.setRepeatMode(Animation.REVERSE);

		Animation scaleIn = new ScaleAnimation(1.0f, 1.2f, 1.0f, 1.2f);
		scaleIn.setDuration(1000);
		scaleIn.setRepeatCount(Animation.INFINITE);
		scaleIn.setRepeatMode(Animation.REVERSE);

		AnimationSet animation = new AnimationSet(false); //change to false
		animation.addAnimation(fadeIn);
		animation.addAnimation(scaleIn);
		videoIV.setAnimation(animation);
		animation.startNow();

	}

	private void stopAnimateWatchVideoImage() {

		if (videoIV == null) {
			return;
		}

		videoIV.clearAnimation();
	}

	private void getAdList() {
		if (ToolsForConnect.CheckNetworkStatusForLoginedNoDialog()) {
			if (AdConfig.getInstance().isNeedForceGetAdList()) {
				DTLog.i(tag, "getAdList force get ad list");
				TpClient.getInstance().getAdList();
			}
		}
	}

	protected void onNewIntent(Intent intent) {
		DTLog.i(tag, "onNewIntent");
		super.onNewIntent(intent);
		setIntent(intent);

		int adCode = intent.getIntExtra(INTENT_AD_TYPE, -1);
		if (adCode > 0 && !intent.getBooleanExtra(INTENT_SHOW_AD_TAPJOY_FROM_BOSS , false)) {
			VideoAdManager.getInstance().cancelAutoPlay();
			AdManager.getInstance().startVideoByAdType(adCode);
			return;
		}

		handleIntent();
	}

	private void handleIntent() {

		Intent intent = getIntent();

		if (intent != null) {
			mSecretaryOper = intent.getStringExtra(TAG_SECRETARY);
			mFromScreen = intent.getStringExtra(EXTRA_FROM);
			if(mSecretaryOper != null) {
				DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS, ActionType.GET_CREDITS_NAVIGATE_TO_VIDEO, mFromScreen, 0);
			}
			DTLog.i(tag, "handleIntent Secertary operation " + mSecretaryOper + " fromScreen = " + mFromScreen);

			int notificationType = intent.getIntExtra(UserWakeUpPushReceiver.NOTIFICATION_TYPE, -1);
			if (notificationType == UserWakeUpNotificationType.FIRST_TWO_DAYS_GOT_FREE_CREDITS_NO_OFFER) {
				DTTracker.getInstance().sendEventV2(CategoryType.USER_WAKE_UP,
						ActionType.USER_WAKE_UP_FIRST_TWO_DAYS_GOT_FREE_CREDITS_NO_OFFER_HANDLED, null, 0);
				DTLog.i(tag, "user wake up first two days got free credits no offer handled");
			} else if (notificationType == UserWakeUpNotificationType.FIRST_TWO_DAYS_GOT_FREE_CREDITS_NO_FEELING_LUCKY) {
				DTTracker.getInstance().sendEventV2(CategoryType.USER_WAKE_UP,
						ActionType.USER_WAKE_UP_FIRST_TWO_DAYS_GOT_FREE_CREDITS_NO_FEELING_LUCKY_HANDLED, null, 0);
				DTLog.i(tag, "user wake up first two days got free credits no feeling lucky handled");
			} else if (notificationType == UserWakeUpNotificationType.FIRST_DAY_MADE_PSTN_CALL_OR_SENT_SMS) {
				DTTracker.getInstance().sendEventV2(CategoryType.USER_WAKE_UP,
						ActionType.USER_WAKE_UP_FIRST_DAY_MADE_PSTN_CALL_OR_SENT_SMS_HANDLED, null, 0);
				DTLog.i(tag, "user wake up first day made pstn call or sent sms handled");
			} else if (notificationType == UserWakeUpNotificationType.ONE_WEEK_NOT_USED) {
				DTTracker.getInstance().sendEventV2(CategoryType.USER_WAKE_UP,
						ActionType.USER_WAKE_UP_FIRST_TIME_ONE_WEEK_NOT_USED_HANDLED, null, 0);
				DTLog.i(tag, "user wake up one week not used handled");
			} else if (notificationType == UserWakeUpNotificationType.TWENTY_DAYS_NOT_USED) {
				DTTracker.getInstance().sendEventV2(CategoryType.USER_WAKE_UP,
						ActionType.USER_WAKE_UP_TWENTY_DAYS_NOT_USED_HANDLED, null, 0);
				DTLog.i(tag, "user wake up twenty days not used handled");
			} else if (notificationType == UserWakeUpNotificationType.FIRST_TWO_DAYS_GOT_NO_CREDITS) {
				DTTracker.getInstance().sendEventV2(CategoryType.USER_WAKE_UP,
						ActionType.USER_WAKE_UP_FIRST_TWO_DAYS_GOT_NO_CREDITS_HANDLED, null, 0);
				DTLog.i(tag, "user wake up first two days got no credits handled");
			} else if (notificationType == UserWakeUpNotificationType.FIRST_TWO_DAYS_GOT_FREE_CREDITS_NO_VIDEO) {
				DTTracker.getInstance().sendEventV2(CategoryType.USER_WAKE_UP,
						ActionType.USER_WAKE_UP_FIRST_TWO_DAYS_GOT_FREE_CREDITS_NO_VIDEO_HANDLED, null, 0);
				DTLog.i(tag, "user wake up first two days got free credits no video handled");
			}
		}

		int adType = intent.getIntExtra(GetCreditsActivity.INTENT_AD_TYPE, 0);
		DTLog.i(tag, "handleIntent adType = " + adType);

		if (adType > 0) {

			DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS, ActionType.GET_CREDITS_NAVIGATE_TO_SPECIFIC_AD, "" + adType, 0);
			if (!DtAppInfo.getInstance().isChinaVersion()) {

				if (adType == AdProviderType.AD_PROVIDER_TYPE_TAPJOY) {
					AdManager.getInstance().showOfferWallByAdType(
							AdProviderType.AD_PROVIDER_TYPE_TAPJOY, this);

				} else if (AdProviderType.isSuperOfferwallOffer(adType)) {

					AdManager.getInstance().showOfferWallByAdType(
							AdProviderType.AD_PROVIDER_TYPE_SUPEROFFERWALL, this);

				} else if (adType == AdProviderType.AD_PROVIDER_TYPE_FLURRY
						|| adType == AdProviderType.AD_PROVIDER_TYPE_ADCOLONY
						|| adType == AdProviderType.AD_PROVIDER_TYPE_SUPERSONIC
						|| adType == AdProviderType.AD_PROVIDER_TYPE_APPLOVIN_VIDEO
						|| adType == AdProviderType.AD_PROVIDER_TYPE_APPNEXT_VIDEO) {
					startPlayVideo(adType);

				} else if(adType == AdProviderType.AD_PROVIDER_TYPE_ADMOB) {

					if(GetCreditsUtils.isDingCreditConnected() && (GetCreditsUtils.GetCanShowVideo(DTApplication.getInstance()) == BOOL.FALSE)) {

						AdMobVpnManager.getInstance().showAdmob(GetCreditsActivity.this, AdProviderType.AD_PROVIDER_TYPE_ADMOB);

					}else {
						// set mSecretaryOper to video to play video
						mSecretaryOper = TAG_SECRETARY_VEDIO;
					}


				} else if(adType == AdProviderType.AD_PROVIDER_TYPE_PEANUTLABS
						||adType == AdProviderType.AD_PROVIDER_TYPE_TAPRESEARCH
						||adType == AdProviderType.AD_PROVIDER_TYPE_POLLFISH){

					Intent intentSurvey = new Intent(this, MoreOffersAndSurveysActivity.class);
					intentSurvey.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					this.startActivity(intentSurvey);
				}

			} else {
				AdManager.getInstance().showOfferWallByAdType(adType, this);
			}
		}
	}


	private void requestMyBalance() {
		TpClient.getInstance().getMyBalance();
	}

	public void onInviteCreidt(View pressed) {
		saveClickOtherCreditButtonTime();
		Intent intent = new Intent(this, InviteCreidtActivity.class);
		this.startActivity(intent);
	}

	@Override
	protected void onStart() {
		DTLog.i(tag, "onStart");
		super.onStart();
		activityStatus = START;
		setListener();
		changeOfferSettingOfCountry();
		if (!DtAppInfo.getInstance().isChinaVersion()) {
			startRefreshBannerTimer();
			SuperOfferwallMgr.getInstance().prepareSuperofferwallList();
			offerItemLayoutTaskForRefresh();
		}
		startCheckVideoAvailabilityTimer();
        setUpHandIndicatorAnimation();
		changeABTestLayout();
		// 刷新彩票入口
		refreshLotteryView();
		addAdListener();

	}

	@Override
	protected void onResume() {
		DTLog.i(tag, "onResume");
		super.onResume();
		if (isEnterFeelingLucky){
			showGuide(GUIDE_TYPE.TYPE_FEELINGLUCKY);
			isEnterFeelingLucky=false;
		}
		if (GetCreditsUtils.isDingCreditConnected()) {
			GetCreditsUtils.Start_Timer_ShowDialog(GetCreditsActivity.this);
			GetCreditsUtils.Start_Timer_Close_Vpn();
		}

		GetCreditsUtils.IsInGetcreditsActivity = true;

		if (!GetCreditsUtils.isServiceWork(GetCreditsActivity.this, GetCreditsUtils.FlowViewServiceName)&&GetCreditsUtils.CanShowVpn(DTApplication.getInstance())){
			GetCreditsUtils.openVpnFloatView(false);
		}

		if(GetCreditsUtils.isDingCreditConnected()){
			DTTracker.getInstance().sendEventV2(CategoryType.VPN_TIP,ActionType.VPN_OPEN_VPN_DIRECTLY, "onResume", 0);
			GetCreditsUtils.openVpnFloatViewDirectly();
		}

		DTLog.d(tag, "mSecretaryOper= " + mSecretaryOper);
		if (mSecretaryOper != null && TAG_SECRETARY_VEDIO.equals(mSecretaryOper)) {
			DTLog.d(tag, "TAG_SECRETARY_VEDIO...");
			videoBtn.performClick();
			mSecretaryOper = null;

		} else if (mSecretaryOper != null && TAG_SECRETARY_AD.equals(mSecretaryOper)) {
		} else if (mSecretaryOper != null && TAG_SECRETARY_AD.equals(mSecretaryOper)) {
			DTLog.d(tag, "TAG_SECRETARY_AD...");
			offerBtn.performClick();
			mSecretaryOper = null;
		}

		if (!DtAppInfo.getInstance().isChinaVersion()) {
			SuperOfferwallMgr.getInstance().setStartTimeToCheckNeedRefreshSuperOfferwall(0);
		}

		DTLog.i(tag, "onResume currentActivity = " + DTApplication.getInstance().getCurrentActivity());


		ImageView newIv = (ImageView) findViewById(R.id.get_credits_app_wall_new);

		if(newIv != null) {
			if (!SharedPreferencesUtil.getCreditsKazooNewState()) {
//				String lastVersion = SharedPreferencesUtil.getLastAppVersionForNewTag();
//				if (lastVersion != null && !lastVersion.isEmpty() && !lastVersion.equals(DtAppInfo.getInstance().getCurrentVersion())) {
//					newIv.setVisibility(View.VISIBLE);
//				} else if ((DtAppInfo.getInstance().getLoginLastTime() - DtAppInfo.getInstance().getFirstLoginTime()) >= TimeUtil.ONE_MONTH) {
//					newIv.setVisibility(View.VISIBLE);
//				} else {
//					newIv.setVisibility(View.GONE);
//				}
				newIv.setVisibility(View.VISIBLE);
			} else {
				newIv.setVisibility(View.GONE);
			}

		}

		// 确认一下是否开奖12h后没有确认过彩票
		if (!isShowVPN && !isShowAdTip) {
			check12HourNotCheckDialog(this);
		}

		AdManager.getInstance().judgeAutoPlayCachedVideo();
	}

    private void setUpHandIndicatorAnimation() {
        String curTime = ToolsForTime.getCurrentTimeAsString();
        String lastCheckinTime = SharedPreferencesUtil.getLastCheckinTime();
        String lastFeelingLuckyClickTime = SharedPreferencesUtil.getLastFeelingLuckyClickTime();
        String lastKazooClickTime = SharedPreferencesUtil.getLastKazooClickTime();

        try {

            boolean hasCheckedin = lastCheckinTime != null && ToolsForTime.daysOfTwo(lastCheckinTime, curTime) == 0;
            boolean hasClickedFeelingLucky = lastFeelingLuckyClickTime != null && ToolsForTime.daysOfTwo(lastFeelingLuckyClickTime, curTime) == 0;
            boolean hasClickedKazoo = lastKazooClickTime != null && ToolsForTime.daysOfTwo(lastKazooClickTime, curTime) == 0;

            if (!hasCheckedin) {
                checkinIndicator.setVisibility(View.VISIBLE);
                feelingLuckyIndicator.setVisibility(View.GONE);
                kazoolinkIndicator.setVisibility(View.GONE);
                checkinIndicator.startAnimation(animBlink);
                feelingLuckyIndicator.clearAnimation();
                kazoolinkIndicator.clearAnimation();
            } else if (!hasClickedFeelingLucky) {
                checkinIndicator.setVisibility(View.GONE);
                feelingLuckyIndicator.setVisibility(View.VISIBLE);
                kazoolinkIndicator.setVisibility(View.GONE);
                checkinIndicator.clearAnimation();
                feelingLuckyIndicator.startAnimation(animBlink);
                kazoolinkIndicator.clearAnimation();
            } else if (!hasClickedKazoo && AppConfig.getInstance().isKazoolinkEnabled()) {
                checkinIndicator.setVisibility(View.GONE);
                feelingLuckyIndicator.setVisibility(View.GONE);
                kazoolinkIndicator.setVisibility(View.VISIBLE);
                checkinIndicator.clearAnimation();
                feelingLuckyIndicator.clearAnimation();
                kazoolinkIndicator.startAnimation(animBlink);
            } else {
                checkinIndicator.setVisibility(View.GONE);
                feelingLuckyIndicator.setVisibility(View.GONE);
                kazoolinkIndicator.setVisibility(View.GONE);
                checkinIndicator.clearAnimation();
                feelingLuckyIndicator.clearAnimation();
                kazoolinkIndicator.clearAnimation();
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
	protected void onRestart() {
		DTLog.i(tag, "onRestart");
		super.onRestart();
		if (SharedPreferencesUtilCallFrequency.isNeedRefresh(
				SharedPreferencesUtilCallFrequency.getRefreshCreditsTime(),
				Global.ONE_MINUTE * 2)) {
			requestMyBalance();
		} else {
			createCreditsRefreshTimer();
		}

		if (!DtAppInfo.getInstance().isChinaVersion()) {
			offerItemLayoutTaskForRefresh();
		}

	}

	@Override
	protected void onPause() {
		DTLog.i(tag, "onPause");
		super.onPause();

//		refreshProductListBg();
		OfferTipManager.getInstance().removeTipConfigListener();
	}

	@Override
	protected void onStop() {
		DTLog.i(tag, "onStop");
		super.onStop();
		GetCreditsUtils.CancelTimer(GetCreditsUtils.Stay_in_Credits_Close_vpn);
		GetCreditsUtils.CancelTimer(GetCreditsUtils.Stay_in_Credits_Show_Dialog);
		stopRefreshBannerTimer();
		dismissWatchVideoProgressDialog();
		destoryCreditsRefreshTimer();
		GetCreditsUtils.IsInGetcreditsActivity = false;

		AdManager.getInstance().cancelInterstitial();
//		AdManager.getInstance().cancelAllVideos();

		stopCheckVideoAvailibityTimer();

	}

	@Override
	protected void onUserLeaveHint() {
		super.onUserLeaveHint();
		DTLog.i(tag, "onUserLeaveHint");
	}


	public void onEventMainThread(VpnTimerEvent event) {
		switch (event.type) {
			case VpnTimerEvent.TYPE_CLOSE_VPN:
				DTLog.i(GetCreditsUtils.TAG, "onEventMainThread : close vpn");
				GetCreditsUtils.closeVPN();
				break;
			case VpnTimerEvent.TYPE_SHOW_STAY_IN_CREDITS_DIALOG:
				DTLog.i(GetCreditsUtils.TAG, "onEventMainThread : show dialog");
				DialogUtil.ShowStayInGetCredits(GetCreditsActivity.this);
				break;
		}
	}

	@Override
	protected void onDestroy() {
		DTLog.i(tag, "onDestory...");

		if (GetCreditsUtils.isDingCreditConnected() || GetCreditsUtils.isServiceWork(GetCreditsActivity.this, GetCreditsUtils.FlowViewServiceName)) {
			GetCreditsUtils.closeVPN();
			GetCreditsUtils.closeVpnFloatView(false);
		}
		GetCreditsUtils.IsGetCreditsActivityExits = false;

		activityStatus = DESTROY;

		if (mOfferTipListener != null) {
			OfferTipManager.getInstance().removeListener(mOfferTipListener);
			mOfferTipListener = null;
		}

		if (AdManager.getInstance().getNativeAd() != null) {
			AdManager.getInstance().getNativeAd().setNativeAdFetchListener(null);
		}

		AdManager.getInstance().unitAdManager(this);


		mActivity = null;
		AdManager.getInstance().unregisterManagerListener();
		super.onDestroy();
		dismissWatchVideoProgressDialog();
		destoryCreditsRefreshTimer();
		destroyRefreshBalanceTimer();
		unregisterReceiver(mBroadcastReceiver);
		UIMgr.getInstance().unregisterActivityEvent(this);

//		FacebookHeadImageFetcher.getImageFetcher().setLoadingImage(R.drawable.img_head);
		FacebookHeadImageFetcher.setImageShape(Shape.Circle);
		unbindDrawables(findViewById(R.id.layout_get_credits));

		KiipManager.getInstance().setListener(null);
		MediabrixManager.getInstace().setListener(null);

		CheckinManager.removeCheckinListener();
		CheckinManager.removeRankingUIJobListener();
		CheckinManager.removeCheckinLevelListener();

		if (noVideoDialog != null && noVideoDialog.isShowing()) {
			noVideoDialog.dismiss();
			noVideoDialog = null;
		}
		mHandler.removeCallbacksAndMessages(null);
		EventBus.getDefault().unregister(this);

		dismissFeelingLuckLoadingAd();

		//showHandle.removeMessages(0);
		System.gc();


	}

	private void initView() {
		scrollView= (ScrollView) findViewById(R.id.scrollView);
		// top
		backBtn = (LinearLayout) findViewById(R.id.get_credits_back);
		helpBtn = (LinearLayout) findViewById(R.id.get_credits_help);
		myBalanceText = (TextView) findViewById(R.id.public_credit_my_balance);
		videoBtn = (LinearLayout) findViewById(R.id.get_credits_video);
		offerBtn = (LinearLayout) findViewById(R.id.get_credits_offer);
		videoIV = (ImageView) findViewById(R.id.get_credits_video_img);
		offerIV = (ImageView) findViewById(R.id.get_credits_offer_img);
		videoTV = (TextView) findViewById(R.id.get_credits_video_tv);
		offerTV = (TextView) findViewById(R.id.get_credits_offer_tv);
		/*tipText = (TextView) findViewById(R.id.get_credits_tip);
		tipText.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tipText.getPaint().setAntiAlias(true);*/
		// offer item
		mADBanner = (ADBanner) findViewById(R.id.ad_banner);
		mADBanner.init(this);
		//checkin
		//checkInHandImg = (ImageView) findViewById(R.id.get_credits_checkin_hand);
		checkinLayout = (RelativeLayout) findViewById(R.id.activity_earn_free_credits_daily_checkin);
		// feeling lucky
		mFeelingLuckyText = (RelativeLayout) findViewById(R.id.activity_earn_free_credits_feeling_lucky);
		mMoreOfferAndSurveys = (RelativeLayout) findViewById(R.id.activity_earn_free_credits_more_offers);
		mMoreOfferAndSurveys.setOnClickListener(this);
		/*mEarn20CreditsInviteFriendTV = (TextView) findViewById(R.id.earn_credits_to_invite_friend_tv);
		String earn20credits = this.getResources().getString(R.string.more_get_credits_earn_all);
		mEarn20CreditsInviteFriendTV.setText(Html.fromHtml(earn20credits));*/

		//inviteFriendsLayout = (RelativeLayout) findViewById(R.id.get_credits_invite_friends);
		appWallLayout = findViewById(R.id.get_credits_app_wall);
		divideForAppWall = findViewById(R.id.view_divide_app_wall);
		mDividerForLottery= findViewById(R.id.view_lottery_divider);
		TextView appWallTv = (TextView) findViewById(R.id.earn_credits_to_app_wall_tv);
		appWallTv.setText(Html.fromHtml(this.getResources().getString(R.string.get_credits_ad_text)));


		guideContainer= (GuideContainer) findViewById(R.id.guideContainer);

		guideView= (GuideView) findViewById(R.id.guideView);
		guideContainer.setGuideView(guideView);
		if(!AppConfig.getInstance().isKazoolinkEnabled()){
			appWallLayout.setVisibility(View.GONE);
			divideForAppWall.setVisibility(View.GONE);
		}else{
			divideForAppWall.setVisibility(View.VISIBLE);
			appWallLayout.setVisibility(View.VISIBLE);
			appWallLayout.setOnClickListener(this);
		}
		// Share
		mShareLayout = (RelativeLayout) findViewById(R.id.activity_earn_free_credits_share);
		mShareLayout.setOnClickListener(this);

		//hand indicator
        checkinIndicator = findViewById(R.id.checkin_indicator);
        feelingLuckyIndicator = findViewById(R.id.feeling_lucky_indicator);
        kazoolinkIndicator = findViewById(R.id.kazoo_indicator);

        Animation fadeIn = new AlphaAnimation(0.3f, 1);
        fadeIn.setDuration(1000);
        fadeIn.setRepeatCount(Animation.INFINITE);
        fadeIn.setRepeatMode(Animation.REVERSE);

        Animation scaleIn = new ScaleAnimation(0.8f, 1.0f, 0.8f, 1.0f);
        scaleIn.setDuration(1000);
        scaleIn.setRepeatCount(Animation.INFINITE);
        scaleIn.setRepeatMode(Animation.REVERSE);

        animBlink = new AnimationSet(false);
        animBlink.addAnimation(fadeIn);
        animBlink.addAnimation(scaleIn);

		// lottery start
		mDailyLotteryLayout = (RelativeLayout) findViewById(R.id.activity_earn_free_credits_daily_lottery);
		mDailyLotteryLayout.setOnClickListener(this);
		mIvDailyLotteryNew = (ImageView) findViewById(R.id.get_credits_lottery_new);

		if (LotteryConfig.getInstance().isHaveEnteredLottery()) {
			mIvDailyLotteryNew.setVisibility(View.GONE);
		} else {
			mIvDailyLotteryNew.setVisibility(View.VISIBLE);
		}

		llLotteryTip = (LinearLayout) findViewById(R.id.ll_lottery_tip);
		llLotteryTip.setOnClickListener(this);


		refreshLotteryView();
		// lottery end

	}

	private void refreshLotteryView() {
		if (llLotteryTip == null || mDailyLotteryLayout == null) {
			return;
		}
		if (AppConfig.getInstance().isShowDailyLottery()  || LotteryConfig.getInstance().isNotFinishLottery()) {
			DTLog.i(tag,"show daily lottery");
			DTTracker.getInstance().sendEventV2(CategoryType.LOTTERY, ActionType.LOTTERY_ENTRY_SHOW,"", 0);
			llLotteryTip.setVisibility(View.VISIBLE);
			mDailyLotteryLayout.setVisibility(View.VISIBLE);
			mDividerForLottery.setVisibility(View.VISIBLE);

		} else {
			DTLog.i(tag,"hide daily lottery");
			DTTracker.getInstance().sendEventV2(CategoryType.LOTTERY, ActionType.LOTTERY_ENTRY_HIDE,"", 0);
			llLotteryTip.setVisibility(View.GONE);
			mDailyLotteryLayout.setVisibility(View.GONE);
			mDividerForLottery.setVisibility(View.GONE);
		}
	}

	/*private Handler showHandle = new Handler(){
		private boolean bShowAppWall = true;

		public void handleMessage(Message msg) {
			if(bShowAppWall){
				inviteFriendsLayout.setVisibility(View.VISIBLE);
				appWallLayout.setVisibility(View.GONE);
			}else{
				inviteFriendsLayout.setVisibility(View.GONE);
				appWallLayout.setVisibility(View.VISIBLE);
			}

			bShowAppWall = !bShowAppWall;
			this.sendEmptyMessageDelayed(0,10 * 1000);
		}
	};*/

	private void setListener() {
		backBtn.setOnClickListener(this);
		videoBtn.setOnClickListener(this);
		offerBtn.setOnClickListener(this);
		//tipText.setOnClickListener(this);
		boolean isPstnCalledOneMinuteDuration = DtAppInfo.getInstance()
				.isPstnCalledOneMinuteDuration();
		DTLog.i(tag, "setListener...isPstnCalledOneMinuteDuration="
				+ isPstnCalledOneMinuteDuration + "; isUserEverIAP="
				+ DtAppInfo.getInstance().isUserEverIAP());
		checkinLayout.setOnClickListener(this);
		setMyBalanceText();
		showHelpBtn();
		// check in anim
		/*if (ToolsForAll.isShowCheckInAnim()) {
			checkInAnimation = new TranslateAnimation(-1, 3, -1, 2);
			checkInAnimation.setDuration(300);
			checkInAnimation.setRepeatCount(Animation.INFINITE);
			checkInAnimation.setRepeatMode(Animation.REVERSE);

			checkInHandImg.setAnimation(checkInAnimation);
			checkInAnimation.startNow();
		}*/
		mFeelingLuckyText.setOnClickListener(this);
	}

	private void showHelpBtn() {
		helpBtn.setVisibility(View.VISIBLE);
		helpBtn.setOnClickListener(this);
	}

	private void stopRefreshBannerTimer() {
		if (mRefreshBannerTimer != null) {
			DTLog.d(tag, "stopRefreshBannerTimer...mTimer != null...");
			mRefreshBannerTimer.cancel();
			mRefreshBannerTimer = null;
		} else {
			DTLog.d(tag, "stopRefreshBannerTimer...mTimer == null...");
		}
	}

	private void startRefreshBannerTimer() {
//		DTLog.d(tag, "startRefreshBannerTimer");
		mRefreshBannerTimer = new Timer(true);
		TimerTask task = new TimerTask() {
			public void run() {
				// task
				DTLog.d(tag, "startRefreshBannerTimer refresh banner");
				mHandler.sendEmptyMessage(REFRESH_BANNER_OFFER_ITEM);
			}
		};
		mRefreshBannerTimer.schedule(task, 0, 30000);
	}

	private void offerItemLayoutTaskForRefresh() {
		if (!DtAppInfo.getInstance().isChinaVersion()) {
			if (ToolsForConnect.CheckNetworkStatusForLoginedNoDialog()) {
				bannerOfferItem = SuperOfferwallMgr.getInstance().getAdBannerOffer(BannerInfo.PLACEMENT_TYPE_GET_CREDITS_MIDDLE_BANNER);
				showOfferItemLayoutForBanner(bannerOfferItem);
			} else {
				DTLog.d(tag, "offerItemLayoutTaskForRefresh...no Network && no login");
			}
		}
	}

	private void showOfferItemLayoutForBanner(
			final DTSuperOfferWallObject offerItem) {
		if (offerItem == null) {
			DTLog.i(tag,
					"showOfferItemLayoutForBanner...bannerOfferItem == null");
			return;
		}
		showHelpBtn();
		mADBanner.setVisibility(View.VISIBLE);
		mADBanner.setOffer(offerItem, BannerInfo.PLACEMENT_TYPE_GET_CREDITS_MIDDLE_BANNER);
		isADBannerShow=true;
		mADBanner.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				if(guideContainer.isShow()){
					Integer idByIndex = guideView.getIdByIndex(0);
					if (idByIndex!=null){
						View viewById = findViewById(idByIndex);
						if (viewById!=null){
							int[] loc = new int[2];
							viewById.getLocationOnScreen(loc);
							RectF orgRect = guideView.getRectByIndex(0);
							int[] loc2 = new int[2];;
							guideView.getLocationOnScreen(loc2);
							float top = orgRect.top + loc2[1];
							float v = loc[1]-top ;
							scrollView.smoothScrollBy(0, (int) v);
						}
					}
				}
				mADBanner.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}
		});
	}



	/**
	 * @author victor
	 */
	private void collectAndUploadAntiFraudData() {
		AntiFraudMgr.getInstance().uploadTelephoneInformation();
	}

	private boolean isShowingPaymentChooseDialog;


	private static final int RC_LAUNCH_GOOGLE_PLAY = 10001;


	private void setMyBalanceText() {
		float myBalance = DtAppInfo.getInstance().getBalance();
		if (myBalance < 0) {
			myBalance = 0.00f;
		}
		DTLog.d(tag, "setMyBalanceText...myBalance=" + myBalance);
		String myBalanceStr = ToolsForCallRate
				.GetCreditStrForCallRate(myBalance);
		myBalanceText.setText(myBalanceStr);
	}

	private void goToHelpWebView() {
		Bundle webBundleHelp = new Bundle();
		webBundleHelp.putInt("Title", R.string.help);
		webBundleHelp.putString("URL", Global.GetCreditsHelpUrl);
		Intent intentToWebviewHelp = new Intent(this, WebViewHelpActivity.class);
		intentToWebviewHelp.putExtras(webBundleHelp);
		this.startActivityForResult(intentToWebviewHelp, Help_RequestCode);
	}


	private void clickOfferWallBtn() {

		DTTracker.getInstance().sendMyEvent(PersonalActionType.GET_CREIDTS,
				PersonalLabelType.GET_CREDITS_COMPLETE_OFFER, 0L);

		DTLog.i(tag, "clickOfferWallBtn currentActivity = " + DTApplication.getInstance().getCurrentActivity());

		DTTracker.getInstance().sendEvent(CategoryType.GET_CREDITS,
				ActionType.GET_CREDITS_TO_OFFER, null, 0);

		if (VPNChecker.getInsance().isLocalCountryVPNConnected()) {
			DTTracker.getInstance().sendEventV2(CategoryType.VPN_TIP, ActionType.VPN_GET_CREDIT_TO_OFFER, null, 0);
		}

		if (GetCreditsUtils.isDingCreditConnected()) {
			DTTracker.getInstance().sendEventV2(CategoryType.VPN2, ActionType.VPN_GET_CREDIT_TO_OFFER, null, 0);
		}

		Assert.assertNotNull("clickOfferWallBtn current activity should not be null", DTApplication.getInstance().getCurrentActivity());

		if(!GetCreditsUtils.isForceConnectDingVpn()){
			DTLog.i(tag, "clickOfferWallBtn have no other vpn connect show offer wall");
			AdManager.getInstance().showOfferWall(this);
		}else{
			DTTracker.getInstance().sendEventV2(CategoryType.VPN_TIP, ActionType.VPN_OTHER_VPN_CONNECT_SHOW_WARNING,"getcredit", 0);
			DTLog.i(tag, "clickOfferWallBtn other vpn connect,show warning dialog");
			DialogUtil.showOtherVpnConnectWarning(mActivity, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					DTLog.i(tag, "clickOfferWallBtn other vpn connect,close other vpn connect ding vpn");
					DTTracker.getInstance().sendEventV2(CategoryType.VPN_TIP,ActionType.VPN_OPEN_VPN_DIRECTLY, "otherVpnWarningOkClick", 0);
					GetCreditsUtils.openVpnFloatViewDirectly();
				}
			});
		}

	}

	private void showKiipAd() {
//		adShowPlace = ADSHOWPLACE.WATCH_VIDEO;
		if (!AdConfig.getInstance().isAdInBlackList(AdProviderType.AD_PROVIDER_TYPE_KIIP)) {
			KiipManager.getInstance().setListener(kiipEventListener);
			KiipManager.getInstance().showKiip(this, false);
		} else {
			DTLog.d(tag, "kiip is in black list, show flurry native now");
//			handleNoneVideoAdToShow();
			showNextEndAd();
		}
	}

//	private void showVideoFBNativeAd() {
//		//显示Audience插屏广告
////		FBNativeManager.showInterstialAd(this);
//		AdManager.getInstance().showFBNativeInterstitial(this, new InterstitialEventListener() {
//			@Override
//			public void onResponseSuccessful(int AdCode) {
//			}
//
//			@Override
//			public void onResponseFailed(int AdCode) {
//				showSuperOfferWall();
//			}
//
//			@Override
//			public void onAdClosed(int AdCode) {
//
//				showLastAd();
//			}
//
//			@Override
//			public void onAdOpened() {
//				EventBus.getDefault().post(new AdShowEvent());
//			}
//		});
//	}

//	private void handleNoneVideoAdToShow() {
//
//		dismissWatchVideoProgressDialog();
//
//		DTLog.i(tag, "handleNoneVideoAdToShow ");
//
////		runOnUiThread(new Runnable() {
////
////			@Override
////			public void run() {
////				AdManager.getInstance().showFNNativeAd(GetCreditsActivity.this, AdProviderType.AD_PROVIDER_TYPE_FLURRY_NATIVE,
////						NativeAd.NativeAdType.Interstitial, new NativeAdEventListener() {
////
////							public void onShowFullscreen() {
////								DTLog.d(tag, "on show full screen");
////							}
////
////							public void onCloseFullscreen() {
////								DTLog.i(tag, "on close full screen");
////
////							}
////
////							public void onClicked() {
////								DTLog.i(tag, "on clicked when no video is available");
////
////							}
////
////							public void onCancelled() {
////								DTLog.i(tag, "Flurry full-screen AD is cancelled, try to show superoffer wall");
////								showSuperOfferWall();
////							}
////
////							public void onUnavailable() {
////								DTLog.i(tag, "Flurry full-screen AD is unavilable, try to show superoffer wall");
////
////								showVideoFBNativeAd();
////
//////								if (!AdManager.getInstance().isHasVideo()) {
//////									DTLog.i(tag, "there is no video");
//////									showNoVideoMessage();
//////									mHandler.sendEmptyMessageDelayed(SHOW_NO_VIDEO, 2000);
//////									return;
//////								}
//////								showSuperOfferWall();
////							}
////
////							@Override
////							public void onExpanded() {
////
////							}
////
////							@Override
////							public void onCollapsed() {
////
////							}
////
////							@Override
////							public void onImpressioned() {
////								DTLog.i(tag, "Flurry native onImpressioned");
////							}
////						}, VIDEOLISTTYPE.WATCH_VIDEO);
////			}
////
////		});
//
//	}


	private void showSuperOfferWall() {
		DTLog.i(tag,"showSuperOfferWall");
		if (ToolsForConnect.CheckHasNetwork(this)
				&& !DtAppInfo.getInstance().isChinaVersion()) {
			SuperofferwallActivity.startActivity(this);
		}
	}

	private void createCreditsRefreshTimer() {
		destoryCreditsRefreshTimer();
		creditsRefreshTimer = new DTTimer(Global.ONE_MINUTE * 2, false, new DTTimerListener() {

			@Override
			public void onTimer(DTTimer timer) {
				// TODO Auto-generated method stub
				destoryCreditsRefreshTimer();
				requestMyBalance();
			}
		});
		creditsRefreshTimer.startTimer();
	}

	private void destoryCreditsRefreshTimer() {
		if (creditsRefreshTimer != null) {
			creditsRefreshTimer.stopTimer();
			creditsRefreshTimer = null;
		}
	}

	private void createWatchVideoTimer() {
		destroyWatchVideoTimer();
		watchVideoTimer = new DTTimer(Global.TimeOutSmall, false, new DTTimerListener() {

			private Pair<Integer, Integer> mAdInfo = AdManager.getInstance().getCurrentVideoAdInfo();

			@Override
			public void onTimer(DTTimer timer) {
				DTLog.i(tag, "video time out, show end ad");
				dismissWatchVideoProgressDialog();
				AdManager.getInstance().cancelAllVideos();
				showEndAd();
//				DTLog.d("AdManager", "Watch video createWatchVideoTimer time out adType = " + mAdInfo.first);
//
//				// cancel video/interstitial
//				if (mAdInfo.first == AdProviderType.AD_PROVIDER_TYPE_INTERSTITIAL) {
//					AdManager.getInstance().cancelInterstitial();
//				} else {
//					AdManager.getInstance().cancelVideo(mAdInfo.first);
//				}
//				if (!ToolsForConnect.CheckHasNetwork(GetCreditsActivity.this)) {
//					return;
//				}
//
//				if (mAdInfo.first == AdProviderType.AD_PROVIDER_TYPE_YUME && AdManager.getInstance().needReplayYuMeVideo()) {
//
//					DTLog.d(tag, "createWatchVideoTimer time out reply yume video index = " + mAdInfo.second);
//					AdManager.getInstance().startVideo(mAdInfo.second);
//
//				} else {
//					if (!AdManager.getInstance().isShowRelativeVideo()
//							|| AdManager.getInstance().isAdHasRelatived(mAdInfo.first)) {
//						if (!AdManager.getInstance().tryNextAdVideo()) {
////							handleNoneVideoAdToShow();
//							showEndAd();
//						}
//					} else {
//						AdManager.getInstance().setShowRelativeVideo(false);
//					}
//				}
			}
		});

		watchVideoTimer.startTimer();

		DTLog.d(tag, "Watch video createWatchVideoTimer timer = " + watchVideoTimer);

	}

	private void destroyWatchVideoTimer() {

		if (this.watchVideoTimer != null) {
			DTLog.d(tag, "Watch video destroyWatchVideoTimer timer = " + watchVideoTimer);
			watchVideoTimer.stopTimer();
			watchVideoTimer = null;
		}
	}

	private void dismissWatchVideoProgressDialog() {
		destroyWatchVideoTimer();
		if (watchVideoProgressDialog != null && watchVideoProgressDialog.isShowing()) {
			DTLog.i(tag, "Watch video dismissWatchVideoProgressDialog dialog = " + watchVideoProgressDialog);
			watchVideoProgressDialog.dismiss();
			watchVideoProgressDialog = null;

		}
	}

	private void showWatchVideoProgressDialog() {

		dismissWatchVideoProgressDialog();

		createWatchVideoTimer();
		watchVideoProgressDialog = new CustomProgressDialog(this);
		watchVideoProgressDialog.setMessage(GetCreditsActivity.this
				.getString(R.string.wait));
		watchVideoProgressDialog.setCancelable(false);
		watchVideoProgressDialog.setCanceledOnTouchOutside(false);
		watchVideoProgressDialog.setCountTime((Global.TimeOutSmall) / 1000);
		watchVideoProgressDialog.show();
		DTLog.i(tag, "Watch video showWatchVideoProgressDialog dialog = " + watchVideoProgressDialog);
	}

	// 显示videodialog
	private void showVideoLoadingDialog() {
		showWatchVideoProgressDialog();
//			String adName = getAdNameByAdType(adType);
		String load_string = DTApplication.getInstance().getString(R.string.loading_rewards);
		if (AdConfig.getInstance().isCompliance()) {
			load_string = DTApplication.getInstance().getString(R.string.loading);
		}
		if (watchVideoProgressDialog != null) {
//				watchVideoProgressDialog.setMessage("Loading " + adName);
			watchVideoProgressDialog.setMessage(load_string);
			DTLog.i(tag, "mHandler...updateAdName...Loading name = " + load_string);
		}
	}

	@Override
	public void handleEvent(int evtType, Object obj) {
		switch (evtType) {
			case DTRESTCALL_TYPE.DTRESTCALL_TYPE_QUERY_HAS_MADE_CALL:
				DTLog.i(tag, "DTQueryHasMadeCallResponse...");
				DTQueryHasMadeCallResponse resCall = (DTQueryHasMadeCallResponse) obj;
				if (resCall != null && resCall.getErrCode() == 0) {
					boolean hasMadeCall = resCall.hasMadeCall;
					DTLog.i(tag, "DTQueryHasMadeCallResponse...hasMadeCall=" + hasMadeCall);
					DtAppInfo.getInstance().setServerMadePstnCall(hasMadeCall);
					DtAppInfo.getInstance().setQueryLastTimeForPstnCall(
							System.currentTimeMillis());
					mHandler.sendEmptyMessage(RESPONSE_QUERY_MADE_CALL);
				}
				break;
			case DTRESTCALL_TYPE.DTRESTCALL_TYPE_QUERY_HAS_PURCHASED_CREDITS:
				DTLog.i(tag, "DTQueryHasPurchasedCreditsResponse...");
				DTQueryHasPurchasedCreditsResponse resPur = (DTQueryHasPurchasedCreditsResponse) obj;
				if (resPur != null && resPur.getErrCode() == 0) {
					boolean isHasPurchased = resPur.hasPurchased;
					DTLog.i(tag, "DTQueryHasPurchasedCreditsResponse...isHasPurchased=" + isHasPurchased);
					DtAppInfo.getInstance().setServerPurchasedCredits(
							isHasPurchased);
					DtAppInfo.getInstance().setQueryLastTimeForPurchasedCredits(
							System.currentTimeMillis());
				}
				break;
			case DTRESTCALL_TYPE.DTRESTCALL_TYPE_GET_ADLIST:
				DTGetAdListResponse response = (DTGetAdListResponse) obj;
				if (response != null && response.getErrCode() == 0) {

					String videoList = response.videoExList != null && !response.videoExList.isEmpty() ? response.videoExList : response.videoList;
					DTLog.i(tag, "handleEvent...video=" + videoList);
					AdManager.getInstance().resetVideoList(videoList);

					AdManager.getInstance().resetInterstitialOrder(response.screenADList);

					if (response.offerWallWeightList == null) {

						AdManager.getInstance().resetOfferList(
								response.offerWallList);
						DTLog.i(tag,
								"handleEvent...offer="
										+ response.offerWallList);
					} else {

						DTLog.i(tag, "handleEvent...resetOfferWallWeightList offerWallWeightList = " + Arrays.toString(response.offerWallWeightList.toArray()));

						if (!DtAppInfo.getInstance().isChinaVersion()) {
							AdManager.getInstance().resetOfferWallWeightList(response.offerWallWeightList);
						}
					}
				}
				break;
		}
	}

	@Override
	public void handleRefreshUI(int evtType, Object obj) {
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		DTLog.i(tag, "onActivityResult...start requestCode = " + requestCode + ", resultCode = " + resultCode);


		if (requestCode == Help_RequestCode) {
			if (resultCode == Activity.RESULT_OK) {
				String openUrl = data
						.getStringExtra(WebViewHelpActivity.OpenUrlStr);
				ToolsForAll.shouldOverrideUrlLoadingForHelp(
						GetCreditsActivity.this, openUrl);
			}
		} else if (requestCode == LotteryActivity.LOTTERY_REQUEST_CODE) {
			if (resultCode == LotteryActivity.LOTTERY_RESULT_OK_CODE) {
				addAdListener();
			}
		}else if (requestCode==REQUESTCOADE_CHECK_IN_ACTIVITY){
				showGuide(GUIDE_TYPE.TYPE_CHECK_IN);
		}
	}


	private void getUserlevel() {
		CheckinManager.getCheckinLevel(new UIJobForCheckinLevelResponse() {

			@Override
			public void doUIJobs(DTGetCheckinLevelResponse response) {
				if (response != null) {
					DTLog.i(tag, "level is " + response.checkinLevelInfo.level);
					SharedPreferencesUtilCheckin.saveCurrentLevel(response.checkinLevelInfo.level);
					SharedPreferencesUtilCheckin.saveUpgMinChkInTimes(response.checkinLevelInfo.keepMinCheckinTimes);
					SharedPreferencesUtilCheckin.saveUpgMinCreditsEarn(response.checkinLevelInfo.keepMinCreditsEarn);
				}
			}
		});
	}

	private void startPlayVideo(int adType) {
		DTLog.i(tag, "startPlayVideo adType = " + adType);

		if (watchVideoProgressDialog != null) {
			DTLog.w(tag, "The progress dialog is showing");
			return;
		}

		if (ToolsForConnect.CheckNetworkStatusForLogined(this)) {
			//showWatchVideoProgressDialog();
			AdManager.getInstance().startVideoByAdType(adType);

		}
	}


	public void ShowWatchVideoNoWifiWarningDialog(final Activity activity, final int adType) {

		if (!DTApplication.getInstance().isAppInBackground() && activity != null) {

			CustomAlertDialog.showCustomAlertDialog(activity,
					activity.getResources().getString(R.string.warning),
					activity.getResources().getString(R.string.network_no_wifi_warning_text),
					null,
					activity.getResources().getString(R.string.cancel),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}
					},
					activity.getResources().getString(R.string.btn_continue),
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							if (adType > 0) {
								AdManager.getInstance().startVideoByAdType(adType);
							} else {
								AdManager.getInstance().startAdVideo();
							}
						}
					});
		}
	}


	private AdNotifier adNotifier = new AdNotifier() {


		@Override
		public void onStartVideoFailed(int adType) {

			DTLog.d(tag, "onStartVideoFailed adType = " + adType);

			VideoAdManager.getInstance().setShowVideo(false, adType);

			/**
			 * if the adType is not equal to current video type, it means the failed type is already
			 * timeout and then receive the failed event.
			 */
			Pair<Integer, Integer> adInfo = AdManager.getInstance().getCurrentVideoAdInfo();
			if (adInfo != null && adInfo.first != adType) {
				return;
			}

//			dismissWatchVideoProgressDialog();

			if (!AdManager.getInstance().isAdHasRelatived(adType)) {
				if (AdManager.getInstance().isShowRelativeVideo()) {
					AdManager.getInstance().setShowRelativeVideo(false);
					return;
				}
			}

			GetCreditsActivity.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {

					if (ToolsForConnect.CheckHasNetwork(GetCreditsActivity.this)) {
						DTLog.d(tag, "onStartVideoFailed run");
						if (!AdManager.getInstance().tryNextAdVideo()) {
//							handleNoneVideoAdToShow();
							showEndAd();
						}
					}
				}
			});
		}

		@Override
		public void onVideoComplete(int adType) {

			isVideoPlaySucceed=true;
			DTLog.i(tag, "onVideoComplete adType = " + adType);
			VideoAdManager.getInstance().setShowVideo(false, adType);
			switch (adType) {
		/* adcolony and hyprmx play complete don't call adViewDidClose */
				case AdProviderType.AD_PROVIDER_TYPE_FLURRY:
					AdConfig.getInstance().flurryVideoShow();
					break;
				case AdProviderType.AD_PROVIDER_TYPE_SUPERSONIC:
					AdConfig.getInstance().supersonicVideoShow();
					break;
				case AdProviderType.AD_PROVIDER_TYPE_APPNEXT_VIDEO:
					AdConfig.getInstance().appnextVideoShow();
					break;
				case AdProviderType.AD_PROVIDER_TYPE_ADCOLONY:
					AdConfig.getInstance().adcolonyVideoShow();
				case AdProviderType.AD_PROVIDER_TYPE_HYPRMX:
				case AdProviderType.AD_PROVIDER_TYPE_HYPR_VIDEO:
//					dismissWatchVideoProgressDialog();

					if (AdManager.getInstance().isAdHasRelatived(adType)) {
						DTLog.i(tag, "try the next after video being completed, adType = " + adType);
						mHandler.postDelayed(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								if (ToolsForConnect.CheckHasNetwork(GetCreditsActivity.this)) {
									DTLog.d(tag, "onVideoComplete run");
									if (!AdManager.getInstance().tryNextAdVideo()) {
//										handleNoneVideoAdToShow();
										showEndAd();
									}
								}
							}
						}, 500);
					}
					break;
			}
		}

		@Override
		public void onStartVideo(int adType) {

			DTLog.d(tag, "onStartVideo adType = " + adType);

			if (adType == AdProviderType.AD_PROVIDER_TYPE_NONE) {

//				handleNoneVideoAdToShow();
				showEndAd();

			} else {

//				showWatchVideoProgressDialog();
////			String adName = getAdNameByAdType(adType);
//				String load_string = DTApplication.getInstance().getString(R.string.loading_rewards);
//				if (AdConfig.getInstance().isCompliance()) {
//					load_string = DTApplication.getInstance().getString(R.string.loading);
//				}
//				if (watchVideoProgressDialog != null) {
////				watchVideoProgressDialog.setMessage("Loading " + adName);
//					watchVideoProgressDialog.setMessage(load_string);
//					DTLog.d(tag, "mHandler...updateAdName...Loading");
//				}
			}
		}

		@Override
		public void onPlayingVideo(int adType) {

			dismissWatchVideoProgressDialog();

			// 本次点击播放完成
			VideoAdManager.getInstance().videoPlayed(adType);
			// 正在播放视频
			VideoAdManager.getInstance().setShowVideo(true, adType);
			// 取消视频请求
			AdManager.getInstance().cancelAllVideos();

			int  videoPlayDailyTotalCounts = AdConfig.getInstance().getVideoPlayDailyTotalCounts();
			long videoPlayLastTime = AdConfig.getInstance().getVideoPlayLastTime();

			if (!DtUtil.areSameDay(new Date(System.currentTimeMillis()), new Date(videoPlayLastTime))) {
				videoPlayDailyTotalCounts = 0;
			}

			videoPlayLastTime = System.currentTimeMillis();

			videoPlayDailyTotalCounts ++;

			DTLog.i(tag,"onPlayingVideo adType = " + adType + "  ; videoPlayDailyTotalCounts = " + videoPlayDailyTotalCounts);
			//Save video daily play counts
			AdConfig.getInstance().setVideoPlayDailyTotalCounts(videoPlayDailyTotalCounts);

			AdConfig.getInstance().setVideoPlayLastTime(videoPlayLastTime);

			AdConfig.getInstance().save();
		}

		@Override
		public void adViewDidOpen(int adType, int adTargetType) {

		}

		@Override
		public void adViewDidClose(int adType) {
			VideoAdManager.getInstance().setShowVideo(false, adType);
		}
		@Override
		public void adViewWillOpen() {
		}

		@Override
		public void adViewWillClose() {
		}
	};


	private void handleOnClickingFeelingLucky() {
		isEnterFeelingLucky=true;
		DTTracker.getInstance().sendEvent(CategoryType.GET_CREDITS
				, ActionType.GET_CREDITS_CHECKIN_CLICK_FEELINGLUCKY
				, "weekday:" + CheckinPushManager.getInstance().getDayOfWeek()
				, CheckinPushManager.getInstance().getNowTimeFromMidNight());
		adShowPlace = ADSHOWPLACE.FEELING_LUCKY;
		//点击feeling lucky重置定时器
		if (GetCreditsUtils.isDingCreditConnected()) {
			GetCreditsUtils.ResetTimer(GetCreditsActivity.this);
		}

		AdManager.getInstance().resetFlurryNativeAD(this);

		KiipManager.getInstance().setListener(kiipEventListener);
		MediabrixManager.getInstace().setListener(mediabrixManagerListener);

		if (CheckinManager.feelingLuckyAvailable()) {
			showWaitingCountDownDialog(15000, R.string.wait, new OnTimeoutListener() {

				@Override
				public void onTimeout() {
					CheckinManager.removeCheckinListener();
					Toast.makeText(GetCreditsActivity.this,
							R.string.network_error_title, Toast.LENGTH_LONG).show();
				}
			});

			CheckinManager.feelingLucky(new UIJobForCheckinResponse() {

				@Override
				public void doUIJobs(DTGetDoDailyCheckinResponse response) {
					if (response == null) {
						DTLog.e(tag, "feeling lucky response is null");
						return;
					}

					if (activityStatus == START) {
						dismissWaitingDialog();
						FeelingLuckyDialog feelingLuckyDialog = new FeelingLuckyDialog(GetCreditsActivity.this, response);
						feelingLuckyDialog.setFeelingLuckyDialogListener(mFeelingLuckyDialogListener);
						feelingLuckyDialog.show();
					}
				}
			});
		} else {
			FeelingLuckyDialog feelingLuckyDialog = new FeelingLuckyDialog(GetCreditsActivity.this, null);
			feelingLuckyDialog.setFeelingLuckyDialogListener(mFeelingLuckyDialogListener);
			feelingLuckyDialog.show();
		}
	}

	private void showInterstitial() {
		if (mInterstitialListener == null) {
			mInterstitialListener = new InterstitialResultListener();
		}
		if (adShowPlace == ADSHOWPLACE.WATCH_VIDEO) {
			if (AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_INTERSTITIAL, VIDEOLISTTYPE.WATCH_VIDEO)) {
				AdManager.getInstance().showInterstitial(this,
						AdConst.INTERSTITIAL_TYPE_COIN, false, mInterstitialListener, VIDEOLISTTYPE.WATCH_VIDEO);
			} else {
				showNextEndAd();
			}
		} else if (adShowPlace == ADSHOWPLACE.FEELING_LUCKY){
			if (AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_INTERSTITIAL, AdConfig.VIDEOLISTTYPE.FELLING_LUCKY)) {
				AdManager.getInstance().showInterstitial(this,
						AdConst.INTERSTITIAL_TYPE_COIN, false, mInterstitialListener, VIDEOLISTTYPE.FELLING_LUCKY, false);
			} else {
				showNextEndAd();
			}
		}
	}

	private void showMediabrixAd() {
		boolean canShowMediabrixAd = false;
			DTLog.i(tag, "handleClickCheckinButton show mediabrix");
			if (AdConfig.getInstance().canShowMediabrix(VIDEOLISTTYPE.CHECK_IN)
					&& DTSystemContext.getNetworkType() == PingNetworkType.WIFI
					&& MediabrixManager.getInstace().isViewsReady()) {
				canShowMediabrixAd = MediabrixManager.getInstace().showViews(DTApplication.getInstance().getCurrentActivity(), false);
		}
		if (!canShowMediabrixAd) {
			DTLog.i(tag,"showMediabrixAd failed");
			showNextEndAd();
		}
	}



	/*
	feeeling luck ad list
	 */
	private void showFNNativeAd() {
		if (mNativeAdEventListener == null) {
			mNativeAdEventListener = new NativeAdEventResultListener();
		}
		if (adShowPlace == ADSHOWPLACE.WATCH_VIDEO) {
			AdManager.getInstance().showNativeAd(GetCreditsActivity.this, AdProviderType.AD_PROVIDER_TYPE_FLURRY_NATIVE,
					NativeAd.NativeAdType.Interstitial, mNativeAdEventListener, VIDEOLISTTYPE.WATCH_VIDEO);
		} else if (adShowPlace == ADSHOWPLACE.FEELING_LUCKY) {
			AdManager.getInstance().showNativeAd(GetCreditsActivity.this, AdProviderType.AD_PROVIDER_TYPE_FLURRY_NATIVE,
					NativeAd.NativeAdType.Interstitial, mNativeAdEventListener, VIDEOLISTTYPE.FELLING_LUCKY);
		}
	}

	private int getCurrentAdPlacement() {
		int currentAdPlacement = 0;
		if (adShowPlace == ADSHOWPLACE.WATCH_VIDEO) {
			currentAdPlacement = BannerInfo.PLACEMENT_TYPE_WATCH_VIDEO;
		} else {
			currentAdPlacement = BannerInfo.PLACEMENT_TYPE_FEELLUCKY;
		}

		return currentAdPlacement;
	}

	private void showFBNativeAd() {

		AdManager.getInstance().showFBNativeInterstitial(this, new InterstitialEventListener() {
			@Override
			public void onResponseSuccessful(int AdCode) {
				dismissFeelingLuckLoadingAd();
			}

			@Override
			public void onResponseFailed(int AdCode) {
				DTLog.i(tag,"showFBNativeAd failed");
				showNextEndAd();
			}

			@Override
			public void onAdClosed(int AdCode) {
				showLastAd();
			}

			@Override
			public void onAdOpened() {
				EventBus.getDefault().post(new AdShowEvent());
			}
		}, getCurrentAdPlacement());
	}

	private void showAdMobNativeAd() {

		AdManager.getInstance().showAdMobNativeInterstitial(this, new InterstitialEventListener() {
			@Override
			public void onResponseSuccessful(int AdCode) {
				dismissFeelingLuckLoadingAd();
			}

			@Override
			public void onResponseFailed(int AdCode) {
				DTLog.i(tag,"showAdMobNativeAd failed");
				showNextEndAd();
			}

			@Override
			public void onAdClosed(int AdCode) {
				showLastAd();
			}

			@Override
			public void onAdOpened() {
				EventBus.getDefault().post(new AdShowEvent());
			}
		}, getCurrentAdPlacement());
	}



	private KiipEventListener kiipEventListener = new KiipEventListener() {

		@Override
		public void onKiipAdUnvailable() {
			DTLog.i(tag, "onKiipAdUnvailable");

			showNextEndAd();

//			if (adShowPlace == ADSHOWPLACE.WATCH_VIDEO) {
//				handleNoneVideoAdToShow();
//				return;
//			}
//
//			if (AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_MEDIABRIX,
//					VIDEOLISTTYPE.FELLING_LUCKY)) {
//				if (AdConfig.getInstance().canShowMediabrix(VIDEOLISTTYPE.FELLING_LUCKY)
//						&& DTSystemContext.getNetworkType() == PingNetworkType.WIFI
//						&& MediabrixManager.getInstace().isViewsReady()) {
//					MediabrixManager.getInstace().showViews(GetCreditsActivity.this, false);
//					return;
//				}
//				showInterstitialOnAd();
//			}
		}

		@Override
		public void onUserCanceledKiipAd() {
			DTLog.i(tag, "onUserCanceledKiipAd");
//			if (adShowPlace == ADSHOWPLACE.WATCH_VIDEO) {
////				handleNoneVideoAdToShow();
//				showNextEndAd();
//				return;
//			}


			showNextEndAd();

//			if (AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_MEDIABRIX,
//					VIDEOLISTTYPE.FELLING_LUCKY)) {
//				if (AdConfig.getInstance().canShowMediabrix(VIDEOLISTTYPE.FELLING_LUCKY)
//						&& DTSystemContext.getNetworkType() == PingNetworkType.WIFI
//						&& MediabrixManager.getInstace().isViewsReady()) {
//					MediabrixManager.getInstace().showViews(GetCreditsActivity.this, false);
//				} else {
//					showInterstitialOnAd();
//				}
//			} else {
//				showInterstitialOnAd();
//			}
		}

		@Override
		public void onKiipShow() {
			// TODO Auto-generated method stub
		}

		@Override
		public void onKiipShownComplete() {
//			if (adShowPlace == ADSHOWPLACE.WATCH_VIDEO) {
//				DTLog.i(tag, "Kiip AD is shown, trying to show flurry native");
//				handleNoneVideoAdToShow();
//
//				return;
//			}
//
//			DTLog.i(tag, "Kiip AD is shown, trying to show interstitial...");
//			if (AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_INTERSTITIAL,
//					VIDEOLISTTYPE.FELLING_LUCKY)) {
//				AdManager.getInstance().showInterstitial(GetCreditsActivity.this,
//						AdConst.INTERSTITIAL_TYPE_COIN, false, null, VIDEOLISTTYPE.FELLING_LUCKY);
//			}
//			showNextEndAd();
			dismissFeelingLuckLoadingAd();
		}
	};

	private MediabrixManagerListener mediabrixManagerListener = new MediabrixManagerListener() {

		@Override
		public void onMediabrixAdUnvailable() {
			// Show full-screen AD of Tapjoy or Inmobi here
			DTLog.i(tag, "onMediabrixAdUnvailable");
//			showInterstitialOnAd();

			showNextEndAd();
		}

		@Override
		public void onUserCanceledMediabrixAd() {
			DTLog.i(tag, "onUserCanceledMediabrix");
//			showInterstitialOnAd();
			showNextEndAd();
		}

		@Override
		public void onMediabriAdRewardConfirmation(String target) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onMediabrixAdShow() {
			// TODO Auto-generated method stub

		}


		@Override
		public void onMediabrixShownComplete() {
			DTLog.i(tag, "Mediabrix AD is shown, trying to show interstitial...");
			if (adShowPlace != ADSHOWPLACE.FEELING_LUCKY || AdConfig.getInstance().canShowAdByType(
					AdProviderType.AD_PROVIDER_TYPE_INTERSTITIAL, VIDEOLISTTYPE.FELLING_LUCKY)) {
				AdManager.getInstance().showInterstitial(GetCreditsActivity.this, AdConst.INTERSTITIAL_TYPE_COIN,
						false, null, VIDEOLISTTYPE.FELLING_LUCKY);
			}
			dismissFeelingLuckLoadingAd();
		}
	};

    class InterstitialResultListener implements InterstitialManangerListener {

		@Override
		public void onInterstitialSuccessful(int AdCode, int type) {
			dismissFeelingLuckLoadingAd();
			if (AdCode == AdProviderType.AD_PROVIDER_TYPE_INMOBI || AdCode == AdProviderType.AD_PROVIDER_TYPE_TAPJOY) {
				VideoAdManager.getInstance().videoPlayed(AdCode);
			}
		}

		@Override
		public void onInterstitialFailed() {
			DTLog.i(tag, "Interstitial is not shown, showing next end ad");

			showNextEndAd();

//			runOnUiThread(new Runnable() {
//
//				@Override
//				public void run() {
//					if (mActivity == null || mActivity.isFinishing()) {
//						return;
//					}
//					if(AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_FLURRY_NATIVE, VIDEOLISTTYPE.FELLING_LUCKY)) {
//						showFNNativeAd();
//					} else {
//						if(!AdManager.getInstance().showAdPlacementAdInFeed(BannerInfo.SHOW_ORDER_LAST, GetCreditsActivity.this)) {
//							if(AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_TAPJOY,
//									VIDEOLISTTYPE.FELLING_LUCKY)) {
//								AdManager.getInstance().showOfferWallByAdType(
//										AdProviderType.AD_PROVIDER_TYPE_TAPJOY, GetCreditsActivity.this);
//							}
//						}
//					}
//				}
//
//			});
		}
    }

	class NativeAdEventResultListener implements NativeAdEventListener {

		@Override
		public void onShowFullscreen() {
			dismissFeelingLuckLoadingAd();
		}

		@Override
		public void onCloseFullscreen() {
			dismissFeelingLuckLoadingAd();
		}

		@Override
		public void onClicked() {
			DTLog.i(tag, "Flurry full-screen AD is clicked on interstitials failed.");
		}

		@Override
		public void onCancelled() {

//			DTLog.i(tag, "Feeling lucky Flurry full-screen AD is onCancelled.");
//			if(!AdManager.getInstance().showAdPlacementAdInFeed(BannerInfo.SHOW_ORDER_LAST, GetCreditsActivity.this)) {
//				if(AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_TAPJOY,
//						VIDEOLISTTYPE.FELLING_LUCKY)) {
//					AdManager.getInstance().showOfferWallByAdType(
//							AdProviderType.AD_PROVIDER_TYPE_TAPJOY, GetCreditsActivity.this);
//				}
//			} else {
//				DTLog.i(tag, "show inhouse ad in feeling luck dialog last");
//			}
			DTLog.i(tag,"insterstitial onCalcelled");
			showLastAd();

		}

		@Override
		public void onUnavailable() {
			DTLog.i(tag,"insterstitial onUnavailable");
//			showFBNativeAd();
			showNextEndAd();

//
//			DTLog.i(tag, "Feeling lucky Flurry full-screen AD is onUnavailable.");
//			if(!AdManager.getInstance().showAdPlacementAdInFeed(BannerInfo.SHOW_ORDER_LAST, GetCreditsActivity.this)) {
//				if(AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_TAPJOY,
//						VIDEOLISTTYPE.FELLING_LUCKY)) {
//					AdManager.getInstance().showOfferWallByAdType(
//							AdProviderType.AD_PROVIDER_TYPE_TAPJOY, GetCreditsActivity.this);
//				}
//			} else {
//				DTLog.i(tag, "show inhouse ad in feeling luck dialog last");
//			}

		}

		@Override
		public void onExpanded() {

		}

		@Override
		public void onCollapsed() {

		}

		@Override
		public void onImpressioned() {
			DTLog.i(tag, "Flurry native onImpressioned 2");
			dismissFeelingLuckLoadingAd();
		}
	}

    private boolean gotOfferByDownloadingApp() {
    	if (SharedPreferencesUtil.getLastDownloadAppTime() != 0)
    		return true;
    	else
    		return false;
    }

    private void startCheckVideoAvailabilityTimer() {

    	mCheckVideoAvailabilityTimer = new DTTimer(2000, true, new DTTimerListener(){

			@Override
			public void onTimer(DTTimer timer) {

				boolean videoAvailable = AdManager.getInstance().isVideoAvailable();

				if(GetCreditsUtils.isDingCreditConnected() && (GetCreditsUtils.GetCanShowVideo(DTApplication.getInstance()) == BOOL.FALSE)) {
					videoAvailable = false;
				}

				if(videoAvailable) {
					startAnimateWatchVideoImage();
				}else {
					stopAnimateWatchVideoImage();
				}
			}
    	});

    	mCheckVideoAvailabilityTimer.startTimer();
    }

    private void stopCheckVideoAvailibityTimer() {

    	if(mCheckVideoAvailabilityTimer != null) {
    		mCheckVideoAvailabilityTimer.stopTimer();
    		mCheckVideoAvailabilityTimer = null;
    	}
    }

	@Override
	public void onConfigLoaded(int error, int tipsCount) {
		// try to show recommended offer
		// if user downloaded some App and got offer before
		if (gotOfferByDownloadingApp()) {

			if(mOfferTipListener == null) {

				OfferTipManager.getInstance().removeListener(mOfferTipListener);

				mOfferTipListener = new OfferTipListener() {

					@Override
					public void showSuperOfferWallTipAfter(OfferTip findOfferTip,
							DTSuperOfferWallObject findOfferItem) {
						// if this offer contains placement show the offer tip dialog
						if (findOfferTip.isShowInGetCredit) {
							new OfferTipDialog(GetCreditsActivity.this, R.style.dialog,
									findOfferTip, findOfferItem).showOfferTip();
						} else {
							DTLog.i(tag, "not show in credits");
						}
					}
				};

				OfferTipManager.getInstance().addListener(mOfferTipListener);
			}

			OfferTipManager.getInstance().showOfferTipByType(
					GetCreditsActivity.this, OfferTip.TIP_TYPE_SUPEROFFERWALL_AFTER, null);
		}
	}



	public void showNoVideoMessage() {
		View noVideoView = getLayoutInflater().inflate(R.layout.layout_custom_progress_dialog, null);
		ImageView imageview = (ImageView) noVideoView.findViewById(R.id.custom_progress_imageview);
		imageview.setVisibility(View.GONE);
		TextView display = (TextView) noVideoView.findViewById(R.id.custom_progress_display);
		display.setText(this.getResources().getString(R.string.no_video_show));
		noVideoDialog = new Dialog(this, R.style.dialog);
		noVideoDialog.setContentView(noVideoView);
		noVideoDialog.setCancelable(false);
		noVideoDialog.setCanceledOnTouchOutside(false);
		noVideoDialog.show();
	}

//	private void showInterstitialOnAd() {
//		if(adShowPlace != ADSHOWPLACE.FEELING_LUCKY || AdConfig.getInstance().canShowAdByType(
//				AdProviderType.AD_PROVIDER_TYPE_INTERSTITIAL, VIDEOLISTTYPE.FELLING_LUCKY)) {
//			showInterstitial();
//			return;
//		}
//
//		if(AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_FLURRY_NATIVE,
//				VIDEOLISTTYPE.FELLING_LUCKY)) {
//			showFNNativeAd();
//			return;
//		}
//
//		if(AdConfig.getInstance().canShowAdByType(AdProviderType.AD_PROVIDER_TYPE_TAPJOY,
//				VIDEOLISTTYPE.FELLING_LUCKY)) {
//			if(!AdManager.getInstance().showAdPlacementAdInFeed(BannerInfo.SHOW_ORDER_LAST, GetCreditsActivity.this)) {
//				AdManager.getInstance().showOfferWallByAdType(
//						AdProviderType.AD_PROVIDER_TYPE_TAPJOY, GetCreditsActivity.this);
//				return;
//			}
//		}
//	}

	public void onEventMainThread(VPNOnGetCreditsEvent event){
		GetCreditsUtils.ResetTimer(GetCreditsActivity.this);
	}

	public void onEventMainThread(VPNconnectedEvent event){
		DTLog.i(tag,"vpn connected ,reset timer & dismiss moreofferandsurveys &Is in GetCreditsActivity : "+ GetCreditsUtils.IsInGetcreditsActivity);
		if(GetCreditsUtils.IsInGetcreditsActivity){
			GetCreditsUtils.ResetTimer(GetCreditsActivity.this);
			if(null!=mMoreOfferAndSurveys){
				mMoreOfferAndSurveys.setVisibility(View.GONE);
			}
		}
	}

	public void onEventMainThread(VPNDisconnectedEvent event){
		DTLog.i(tag,"vpn disconnected ,show moreofferandsurveys& Is in GetCreditsActivity : "+ GetCreditsUtils.IsInGetcreditsActivity);
		if(null!=mMoreOfferAndSurveys){
			mMoreOfferAndSurveys.setVisibility(View.VISIBLE);
		}
	}

	public void onEventMainThread(FeelingLuckyAdListEvent event) {
		int adType = event.getAdType();
		switch (adType) {
			case AdProviderType.AD_PROVIDER_TYPE_FLURRY_NATIVE:
				DTLog.i(tag,"onEventMainThread show flurry native");
				showFNNativeAd();
				break;
			case AdProviderType.AD_PROVIDER_TYPE_TAPJOY:
				DTLog.i(tag,"onEventMainThread show tapjoy");
				if(!AdManager.getInstance().showAdPlacementAdInFeed(BannerInfo.SHOW_ORDER_LAST, GetCreditsActivity.this)) {
					AdManager.getInstance().showOfferWallByAdType(
							AdProviderType.AD_PROVIDER_TYPE_TAPJOY, GetCreditsActivity.this);
				}
				break;
			case AdProviderType.AD_PROVIDER_TYPE_INTERSTITIAL:
				DTLog.i(tag,"onEventMainThread show interstitial");
//				showInterstitialOnAd();
				break;

			case AdProviderType.AD_PROVIDER_TYPE_FB_NATIVE:
				DTLog.i(tag,"onEventMainThread show fbnative");
				showFBNativeAd();
				break;
		}
	}

	// VPN dialog 显示事件
	public void onEventMainThread(VpnDialogOpenEvent event) {
		isShowVPN = true;
		dismiss12hoursDialog();
	}


	//通过video Credit和Kiip 方式获得的叮咚币到账通知
	/*public void onEventMainThread(EarnCreditByVideoGreditEvent event){
		isVideoCreditToTheAccount = true;
	}*/
	/**
	 * 判断是否需要显示Dialog引导用户去点击其他方式获取credit
	 * */
	private boolean checkShowedCompleteAnOfferDialog(){
		//显示过Dialog 返回
		if (SharedPreferencesUtil.isShowedCompleteAnOfferDialog()){
			return true;
		}
		/*//Video Credit 没到帐
		if (!isVideoCreditToTheAccount){
			return true;
		}*/
		if (mClickGetVideoCreditButtonTime == 0){
			return true;
		}
		//Video Credit 没有点击其他获得Credit的按钮显示Dialog
		if (mClickOtherCreditButtonTime == 0){
			showCompleteOfferAlert();
			return false;
		}
		if (mClickOtherCreditButtonTime != 0 && mClickGetVideoCreditButtonTime != 0){
			if (ToolsForTime.compareTime(mClickGetVideoCreditButtonTime,mClickOtherCreditButtonTime)){
				SharedPreferencesUtil.setShowedCompleteAnOfferDialog(true);
				return true;
			}else {
				showCompleteOfferAlert();
				return false;
			}
		}
		return true;
	}

	/**
	 * 保存点击Video Credit的时间
	 * */
	private void saveClickGetVideoCreditButtonTime(){
		if (!SharedPreferencesUtil.isShowedCompleteAnOfferDialog() && mClickGetVideoCreditButtonTime == 0 ){
			mClickGetVideoCreditButtonTime = System.currentTimeMillis();
		}
	}
	/**
	 * 保存点击获取其他 Credit的时间
	 * */
	private void saveClickOtherCreditButtonTime(){
		if (!SharedPreferencesUtil.isShowedCompleteAnOfferDialog() && mClickOtherCreditButtonTime == 0 ){
			mClickOtherCreditButtonTime = System.currentTimeMillis();
		}
	}

	private void showCompleteOfferAlert(){
		mCompleteAnOfferDialog = DTAlert.showCompleteOfferAlert(this ,completeAnOfferListener ,checkInListener,luckyListener,inviteFriendListener,kazoonListener);
		SharedPreferencesUtil.setShowedCompleteAnOfferDialog(true);
		DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,
				ActionType.COMPLETE_OFFER_SHOW_DIALOG, null, 0);
	}

	OnClickListener completeAnOfferListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCompleteAnOfferDialog.dismiss();
			clickOfferWallBtn();
			DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,
					ActionType.COMPLETE_OFFER_SHOW_DIALOG_CLICK_OFFER_WALL, null, 0);
		}
	};
	OnClickListener checkInListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCompleteAnOfferDialog.dismiss();
			DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,
					ActionType.COMPLETE_OFFER_SHOW_DIALOG_CLICK_CHECKIN, null, 0);
			DtAppInfo.getInstance().setClickedCheckInIconTime(new Date().getTime());
			startActivityForResult(new Intent(GetCreditsActivity.this, CheckinActivity.class),REQUESTCOADE_CHECK_IN_ACTIVITY);
		}
	};
	OnClickListener luckyListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCompleteAnOfferDialog.dismiss();
			DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,
					ActionType.COMPLETE_OFFER_SHOW_DIALOG_CLICK_FEEL_LUCKY, null, 0);
			handleOnClickingFeelingLucky();
		}
	};
	OnClickListener inviteFriendListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCompleteAnOfferDialog.dismiss();
			DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,
					ActionType.COMPLETE_OFFER_SHOW_DIALOG_CLICK_INVITE, null, 0);
			Intent intent = new Intent(GetCreditsActivity.this, InviteCreidtActivity.class);
			startActivity(intent);
		}
	};

	OnClickListener kazoonListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			mCompleteAnOfferDialog.dismiss();
			Intent intent = new Intent(GetCreditsActivity.this ,AppWallEnterActivity.class);
			intent.putExtra(AppWallEnterActivity.COME_FROM_CHAT,false);
			startActivity(intent);
			SharedPreferencesUtil.setLastKazooClickTime(ToolsForTime.getCurrentTimeAsString());
			DTTracker.getInstance().sendEventV2(CategoryType.GET_CREDITS,ActionType.GET_CREDITS_CLICK_KAZOO_LINK,null,0);
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (checkShowedCompleteAnOfferDialog()){
				if (EXTRA_FROM_LOTTERY.equals(mFromScreen)) {
					mFromScreen = null;
					LotteryActivity.startActivityFromGetCreditsBack(this);
				} else {
					this.finish();
				}
			}
		}
		return false;
	}

	private FeelingLuckyDialogListener mFeelingLuckyDialogListener = new FeelingLuckyDialogListener() {

		@Override
		public void startLuckyLoadingAd() {
			showLuckyLoadingAd();
		}
	};

	private void showLuckyLoadingAd() {

		mIsFeelingLuckyLoadingTimeOut = false;

		adShowPlace = ADSHOWPLACE.FEELING_LUCKY;

		DTTracker.getInstance().sendEvent(CategoryType.GET_CREDITS
				, ActionType.GET_CREDITS_CHECKIN_CLICK_FEELINGLUCKY_GETMORE
				, "weekday:" + CheckinPushManager.getInstance().getDayOfWeek()
				, CheckinPushManager.getInstance().getNowTimeFromMidNight());

		DTLog.i(tag,"showLuckyLoadingAd");

		List<Integer> loadingAdList = new ArrayList<>();

		if (AdConfig.getInstance().getNativeAdConfig() != null) {
			loadingAdList = AdConfig.getInstance().getNativeAdConfig().getWaitLoadingAdList();
		}

		if (loadingAdList == null) {
			int[]adArray = {
					AdProviderType.AD_PROVIDER_TYPE_FLURRY_NATIVE,
					AdProviderType.AD_PROVIDER_TYPE_FB_NATIVE,
					AdProviderType.AD_PROVIDER_TYPE_ADMOB_NATIVE,
			};
			for (int type : adArray) {
				loadingAdList.add(type);
			}
		}

		DTActivity currentActivity = DTApplication.getInstance().getCurrentActivity();
//		PostFeelingluckAction endAdAction = new PostFeelingluckAction();
		mFeelingLuckyLoadingAdDialog = new FeelingLuckyAdDialog(currentActivity, R.style.mydialog, "");
		mFeelingLuckyLoadingAdDialog.setAdTypeList(loadingAdList);
		mFeelingLuckyLoadingAdDialog.setAdPlacement(BannerInfo.PLACEMENT_TYPE_FEELINGLUCKY_LOADING);
		mFeelingLuckyLoadingAdDialog.setmWaitingAdListener(new WaitingAdListener() {
			@Override
			public void close() {
				DTLog.i(tag,"FeelingLuckyLoadingAdDialog close");
//				mIsFeelingLuckyLoadingTimeOut = false;
			}
			@Override
			public void timeout() {
				DTLog.i(tag,"FeelingLuckyLoadingAdDialog timeout");
				mIsFeelingLuckyLoadingTimeOut = true;
			}
		});
//		loadAdDialog.setPostFeelingluckAction(endAdAction);
		mFeelingLuckyLoadingAdDialog.show();
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
//				if (!mIsFeelingLuckyLoadingTimeOut) {
//					return;
//				}
				showEndAd();
			}
		},1000);
	}

	private void dismissFeelingLuckLoadingAd() {
		if (mFeelingLuckyLoadingAdDialog != null) {
			mFeelingLuckyLoadingAdDialog.dismiss();
			mFeelingLuckyLoadingAdDialog = null;
		}
	}


	private void showEndAd() {
		DTLog.i(tag, "showEndAd");
		mEndAdTypeList = new ArrayList<>();

		if (adShowPlace == ADSHOWPLACE.FEELING_LUCKY) {

			//feeling lucky
			int[] endAdArray = {
					AdProviderType.AD_PROVIDER_TYPE_KIIP,
					AdProviderType.AD_PROVIDER_TYPE_MEDIABRIX,
					AdProviderType.AD_PROVIDER_TYPE_INTERSTITIAL,
			};
			for (int type : endAdArray) {
				mEndAdTypeList.add(type);
			}

			List<Integer>nativeAdTypeList = null;
			if (AdConfig.getInstance().getNativeAdConfig() != null) {
				nativeAdTypeList = AdConfig.getInstance().getNativeAdConfig().getEndShowNativeAdList();
			}
			if (nativeAdTypeList != null) {
				for (int type : nativeAdTypeList) {
					mEndAdTypeList.add(type);
				}
			}

		} else if (adShowPlace == ADSHOWPLACE.WATCH_VIDEO) {
			//watch video
			// 当前正在播放视频或者本次点击视频已经播放了，则不显示end广告
			if (VideoAdManager.getInstance().isShowVideo()||VideoAdManager.getInstance().isCurrentClickVideoPlayed()) {
				DTLog.i(tag, "showLastAd video is playing or video is played,don't show end ad");
				return;
			}
			// 本次end广告已经播放了，则不显示end广告
			if (!VideoAdManager.getInstance().canPlayEndAd()) {
				DTLog.i(tag, "showLastAd current last ad is over,don't show end ad");
				return;
			}

			//Remove watch video progress dialog
			dismissWatchVideoProgressDialog();

			if (!isNetworkWifi) {
				//If no wifi  , show kiip first
				int[] endAdArray = {
						AdProviderType.AD_PROVIDER_TYPE_KIIP,
				};
				for (int type : endAdArray) {
					mEndAdTypeList.add(type);
				}
			}

			//watch video
			List<Integer>nativeAdTypeList = null;
			if (AdConfig.getInstance().getNativeAdConfig() != null) {
				nativeAdTypeList = AdConfig.getInstance().getNativeAdConfig().getEndShowNativeAdList();
			}
			if (nativeAdTypeList != null) {
				for (int type : nativeAdTypeList) {
					mEndAdTypeList.add(type);
				}
			}
		}

		mCurrentAdTypeIndex = 0;

		DTLog.i(tag,"getcredits end adList = " + Arrays.toString(mEndAdTypeList.toArray()));
		showNextEndAd();
	}

	private void showNextEndAd() {
		if (adShowPlace == ADSHOWPLACE.WATCH_VIDEO) {
			// 正在播放视频，不继续加载endAd
			if (VideoAdManager.getInstance().isShowVideo()) {
				DTLog.i(tag, "showNextEndAd video is playing ,don't show loading ad");
				return;
			}

			// 本次end广告已经播放了，则不显示end广告
			if (!VideoAdManager.getInstance().canPlayEndAd()) {
				DTLog.i(tag, "showNextEndAd current last ad is over,don't show end ad");
				return;
			}
		}


		if (adShowPlace == ADSHOWPLACE.FEELING_LUCKY && mIsFeelingLuckyLoadingTimeOut) {
			return;
		}
		DTLog.i(tag,"showNextEndAd");
		if (mEndAdTypeList != null && mCurrentAdTypeIndex < mEndAdTypeList.size()) {

			int currentAdType = mEndAdTypeList.get(mCurrentAdTypeIndex);
			mCurrentAdTypeIndex ++;
			DTLog.i(tag,"showNextAd loadNext, adType = " + currentAdType);
			mAdHandler.sendEmptyMessage(currentAdType);
		}
		else {
			// 本次点击EndAd已经播放过了
			VideoAdManager.getInstance().playEndAd();
			DTLog.i(tag,"showNextAd failed, mAdTypeList is use up");
			showLastAd();
		}
	}

	private void showLastAd() {
		if (DTApplication.getInstance().getCurrentActivity() != this) {
			DTLog.i(tag,"showLastAd current activity is not getcredits, not show appwall");
			return;
		}
		DTLog.i(tag,"showLastAd adShowPlace = " + adShowPlace);
		if (adShowPlace == ADSHOWPLACE.WATCH_VIDEO) {
			// 当前正在播放视频或者本次点击视频已经播放了，则不显示last广告
			if (VideoAdManager.getInstance().isShowVideo()||VideoAdManager.getInstance().isCurrentClickVideoPlayed()) {
				DTLog.i(tag, "showLastAd video is playing or video is played,don't show last ad");
				return;
			}
			// 本次last广告已经播放了，则不显示last广告
			if (!VideoAdManager.getInstance().canPlayLastAd()) {
				DTLog.i(tag, "showLastAd current last ad is over,don't show end ad");
				return;
			}
			// 播放lastAd
			VideoAdManager.getInstance().playLastAd();

			if (VideoOfferManager.getInstance().canShowOffer()) {
				DTLog.i(tag, "showLastAd show video offer");
				VideoOfferManager.getInstance().showOfferDialog(this);
			} else {
				DTLog.i(tag, "showLastAd show showSuperOfferWall");
				showSuperOfferWall();
			}

		} else if (adShowPlace == ADSHOWPLACE.FEELING_LUCKY) {

			if (mIsFeelingLuckyLoadingTimeOut) {
				return;
			}
			DTLog.i(tag,"showLastAd for Feeling lucky");
			if (!AdManager.getInstance().showAdPlacementAdInFeed(BannerInfo.SHOW_ORDER_LAST, GetCreditsActivity.this)) {
				DTLog.i(tag,"showLastAd show tapjoy");
				AdManager.getInstance().showTapJoyWall(GetCreditsActivity.this,false);
			}
		}
	}


	private Handler mAdHandler = new Handler() {

		public void handleMessage(Message msg) {
			if(isInBlackListCloseLoadingAd) {
				isInBlackListCloseLoadingAd = false;
				return;
			}

			switch(msg.what) {
//				case AdProviderType.AD_PROVIDER_TYPE_ADMOB:
//					DTLog.i(tag,"handleMessage show adMob");
//					showAdmobInterstitial();
//					break;
				case AdProviderType.AD_PROVIDER_TYPE_KIIP:
					DTLog.i(tag,"handleMessage show kiip");
					//如果连接vpn禁止显示Kiip广告
					showKiipAd();
					if(GetCreditsUtils.isForceConnectDingVpn()){
						DTTracker.getInstance().sendEventV2(CategoryType.VPN_TIP, ActionType.VPN_FORBID_KIIP_WHEN_OTHER_VPN_CONNECT,"", 0);
						DTLog.i(tag,"handleMessage show kiip, forbiden ,other vpn connect");
					}
					break;
				case AdProviderType.AD_PROVIDER_TYPE_INTERSTITIAL:
					DTLog.i(tag,"handleMessage show insterstitial");
					showInterstitial();
					break;
				case AdProviderType.AD_PROVIDER_TYPE_MEDIABRIX:
					DTLog.i(tag,"handleMessage show mediabrix");
					showMediabrixAd();
					break;
				case AdProviderType.AD_PROVIDER_TYPE_FLURRY_NATIVE:
					DTLog.i(tag,"handleMessage show flurry native");
					showFNNativeAd();
					break;
				case AdProviderType.AD_PROVIDER_TYPE_FB_NATIVE:
					DTLog.i(tag,"handleMessage show fb native");
					showFBNativeAd();
					break;
				case AdProviderType.AD_PROVIDER_TYPE_ADMOB_NATIVE:
					DTLog.i(tag,"handleMessage show admob native");
					showAdMobNativeAd();
					break;
				case AdProviderType.AD_PROVIDER_TYPE_PUBNATIVE:
					DTLog.i(tag,"handleMessage show pubnative native");
					break;
				default:
					showNextEndAd();
					break;
			}
		}
	};


	public void onEventMainThread(RefreshBalanceForWatchVideoEvent event) {
		createRefreshBalanceTimer();
	}

	private void createRefreshBalanceTimer() {
		DTLog.i(tag ,"video complete start refresh balance 5s timer");
		destroyRefreshBalanceTimer();
		refreshBalanceTimer = new DTTimer(Global.ONE_SECOND * 5, false, new DTTimerListener() {

			@Override
			public void onTimer(DTTimer timer) {
				// TODO Auto-generated method stub
				destroyRefreshBalanceTimer();
				requestMyBalance();
				DTLog.i(tag ,"video complete refresh balance");
			}
		});
		refreshBalanceTimer.startTimer();
	}

	private void destroyRefreshBalanceTimer() {
		if (refreshBalanceTimer != null) {
			refreshBalanceTimer.stopTimer();
			refreshBalanceTimer = null;
		}
	}

	private void removeAdListeners() {
		DTLog.i(tag,"removeAdListeners");
		AdManager.getInstance().unitAdManager(this);
	}

	private void addAdListener() {
		DTLog.i(tag,"addAdListener");
		AdManager.getInstance().initAdManager(this, adNotifier);
	}

	public void onEventMainThread(LotterySettingLoadedEvent event) {
		DTLog.i(tag,"onEventMainThread LotterySettingLoadedEvent");
		refreshLotteryView();
	}

	public void onEventMainThread(LotteryBackEvent event) {
		DTLog.i(tag,"onEventMainThread LotteryBackEvent");
		addAdListener();
		if(GetCreditsUtils.isEnterLotteryCloseVpn){
			DTTracker.getInstance().sendEventV2(CategoryType.VPN_TIP,ActionType.VPN_OPEN_VPN_DIRECTLY, "backFromLottery", 0);
			GetCreditsUtils.openVpnFloatViewDirectly();
			GetCreditsUtils.isEnterLotteryCloseVpn=false;
		}
	}

	public void check12HourNotCheckDialog(final DTActivity activity) {
		DTLog.i(tag, "check12HourNotCheckDialog");
		//入口关闭了
		if (!AppConfig.getInstance().isShowDailyLottery()) {
			DTLog.i(tag,"check12HourNotCheckDialog entry is close , not show 12hourdialog");
			return;
		}

		LotterySyncInfo lotterySyncInfo = LotteryConfig.getInstance().getLotterySyncInfo();
		if (lotterySyncInfo != null) {
			long lotteryId = lotterySyncInfo.getLotteryId();
			long lotteryDrawTime = lotterySyncInfo.getLotteryDrawTime();
			int lotteryTotalCount = lotterySyncInfo.getLotteryTotalCount();
			long lotteryExpireTime = lotterySyncInfo.getLotteryExpireTime();
			boolean isHasQueriedResult = lotterySyncInfo.isHasQueriedResult();
			boolean isHasShowDrawLottery12HoursAlert = lotterySyncInfo.isHasShowDrawLottery12HoursAlert();
			// 计算与Server时间的offset，从而获取实际的Server的时间
//			long offsettime = DtAppInfo.getInstance().getServerToLocalTimeOffset()*1000;
//			long currentTime = System.currentTimeMillis() + offsettime;
			long currentTime = System.currentTimeMillis();

			DTLog.i(tag,"check12HourNotCheckDialog lotteryId = " + lotteryId
					+ " ; lotteryDrawTime = " + lotteryDrawTime
					+ " ; lotteryTotalCount = " + lotteryTotalCount
					+ " ; isHasShowDrawLottery12HoursAlert = " + isHasShowDrawLottery12HoursAlert
					+ " ; isHasQueriedResult = " + isHasQueriedResult
					+ " ; lotteryExpireTime = " + lotteryExpireTime
					+ " ; currentTime = " + currentTime);

			if (lotteryId != 0 && lotteryDrawTime != 0 && ! isHasShowDrawLottery12HoursAlert && !isHasQueriedResult) {
				//12小时改为0小时，开奖后需要立刻弹出
				long hour = 0;
//				if (DTLog.DBG) { // DN1下彩票开奖后弹窗提示缩短到1个小时
//					DTLog.i(tag,"use one hour");
//					hour = 1;
//				}
				if (System.currentTimeMillis() - lotteryDrawTime > hour * 60 * 60 * 1000    // 开奖后12小时没有查看结果
						) {
					DTLog.i(tag,"check12HourNotCheckDialog show 12dialog");
					String ticketString = activity.getString(R.string.ticket);
					if (lotteryTotalCount > 1) {
						ticketString = activity.getString(R.string.lottery_small_tickets);
					}
					//显示12小时弹窗
					lottery12HoursDialog = CustomAlertDialog
							.showCustomAlertDialog(activity,
									activity.getString(R.string.lottery_check_lottery_result),
									activity.getString(R.string.lottery_you_purchase_lottery_may_be_winner, "" + lotteryTotalCount , ticketString),
									"",
									activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											DTLog.i(tag,"check12HourNotCheckDialog cancel");
											dialog.dismiss();
											lottery12HoursDialog = null;
											DTTracker.getInstance().sendEventV2(CategoryType.LOTTERY, ActionType.LOTTERY_12_HOUR_CHECK_RESULT_CANCEL,"", 0);
										}
									},
									activity.getString(R.string.lottery_check_result_go), new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog, int which) {
											DTLog.i(tag,"check12HourNotCheckDialog entry");
											dialog.dismiss();
											if (LotteryActivity.startLotteryActivity(activity,false)) {
												removeAdListeners();
											}
											lottery12HoursDialog = null;
											DTTracker.getInstance().sendEventV2(CategoryType.LOTTERY, ActionType.LOTTERY_12_HOUR_CHECK_RESULT_GO,"", 0);
										}
									}
							);
					DTTracker.getInstance().sendEventV2(CategoryType.LOTTERY, ActionType.LOTTERY_12_HOUR_CHECK_RESULT_SHOW,"", 0);
					lotterySyncInfo.setHasShowDrawLottery12HoursAlert(true);
					LotteryConfig.getInstance().setLotterySyncInfo(lotterySyncInfo);
					LotteryConfig.getInstance().save();
				}
			}
		}
		return;
	}
	    /**
     * 取消12小时未确认中奖彩票弹窗
     */
    public void dismiss12hoursDialog() {
		DTLog.i(tag, "dismiss12hoursDialog");
        if (lottery12HoursDialog != null && lottery12HoursDialog.isShowing()) {
            lottery12HoursDialog.dismiss();
            lottery12HoursDialog = null;
        }
    }




	//	uide显示控制===================================
	private GuideView guideView;
	private GuideContainer guideContainer;
	private boolean isVideoPlaySucceed;
	private boolean isEnterFeelingLucky;
	private boolean isHightValueCountry;
	enum GUIDE_TYPE{
		TYPE_VEDIO,
		TYPE_CHECK_IN,
		TYPE_FEELINGLUCKY
	}

	private void showGuide(GUIDE_TYPE type){

		DTLog.i(tag, "showGuide GUIDE_TYPE"+type);
//		if (!SharedPreferencesUtil.isNewRegisterUser())return;
		if (isADBannerShow){
			scrollView.scrollTo(0,mADBanner.getHeight());
		}
		switch (type){
			case TYPE_VEDIO:
				if (!SharedPreferencesUtil.isNeedShowVideoGuide())return;
				SharedPreferencesUtil.saveIsNeedShowVideoGuide(true);
				if (isVideoPlaySucceed){
					if (isHightValueCountry){
						showGuideVideoHighValueCountry();
					}else {
						showGuideVideoLowValueCountry();
					}
				}else{
					if (isHightValueCountry){
						showGuideNoVideoHighValueCountry();
					}else {
						showGuideNoVideoLowValueCountry();
					}
				}
				guideContainer.animIn(200);
				break;
			case TYPE_FEELINGLUCKY:
				if (!SharedPreferencesUtil.isNeedShowFeelingLuckyGuide())return;
				SharedPreferencesUtil.saveIsNeedShowFeelingLuckyGuide(true);
				showGuideFeelingLucky();
				guideContainer.animIn(200);
				break;
			case TYPE_CHECK_IN:
				if (!SharedPreferencesUtil.isNeedShowCheckInGuide())return;
				SharedPreferencesUtil.saveIsNeedShowCheckInGuide(true);
				showGuideCheckIn();
				guideContainer.animIn(200);
				break;
		}
	}

	private void showGuideVideoHighValueCountry() {
		guideContainer.clearAllView();
		DTLog.i(tag, "showGuideVideoHighValueCountry");
		View inflate = getLayoutInflater().inflate(R.layout.guide_item_watch_more_videos, null);
		View inflate1 = getLayoutInflater().inflate(R.layout.guide_item_complete_an_offer, null);
		guideContainer.addBlackRect(videoBtn, inflate, null,10);
		guideContainer.addBlackRect(offerBtn,  null,inflate1,10);
	}

	private void showGuideVideoLowValueCountry() {
		guideContainer.clearAllView();
		DTLog.i(tag, "showGuideVideoLowValueCountry");
		View view = getLayoutInflater().inflate(R.layout.guide_item_watch_more_videos, null);
		guideContainer.addBlackRect(videoBtn, view, null,10);
		List<View> viewList = new ArrayList<>();
		viewList.add(checkinLayout);
		viewList.add(mFeelingLuckyText);
		View inflate = getLayoutInflater().inflate(R.layout.guide_item_check_in_for_novideo_highvalue, null);
		View inflate1 = getLayoutInflater().inflate(R.layout.guide_item_test_lucky_1_credits, null);
		guideContainer.addBlackRect(checkinLayout, inflate, null,-10);
		guideContainer.addBlackRect(mFeelingLuckyText, null, inflate1,-10);
	}

	private void showGuideNoVideoLowValueCountry() {
		guideContainer.clearAllView();
		DTLog.i(tag, "showGuideNoVideoLowValueCountry");
		View inflate = getLayoutInflater().inflate(R.layout.guide_item_check_in_for_free_credits, null);
		View inflate1 = getLayoutInflater().inflate(R.layout.guide_item_test_lucky_1_credits, null);
		guideContainer.addBlackRect(checkinLayout, inflate, null,-10);
		guideContainer.addBlackRect(mFeelingLuckyText, null, inflate1,-10);
	}

	private void showGuideNoVideoHighValueCountry() {
		guideContainer.clearAllView();
		DTLog.i(tag, "showGuideNoVideoHighValueCountry");
		View view = getLayoutInflater().inflate(R.layout.guide_item_fast_get_free_credits, null);
		guideContainer.addBlackRect(offerBtn, view, null,10);

		View inflate = getLayoutInflater().inflate(R.layout.guide_item_check_in_for_novideo_highvalue, null);
		View inflate1 = getLayoutInflater().inflate(R.layout.guide_item_test_lucky_1_credits, null);
		guideContainer.addBlackRect(checkinLayout, inflate, null,-10);
		guideContainer.addBlackRect(mFeelingLuckyText, null, inflate1,-10);
	}

	private void showGuideCheckIn() {
		guideContainer.clearAllView();
		DTLog.i(tag, "showGuideCheckIn");

		View inflate = getLayoutInflater().inflate(R.layout.guide_item_check_in, null);
		guideContainer.addBlackRect(checkinLayout, inflate, null,-10);
	}

	private void showGuideFeelingLucky() {
		guideContainer.clearAllView();
		DTLog.i(tag, "showGuideFeelingLucky");
		View inflate = getLayoutInflater().inflate(R.layout.guide_item_feeling_lucky, null);
		guideContainer.addBlackRect(mFeelingLuckyText, inflate, null,-10);
	}
}

