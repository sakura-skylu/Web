package com.example.doda.mapper;

import com.example.doda.entity.DrugPrediction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.List;

@Mapper
public interface DrugPredictionMapper {
    @Select({
            "<script>",
            "SELECT * FROM drug_predictions",
            "WHERE cancerType = #{cancerType}",
            "<if test='drugName != null and drugName != \"\"'>",
            "AND LOWER(drugName) LIKE CONCAT('%', LOWER(#{drugName}), '%')",
            "</if>",
            "</script>"
    })
    List<DrugPrediction> findDrug(String cancerType, String drugName);
}