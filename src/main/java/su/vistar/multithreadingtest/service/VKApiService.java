package su.vistar.multithreadingtest.service;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.LinkedHashMap;
import java.util.Map;
import su.vistar.multithreadingtest.dto.UsersSearchResponse;

public class VKApiService extends HTTPService {
    //токен должен быть для каждого пользователя приложения свой, этой мой токен
    //брать из базы
    private final String ACCESS_TOKEN = "ee198ba34004757f86d9456bfbd5832505855526c44ff623ff2ae908533fac2d92a3d74744d509eeddb54";

    //https://oauth.vk.com/authorize?client_id=5786702&redirect_uri=https://oauth.vk.com/blank.html&scope=offline,messages&response_type=token&v=5.62
    public boolean sendMessage(String userId, String message) throws IOException, MalformedURLException, MalformedURLException, IOException, UnsupportedEncodingException {
        String query = "https://api.vk.com/method/messages.send";
        Map<String, String> params = new LinkedHashMap<>();
        params.put("user_ids", userId);
        params.put("message", message);
        params.put("access_token", ACCESS_TOKEN);
        String response = doPOSTQuery(query, params);
        return (!response.toString().contains("error"));
    }

    public UsersSearchResponse getPeople(String query, int offset) throws IOException {
        String baseUrl = "https://api.vk.com/method/users.search?v=5.62&access_token=%s&count=100&offset=%d&";
        baseUrl = String.format(baseUrl, ACCESS_TOKEN, offset);
        baseUrl += query;
        String response = doGETQuery(baseUrl);
        UsersSearchResponse answer = gson.fromJson(response, UsersSearchResponse.class);
        return answer;
    }
}
