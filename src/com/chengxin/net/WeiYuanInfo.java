package com.chengxin.net;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;
import android.util.Log;

import com.chengxin.Entity.AddGroup;
import com.chengxin.Entity.ChatImg;
import com.chengxin.Entity.CheckFriends;
import com.chengxin.Entity.CommentGoodsState;
import com.chengxin.Entity.CountryList;
import com.chengxin.Entity.Favorite;
import com.chengxin.Entity.FriendsLoop;
import com.chengxin.Entity.FriendsLoopItem;
import com.chengxin.Entity.Goods;
import com.chengxin.Entity.GoodsCommentEntity;
import com.chengxin.Entity.GoodsEntity;
import com.chengxin.Entity.GroupList;
import com.chengxin.Entity.LoginResult;
import com.chengxin.Entity.Meeting;
import com.chengxin.Entity.MeetingItem;
import com.chengxin.Entity.MerchantEntity;
import com.chengxin.Entity.MerchantMenu;
import com.chengxin.Entity.MessageInfo;
import com.chengxin.Entity.MessageResult;
import com.chengxin.Entity.MessageType;
import com.chengxin.Entity.MorePicture;
import com.chengxin.Entity.Room;
import com.chengxin.Entity.RoomList;
import com.chengxin.Entity.RoomUsrList;
import com.chengxin.Entity.UploadImg;
import com.chengxin.Entity.UserList;
import com.chengxin.Entity.VersionInfo;
import com.chengxin.Entity.WeiYuanState;
import com.chengxin.global.FeatureFunction;
import com.chengxin.global.WeiYuanCommon;
import com.chengxin.map.BMapApiApp;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

public class WeiYuanInfo  implements Serializable{
	private static final long serialVersionUID = 1651654562644564L;

	/**www.kobego.com 115.29.32.248*/
	private static final String SERVER_STR = "http://121.40.214.35:8000/"; // "http://119.84.73.193/"; // 
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
			Log.e("reString", reString);
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
			//Log.e("addFriend", reString);
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
			//Log.e("getContactGroupList", reString);
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
			//Log.e("reString", reString);

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
			//Log.e("reString", reString);

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
			//Log.e("reString", reString);

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
			//Log.e("reString", reString);

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
			//Log.e("reString", reString);

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
			Log.e("favoreiteMoving", reString);
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
			Log.e("favoriteList", reString);
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
			Log.e("favoreiteMoving", reString);
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
		Log.e("createRoom", "groupName:"+groupname);
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
		Log.d("getCityAndContryUser", reString);
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
			Log.e("sendMessage", reString);
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
	public MerchantMenu getShopType() throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		String url = SERVER_PREFIX  + "/shop/api/categroyList";
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,0);
		if(reString != null && !reString.equals("") && !reString.equals("null") /* && reString.startsWith("{")*/){
			return new MerchantMenu(reString);

		}
		return null;
	}



	/***
	 * 申请成为商户 /shop/api/apply
	 * @param uid 			true 	登录用户id
	 * @param name 					商家名称
	 * @param usrename				 联系人
	 * @param phone					 电话
	 * @param address				 地址
	 * @param lat					 纬度
	 * @param lng 					经度
	 * @param content				 备注
	 * @return
	 * @throws WeiYuanException 
	 */
	public WeiYuanState applyMerchant(String name,String username,String phone,
			String address,String lat,String lng,String content) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if((name == null || name.equals(""))
				|| (username == null || username.equals(""))
				|| (phone == null || phone.equals(""))
				|| (address == null || address.equals(""))
				|| (lat == null || lat.equals(""))
				|| (lng == null || lng.equals(""))){
			return null;	
		}
		bundle.add("name",name);
		bundle.add("username", username);
		bundle.add("phone", phone);
		bundle.add("address", address);
		if(content != null && !content.equals("")){
			bundle.add("content", content);
		}

		bundle.add("lat", lat);
		bundle.add("lng", lng);
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		String url = SERVER_PREFIX  + "/shop/api/apply";
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

	//




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
	public MerchantEntity getMerchantkList(int page,int categoryid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("categoryid",String.valueOf(categoryid));
		bundle.add("page", String.valueOf(page));
		bundle.add("lat",String.valueOf( WeiYuanCommon.getCurrentLat(BMapApiApp.getInstance())));
		bundle.add("lng",String.valueOf( WeiYuanCommon.getCurrentLng(BMapApiApp.getInstance())));
		/*	bundle.add("pageSize", String.valueOf(Common.LOAD_SIZE));*/
		String url = SERVER_PREFIX + "/shop/api/shopList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			//Log.e("reString", reString);

			return new MerchantEntity(reString);
		}

		return null;
	}

	//---------2015-02-02 商户 2015-02-02-------------

	//+++++++++2015-02-05商品列表2015-02-05 ++++++++++
	/**
	 * 商品列表 /shop/api/goodsList
	 * @param page
	 * @return
	 * @throws WeiYuanException
	 */
	public GoodsEntity getGoodsList(int page,int shopid) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("appkey",APPKEY);
		if(shopid == 0){
			return null;
		}
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("shopid", String.valueOf(shopid));
		bundle.add("page", String.valueOf(page));
		/*bundle.add("pageSize", String.valueOf(Common.LOAD_SIZE));*/
		String url = SERVER_PREFIX + "/shop/api/goodsList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			//Log.e("reString", reString);

			return new GoodsEntity(reString);
		}

		return null;
	}
	//---------2015-02-05商品列表2015-02-05 -------------

	/***
	 * 5.	商品详细(/shop/api/detail)
	 * @param goods_id	true	String	商品id
	 * @throws WeiYuanException 
	 */
	public Goods getGoodsDetail(String goods_id) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if(goods_id == null || goods_id.equals("")){
			return null;
		}
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("goods_id", goods_id);
		String url = SERVER_PREFIX + "/shop/api/detail";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.e("getGoodsDetail", reString);
			return new Goods(reString);
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
	public WeiYuanState cancleOrCollection(String goods_id,int action) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		if(goods_id == null || goods_id.equals("")){
			return null;
		}
		bundle.add("uid", WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("goods_id", goods_id);
		bundle.add("action", String.valueOf(action));
		String url = SERVER_PREFIX + "/shop/api/favorite";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.e("getGoodsDetail", reString);
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
			Log.e("getGoodsDetail", reString);
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
			Log.e("getGoodsDetail", reString);
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
	public MerchantEntity getShoppingCartList(String goods_id) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();
		bundle.add("goods_id", goods_id);
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		/*	bundle.add("pageSize", String.valueOf(Common.LOAD_SIZE));*/
		String url = SERVER_PREFIX + "/shop/api/cartGoodsList";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.e("getShoppingCartList", reString);

			return new MerchantEntity(reString);
		}

		return null;
	}

	/**
	 * 9.	提交订单(/shop/api/submitOrder)
	 * @param uid 			true	 登录用户id
	 * @param goods			true	String	商品格式：商品id1*count1,id2*count2
	 * @param username		true	string	联系人
	 * @param phone			true	string	电话
	 * @param address		true	string	地址
	 * @param content		false	string	备注
	 * @param shopid		true	int	商家Id
	 * @throws WeiYuanException 
	 */

	public WeiYuanState submitOrder(String goods,String username,String phone,
			String address,String content,int shopid			) throws WeiYuanException{
		WeiYuanParameters bundle = new WeiYuanParameters();

		if(((goods == null || goods.equals(""))	|| goods.endsWith(","))
				|| (username == null || username.equals(""))
				|| (address == null || address.equals(""))
				|| shopid == 0){
			return null;
		}
		bundle.add("goods", goods);
		bundle.add("uid",WeiYuanCommon.getUserId(BMapApiApp.getInstance()));
		bundle.add("username", username);
		bundle.add("phone",phone);
		bundle.add("address", address);
		bundle.add("shopid", String.valueOf("shopid"));
		if(content != null && !content.equals("")){
			bundle.add("content",content);
		}
		String url = SERVER_PREFIX + "/shop/api/submitOrder";
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.e("submitOrder", reString);
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;	
	}

	
	/**
	 * 11.	添加商品(/shop/api/addGoods)
	 * @param uid 			true	 登录用户id
	 * @param categoryid	true	int	类别id
	 * @param logo			true	file	商品Logo
	 * @param name			true	string	商品名称
	 * @param price			true	string	商品价格
	 * @param picture		true	file	商品图册 多张就1 2 3就可以了	
	 * @param content		false 	string 商品详细
	 * @param parameter		false 	string 商品规格
	 * @throws WeiYuanException 
	 */
	public WeiYuanState addGoods(int categoryid,String goodsName,
				String price,List<MorePicture> picList,String content,
				String parameter) throws WeiYuanException{		
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
		String reString = request(url, bundle, Utility.HTTPMETHOD_POST,1);

		if(reString != null && !reString.equals("") && !reString.equals("null")){
			Log.e("submitOrder", reString);
			try {
				return new WeiYuanState(new JSONObject(reString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
}
