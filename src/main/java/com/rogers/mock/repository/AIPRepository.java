package com.rogers.mock.repository;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rogers.mock.model.AIPRecord;

@Component
public class AIPRepository {

    private static final String FILE_NAME = "aip-data.json";

    private Map<String, AIPRecord> records = new HashMap<String, AIPRecord>();

    private ObjectMapper mapper = new ObjectMapper();

    public AIPRepository() {
        load();
    }

    public synchronized AIPRecord get(String accountNumber) {
        return records.get(accountNumber);
    }

    public synchronized void save(AIPRecord record) {
        records.put(record.getAccountNumber(), record);
        persist();
    }

    public synchronized void delete(String accountNumber) {
        records.remove(accountNumber);
        persist();
    }

    public synchronized boolean exists(String accountNumber) {
        return records.containsKey(accountNumber);
    }

    public synchronized AIPRecord findByMac(String macAddress) {

        for (AIPRecord record : records.values()) {

            if (record.getMacAddress() != null && record.getMacAddress() .equalsIgnoreCase(macAddress)) {
                return record;
            }
        }

        return null;
    }

    public Collection<AIPRecord> getAll() {
        return records.values();
    }

    private void load() {

        try {
            File file = new File(FILE_NAME);
            
            if (file.exists()) {
                records = mapper.readValue(file, new TypeReference<Map<String, AIPRecord>>() {});
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void persist() {

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_NAME), records);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}