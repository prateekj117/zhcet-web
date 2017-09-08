package in.ac.amu.zhcet.configuration;

import in.ac.amu.zhcet.data.model.configuration.Configuration;
import in.ac.amu.zhcet.data.model.configuration.ConfigurationModel;
import in.ac.amu.zhcet.data.repository.ConfigurationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ConfigurationComponent {

    @Autowired
    public ConfigurationComponent(ConfigurationRepository configurationRepository) {
        log.info("Checking default configuration of application");
        Configuration configuration = configurationRepository.findFirstByOrderByIdDesc();

        if (configuration == null) {
            log.info("Default configuration absent... Building new config");
            Configuration defaultConfiguration = new Configuration();
            configurationRepository.save(defaultConfiguration);
            log.info("Saved default configuration : " + defaultConfiguration);
        } else if(configuration.getConfig().getVersion() < ConfigurationModel.VERSION) {
            log.info("Outdated configuration schema... Updating");
            configuration.setId(null);
            configuration.getConfig().setVersion(ConfigurationModel.VERSION);
            configurationRepository.save(configuration);
            log.info("Updated configuration : " + configuration);
        } else {
            log.info("Configuration already present : " + configuration);
        }
    }

}