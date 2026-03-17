package com.example.doda.mapper;

import com.example.doda.entity.DrugPrediction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DrugPredictionMapper {
    @Select({
            "<script>",
            "SELECT sampleId, drugName, sensitivityScore, cancerType FROM drug_predictions",
            "WHERE cancerType = #{cancerType}",
            "<if test='drugName != null and drugName != \"\"'>",
            "AND LOWER(drugName) LIKE CONCAT('%', LOWER(#{drugName}), '%')",
            "</if>",
            "ORDER BY sampleId",
            "LIMIT #{limit} OFFSET #{offset}",
            "</script>"
    })
    List<DrugPrediction> findDrugPage(@Param("cancerType") String cancerType,
                                      @Param("drugName") String drugName,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    @Select({
            "<script>",
            "SELECT COUNT(1) FROM drug_predictions",
            "WHERE cancerType = #{cancerType}",
            "<if test='drugName != null and drugName != \"\"'>",
            "AND LOWER(drugName) LIKE CONCAT('%', LOWER(#{drugName}), '%')",
            "</if>",
            "</script>"
    })
    long countDrug(@Param("cancerType") String cancerType,
                   @Param("drugName") String drugName);

    @Select({
            "<script>",
            "SELECT sampleId, drugName, sensitivityScore, cancerType FROM drug_predictions",
            "WHERE cancerType = #{cancerType}",
            "<if test='drugName != null and drugName != \"\"'>",
            "AND LOWER(drugName) LIKE CONCAT('%', LOWER(#{drugName}), '%')",
            "</if>",
            "ORDER BY sampleId",
            "</script>"
    })
    List<DrugPrediction> findDrug(@Param("cancerType") String cancerType,
                                  @Param("drugName") String drugName);
}
