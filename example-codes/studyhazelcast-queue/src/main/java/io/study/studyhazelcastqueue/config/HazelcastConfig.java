package io.study.studyhazelcastqueue.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.QueueConfig;

@Configuration
public class HazelcastConfig {

	@Bean
	public Config hazelCastConfig(){
		Config config = new Config();
		config.setInstanceName("hazelcast-test")
			.addQueueConfig(hazelcastQueueConfig());

		return config;
	}

	@Bean(name = "hazelcastQueueConfig")
	public QueueConfig hazelcastQueueConfig(){
		return new QueueConfig()
			.setName("hazelcastQueueConfig")
			.setMaxSize(1000);
	}
}
