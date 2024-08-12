package fabric;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.loader.api.FabricLoader;

@SpringBootApplication
public class FabricApplication
{

    @Bean
    public CommandLineRunner run(Config config, Logger logger) {
        return args -> {
            logger.info(config.getString("MySQL.Host"));
        };
    }
    
    @Bean
    public Config config(FabricLoader fabricLoader, Logger logger) {
        Config config = new Config(fabricLoader, logger);
        try {
            config.loadConfig();
        } catch (IOException e) {
            logger.error("Error loading config", e);
        }
        return config;
    }

    @Bean
    public Logger logger() {
        return LoggerFactory.getLogger("FMC");
    }

    @Bean
    public FabricLoader fabricLoader() {
        return FabricLoader.getInstance();
    }
}
