package com.example.ssetest.service;

import com.example.ssetest.domain.Error;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author sjChoi
 * @since 3/18/24
 */
@Service
@RequiredArgsConstructor
public class ErrorElasticsearchService {
    private final ElasticsearchOperations elasticsearchOperations;

    @Transactional(readOnly = true)
    public List<Error> errorList(Map<String, Object> paramMap) {
        List<Error> errorList = new ArrayList<>();
        Criteria criteria;
        if (paramMap.containsKey("type")) {
            String type = paramMap.get("type").toString();
            criteria = new Criteria("message").contains(type);
        } else {
            criteria = new Criteria();
        }

        CriteriaQuery query;
        if (paramMap.containsKey("size")) {
            int page = Integer.parseInt(paramMap.get("page").toString());
            int size = Integer.parseInt(paramMap.get("size").toString());
            int from = page - 1;
            Pageable pageable = PageRequest.of(from, size);
            query = new CriteriaQuery(criteria).setPageable(pageable);
        } else {
            query = new CriteriaQuery(criteria);
        }

        SearchHits<Error> searchError = elasticsearchOperations.search(query, Error.class);
        searchError.forEach(error -> errorList.add(error.getContent()));
        return errorList;
    }
}
