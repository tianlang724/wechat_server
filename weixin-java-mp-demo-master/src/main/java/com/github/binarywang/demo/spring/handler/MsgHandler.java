package com.github.binarywang.demo.spring.handler;

import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.binarywang.demo.spring.builder.EcardCheckinBuilder;
import com.github.binarywang.demo.spring.builder.LibraryCheckinBuilder;
import com.github.binarywang.demo.spring.business.*;
import org.springframework.stereotype.Component;

import com.github.binarywang.demo.spring.builder.TextBuilder;
import com.github.binarywang.demo.spring.service.WeixinService;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;

/**
 * 
 * @author Binary Wang
 *
 */
@Component
public class MsgHandler extends AbstractHandler {

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
      Map<String, Object> context, WxMpService wxMpService,
            WxSessionManager sessionManager)    {
        WxMpXmlOutMessage returnMsg=null;
        WeixinService weixinService = (WeixinService) wxMpService;
        if (!wxMessage.getMsgType().equals(WxConsts.XML_MSG_TEXT)) {
            //TODO 可以选择将消息保存到本地
        }
        String content=null;
        String usrContext=wxMessage.getContent();
        long startTime = System.currentTimeMillis();//获取当前时间
        JsonNode root= HttpHelper.getLuisResponse(usrContext);
        long endTime = System.currentTimeMillis();
        System.out.println("luis运行时间："+(endTime-startTime)+"ms");
        JsonNode topScore=root.get("topScoringIntent");
        String intent=topScore.get("intent").asText();
        JsonNode entites=root.get("entities");
        String time=null;
        if(entites.isArray()){
            for (JsonNode node : entites){
                if(node.get("type").asText().equals("builtin.datetimeV2.date")){
                    JsonNode resolution=node.get("resolution");
                    JsonNode values=resolution.get("values");
                    JsonNode value=values.get(0);
                    time=value.get("value").asText();
     //               time=time.substring(0,4)+time.substring(5,7)+time.substring(8,10);
                    System.out.println(time);
                }
            }
        }
        String userId=wxMessage.getFromUser();
        //String intent="一卡通余额";
        switch (intent){
            case "一卡通余额":
                startTime = System.currentTimeMillis();//获取当前时间
                content=MessageHandler.doProcessBalance(userId);
                endTime = System.currentTimeMillis();
                System.out.println("database运行时间："+(endTime-startTime)+"ms");
                if(content!=null)
                    return new TextBuilder().build(content, wxMessage, weixinService);
                else{
                    return new EcardCheckinBuilder().build(content,wxMessage,weixinService);
                }

            case "一卡通充值记录":
                if(time==null) {
                    content = MessageHandler.doProcessRecharge(userId);
                }else{
                    content = MessageHandler.doProcessRecharge(userId,time);
                }
                if(content!=null)
                    return new TextBuilder().build(content, wxMessage, weixinService);
                else{
                    return new EcardCheckinBuilder().build(content,wxMessage,weixinService);
                }
            case "一卡通消费流水":
                if(time==null) {
                    content = MessageHandler.doProcessExpend(userId);
                }else{
                    content = MessageHandler.doProcessExpend(userId,time);
                }
                if(content!=null)
                    return new TextBuilder().build(content, wxMessage, weixinService);
                else{
                    return new EcardCheckinBuilder().build(content,wxMessage,weixinService);
                }
            case "已修学分":
                content=MessageHandler.doCourcesScores(userId);
                if(content!=null)
                    return new TextBuilder().build(content, wxMessage, weixinService);
                else{
                    return new EcardCheckinBuilder().build(content,wxMessage,weixinService);
                }
            case "课程考试情况":
                content=MessageHandler.doCourseExeams(userId);
                if(content!=null)
                    return new TextBuilder().build(content, wxMessage, weixinService);
                else{
                    return new EcardCheckinBuilder().build(content,wxMessage,weixinService);
                }
            case "图书借阅历史":
                content=MessageHandler.doLibHistory(userId);
                if(content!=null)
                    return new TextBuilder().build(content, wxMessage, weixinService);
                else{
                    return new LibraryCheckinBuilder().build(content,wxMessage,weixinService);
                }
            case "你是谁":
                return new TextBuilder().build("我是电小科Bot呀，可以查询一卡通信息，图书借阅历史呢", wxMessage, weixinService);
            case "更新一卡通余额":
                HttpHelper.getCrawler("/ecard?usertoken="+userId);
                return new TextBuilder().build("正在更新一卡通余额...稍后再来查询哦", wxMessage, weixinService);
            case "更新一卡通消费":
                HttpHelper.getCrawler("/ecard/expend?usertoken="+userId);
                return new TextBuilder().build("正在更新一卡通消费情况...稍后再来查询哦", wxMessage, weixinService);
            case "更新一卡通充值":
                HttpHelper.getCrawler("/ecard/recharge?usertoken="+userId);
                return new TextBuilder().build("正在更新一卡通充值记录...稍后再来查询哦", wxMessage, weixinService);
            case "更新图书借阅历史":
                HttpHelper.getCrawler("/lib/history?usertoken="+userId);
                return new TextBuilder().build("正在更新图书借阅历史...稍后再来查询哦", wxMessage, weixinService);
            default:
                return new TextBuilder().build("啊哦~这是什么操作啊,看不懂欸", wxMessage, weixinService);
        }

        //TODO 组装回复消息
        //returnMsg=new TextBuilder().build(content, wxMessage, weixinService);
//        if(returnMsg==null){
//            return new TextBuilder().build("……我需要喝口水冷静一下了", wxMessage, weixinService);
//        }
//        return returnMsg;
    }

}
