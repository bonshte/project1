package com.trading212.project1.repositories;
import com.trading212.project1.core.models.AdStub;
import com.trading212.project1.core.models.scraping.AccommodationType;
import io.milvus.client.MilvusServiceClient;
import io.milvus.grpc.*;
import io.milvus.param.*;
import io.milvus.param.collection.*;
import io.milvus.param.control.ManualCompactParam;
import io.milvus.param.dml.DeleteParam;
import io.milvus.param.dml.InsertParam;
import io.milvus.param.dml.QueryParam;
import io.milvus.param.dml.SearchParam;
import io.milvus.param.index.CreateIndexParam;
import io.milvus.param.index.DropIndexParam;
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.param.partition.ReleasePartitionsParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Repository
public class MilvusPurchaseAdsRepository implements VectorAdsRepository {

    private final MilvusServiceClient milvusClient;
    private static final String COLLECTION_NAME = "PURCHASE_AD_COLLECTION";
    private static final Integer EMBED_DIM = 1536;
    private static final IndexType INDEX_TYPE = IndexType.IVF_FLAT;
    private static final String INDEX_PARAM = "{\"nlist\":128}";
    private static final String INDEX_NAME = "embeddingIndex";
    private static final String SEARCH_PARAM = "{\"nprobe\":16}";
    private static final int VARCHAR_DEFAULT_LENGTH = 255;
    private static final String SOFIA_PARTITION = "sofia";
    private static final String PROVINCE_PARTITION = "province";
    public MilvusPurchaseAdsRepository(MilvusServiceClient serviceClient) {
        this.milvusClient = serviceClient;
    }

    private void handleResponseStatus(R<?> r) {
        if (r.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException(r.getMessage());
        }
    }

    public void createCollection(long timeout) {
        FieldType embeddingFieldType = FieldType.newBuilder()
                .withName(EMBEDDING_FIELD)
                .withDataType(DataType.FloatVector)
                .withDimension(EMBED_DIM)
                .build();

        FieldType townFieldType = FieldType.newBuilder()
                .withName(TOWN_FIELD)
                .withDataType(DataType.VarChar)
                .withMaxLength(VARCHAR_DEFAULT_LENGTH)
                .build();

        FieldType neighbourhoodFieldType = FieldType.newBuilder()
                .withName(NEIGHBOURHOOD_FIELD)
                .withDataType(DataType.VarChar)
                .withMaxLength(VARCHAR_DEFAULT_LENGTH)
                .build();


        FieldType priceFieldType = FieldType.newBuilder()
                .withName(PRICE_FIELD)
                .withDataType(DataType.Int32)
                .build();

        FieldType accommodationTypeFieldType = FieldType.newBuilder()
                .withName(ACCOMMODATION_TYPE_FIELD)
                .withDataType(DataType.VarChar)
                .withMaxLength(VARCHAR_DEFAULT_LENGTH)
                .build();

        FieldType idFieldType = FieldType.newBuilder()
                .withName(ID_FIELD)
                .withDataType(DataType.Int64)
                .withPrimaryKey(true)
                .build();

        CreateCollectionParam createCollectionReq = CreateCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .addFieldType(embeddingFieldType)
                .addFieldType(townFieldType)
                .addFieldType(neighbourhoodFieldType)
                .addFieldType(priceFieldType)
                .addFieldType(accommodationTypeFieldType)
                .addFieldType(idFieldType)
                .build();

        R<RpcStatus> response = milvusClient.withTimeout(timeout, TimeUnit.MILLISECONDS)
                .createCollection(createCollectionReq);
        handleResponseStatus(response);
        System.out.println(response);
    }

    public void dropCollection() {
        R<RpcStatus> response = milvusClient.dropCollection(DropCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());
        handleResponseStatus(response);
    }

    public void loadCollection() {
        R<RpcStatus> response = milvusClient.loadCollection(LoadCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());
        handleResponseStatus(response);
    }

    public void createPartition(String partitionName) {
        R<RpcStatus> response = milvusClient.createPartition(CreatePartitionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withPartitionName(partitionName)
                .build());
        handleResponseStatus(response);
    }
    //this is to refactor the clusters after deletions in memory
    public void compact() {
        R<ManualCompactionResponse> response = milvusClient.manualCompact(ManualCompactParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());
        handleResponseStatus(response);
    }

    public void insertProperties(String partitionName, List<List<Float>> embeddings, List<AdStub> ads) {

        List<InsertParam.Field> fields = new ArrayList<>();
        List<Integer> prices = new LinkedList<>();
        List<String> towns = new LinkedList<>();
        List<String> neighbourhoods = new LinkedList<>();
        List<Long> ids = new LinkedList<>();
        List<String> accommodationTypes = new LinkedList<>();

        for (var ad : ads) {
            prices.add(ad.getPrice());
            towns.add(ad.getTown());
            neighbourhoods.add(ad.getNeighbourhood());
            ids.add(ad.getId());
            accommodationTypes.add(ad.getAccommodationType().toString());
        }

        fields.add(new InsertParam.Field(ACCOMMODATION_TYPE_FIELD, accommodationTypes));
        fields.add(new InsertParam.Field(ID_FIELD, ids));
        fields.add(new InsertParam.Field(TOWN_FIELD, towns));
        fields.add(new InsertParam.Field(NEIGHBOURHOOD_FIELD, neighbourhoods));
        fields.add(new InsertParam.Field(PRICE_FIELD, prices));
        fields.add(new InsertParam.Field(EMBEDDING_FIELD, embeddings));


        InsertParam insertParam = InsertParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withPartitionName(partitionName)
                .withFields(fields)
                .build();

        R<MutationResult> response = milvusClient.insert(insertParam);
        handleResponseStatus(response);
    }

    private R<SearchResults> searchProperty(List<Float> searchEmbedding, String expr) {
        List<String> outFields = List.of(ID_FIELD);
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withMetricType(MetricType.L2)
                .withOutFields(outFields)
                .withTopK(2)
                .withVectors(List.of(searchEmbedding))
                .withVectorFieldName(EMBEDDING_FIELD)
                .withExpr(expr)
                .withParams(SEARCH_PARAM)
                .build();

        R<SearchResults> response = milvusClient.search(searchParam);
        handleResponseStatus(response);
        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());


        List<SearchResultsWrapper.IDScore> scores = wrapper.getIDScore(0);
        System.out.println(scores);

        System.out.println(wrapper.getFieldData(ID_FIELD, 0));
        return response;
    }

    //fixed?
    public void createIndex(String fieldName, String indexName, IndexType indexType,
                            MetricType metricType, String indexParam) {
        R<RpcStatus> response = milvusClient.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFieldName(fieldName)
                .withIndexName(indexName)
                .withIndexType(indexType)
                .withMetricType(metricType) //try and euclid to check which works better
                .withExtraParam(indexParam)
                .withSyncMode(Boolean.TRUE)
                .build());
        handleResponseStatus(response);
    }

    public void releasePartition(String partitionName) {
        R<RpcStatus> response = milvusClient.releasePartitions(ReleasePartitionsParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .addPartitionName(partitionName)
                .build());
        handleResponseStatus(response);
    }

    //fix
    public void delete(String partitionName, String expr) {
        DeleteParam build = DeleteParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withPartitionName(partitionName)
                .withExpr(expr)
                .build();
        R<MutationResult> response = milvusClient.delete(build);
        handleResponseStatus(response);
    }

    public void dropIndex(String indexName) {
        R<RpcStatus> response = milvusClient.dropIndex(DropIndexParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withIndexName(indexName)
                .build());
        handleResponseStatus(response);
    }

    public void releaseCollection() {
        R<RpcStatus> response = milvusClient.releaseCollection(ReleaseCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());
        handleResponseStatus(response);
    }

    private void flushToCollection() {
        milvusClient.flush(FlushParam.newBuilder().addCollectionName(COLLECTION_NAME).build());
    }

    private boolean hasCollection() {

        R<Boolean> response = milvusClient.hasCollection(HasCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());
        handleResponseStatus(response);
        return response.getData().booleanValue();
    }

    private R<QueryResults> query(String expr) {
        System.out.println();
        List<String> fields = Arrays.asList(ID_FIELD, EMBEDDING_FIELD, PRICE_FIELD);
        QueryParam test = QueryParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withExpr(expr)
                .withOutFields(fields)
                .build();
        R<QueryResults> response = milvusClient.query(test);
        handleResponseStatus(response);
        QueryResultsWrapper wrapper = new QueryResultsWrapper(response.getData());
        System.out.println(ID_FIELD + ":" + wrapper.getFieldWrapper(ID_FIELD).getFieldData().toString());
        System.out.println(EMBEDDING_FIELD + ":" + wrapper.getFieldWrapper(EMBEDDING_FIELD).getFieldData().toString());
        System.out.println(PRICE_FIELD + ":" + wrapper.getFieldWrapper(PRICE_FIELD).getFieldData().toString());
        System.out.println("Query row count: " + wrapper.getFieldWrapper(ID_FIELD).getRowCount());
        return response;
    }
}
