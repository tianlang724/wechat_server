package com.github.binarywang.demo.spring.builder;

import com.github.binarywang.demo.spring.service.WeixinService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;



public class BBSCheckinBuilder extends  AbstractBuilder {
    @Override
    public WxMpXmlOutMessage build(String content, WxMpXmlMessage wxMessage,
                                   WeixinService service)   {
        WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
        item.setDescription("请跳转页面绑定清水河畔信息~");
        item.setTitle("清水河畔信息绑定");
        item.setUrl("http://115.159.111.231/schoolbotapi/uestc/portal?user_token=" + wxMessage.getFromUser());
        return WxMpXmlOutMessage.NEWS()
                .fromUser(wxMessage.getToUser())
                .toUser(wxMessage.getFromUser())
                .addArticle(item)
                .build();
    }
}