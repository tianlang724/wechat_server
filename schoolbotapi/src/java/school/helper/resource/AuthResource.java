package school.helper.resource;

import school.helper.crepper.WebContentManager;
import school.helper.storage.MySqlOperator;
import school.helper.storage.UserPrivacyInfo;
import school.helper.thread.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/auth")
public class AuthResource {

    @Path("/lib")
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.TEXT_HTML)
    public String authenticateFromLib(@FormParam("username") String username, @FormParam("password") String password,
                                         @FormParam("usertoken") String usertoken) throws Exception {
        WebContentManager webContentManager = new WebContentManager(username, password, true);
        boolean result = webContentManager.authenFormLib();
        if (!result) return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style type=\"text/css\">\n" +
                "        body {\n" +
                "            position: fixed;\n" +
                "            height: 100%;\n" +
                "            width: 100%;\n" +
                "            margin: 0px;\n" +
                "            background-color: #417690;\n" +
                "        }\n" +
                "        #backgroud {\n" +
                "            background-color: #417690;\n" +
                "        }\n" +
                "        #fail {\n" +
                "            margin-top: 40vh;\n" +
                "            margin-left: auto;\n" +
                "            margin-right: auto;\n" +
                "            text-align: left;\n" +
                "            width: 90%;\n" +
                "            height: 20vh;\n" +
                "            background-color: white;\n" +
                "            padding: 30px 40px 30px 40px;\n" +
                "            font-size: xx-large;\n" +
                "            color: #417690;\n" +
                "        }\n" +
                "    </style>\n" +
                "    <title>出错啦！</title>\n" +
                "</head>\n" +
                "<body >\n" +
                "<div id=\"backgroud\">\n" +
                "        <p  id=\"fail\">用户名密码错误</p>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
        else {
            new MySqlOperator().updateLibPrivacyInfoFromDB(usertoken, username, password);
            new LibHistoryThread(new WebContentManager(username, password, true), usertoken);
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <style type=\"text/css\">\n" +
                    "        body {\n" +
                    "            position: fixed;\n" +
                    "            height: 100%;\n" +
                    "            width: 100%;\n" +
                    "            margin: 0px;\n" +
                    "            background-color: #417690;\n" +
                    "        }\n" +
                    "        #backgroud {\n" +
                    "            background-color: #417690;\n" +
                    "        }\n" +
                    "        #fail {\n" +
                    "            margin-top: 40vh;\n" +
                    "            margin-left: auto;\n" +
                    "            margin-right: auto;\n" +
                    "            text-align: left;\n" +
                    "            width: 90%;\n" +
                    "            height: 20vh;\n" +
                    "            background-color: white;\n" +
                    "            padding: 30px 40px 30px 40px;\n" +
                    "            font-size: xx-large;\n" +
                    "            color: #417690;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "    <title>Welcome</title>\n" +
                    "</head>\n" +
                    "<body >\n" +
                    "<div id=\"backgroud\">\n" +
                    "        <p  id=\"fail\">" + "登陆成功" + "</p>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";
        }
    }

    @Path("/portal")
    @POST
    @Consumes("application/x-www-form-urlencoded")
    @Produces(MediaType.TEXT_HTML)
    public String authenticateFromPortal(@FormParam("username") String username, @FormParam("password") String password,
                                         @FormParam("usertoken") String usertoken) throws Exception {
        WebContentManager webContentManager = new WebContentManager(username, password, false);
        String result = webContentManager.varifyEcardIdentification();
        if (result.equals("FAIL")) return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <style type=\"text/css\">\n" +
                "        body {\n" +
                "            position: fixed;\n" +
                "            height: 100%;\n" +
                "            width: 100%;\n" +
                "            margin: 0px;\n" +
                "            background-color: #417690;\n" +
                "        }\n" +
                "        #backgroud {\n" +
                "            background-color: #417690;\n" +
                "        }\n" +
                "        #fail {\n" +
                "            margin-top: 40vh;\n" +
                "            margin-left: auto;\n" +
                "            margin-right: auto;\n" +
                "            text-align: left;\n" +
                "            width: 90%;\n" +
                "            height: 20vh;\n" +
                "            background-color: white;\n" +
                "            padding: 30px 40px 30px 40px;\n" +
                "            font-size: xx-large;\n" +
                "            color: #417690;\n" +
                "        }\n" +
                "    </style>\n" +
                "    <title>出错啦！</title>\n" +
                "</head>\n" +
                "<body >\n" +
                "<div id=\"backgroud\">\n" +
                "        <p  id=\"fail\">用户名密码错误</p>\n" +
                "</div>\n" +
                "</body>\n" +
                "</html>";
        else {
            new MySqlOperator().updateEcardPrivacyInfoFromDB(usertoken, username, password);
            new BalanceThread(new WebContentManager(username, password, false), usertoken);
            new ExpendThread(new WebContentManager(username, password, false), usertoken);
            new RechargeThread(new WebContentManager(username, password, false), usertoken);
            new ChoosenCourseThread(new WebContentManager(username, password, false), usertoken);
            new CoursePointThread(new WebContentManager(username, password, false), usertoken);
            return "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <style type=\"text/css\">\n" +
                    "        body {\n" +
                    "            position: fixed;\n" +
                    "            height: 100%;\n" +
                    "            width: 100%;\n" +
                    "            margin: 0px;\n" +
                    "            background-color: #417690;\n" +
                    "        }\n" +
                    "        #backgroud {\n" +
                    "            background-color: #417690;\n" +
                    "        }\n" +
                    "        #fail {\n" +
                    "            margin-top: 40vh;\n" +
                    "            margin-left: auto;\n" +
                    "            margin-right: auto;\n" +
                    "            text-align: left;\n" +
                    "            width: 90%;\n" +
                    "            height: 20vh;\n" +
                    "            background-color: white;\n" +
                    "            padding: 30px 40px 30px 40px;\n" +
                    "            font-size: xx-large;\n" +
                    "            color: #417690;\n" +
                    "        }\n" +
                    "    </style>\n" +
                    "    <title>Welcome</title>\n" +
                    "</head>\n" +
                    "<body >\n" +
                    "<div id=\"backgroud\">\n" +
                    "        <p  id=\"fail\">"+result + "</p>\n" +
                    "</div>\n" +
                    "</body>\n" +
                    "</html>";
        }
    }
}
