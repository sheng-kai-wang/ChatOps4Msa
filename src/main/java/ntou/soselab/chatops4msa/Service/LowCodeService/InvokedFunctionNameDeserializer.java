package ntou.soselab.chatops4msa.Service.LowCodeService;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.InvokedFunction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// TODO: debug this class
public class InvokedFunctionNameDeserializer extends JsonDeserializer<InvokedFunction> {
    @Override
    public InvokedFunction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        InvokedFunction invokedFunction = new InvokedFunction();
        invokedFunction.setName(jsonParser.getCurrentName());
        jsonParser.nextToken();
        if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
            Map<String, String> parameterMap = new HashMap<>();
            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String parameterName = jsonParser.getCurrentName();
                jsonParser.nextToken();
                String parameterValue = jsonParser.getValueAsString();
                parameterMap.put(parameterName, parameterValue);
            }
            invokedFunction.setParameterMap(parameterMap);
        }
        return invokedFunction;
    }
}
