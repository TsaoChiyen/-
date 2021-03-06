package com.chengxin.net;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.chengxin.Entity.AdDomainList;
import com.chengxin.Entity.AddGroup;
import com.chengxin.Entity.Bill;
import com.chengxin.Entity.BillList;
import com.chengxin.Entity.ChatImg;
import com.chengxin.Entity.CheckFriends;
import com.chengxin.Entity.CommentGoodsState;
import com.chengxin.Entity.CountryList;
import com.chengxin.Entity.Demand;
import com.chengxin.Entity.DemandList;
import com.chengxin.Entity.Favorite;
import com.chengxin.Entity.Financier;
import com.chengxin.Entity.FinancingGoods;
import com.chengxin.Entity.FinancingGoodsList;
import com.chengxin.Entity.FriendsLoop;
import com.chengxin.Entity.FriendsLoopItem;
import com.chengxin.Entity.Goods;
import com.chengxin.Entity.GoodsCommentEntity;
import com.chengxin.Entity.GoodsEntity;
import com.chengxin.Entity.GroupList;
import com.chengxin.Entity.LoginResult;
import com.chengxin.Entity.MarketsCategory;
import com.chengxin.Entity.Meeting;
import com.chengxin.Entity.MeetingItem;
import com.chengxin.Entity.Merchant;
import com.chengxin.Entity.MerchantCategory;
import com.chengxin.Entity.MerchantEntity;
import com.chengxin.Entity.MerchantGoods;
import com.chengxin.Entity.MerchantGoodsList;
import com.chengxin.Entity.MerchantInfo;
import com.chengxin.Entity.MerchantList;
import com.chengxin.Entity.MerchantMenu;
import com.chengxin.Entity.MessageInfo;
import com.chengxin.Entity.MessageResult;
import com.chengxin.Entity.MessageType;
import com.chengxin.Entity.MorePicture;
import com.chengxin.Entity.Order;
import com.chengxin.Entity.OrderList;
import com.chengxin.Entity.Picture;
import com.chengxin.Entity.Room;
import com.chengxin.Entity.RoomList;
import com.chengxin.Entity.RoomUsrList;
import com.chengxin.Entity.ShopAreaList;
import com.chengxin.Entity.ShopGoodsList;
import com.chengxin.Entity.ShopServiceList;
import com.chengxin.Entity.UniPayResult;
import com.chengxin.Entity.UserList;
import com.chengxin.Entity.VersionInfo;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.map.BMapApiApp;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;
import com.google.zxing.common.StringUtils;

public class WeiYuanInfo  implements Serializable{
	private static final long serialVersionUID = 1651654562644564L;

	/**www.kobego.com 115.29.32.248*/
	private static final String SERVER_STR = "http://121.40.214.35:82/"; //"http://119.84.73.193/"; //   
	public static final String SERVER_PREFIX = SERVER_STR + "index.php";
	public static final String CODE_URL =SERVER_STR;
	public static final String HEAD_URL = SERVER_STR + "index.php";
	public static final int PAGESIZE = 20;

	public static final String APPKEY ="0e93f53b5b02e29ca3eb6f37da3b05b9";

	public String request(String url, WeiYuanParameters params, String httpMethod, int loginType) throws WeiYuanException{
		String rlt = null;
		rlt = Utility.openUrl(url, httpMethod, params,loginType);
		if(rlt != null && rlt.length() != 0){
			int c = rlt.indexOf("{");
			if(c != 0){
				rlt = rlt.subSequence(c, rlt.length()).toString();
			}
		}

		return rlt;
	}

	public String requestProtocol(String url, WeiYuanParameters params, String httpMethod) throws WeiYuanException{
		String rlt = null;
		rlt = Utility.openUrl(url, httpMethod, params,0);
		return rlt;

	}

	/**
	 *  用户注册协议(/user/apiother/regist)
	 *  @throws WeiYuanException
	 */
	public String getProtocol() throws WeiYuanException {
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX+ "/user/apiother/regist";
		String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
		if(reString != null && !reString.equals("") ){
			Log.d("reString", reString);
			return reString;
		}
		return null;
	}

    /**
     *  帮助中心(/user/apiother/help)
     *  返回的是一个html的页面
     *  @throws WeiYuanException
     */
    public String getHelpHtml() throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        bundle.add("appkey",APPKEY);
        String url = SERVER_PREFIX + "/user/apiother/help";
        String reString = requestProtocol(url, bundle, Utility.HTTPMETHOD_POST);
        if(reString != null && !reString.equals("") && !reString.equals("null") ){
            return reString;
        }
        return null;
    }

	/**
	 *  获取验证码(/user/apiother/getCode)
	 *  @param isGetCode true=getcode false=-clean
	 *  @throws WeiboException
	 */
	public WeiYuanState getVerCode(String tel,int type) throws WeiYuanException{
		if (tel == null || tel.equals("")) {
			return null;
		}
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("phone",tel);
		if(type!=0){
			bundle.add("type",String.valueOf(type));
		}
		String url = SERVER_PREFIX + "/user/apiother/getCode";
	 
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		try {
			if(reString != null && !reString.equals("null") && !reString.equals("")){
				return new WeiYuanState(new JSONObject(reString));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *  验证验证码(/user/apiother/checkCode)
	 *  @param phone	true	string	手机号
	 *  @param code     true	string	验证码
	 */
	public WeiYuanState checkVerCode(String phone, String code) throws WeiYuanException{
		if (phone == null || phone.equals("")) {
			return null;
		}

		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("phone",phone);
		bundle.add("code",String.valueOf(code));
		String url = SERVER_PREFIX + "/user/apiother/getCode";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		try {
			if(reString != null && !reString.equals("null") && !reString.equals("")){
				return new WeiYuanState(new JSONObject(reString));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 *  更新用户的Gps user/api/upateGps
	 *  @param uid false string 登录用户id
	 *  @param lat true string 纬度
	 *  @param lng true string 经度
	 *  @throws WeiYuanException
	 */
	public WeiYuanState updataGps(String lat,String lng) throws WeiYuanException{
		if ((lat == null || lat.equals(""))
				|| (lng == null || lng.equals(""))) {
			return null;
		}

		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("lat",lat);
		bundle.add("lng",lng);
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/upateGps";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		try {
			if(reString != null && !reString.equals("null") && !reString.equals("")){
				return new WeiYuanState(new JSONObject(reString));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 附近的人 /user/api/nearbyUser
	 * @param uid false 登录用户id
	 * @param lat string 纬度
	 * @param lng string 经度
	 * @throws WeiYuanException 
	 */

	public UserList getNearyUserList(double lat,double lng,int page) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("lat", String.valueOf(lat));

		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("lng", String.valueOf(lng));
		bundle.add("page", String.valueOf(page));
		String url = SERVER_PREFIX + "/user/api/nearbyUser";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new UserList(reString,0);
		}
		return null;
	}

	//二、登陆注册
	/**
	 * 
	 * 1、注册
		①　非学员注册(/user/api/regist)
		1、HTTP请求方式 GET/POST
		2、是否需要登录 false
		3、支持格式 JSON
		参数	必选	类型	说明
		phone	true	string	用户的手机号
		password	true	string	密码
		name	true	string	用户姓名
		validCode	true	string	邀请码验证码
	 */
	public LoginResult register(String phone,String password,String validCode) throws WeiYuanException{
		LoginResult register = null;
		WeiYuanParameters bundle = new WeiYuanParameters();
		if ((phone == null || phone.equals(""))
				|| (password == null || password.equals(""))) {
			return null;
		}
		bundle.add("phone",phone);
		bundle.add("password",password);

		String url = SERVER_PREFIX + "/user/api/regist";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new LoginResult(reString);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}

		}
		return register;
	}

	/**
	 * 
	 * 用户登录 /user/api/login
	 * @param phone		true	string	用户的手机号
	 * @param password	true	string	密码
	 * @return
	 * @throws WeiYuanException
	 * http://192.168.1.12/weiyuan/index.php/user/api/login?phone=13689084790&password=123456
	 */
	public LoginResult getLogin(String phone, String password) throws WeiYuanException {
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("phone", phone);
		bundle.add("password", password);
		String url = SERVER_PREFIX  + "/user/api/login";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			return new LoginResult(reString.trim());
		}
		return null;
	}

	/**
	 * ①　忘记密码，获取新密码(/api/index/forgetpwd)
	 * @param phone	true	int	
	 * @throws WeiYuanException 
	 */
	public WeiYuanState findPwd(String phone) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("phone", phone);
		String url = SERVER_PREFIX  + "/api/index/forgetpwd";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	} 

	/**
	 * 
	 * 更改用户资料 post方式请求
	 * /user/api/edit
	@param picture     true     file 上传图片
	@param nickname    true     String 昵称
	@param gender	   false	string	 0-男 1-女 2-未知 未填写
	@param sign	       false	string	签名
	@param province  false	int	省
	@param city	   	   false	int	市
	 * @throws WeiYuanException 
	 * 
	 */

	public LoginResult modifyUserInfo(
			String file,String nickname,int gender,
			String sign,String provinceid,String city) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();

		//必填选项
		bundle.add("appkey", APPKEY);
		if(file!=null && !file.equals("") && file.length()>0){
			List<MorePicture> listpic = new ArrayList<MorePicture>();
			listpic.add(new MorePicture("picture",file));
			bundle.addPicture("pic", listpic);
		}

		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(nickname !=null && !nickname.equals("")){
			bundle.add("nickname",nickname);
		}
		bundle.add("gender",String.valueOf(gender));

		bundle.add("sign",sign);

		if(provinceid!=null && !provinceid.equals("")){
			bundle.add("province", provinceid);
		}
		if(city!=null && !city.equals("")){
			bundle.add("city", city);
		}

		String url = SERVER_PREFIX + "/user/api/edit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new LoginResult(reString);
		}
		return null;
	}

	/**
	 * 根据id获取用户资料
	 * @param uid
	 * @return
	 * @throws WeiYuanException
	 */
	public LoginResult getUserInfo(String uid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY); 
		bundle.add("fuid", uid);
		bundle.add("uid", String.valueOf(WeiYuanCommon.getUserId(BMapApiApp.getInstance())));
		String url = SERVER_PREFIX +  "/user/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new LoginResult(reString);
		}

		return null;
	}

	/**
	 * 16.	设置星标朋友(/user/api/setStar)
	 * fuid	true	int	用户id
	 */
	public LoginResult setStar(String fuid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		if(fuid == null || fuid.equals("")){
			return null;
		}
		bundle.add("fuid", fuid);
		bundle.add("uid", String.valueOf(WeiYuanCommon.getUserId(BMapApiApp.getInstance())));
		String url = SERVER_PREFIX +  "/user/api/setStar";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new LoginResult(reString);
		}

		return null;
	}

	/**
	 * 
	 * 上传文件
	 * @param f_upload
	 * @param type 1-图片 2-声音
	 * @return
	 * @throws WeiYuanException
	 */
	public ChatImg uploadFile(String f_upload, int type) throws WeiYuanException{
		ChatImg chatImg = null;
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		List<MorePicture> listpic = new ArrayList<MorePicture>();
		listpic.add(new MorePicture("f_upload", f_upload));
		bundle.addPicture("pic", listpic);

		bundle.add("type", String.valueOf(type));
		String url = SERVER_PREFIX +"/api/index/upload";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("data")){
					String s = json.getString("data");
					if(s!=null && !s.equals("")){
						chatImg = ChatImg.getInfo(s);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return chatImg;
	}

	//四、通讯录
	//1.朋友列表
	/**
	 * ①　朋友列表(/user/api/friendList)
	 * 获取好友列表
	 * @param page
	 * @return
	 * @throws WeiYuanException
	 */

	public GroupList getUserList() throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", String.valueOf(WeiYuanCommon.getUserId(BMapApiApp.getInstance())));
		String url = SERVER_PREFIX +"/user/api/friendList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new GroupList(reString);
		}
		return null;
	}

	//2、添加朋友
	// 1.1 添加朋友
	/**
	 * ①好友申请(/user/api/applyAddFriend)
	 * /api/user/to_friend
	 * @param action to_friend
	 * @param uid 
	 * @param fuid
	 * http://www.deedkey.com/friend/Index/action?action=to_friend&uid=200269&fuid=53
	 */
	public WeiYuanState applyFriends(String userID,String fuid,String reason) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if((userID == null || userID.equals(""))
				|| (fuid == null || fuid.equals(""))
				/*|| (reason == null || reason.equals(""))*/){
			return null;
		}
		bundle.add("uid",userID);
		bundle.add("fuid",fuid);
		bundle.add("content",reason);
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX + "/user/api/applyAddFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * ②同意加为好友(/user/api/agreeAddFriend)
	 * @param action be_friend
	 * @param uid 
	 * @param fuid
	 */
	public WeiYuanState agreeFriends(String fuid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid",fuid);
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX + "/user/api/agreeAddFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * ③ 拒绝加为好友(/user/api/refuseAddFriend)
	 * @param action refuse_f
	 * @param uid
	 * @param toUid 被拒绝的uid
	 */
	public WeiYuanState denyFriends(String toUid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid",toUid);
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX + "/user/api/refuseAddFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}	

	/**
	 * ④ 删除好友(/user/api/deleteFriend)
	 *  @param uid
	 *  @param fuid 好友uid
	 */
	public WeiYuanState cancleFriends(String fuid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid",fuid);
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX + "/user/api/deleteFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}	

	//1.2搜号码
	/**
	 * 
	 * ① 通过手机号或昵称搜索(/user/api/search)
	 * @param userName
	 * @param page
	 * @return
	 * @throws WeiYuanException
	 */
	public UserList search_number(String search,int page) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("search", search);

		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("page", String.valueOf(page));
		String url = SERVER_PREFIX + "/user/api/search";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new UserList(reString,0);
		}
		return null;
	}

	//1.3 从手机通讯录列表添加
	/**
	 * ① 导入手机通讯录(/user/api/importContact)
	 */
	public CheckFriends getContactUserList(String phone) throws WeiYuanException{
		if (phone == null || phone.equals("") || phone.contains("null")) {
			return null;
		}
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("phone",phone);
		bundle.add("uid", String.valueOf(WeiYuanCommon.getUserId(BMapApiApp.getInstance())));
		String url = SERVER_PREFIX + "/user/api/importContact";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new CheckFriends(reString);
		}
		return null;
	}

	//3、新的朋友
	/**
	 * ①　新的朋友(/api/user/newfriend)
	 * @param phone	true	string	格式 手机1,手机2,手机3,手机4
	 * @param uid 登录用户id
	 * @throws WeiYuanException 
	 */

	public UserList getNewFriend(String phone) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		if(phone == null || phone.equals("")
				|| phone.startsWith(",")){
			return null;
		}
		bundle.add("phone", phone);

		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/newFriend";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			//Log.d("addFriend", reString);
			return new UserList(reString,1);

		}
		return null;
	}


	/**
	 * ①　添加关注与取消关注(/api/publics/follow)
	 * @param publics_id	true	int	公众号的ID
	 * @param uid 登录用户id
	 */
	public WeiYuanState addFocus( String subUserID) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();

		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("publics_id", subUserID);
		bundle.add("appkey",APPKEY);
		String url = SERVER_PREFIX + "/api/publics/follow";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * ① 朋友分组(/api/user/group)
	 * @param uid 登录用户id
	 * @param type true int 0-名字 1-地区 2-频率 3-添加时间 4-课程 5-行业
	 * @throws WeiYuanException 
	 */
	public UserList getContactGroupList(int type) throws WeiYuanException{

		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("type",String.valueOf(type));
		String url = SERVER_PREFIX +"/api/user/group";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") ){
			//Log.d("getContactGroupList", reString);
			try {
				return new UserList(reString,0);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return  null;
	}

	/**
	 * 添加关注与取消关注(/api/user/follow)
	 * @param uid 登录用户id
	 * @param fuid 要关注的用户ID
	 * @param type 0-取消关注 1-添加关注
	 * @param appkey
	 * @throws WeiYuanException 
	 * 
	 */
	public WeiYuanState addfocus(String fuid/*,int type*/) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		/*	bundle.add("type",String.valueOf(type));*/
		String url = SERVER_PREFIX + "/api/user/follow";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			//Log.d("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	// dalong
	/**
	 *  
	 * 加入黑名单 /user/api/black

	 * @param blackUid
	 * @return
	 * @throws WeiYuanException
	 */
	public WeiYuanState addBlock(String blackUid) throws WeiYuanException {
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", blackUid);
		String url = SERVER_PREFIX + "/user/api/black";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			//Log.d("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}



	/**
	 *  
	 * 举报好友 /api/user/jubao
	 * @param fuid
	 * @param content
	 * @param type 1-用户 2-订阅号
	 * @return
	 * @throws WeiYuanException
	 */
	public WeiYuanState reportedFriend(String fuid, String content,int type) throws WeiYuanException {
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		bundle.add("type",String.valueOf(type));
		bundle.add("content", content);
		String url = SERVER_PREFIX +  "/api/user/jubao";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			//Log.d("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}


	/**
	 *  
	 * 获取黑名单列表/user/api/blackList
	 * @param page
	 * @return
	 * @throws WeiYuanException
	 */
	public UserList getBlockList(/*int page*/) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		/*	bundle.add("page", String.valueOf(page));
		bundle.add("pageSize", String.valueOf(Common.LOAD_SIZE));*/
		String url = SERVER_PREFIX + "/user/api/blackList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			//Log.d("reString", reString);

			return new UserList(reString,0);
		}

		return null;
	}

	/**
	 *  
	 * 取消黑名单 /user/api/black
	 * @param fuid
	 * @return
	 * @throws WeiYuanException
	 */
	public WeiYuanState cancelBlock(String fuid) throws WeiYuanException {
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		String url = SERVER_PREFIX +"/user/api/black";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			//Log.d("reString", reString);

			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 *  添加收藏(/user/api/favorite)	
	 *  @param uid true 登陆用户id
	 * 	@throws WeiYuanException 
	 *  @param fuid	true	int	被收藏人的uid
	 *  @param otherid	false	int	如果是收藏的群组的消息，就传入此id
	 *  @param content	true	string	收藏的内容
	 */
	public WeiYuanState favoreiteMoving(String fuid,String groupId,
			String content) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if((content == null || content.equals(""))
				&& (fuid == null || fuid.equals(""))
				&& (groupId == null || groupId.equals(""))){
			return null;
		}
		bundle.add("content", content);
		if(fuid!=null && !fuid.equals("")){
			bundle.add("fuid", fuid);
		}

		if(groupId!=null && !groupId.equals("")){
			bundle.add("otherid", groupId);
		}

		String url = SERVER_PREFIX +"/user/api/favorite";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			Log.d("favoreiteMoving", reString);
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	/**
	 * http://117.78.2.70/index.php/user/api/favoriteList?uid=200000
	 *  收藏列表(/user/api/favoriteList)	
	 *  @parem uid true 登陆用户id
	 *  http://117.78.2.70/index.php/user/api/favoriteList?appkey=0e93f53b5b02e29ca3eb6f37da3b05b9&uid=200018&page=1, count=20
	 */
	public Favorite favoriteList(int page) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(page!=0){
			bundle.add("page",String.valueOf(page));
		}
		bundle.add("count", "20");
		String url = SERVER_PREFIX +"/user/api/favoriteList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			Log.d("favoriteList", reString);
			return new Favorite(reString);
		}
		return null;
	}



	/**
	 * 删除收藏(/user/api/deleteFavorite)
	 * @param uid  true 登陆用户id
	 * @parem favoriteid	true	int	收藏记录的id	
	 */

	public WeiYuanState canclefavMoving(int favoriteid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(favoriteid == 0){
			return null;
		}
		bundle.add("favoriteid", String.valueOf(favoriteid));

		String url = SERVER_PREFIX +"/user/api/deleteFavorite";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			Log.d("favoreiteMoving", reString);
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}




	/**
	 * 创建组
	 * @param name			组名
	 * @return
	 * @throws WeiYuanException
	 */
	public AddGroup AddGroup(String name) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("teamName", name);
		bundle.add("action", "addTeam");
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "friend/Index/action";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new AddGroup(reString);
		}

		return null;
	}

	/**
	 * 检测更新 /version/api/update
	 * @param version		版本号
	 * @return
	 * @throws WeiYuanException
	 */
	public VersionInfo checkUpgrade(String version) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		if(version == null || version.equals("")){
			return null;
		}
		bundle.add("os", "android");
		bundle.add("version", version.substring(1));
		String url = SERVER_PREFIX +"/version/api/update";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new VersionInfo(reString);
		}

		return null;
	}

	/**
	 * /session/api/add
	 * ①　1.	创建临时会话并添加用户
	 * @param name  true 会话名称 
	 * @param uids  true 所邀请用户ID串  格式：id1,id2,id3,id4
	 * @return
	 * @throws WeiYuanException
	 */
	public Room createRoom(String groupname, String uids) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		Log.d("createRoom", "groupName:"+groupname);
		bundle.add("name", groupname);
		bundle.add("uids", uids);
		String url = SERVER_PREFIX + "/session/api/add";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new Room(reString);
		}

		return null;
	}


	/**
	 * ②　 添加用户到一个会话(/session/api/addUserToSession)
	 * @param groupid     true int 群组id
	 * @param inviteduids true string  参数格式: uid1,uid2,uid3
	 * @return
	 * @throws WeiYuanException
	 */
	public WeiYuanState inviteUsers(String sessionid, String uids) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		bundle.add("uids", uids);
		String url = SERVER_PREFIX + "/session/api/addUserToSession";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 扫一扫/session/api/join
	 * @param sessionid     true int 群组id
	 * @return
	 * @throws WeiYuanException
	 */
	public Room join(String sessionid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		String url = SERVER_PREFIX + "/session/api/join";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new Room(reString);
		}

		return null;
	}



	/**
	 * ③　把用户从某个群踢出(/session/api/remove)
	 * @param groupid			房间ID
	 * @param fuid				被踢用户ID
	 * @return
	 * @throws WeiYuanException
	 */
	public WeiYuanState kickParticipant(String sessionid, String fuid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", String.valueOf(sessionid));
		bundle.add("fuid", fuid);
		String url = SERVER_PREFIX +"/session/api/remove";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}


	/**
	 * 获取某个用户的所在的群（房间列表）
	 * /session/api/userSessionList
	 * @param fuid	false	String	不传则查看自己的。传了则查看别人的
	 * @return
	 * @throws WeiYuanException
	 */
	public RoomList getRoomList(String fuid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX +"/session/api/userSessionList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new RoomList(reString);
		}

		return null;
	}

	/**
	 * 获取某个房间的用户列表(/api/group/getGroupUserList)
	 * @param groupid			房间ID
	 * @return
	 * @throws WeiYuanException
	 */
	public RoomUsrList getRoomUserList(String groupid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("groupid", groupid);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/api/group/getGroupUserList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new RoomUsrList(reString);
		}

		return null;
	}

	/**
	 * ④　删除群(/session/api/delete)
	 * @param sessionid			群组id
	 * @return
	 * @throws WeiYuanException
	 */
	public WeiYuanState deleteRoom(String sessionid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", String.valueOf(sessionid));
		String url = SERVER_PREFIX + "/session/api/delete";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 退出房间(/session/api/quit)
	 * @param sessionid				房间ID
	 * @return
	 * @throws WeiYuanException
	 */
	public WeiYuanState exitRoom(String sessionid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		String url = SERVER_PREFIX + "/session/api/quit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 7.群组聊天
	 * ⑨　修改群资料(/session/api/edit)
	 * @param uid       true  string 登陆用户id
	 * @param sessionid	true	int	群id
	 * @param name	false	string	群名称
	 * @param groupnickname	false	string	群昵称
	 */

	public WeiYuanState modifyGroupNickName(String sessionid,String name) 
			throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		bundle.add("name", name);
		String url = SERVER_PREFIX + "/session/api/edit";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}


	/**
	 * 修改我的群昵称 /session/api/setNickname
	 * @param uid true string 登陆用户id
	 * @param mynickname	true	string	设置的群昵称
	 * @param sessionid	true	int	群组id
	 */
	public WeiYuanState modifyMyNickName(String sessionid,String groupnickname) 
			throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		bundle.add("mynickname", groupnickname);
		String url = SERVER_PREFIX + "/session/api/setNickname";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}




	/**
	 * 7.群组聊天
	 * ⑩　设置群类型(/api/group/ispublic)
	 * @param uid       true  string 登陆用户id
	 * @param groupid	true	int	群id
	 * @param ispublic	true	int	0-公开群 1-私密群
	 * 
	 */
	public WeiYuanState isPublicGroup(String groupid,int ispublic) 
			throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("groupid", groupid);
		bundle.add("ispublic", String.valueOf(ispublic));
		String url = SERVER_PREFIX + "/api/group/ispublic";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}


	/**
	 * 7.群组聊天
	 * 11　设置是否接收消息(/session/api/getmsg)
	 * @param uid       true  string 登陆用户id
	 * @param groupid	true	int	群id
	 * @param isgetmsg	true	int	0-不接收 1-接收
	 * 
	 */
	public WeiYuanState isGetGroupMsg(String groupid,int isgetmsg) 
			throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", groupid);
		bundle.add("isgetmsg", String.valueOf(isgetmsg));
		String url = SERVER_PREFIX + "/session/api/getmsg";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	/**
	 * 17.	设置是否接收另一用户的消息(/user/api/setGetmsg)
	 * @param  fuid	true	int	用户id
	 */
	public WeiYuanState setMsg(String fuid) 
			throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		String url = SERVER_PREFIX + "/user/api/setGetmsg";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}



	/**
	 * 群聊
	 * 4.	会话详细(/session/api/detail)
	 *  @param uid       true  string 登陆用户id
	 *  @param groupid	true	int	群id
	 */

	public Room getRoomInfoById(String sessionid) 
			throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("sessionid", sessionid);
		String url = SERVER_PREFIX + "/session/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new Room(reString);
		}

		return null;
	}






	//六、我
	// 1.获取省市 行业 课程
	/**
	 * ①　省市(/user/apiother/areaList)
	 * @param uid 
	 * @throws WeiYuanException 
	 */
	public CountryList getCityAndContryUser() throws WeiYuanException{
		String reString = FeatureFunction.getAssestsFile("AreaCode");
//		Log.d("getCityAndContryUser", reString);
		if(reString != null && !reString.equals("") && !reString.equals("null")){
			return new CountryList(reString);
		}
		return null;
	}


	/**
	 * 
	 * 6 修改备注名(/user/api/remark )	
	 * @param fuid
	 * @param remark
	 * @return
	 * @throws WeiYuanException
	 */
	public WeiYuanState remarkFriend(String fuid, String remark) throws WeiYuanException {
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey", APPKEY);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("fuid", fuid);
		bundle.add("remark", remark);
		String url = SERVER_PREFIX + "/user/api/remark";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	//5. 朋友圈

	/**
	 * 设置用户封面图 post方式请求
	 * /friend/api/setCover
	 * @param uid	true	string	当前登陆用户ID
	 * @param action	true	frontCover	
	 *  @param f_upload	true		上传图片
	 * @throws QiyueException 
	 */ 
	public WeiYuanState uploadUserBg(String userID,List<MorePicture> listpic) throws WeiYuanException{
		WeiYuanState status = null;
		WeiYuanParameters bundle = new WeiYuanParameters();
		if(listpic!=null && listpic.size()>0){
			bundle.addPicture("pic", listpic);
		}
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/friend/api/setCover";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				JSONObject jsonObj = new JSONObject(reString);
				if (jsonObj!=null && !jsonObj.equals("") && !jsonObj.equals("null")) {
					status = new WeiYuanState(jsonObj);
				}
			} catch (Exception e) {
				e.printStackTrace();
				//return null;
			}
		}
		return status;
	}

	/**
	 *  1.发布分享(/friend/api/add)	
	 *  @param uid  true 登陆用户id
	 *  @param  picList 上传图片	false	string	最多上传6张，命名picture1 picture2.....
	 *  @param  content	true	string	分享文字内容
	 *  @param  lng	false	string	经度
	 *  @param  lat	false	string	纬度
	 *  @param  address	false	string	经纬度所在的地址
	 *  @param  visible	false	string	不传表示是公开的，传入格式：id1,id2,id3

	 */

	public WeiYuanState addShare(List<MorePicture> picList,String content,
			String lng,String lat,String address,String visible) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if((picList == null || picList.size()<=0)
				&& (content == null || content.equals(""))){
			return null;
		}

		if(picList!=null && picList.size()>0){
			bundle.addPicture("pic", picList);
		}

		if(content !=null && !content.equals("")){
			bundle.add("content", content);
		}

		if(lng!=null && !lng.equals("")){
			bundle.add("lng",lng);
		}

		if(lat != null && !lat.equals("")){
			bundle.add("lat",lat);
		}

		if(address!=null && !address.equals("")){
			bundle.add("address",address);
		}

		if(visible!=null && !visible.equals("")
				&& !visible.startsWith(",")){
			bundle.add("visible",visible);
		}

		String url = SERVER_PREFIX + "/friend/api/add";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new WeiYuanState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}

	/**
	 * 2. 删除分享(/friend/api/delete)	
	 * @param uid string true 登陆用户id
	 * @param fsid int true 分享id
	 */ 

	public WeiYuanState deleteShare(int fsid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(fsid == 0){
			return null;
		}

		bundle.add("fsid",String.valueOf(fsid));

		String url = SERVER_PREFIX + "/friend/api/delete";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {////
				return new WeiYuanState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 3.分享详细(/friend/api/detail)
	 * @param uid string true 登陆用户id
	 * @param fsid int true 分享id
	 */ 

	public FriendsLoopItem shareDetail(int fsid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(fsid == 0){
			return null;
		}

		bundle.add("fsid",String.valueOf(fsid));

		String url = SERVER_PREFIX + "/friend/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new FriendsLoopItem(reString);

			} catch (Exception e) {
				e.printStackTrace();
				return  null;
			}
		}
		return  null;
	}

	/**
	 * 4. 朋友圈列表(/friend/api/shareList)
	 * @param uid true 登录用户id
	 * @param page int 请求的页数
	 */
	public FriendsLoop shareList(int page) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);

		if(page!=0){
			bundle.add("page",String.valueOf(page));
		}
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/friend/api/shareList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new FriendsLoop(reString);
		}
		return null;
	} 


	/**
	 * 5.朋友相册(/friend/api/userAlbum)	
	 * fuid 
	 */
	public FriendsLoop myHomeList(int page,String fuid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		if(fuid!=null && !fuid.equals(WeiYuanCommon.getUserId(BMapApiApp.getInstance()))){
			bundle.add("fuid",fuid);
		}
		if(page!=0){
			bundle.add("page",String.valueOf(page));
		}
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/friend/api/userAlbum";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			return new FriendsLoop(reString);
		}
		return null;
	} 

	/**
	 * 6.	添加 取消赞(/friend/api/sharePraise)
	 * @param uid true 登陆用户id
	 * @param fsid true int 分享id
	 */

	public WeiYuanState sharePraise(int fsid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(fsid == 0){
			return null;
		}

		bundle.add("fsid",String.valueOf(fsid));

		String url = SERVER_PREFIX + "/friend/api/sharePraise";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 7.回复(/friend/api/shareReply)
	 * @param uid true 登陆用户id
	 * @param fsid true int 分享id
	 * @param fuid true int 回复哪个人
	 */
	public WeiYuanState shareReply(int fsid,String toUid,String content) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(fsid == 0 ||( toUid == null || toUid.equals(""))){
			return null;
		}

		bundle.add("content", content);
		bundle.add("fsid",String.valueOf(fsid));
		bundle.add("fuid", toUid);

		String url = SERVER_PREFIX + "/friend/api/shareReply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new WeiYuanState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}


	/**
	 * 8.	删除回复(/friend/api/deleteReply)
	 * @param uid true 登陆用户id 
	 * @param replyid	true	int	某条回复的id
	 */

	public WeiYuanState deleteReply(int replyid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(replyid == 0){
			return null;
		}

		bundle.add("replyid",String.valueOf(replyid));

		String url = SERVER_PREFIX + "/friend/api/deleteReply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 9.	设置朋友圈权限(/friend/api/setFriendCircleAuth)
	 * @param uid true 登陆用户id
	 * @param fuid true 要设置的用户id
	 * @param type true int  1-不看他（她）的朋友圈 2-不让他（她）看我的朋友圈
	 */

	public WeiYuanState setFriendCircleAuth(int type,String fuid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);

		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(type == 0 || (fuid == null || fuid.equals(""))){
			return null;
		}

		bundle.add("type",String.valueOf(type));
		bundle.add("fuid", fuid);

		String url = SERVER_PREFIX + "/friend/api/setFriendCircleAuth";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new WeiYuanState(new JSONObject(reString));

			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	//7.设置

	/**
	 * 
	 * 意见反馈 /user/api/feedback
	 * @param content
	 * @return
	 * @throws WeiYuanException
	 */
	public WeiYuanState feedback(String content) throws WeiYuanException {
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("content", content);
		String url = SERVER_PREFIX + "/user/api/feedback";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				JSONObject json = new JSONObject(reString);
				if(!json.isNull("state")){
					return new WeiYuanState(json.getJSONObject("state"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		return null;
	}


	/**
	 * 7.1 修改密码(/user/api/editPassword)
	 * @param uid true string 登陆用户id
	 * @param oldpassword true string 旧密码
	 * @param newpassword true string 新密码
	 */
	public WeiYuanState editPasswd(String oldpassword,String newpassword) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		if((oldpassword == null || oldpassword.equals(""))
				|| (newpassword == null || newpassword.equals(""))){
			return null;
		}
		bundle.add("oldpassword", oldpassword);
		bundle.add("newpassword", newpassword);
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/editPassword";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 根据姓名获取用户详细(/api/user/getUserByName)
	 * @param uid 登陆用户id
	 * @param name	true	string	用户姓名
	 */
	public LoginResult getUserByName(String name) throws WeiYuanException {
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		if(name == null || name.equals("")){
			return null;
		}
		bundle.add("name", name);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/api/user/getUserByName";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			return new LoginResult(reString.trim());
		}

		return null;

	}


	/**
	 * 设置加好友是否需要验证(/user/api/setVerify)
	 * verify int true 0-不验证 1-验证
	 * @return 
	 * @throws WeiYuanException
	 */
	public WeiYuanState setVerify(int verify) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/user/api/setVerify";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}	




	/**
	 * 发送消息接口
	 * @param messageInfo
	 * @return
	 * @throws BridgeException
	 * http://117.78.2.70/index.php/user/api/sendMessage?uid=200000
	 * &content=共和国乖乖&fromurl=http://117.78.2.70/Uploads/Picture/
	 * avatar/200000/s_f9f0399347f63dc71c8880d057403f97.jpg
	 * &voicetime=0&tag=0814a62b-5cf3-432d-a878-3da9c99af257
	 * &fromid=200000&fromname=萌萌哒&typechat=200&toname=小黄鸭,海洋天堂,漩涡鸣人&typefile=1&toid=17
	 */
	public MessageResult sendMessage(MessageInfo messageInfo,boolean isForward) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if(messageInfo == null){
			return null;
		}
		bundle.add("typechat", String.valueOf(messageInfo.typechat));
		bundle.add("tag", messageInfo.tag);
		if(!TextUtils.isEmpty(messageInfo.fromname)){
			bundle.add("fromname", messageInfo.fromname);
		}
		if(!TextUtils.isEmpty(messageInfo.fromid)){
			bundle.add("fromid", messageInfo.fromid);
		}

		if(!TextUtils.isEmpty(messageInfo.fromurl)){
			bundle.add("fromurl", messageInfo.fromurl);
		}
		bundle.add("toid", messageInfo.toid);
		if(!TextUtils.isEmpty(messageInfo.toname)){
			bundle.add("toname", messageInfo.toname);
		}

		if(!TextUtils.isEmpty(messageInfo.tourl)){
			bundle.add("tourl", messageInfo.tourl);
		}
		bundle.add("typefile", String.valueOf(messageInfo.typefile));

		if(!TextUtils.isEmpty(messageInfo.content)){
			bundle.add("content", messageInfo.content);
		}

		if(messageInfo.typefile == MessageType.PICTURE){

			if(isForward && !TextUtils.isEmpty(messageInfo.imageString)){
				bundle.add("image", messageInfo.imageString);
			}else{
				if(!TextUtils.isEmpty(messageInfo.imgUrlS)){
					List<MorePicture> fileList = new ArrayList<MorePicture>();
					fileList.add(new MorePicture("file_upload", messageInfo.imgUrlS));
					bundle.addPicture("pic", fileList);
				}
			}
			if(messageInfo.imgWidth!=0){
				bundle.add("width", String.valueOf(messageInfo.imgWidth));
			}

			if(messageInfo.imgHeight !=0){
				bundle.add("height", String.valueOf(messageInfo.imgHeight));
			}

		}else if(messageInfo.typefile == MessageType.VOICE){
			if(isForward && !TextUtils.isEmpty(messageInfo.voiceString)){
				bundle.add("voice", messageInfo.voiceString);
			}else  if(!TextUtils.isEmpty(messageInfo.voiceUrl)){
				List<MorePicture> fileList = new ArrayList<MorePicture>();
				fileList.add(new MorePicture("file_upload", messageInfo.voiceUrl));
				bundle.addPicture("pic", fileList);

			}
		}else if(messageInfo.typefile == MessageType.SMALL_VIDEO){
			List<MorePicture> fileList = new ArrayList<MorePicture>();
			fileList.add(new MorePicture("file_upload", messageInfo.videoUrl));
			if(messageInfo.imgUrlS != null && !messageInfo.imgUrlS.equals("")){
				fileList.add(new MorePicture("file_upload2", messageInfo.imgUrlS));
			}
			bundle.addPicture("pic", fileList);
		}

		if(messageInfo.mLat != 0){
			bundle.add("lat", String.valueOf(messageInfo.mLat));
		}

		if(messageInfo.mLng != 0){
			bundle.add("lng", String.valueOf(messageInfo.mLng));
		}

		if(!TextUtils.isEmpty(messageInfo.mAddress)){
			bundle.add("address", messageInfo.mAddress);
		}

		bundle.add("voicetime", String.valueOf(messageInfo.voicetime));
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX + "/user/api/sendMessage";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		if(reString != null && !reString.equals("") && !reString.equals("null")  && reString.startsWith("{")){
			Log.d("sendMessage", reString);
			return new MessageResult(reString);
		}

		return null;
	}


	//秘室
	/**
	 * 1.创建秘室(/meeting/api/add)
	 * @param uid  		true 	String 登陆用户id
	 * @param picture	false	string	上传logo图片
	 * @param name		true	string	秘室标题
	 * @param content	true	string	秘室主题
	 * @param start		true	int	开始时间戳
	 * @param end		true	int	结束时间戳
	 * @throws WeiYuanException 
	 */
	public WeiYuanState createMetting(String picture,String name,String content,
			long start,long end) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if(picture!=null && !picture.equals("")){
			List<MorePicture> listPic = new ArrayList<MorePicture>();
			listPic.add(new MorePicture("picture",picture));
			bundle.addPicture("pic", listPic);
		}
		if((name == null || name.equals("")) || (content == null || content.equals(""))
				|| start == 0 || end == 0){
			return null;	
		}
		bundle.add("name", name);
		bundle.add("content", content);
		bundle.add("start",String.valueOf(start));
		bundle.add("end",String.valueOf(end));

		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/add";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}


		return null;
	}


	/**
	 * 2.秘室详细(/meeting/api/detail)
	 * @param meetingid	true	string	秘室id
	 * @throws WeiYuanException 
	 */ 
	public MeetingItem mettingDetail(int meetingid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(meetingid == 0){
			return null;
		}
		bundle.add("meetingid",String.valueOf(meetingid));
		String url = SERVER_PREFIX  + "/meeting/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			return new MeetingItem(reString);
		}
		return null;
	}


	/**
	 * 3. 秘室列表(/meeting/api/meetingList)
	 * @param uid true String 登陆用户id
	 * @param  type	true	string	type 1-正在进行中 2-往期 3-我的
	 * @param page int 
	 * @throws WeiYuanException 
	 */
	public Meeting meetingList(int type,int page) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(type == 0){
			return null;
		}
		bundle.add("type",String.valueOf(type));
		bundle.add("page", String.valueOf(page));
		String url = SERVER_PREFIX  + "/meeting/api/meetingList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") ){
			return new Meeting(reString);

		}
		return null;
	}


	/**
	 * 4. 申请加入秘室(/meeting/api/apply)
	 * @param uid true String 登陆用户id
	 * @param meetingid	true	string	秘室id
	 * @throws WeiYuanException 
	 */
	public WeiYuanState applyMeeting(int meetingid,String reasion) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if( meetingid == 0 || (reasion == null || reasion.equals(""))){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("content", reasion);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/apply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}


	/**
	 * 5. 同意申请加入秘室(/meeting/api/agreeApply)	33
	 * @param uid true String 登陆用户id
	 * @param meetingid	true	string	秘室id
	 * @param fuid	true	int	申请用户id
	 */
	public WeiYuanState agreeApplyMeeting(int meetingid,String fuid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if( meetingid == 0 || (fuid == null || fuid.equals(""))){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("fuid", fuid);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/agreeApply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}



	/**
	 * 6. 不同意申请加入秘室(/meeting/api/disagreeApply)	34
	 * @param uid true String 登陆用户id
	 * @param meetingid	true	string	秘室id
	 * @paramfuid	true	int	申请用户id
	 */

	public WeiYuanState disagreeApplyMeeting(int meetingid,String fuid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if( meetingid == 0 || (fuid == null || fuid.equals(""))){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("fuid", fuid);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/disagreeApply";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/**
	 * 7. 邀请加入秘室(/meeting/api/invite)	34
	 * @param uid true String 登陆用户id
	 * @param meetingid	true	string	秘室id
	 * @param uids	true	int	被邀请用户id
	 */
	public WeiYuanState inviteMeeting(int meetingid, String uids) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		if(meetingid == 0 
				|| (uids == null || uids.equals("")
				|| uids.startsWith(",") || uids.endsWith(","))){
			return null;
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("uids", uids);
		String url = SERVER_PREFIX + "/meeting/api/invite";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}

		return null;
	}


	/**
	 * 10. 秘室的用户申请列表(/meeting/api/meetingApplyList)	36
	 * @param uid true String 登陆用户id
	 * @param meetingid	true	string	秘室id
	 * @throws WeiYuanException 
	 */
	public UserList meetingApplyList(int page,int meetingid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if( meetingid == 0){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("page", String.valueOf(page));
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/meetingApplyList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			return new UserList(reString,0);
		}
		return null;
	}



	/**
	 * 11. 用户活跃度排行(/meeting/api/huoyue)	37
	 * @param uid true String 登陆用户id
	 * @param meetingid	true	string	秘室id
	 */
	public UserList huoyueList(int page,int meetingid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if( meetingid == 0){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("page", String.valueOf(page));
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/huoyue";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			return new UserList(reString,0);
		}
		return null;
	}


	/**
	 * 12. 移除用户(/meeting/api/remove)	37
	 * @param uid true String 登陆用户id
	 * @param meetingid	true	string	秘室id
	 * @param fuid	true	int	要移除的用户
	 */
	public WeiYuanState removeMetUser(int meetingid,String fuid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if( meetingid == 0 || (fuid == null || fuid.equals(""))){
			return null;	
		}
		bundle.add("meetingid", String.valueOf(meetingid));
		bundle.add("fuid", fuid);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/meeting/api/remove";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}

	/***
	 * 商户类别(/shop/api/categroyList)
	 * @throws WeiYuanException
	 */
	public MerchantMenu getShopType(int shopType) throws WeiYuanException{
//		WeiYuanParameters bundle = new WeiYuanParameters();
		String url = SERVER_PREFIX  + "/shop/api/categroyList";

		if (shopType == 1) {
			url = SERVER_PREFIX  + "/basket/api/categroyList";
		}
//		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		
		if (url != null) {
			String reString = request(url, null, Utility.HTTPMETHOD_POST,0);

			if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
				return new MerchantMenu(reString);
			}
		}
		
		return null;
	}

	/***
	 * 商户类别(/shop/api/categroyList)
	 * @throws WeiYuanException
	 */
	public MarketsCategory getMarketsCategory() throws WeiYuanException{
//		WeiYuanParameters bundle = new WeiYuanParameters();
		String url = SERVER_PREFIX  + "/basket/api/categroyList";

//		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		
		if (url != null) {
			String reString = request(url, null, Utility.HTTPMETHOD_POST,0);

			if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
				return new MarketsCategory(reString);
			}
		}
		
		return null;
	}

	/**
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *  申请成为商户 /shop/api/apply
     *
     *	@param name         true    商户名称
     *	@param address      true    商户地址
     *	@param username     true    联系人
     *	@param phone        true    联系电话
     *	@param useraddress  true    联系地址
     *	@param lat                  经度
     *	@param lng                  纬度
     *	@param city         true    城市
     *	@param logo                 商户图标
     *	@param shoppaper            营业执照
     *	@param authpaper            授权证书
     *	@param bank                 银行名称
     *	@param bankname             银行用户名
     *	@param bankcard             银行账号
     *	@param content              备注信息
     *
	 *  @return
     *
	 *  @throws WeiYuanException
	 */
	public WeiYuanState applyMerchant(
			int shopType,
			String name,
            String address,
            String username,
            String phone,
            String useraddress,
            String lat,
            String lng,
            String city,
            String logo,
            String shoppaper,
            String authpaper,
            String bank,
            String bankuser,
            String account,
            String content) throws WeiYuanException{
		if ((name == null || name.equals("")) ||
            (username == null || username.equals("")) ||
            (phone == null || phone.equals(""))	||
            (address == null || address.equals(""))	||
            (useraddress == null || useraddress.equals(""))	||
            (city == null || city.equals("")) ){
			return null;	
		}
		
		WeiYuanParameters bundle = new WeiYuanParameters();
		
		bundle.add("name",name);
		bundle.add("username", username);
		bundle.add("phone", phone);
        bundle.add("address", address);
        bundle.add("useraddress", useraddress);
        bundle.add("city", city);

        if (lat != null && !lat.equals(""))             bundle.add("lat", lat);
        if (lng != null && !lng.equals(""))             bundle.add("lng", lng);
        if (bank != null && !bank.equals(""))           bundle.add("bank", bank);
        if (bankuser != null && !bankuser.equals(""))   bundle.add("bankName", bankuser);
        if (account != null && !account.equals(""))     bundle.add("bankCard", account);
        if (content != null && !content.equals(""))     bundle.add("content", content);

        if(logo!=null && !logo.equals("") ||
           shoppaper!=null && !shoppaper.equals("") ||
           authpaper!=null && !authpaper.equals("") ){
            List<MorePicture> listPic = new ArrayList<MorePicture>();
            
            if (logo != null && !logo.equals(""))
                listPic.add(new MorePicture("logo",logo));;
            
            if (shoppaper != null && !shoppaper.equals(""))
                listPic.add(new MorePicture("shopPaper",shoppaper));;
            
            if (authpaper != null && !authpaper.equals(""))
                listPic.add(new MorePicture("authPaper",authpaper));;
            
            bundle.addPicture("pic", listPic);
        }

        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		
		String url = SERVER_PREFIX + "/shop/api/apply";
		
		if (shopType == 1) {
			url = SERVER_PREFIX + "/basket/api/apply";
		}
		
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
		
		if (reString != null && !reString.equals("") && !reString.equals("null") ) {
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
				return null;
			}
		}
		
		return null;
	}

	//+++++++++2015-02-02 商户 2015-02-02 ++++++++++
	/**
	 *  
	 * 商家列表(/shop/api/shopList)
	 * @param lat			False	String	纬度 
	 * @param lng			false	string	经度
	 * @param categoryid	false	string	类别id
	 * @param page
	 * @return
	 * @throws WeiYuanException
	 */
	public MerchantEntity getMerchantkList(
			int shopType,
			int page,
			int categoryid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("categoryid",String.valueOf(categoryid));
		bundle.add("page", String.valueOf(page));
		bundle.add("lat",String.valueOf( WeiYuanCommon.getCurrentLat(BMapApiApp.getInstance())));
		bundle.add("lng",String.valueOf( WeiYuanCommon.getCurrentLng(BMapApiApp.getInstance())));
		/*	bundle.add("pageSize", String.valueOf(Common.LOAD_SIZE));*/
		String url = null;
		
		if (shopType == 0) {
			url = SERVER_PREFIX + "/shop/api/shopList";
		} else {
			url = SERVER_PREFIX + "/basket/api/shopList";
		}

		if (url != null) {
			String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

			if(reString != null && !reString.equals("") && !reString.equals("null")){
				//Log.d("reString", reString);

				return new MerchantEntity(reString);
			}
		}

		return null;
	}

    /**
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	商家列表
     *
     *  @param categoryid   商品分类id
     *  @param areaId       商品区域id
     */
    public MerchantEntity getShopList(
    		int shopType,
    		int page,
    		int categoryid,
    		Double lat,
    		Double lng,
    		String city) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();

        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        bundle.add("lat",String.valueOf(lat));
        bundle.add("lng",String.valueOf(lng));
        if (categoryid > 0) bundle.add("categoryid",String.valueOf(categoryid));
        if (page > 1) bundle.add("page", String.valueOf(page));
        if (city != null && city.length() > 0) bundle.add("city", city);

        String url = null;
        
        if (shopType == 0) {
            url = SERVER_PREFIX + "/shop/api/shopList";
        } else {
            url = SERVER_PREFIX + "/basket/api/shopList";
        }
        
        if (url != null) {
            try {
                String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
                
                if(reString != null && !reString.equals("") && !reString.equals("null")){
                    return new MerchantEntity(reString);
                }
            } catch (WeiYuanException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    /**
     *	Copyright © 2014 sam Inc. All rights reserved.
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	商品列表
     *
     *  @param shopid       商户id
     *  @param categoryid   商品分类id
     *  @param areaid       商品地区id
     *  @param sort         价格排序 1：升序，2：降序
     *  @param page
     *
     */
    public GoodsEntity getGoodsList(
    		int shopType,
    		int page,
    		int shopid,
    		int categoryid,
    		String city,
    		int sort) throws WeiYuanException  {
        WeiYuanParameters bundle = new WeiYuanParameters();

        if (page > 1)
            bundle.add("page", String.valueOf(page));

        if (shopid > 0)
            bundle.add("shopid", String.valueOf(shopid));
        
        if (categoryid > 0)
            bundle.add("categoryid", String.valueOf(categoryid));
        
        if (city != null && !city.isEmpty())
            bundle.add("city", city);
        
        if (sort > 0)
            bundle.add("sort", String.valueOf(sort));

        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = null;
        
        if (shopType == 0) {
            url = SERVER_PREFIX + "/shop/api/goodsList";
        } else {
            url = SERVER_PREFIX + "/basket/api/goodsList";
        }

        if (url != null) {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);
            
            if(reString != null && !reString.equals("") && !reString.equals("null")){
                return new GoodsEntity(reString);
            }
        }
        
        return null;
    }

    //---------2015-02-05商品列表2015-02-05 -------------

	/**
	 * 商品列表 /shop/api/goodsList
	 * @param page
	 * @return
	 * @throws WeiYuanException
	 */
	public GoodsEntity getGoodsList(
			int shopType,
			int page,
			int shopid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		if(shopid == 0){
			return null;
		}
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("shopid", String.valueOf(shopid));
		bundle.add("page", String.valueOf(page));
		/*bundle.add("pageSize", String.valueOf(Common.LOAD_SIZE));*/
		
		String url = null;
		
		if (shopType == 0)
			url = SERVER_PREFIX + "/shop/api/goodsList";
		else
			url = SERVER_PREFIX + "/basket/api/goodsList";
			
		if (url != null) {
			String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

			if(reString != null && !reString.equals("") && !reString.equals("null")){
				//Log.d("reString", reString);

				return new GoodsEntity(reString);
			}
		}

		return null;
	}
	
    //---------2015-02-05商品列表2015-02-05 -------------

	/***
     *
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
	 * 5.	商品详细(/shop/api/detail)
	 * @param goods_id	true	String	商品id
	 * @throws WeiYuanException 
	 */
	public Goods getGoodsDetail(
			int shopType,
			String goods_id) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if(goods_id == null || goods_id.equals("")){
			return null;
		}
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("goodid", goods_id);
		String url = null; 
		
		if (shopType == 0) {
			url = SERVER_PREFIX + "/shop/api/detail";
		} else {
			url = SERVER_PREFIX + "/basket/api/detail";
		}

		if (url != null) {
			String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

			if(reString != null && !reString.equals("") && !reString.equals("null")){
				Log.d("getGoodsDetail", reString);
				return new Goods(reString);
			}
		}

		return null;
	}

	/**
	 * 收藏取消收藏(/shop/api/favorite)
	 * @param uid true 登录用户id
	 * @param goods_id	true	String	商品id
	 * @param action	true	int	0-取消收藏 1-收藏
	 * @throws WeiYuanException 
	 */
	public WeiYuanState cancleOrCollection(
			int shopType,
			String goods_id,
			int action) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if(goods_id == null || goods_id.equals("")){
			return null;
		}
		
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("goods_id", goods_id);
		bundle.add("action", String.valueOf(action));
		
		String url = SERVER_PREFIX + "/shop/api/favorite";
		
		if (shopType == 1) {
			url = SERVER_PREFIX + "/basket/api/favorite";
		}
		
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.d("getGoodsDetail", reString);
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 *对商品添加评论(/shop/api/addComment)
	 *@param uid 		true 	登录用户id
	 *@param goods_id	true	String	商品id
	 *@param star		true	int	星数
	 *@param content	true	string	内容
	 * @throws WeiYuanException 
	 *
	 */
	public CommentGoodsState forGoodsAddComment(String goods_id,int star,String content) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if((goods_id == null || goods_id.equals(""))
				|| (content == null || content.equals(""))
				|| star == 0){
			return null;
		}
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("goods_id", goods_id);
		bundle.add("content", content);
		bundle.add("star", String.valueOf(star));
		String url = SERVER_PREFIX + "/shop/api/addComment";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.d("getGoodsDetail", reString);
			try {
				return new CommentGoodsState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	/**
	 * 对商品评论列表(/shop/api/commentList)
	 * @param uid 		true string 登录用户id
	 * @param goods_id	true	String	商品id
	 * @throws WeiYuanException 
	 */
	public GoodsCommentEntity forGoodsCommentList(String goods_id,int page) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if(goods_id == null || goods_id.equals("")){
			return null;
		}
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("goods_id", goods_id);
		bundle.add("page",String.valueOf(page));
		String url = SERVER_PREFIX + "/shop/api/commentList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.d("getGoodsDetail", reString);
			return new GoodsCommentEntity(reString);
		}
		return null;
	}

	/**
	 * 购物车数据
	 * shop/api/cartGoodsList
	 * @param uid true 登录用户id
	 * @param goods_id string 商品id
	 */
	public MerchantEntity getShoppingCartList(
			int shoptype,
			String goods_id) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("goods_id", goods_id);
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		/*	bundle.add("pageSize", String.valueOf(Common.LOAD_SIZE));*/
		String url = SERVER_PREFIX + "/shop/api/cartGoodsList";
		
		if (shoptype == 1) {
			url = SERVER_PREFIX + "/basket/api/cartGoodsList";
		}
		
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.d("getShoppingCartList", reString);

			return new MerchantEntity(reString);
		}

		return null;
	}

	/**
	 * 9.	提交订单(/shop/api/submitOrder)
     * @param type 			true	String  支付方式
	 * @param goods			true	String	商品格式：商品id1*count1,id2*count2
	 * @param username		true	string	联系人
	 * @param phone			true	string	电话
	 * @param address		true	string	地址
	 * @param content		false	string	备注
	 * @param shopid		true	int	商家Id
	 * @return 
	 * @throws WeiYuanException 
	 */

	public Order submitOrder(
			int shopType,
			int type,
			String goods,
			String username,
			String phone,
			String address,
			String content,
			int shopid) throws WeiYuanException {
		WeiYuanParameters bundle = new WeiYuanParameters();

		if(((goods == null || goods.equals(""))	|| goods.endsWith(","))
				|| (username == null || username.equals(""))
                || (type == 0)
                || (address == null || address.equals(""))
				|| shopid == 0){
			return null;
		}

        bundle.add("type", String.valueOf(type));
        bundle.add("goods", goods);
        bundle.add("shopid", String.valueOf(shopid));
        bundle.add("username", username);
        bundle.add("phone",phone);
        bundle.add("address", address);

        if(content != null && !content.equals("")){
            bundle.add("content",content);
        }
		
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));

		String url = null;
		
		if (shopType == 0) {
			url = SERVER_PREFIX + "/shop/api/submitOrder";
		} else {
			url = SERVER_PREFIX + "/basket/api/submitOrder";
		}

		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.d("submitOrder", reString);
			try {
				return new Order(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;	
	}
	
	/**
	 * 订单付款(/shop/api/orderPay)
     * @param id 			true	String  订单id
	 * @return 
	 * @throws WeiYuanException 
	 */

	public UniPayResult payOrder(
			int shopType,
			String orderId) throws WeiYuanException {
		WeiYuanParameters bundle = new WeiYuanParameters();
		
		if (orderId == null || orderId.equals("") || orderId.equals("0")) {
			return null;
		}
		
        bundle.add("id", orderId);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));

		String url = null;
		
        if (shopType == 0) {
            url = SERVER_PREFIX + "/shop/api/orderPay";
        } else {
            url = SERVER_PREFIX + "/basket/api/orderPay";
        }

		String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.d("submitOrder", reString);
			try {
				return new UniPayResult(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;	
	}

	
	/**
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
	 *  添加商品(/shop/api/addGoods)
     *
     *  @param categoryid	true	int	类别id
	 *  @param logo			true	file	商品Logo
	 *  @param name			true	string	商品名称
	 *  @param price		true	string	商品价格
	 *  @param picture		true	file	商品图册 多张就1 2 3就可以了
	 *  @param content              string 商品详细
	 *  @param parameter            string 商品规格
     *  @param barcode              商品条码
	 *  @throws WeiYuanException
	 */
	public WeiYuanState addGoods(
			int shoptype,
			int categoryid,
            String goodsName,
            String price,
            List<MorePicture> picList,
            String content,
            String parameter,
            String barcode) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
        
		if((goodsName == null || goodsName.equals(""))
				|| (price == null || price.equals(""))
				|| categoryid == 0 
				|| (picList == null || picList.size() <= 0)){
			return null;
		}
		
        bundle.add("categoryid", String.valueOf(categoryid));
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        bundle.add("name", goodsName);
        bundle.add("barcode", barcode);
		bundle.add("price",price);
		
        if(picList!=null && picList.size()>0){
			bundle.addPicture("pic", picList);
		}
		
        if(content != null && !content.equals("")){
			bundle.add("content", content);
		}
		
        if(parameter != null && !parameter.equals("")){
			bundle.add("parameter", parameter);
		}
	
		String url = SERVER_PREFIX + "/shop/api/addGoods";
		
		if (shoptype == 1) {
			url = SERVER_PREFIX + "/basket/api/addGoods";
		}
		
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.d("addGoods", reString);
		
            try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
    
    /**
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	修改商品库(/shop/api/editGoods)
     *
     *  @param categoryid    商品分类   (必填)
     *  @param name          商品名称   (必填)
     *  @param price         商品价格   (必填)
     *  @param picture       图片(数组)
     *  @param content       备注
     *  @param parameter     规格参数
     *  @param logo          商品图标   (必填)
     *  @param barcode       商品条码
     *
     */
    public WeiYuanState editGoods(
    		int shoptype,
    		int goodsId,
            int categoryid,
            String goodsName,
            String price,
            List<MorePicture> picList,
            String content,
            String parameter,
            String barcode) throws WeiYuanException{
        
        if (goodsId == 0) {
            return null;
        }
        
        WeiYuanParameters bundle = new WeiYuanParameters();

        if (categoryid > 0)
            bundle.add("categoryid", String.valueOf(categoryid));

        if (goodsName != null && !goodsName.equals(""))
            bundle.add("name", goodsName);
        
        if (barcode != null && !barcode.equals(""))
            bundle.add("barcode", barcode);

        if (price != null && !price.equals(""))
            bundle.add("price",price);
        
        if(picList!=null && picList.size() > 0){
            bundle.addPicture("pic", picList);
        }
        
        if(content != null && !content.equals("")){
            bundle.add("content", content);
        }
        
        if(parameter != null && !parameter.equals("")){
            bundle.add("parameter", parameter);
        }
        
        if (bundle.size() > 0) {
            bundle.add("id",String.valueOf(goodsId));
            bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));

            String url = SERVER_PREFIX + "/shop/api/editGoods";
            
            if (shoptype == 1) {
            	url = SERVER_PREFIX + "/basket/api/editGoods";
            }
            
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if(reString != null && !reString.equals("") && !reString.equals("null")){
                Log.d("editGoods", reString);
                
                try {
                    return new WeiYuanState(new JSONObject(reString));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return null;
    }

    /**
     *	Copyright © 2014 sam Inc. All rights reserved.
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	删除产品库(/shop/Api/delGoods)
     * @param mShopType 
     *
     *  @param goodsIds   产品 ID,多个用“,”逗号隔开
     *
     */
    public WeiYuanState deleteGoods(int shopType, List<String> goodsIds) throws WeiYuanException {
        if (goodsIds == null) return null;
        if (goodsIds.size() == 0) return null;
        
        String idsString = goodsIds.toString();
        idsString = idsString.substring(1, idsString.length() - 1);
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        bundle.add("id", idsString);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/Api/delGoods";
        
        if (shopType == 1) {
        	url = SERVER_PREFIX + "/basket/Api/delGoods";
        }
        
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);

        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("delGoods", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    
        return null;
    }

    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	客服列表(/shop/Api/serviceList)
     *
     *  @param  shopid: 商家 ID
     */
    public ShopServiceList serviceListWithShopId(
    		int shopType,
    		int shopid) throws WeiYuanException {
        if (shopid > 0) {
            WeiYuanParameters bundle = new WeiYuanParameters();
            bundle.add("shopid", String.valueOf(shopid));
            bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));

            String url = SERVER_PREFIX + "/shop/Api/serviceList";

            if (shopType == 1) {
                url = SERVER_PREFIX + "/basket/Api/serviceList";
            }
			
            try {
            	String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
	    		if(reString != null && !reString.equals("") && !reString.equals("null")){
	    			return new ShopServiceList(reString);
	    		}
			} catch (WeiYuanException e) {
				e.printStackTrace();
			}
        }
        
		return null;
    }
    
    /**
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	设置银行信息(/shop/api/editBank)
     *
     *	@param  bank    开户行
     *	@param  user    开户名
     *	@param  account 账号
     */
    public WeiYuanState updateBank(
    		int shoptype,
    		String bank,
    		String user,
    		String account) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();

        if((bank == null || bank.equals("")) ||
           (user == null || user.equals("")) ||
           (account == null || account.equals("")) ) {
            return null;
        }
    
        bundle.add("bank", bank);
        bundle.add("bankName", user);
        bundle.add("bankCard",account);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
    
        String url = SERVER_PREFIX + "/shop/api/editBank";
        
        if (shoptype == 1) {
        	url = SERVER_PREFIX + "/basket/api/editBank";        	
        }
        
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("editBank", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	添加客服
     *
     *  @param  name:           客服名称
     *  @param  serviceName:    客服账户
     *
     */
    public WeiYuanState addServiceOfShop(
    		int shoptype,
    		String name,
    		String serviceName) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if((name == null || name.equals("")) ||
           (serviceName == null || serviceName.equals("")) ) {
            return null;
        }
        
        bundle.add("name", name);
        bundle.add("username", serviceName);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/api/addService";
        
        if (shoptype == 1) {
        	url = SERVER_PREFIX + "/basket/api/addService";
        }
        
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("addService", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
		return null;
    }

    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	删除客服
     *
     *  @param  serviceId:  客服id
     *
     */
    public WeiYuanState delServiceOfShop(
    		int shopType,
    		String serviceId)
    				throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if((serviceId == null || serviceId.equals(""))) {
            return null;
        }
        
        bundle.add("id", serviceId);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = null;
        
        if (shopType == 0) {
            url = SERVER_PREFIX + "/shop/api/delService";
        } else {
            url = SERVER_PREFIX + "/basket/api/delService";
        }
        
        if (url != null) {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if(reString != null && !reString.equals("") && !reString.equals("null")){
                Log.d("delService", reString);
                
                try {
                    return new WeiYuanState(new JSONObject(reString));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        
        return null;
    }

    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	商户独立密码设置、修改
     *
     *  @param  password:   商户独立密码
     *  @throws WeiYuanException 
     *
     */
    public WeiYuanState setShopPassword(
    		int shoptype,
    		String password) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if((password == null || password.equals(""))) {
            return null;
        }
        
        bundle.add("password", password);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/api/setPassword";
        
        if (shoptype == 1) {
        	url = SERVER_PREFIX + "/basket/api/setPassword";        	
        }
        
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("setPassword", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
     }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	商户独立密码验证
     *
     *  @param  password:   商户独立密码
     *  @throws WeiYuanException 
     *
     */
    public WeiYuanState verifyShopPassword(
    		int shoptype,
    		String password) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if((password == null || password.equals(""))) {
            return null;
        }
        
        bundle.add("password", password);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/api/verify";
        
        if (shoptype == 1) {
        	url = SERVER_PREFIX + "/basket/api/verify";        	
        }
        
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("verifyPassword", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
     }
    
    /**
     *	@param mStatus 
     * @Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	商品库列表
     *
     *  @param  categoryid:  分类 ID
     *  @param  shopType:    状态 1: 未上架； 2: 已上架
     *
     */
    public ShopGoodsList getShopGoosList(
    		int shopType,
    		int page,
    		int categoryid,
    		int status) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        if (page > 1) bundle.add("page",    String.valueOf(page));
        if (categoryid > 0) bundle.add("categoryid",    String.valueOf(categoryid));
        if (status > 0)     bundle.add("status",        String.valueOf(status));
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/Api/productList";
        
        if (shopType == 1) {
        	url = SERVER_PREFIX + "/basket/Api/productList";
        }
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            if(reString != null && !reString.equals("") && !reString.equals("null")){
                return new ShopGoodsList(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	商品上下架
     * @param status 
     *
     *  @param status:   枚举值:1 下架 2 上架
     *  @param data:     数据格式:1,200,50 <=>商品 ID,价格,库存
     *                  这里的 data 应该是ShlefGoods数组
     *
     */
    public Goods shelfGoods(int shopType, int status, List <Goods> list) throws WeiYuanException {
        if (list == null || list.size() == 0) {
            return null;
        }
        
        if (status < 1 || status > 2) {
            return null;
        }
        
        WeiYuanParameters bundle = new WeiYuanParameters();

        String data = "";
        
        for (int i = 0; i < list.size(); i++) {
        	Goods item = list.get(i);
        	
        	if (item.name != null) {
            	String strItem = item.id + "," + String.valueOf(item.price) + "," + String.valueOf(item.number);
            	
            	if (i == list.size() - 1) {
                	data = data + strItem;
            	} else {
                	data = data + strItem + ";";
            	}
        	}
        }

        if (data.length() > 0) {
            bundle.add("data", data);
            bundle.add("status", String.valueOf(status));
            bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
            
            String url = SERVER_PREFIX + "/shop/api/goodStatus";
            
            if (shopType == 1) {
            	url = SERVER_PREFIX + "/basket/api/goodStatus";
            }
            
            try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if(reString != null && !reString.equals("") && !reString.equals("null")){
                Log.d("goodStatus", reString);
                
                    return new Goods(reString);
            }
            } catch (WeiYuanException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	修改商品库
     *
     *  @param goodsId       商品id    (必填)
     *  @param price         商品价格   (必填)
     *  @param number        商品库存量
     *      data:     数据格式:1,200,50 <=>商品 ID,价格,库存
     *                  这里的 data 应该是ShlefGoods数组
     */
    public Goods editShelfGoods(
    		int shoptype,
    		String goodsId,
    		String price,
    		String number) throws WeiYuanException {
        if (goodsId == null || goodsId.equals("")  || goodsId.equals("0")) {
            return null;
        }
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        String data = goodsId + "," + price + "," + number;
        
        if (data.length() > 0) {
            bundle.add("data", data);
            bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
            
            String url = SERVER_PREFIX + "/shop/api/goodStatus";
            
            if (shoptype == 1) {
            	url = SERVER_PREFIX + "/basket/api/goodStatus";
            }
            
            try {
	            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
	            
	            if(reString != null && !reString.equals("") && !reString.equals("null")){
	                Log.d("goodStatus", reString);
                    return new Goods(reString);
	            }
            } catch (WeiYuanException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	订单列表
     *
     *  @param status           订单状态
     *  @param type             用户角色 1： 商家，2： 买家
     */
    public OrderList getOrderList(
    		int shopType,
    		int page,
    		int status,
    		int type) throws WeiYuanException {
        
        if (type == 1 || type == 2) {
            WeiYuanParameters bundle = new WeiYuanParameters();

            bundle.add("type", String.valueOf(type));
            
            if (page > 1)   bundle.add("page", String.valueOf(page));
            if (status > 0) bundle.add("status", String.valueOf(status));
            bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
            
            String url = SERVER_PREFIX + "/shop/Api/orderList";
            
            if (shopType == 1) {
                url = SERVER_PREFIX + "/basket/Api/orderList";
            }
            
            try {
                String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
                if(reString != null && !reString.equals("") && !reString.equals("null")){
                    return new OrderList(reString);
                }
            } catch (WeiYuanException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	订单状态更新
     *
     *  @param id        订单 ID
     *  @param status    1:等待发货
     *                   2:已发货
     *                   3:未付款
     *                   4:退货中
     *                   5:已退单
     *                   6:已完成
     *                   7:已退货
     *                   8:结款中
     *                   9:已结款
     */
    public WeiYuanState updateOrder(
    		int shopType,
    		String orderId,
    		String status) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("id", orderId);
        bundle.add("status", status);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = null;
        
        if (shopType == 0) {
            url = SERVER_PREFIX + "/shop/api/orderStatus";
        } else {
            url = SERVER_PREFIX + "/basket/api/orderStatus";
        }

        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("orderStatus", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	订单退单处理
     *
     *  @param id        订单 ID
     *  @param reason    退单理由
     *  @note status     5:已退单
     */
    public WeiYuanState retreatOrder(
    		int shopType,
    		String orderId,
    		String reason) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("id", orderId);
        bundle.add("status", "5");
        bundle.add("content", reason);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = null;
        
        if (shopType == 0) {
            url = SERVER_PREFIX + "/shop/api/orderStatus";
        } else {
            url = SERVER_PREFIX + "/basket/api/orderStatus";
        }

        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("orderStatus", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	订单发货处理
     *
     *  @param id           订单 ID
     *  @param logistics    物流公司
     *  @param logistics    运单号
     *  @note status        2:已发货
     *
     */
    public WeiYuanState deliveryOrder(
    		int shopType,
    		String orderId,
    		String logistics,
    		String waybill) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("id", orderId);
        bundle.add("type", "1");
        bundle.add("status", "2");
        bundle.add("logcompany", logistics);
        bundle.add("lognumber", waybill);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = null;
        
        if (shopType == 0) {
            url = SERVER_PREFIX + "/shop/api/orderStatus";
        } else {
            url = SERVER_PREFIX + "/basket/api/orderStatus";
        }
        
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("orderStatus", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	订单收货处理
     *
     *  @param id           订单 ID
     *  @note status        6:已收货
     *
     */
    public WeiYuanState recieveGoodsByOrderId(
    		int shopType,
    		String orderId) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("id", orderId);
        bundle.add("type", "2");
        bundle.add("status", "6");
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = null;

        if (shopType == 0) {
            url = SERVER_PREFIX + "/shop/api/orderStatus";
        } else {
            url = SERVER_PREFIX + "/basket/api/orderStatus";
        }

        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("orderStatus", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    /**
     *	@param mShopType 
     * @Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	申请结款
     */
    public WeiYuanState applySettle(int shopType, String orderIds) throws WeiYuanException {
        if (orderIds == null || orderIds.equals("")) {
            return null;
        }
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("id", orderIds);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/api/applyPayment";
        
        if (shopType == 1) {
        	url = SERVER_PREFIX + "/basket/api/applyPayment";
        }
        
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("applyPayment", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    /**
     *	@param mShopType 
     * @Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	可结款列表
     */
    public OrderList accountSettleable(int shopType) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
       
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/Api/paymentList";
        
        if (shopType == 1) {
        	url = SERVER_PREFIX + "/basket/Api/paymentList";
        }
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            if(reString != null && !reString.equals("") && !reString.equals("null")){
                return new OrderList(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
        }

        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	历史结款列表
     */
    public OrderList accountHistory(int shopType) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/Api/paymentHis";
 
        if (shopType == 1) {
            url = SERVER_PREFIX + "/basket/Api/paymentHis";
        }
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            if(reString != null && !reString.equals("") && !reString.equals("null")){
                return new OrderList(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     *	@param shopType 
     * @Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	异常款项申诉
     *
     *  @param  content: 申诉内容
     */
    public WeiYuanState abnormalAppeal(int shopType, String content) throws WeiYuanException {
        if (content == null || content.equals("")) {
            return null;
        }
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("content", content);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/api/addAppeal";
        
        if (shopType == 1) {
            url = SERVER_PREFIX + "/basket/api/addAppeal";
        }
        
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("addAppeal", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	添加需求
     *	@param content  需求内容
     *	@param lat      当前纬度
     *	@param lng      当前经度
     */
    public Demand addDemand(String content, String lat, String lng) throws WeiYuanException {
        if (content == null) return null;
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("content", content);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        if (lat != null && !lat.equals("")) {
            bundle.add("lat", lat);
        }

        if (lng != null && !lng.equals("")) {
            bundle.add("lng", lng);
        }
        
        String url = SERVER_PREFIX + "/shop/api/addDemand";
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("addDemand", reString);
            
            try {
                return new Demand(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	需求列表
     *	@param key      搜索关键字，匹配content字段
     *	@param lat      当前纬度
     *	@param lng      当前经度
     */
    public DemandList getDemand(int page, String key, String lat, String lng) throws WeiYuanException {
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if (key != null && !key.equals("")) {
            bundle.add("keywords", key);
        }
        
        if (page > 1) {
            bundle.add("page", String.valueOf(page).toString());
        }

        if (lat != null && !lat.equals("")) {
            bundle.add("lat", lat);
        }
        
        if (lng != null && !lng.equals("")) {
            bundle.add("lng", lng);
        }
        
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/Api/demandList";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if(reString != null && !reString.equals("") && !reString.equals("null")){
                return new DemandList(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	编辑收货地址
     *
     *  @param  address:  收货地址
     */
    public WeiYuanState editShippingAddress(String orderId, String address) throws WeiYuanException {
        if (address == null || orderId == null || orderId.equals(""))
            return null;
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("address", address);
        bundle.add("id", orderId);
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/Api/editAddress";
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("editAddress", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }

    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	添加账单
     */
	public Bill addBill(Bill bill) throws WeiYuanException {
        if (bill.type < 1 || bill.type > 2) {
            return null;
        }

        WeiYuanParameters bundle = bill.getParameters();

        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/Api/addBill";
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("addBill", reString);
            
            try {
                return new Bill(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
	}

    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	编辑账单
     */
	public Bill editBill(Bill bill) throws WeiYuanException  {
        if (bill.id <= 0) {
            return null;
        }
        
        if (bill.type < 1 || bill.type > 2) {
            return null;
        }

        WeiYuanParameters bundle = bill.getParameters();
        
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/Api/editBill";
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("editBill", reString);
            
            try {
                return new Bill(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
	}

	public WeiYuanState clearBill(int billId, boolean withAll) throws WeiYuanException {
        if (billId <= 0) {
            return null;
        }
        
        WeiYuanParameters bundle = new WeiYuanParameters();

        bundle.add("id", String.valueOf(billId));
        
        if (withAll)
            bundle.add("repayAll", "true");
        	
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/Api/repayBill";
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("repayBill", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
	}

	/**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	删除账单
     *
     *	@param billId       账单id
     */
    public WeiYuanState deleteBill(int billId) throws WeiYuanException {
        if (billId <= 0) {
            return null;
        }
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("id", String.valueOf(billId));
        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/Api/delBill";
        String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
        if(reString != null && !reString.equals("") && !reString.equals("null")){
            Log.d("delBill", reString);
            
            try {
                return new WeiYuanState(new JSONObject(reString));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	账单列表
     */
    public BillList getBillList(int page) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if (page > 1) {
            bundle.add("page", String.valueOf(page));
        }

        bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/shop/Api/billList";

        try {
        	String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
        	if(reString != null && !reString.equals("") && !reString.equals("null")){
            	return new BillList(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	入驻商户申请
     *
     *  @param  company:        公司名称
     *  @param  workPaper:      工作证明图
     *  @param  idcard:         身份证图
     *  @param  certificate:    从业资格图
     *  @param  city:           城市 ID
     *  @param  lat:            经度
     *  @param  lng:            纬度
     *  @param  address:        办公地址
     */
    public Financier applyFinacier(String company,
                                  String address,
                                  String city,
                                  double lat,
                                  double lng,
                                  String workPermit,
                                  String idCard,
                                  String certificate) throws WeiYuanException {
        
        
        if (company == null || company.equals("") ||
            address == null || address.equals("") ||
            city == null || city.equals("")) {
            return null;
        }
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("company",company);
        bundle.add("address", address);
        bundle.add("city", city);
        
        if (lat != 0)             bundle.add("lat", String.valueOf(lat));
        if (lng != 0)             bundle.add("lng", String.valueOf(lng));
        
        if((workPermit!=null && !workPermit.equals("")) ||
           (certificate!=null && !certificate.equals("")) ||
           (idCard!=null && !idCard.equals(""))){
            List<MorePicture> listPic = new ArrayList<MorePicture>();
            
            if (workPermit != null && !workPermit.equals(""))
                listPic.add(new MorePicture("workPaper",workPermit));;
            
            if (certificate != null && !certificate.equals(""))
                listPic.add(new MorePicture("certificate",certificate));;
            
            if (idCard != null && !idCard.equals(""))
                listPic.add(new MorePicture("idCard",idCard));;
            
            bundle.addPicture("pic", listPic);
        }
        
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/financ/api/apply";

        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new Financier(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	入驻商户编辑
     *
     *  @param  id:             融资商户 ID
     *  @param  company:        公司名称
     *  @param  workPaper:      工作证明图
     *  @param  idcard:         身份证图
     *  @param  certificate:    从业资格图
     *  @param  city:           城市 ID
     *  @param  lat:            经度
     *  @param  lng:            纬度
     *  @param  address:        办公地址
     */
    public Financier editFinacier(int id,
    							  String company,
						          String address,
						          String city,
						          double lat,
						          double lng,
						          String workPermit,
						          String idCard,
						          String certificate) throws WeiYuanException {
        if (id == 0) return null;
        
        WeiYuanParameters bundle = new WeiYuanParameters();

        bundle.add("shopid",String.valueOf(id));
        
        if (company != null && !company.equals("")) bundle.add("company",company);
        if (address != null && !address.equals("")) bundle.add("address", address);
        if (city != null && !city.equals("")) bundle.add("city", city);
        
        if (lat != 0)             bundle.add("lat", String.valueOf(lat));
        if (lng != 0)             bundle.add("lng", String.valueOf(lng));
        
        if((workPermit!=null && !workPermit.equals("")) ||
           (certificate!=null && !certificate.equals("")) ||
           (idCard!=null && !idCard.equals(""))){
            List<MorePicture> listPic = new ArrayList<MorePicture>();
            
            if (workPermit != null && !workPermit.equals(""))
                listPic.add(new MorePicture("workPaper",workPermit));;
            
            if (certificate != null && !certificate.equals(""))
                listPic.add(new MorePicture("certificate",certificate));;
            
            if (idCard != null && !idCard.equals(""))
                listPic.add(new MorePicture("idcard",idCard));;
            
            bundle.addPicture("pic", listPic);
        }
        
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/financ/api/edit";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new Financier(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	添加商品
     *
     *  @param  name:       商品名称
     *  @param  type:       商品类型
     *  @param  features:   特征信息
     *  @param  material:   所需材料
     *  @param  bidding:    竞价排名价格
     *  @param  adPrice:    广告位价格
     */
    public FinancingGoods addFinancingGoods(FinancingGoods product) throws WeiYuanException {
        if (product.name == null || product.name.equals("")) {
            return null;
        }
        
        WeiYuanParameters bundle = product.getParameters();
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/financ/api/addGoods";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new FinancingGoods(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	编辑商品
     *
     *  @param  id:         商品 ID
     *  @param  name:       商品名称
     *  @param  type:       商品类型
     *  @param  features:   特征信息
     *  @param  material:   所需材料
     *  @param  bidding:    竞价排名价格
     *  @param  adPrice:    广告位价格
     */
    public FinancingGoods editFinancingGoods(FinancingGoods product) throws WeiYuanException {
        if (product.id == 0) {
            return null;
        }
        
        WeiYuanParameters bundle = product.getParameters();
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/financ/api/editGoods";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new FinancingGoods(new JSONObject(reString));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	商品列表
     *
     *  @param  city:   城市 ID
     *  @param  lat:    GPS定位坐标:纬度
     *  @param  lng:    GPS定位坐标:经度
     */
    public FinancingGoodsList getFinancingGoodsList(
    		int page, 
    		String type,
    		String city,
    		double lat,
    		double lng) throws WeiYuanException {

        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if (page > 1) bundle.add("page", String.valueOf(page));
        if (type != null && !type.equals("")) bundle.add("type",type);
        if (city != null && !city.equals("")) bundle.add("city",city);
        if (lat != 0) bundle.add("lat",String.valueOf(lat));
        if (lng != 0) bundle.add("lng",String.valueOf(lng));
        
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/financ/api/goodList";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new FinancingGoodsList(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	商品列表
     *
     *  @param  shopid:   融资商ID
     */
    public FinancingGoodsList getFinancingGoodsList(
    		int page,
    		int shopid) throws WeiYuanException {

        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if (shopid == 0) return null;
        
        if (page > 1) bundle.add("page", String.valueOf(page));
        bundle.add("shopid",String.valueOf(shopid));
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/financ/api/goodList";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new FinancingGoodsList(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
    }
    
    /**
     *	@Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	删除商品
     *
     *  @param  goodid:   商品ID
     */
    public WeiYuanState deleteFinancingGoods(int goodsid) throws WeiYuanException {

        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if (goodsid == 0) return null;
        
        bundle.add("goodid",String.valueOf(goodsid));
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/financ/api/deleteGood";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new WeiYuanState(new JSONObject(reString));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
    }

    
    /**
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	商区列表
     *
     */
    public ShopAreaList getShopAreaList(int shopType) throws WeiYuanException {
        String url = null;
        
        if (shopType == 0) {
            url = SERVER_PREFIX + "/shop/api/areaList";
        } else {
            url = SERVER_PREFIX + "/basket/api/areaList";
        }
        
        if (url != null) {
            try {
                String reString = request(url, null, Utility.HTTPMETHOD_POST, 0);
                
                if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                    return new ShopAreaList(reString);
                }
            } catch (WeiYuanException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        return null;
    }
    
    /**
     *	Copyright © 2015 tcy@dreamisland. All rights reserved.
     *
     *	商品详细
     *
     *  @param shopid   商家 ID
     *
     */
    public Merchant getShop(
    		int shopType,
    		int shopid) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if (shopid == 0) return null;
        
        bundle.add("shopid",String.valueOf(shopid));
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = null;
        
        if (shopType == 0)
            url = SERVER_PREFIX + "/shop/Api/shopDetail";
        else 
            url = SERVER_PREFIX + "/basket/Api/shopDetail";
        	
        if (url != null) {
            try {
                String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
                
                if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                    return new Merchant(reString);
                }
            } catch (WeiYuanException e) {
                e.printStackTrace();
                return null;
            }
        }
        
        return null;
    }

	public MerchantList getMerchantList(
			int page, 
			int merchantType,
			String city,
			String category,
			Double lat,
			Double lng) throws WeiYuanException {

        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("type", String.valueOf(merchantType));
        
        if (page > 1) bundle.add("page", String.valueOf(page));
        if (city != null && !city.equals("")) bundle.add("city",city);
        if (category != null && !category.equals("")) bundle.add("category",category);
        if (lat != 0) bundle.add("lat",String.valueOf(lat));
        if (lng != 0) bundle.add("lng",String.valueOf(lng));
        
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/merchant/api/shopList";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new MerchantList(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
	}

	public MerchantInfo applyMerchantInfo(MerchantInfo merchant) throws WeiYuanException {
        if (merchant == null) {
            return null;
        }
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("type", String.valueOf(merchant.type));
        bundle.add("name",merchant.name);

        bundle.add("content", merchant.content);
        String cat = WeiYuanCommon.join(",", merchant.categories);
        bundle.add("category", cat);

        if (merchant.phone != null && merchant.phone.length() > 0) bundle.add("phone", merchant.phone);
        
        if (merchant.money > 0) bundle.add("money", String.valueOf(merchant.money));
        
        if (merchant.address != null && merchant.address.length() > 0) bundle.add("address", merchant.address);
        if (merchant.city != null && merchant.city.length() > 0) bundle.add("city", merchant.city);
        
        bundle.add("lat", String.valueOf(merchant.lat));
        bundle.add("lng", String.valueOf(merchant.lng));
        
        if(merchant.attachment != null && merchant.attachment.size() > 0){
            List<MorePicture> listPic = new ArrayList<MorePicture>();

            for (Picture pic : merchant.attachment) {
            	if (pic.originUrl != null)
            		listPic.add(new MorePicture(pic.key, pic.originUrl));;
			}
            
            if (listPic.size() > 0) {
            	bundle.addPicture("pic", listPic);
            }
        }
        
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/merchant/api/apply";

        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new MerchantInfo(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
	}

	public MerchantInfo editMerchantInfo(MerchantInfo merchant) throws WeiYuanException {
        if (merchant == null) {
            return null;
        }
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("id", String.valueOf(merchant.id));
        bundle.add("name",merchant.name);
        bundle.add("type", String.valueOf(merchant.type));

        bundle.add("content", merchant.content);
        String cat = WeiYuanCommon.join(",", merchant.categories);
        bundle.add("category", cat);

        if (merchant.phone != null && merchant.phone.length() > 0) bundle.add("phone", merchant.phone);
        if (merchant.money > 0) bundle.add("money", String.valueOf(merchant.money));
        
        if (merchant.address != null && merchant.address.length() > 0) bundle.add("address", merchant.address);
        if (merchant.city != null && merchant.city.length() > 0) bundle.add("city", merchant.city);
        
        bundle.add("lat", String.valueOf(merchant.lat));
        bundle.add("lng", String.valueOf(merchant.lng));
        
        if(merchant.attachment != null && merchant.attachment.size() > 0){
            List<MorePicture> listPic = new ArrayList<MorePicture>();

            for (Picture pic : merchant.attachment) {
            	if (pic.newPic && pic.originUrl != null && pic.originUrl.length() > 0) {
                    listPic.add(new MorePicture(pic.key, pic.originUrl));
            	}
			}
            
            if (listPic.size() > 0) {
            	bundle.addPicture("pic", listPic);
            }
        }
        
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/merchant/api/edit";

        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
        
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new MerchantInfo(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
	}

	public MerchantCategory getMerchantCategory() throws WeiYuanException {
		String url = SERVER_PREFIX  + "/merchant/api/categroyList";

		if (url != null) {
			String reString = request(url, null, Utility.HTTPMETHOD_POST,0);

			if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
				return new MerchantCategory(reString);
			}
		}
		
		return null;
	}

	public MerchantGoods addMerchantGoods(MerchantGoods goods)
			throws WeiYuanException {
		if (goods == null) {
			return null;
		}

		WeiYuanParameters bundle = new WeiYuanParameters();

		bundle.add("name", goods.name);
		bundle.add("price", String.valueOf(goods.price));
		bundle.add("shopid", String.valueOf(goods.shopid));

		if (goods.type != null && !goods.type.equals(""))
			bundle.add("type", goods.type);

		if (goods.features != null && !goods.features.equals(""))
			bundle.add("features", goods.features);

		if (goods.material != null && !goods.material.equals(""))
			bundle.add("material", goods.material);

		if (goods.bidding > 0)
			bundle.add("bidding", String.valueOf(goods.bidding));
		else 
			bundle.add("bidding", String.valueOf(goods.price));

		if (goods.adPrice > 0)
			bundle.add("adPrice", String.valueOf(goods.adPrice));
		else 
			bundle.add("adPrice", String.valueOf(goods.price));

        if(goods.attachment != null && goods.attachment.size() > 0){
            List<MorePicture> listPic = new ArrayList<MorePicture>();

            for (Picture pic : goods.attachment) {
            	if (pic.newPic) {
                    listPic.add(new MorePicture(pic.key, pic.originUrl));;
            	}
			}
            
            if (listPic.size() > 0) {
            	bundle.addPicture("pic", listPic);
            }
        }

		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));

		String url = SERVER_PREFIX + "/merchant/api/addGoods";

		try {
			String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);

			if (reString != null && !reString.equals("")
					&& !reString.equals("null")) {
				return new MerchantGoods(reString);
			}
		} catch (WeiYuanException e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}

	public MerchantGoods editMerchantGoods(MerchantGoods goods)
			throws WeiYuanException {
        if (goods == null || goods.id == 0) {
            return null;
        }
        
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("id",String.valueOf(goods.id));
        
        bundle.add("name",goods.name);
        bundle.add("price",String.valueOf(goods.price));
		bundle.add("shopid", String.valueOf(goods.shopid));

        if (goods.type != null && !goods.type.equals(""))
        	bundle.add("type",goods.type);
        
        if (goods.features != null && !goods.features.equals(""))
        	bundle.add("features",goods.features);
        
        if (goods.material != null && !goods.material.equals(""))
        	bundle.add("material",goods.material);
        
        if (goods.bidding > 0)
        	bundle.add("bidding",String.valueOf(goods.bidding));
        
        if (goods.adPrice > 0)
        	bundle.add("adPrice",String.valueOf(goods.adPrice));
        
        if(goods.attachment != null && goods.attachment.size() > 0){
            List<MorePicture> listPic = new ArrayList<MorePicture>();

            for (Picture pic : goods.attachment) {
            	if (pic.newPic) {
                    listPic.add(new MorePicture(pic.key, pic.originUrl));;
            	}
			}
            
            if (listPic.size() > 0) {
            	bundle.addPicture("pic", listPic);
            }
        }

        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/merchant/api/editGoods";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new MerchantGoods(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
    }

	public MerchantGoodsList getMerchantGoodsByShopId(int id, int page)
			throws WeiYuanException {

        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if (page > 1) bundle.add("page", String.valueOf(page));
        bundle.add("shopid", String.valueOf(id));
        
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/merchant/api/goodList";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new MerchantGoodsList(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
	}

	public MerchantGoodsList getMerchantGoodsList(
			int page,
			int shopid) throws WeiYuanException {

        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if (shopid == 0) return null;
        
        if (page > 1) bundle.add("page", String.valueOf(page));
        bundle.add("shopid",String.valueOf(shopid));
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/merchant/api/goodList";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new MerchantGoodsList(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
	}

	public WeiYuanState deleteMerchantGoods(int goodsid) throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        if (goodsid == 0) return null;
        
        bundle.add("goodid",String.valueOf(goodsid));
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/merchant/api/deleteGood";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new WeiYuanState(new JSONObject(reString));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
	}

	public AdDomainList advertList() throws WeiYuanException {
        WeiYuanParameters bundle = new WeiYuanParameters();
        
        bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
        
        String url = SERVER_PREFIX + "/user/api/advertList";
        
        try {
            String reString = request(url, bundle, Utility.HTTPMETHOD_POST, 1);
            
            if (reString != null && !reString.equals("") && !reString.equals("null") ) {
                return new AdDomainList(reString);
            }
        } catch (WeiYuanException e) {
            e.printStackTrace();
            return null;
        }
        
        return null;
	}
}
