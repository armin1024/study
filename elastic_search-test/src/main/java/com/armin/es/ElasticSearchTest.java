package com.armin.es;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.index.query.WildcardQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * 全文检索
 *  ElasticSearch 基于 lucene
 */
public class ElasticSearchTest {
    // 获取客户端方法
    public TransportClient getClient() {
        try {
            return new PreBuiltTransportClient(Settings.EMPTY)
                    .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 创建索引（可做覆盖更新，将覆盖原来的值）
     */
    @Test
    public void testCreateIndex() {
        // 获取 es 客户端
        TransportClient client = getClient();
        // 创建索引库、指定类型、id
        IndexRequestBuilder builder = client.prepareIndex("armin", "xss", "2");
        // 准备索引数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "Jack");
        data.put("age", 24);
        data.put("sex", "female");
        data.put("work_id", "0927");
        // 数据创建 并 返回创建的结果
        IndexResponse response = builder.setSource(data).get();
        System.out.println(response);
    }

    /**
     * 查询索引
     */
    @Test
    public void testGetIndex() {
        // 获取 es 客户端
        TransportClient client = getClient();
        // 创建被查询的索引对象
        GetRequestBuilder builder = client.prepareGet("armin", "xss", "1");
        // 获取到查询结果的对象
        GetResponse response = builder.get();
        // 从结果对象中的数据源中打印数据
        System.out.println(response.getSource());
    }

    /**
     * 修改索引（增量更新，在原有基础上更新所给数据）
     */
    @Test
    public void testUpdateIndex() {
        // 获取 es 客户端
        TransportClient client = getClient();
        // 创建被更新的索引对象
        UpdateRequestBuilder builder = client.prepareUpdate("armin", "xss", "1");
        // 准备更新的数据
        HashMap<String, Object> data = new HashMap<>();
        data.put("name", "Rose");
        data.put("sex", "female");
        data.put("sale", 17999);
        // 更新索引 并 返回更新结果
        UpdateResponse response = builder.setDoc(data).get();
        System.out.println(response);
    }

    /**
     * 删除索引
     */
    @Test
    public void testDeleteIndex() {
        // 获取 es 客户端
        TransportClient client = getClient();
        // 创建被删除索引的对象
        DeleteRequestBuilder builder = client.prepareDelete("armin", "xss", "2");
        // 执行删除 并 返回响应结果
        DeleteResponse response = builder.get();
        System.out.println(response);
    }
    
    /**
     * 批量添加
     */
    @Test
    public void testBulkIndex() {
        // 获取 es 客户端
        TransportClient client = getClient();
        // 创建批量操作对象
        BulkRequestBuilder builder = client.prepareBulk();
        // 批量操作
        for (int i = 0; i < 10; i++) {
            // 准备数据
            HashMap<String, Object> data = new HashMap<>();
            if (i%2 == 0) {
                data.put("name", "java_" + i);
                data.put("sex", "female");
                data.put("age", 18 + i);
            } else {
                data.put("name", "python_" + i);
                data.put("sex", "male");
                data.put("age", 18 + i);
            }
            // 将数据添加到指定索引
            builder.add(client.prepareIndex("armin", "xss", String.valueOf(i)).setSource(data));
        }
        // 数据添加 并 返回操作结果
        BulkResponse responses = builder.get();
        System.out.println(responses.buildFailureMessage());
    }

    /**
     * 组合查询
     *  查询所有的java 分页 取一页 每页2条 按照年龄升序排列  年龄大于20  小于28
     */
    @Test
    public void testSearch() {
        TransportClient client = getClient();
        // 获取查询对象
        SearchRequestBuilder search = client.prepareSearch("armin");
        // 设置查询类型
        search.setTypes("xss");
        // 开始索引位（类似limit第一个参数）
        search.setFrom(0);
        // 查询页面大小
        search.setSize(2);
        // 根据属性排序
        search.addSort("age", SortOrder.ASC);
        // 组合条件
        BoolQueryBuilder builder = new BoolQueryBuilder();
        builder.must(new WildcardQueryBuilder("name", "java*"));
        builder.filter(new RangeQueryBuilder("age").gte(18).lte(25));
        SearchRequestBuilder searchRequestBuilder = search.setQuery(builder);
        // 执行查询 并 返回结果
        SearchResponse response = searchRequestBuilder.get();
        SearchHits hits = response.getHits();
        for (SearchHit hit : hits) {
            System.out.println(hit.getSource());
        }
    }
}
