import com.github.opendevl.JFlat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class JsonReader {
    // Data apis.
    final private static String SUBJECT_API = "https://gist.githubusercontent.com/sharish/08152723d3910ad5c15726cbce8993d4/raw/7e59de7c2b91375e676e04bfb036c3dbdc3bca28/subject.json";
    final private static String TEST_API = "https://gist.githubusercontent.com/sharish/45aa2116865624e59bf700e275954342/raw/8e34e8c13820dd7a16164e1438016f50f073f307/test.json";
    final private static String LESSON_API = "https://gist.githubusercontent.com/sharish/2b342b3b65fcb1d69af9d4b4c165ad13/raw/d5b1e3553f231135d4025b1848298795f192b6c6/lesson.json";

    public static void main(String[] args) throws IOException, JSONException {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        JsonToCsv(input);
    }

    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            return new JSONObject(jsonText);
        }
    }

    public static void JsonToCsv(String apiType) throws IOException, JSONException{

        // string array consists of undesired fields
        String[] unwantedFields = {"data","list","time","updated_id"};

        String inputApi = "";
        if(apiType.equals("lesson"))
            inputApi = LESSON_API;
        if(apiType.equals("test"))
            inputApi = TEST_API;
        if(apiType.equals("subject"))
            inputApi = SUBJECT_API;

        JSONObject json = readJsonFromUrl(inputApi);
        JSONObject jsonObject = json.getJSONObject("data");
        JSONArray jsonArray = jsonObject.getJSONArray("data");


        // removing unwanted fields from the jsonArray
        for(int i=0; i<jsonArray.length(); i++){
            JSONObject k = (JSONObject)jsonArray.get(i);
            for (String unwantedField : unwantedFields) {
                k.remove(unwantedField);
            }
        }

        System.out.println(jsonArray.toString());


        String jsonDataString = jsonArray.toString();
        try {
            //Writing retrieved json string into csv file.
            JFlat flatMe = new JFlat(jsonDataString);
            flatMe.json2Sheet()
                    .headerSeparator("/")
                    .write2csv("/Users/apple/Desktop/"+apiType+".csv");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}







