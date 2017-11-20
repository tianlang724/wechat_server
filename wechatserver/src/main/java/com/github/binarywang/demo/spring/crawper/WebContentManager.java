package com.github.binarywang.demo.spring.crawper;

import com.fasterxml.jackson.databind.deser.DataFormatReaders;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.Consts;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebContentManager {

    // 常量
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.101 Safari/537.36";
    private static final String PORTAL_FORM_REGEXP = "<input type=\"hidden\" name=\"\\w+\" value=\"(.+?)\"";
    private static final String BALANCE_REGEXP = "卡余额：.+?>(.+?)</span>";
    private static final String RECENT_CHARGE_REGEXP = "<td>(.+?)</td> <td>(.+?)</td> <td>(.+?)</td> <td.+?><span.+?>(.+?)</span></td> <td.+?>(.+?)</td>";

    private CloseableHttpClient httpClient;
    private HttpClientContext context;
    private CookieStore cookieStore;
    private String userName;
    private String userPassword;
    private Logger logger;

    // 图书网页的一个请求参数
    private String magicCode;

    // 教学管理页面请求参数
    private String GMSTeachingInfoMainNode;
    private String GMSScoreTableSubNode;
    private String GMSScoreTablePath;
    private String GMSScoreTableMainObj;
    private String GMSScoreTableTFile;
    private String GMSScoreTableCurrentModelId;
    private String GMSScoreTableFilter;

    public WebContentManager(String userName, String userPassword, boolean secure) throws Exception {
        this.context = HttpClientContext.create();
        this.cookieStore = new BasicCookieStore();
        this.context.setCookieStore(this.cookieStore);
        this.userName = userName;
        this.userPassword = userPassword;
        if (secure) {
            this.httpClient = new SSLClient().getSecureHttpClient();
        }
        else {
//            HttpHost proxy = new HttpHost("127.0.0.1", 8888);
//            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            this.httpClient = HttpClients.custom()
//                    .setRoutePlanner(routePlanner)
//                    .setRedirectStrategy(new LaxRedirectStrategy())
                    .build();
        }
        logger = LoggerFactory.getLogger(WebContentManager.class);
    }

    public String varifyEcardIdentification() throws Exception {
        String result = authenticateFromPortal();
        if (result.equals("FAIL")) {
            return "FAIL";
        }
        else {
            return result;
        }
    }

    private boolean getEcardIdentification() throws Exception {
        if (authenticateFromPortal().equals("FAIL")) return false;
        getEcardAuthenticationFromSpider();
        return true;
    }

    private Matcher findMatcher(final String REGEXP, String content) {
        Pattern r = Pattern.compile(REGEXP);
        Matcher m = r.matcher(content);
        return m;
    }

    // 获取一卡通余额信息
    public String getEcardBalance() throws Exception {
        if (!getEcardIdentification()) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.USER_PASSWORD_NOT_CORRENT);
        String page = getEcardPersonalPageFromSpider();
        Matcher m = findMatcher(BALANCE_REGEXP, page);
        m.find();
        JsonNodeFactory factory = new JsonNodeFactory(false);
        ObjectNode balanceNode = factory.objectNode();
        balanceNode.put("balance", m.group(1));
        return balanceNode.toString();
    }

    // 获取消费详细情况
    public String getRecentlySpecificExpend() throws Exception {
        if (!getEcardIdentification()) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.USER_PASSWORD_NOT_CORRENT);
        getEcardPersonalPageFromSpider();
        URI expendPostUri = new URIBuilder()
                .setScheme("http")
                .setHost("ecard.uestc.edu.cn")
                .setPath("/web/guest/personal")
                .setParameter("p_p_id", "transDtl_WAR_ecardportlet")
                .setParameter("p_p_lifecycle", "0")
                .setParameter("p_p_state", "exclusive")
                .setParameter("p_p_mode", "view")
                .setParameter("p_p_col_id", "column-4")
                .setParameter("p_p_col_count", "1")
                .setParameter("_transDtl_WAR_ecardportlet_action", "dtlmoreview")
                .build();
        HttpPost expendPost = new HttpPost(expendPostUri);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("_transDtl_WAR_ecardportlet_qdate", "7"));
        formparams.add(new BasicNameValuePair("_transDtl_WAR_ecardportlet_qtype", "2"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        expendPost.setEntity(entity);
        expendPost.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse ecardGetRes = httpClient.execute(expendPost, context);
        String content = EntityUtils.toString(ecardGetRes.getEntity());
        Matcher m = findMatcher(RECENT_CHARGE_REGEXP, content);
        JsonNodeFactory factory = new JsonNodeFactory(false);
        ObjectNode expend = factory.objectNode();
        ArrayNode expendArray = factory.arrayNode();
        while(m.find()) {
            ObjectNode specificNode = factory.objectNode();
            specificNode.put("消费时间", m.group(1) + m.group(2));
            ObjectNode equipNode = factory.objectNode();
            specificNode.put("消费设备", m.group(3));
            ObjectNode moneyNode = factory.objectNode();
            specificNode.put("消费金额", m.group(4));
            ObjectNode balanceNode = factory.objectNode();
            specificNode.put("卡余额", m.group(5));
            expendArray.add(specificNode);
        }
        expend.set("消费流水", expendArray);
        ecardGetRes.close();
        return expend.toString();
    }

    // 获取最近充值情况
    public String getRecentlyRecharge() throws Exception {
        if (!getEcardIdentification()) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.USER_PASSWORD_NOT_CORRENT);
        getEcardPersonalPageFromSpider();
        URI rechargePostUri = new URIBuilder()
                .setScheme("http")
                .setHost("ecard.uestc.edu.cn")
                .setPath("/web/guest/personal")
                .setParameter("p_p_id", "transDtl_WAR_ecardportlet")
                .setParameter("p_p_lifecycle", "0")
                .setParameter("p_p_state", "exclusive")
                .setParameter("p_p_mode", "view")
                .setParameter("p_p_col_id", "column-4")
                .setParameter("p_p_col_count", "1")
                .setParameter("_transDtl_WAR_ecardportlet_action", "dtlmoreview")
                .build();
        HttpPost rechargePost = new HttpPost(rechargePostUri);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("_transDtl_WAR_ecardportlet_qdate", "90"));
        formparams.add(new BasicNameValuePair("_transDtl_WAR_ecardportlet_qtype", "1"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        rechargePost.setEntity(entity);
        rechargePost.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse ecardGetRes = httpClient.execute(rechargePost, context);
        String content = EntityUtils.toString(ecardGetRes.getEntity());
        Matcher m = findMatcher(RECENT_CHARGE_REGEXP, content);
        JsonNodeFactory factory = new JsonNodeFactory(false);
        ObjectNode recharge = factory.objectNode();
        ArrayNode rechargeArray = factory.arrayNode();
        while(m.find()) {
            ObjectNode specificNode = factory.objectNode();
            specificNode.put("充值时间", m.group(1) + m.group(2));
            ObjectNode equipNode = factory.objectNode();
            specificNode.put("充值设备", m.group(3));
            ObjectNode moneyNode = factory.objectNode();
            specificNode.put("充值金额", m.group(4));
            ObjectNode balanceNode = factory.objectNode();
            specificNode.put("卡余额", m.group(5));
            rechargeArray.add(specificNode);
        }
        recharge.set("充值记录", rechargeArray);
        ecardGetRes.close();
        return recharge.toString();
    }

    private void getGMSTeachingMainNodeId() throws Exception {
        URI GMSMainPageGetUri = new URIBuilder()
                .setScheme("http")
                .setHost("gs.uestc.edu.cn")
                .setPath("/epstar/web/swms/mainframe/home.jsp")
                .build();
        HttpGet GMSMainPageGet = new HttpGet(GMSMainPageGetUri);
        GMSMainPageGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse GMSMainPageGetRes = httpClient.execute(GMSMainPageGet, context);
        String GMSMainPage = EntityUtils.toString(GMSMainPageGetRes.getEntity());
        String GMSTeachingMainNodeIdPattern = "nodeid=\"(.+?)\">";
        Matcher GMSTeachingMainNodeMatcher = findMatcher(GMSTeachingMainNodeIdPattern, GMSMainPage);
        if (GMSTeachingMainNodeMatcher.find()) {
            GMSTeachingInfoMainNode = GMSTeachingMainNodeMatcher.group(1);
        }
        GMSMainPageGetRes.close();
    }

    private void getGMSScoreTableSubNodeId() throws Exception {
        URI GMSScoreTableIdPostUri = new URIBuilder()
                .setScheme("http")
                .setHost("gs.uestc.edu.cn")
                .setPath("/epstar/web/swms/mainframe/getmenu.jsp")
                .build();
        HttpPost GMSScoreTableIdPost = new HttpPost(GMSScoreTableIdPostUri);
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("node", GMSTeachingInfoMainNode));
        GMSScoreTableIdPost.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));
        GMSScoreTableIdPost.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse GMSScoreTableIdPostRes = httpClient.execute(GMSScoreTableIdPost, context);
        String GMSScoreTableIdPage = EntityUtils.toString(GMSScoreTableIdPostRes.getEntity());
        String GMSScoreTableIdPattern = "教学信息.+?\"id\":\"(.+?)\"";
        Matcher GMSScoreTableIdMatcher = findMatcher(GMSScoreTableIdPattern, GMSScoreTableIdPage);
        if (GMSScoreTableIdMatcher.find()) {
            GMSScoreTableSubNode = GMSScoreTableIdMatcher.group(1);
        }
        GMSScoreTableIdPostRes.close();
    }

    private void getGMSScoreTableRequestParams() throws Exception {
        URI GMSScoreTableRequestParamsPostUri = new URIBuilder()
                .setScheme("http")
                .setHost("gs.uestc.edu.cn")
                .setPath("/epstar/web/swms/mainframe/getmenu.jsp")
                .build();
        HttpPost GMSScoreTableRequestParamsPost = new HttpPost(GMSScoreTableRequestParamsPostUri);
        List<NameValuePair> formParams = new ArrayList<>();
        formParams.add(new BasicNameValuePair("node", GMSScoreTableSubNode));
        GMSScoreTableRequestParamsPost.setEntity(new UrlEncodedFormEntity(formParams, Consts.UTF_8));
        GMSScoreTableRequestParamsPost.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse GMSScoreTableRequestParamsPostRes = httpClient.execute(GMSScoreTableRequestParamsPost, context);
        String GMSScoreTableRequestParamsPage = EntityUtils.toString(GMSScoreTableRequestParamsPostRes.getEntity());
        String GMSScoreTableRequestParamsPattern = "成绩表学生查询.+?:\"(.+?)\\?mainobj=(.+?)&tfile=(.+?)&current.model.id=(.+?)\"";
        Matcher GMSScoreTableRequestParamsMatcher = findMatcher(GMSScoreTableRequestParamsPattern, GMSScoreTableRequestParamsPage);
        if (GMSScoreTableRequestParamsMatcher.find()) {
            GMSScoreTablePath = GMSScoreTableRequestParamsMatcher.group(1);
            GMSScoreTableMainObj = GMSScoreTableRequestParamsMatcher.group(2);
            GMSScoreTableTFile = GMSScoreTableRequestParamsMatcher.group(3);
            GMSScoreTableCurrentModelId = GMSScoreTableRequestParamsMatcher.group(4);
        }
        GMSScoreTableRequestParamsPostRes.close();
    }

    private void getGMSScoreTableFilter() throws Exception {
        URI GMSScoreTableFilterGetUri = new URIBuilder()
                .setScheme("http")
                .setHost("gs.uestc.edu.cn")
                .setPath(GMSScoreTablePath)
                .setParameter("mainobj", GMSScoreTableMainObj)
                .setParameter("tfile", GMSScoreTableTFile)
                .setParameter("current.model.id", GMSScoreTableCurrentModelId)
                .build();
        HttpGet GMSScoreTableFilterGet = new HttpGet(GMSScoreTableFilterGetUri);
        GMSScoreTableFilterGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse GMSScoreTableFilterGetRes = httpClient.execute(GMSScoreTableFilterGet, context);
        String GMSScoreTableFilterPage = EntityUtils.toString(GMSScoreTableFilterGetRes.getEntity());
        String GMSScoreTableFilterPattern = "src=\"(.+?)\\?mainobj=(.+?)&tfile=(.+?)&filter=(.+?)\"";
        Matcher GMSScoreTableFilterMatcher = findMatcher(GMSScoreTableFilterPattern, GMSScoreTableFilterPage);
        if (GMSScoreTableFilterMatcher.find()) {
            GMSScoreTablePath = GMSScoreTableFilterMatcher.group(1);
            GMSScoreTableMainObj = GMSScoreTableFilterMatcher.group(2);
            GMSScoreTableTFile = GMSScoreTableFilterMatcher.group(3);
            GMSScoreTableFilter = GMSScoreTableFilterMatcher.group(4);
        }
        GMSScoreTableFilterGetRes.close();
    }

    // 获取所有课程信息
    public String getAllCourse() throws Exception {
        if (authenticateFromPortal().equals("FAIL")) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.USER_PASSWORD_NOT_CORRENT);
        getGMSTeachingMainNodeId();
        getGMSScoreTableSubNodeId();
        getGMSScoreTableRequestParams();
        getGMSScoreTableFilter();
        URI allCourseGetUri = new URIBuilder()
                .setScheme("http")
                .setHost("gs.uestc.edu.cn")
                .setPath(GMSScoreTablePath)
                .setParameter("mainobj", GMSScoreTableMainObj)
                .setParameter("tfile", GMSScoreTableTFile)
                .setParameter("filter", GMSScoreTableFilter)
                .build();
        HttpGet allCourseGet = new HttpGet(allCourseGetUri);
        allCourseGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse allCourseGetRes = httpClient.execute(allCourseGet, context);
        String allCoursePage = EntityUtils.toString(allCourseGetRes.getEntity(), "UTF-8");
        String courseNamePattern = "<font id=kcmc >(.+?)</font>";
        String courseScorePattern = "<font id=cj >(.+?)</font>";
        String coursePointPattern = "<font id=xf >(.+?)</font>";
        String courseCategoryPattern = "<font id=kclbmc >(.+?)</font>";
        String courseTeacherPattern = "<font id=zjlszgh >(.+?)</font>";
        Matcher courseNameMatcher = findMatcher(courseNamePattern, allCoursePage);
        Matcher coursePointMatcher = findMatcher(coursePointPattern, allCoursePage);
        Matcher courseScoreMatcher = findMatcher(courseScorePattern, allCoursePage);
        Matcher courseCategoryMatcher = findMatcher(courseCategoryPattern, allCoursePage);
        Matcher courseTeacherMatcher = findMatcher(courseTeacherPattern, allCoursePage);
        JsonNodeFactory factory = new JsonNodeFactory(false);
        ObjectNode allCourseNode = factory.objectNode();
        ArrayNode courses = factory.arrayNode();
        while (courseNameMatcher.find() && coursePointMatcher.find() && courseScoreMatcher.find() && courseCategoryMatcher.find() && courseTeacherMatcher.find()) {
            ObjectNode course = factory.objectNode();
            course.put("课程类别", courseCategoryMatcher.group(1));
            course.put("课程名称", courseNameMatcher.group(1));
            course.put("课程老师", courseTeacherMatcher.group(1));
            course.put("课程学分", coursePointMatcher.group(1));
            course.put("课程得分", courseScoreMatcher.group(1));
            courses.add(course);
        }
        allCourseNode.set("已选课程", courses);
        allCourseGetRes.close();
        return allCourseNode.toString();
    }

    // 获取已修学分一览
    public String getAcquiredPoint() throws Exception {
        if (authenticateFromPortal().equals("FAIL")) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.USER_PASSWORD_NOT_CORRENT);
        getGMSTeachingMainNodeId();
        getGMSScoreTableSubNodeId();
        getGMSScoreTableRequestParams();
        getGMSScoreTableFilter();
        URI allCourseGetUri = new URIBuilder()
                .setScheme("http")
                .setHost("gs.uestc.edu.cn")
                .setPath(GMSScoreTablePath)
                .setParameter("mainobj", GMSScoreTableMainObj)
                .setParameter("tfile", GMSScoreTableTFile)
                .setParameter("filter", GMSScoreTableFilter)
                .build();
        HttpGet allCourseGet = new HttpGet(allCourseGetUri);
        allCourseGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse allCourseGetRes = httpClient.execute(allCourseGet, context);
        String allCoursePage = EntityUtils.toString(allCourseGetRes.getEntity(), "UTF-8");
        String publicFoundamentalCourse = "<font id=ggxwk >(.+?)</font>";
        String foundamentalCourse = "<font id=jck >(.+?)</font>";
        String majorFoundamentalCourse = "<font id=zyjck >(.+?)</font>";
        String electiveCourseInTheDiscipline = "<font id=bxkxxk >(.+?)</font>";
        String electiveCourseCrossTheDiscipline = "<font id=kxkxxk >(.+?)</font>";
        String electiveExpCourse = "<font id=syxxk >(.+?)</font>";
        String requiredSectionCourse = "<font id=bxhj >(.+?)</font>";
        Matcher publicFoundamentalCourseM = findMatcher(publicFoundamentalCourse, allCoursePage);
        Matcher foundamentalCourseM = findMatcher(foundamentalCourse, allCoursePage);
        Matcher majorFoundamentalCourseM = findMatcher(majorFoundamentalCourse, allCoursePage);
        Matcher electiveCourseInTheDisciplineM = findMatcher(electiveCourseInTheDiscipline, allCoursePage);
        Matcher electiveCourseCrossTheDisciplineM = findMatcher(electiveCourseCrossTheDiscipline, allCoursePage);
        Matcher electiveExpCourseM = findMatcher(electiveExpCourse, allCoursePage);
        Matcher requiredSectionCourseM = findMatcher(requiredSectionCourse, allCoursePage);
        JsonNodeFactory factory = new JsonNodeFactory(false);
        ObjectNode acquiredPoints = factory.objectNode();
        if (publicFoundamentalCourseM.find()
                && foundamentalCourseM.find()
                && majorFoundamentalCourseM.find()
                && electiveCourseInTheDisciplineM.find()
                && electiveCourseCrossTheDisciplineM.find()
                && electiveExpCourseM.find()
                && requiredSectionCourseM.find()) {
            acquiredPoints.put("公共基础课", publicFoundamentalCourseM.group(1));
            acquiredPoints.put("基础课", foundamentalCourseM.group(1));
            acquiredPoints.put("专业基础课", majorFoundamentalCourseM.group(1));
            acquiredPoints.put("本学科选修课", electiveCourseInTheDisciplineM.group(1));
            acquiredPoints.put("跨学科选修课", electiveCourseCrossTheDisciplineM.group(1));
            acquiredPoints.put("实验选修课", electiveExpCourseM.group(1));
            acquiredPoints.put("必修环节", requiredSectionCourseM.group(1));
        }
        return acquiredPoints.toString();
    }

    // 获取未评教的课程
    public String getUnTeachingEvaluationCourse() throws Exception {

        return null;
    }

    // 获取未通过的课程
    public String getUnpassedCourse() throws Exception {
        return null;
    }

    // 获取已通过的课程
    public String getPassedCourse() throws Exception {
        return null;
    }

    // 获取校园图书馆登陆验证码
    public byte[] getCaptchaFromUestcLib() throws Exception {
        return null;
    }

    // 获取校园图书馆借阅历史
    public String getReadingHistoryFromLib() throws Exception {
        if (!authenFormLib()) return ErrorNodeFactory.getErrorNode(ErrorNodeFactory.USER_PASSWORD_NOT_CORRENT);
        URI redirectedUri = new URIBuilder()
                .setScheme("https")
                .setHost("webpac.uestc.edu.cn")
                .setPath("/patroninfo~S1*chx/" + magicCode + "/readinghistory")
                .build();
        System.out.println("redirected uri:" + redirectedUri);
        HttpGet libReadingHistoryGet = new HttpGet(redirectedUri);
        libReadingHistoryGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse libReadingHistoryGetRes = httpClient.execute(libReadingHistoryGet, context);
        String libReadingHistoryGetPage = EntityUtils.toString(libReadingHistoryGetRes.getEntity());
        String bookNamePattern = "\"patFuncTitleMain\">(.+?)</span>";
        String bookAuthorPattern = "\"patFuncAuthor\">(.*?)</td>";
        String bookDatePattern = "\"patFuncDate\">(.+?)</td>";
        Matcher nameMatcher = findMatcher(bookNamePattern, libReadingHistoryGetPage);
        Matcher authorMatcher = findMatcher(bookAuthorPattern, libReadingHistoryGetPage);
        Matcher dataMatcher = findMatcher(bookDatePattern, libReadingHistoryGetPage);
        JsonNodeFactory factory = new JsonNodeFactory(false);
        ObjectNode historyObjectNode = factory.objectNode();
        ArrayNode histories = factory.arrayNode();
        while (nameMatcher.find() && authorMatcher.find() && dataMatcher.find()) {
            ObjectNode history = factory.objectNode();
            history.put("题名", nameMatcher.group(1));
            history.put("作者", authorMatcher.group(1));
            history.put("借出时间", dataMatcher.group(1));
            histories.add(history);
        }
        historyObjectNode.set("借阅历史", histories);
        libReadingHistoryGetRes.close();
        return historyObjectNode.toString();
    }

    // 校园图书馆登陆认证
    public boolean authenFormLib() throws Exception {
        URI libFormGetUri = new URIBuilder()
                .setScheme("https")
                .setHost("webpac.uestc.edu.cn")
                .setPath("/patroninfo*chx")
                .build();
        HttpGet libFormGet = new HttpGet(libFormGetUri);
        libFormGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse libFormGetRes = httpClient.execute(libFormGet, context);
        String libFormGetPage = EntityUtils.toString(libFormGetRes.getEntity());
        libFormGetRes.close();
        URI libFormPostUri = new URIBuilder()
                .setScheme("https")
                .setHost("webpac.uestc.edu.cn")
                .setPath("/patroninfo*chx")
                .build();
        HttpPost libFormPost = new HttpPost(libFormPostUri);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("extpatid", userName));
        formparams.add(new BasicNameValuePair("extpatpw", userPassword));
        formparams.add(new BasicNameValuePair("code", ""));
        formparams.add(new BasicNameValuePair("pin", ""));
        formparams.add(new BasicNameValuePair("submit.x", "50"));
        formparams.add(new BasicNameValuePair("submit.y", "20"));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        libFormPost.setEntity(entity);
        CloseableHttpResponse libFormPostRes = httpClient.execute(libFormPost, context);
        libFormPostRes.close();
        URI finalUrl = libFormPost.getURI();
        List<URI> locations = context.getRedirectLocations();
        if (locations != null) {
            finalUrl = locations.get(locations.size() - 1);
        }
        String magicCodePattern = "/(\\d+?)/";
        Matcher magicCodeMatcher = findMatcher(magicCodePattern, finalUrl.toString());
        if (!magicCodeMatcher.find()) return false;
        magicCode = magicCodeMatcher.group(1);
        return true;
    }


    public String getEcardPersonalPageFromSpider() throws Exception {
        URI ecardPersonalGetUri = new URIBuilder()
                .setScheme("http")
                .setHost("ecard.uestc.edu.cn")
                .setPath("/web/guest/personal")
                .build();
        HttpGet ecardPersonalGet = new HttpGet(ecardPersonalGetUri);
        ecardPersonalGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse ecardPersonalPageRes = httpClient.execute(ecardPersonalGet, context);
        String ecardPersonalPage = EntityUtils.toString(ecardPersonalPageRes.getEntity());
        ecardPersonalPageRes.close();
        return ecardPersonalPage;
    }

    private void getEcardAuthenticationFromSpider() throws Exception {
        URI ecardGetUri = new URIBuilder()
                .setScheme("http")
                .setHost("ecard.uestc.edu.cn")
                .setPath("/")
                .build();
        HttpGet ecardGet = new HttpGet(ecardGetUri);
        ecardGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse ecardGetRes = httpClient.execute(ecardGet, context);
        ecardGetRes.close();
        URI ecardLoginGetUri = new URIBuilder()
                .setScheme("http")
                .setHost("ecard.uestc.edu.cn")
                .setPath("/caslogin.jsp")
                .build();
        HttpGet ecardLoginGet = new HttpGet(ecardLoginGetUri);
        ecardLoginGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse ecardLoginGetRes = httpClient.execute(ecardLoginGet, context);
        ecardLoginGetRes.close();
        URI ecardPersonalGetUri = new URIBuilder()
                .setScheme("http")
                .setHost("ecard.uestc.edu.cn")
                .setPath("/")
                .build();
        HttpGet ecardPersonalGet = new HttpGet(ecardPersonalGetUri);
        ecardPersonalGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse ecardPersonalPageRes = httpClient.execute(ecardPersonalGet, context);
        ecardPersonalPageRes.close();
    }

    private String authenticateFromPortal() throws Exception {
        URI loginToPortalPostUri = new URIBuilder()
                .setScheme("http")
                .setHost("portal.uestc.edu.cn")
                .setPath("/index.portal")
                .build();
        HttpGet webformContentGet = new HttpGet(loginToPortalPostUri);
        CloseableHttpResponse webformContentGetRes = httpClient.execute(webformContentGet, context);
        String webformContentGetResString = EntityUtils.toString(webformContentGetRes.getEntity());
        Matcher m = findMatcher(PORTAL_FORM_REGEXP, webformContentGetResString);
        String[] forms = new String[5];
        int i = 0;
        while (m.find()) {
            forms[i++] = m.group(1);
        }
        URI webformContentPostUri = new URIBuilder()
                .setScheme("http")
                .setHost("idas.uestc.edu.cn")
                .setPath("/authserver/login")
                .setParameter("service", "http://portal.uestc.edu.cn/index.portal")
                .build();
        HttpPost webformContentPost = new HttpPost(webformContentPostUri);
        List<NameValuePair> formparams = new ArrayList<NameValuePair>();
        formparams.add(new BasicNameValuePair("username", userName));
        formparams.add(new BasicNameValuePair("password", userPassword));
        formparams.add(new BasicNameValuePair("lt", forms[0]));
        formparams.add(new BasicNameValuePair("dllt", forms[1]));
        formparams.add(new BasicNameValuePair("execution", forms[2]));
        formparams.add(new BasicNameValuePair("_eventId", forms[3]));
        formparams.add(new BasicNameValuePair("rmShown", forms[4]));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams, Consts.UTF_8);
        webformContentPost.setEntity(entity);
        CloseableHttpResponse webformContentPostRes = httpClient.execute(webformContentPost, context);
        webformContentPostRes.close();
        URI portalGetUri = new URIBuilder()
                .setScheme("http")
                .setHost("portal.uestc.edu.cn")
                .build();
        HttpGet portalGet = new HttpGet(portalGetUri);
        portalGet.addHeader("User-Agent", USER_AGENT);
        CloseableHttpResponse portalGetRes = httpClient.execute(portalGet, context);
        String content = EntityUtils.toString(portalGetRes.getEntity());
        portalGetRes.close();
        m = findMatcher("<li>(欢迎您：.+?)</li>", content);
        if (m.find()) {
            return m.group(1);
        }
        else {
            return "FAIL";
        }
    }
}
