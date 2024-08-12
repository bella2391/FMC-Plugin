package fabric;

import net.fabricmc.loader.api.FabricLoader;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Config
{
	private final FabricLoader fabric;
	private final Logger logger;
	private final Path dataDirectory;
	
    private Config instance = null;
    private Map<String, Object> config = null;
    
    public Config(FabricLoader fabric, Logger logger)
    {
    	this.fabric = fabric;
    	this.logger = logger;
    	this.dataDirectory = fabric.getConfigDir();
        instance = this;
    }

    public Map<String, Object> getConfig()
    {
    	if (Objects.isNull(config))
    	{
            // Configのインスタンスが初期化されていない場合は、設定を読み込む
            if (Objects.nonNull(instance))
            {
                try
                {
					instance.loadConfig();
				}
                catch (IOException e)
                {
                	logger.error("Error loading config",e);
				}
            }
            else
            {
            	logger.error("Config instance is not initialized.");
            }
        }
        return config;
    }
    
    public synchronized void loadConfig() throws IOException
    {
        Path configPath = dataDirectory.resolve("config.yml");
        
        // ディレクトリの作成
        if (Files.notExists(dataDirectory))
        {
            Files.createDirectories(dataDirectory);
        }
        
        // ファイルの作成
        if (!Files.exists(configPath))
        {
            try (InputStream in = getClass().getResourceAsStream("/config.yml"))
            {
            	if (Objects.isNull(in))
            	{
            		logger.error("Default configuration file not found in resources.");
                    return;
            	}
                Files.copy(in, configPath);
                
                // 読み込みと新規内容の追加
                String existingContent = Files.readString(configPath);
                String addContents = "";
                /*String addContents = "\n\nServers:\n    Hub: \"\"\n    Memory_Limit: ";
                addContents += "\n\n    Proxy:\n        Memory: ";
                
                // 例: サーバー名を追加する部分
                for (RegisteredServer server : server.getAllServers())
                {
                	addContents += "\n    "+server.getServerInfo().getName()+":";
                	addContents += "\n        Memory: ";
                	addContents += "\n        Bat_Path: \"\"";
                }*/
                
                // 新しい内容を追加してファイルに書き込み
                Files.writeString(configPath, existingContent + addContents);
            }
        }

        // Yamlでの読み込み
        Yaml yaml = new Yaml();
        try (InputStream inputStream = Files.newInputStream(configPath))
        {
            config = yaml.load(inputStream);
            if(Objects.isNull(config))
            {
            	logger.error("Failed to load YAML configuration, config is null.");
            }
            else
            {
            	logger.info("YAML configuration loaded successfully.");
            }
        }
        catch (IOException e)
        {
        	logger.error("Error reading YAML configuration file.", e);
        }
    }

    public void saveConfig() throws IOException
    {
        Path configPath = dataDirectory.resolve("config.yml");
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);
        try (FileWriter writer = new FileWriter(configPath.toFile()))
        {
            yaml.dump(config, writer);
        }
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getStringList(String key)
    {
    	if (Objects.isNull(config))
    	{
            logger.error("Config has not been initialized.");
            return Collections.emptyList();
        }
        Object value = config.get(key);
        if (value instanceof List)
        {
            return (List<String>) value;
        }
        else if (Objects.isNull(value))
        {
            logger.error("The key '" + key + "' does not exist in the configuration.");
            return Collections.emptyList();
        }
        else
        {
            logger.error("The value for the key '" + key + "' is not a list.");
            return Collections.emptyList();
        }
    }
    
    /**
     * 階層的なキーを指定して値を取得する
     * @param path 階層的なキー (例: "MySQL.Database")
     * @return 階層的なキーに対応する値
     */
    public Object getNestedValue(String path)
    {
        if (Objects.isNull(config))	return null;

        String[] keys = path.split("\\.");
        Map<String, Object> currentMap = config;

        for (int i = 0; i < keys.length; i++)
        {
            Object value = currentMap.get(keys[i]);

            if (Objects.isNull(value))	return null;

            if (i == keys.length - 1)	return value;

            if (value instanceof Map) 
            {
                currentMap = (Map<String, Object>) value;
            }
            else
            {
                return null; // キーがマップではない場合
            }
        }
        return null;
    }

    /**
     * 階層的なキーを指定して文字列を取得する
     * @param path 階層的なキー (例: "MySQL.Database")
     * @return 階層的なキーに対応する文字列値
     */
    // 階層的なキーを指定して文字列を取得する
    public String getString(String path, String defaultValue)
    {
        Object value = getNestedValue(path);
        return value instanceof String ? (String) value : defaultValue;
    }

    public String getString(String path)
    {
        return getString(path, null);
    }

    // 階層的なキーを指定してブール値を取得する
    public boolean getBoolean(String path, boolean defaultValue)
    {
        Object value = getNestedValue(path);
        return value instanceof Boolean ? (Boolean) value : defaultValue;
    }

    public boolean getBoolean(String path)
    {
        return getBoolean(path, false);
    }
    
    // 階層的なキーを指定して整数を取得する
    public int getInt(String path, int defaultValue)
    {
        Object value = getNestedValue(path);
        if (value instanceof Number)
        {
            return ((Number) value).intValue();
        }
        return defaultValue;
    }

    public int getInt(String path)
    {
        return getInt(path, 0);
    }
    
    // 階層的なキーを指定してlong型の整数を取得する
    public long getLong(String path, long defaultValue)
    {
        Object value = getNestedValue(path);
        if (value instanceof Number)
        {
            return ((Number) value).longValue();
        }
        return defaultValue;
    }

    public long getLong(String path)
    {
        return getLong(path, 0L);
    }
    
    // 階層的なキーを指定してリストを取得する
    @SuppressWarnings("unchecked")
    public List<String> getList(String path, List<String> defaultValue)
    {
        Object value = getNestedValue(path);
        return value instanceof List ? (List<String>) value : defaultValue;
    }

    public List<String> getList(String path)
    {
        return getList(path, Collections.emptyList());
    }
}
