package warpi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public List<Long> acceptedCids(@Value("${acceptedCids}") String cidList) {
        return Arrays.stream(cidList.split(","))
                .map(Long::valueOf)
                .collect(toList());
    }

}
