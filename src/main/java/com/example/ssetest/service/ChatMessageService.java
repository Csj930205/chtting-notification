package com.example.ssetest.service;

import com.example.ssetest.domain.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**1
 * @author sjChoi
 * @since 2/19/24
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ElasticsearchOperations elasticsearchOperations;

    public List<ChatMessage> getAllChatMessage(String roomUid, Map<String, Object> paramMap) {
        List<ChatMessage> chatMessagesList = new ArrayList<>();
        Criteria criteria = new Criteria("roomUid").is(roomUid);

        CriteriaQuery query;
        if (paramMap.containsKey("paging") && paramMap.get("paging").equals("true")) {
            int page = Integer.parseInt(paramMap.get("page").toString());
            int size = Integer.parseInt(paramMap.get("size").toString());
            int from = page - 1;

            Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Direction.DESC, "timestamp"));
            query = new CriteriaQuery(criteria).setPageable(pageable);
        } else {
            query = new CriteriaQuery(criteria);
        }
        SearchHits<ChatMessage> searchHits = elasticsearchOperations.search(query, ChatMessage.class);
        searchHits.forEach(hit -> chatMessagesList.add(hit.getContent()));
        return chatMessagesList;

    }
}
