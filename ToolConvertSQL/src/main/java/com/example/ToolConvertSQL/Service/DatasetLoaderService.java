package com.example.ToolConvertSQL.Service;

import com.example.ToolConvertSQL.DTO.EvaluationCase;
import com.example.ToolConvertSQL.Service.Imp.DatasetLoaderServiceImp;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class DatasetLoaderService implements DatasetLoaderServiceImp {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<EvaluationCase> loadDataset(String fileName) {

        try {
            ClassPathResource resource = new ClassPathResource(fileName);
            InputStream inputStream = resource.getInputStream();

            return objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<EvaluationCase>>() {}
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to load dataset: " + e.getMessage());
        }
    }
}
