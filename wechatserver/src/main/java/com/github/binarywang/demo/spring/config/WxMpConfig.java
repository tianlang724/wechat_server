package com.github.binarywang.demo.spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 
 * @author Binary Wang
 *
 */
@Configuration
public class WxMpConfig {
  @Value("#{wxProperties.wx_token}")
  private String token;

  @Value("#{wxProperties.wx_appid}")
  private String appid;

  @Value("#{wxProperties.wx_appsecret}")
  private String appsecret;

  @Value("#{wxProperties.wx_aeskey}")
  private String aesKey;

  @Value("#{wxProperties.luis_uri}")
  private String luisUri;

  @Value("#{wxProperties.crawler_uri}")
  private String crawlerUri;

  public String getToken() {
    return this.token;
  }

  public String getAppid() {
    return this.appid;
  }

  public String getAppsecret() {
    return this.appsecret;
  }

  public String getAesKey() {
    return this.aesKey;
  }

  public String getLuisUri(){return this.luisUri;}

  public String getCrawlerUri(){return this.crawlerUri;}
}