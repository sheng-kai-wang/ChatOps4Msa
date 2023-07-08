package ntou.soselab.chatops4msa.Entity.ToolkitFunction;

import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class GraphqlToolkit extends ToolkitFunction {
    public String toolkitGraphqlQuery(String url, String graphql, String authorization) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", authorization);
        MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<>();
        requestBody.add("query", graphql);
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
        return responseEntity.getBody();
    }
}
