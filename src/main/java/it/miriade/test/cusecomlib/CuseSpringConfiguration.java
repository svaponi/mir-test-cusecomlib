package it.miriade.test.cusecomlib;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import it.miriade.test.cusecomlib.utils.CustomPropertySource;
import it.miriade.test.cusecomlib.yaml.YamlSupport;
import it.miriade.test.cusecomlib.yaml.YamlSupportFactory;

/**
 * JavaConfig. Classe che inizializza il contesto di Spring.
 * 
 * @author svaponi
 */
@Configuration
@ComponentScan(basePackages = { "it.miriade.test.cusecomlib", "cucumber.miriade" })
public class CuseSpringConfiguration implements CuseDefaultSpec, ApplicationContextAware {

	private final Logger log = LoggerFactory.getLogger(getClass());

	/**
	 * Si occupa di leggere i parametri di configurazione dalle varie sorgenti. Per ordine di priorità vedi
	 * README.md.<br/>
	 * 
	 * @return
	 */
	@Bean
	public CuseSetupConfiguration setupConfiguration() {
		log.debug("Defining bean {}", CuseSetupConfiguration.class.getSimpleName());
		return new CuseSetupConfiguration();
	}

	/**
	 * Questa factory facilita la costruzione degli {@link YamlSupport}, inoltre implementa una cache che evita di
	 * ricostruire + volte lo stesso oggetto.
	 * 
	 * @param config
	 * @return
	 */
	@Autowired
	@Bean
	public YamlSupportFactory yamlSupportFactory(CuseSetupConfiguration config) {
		log.debug("Defining bean {}", YamlSupportFactory.class.getSimpleName());
		return new YamlSupportFactory(config);
	}

	/**
	 * Utility principale della libreria
	 * 
	 * @param config
	 * @param yamlSupportFactory
	 * @return
	 */
	@Bean
	@Autowired
	public CuseUtil cuseUtil(CuseSetupConfiguration config, YamlSupportFactory yamlSupportFactory) {
		log.debug("Defining bean {}", CuseUtil.class.getSimpleName());
		return new CuseUtil(config, yamlSupportFactory);
	}

	// /**
	// * {@link YamlSupport} per i dati dell'ambiente: test, collaudo, ecc..
	// *
	// * @param config
	// * @param yamlSupportFactory
	// * @return
	// */
	// @Autowired
	// @Bean(name = "targetEnvYaml")
	// public YamlSupport yamlSupportEnv(CuseSetupConfiguration config, YamlSupportFactory yamlSupportFactory) {
	// log.debug("Defining bean {}({})", YamlSupport.class.getSimpleName(), config.targetEnv());
	// return yamlSupportFactory.build(config.targetEnv());
	// }
	//
	// /**
	// * {@link YamlSupport} comune per i dati trasversali agli ambienti (es. i gli hook della UI)
	// *
	// * @param yamlSupportFactory
	// * @return
	// */
	// @Autowired
	// @Bean(name = "commonYaml")
	// public YamlSupport yamlSupportCommon(YamlSupportFactory yamlSupportFactory) {
	// log.debug("Defining bean {}({})", YamlSupport.class.getSimpleName(),
	// CuseDefaultSpec.YAML_SUPPORT_COMMON_FILENAME);
	// return yamlSupportFactory.build(CuseDefaultSpec.YAML_SUPPORT_COMMON_FILENAME);
	// }

	/**
	 * Property placeholder stabilisce la priorità dei file di properties e la logica di override delle properties. In
	 * questo caso le System Properties sovrascrivono le properties dei property file.
	 * <br/>
	 * <ul>
	 * <li>
	 * Il {@code cuse.properties} (required) viene aggiunto al classpath del progetto di test che utilizza la
	 * commonlib, e contiene le properties custom del progetto e va a sovrascrivere le properties di default.
	 * </li>
	 * <li>
	 * Il property file (opzional) localizzato dalla System Property {@link CuseDefaultSpec#USE_PROPERTY_FILE} viene
	 * dato in pasto a runtime alla esecuzione dei test e sovrascrive tutte le altre properties. E' equivalente a
	 * settare le System Properties in riga di comando.
	 * </li>
	 * </ul>
	 * 
	 * @see CuseDefaultSpec#CUSE_PROPERTY_FILE
	 * @see CuseDefaultSpec#USE_PROPERTY_FILE
	 * @return
	 * @throws IOException
	 */
	@Bean
	public PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() throws IOException {
		log.debug("Defining bean {}", PropertyPlaceholderConfigurer.class.getSimpleName());
		PropertyPlaceholderConfigurer props = new PropertyPlaceholderConfigurer();
		props.setIgnoreResourceNotFound(true);
		props.setIgnoreUnresolvablePlaceholders(true);
		props.setSearchSystemEnvironment(true);
		props.setSystemPropertiesMode(PropertyPlaceholderConfigurer.SYSTEM_PROPERTIES_MODE_OVERRIDE);
		props.setOrder(100);
		List<Resource> locations = new ArrayList<>();
		locations.add(new ClassPathResource(CUSE_PROPERTY_FILE));
		String use_properties = System.getProperty(USE_PROPERTY_FILE);
		if (StringUtils.hasText(use_properties)) {
			log.info("Using properties: {}", use_properties);
			locations.add(new FileSystemResource(use_properties));
		}
		props.setLocations(locations.toArray(new Resource[] {}));
		return props;
	}

	// @Autowired
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		log.debug("Adding property source {}", CustomPropertySource.class.getSimpleName());
		((AbstractApplicationContext) applicationContext).getEnvironment().getPropertySources().addLast(new CustomPropertySource());
	}

}
