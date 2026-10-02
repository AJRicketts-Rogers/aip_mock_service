package com.rogers.mock.controller;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    @PostMapping("/bind")
    public ResponseEntity<?> bind(
            @RequestHeader(value = "Transaction-Id", required = false) String transactionId,
            @RequestBody Map<String, Object> request) {

        logRequest("BIND", transactionId, request);

        String accountNumber = getString(request, "accountNumber");
        String macAddress = getString(request, "macAddress");

        if ("FAIL400".equals(accountNumber)) {
            return badRequest(
                    "400",
                    "bindModem, account=" + accountNumber
                            + " is not in RESERVED_WAIT_FOR_DEVICE status");
        }

        if ("BADMAC".equals(macAddress)) {
            return badRequest(
                    "400",
                    "bindModem, macAddress=" + macAddress
                            + " is already assigned to another account");
        }

        return ResponseEntity.ok(
                buildResponse(
                        request,
                        "ACTIVE",
                        "200",
                        "Success"));
    }

    @PostMapping("/unbind")
    public ResponseEntity<?> unbind(
            @RequestHeader(value = "Transaction-Id", required = false) String transactionId,
            @RequestBody Map<String, Object> request) {

        logRequest("UNBIND", transactionId, request);

        String accountNumber = getString(request, "accountNumber");
        String macAddress = getString(request, "macAddress");

        if ("FAIL400".equals(accountNumber)) {
            return badRequest(
                    "400",
                    "unbindModem, account=" + accountNumber
                            + " is not ACTIVE status");
        }

        if ("BADMAC".equals(macAddress)) {
            return badRequest(
                    "400",
                    "unbindModem, macAddress=" + macAddress
                            + " is not associated with specified account");
        }

        return ResponseEntity.ok(
                buildResponse(
                        request,
                        "RESERVED_WAIT_FOR_DEVICE",
                        "200",
                        "Success"));
    }

    @PostMapping("/swap")
    public ResponseEntity<?> swap(
            @RequestHeader(value = "Transaction-Id", required = false) String transactionId,
            @RequestBody Map<String, Object> request) {

        logRequest("SWAP", transactionId, request);

        String accountNumber = getString(request, "accountNumber");
        String macAddress = getString(request, "macAddress");

        if ("FAIL400".equals(accountNumber)) {
            return badRequest(
                    "400",
                    "equipSwap, account=" + accountNumber
                            + " is not ACTIVE status");
        }

        if ("BADMAC".equals(macAddress)) {
            return badRequest(
                    "400",
                    "equipSwap, macAddress=" + macAddress
                            + " is associated to another account");
        }

        return ResponseEntity.ok(
                buildResponse(
                        request,
                        "ACTIVE",
                        "200",
                        "Success"));
    }

    private ResponseEntity<Map<String, Object>> badRequest(
            String code,
            String message) {

        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("CODE", code);
        status.put("MESSAGE", message);

        response.put("status", status);

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    private Map<String, Object> buildResponse(
            Map<String, Object> request,
            String accountStatus,
            String code,
            String message) {

        Map<String, Object> response = new LinkedHashMap<>();

        Map<String, Object> status = new LinkedHashMap<>();
        status.put("CODE", code);
        status.put("MESSAGE", message);

        Map<String, Object> aipObject = new LinkedHashMap<>();

        aipObject.put("accountNumber",
                getString(request, "accountNumber"));
        aipObject.put("samKey",
                getString(request, "samKey"));
        aipObject.put("status", accountStatus);
        aipObject.put("firstName",
                getString(request, "firstName"));
        aipObject.put("lastName",
                getString(request, "lastName"));
        aipObject.put("macAddress",
                getString(request, "macAddress"));
        aipObject.put("WAN", "72.139.118.185");
        aipObject.put("subnet",
                getString(request, "subnet"));
        aipObject.put("gatewayIp", "72.139.118.185");
        aipObject.put("subnetMask", "255.255.255.248");
        aipObject.put("blockSize",
                getString(request, "blockSize"));
        aipObject.put("usableIps", "5");
        aipObject.put("activeDate", "2026-03-06 12:00:00");
        aipObject.put("disconnectDate", "");
        aipObject.put("reserveDate", "2026-03-05 12:00:00");
        aipObject.put("expiryDate", "");
        aipObject.put("customerIp",
                Arrays.asList(
                        "72.139.118.186",
                        "72.139.118.187",
                        "72.139.118.188",
                        "72.139.118.189",
                        "72.139.118.190"));
        aipObject.put("broadcastIp", "72.139.118.191");
        aipObject.put("DNSServer1", "64.71.255.198");
        aipObject.put("DNSServer2", "64.71.255.204");

        response.put("status", status);
        response.put("AIPObject", aipObject);

        return response;
    }

    private void logRequest(
            String operation,
            String transactionId,
            Map<String, Object> request) {

        System.out.println();
        System.out.println(operation + " REQUEST");
        System.out.println("TransactionId: " + transactionId);
        System.out.println("Body: " + request);
        System.out.println();
    }

    private String getString(
            Map<String, Object> request,
            String key) {

        Object value = request.get(key);
        return value == null ? "" : value.toString();
    }
}