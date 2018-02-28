package com.xingdu.BaiChuan;

import android.app.Application;
import android.widget.Toast;

import com.alibaba.baichuan.android.trade.AlibcTrade;
import com.alibaba.baichuan.android.trade.AlibcTradeSDK;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeCallback;
import com.alibaba.baichuan.android.trade.callback.AlibcTradeInitCallback;
import com.alibaba.baichuan.android.trade.model.AlibcShowParams;
import com.alibaba.baichuan.android.trade.model.OpenType;
import com.alibaba.baichuan.android.trade.page.AlibcAddCartPage;
import com.alibaba.baichuan.android.trade.page.AlibcBasePage;
import com.alibaba.baichuan.android.trade.page.AlibcDetailPage;
import com.alibaba.baichuan.android.trade.page.AlibcMyCartsPage;
import com.alibaba.baichuan.android.trade.page.AlibcMyOrdersPage;
import com.alibaba.baichuan.android.trade.page.AlibcPage;
import com.alibaba.baichuan.android.trade.page.AlibcShopPage;
import com.alibaba.baichuan.trade.biz.AlibcConstants;
import com.alibaba.baichuan.trade.biz.context.AlibcTradeResult;
import com.alibaba.baichuan.trade.biz.login.AlibcLogin;
import com.alibaba.baichuan.trade.biz.login.AlibcLoginCallback;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * This class echoes a string called from JavaScript.
 */
public class BaiChuan extends CordovaPlugin {

    public static boolean init = true;

    @Override
    public boolean execute(final String action, final JSONArray args, final CallbackContext callbackContext) throws JSONException {

        Application application = cordova.getActivity().getApplication();
        initBaichuan(action, args, callbackContext, application);


        return true;
    }

    /**
     * 初始化百川sdk
     *
     * @param action
     * @param args
     * @param callbackContext
     * @param application
     */
    private void initBaichuan(final String action, final JSONArray args, final CallbackContext callbackContext, Application application) {
        //电商SDK初始化
        AlibcTradeSDK.asyncInit(application, new AlibcTradeInitCallback() {
            @Override
            public void onSuccess() {
//        Toast.makeText(cordova.getActivity().getApplication(), "初始化成功", Toast.LENGTH_SHORT).show();
// 初始化成功，设置相关的全局配置参数
                try {
                    if (action.equals("detailPage")) {
                        JSONObject j = args.getJSONObject(0);
                        int type = j.getInt("type");
                        String skuId = j.getString("skuId");
                        if (type == 0)
                            pageDetail(skuId, callbackContext);
                        else {
                            pageUrl(skuId, callbackContext);
                        }
                    } else if (action.equals("addCart")) {
                        String skuId = args.getString(0);
                        addCart(skuId, callbackContext);
                    } else if (action.equals("shopPage")) {
                        String shopId = args.getString(0);
                        shopPage(shopId, callbackContext);
                    } else if (action.equals("myCart")) {
                        myCart(callbackContext);
                    } else if (action.equals("pageOrder")) {
                        int status = args.getInt(0);
                        pageOrder(status, callbackContext);
                    } else if (action.equals("login")) {
                        login();
                    } else if (action.equals("logout")) {
                        logout();
                    }
                } catch (Exception e) {
//          Toast.makeText(cordova.getActivity().getApplication(), "初始化失败" + e, Toast.LENGTH_SHORT).show();
                    callbackContext.error("百川初始化失败:" + e);
                }
            }

            @Override
            public void onFailure(int code, String msg) {
//        Toast.makeText(cordova.getActivity().getApplication(), "初始化失败,错误码=" + code + " / 错误消息=" + msg, Toast.LENGTH_SHORT).show();
                callbackContext.error("初始化失败,错误码=" + code + " / 错误消息=" + msg);
            }
        });
    }

    /**
     * 根据商品id 打开商品内页
     *
     * @param skuId
     * @param callbackContext
     */
    private void pageDetail(String skuId, CallbackContext callbackContext) {
        try {
            //商品详情page
            AlibcBasePage detailPage = new AlibcDetailPage(skuId);
            pageDe(detailPage, callbackContext);
        } catch (Exception e) {
            callbackContext.error("Expected on " + e);
        }
    }

    /**
     * 根据url打开商品内页
     *
     * @param url
     * @param callbackContext
     */
    private void pageUrl(String url, CallbackContext callbackContext) {
        try {
            AlibcBasePage page = new AlibcPage(url);
            pageDe(page, callbackContext);
        } catch (Exception e) {
            callbackContext.error("Expected on " + e);
        }
    }

    /**
     * 用户订单列表页
     *
     * @param callbackContext
     */
    private void pageOrder(int status, CallbackContext callbackContext) {
        try {

            //实例化我的订单打开page
            AlibcBasePage ordersPage = new AlibcMyOrdersPage(status, true);
            pageDe(ordersPage, callbackContext);

        } catch (Exception e) {
            callbackContext.error("Expected on " + e);
        }
    }

    /**
     * 我的购物车
     *
     * @param callbackContext
     */
    private void myCart(CallbackContext callbackContext) {
        try {


            AlibcBasePage myCartsPage = new AlibcMyCartsPage();
            pageDe(myCartsPage, callbackContext);
        } catch (Exception e) {
            callbackContext.error("Expected on " + e);
        }
    }

    /**
     * 添加购物车
     *
     * @param itemId
     * @param callbackContext
     */
    private void addCart(String itemId, CallbackContext callbackContext) {
        try {
            AlibcBasePage addCardPage = new AlibcAddCartPage(itemId);
            pageDe(addCardPage, callbackContext);
        } catch (Exception e) {
            callbackContext.error("Expected on " + e);
        }
    }

    /**
     * 商户店铺页
     *
     * @param shopId
     * @param callbackContext
     */
    private void shopPage(String shopId, CallbackContext callbackContext) {
        try {

            AlibcBasePage shopPage = new AlibcShopPage(shopId);
            pageDe(shopPage, callbackContext);
        } catch (Exception e) {
            callbackContext.error("Expected on " + e);
        }
    }

    private void pageDe(AlibcBasePage page, CallbackContext callbackContext) {
        try {
            //提供给三方传递配置参数
            Map<String, String> exParams = new HashMap<String, String>();
            exParams.put(AlibcConstants.ISV_CODE, "appisvcode");


            //设置页面打开方式
            AlibcShowParams showParams = new AlibcShowParams(OpenType.H5, false);

            //使用百川sdk提供默认的Activity打开detail
            AlibcTrade.show(cordova.getActivity(), page, showParams, null, exParams,
                    new AlibcTradeCallback() {
                        @Override
                        public void onTradeSuccess(AlibcTradeResult tradeResult) {
                            //打开电商组件，用户操作中成功信息回调。tradeResult：成功信息（结果类型：加购，支付；支付结果）
//            Toast.makeText(cordova.getActivity(), "succ ",
//              Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailure(int code, String msg) {
                            //打开电商组件，用户操作中错误信息回调。code：错误码；msg：错误信息

                            Toast.makeText(cordova.getActivity(), "err: " + msg,
                                    Toast.LENGTH_SHORT).show();
                        }
                    });


        } catch (Exception e) {
            callbackContext.error("Expected on " + e);
        }
    }

    /**
     * 授权登陆登录
     */
    public void login() {
        AlibcLogin alibcLogin = AlibcLogin.getInstance();
        alibcLogin.showLogin(new AlibcLoginCallback() {
            @Override
            public void onSuccess(int i) {
                Toast.makeText(cordova.getActivity(), "登录成功 ",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int code, String msg) {
                Toast.makeText(cordova.getActivity(), "登录失败 ",
                        Toast.LENGTH_LONG).show();
            }
        });
    }


    /**
     * 退出登录
     */
    public void logout() {

        AlibcLogin alibcLogin = AlibcLogin.getInstance();

        alibcLogin.logout(new AlibcLoginCallback() {
            @Override
            public void onSuccess(int i) {
                Toast.makeText(cordova.getActivity(), "登出成功 ",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(int i, String s) {
                Toast.makeText(cordova.getActivity(), "退出登陆失败 ",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

}
