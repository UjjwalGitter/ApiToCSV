import org.json.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestSuite {
    public static final String API = "https://gist.githubusercontent.com/sharish/2b342b3b65fcb1d69af9d4b4c165ad13/raw/d5b1e3553f231135d4025b1848298795f192b6c6/lesson.json";
    @Test
    public void generate(){
        System.out.println("This is good");
    }

    @Test
    public void validateTest(){
        String originalApi = "https://gist.githubusercontent.com/sharish/2b342b3b65fcb1d69af9d4b4c165ad13/raw/d5b1e3553f231135d4025b1848298795f192b6c6/lesson.json";
        boolean isApiCorrect =false;
        String inputApi = JsonReader.validate("lesson");
        assertEquals(inputApi,originalApi);
    }
    @Test
    public void desiredFieldsTest(){
        HashSet<String> wantedFields =  new HashSet<>();
        wantedFields.add("_id");
        wantedFields.add("last_updated");
        wantedFields.add("correct");
        wantedFields.add("subject_id");
        assertEquals(JsonReader.desiredFields(),wantedFields);
    }

    @Test
    public void readJsonFromUrlTest() throws IOException, org.json.simple.parser.ParseException {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject =  (JSONObject) jsonParser.parse(new FileReader("src/main/resources/lesson.json"));
        String str = jsonObject.toJSONString();
        org.json.JSONObject jsonObject1 = new org.json.JSONObject(str);

        org.json.JSONObject jsonObject2 = jsonObject1.getJSONObject("data");
        JSONArray jsonArray = jsonObject2.getJSONArray("data");
            JSONArray expected = JsonReader.readJsonFromUrl(API);
            String expectedJsonString = jsonArray.toString();
            String actualJsonString = expected.toString();
            assertEquals(expectedJsonString, actualJsonString);
    }

    @Test
    public void getMaximum_id(){

    }

}
