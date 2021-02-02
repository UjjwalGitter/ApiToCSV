import com.github.opendevl.JFlat;
import model.Model;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.FileEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.*;
import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.*;


// model class consisting of max id and max last updated
// modify readJsonfunction
// book_update to json
public class JsonReader {
    // Data apis.
    public static Model model;
    final private static String SUBJECT_API = "https://gist.githubusercontent.com/sharish/08152723d3910ad5c15726cbce8993d4/raw/7e59de7c2b91375e676e04bfb036c3dbdc3bca28/subject.json";
    final private static String TEST_API = "https://gist.githubusercontent.com/sharish/45aa2116865624e59bf700e275954342/raw/8e34e8c13820dd7a16164e1438016f50f073f307/test.json";
    final private static String LESSON_API = "https://gist.githubusercontent.com/sharish/2b342b3b65fcb1d69af9d4b4c165ad13/raw/d5b1e3553f231135d4025b1848298795f192b6c6/lesson.json";
    private static boolean loadMore = true;
    public static final int  MAX_API_COUNT = 10;// 50000

    public static void main(String[] args) throws Exception {

     /*   String perlApi = "https://drive.google.com/file/d/1yNySiIDfdpeJenIbmhC3em88-kN1VpiR/view?usp=sharing";
        String modifiedPerlApi = convertDownloadableLink(perlApi);
        jsonToCsv(modifiedPerlApi); */
      //  System.out.println(jsonArray.toString());
      //  writingJsonToCsv(jsonArray.toString(),"abacus");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        Set<String> set = new HashSet<>();
        set.add("test");
        set.add("lesson");
        set.add("subject");
       // getJSON();

        if (set.contains(input))
            jsonToCsv(input);
        else
            System.out.println("Provide valid input");
    }

    public static void jsonToCsv(String apiType) throws IOException, JSONException, URISyntaxException {

        String inputApi = validate(apiType);
        HashSet<String> wantedFields = desiredFields();

        int counter = MAX_API_COUNT;
        model = new Model(new BigInteger("0"),new BigInteger("0"));

        while (loadMore && counter>0) {
            JSONArray jsonArray = readJsonFromUrl(inputApi, model);

            if (jsonArray == null)
                break;

            String jsonDataString = partitioningJsonArray(jsonArray, wantedFields);
            writingToJson(jsonDataString,apiType);
            writingJsonToCsv(jsonDataString, apiType);
          //  System.out.println(counter);
            counter--;
        }
    }

    public static String validate(String apiType) {
        return switch (apiType) {
            case "lesson" -> LESSON_API;
            case "test" -> TEST_API;
            case "subject" -> SUBJECT_API;
            default -> "";
        };
    }

    public static HashSet<String> desiredFields() {
        HashSet<String> wantedFields = new HashSet<>();
        wantedFields.add("_id");
        wantedFields.add("subject_id");
        wantedFields.add("last_updated");
        return wantedFields;
    }

    //first 0,0

    public static JSONArray readJsonFromUrl(String inputApi, Model model) throws IOException, URISyntaxException {
     //   HttpGet request = new HttpGet(inputApi);
        URIBuilder builder = new URIBuilder(inputApi);
        builder.setParameter("_id",model.maxID.toString());
        builder.setParameter("last_updated",model.maxLastUpdated.toString());

        HttpGet request = new HttpGet(builder.build());

       // System.out.println(request.toString());
        setHeaderToRequest(request);
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = client.execute(request);

        HttpEntity entity = response.getEntity();
        String result = EntityUtils.toString(entity);
        JSONObject jsonObject = new JSONObject(result);
        JSONObject jo1 = jsonObject.getJSONObject("data");

        jsonObject = jsonObject.getJSONObject("data");
        loadMore = (jsonObject.getBoolean("load_more"));

        return jo1.getJSONArray("data");
    }


    // maxid, maxLast, json

    public static String partitioningJsonArray(JSONArray jsonArray, HashSet<String> wantedFields) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject k = (JSONObject) jsonArray.get(i);
            ArrayList<String> arr = new ArrayList<>(k.keySet());
            for (String s : arr) {
                if (!wantedFields.contains(s))
                    k.remove(s);
            }
            BigInteger tempMaxId = new BigInteger(k.getString("_id"), 16);
            BigInteger tempLastUpdated = k.getBigInteger("last_updated");

            model.setMaxID(model.getMaxID().max(tempMaxId));
            model.setMaxLastUpdated(model.getMaxLastUpdated().max(tempLastUpdated));
        }
      //  System.out.println("MAX BIGINT: " + max_ID);
        return jsonArray.toString();
    }

    public static void writingToJson(String jsonArray,String apiType) {
        try {
            FileWriter file = new FileWriter("/Users/apple/Desktop/"+apiType+".json");
            file.write(jsonArray);
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public static void setHeaderToRequest(HttpGet request){

        request.addHeader("platform","android");
        request.addHeader("timestamp","epochtime");
        request.addHeader("app-version","200");
    }

    // for converting google drive links into downloadable
    public static String convertDownloadableLink(String url) {
        String[] arr = url.split("/");
        return "https://drive.google.com/u/0/uc?id=" + arr[5] + "&export=download";
    }

//    public static void uploadFile(){
//        String url = "https://example.com/some_endpoint";
//        File file = new File("/Users/apple/Desktop/lesson.csv");
//
//        try (CloseableHttpClient client = HttpClients.createDefault()) {
//            HttpPost post = new HttpPost(url);
//            HttpEntity entity = MultipartEntityBuilder.create().addPart("file", new FileBody(file)).build();
//            post.setEntity(entity);
//
//            try (CloseableHttpResponse response = client.execute(post)) {
//                // ...
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static void executeRequest(HttpPost httpPost) {
        try {
            HttpClient client =  HttpClientBuilder.create().build();;
            HttpResponse response = client.execute(httpPost);
            System.out.println("Response Code:  " + response.getStatusLine().getStatusCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void executeMultiPartRequest(String urlString, File file) {//
        HttpPost postRequest = new HttpPost(urlString);
        String accessToken = "";
        HttpPost modifiedPostRequest = addHeader(postRequest, accessToken);
        try {
            modifiedPostRequest.setEntity(new FileEntity(file));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        executeRequest(modifiedPostRequest);
    }

    private static HttpPost addHeader(HttpPost httpPost, String accessToken) {
        httpPost.setHeader("Authorization", "Bearer " + accessToken);
        return httpPost;
    }

    /*
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
    private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
            sb.append((char) cp);
        }
        return sb.toString();
    }
*/

}