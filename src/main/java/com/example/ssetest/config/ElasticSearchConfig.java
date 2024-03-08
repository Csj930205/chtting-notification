package com.example.ssetest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

/**
 * @author sjChoi
 * @since 2/6/24
 */
@Configuration
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.url}")
    private String elasticSearchUrl;

    /**
     * Spring boot 및 Spring 버전이 업그레이드 됨에 따라 Elasticsearch 설정변경
     * @return
     */
    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticSearchUrl)
                .build();
    }

//
//    /**
//     * Spring Boot에서 ElasticsearchClientConfigurations를 자동 설정하는 도중, void co.elastic.clients.transport.rest_client.RestClientTransport.<init>(org.elasticsearch.client.RestClient, co.elastic.clients.json.JsonpMapper, co.elastic.clients.transport.TransportOptions) 메소드가 2곳에서 호출되어 충돌을 일으킨다.
//     * 한 곳은 spring boot과 한 곳은 elastic client인데 둘 다 사용해야하는 사람들에게는 머리아픈 얘기가 될 수 밖에 없다.
//     * 거기서 해결책을 위에 링크 중 GitHub issue에서 발견할 수 있는데 아예 사용자가 해당 생성자를 새로 정의하여 우선권을 가져가, 충돌을 막아버리는 것이다.
//     * @param restClient
//     * @param restClientOptions
//     * @return
//     */
//    @Bean
//    RestClientTransport restClientTransport(RestClient restClient, ObjectProvider<RestClientOptions> restClientOptions) {
//        return new RestClientTransport(restClient, new JacksonJsonpMapper(), restClientOptions.getIfAvailable());
//    }
}
