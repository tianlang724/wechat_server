package com.github.binarywang.demo.spring.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.binarywang.demo.spring.builder.TextBuilder;
import com.github.binarywang.demo.spring.datebase.DatabaseHelper;
import com.github.binarywang.demo.spring.service.WeixinService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessageHandler {
    public static final String bbsUrl = "";
    public static final String bookUrl = "http://115.159.111.231/schoolbotapi/lib/portal?";
    public static final String ecardUrl = "http://115.159.111.231/schoolbotapi/uestc/portal?";

    /*
    检查爬虫服务器返回值是否为错误码
    有错误码返回给错误码
    没有错误码返回0
     */
    public static int checkResponseState(JsonNode root) {
        if (root.get("error") == null) {
            return 0;
        } else {

            JsonNode error = root.get("error");
            JsonNode reason = error.get("reason");
            if (reason != null) {
                int code = reason.asInt();
                System.out.println(code);
                return code;
            } else {
                return 0;
            }
        }
    }

    public static WxMpXmlOutMessage doProcessBalance(String userId, WeixinService wxServer, WxMpXmlMessage wxMessage) {
        String retString = null;
        JsonNode root = HttpHelper.getCrawlerResponse("/rest/ecard?usertoken=" + userId);
        if (root != null) {
            int code = checkResponseState(root);
            switch (code) {
                case 0:
                    String balanceString = root.get("balance").toString();
                    retString = "你的一卡通余额是：" + balanceString;
                    break;
                case 1:
                case 2:
                    WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
                    item.setDescription("请跳转页面绑定一卡通信息~");
                    item.setTitle("一卡通绑定");
                    item.setUrl(ecardUrl + "user_token=" + wxMessage.getFromUser());
                    return WxMpXmlOutMessage.NEWS()
                            .fromUser(wxMessage.getToUser())
                            .toUser(wxMessage.getFromUser())
                            .addArticle(item)
                            .build();
            }

            System.out.println(retString);
        } else {
            retString = "服务器开小差了";
        }
        return new TextBuilder().build(retString, wxMessage, wxServer);
    }

    public static WxMpXmlOutMessage doProcessExpend(String userId, WeixinService wxServer, WxMpXmlMessage wxMessage) {
        String retString = null;
        JsonNode root = HttpHelper.getCrawlerResponse("/rest/ecard/expend?usertoken=" + userId);
        StringBuilder sb = new StringBuilder();
        if (root != null) {
            int code = checkResponseState(root);
            switch (code) {
                case 0:
                    sb.append("消费记录：\n");
                    root = root.get("消费流水");
                    if (root.isArray()) {
                        for (JsonNode node : root) {
                            sb.append(node.get("消费时间").textValue());
                            sb.append("在");
                            sb.append(node.get("消费设备").textValue());
                            sb.append("消费");
                            sb.append(node.get("消费金额").textValue());
                            sb.append("元");
                            sb.append(",剩余");
                            sb.append(node.get("卡余额").textValue());
                            sb.append("元\n");
                        }
                        retString = sb.toString();
                    } else {
                        System.out.println(root.getNodeType());
                    }
                    break;
                case 1:
                case 2:
                    WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
                    item.setDescription("请跳转页面绑定一卡通信息~");
                    item.setTitle("一卡通绑定");
                    item.setUrl(ecardUrl + "user_token=" + wxMessage.getFromUser());
                    return WxMpXmlOutMessage.NEWS()
                            .fromUser(wxMessage.getToUser())
                            .toUser(wxMessage.getFromUser())
                            .addArticle(item)
                            .build();
            }
        } else {
            retString = "没有查到一卡通消费记录";
        }
        return new TextBuilder().build(retString, wxMessage, wxServer);
    }

    public static WxMpXmlOutMessage doProcessRecharge(String userId, WeixinService wxServer, WxMpXmlMessage wxMessage) {
        String retString = null;
        StringBuilder sb = new StringBuilder();
        JsonNode root = HttpHelper.getCrawlerResponse("/rest/ecard/recharge?usertoken=" + userId);
        sb.append("充值记录：\n");
        if (root != null) {
            int code = checkResponseState(root);
            switch (code) {
                case 0:
                    root = root.get("充值记录");
                    if (root.isArray()) {
                        for (JsonNode node : root) {
                            sb.append(node.get("充值时间").textValue());
                            sb.append("在");
                            sb.append(node.get("充值设备").textValue());
                            sb.append("充值");
                            sb.append(node.get("充值金额").textValue());
                            sb.append("元\n");
                        }
                        retString = sb.toString();
                    } else {
                        System.out.println(root.getNodeType());
                    }
                    break;
                case 1:
                case 2:
                    WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
                    item.setDescription("请跳转页面绑定一卡通信息~");
                    item.setTitle("一卡通绑定");
                    item.setUrl(ecardUrl + "user_token=" + wxMessage.getFromUser());
                    return WxMpXmlOutMessage.NEWS()
                            .fromUser(wxMessage.getToUser())
                            .toUser(wxMessage.getFromUser())
                            .addArticle(item)
                            .build();
            }

        } else {
            retString = "没有查到一卡通充值记录";
        }
        return new TextBuilder().build(retString, wxMessage, wxServer);
    }


/*
****************************************************************************************
 */
//    public static String doProcessBalance(String userId){
//        String retString=null;
//        JsonNode root=HttpHelper.getCrawlerResponse("/rest/ecard?usertoken="+userId);
//        if(root!=null){
//            String balanceString=root.get("balance").toString();
//            retString="你的一卡通余额是："+balanceString;
//            System.out.println(retString);
//        }else{
//            retString="没有查到一卡通余额";
//        }
//        return retString;
//
//    }
//    public static String doProcessExpend(String userId){
//        String retString=null;
//        JsonNode root=HttpHelper.getCrawlerResponse("/rest/ecard/expend?usertoken="+userId);
//        StringBuilder sb=new StringBuilder();
//        if(root!=null){
//            sb.append("消费记录：\n");
//            root=root.get("消费流水");
//            if (root.isArray()) {
//                for (JsonNode node : root) {
//                    sb.append(node.get("消费时间").textValue());
//                    sb.append("在");
//                    sb.append(node.get("消费设备").textValue());
//                    sb.append("消费");
//                    sb.append(node.get("消费金额").textValue());
//                    sb.append("元");
//                    sb.append(",剩余");
//                    sb.append(node.get("卡余额").textValue());
//                    sb.append("元\n");
//                }
//            }else{
//                System.out.println(root.getNodeType());
//            }
//            retString=sb.toString();
//
//        }else{
//            retString="没有查到一卡通消费记录";
//        }
//        return retString;
//    }
//    public static String doProcessRecharge(String userId){
//        String retString=null;
//        StringBuilder sb=new StringBuilder();
//        JsonNode root=HttpHelper.getCrawlerResponse("/rest/ecard/recharge?usertoken="+userId);
//        sb.append("充值记录：\n");
//        if(root!=null){
//            root=root.get("充值记录");
//            if (root.isArray())
//            {
//                for (JsonNode node : root)
//                {
//                    sb.append(node.get("充值时间").textValue());
//                    sb.append("在");
//                    sb.append(node.get("充值设备").textValue());
//                    sb.append("充值");
//                    sb.append(node.get("充值金额").textValue());
//                    sb.append("元\n");
//                }
//            }else{
//                System.out.println(root.getNodeType());
//            }
//            //ret=new TextBuilder().build(sb.toString(), wxMpXmlMessage, wxService);
//            retString=sb.toString();
//        }else{
//            retString="没有查到一卡通充值记录";
//        }
//        return retString;
//    }
//}

    /***********************************************************************************************/
    public static String doProcessBalance(String userId) {
        String retString = null;
        retString = DatabaseHelper.queryDatabase("user_ecard", "balance", userId);
        if (retString != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try {
                root = mapper.readTree(retString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            double balance=root.get("balance").asDouble();
            if(balance>100)
            {
                retString = "哇，你是土豪欸，一卡通有：" + balance+"元";
            }else if(balance>10)
            {
                retString = "衣食无忧啦，你的一卡通还有：" +balance+"元";
            }else{
                retString = "该充值了，不然就要吃土了，你的一卡通只有：" +balance+"元";
            }

            System.out.println(retString);
        } else {
            if(DatabaseHelper.queryPassword("ecard",userId)){
                retString = "暂时没有查到一卡通余额，待会儿再来哦";
            }else{
                //给用户跳转页面填写一卡通页面,返回空
                retString = null;
            }
            //请求服务器，
        }
        return retString;

    }

    public static String doProcessExpend(String userId) {
        String retString = null;
        retString = DatabaseHelper.queryDatabase("user_ecard", "expend", userId);
        if (retString != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try {
                root = mapper.readTree(retString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("消费记录：\n");
            root = root.get("消费流水");
            if (root.isArray()) {
                for (JsonNode node : root) {
                    String nodetime=node.get("消费时间").textValue();
                    sb.append(nodetime.substring(0,4));
                    sb.append("年");
                    sb.append(nodetime.substring(4,6));
                    sb.append("月");
                    sb.append(nodetime.substring(6,8));
                    sb.append("日");
                    sb.append(nodetime.substring(8,10));
                    sb.append("时");
                    sb.append(nodetime.substring(10,12));
                    sb.append("分在");
                    sb.append(node.get("消费设备").textValue());
                    sb.append("消费");
                    sb.append(node.get("消费金额").textValue());
                    sb.append("元");
                    sb.append(",剩余");
                    sb.append(node.get("卡余额").textValue());
                    sb.append("元\n");
                }
            } else {
                System.out.println(root.getNodeType());
            }
            retString = sb.toString();

        } else {
            if(DatabaseHelper.queryPassword("ecard",userId)){
                retString = "暂时没有查到一卡通消费记录，待会儿再来哦";
            }else{
                //给用户跳转页面填写一卡通页面,返回空
                retString = null;
            }
            //向爬虫服务器发起请求
        }
        return retString;
    }

    public static String doProcessRecharge(String userId) {
        String retString = null;
        retString = DatabaseHelper.queryDatabase("user_ecard", "recharge", userId);
        if (retString != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try {
                root = mapper.readTree(retString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("充值记录：\n");
            root = root.get("充值记录");
            if (root.isArray()) {
                for (JsonNode node : root) {
                    String nodetime=node.get("充值时间").textValue();
                    sb.append(nodetime.substring(0,4));
                    sb.append("年");
                    sb.append(nodetime.substring(4,6));
                    sb.append("月");
                    sb.append(nodetime.substring(6,8));
                    sb.append("日");
                    sb.append(nodetime.substring(8,10));
                    sb.append("时");
                    sb.append(nodetime.substring(10,12));
                    sb.append("分在");
                    sb.append(node.get("充值设备").textValue());
                    sb.append("充值");
                    sb.append(node.get("充值金额").textValue());
                    sb.append("元\n");
                }
            } else {
                System.out.println(root.getNodeType());
            }
            //ret=new TextBuilder().build(sb.toString(), wxMpXmlMessage, wxService);
            retString = sb.toString();
        } else {
            if(DatabaseHelper.queryPassword("ecard",userId)){
                retString = "暂时没有查到一卡通充值记录，待会儿再来哦";
            }else{
                //给用户跳转页面填写一卡通页面,返回空
                retString = null;
            }
            //向爬虫服务器发起请求
        }
        return retString;
    }
    public static String doProcessExpend(String userId,String time) {
        String retString = null;
        retString = DatabaseHelper.queryDatabase("user_ecard", "expend", userId);
        if (retString != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try {
                root = mapper.readTree(retString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(time);
            sb.append("消费记录：\n");
            root = root.get("消费流水");
            time=time.substring(0,4)+time.substring(5,7)+time.substring(8,10);
            String compile="^"+time+".*";
            Pattern pattern=Pattern.compile(compile);
            double sum=0;
            if (root.isArray()) {
                for (JsonNode node : root) {
                    String nodetime=node.get("消费时间").textValue();
                    Matcher matcher=pattern.matcher(nodetime);
                    if(matcher.matches()) {
                        sb.append(nodetime.substring(8,10));
                        sb.append("时");
                        sb.append(nodetime.substring(10,12));
                        sb.append("分在");
                        sb.append(node.get("消费设备").textValue());
                        sb.append("消费");
                        sb.append(node.get("消费金额").textValue());
                        sum+=node.get("消费金额").asDouble();
                        sb.append("元");
                        sb.append(",剩余");
                        sb.append(node.get("卡余额").textValue());
                        sb.append("元\n");
                    }
                }
            } else {
                System.out.println(root.getNodeType());
            }
            sb.append("共消费：");
            sb.append(sum);
            sb.append("元");
            retString = sb.toString();

        } else {
            if(DatabaseHelper.queryPassword("ecard",userId)){
                retString = "暂时没有查到一卡通消费记录，待会儿再来哦";
            }else{
                //给用户跳转页面填写一卡通页面,返回空
                retString = null;
            }
            //向爬虫服务器发起请求
        }
        return retString;
    }

    public static String doProcessRecharge(String userId,String time) {
        String retString = null;
        retString = DatabaseHelper.queryDatabase("user_ecard", "recharge", userId);
        if (retString != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try {
                root = mapper.readTree(retString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            sb.append(time);
            sb.append("充值记录：\n");
            root = root.get("充值记录");
            time=time.substring(0,4)+time.substring(5,7)+time.substring(8,10);
            String compile="^"+time+".*";
            Pattern pattern=Pattern.compile(compile);
            if (root.isArray()) {
                for (JsonNode node : root) {
                    String nodetime=node.get("充值时间").textValue();
                    Matcher matcher=pattern.matcher(nodetime);
                    if(matcher.matches()) {
                        sb.append(nodetime.substring(8,10));
                        sb.append("时");
                        sb.append(nodetime.substring(10,12));
                        sb.append("分在");
                        sb.append(node.get("充值设备").textValue());
                        sb.append("充值");
                        sb.append(node.get("充值金额").textValue());
                        sb.append("元\n");
                    }
                }
            } else {
                System.out.println(root.getNodeType());
            }
            //ret=new TextBuilder().build(sb.toString(), wxMpXmlMessage, wxService);
            retString = sb.toString();
        } else {
            if(DatabaseHelper.queryPassword("ecard",userId)){
                retString = "暂时没有查到一卡通充值记录，待会儿再来哦";
            }else{
                //给用户跳转页面填写一卡通页面,返回空
                retString = null;
            }
            //向爬虫服务器发起请求
        }
        return retString;
    }
    public static String doLibHistory(String userId) {
        String retString = null;
        retString = DatabaseHelper.queryDatabase("user_lib", "history", userId);
        if (retString != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try {
                root = mapper.readTree(retString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("借阅历史：\n");
            root = root.get("借阅历史");
            int i=1;
            if (root.isArray()) {
                for (JsonNode node : root) {
                    sb.append(i);
                    sb.append("、");
                    sb.append(node.get("题名").textValue());
                    sb.append(",作者：");
                    sb.append(node.get("作者").textValue());
                    sb.append("，借阅时间：");
                    sb.append(node.get("借出时间").textValue());
                    sb.append("\n");
                    i++;
                }
            } else {
                System.out.println(root.getNodeType());
            }
            //ret=new TextBuilder().build(sb.toString(), wxMpXmlMessage, wxService);
            retString = sb.toString();
        } else {
            if(DatabaseHelper.queryPassword("lib",userId)){
                retString = "暂时没有查到借阅历史，待会儿再来哦";
            }else{
                //给用户跳转页面填写一卡通页面,返回空
                retString = null;
            }
            //请求服务器，
        }
        return retString;

    }
    public static String doCourcesScores(String userId) {
        String retString = null;
        retString = DatabaseHelper.queryDatabase("user_course", "point", userId);
        if (retString != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try {
                root = mapper.readTree(retString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("已获得学分信息：\n公共基础课：");
            sb.append(root.get("公共基础课").asText());
            sb.append("\n基础课：");
            sb.append(root.get("基础课").asText());
            sb.append("\n专业基础课:");
            sb.append(root.get("专业基础课").asText());
            sb.append("\n本学科选修课:");
            sb.append(root.get("本学科选修课").asText());
            sb.append("\n跨学科选修课:");
            sb.append(root.get("跨学科选修课").asText());
            sb.append("\n实验选修课:");
            sb.append(root.get("实验选修课").asText());
            sb.append("\n必修环节:");
            sb.append(root.get("必修环节").asText());
            retString = sb.toString();
        } else {
            if(DatabaseHelper.queryPassword("ecard",userId)){
                retString = "暂时没有查到学分信息，待会儿再来哦";
            }else{
                //给用户跳转页面填写一卡通页面,返回空
                retString = null;
            }
            //请求服务器，
        }
        return retString;
    }
    public static String doCourseExeams(String userId) {
        String retString = null;
        retString = DatabaseHelper.queryDatabase("user_course", "choosen", userId);
        if (retString != null) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = null;
            try {
                root = mapper.readTree(retString);
            } catch (IOException e) {
                e.printStackTrace();
            }
            StringBuilder sb = new StringBuilder();
            sb.append("已选课程：\n");
            root = root.get("已选课程");
            int i=1;
            if (root.isArray()) {
                for (JsonNode node : root) {
                    sb.append(i);
                    sb.append("、");
                    sb.append(node.get("课程类别").textValue());
                    sb.append("：");
                    sb.append(node.get("课程名称").textValue());
                    sb.append("，教师：");
                    sb.append(node.get("课程老师").textValue());
                    sb.append(",学分：");
                    sb.append(node.get("课程学分").textValue());
                    sb.append(",考试情况:");
                    sb.append(node.get("课程得分").textValue());
                    sb.append("\n");
                }
            } else {
                System.out.println(root.getNodeType());
            }
            retString = sb.toString();
        } else {
            if(DatabaseHelper.queryPassword("ecard",userId)){
                retString = "暂时没有查到课程情况，待会儿再来哦";
            }else{
                //给用户跳转页面填写一卡通页面,返回空
                retString = null;
            }
            //请求服务器，
        }
        return retString;
    }
}
