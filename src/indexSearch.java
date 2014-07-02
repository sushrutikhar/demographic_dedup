import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.exists.indices.IndicesExistsRequest;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequestBuilder;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.IndicesAdminClient;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;

public class indexSearch {

	public  Client createClient(String clusterName,String hostName,int port)
	{
		 Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", clusterName).build();
		 TransportClient transportClient = new TransportClient(settings);
		 Client client = transportClient.addTransportAddress(new InetSocketTransportAddress(hostName, port));
		 return client;
	}
	
	public void createIndex(Client client,String indexName) throws Exception {
		IndicesAdminClient indices = client.admin().indices();

        if (indices.exists(new IndicesExistsRequest(indexName)).actionGet().isExists()) {
        	System.err.println("\nIndex :"+indexName+" already exists ...");
            return;
        }
 
        Settings settings = ImmutableSettings.settingsBuilder().loadFromSource(jsonBuilder()
                .startObject()
                    .startObject("analysis")
                    	.startObject("analyzer")
                            .startObject("test_analyzer")
                                .field("type", "custom")
                                .field("tokenizer", "standard")
                                .field("filter", new String[]{"lowercase","my_metaphone"})
                            .endObject()
                       .endObject()
                    
                        .startObject("filter")
                            .startObject("my_metaphone")
                            	.field("type","phonetic")
                            	.field("encoder","doublemetaphone")
                            	.field("replace","false")
                            .endObject()
                       .endObject()
                       
                    .endObject()
                .endObject().string()).build();
        
        CreateIndexRequestBuilder createIndexRequestBuilder = indices.prepareCreate(indexName);
        createIndexRequestBuilder.setSettings(settings);
        createIndexRequestBuilder.execute().actionGet();
        client.admin().indices().prepareRefresh().execute().actionGet();
        System.err.println("Index :"+indexName+" is created successfully");
	}
	
	public void addDoc(Client client,String indexName,String docType,String docId,UserDTO user) throws IOException
	{
		IndexRequestBuilder indexRequestBuilder = client.prepareIndex(indexName,docType);//,docId);
		XContentBuilder contentBuilder;
		contentBuilder = jsonBuilder().startObject().prettyPrint();
		
		contentBuilder.field("salutation", user.getSalutation());
		contentBuilder.field("abbrevation", user.getAbbrevation());
		contentBuilder.field("initial", user.getInitial());
		contentBuilder.field("firstName", user.getFirstName());
		contentBuilder.field("lastName", user.getLastName());
		
		contentBuilder.field("gender", user.getGender());
		contentBuilder.field("age", user.getAge());
		
		contentBuilder.field("houseNumber", user.getHouseNumber());
		contentBuilder.field("houseName", user.getHouseName());
		contentBuilder.field("street", user.getStreet());
		contentBuilder.field("village", user.getVillage());
		contentBuilder.field("subDistrict", user.getSubDistrict());
		contentBuilder.field("district", user.getDistrict());
		contentBuilder.field("state", user.getState());
		contentBuilder.field("postCode", user.getPostCode());
		
		contentBuilder.endObject();
		indexRequestBuilder.setSource(contentBuilder);
		IndexResponse response = indexRequestBuilder.execute().actionGet();	 
		//return response;  
	}
	
	public void addDoc(Client client,String indexName,String docType,ArrayList<UserDTO> userList) throws Exception{
		long numberDoc = getNumberDocs(client,indexName,docType);
		client.admin().indices().prepareRefresh().execute().actionGet();
		for(UserDTO user : userList){
			addDoc(client,indexName,docType,Long.toString(numberDoc+1),user);
			numberDoc++;
		}
		client.admin().indices().prepareRefresh().execute().actionGet();
		System.err.println("Documents added successfully");
	}
	
	public SearchResponse searchDoc(Client client,String indexName,String docType,UserDTO user){
		
		System.out.println("\nSearching for User:"+user.toString()+"\n");
		SearchResponse response = client.prepareSearch(indexName)
				.setTypes(docType)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.boolQuery()
						.must(QueryBuilders.disMaxQuery()
								.add(QueryBuilders.rangeQuery("age").from(user.getAge()-1).to(user.getAge()+1))
								.add(QueryBuilders.termQuery("age", -1)
								)
							)
						.must(QueryBuilders.disMaxQuery()
								.add(QueryBuilders.termQuery("gender", user.getGender()))
								.add(QueryBuilders.termQuery("gender", -1)
								)
							)
						.should(
							QueryBuilders.disMaxQuery()	
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("salutation").likeText(user.getSalutation()))
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("abbrevation").likeText(user.getAbbrevation()))
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("initial").likeText(user.getInitial()))
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("firstName").likeText(user.getFirstName()))
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("lastName").likeText(user.getLastName()))
								
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("houseNumber").likeText(user.getHouseNumber()))
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("houseName").likeText(user.getHouseName()))
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("street").likeText(user.getStreet()))
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("village").likeText(user.getVillage()))
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("subDistrict").likeText(user.getSubDistrict()))
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("district").likeText(user.getDistrict()))
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("state").likeText(user.getState()))
								.add(QueryBuilders.fuzzyLikeThisFieldQuery("postCode").likeText(user.getPostCode()))
								
								.boost(1.2f)
								.tieBreaker(0.7f)
								)
							)
				.setFrom(0)
				.setSize(100)
				.setScroll(TimeValue.timeValueMinutes(2))
				.execute()
				.actionGet();
		SearchHit[] results = response.getHits().getHits();
		System.err.println("Current results: " + results.length);
		for (SearchHit hit : results) {
			System.out.println("------------------------------");
		Map<String,Object> result = hit.getSource();
		String docId = hit.getId();
		System.out.println(hit.getScore()+"\t"+docId+"\t"+result);
		}
		/*
		while (true) {
		    response = client.prepareSearchScroll(
		    		response.getScrollId()
		    		).setScroll(new TimeValue(600000)).execute().actionGet();
		    for (SearchHit hit : response.getHits()) {
		        //Handle the hit...
		    	System.out.println("------------------------------");
				Map<String,Object> result = hit.getSource();   
				System.out.println(hit.getScore()+"\t"+result);
		    }
		    //Break condition: No hits are returned
		    if (response.getHits().getHits().length == 0) {
		        break;
		    }
		}
		*/
		return response;
	}
	
	/*
	public  void searchDocument(Client client, String indexName, String docType, String field, String value){
		SearchResponse response= client.prepareSearch(indexName)
				.setTypes(docType)
				.setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
				.setQuery(QueryBuilders.fuzzyLikeThisQuery(field).likeText(value))//.fuzzyQuery("name", "jai"))//.matchAllQuery())
				//.setQuery(QueryBuilders.termQuery("name", "ljai"))
				.setExplain(true)
				.execute()
				.actionGet();

		
		SearchHit[] results = response.getHits().getHits();
		
		System.out.println("Current results: " + results.length);
		for (SearchHit hit : results) {
			System.out.println("------------------------------");
		Map<String,Object> result = hit.getSource();   
		System.out.println(result+"\t"+hit.getScore());
		}
		
	}
	*/
	
	public  void updateDocument(Client client, String index, String type, String id, String field, String newValue){	
		Map<String, Object> updateObject = new HashMap<String, Object>();
		updateObject.put(field, newValue);
		
		client.prepareUpdate(index, type, id)
		.setScript("ctx._source." + field + "=" + field)
		.setScriptParams(updateObject).execute().actionGet();
	}	
	
	public void deleteDocument(Client client, String index, String type, String id){
        
        DeleteResponse response = client.prepareDelete(index, type, id).execute().actionGet();
        System.err.println("Information on the deleted document:");
        System.err.println("Index: " + response.getIndex());
        System.err.println("Type: " + response.getType());
        System.err.println("Id: " + response.getId());
        System.err.println("Version: " + response.getVersion());
    }
	
	public static GetResponse getDoc(Client client,String indexName,String docType,String docId)
	{
		GetRequestBuilder getRequestBuilder = client.prepareGet(indexName,docType,docId);
		GetResponse response = getRequestBuilder.execute().actionGet();
		return response;
	}
	
	public static GetResponse getDoc(Client client,String indexName,String docType,String docId,String[] fields)
	{
		GetRequestBuilder getRequestBuilder = client.prepareGet(indexName,docType,docId);
		getRequestBuilder.setFields(fields);
		GetResponse response = getRequestBuilder.execute().actionGet();
		return response;
	}
	
	public long getNumberDocs(Client client,String indexName,String docType)
	{
		CountResponse response = client.prepareCount(indexName)
				.setTypes(docType)
		        .setQuery(QueryBuilders.matchAllQuery())
		        .execute()
		        .actionGet();
		return response.getCount();
	}
	
	public void deleteIndex(Client client,String indexName){
		IndicesAdminClient indices = client.admin().indices();
		
		if (indices.exists(new IndicesExistsRequest(indexName)).actionGet().isExists()) {
			System.err.println("\nDeleting the index :"+indexName+" ...");
			indices.delete(new DeleteIndexRequest(indexName)).actionGet();
		}
	}
}
