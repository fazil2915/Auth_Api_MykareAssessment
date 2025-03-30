package Config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;

@Configuration
public class envConfig {
    private static  final Dotenv dotenv=Dotenv.load();

    public static String getEnv(String key){
        return dotenv.get(key);
    }
}
