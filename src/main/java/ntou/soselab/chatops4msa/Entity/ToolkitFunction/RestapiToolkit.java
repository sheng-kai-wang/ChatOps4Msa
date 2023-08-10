package ntou.soselab.chatops4msa.Entity.ToolkitFunction;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RestapiToolkit extends ToolkitFunction {

    public String toolkitRestapiGet(String url) {
        RestTemplate restTemplate = new RestTemplate();
        url = url.replaceAll("\"", "");
        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        return responseEntity.getBody();
    }

    public String toolkitRestapiPost(String url, String body, String authorization) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorization);
        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getBody();
    }
}
