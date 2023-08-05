package com.trading212.project1.repositories.milvus;

import com.trading212.project1.core.models.Ad;
import com.trading212.project1.repositories.EmbeddingAdsRepository;
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
import io.milvus.param.partition.CreatePartitionParam;
import io.milvus.param.partition.DropPartitionParam;
import io.milvus.param.partition.HasPartitionParam;
import io.milvus.param.partition.ReleasePartitionsParam;
import io.milvus.response.QueryResultsWrapper;
import io.milvus.response.SearchResultsWrapper;
import org.springframework.stereotype.Repository;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Repository
public class MilvusAdsRepository implements EmbeddingAdsRepository {
    private final MilvusServiceClient milvusClient;
    private static final String ID_FIELD = "ad_id";
    private static final String EMBEDDING_FIELD = "property_embedding";
    private static final String TOWN_FIELD = "town";
    private static final String NEIGHBOURHOOD_FIELD = "neighbourhood";
    private static final String PRICE_FIELD = "price";
    private static final String ACCOMMODATION_TYPE_FIELD = "accommodation_type";
    private static final String COLLECTION_NAME = "ADS";
    private static final Integer EMBED_DIMENSIONS = 1536;
    private static final IndexType INDEX_TYPE = IndexType.IVF_FLAT;
    private static final String INDEX_PARAM = "{\"nlist\":128}";
    private static final String INDEX_NAME = "embedding_index";
    private static final String SEARCH_PARAM = "{\"nprobe\":16}";
    private static final int VARCHAR_DEFAULT_LENGTH = 255;
    public static final String RENT_PARTITION = "rent_ads";
    public static final String SALE_PARTITION = "sale_ads";
    private static final int TIMEOUT = 3000;

    public MilvusAdsRepository(MilvusServiceClient serviceClient) {
        this.milvusClient = serviceClient;
    }

    public void query() {
        List<String> fields = Arrays.asList(ID_FIELD, TOWN_FIELD, NEIGHBOURHOOD_FIELD, PRICE_FIELD, ACCOMMODATION_TYPE_FIELD);
        QueryParam test = QueryParam.newBuilder()
            .withCollectionName(COLLECTION_NAME)
            .withOutFields(fields)
            .withExpr(PRICE_FIELD + "> 50")
            .build();
        R<QueryResults> response = milvusClient.query(test);
        handleResponseStatus(response);
        QueryResultsWrapper wrapper = new QueryResultsWrapper(response.getData());
        System.out.println(ID_FIELD + ":" + wrapper.getFieldWrapper(ID_FIELD).getFieldData().toString());
        System.out.println(TOWN_FIELD + ":" + wrapper.getFieldWrapper(TOWN_FIELD).getFieldData().toString());
        System.out.println(NEIGHBOURHOOD_FIELD + ":" + wrapper.getFieldWrapper(NEIGHBOURHOOD_FIELD).getFieldData().toString());
        System.out.println(PRICE_FIELD + ":" + wrapper.getFieldWrapper(PRICE_FIELD).getFieldData().toString());
        System.out.println(ACCOMMODATION_TYPE_FIELD + ":" + wrapper.getFieldWrapper(ACCOMMODATION_TYPE_FIELD).getFieldData().toString());
        System.out.println("Query row count: " + wrapper.getFieldWrapper(ID_FIELD).getRowCount());
    }

    public void setUp() {
        if (!hasCollection()) {
            createCollection(TIMEOUT);
            createIndex();
        }

        if (!hasPartition(RENT_PARTITION)) {
            createPartition(RENT_PARTITION);
        }
        if (!hasPartition(SALE_PARTITION)) {
            createPartition(SALE_PARTITION);
        }

        loadCollection();
    }

    public void createCollection(long timeout) {
        FieldType embeddingFieldType = FieldType.newBuilder()
                .withName(EMBEDDING_FIELD)
                .withDataType(DataType.FloatVector)
                .withDimension(EMBED_DIMENSIONS)
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
    }


    public void createIndex() {
        R<RpcStatus> response = milvusClient.createIndex(CreateIndexParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withFieldName(EMBEDDING_FIELD)
                .withIndexName(INDEX_NAME)
                .withIndexType(INDEX_TYPE)
                .withMetricType(MetricType.L2)
                .withExtraParam(INDEX_PARAM)
                .withSyncMode(Boolean.TRUE)
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

    public void compact() {
        R<ManualCompactionResponse> response = milvusClient.manualCompact(ManualCompactParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());
        handleResponseStatus(response);
    }

    public void releaseCollection() {
        R<RpcStatus> response = milvusClient.releaseCollection(ReleaseCollectionParam.newBuilder()
            .withCollectionName(COLLECTION_NAME)
            .build());
        handleResponseStatus(response);
    }

    public void deleteAds(String partitionName, List<Long> adIds) {

        if (adIds.isEmpty()) {
            System.out.println("return with empty list from deleteAds");
            return;
        }
        String expr = ID_FIELD + " in [" +
            adIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", ")) +
            "]";
        System.out.println(expr);

        DeleteParam deleteParam = DeleteParam.newBuilder()
            .withCollectionName(COLLECTION_NAME)
            .withPartitionName(partitionName)
            .withExpr(expr)
            .build();

        R<MutationResult> response = milvusClient.delete(deleteParam);
        handleResponseStatus(response);

    }

    private void deleteAllRecordsInPartition(String partitionName) {
        releaseCollection();
        dropPartition(partitionName);
        compact();
        createPartition(partitionName);
        loadCollection();
    }

    public void deleteAdsNotInIds(String partitionName, List<Long> adIds) {
        System.out.println("begin delete ads not in milvus");
        if (adIds.isEmpty()) {
            deleteAllRecordsInPartition(partitionName);
            return;
        }


        String expr = "not " + ID_FIELD + " in [" + adIds.stream().map(Object::toString).collect(Collectors.joining(", ")) + "]";



        DeleteParam deleteParam = DeleteParam.newBuilder()
            .withCollectionName(COLLECTION_NAME)
            .withPartitionName(partitionName)
            .withExpr(expr)
            .build();

        R<MutationResult> response = milvusClient.delete(deleteParam);
        handleResponseStatus(response);
        System.out.println("return from delete ads not in milvus");
    }

    public void createAds(String partitionName, List<List<Float>> embeddings, List<Ad> ads) {
        if (ads.isEmpty()) {
            return;
        }

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
            ids.add(ad.getAdId());
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
        System.out.println("created in milvus?");
        query();
        System.out.println("after query");
    }

    @Override
    public List<Long> similaritySearchForAds(List<Float> searchEmbedding, String expr, String partitionName, int topK) {
        List<String> outFields = List.of(ID_FIELD);
        SearchParam searchParam = SearchParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withPartitionNames(List.of(partitionName))
                .withMetricType(MetricType.L2)
                .withOutFields(outFields)
                .withTopK(topK)
                .withVectors(List.of(searchEmbedding))
                .withVectorFieldName(EMBEDDING_FIELD)
                .withExpr(expr)
                .withParams(SEARCH_PARAM)
                .build();

        R<SearchResults> response = milvusClient.search(searchParam);
        handleResponseStatus(response);
        SearchResultsWrapper wrapper = new SearchResultsWrapper(response.getData().getResults());
        List<Long> similarPropertiesId = wrapper.getFieldData(ID_FIELD, 0)
                .stream()
                .map(object -> (Long) object)
                .toList();
        return similarPropertiesId;
    }
    public void flush() {
        milvusClient.flush(FlushParam.newBuilder().addCollectionName(COLLECTION_NAME).build());
    }

    private boolean hasPartition(String partitionName) {
        R<Boolean> response = milvusClient.hasPartition(HasPartitionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .withPartitionName(partitionName)
                .build());
        handleResponseStatus(response);

        return response.getData().booleanValue();
    }

    private void dropPartition(String partitionName) {
        R<RpcStatus> response = milvusClient.dropPartition(DropPartitionParam.newBuilder()
            .withCollectionName(COLLECTION_NAME)
            .withPartitionName(partitionName)
            .build());
        handleResponseStatus(response);
    }

    private boolean hasCollection() {
        R<Boolean> response = milvusClient.hasCollection(HasCollectionParam.newBuilder()
                .withCollectionName(COLLECTION_NAME)
                .build());
        handleResponseStatus(response);
        return response.getData().booleanValue();
    }


    private void handleResponseStatus(R<?> r) {
        if (r.getStatus() != R.Status.Success.getCode()) {
            throw new RuntimeException(r.getMessage());
        }
    }

    private R<RpcStatus> releasePartition(String partitionName) {
        R<RpcStatus> response = milvusClient.releasePartitions(ReleasePartitionsParam.newBuilder()
            .withCollectionName(COLLECTION_NAME)
            .addPartitionName(partitionName)
            .build());
        handleResponseStatus(response);
        System.out.println(response);
        return response;
    }

    public void dropCollection() {
        R<RpcStatus> response = milvusClient.dropCollection(DropCollectionParam.newBuilder()
            .withCollectionName(COLLECTION_NAME)
            .build());
        handleResponseStatus(response);
    }

}
