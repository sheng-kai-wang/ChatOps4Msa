package ntou.soselab.chatops4msa.Service.LowCodeService;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import ntou.soselab.chatops4msa.Entity.Capability.DevOpsTool.LowCode.InvokedOperation;

import java.io.IOException;

public class InvokedOperationNameDeserializer extends StdDeserializer<InvokedOperation> {

    public InvokedOperationNameDeserializer() {
        super(InvokedOperation.class);
    }

    @Override
    public InvokedOperation deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        InvokedOperation invokedOperation = new InvokedOperation();

        // process 'name' property
        JsonNode rootNode = jsonParser.readValueAsTree();
        JsonNode nameNode = rootNode.get("name");
        if (nameNode != null) {
            invokedOperation.setName(nameNode.textValue());
        }

        return invokedOperation;
    }
}
