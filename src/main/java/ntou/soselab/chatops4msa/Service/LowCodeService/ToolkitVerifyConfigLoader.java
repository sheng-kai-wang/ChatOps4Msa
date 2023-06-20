package ntou.soselab.chatops4msa.Service.LowCodeService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ToolkitVerifyConfigLoader {
    private static final String TOOLKIT_VERIFY_CLASSPATH = "classpath*:toolkit_verify.{yml,yaml}";
    public static final Map<String, List<String>> CONFIG_MAP;

    static {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(classLoader);
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Resource[] resources = resolver.getResources(TOOLKIT_VERIFY_CLASSPATH);
            CONFIG_MAP = mapper.readValue(resources[0].getInputStream(), Map.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
