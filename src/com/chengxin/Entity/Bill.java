package com.chengxin.Entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.chengxin.net.WeiYuanParameters;
import com.chengxin.org.json.JSONArray;
import com.chengxin.org.json.JSONException;
import com.chengxin.org.json.JSONObject;

/**
 *  还款账单
 */
public class Bill implements Serializable{

	public static final String[] STRING_PERIOD = {
		"仅一次",
		"每月",
		"每2月",
		"每3月",
		"每4月",
		"每5月",
		"每6月",
		"每7月",
		"每8月",
		"每9月",
		"每10月",
		"每11月",
		"每年"
		};

    private static final long serialVersionUID = 1L;
    
    public int id;              //< 账单id
    public int uid;             //< 用户id
    public int type;            //< 还款类型：1:信用卡; 2:贷款
    public int credit;			//< 信用卡后4位 
    public String bankName;		//< 银行名称
    public String name;         //< 姓名|贷款名称
    public long repayDate;		//< 每月还款日期|首次还款日期
    public double quota; 		//< 额度
    public int integral; 		//< 积分
    public double repayMoney; 	//< 最低还款|每期还款
    public double totalRepay; 	//< 剩余还款
    public int remindDate; 		//< 提醒日期
    public int remindCycle; 	//< 提醒周期
    public int leadDay; 		//< 提前提醒天数
    public String email; 		//< 邮箱
    public String emailPass; 	//< 邮箱密码
    public int autoGet; 		//< 0:不获取; 1:获取
    public String remarks; 		//< 备注
    public int repayStatus;		//< 是否本期已还
    public int remain; 			//< 还款剩余天数
    
    /**
     *	账单还款记录 
     */
    public List<BillRepay> repayList = new ArrayList<BillRepay>();

  /**
    CREATE TABLE IF NOT EXISTS `tc_user_bill` (
	  	`id` int(11) NOT NULL,
	    `uid` int(11) NOT NULL,
	    `type` tinyint(4) NOT NULL COMMENT '1信用卡2贷款',
	    `credit` int(4) DEFAULT NULL COMMENT '信用卡后4位',
	    `bankName` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '银行名称',
	    `name` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '姓名|贷款名称',
	    `repayDate` int(11) NOT NULL DEFAULT '0' COMMENT '每月还款日期|首次还款日期',
	    `quota` float(10,2) NOT NULL DEFAULT '0.00' COMMENT '额度',
	    `integral` int(11) NOT NULL DEFAULT '0' COMMENT '积分',
	    `repayMoney` float(10,2) NOT NULL DEFAULT '0.00' COMMENT '最低还款|每期还款',
	    `totalRepay` float(10,2) NOT NULL DEFAULT '0.00' COMMENT '剩余还款',
	    `remindDate` int(11) NOT NULL DEFAULT '0' COMMENT '提醒日期',
	    `remindCycle` int(11) NOT NULL DEFAULT '0' COMMENT '提醒周期',
	    `leadDay` int(11) NOT NULL DEFAULT '1' COMMENT '提前提醒天数',
	    `email` varchar(50) CHARACTER SET utf8 DEFAULT NULL COMMENT '邮箱',
	    `emailPass` varchar(50) CHARACTER SET utf8 DEFAULT NULL,
	    `autoGet` tinyint(4) NOT NULL DEFAULT '0' COMMENT '0不获取1获取',
	    `remarks` varchar(200) CHARACTER SET utf8 DEFAULT NULL COMMENT '备注'
    ) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
    */
    
    public Login user;          //< 还款人信息
    public WeiYuanState state;  //< 返回的状态对象

    public Bill(String reqString) {
        super();
        
        try {
            if(reqString == null || reqString.equals("")){
                return;
            }
            initCompent(new JSONObject(reqString));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    public Bill(JSONObject obj){
        super();

        try {
            initCompent(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    
    
    public Bill(int type) {
        super();
        this.type = type;
        this.id = 0;
	}

	private void initCompent(JSONObject json) throws JSONException{
        if(json == null || json.equals("")){
            return;
        }
        
        if(!json.isNull("data")){
            String dataString = json.getString("data");
            if(dataString != null && !dataString.equals("")
               && dataString.startsWith("{")){
                init(json.getJSONObject("data"));
            }
        }else{
            init(json);
        }
        
        if(!json.isNull("state")){
            String stateString = json.getString("state");
            if(stateString != null && !stateString.equals("")
               && stateString.startsWith("{")){
                state = new WeiYuanState(json.getJSONObject("state"));
            }
        }
    }
    
    private void init(JSONObject json) throws JSONException{
        if(json == null || json.equals("")){
            return;
        }
        
        id 			= json.getInt("id");
        uid 		= json.getInt("uid");
        type 		= json.getInt("type");
        credit 		= json.getInt("credit");
        bankName 	= json.getString("bankName");
        name 		= json.getString("name");
        repayDate 	= json.getLong("repayDate");
        quota 		= json.getDouble("quota");
        integral 	= json.getInt("integral");
        repayMoney 	= json.getDouble("repayMoney");
        totalRepay 	= json.getDouble("totalRepay");
        remindDate 	= json.getInt("remindDate");
        remindCycle = json.getInt("remindCycle");
        leadDay 	= json.getInt("leadDay");
        email 		= json.getString("email");
        emailPass 	= json.getString("emailPass");
        autoGet 	= json.getInt("autoGet");
        remarks 	= json.getString("remarks");
        repayStatus = json.getInt("repayStatus");
        remain 		= json.getInt("remind");
        
        if (!json.isNull("repayments")) {
            String strData = json.getString("repayments");

            if(strData != null && !strData.equals("") && strData.startsWith("[")){
            	List<BillRepay> list = BillRepay.constructList(json.getJSONArray("repayments"));
                
                if(list != null && list.size() > 0){
                	repayList.addAll(list);
                }
            }
        }
        
        if (!json.isNull("user")) {
            user = new Login(json.getJSONObject("user"));
        }
    }

	public static List<Bill> constructList(JSONArray array) {
		try {
			List<Bill> billList = new ArrayList<Bill>();
			int size = array.length();

			for (int i = 0; i < size; i++) {
				billList.add(new Bill(array.getJSONObject(i)));
			}
            
			return billList;
		} catch (JSONException jsone) {
			jsone.printStackTrace();
		}
		
		return null;
	}

	public WeiYuanParameters getParameters() {
        WeiYuanParameters bundle = new WeiYuanParameters();

        if (bankName != null && !bankName.equals("null"))
        	bundle.add("bankName", 	bankName);
        
        if (email != null && !email.equals("null"))
        	bundle.add("email", 	email);
        
        if (emailPass != null && !emailPass.equals("null"))
        	bundle.add("emailPass", emailPass);
        
        if (name != null && !name.equals("null"))
        	bundle.add("name", 		name);
        
        if (remarks != null && !remarks.equals("null"))
        	bundle.add("remarks", 	remarks);

        if (id > 0)
        	bundle.add("id", 		String.valueOf(id));

        bundle.add("autoGet", 		String.valueOf(autoGet));
        bundle.add("credit", 		String.valueOf(credit));
        bundle.add("leadDay",		String.valueOf(leadDay));
        bundle.add("quota",			String.valueOf(quota));
        bundle.add("remindCycle",	String.valueOf(remindCycle));
        bundle.add("remindDate", 	String.valueOf(remindDate));
        bundle.add("repayDate", 	String.valueOf(repayDate));
        bundle.add("repayMoney", 	String.valueOf(repayMoney));
        bundle.add("totalRepay", 	String.valueOf(totalRepay));
        bundle.add("type", 			String.valueOf(type));
        bundle.add("integral", 		String.valueOf(integral));

        return bundle;
	}

	public CharSequence getRemindCircleString() {
		if (remindCycle >= 0 && remindCycle < STRING_PERIOD.length) {
			return STRING_PERIOD[remindCycle];
		}
		
		return "";
	}
}