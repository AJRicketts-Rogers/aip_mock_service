package com.rogers.mock.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.rogers.mock.repository.AIPRepository;
import com.rogers.mock.model.AIPRecord;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {
	
	@Autowired
	private AIPRepository repository;

	@PostMapping
	public ResponseEntity<?> processRequest(
	        @RequestHeader(value = "Transaction-Id", required = false) String transactionId,
	        @RequestBody Map<String, Object> request) {

	    String operation = getString(request, "operation");

	    logRequest(operation, transactionId, request);

	    String accountNumber = getString(request, "accountNumber");
	    String macAddress = getString(request, "macAddress");
	    AIPRecord record;

	    switch (operation) {

	    case "bindModem":

	    	AIPRecord macRecord = repository.findByMac(macAddress);

			if (macRecord != null && !accountNumber.equals(macRecord.getAccountNumber())) {
		
			    return badRequest("400", "bindModem, macAddress=" + macAddress + " is already assigned to another account");
			}
		
			AIPRecord existing = repository.get(accountNumber);
		
			if (existing != null && "ACTIVE".equals(existing.getStatus())) {
		
			    return badRequest("400", "bindModem, account=" + accountNumber + " is already ACTIVE");
			}
		
			record = existing == null ? new AIPRecord() : existing;
		
			record.setAccountNumber(accountNumber);
			record.setSamKey(getString(request, "samKey"));
			record.setStatus("ACTIVE");
			record.setMacAddress(macAddress);
			record.setSerialNumber(getString(request, "serialNumber"));
			record.setMake(getString(request, "make"));
			record.setModel(getString(request, "model"));
			record.setSubnet(getString(request, "subnet"));
			record.setBlockSize(getString(request, "blockSize"));
		
			repository.save(record);
		
			return ResponseEntity.ok(buildResponse(request, "ACTIVE", "200", "Success"));

	    case "unbindModem":
	    	record = repository.get(accountNumber);

			if (record == null) {
		
			    return badRequest("400", "unbindModem, account=" + accountNumber + " does not exist");
			}
			
			if (record.getStatus().equals("RESERVED_WAIT_FOR_DEVICE")) {
		
			    return badRequest("400", "unbindModem, account=" + accountNumber + " is not ACTIVE, already in RESERVED_WAIT_FOR_DEVICE status");
			}
		
			if (!macAddress.equalsIgnoreCase(record.getMacAddress())) {
		
			    return badRequest("400", "unbindModem, macAddress=" + macAddress + " is not associated to specified account");
			}
		
			record.setStatus("RESERVED_WAIT_FOR_DEVICE");
			record.setMacAddress("");
		
			repository.save(record);
			
			return ResponseEntity.ok(buildResponse(request, "RESERVED_WAIT_FOR_DEVICE", "200", "Success"));

	    case "equipSwap":

	    	record =
	        repository.get(accountNumber);

			if (record == null || !"ACTIVE".equals(record.getStatus())) {
		
			    return badRequest("400", "equipSwap, account=" + accountNumber + " is not ACTIVE status");
			}
		
			String oldMac = getString(request, "oldMacAddress");
		
			if (!oldMac.equalsIgnoreCase(record.getMacAddress())) {
		
			    return badRequest("400", "equipSwap, macAddress=" + oldMac + " is not associated to specified account");
			}
		
			record.setMacAddress(macAddress);
		
			repository.save(record);
			
			return ResponseEntity.ok(buildResponse(request, "ACTIVE", "200", "Success"));
	
	    case "changeOwner":

	    	String oldAccount = getString(request, "oldAccountNumber");

			AIPRecord oldRecord = repository.get(oldAccount);
		
			if (oldRecord == null || !"ACTIVE".equals(oldRecord.getStatus())) {
		
			    return badRequest("400", "changeOwner, account=" + oldAccount + " not in ACTIVE status");
			}
		
			if (repository.exists(accountNumber)) {
		
			    return badRequest("400", "changeOwner, account=" + accountNumber + " already exists");
			}
		
			oldRecord.setAccountNumber(accountNumber);
			oldRecord.setSamKey(getString(request, "samKey"));
		
			repository.delete(oldAccount);
			repository.save(oldRecord);
			
			return ResponseEntity.ok(buildResponse(request, "ACTIVE", "200", "Success"));

	    default:
	        return badRequest("400", "Unknown operation: " + operation);
	    }
	}


    private ResponseEntity<Map<String, Object>> badRequest(String code, String message) {

        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("CODE", code);
        status.put("MESSAGE", message);

        response.put("status", status);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    private Map<String, Object> buildResponse(Map<String, Object> request, String accountStatus, String code,String message) {

        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("CODE", code);
        status.put("MESSAGE", message);

        Map<String, Object> aipObject = new LinkedHashMap<>();

        aipObject.put("accountNumber", getString(request, "accountNumber"));
        aipObject.put("samKey", getString(request, "samKey"));
        aipObject.put("status", accountStatus);
        aipObject.put("firstName", getString(request, "firstName"));
        aipObject.put("lastName", getString(request, "lastName"));
        aipObject.put("macAddress", getString(request, "macAddress"));
        aipObject.put("oldAccountNumber", getString(request, "oldAccountNumber"));
        aipObject.put("oldSamKey", getString(request, "oldSamKey"));
        aipObject.put("subnet", getString(request, "subnet"));
        aipObject.put("blockSize", getString(request, "blockSize"));
        aipObject.put("subnet", getString(request, "subnet"));
        aipObject.put("blockSize", getString(request, "blockSize"));
        aipObject.put("WAN", "72.139.118.185");
        aipObject.put("gatewayIp", "72.139.118.185");
        aipObject.put("subnetMask", "255.255.255.248");
        aipObject.put("usableIps", "5");
        aipObject.put("activeDate", "2026-03-06 12:00:00");
        aipObject.put("disconnectDate", "");
        aipObject.put("reserveDate", "2026-03-05 12:00:00");
        aipObject.put("expiryDate", "");
        aipObject.put("customerIp", Arrays.asList("72.139.118.186","72.139.118.187","72.139.118.188","72.139.118.189","72.139.118.190"));
        aipObject.put("broadcastIp", "72.139.118.191");
        aipObject.put("DNSServer1", "64.71.255.198");
        aipObject.put("DNSServer2", "64.71.255.204");

        response.put("status", status);
        response.put("AIPObject", aipObject);

        return response;
    }

    private void logRequest(String operation, String transactionId, Map<String, Object> request) {

        System.out.println();
        System.out.println(operation + " REQUEST");
        System.out.println("TransactionId: " + transactionId);
        System.out.println("Body: " + request);
        System.out.println();
    }

    private String getString(Map<String, Object> request, String key) {

        Object value = request.get(key);
        return value == null ? "" : value.toString();
    }
}