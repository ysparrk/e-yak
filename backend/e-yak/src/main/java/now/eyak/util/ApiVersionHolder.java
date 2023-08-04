package now.eyak.util;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class ApiVersionHolder {
    private final String version;

    public ApiVersionHolder(@Value("${api-version}") String version) {
        this.version = version;
    }
}
