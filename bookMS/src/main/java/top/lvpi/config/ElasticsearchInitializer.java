package top.lvpi.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
public class ElasticsearchInitializer implements CommandLineRunner {

    private static final String BOOKS_INDEX = "books";
//    private static final String TOPICS_INDEX = "topics";
    private static final int MAX_RETRIES = 30;
    private static final long RETRY_DELAY = 2000; // 2 seconds

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private final ElasticsearchOperations elasticsearchOperations;
    private final ResourceLoader resourceLoader;

    public ElasticsearchInitializer(ElasticsearchOperations elasticsearchOperations, ResourceLoader resourceLoader) {
        this.elasticsearchOperations = elasticsearchOperations;
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(String... args) {
        initializeIndex(BOOKS_INDEX, "es/books-mapping.json");
//        initializeIndex(TOPICS_INDEX, "es/topics-mapping.json");
    }

    private void initializeIndex(String indexName, String mappingFile) {
        int retryCount = 0;
        while (retryCount < MAX_RETRIES) {
            try {
                // 检查索引是否存在
                boolean indexExists = elasticsearchClient.indices()
                    .exists(ExistsRequest.of(e -> e.index(indexName)))
                    .value();

                if (indexExists) {
                    log.info("Index {} already exists, skipping initialization", indexName);
                    return;
                }

                // 读取映射文件
                Resource resource = resourceLoader.getResource("classpath:" + mappingFile);
                String mappingJson = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                
                // 创建索引
                CreateIndexResponse response = elasticsearchClient.indices()
                    .create(c -> c
                        .index(indexName)
                        .withJson(new java.io.StringReader(mappingJson))
                    );

                if (response.acknowledged()) {
                    log.info("Successfully created index {}", indexName);
                    return;
                } else {
                    log.error("Failed to create index {}", indexName);
                }
            } catch (Exception e) {
                retryCount++;
                if (retryCount < MAX_RETRIES) {
                    log.warn("Failed to initialize Elasticsearch index {}, retrying in 2 seconds... (attempt {}/{})", 
                        indexName, retryCount, MAX_RETRIES, e);
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    log.error("Failed to initialize Elasticsearch index {} after {} attempts", 
                        indexName, MAX_RETRIES, e);
                    throw new RuntimeException("Failed to initialize Elasticsearch index", e);
                }
            }
        }
    }
} 