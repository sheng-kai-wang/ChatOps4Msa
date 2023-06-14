package ntou.soselab.chatops4msa.Service.LowCodeService;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.InvokedFunction;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// TODO: debug this class
public class InvokedFunctionNameDeserializer extends StdDeserializer<InvokedFunction> {

    public InvokedFunctionNameDeserializer() {
        this(null);
    }

    public InvokedFunctionNameDeserializer(Class<?> vc) {
        super(vc);
    }

//    @Override
//    public InvokedFunction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
//        InvokedFunction invokedFunction = new InvokedFunction();
//        invokedFunction.setName(jsonParser.getCurrentName());
////        System.out.println("========= jsonParser: " + jsonParser.getText());
//        if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
//            Map<String, String> parameterMap = new HashMap<>();
//            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
//                if (jsonParser.getCurrentToken() == JsonToken.FIELD_NAME) {
//                    String parameterName = jsonParser.getCurrentName();
//                    jsonParser.nextToken();
//                    String parameterValue = jsonParser.getValueAsString();
//                    parameterMap.put(parameterName, parameterValue);
//                    jsonParser.nextToken();
//                }
//            }
//            if (!parameterMap.isEmpty()) {
//                invokedFunction.setParameterMap(parameterMap);
//            }
//        }
//        return invokedFunction;
//    }

    @Override
    public InvokedFunction deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        InvokedFunction invokedFunction = new InvokedFunction();
//        invokedFunction.setName(jsonParser.getCurrentName());
//        if (jsonParser.getCurrentToken() == JsonToken.START_OBJECT) {
//            Map<String, String> parameterMap = new HashMap<>();
//            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
//                String parameterName = jsonParser.getCurrentName();
//                jsonParser.nextToken();
//                String parameterValue = jsonParser.getValueAsString();
//                parameterMap.put(parameterName, parameterValue);
//            }
//            invokedFunction.setParameterMap(parameterMap);
//            jsonParser.nextToken();
//        }
        return invokedFunction;
    }
}
