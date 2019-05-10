package cn.cpc.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.elasticsearch.action.admin.indices.get.GetIndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.cpc.config.ElasticsearchConfig;

@RestController
public class TestController {

	// 注入高级客户端
	@Autowired
	private RestHighLevelClient client;

	// 注入es的配置文件可以查看读取的是否正确
	@Autowired
	private ElasticsearchConfig esConfig;

	@GetMapping(value = "/test")
	public String test() throws Exception {
		// 查看配置
		System.out.println(esConfig);

		// 判断索引是否存在
		String index1 = "索引名";
		System.out.println(isIndexExists(index1));
		// 多条件搜索
		List<String> list = search();

		return list.toString();
	}

	// 判断es的index是否存在
	public boolean isIndexExists(String index) throws IOException {
		GetIndexRequest request = new GetIndexRequest();
		request.indices(index);
		request.local(false);
		request.humanReadable(true);
		return client.indices().exists(request, RequestOptions.DEFAULT);
	}

	public List<String> search() throws IOException {
		// 构建搜索
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// 查询条件很多,如果做搜索时有些选项可填可不填,可空克不空
		// 创建条件查询
		BoolQueryBuilder boolBuilder = QueryBuilders.boolQuery();
		// 条件一
		boolBuilder.must(QueryBuilders.matchPhraseQuery("字段1", "xx"));
		// 条件二
		boolBuilder.must(QueryBuilders.matchPhraseQuery("字段2", "xx"));
		// 带范围的
		// searchSourceBuilder.postFilter(QueryBuilders.rangeQuery("某个是数的字段").gte("gte大于等于").lte("lte小于等于"));
		// 排个序
		// searchSourceBuilder.sort("排序字段", SortOrder.DESC);
		// 分个页(from从第几条开始,size每页显示几条)
		// searchSourceBuilder.from(0).size(10);
		// 将条件添加进构造的搜索
		searchSourceBuilder.query(boolBuilder);
		// 查询所有(这条和上面这条只能生效一个,有条件就不能匹配所有)
		// searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		SearchRequest searchRequest = new SearchRequest();
		// 设置索引
		searchRequest.indices("logstash-apigatewaytracelogger-dev-2019.05.10");
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		SearchHits searchHits = searchResponse.getHits();
		System.out.println("查到数目:" + searchHits.getTotalHits());
		SearchHit[] hits = searchHits.getHits();
		ArrayList<String> list = new ArrayList<String>();
		for (SearchHit hit : hits) {
			String json = hit.getSourceAsString();
			list.add(json);
		}

		return list;
	}
}