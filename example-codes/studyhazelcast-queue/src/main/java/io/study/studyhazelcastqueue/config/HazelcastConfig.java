package io.study.studyhazelcastqueue.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.QueueConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

@Configuration
public class HazelcastConfig {

	@Bean(name = "hazelcastInstance")
	public HazelcastInstance hazelcastInstance(
		@Qualifier("hazelcastBufferConfig") Config config
	){
		return Hazelcast.newHazelcastInstance(config);
	}

	@Bean(name = "hazelcastBufferConfig")
	public Config hazelcastBufferConfig(
		@Qualifier("hazelcastQueueConfig") QueueConfig queueConfig
	){
		Config config = new Config();
		config.setInstanceName("hazelcast-test")
			.addQueueConfig(queueConfig);

		return config;
	}

	@Bean(name = "hazelcastQueueConfig")
	public QueueConfig hazelcastQueueConfig(){
		return new QueueConfig()
			.setName("hazelcastQueueConfig")
			.setMaxSize(1000);
	}
}
