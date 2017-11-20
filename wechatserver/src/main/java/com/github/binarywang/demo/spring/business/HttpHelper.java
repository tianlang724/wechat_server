package com.github.binarywang.demo.spring.business;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.binarywang.demo.spring.config.WxMpConfig;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpHelper {
    static final String luisUri="https://westus.api.cognitive.microsoft.com/luis/v2.0/apps/75e7ffcb-f98d-453a-9e84-a5559fec2918?subscription-key=3617865e39314ba29d0ddc084f05d058&verbose=true&timezoneOffset=0&q=";
    static final String crawlerUri="http://115.159.111.231:80/schoolbotapi/rest";

//   从配置文件读取uri  待完善
//    static{
//        WxMpConfig wxConfig=new WxMpConfig();
//        final String luisUri=wxConfig.getLuisUri();
//        final String crawler=wxConfig.getCrawlerUri();
//
//    }
    public  static JsonNode getLuisResponse(String reqContext){
        //get请求返回结果
        JsonNode jsonResult = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(luisUri+reqContext);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            try {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String luisContext= EntityUtils.toString(entity);
                    ObjectMapper mapper=new ObjectMapper();
                    jsonResult=mapper.readTree(luisContext);
                }
            } finally {
                response.close();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonResult;
    }
    public  static JsonNode postCrawlerResponse(String userId,String passWord,String path) {
        JsonNode jsonResult = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            HttpPost httpPost = new HttpPost(crawlerUri+path);
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("username", userId));
            params.add(new BasicNameValuePair("password", passWord));
            httpPost.setEntity(new UrlEncodedFormEntity(params));
            CloseableHttpResponse response = httpclient.execute(httpPost);
            int code=response.getStatusLine().getStatusCode();
            switch (code){
                case 200:
                    try{
                        String jsonString = EntityUtils.toString(response.getEntity(),"UTF-8");
                        ObjectMapper mapper=new ObjectMapper();
                        jsonResult=mapper.readTree(jsonString);
                    }finally {
                        response.close();
                    }
                    break;
                case 500:
                    //
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonResult;
    }
    public  static JsonNode getCrawlerResponse(String params) {
        JsonNode jsonResult = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(crawlerUri+params);
            // 执行get请求.
            CloseableHttpResponse response = httpclient.execute(httpget);
            int code=response.getStatusLine().getStatusCode();
            switch (code){
                case 200:
                    try{
                        String jsonString = EntityUtils.toString(response.getEntity(),"UTF-8");
                        System.out.println(jsonString);
                        ObjectMapper mapper=new ObjectMapper();
                        jsonResult=mapper.readTree(jsonString);
                    }finally {
                        response.close();
                    }
                    break;
                case 500:
                    //
                    break;
                default:
                    break;
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return jsonResult;
    }
    public  static void getCrawler(String params) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            // 创建httpget.
            HttpGet httpget = new HttpGet(crawlerUri+params);
            // 执行get请求.
            httpclient.execute(httpget);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,释放资源
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
