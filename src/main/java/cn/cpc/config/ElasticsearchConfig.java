package cn.cpc.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import cn.cpc.commons.ESClientSpringFactory;

@Configuration
@ComponentScan(basePackageClasses = ESClientSpringFactory.class)
public class ElasticsearchConfig {
	@Value("${elasticSearch.host}")
	private String host;

	@Value("${elasticSearch.port}")
	private int port;

	@Value("${elasticSearch.client.connectNum}")
	private Integer connectNum;

	@Value("${elasticSearch.client.connectPerRoute}")
	private Integer connectPerRoute;

	@Bean
	public HttpHost httpHost() {
		return new HttpHost(host, port, "http");
	}

	@Bean(initMethod = "init", destroyMethod = "close")
	public ESClientSpringFactory getFactory() {
		return ESClientSpringFactory.build(httpHost(), connectNum, connectPerRoute);
	}

	@Bean
	@Scope("singleton")
	public RestClient getRestClient() {
		return getFactory().getClient();
	}

	@Bean
	@Scope("singleton")
	public RestHighLevelClient getRHLClient() {
		return getFactory().getRhlClient();
	}

	@Override
	public String toString() {
		return "ElasticsearchConfig [host=" + host + ", port=" + port + ", connectNum=" + connectNum
				+ ", connectPerRoute=" + connectPerRoute + "]";
	}

}
