import com.github.opendevl.JFlat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class JsonReader {
    // Data apis.
    private static BigInteger max_ID, maxLastUpdated;
    final private static String SUBJECT_API = "https://gist.githubusercontent.com/sharish/08152723d3910ad5c15726cbce8993d4/raw/7e59de7c2b91375e676e04bfb036c3dbdc3bca28/subject.json";
    final private static String TEST_API = "https://gist.githubusercontent.com/sharish/45aa2116865624e59bf700e275954342/raw/8e34e8c13820dd7a16164e1438016f50f073f307/test.json";
    final private static String LESSON_API = "https://gist.githubusercontent.com/sharish/2b342b3b65fcb1d69af9d4b4c165ad13/raw/d5b1e3553f231135d4025b1848298795f192b6c6/lesson.json";
    private static boolean load_more = true;


    public static void main(String[] args) throws IOException, JSONException {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Set<String> set = new HashSet<>();
        set.add("test");
        set.add("lesson");
        set.add("subject");

        if (set.contains(input))
            JsonToCsv(input);
        else
            System.out.println("Provide valid input");
    }


    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }

    public static JSONArray readJsonFromUrl(String url) throws IOException, JSONException {
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            String jsonText = readAll(rd);
            JSONObject jsonObject = new JSONObject(jsonText);
            jsonObject = jsonObject.getJSONObject("data");
            load_more = (jsonObject.getBoolean("load_more"));

            System.out.println(load_more);
            return jsonObject.getJSONArray("data");
        }
    }

    public static void JsonToCsv(String apiType) throws IOException, JSONException {

        String inputApi = validate(apiType);
        // string array consists of desired fields //

        HashSet<String> wantedFields = desiredFields();

        while (load_more) {
            JSONArray jsonArray = readJsonFromUrl(inputApi);
            if (jsonArray == null)
                break;
            String jsonDataString = partitioningJsonArray(jsonArray, wantedFields);
            writingJsonToCsv(jsonDataString, apiType);
        }
    }

    // bhool gae false karna tio iuska mtlab next data jasoa arry empty:: /
    //total count break//
    public static String validate(String apiType) {
        String inputApi = "";
        if (apiType.equals("lesson"))
            inputApi = LESSON_API;
        else if (apiType.equals("test"))
            inputApi = TEST_API;
        else if (apiType.equals("subject"))
            inputApi = SUBJECT_API;
        return inputApi;
    }

    public static HashSet<String> desiredFields() {
        HashSet<String> wantedFields = new HashSet<>();
        wantedFields.add("_id");
        wantedFields.add("last_updated");
        wantedFields.add("correct");
        wantedFields.add("subject_id");
        return wantedFields;
    }

    // required fileds present
    // false msg break;;

    public static String partitioningJsonArray(JSONArray jsonArray, HashSet<String> wantedFields) {
        max_ID = new BigInteger("1");
        maxLastUpdated = new BigInteger("1");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject k = (JSONObject) jsonArray.get(i);
            ArrayList<String> arr = new ArrayList<>(k.keySet());
            for (String s : arr) {
                if (!wantedFields.contains(s))
                    k.remove(s);
            }
            BigInteger tempMaxId = new BigInteger(k.getString("_id"), 16);
            BigInteger tempLastUpdated = k.getBigInteger("last_updated");
            max_ID = max_ID.max(tempMaxId);
            maxLastUpdated = maxLastUpdated.max(tempLastUpdated);
        }
        System.out.println("MAX BIGINT: " + max_ID);
        return jsonArray.toString();
    }

    public static void writingJsonToCsv(String jsonDataString, String apiType) {
        try {
            //Writing retrieved json string into csv file.
            JFlat flatMe = new JFlat(jsonDataString);
            flatMe.json2Sheet()
                    .headerSeparator("/")
                    .write2csv("/Users/apple/Desktop/" + apiType + ".csv");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static BigInteger getMaxID() {
        return max_ID;
    }

    public static BigInteger getMaxLastUpdated() {
        return max_ID;
    }
}