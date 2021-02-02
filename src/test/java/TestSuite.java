import model.Model;
import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSuite {
    public static final String API = "https://gist.githubusercontent.com/sharish/2b342b3b65fcb1d69af9d4b4c165ad13/raw/d5b1e3553f231135d4025b1848298795f192b6c6/lesson.json";
    public static String maxId = "";
    public static Model model;
    public static String maxLastUpdated = "";
    public static String jsonStringFromLocal = "";

    @BeforeEach
    public void beforeApiCall() throws IOException, ParseException, URISyntaxException {
        BigInteger max_id = new BigInteger("1");
        BigInteger max_last_updated = new BigInteger("1");
        JsonReader.jsonToCsv("lesson");
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject =  (JSONObject) jsonParser.parse(new FileReader("src/main/resources/lesson.json"));
        String str = jsonObject.toJSONString();
        org.json.JSONObject jsonObject1 = new org.json.JSONObject(str);
        org.json.JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
        JSONArray jsonArray = jsonObject2.getJSONArray("data");

        jsonStringFromLocal = jsonArray.toString();

        for (int i = 0; i < jsonArray.length(); i++) {
            org.json.JSONObject jsonObject3 = jsonArray.getJSONObject(i);
            BigInteger tempId = new BigInteger(jsonObject3.getString("_id"),16);
            BigInteger tempLastUpdated = jsonObject3.getBigInteger("last_updated");

            max_id = max_id.max(tempId);
            max_last_updated = max_last_updated.max(tempLastUpdated);
        }
        maxId = max_id.toString();
        maxLastUpdated = max_last_updated.toString();
    }


    @Test
    public void validateTest(){
        String inputApi = JsonReader.validate("lesson");
        assertEquals(inputApi,API);
    }
    @Test
    public void desiredFieldsTest(){
        HashSet<String> wantedFields =  new HashSet<>();
        wantedFields.add("_id");
        wantedFields.add("last_updated");
        wantedFields.add("subject_id");
        assertEquals(JsonReader.desiredFields(),wantedFields);
    }

    @Test
    public void readJsonFromUrlTest() throws IOException, URISyntaxException {
        JSONArray expectedJson = JsonReader.readJsonFromUrl(API,model);
            String expectedJsonString = expectedJson.toString();
            String actualJsonString = jsonStringFromLocal;
            assertEquals(expectedJsonString, actualJsonString);
    }

    @Test
    public void getMaxIdTest()  { assertEquals(JsonReader.model.getMaxID().toString(), maxId); }

    @Test
    public void getMaxLastUpdated(){
        System.out.println(maxLastUpdated);
        assertEquals(JsonReader.model.getMaxLastUpdated().toString(),maxLastUpdated);
    }

}
