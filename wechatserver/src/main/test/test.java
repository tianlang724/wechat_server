import com.fasterxml.jackson.databind.JsonNode;
import com.github.binarywang.demo.spring.business.HttpHelper;
import com.github.binarywang.demo.spring.datebase.DatabaseHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class test{
    @org.junit.Test
    public void test() {

        DatabaseHelper.queryDatabase("user_ecard","balance","oZhbkvomCe_Q9zghX5nFo1kF7RiM");
        DatabaseHelper.queryDatabase("user_ecard","expend","oZhbkvomCe_Q9zghX5nFo1kF7RiM");
        DatabaseHelper.queryDatabase("user_ecard","expend","123");
        DatabaseHelper.queryPassword("ecard","oZhbkvomCe_Q9zghX5nFo1kF7RiM");
        DatabaseHelper.queryPassword("bbs","oZhbkvomCe_Q9zghX5nFo1kF7RiM");

    }
    @org.junit.Test
    public void testLuis(){
        String usrContext="十一月十七号饭卡消费记录";
        JsonNode root= HttpHelper.getLuisResponse(usrContext);
        long endTime = System.currentTimeMillis();
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
                    System.out.println(time);
                    time=time.substring(0,4)+time.substring(5,7)+time.substring(8,10);
                    System.out.println(time);
                }
            }
        }
    }
    @org.junit.Test
    public void testRep(){
        String intent="2014fjidjfid";
        Pattern r = Pattern.compile("^2014.*");
        Matcher m = r.matcher(intent);
        if (m.matches()) {
            System.out.print("yes");
        }
    }
}
