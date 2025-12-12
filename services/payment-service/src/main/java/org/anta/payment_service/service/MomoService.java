package org.anta.payment_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.anta.payment_service.client.MomoAPI;
import org.anta.payment_service.dto.request.CreateMomoRequest;
import org.anta.payment_service.dto.response.CreateMomoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MomoService {

    @Value("${momo.partner-code}")
    private String PARTNER_CODE;

    @Value("${momo.access-key}")
    private String ACCESS_KEY;

    @Value("${momo.secret-key}")
    private String SECRET_KEY;

    @Value("${momo.return-url}")
    private String REDIRECT_URL;

    @Value("${momo.ipn-url}")
    private String IPN_URL;

    @Value("${momo.request-type:captureWallet}")
    private String REQUEST_TYPE;


    private final MomoAPI momoAPI;

    public CreateMomoResponse createQRForPayment(String requestId,
                                                 Long amount, String partnerOrderIdInput) {

        // partnerOrderIdInput is expected to be already prepared by caller (e.g. "10-<requestId>")
        String partnerOrderId = partnerOrderIdInput;
        if (partnerOrderId == null || partnerOrderId.isBlank()) {
            // if caller didn't supply a partnerOrderId, fallback to unique id
            partnerOrderId = UUID.randomUUID().toString();
        }

        String orderInfo = "Payment for order: " + partnerOrderId;
        String extraData = "";

        String rawSignature = String.format(
                "accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                ACCESS_KEY,
                amount,
                extraData,
                IPN_URL,
                partnerOrderId,
                orderInfo,
                PARTNER_CODE,
                REDIRECT_URL,
                requestId,
                REQUEST_TYPE
        );

        log.info(" [CREATE] Raw string before signing:\n{}", rawSignature);

        String prettySignnature;
        try {
            prettySignnature = signHmacSHA256(rawSignature, SECRET_KEY);
            log.info("Signature: {}", prettySignnature);
        } catch (Exception e) {
            log.error("Error while signing HMAC SHA256: {}" , e.getMessage(), e);
            throw new RuntimeException("Error creating signature for Momo request", e);
        }

        CreateMomoRequest createMomoRequest = CreateMomoRequest.builder()
                .partnerCode(PARTNER_CODE)
                .requestType(REQUEST_TYPE)
                .ipnUrl(IPN_URL)
                .orderId(partnerOrderId)
                .amount(amount)
                .orderInfo(orderInfo)
                .requestId(requestId)
                .redirectUrl(REDIRECT_URL)
                .lang("vi")
                .extraData(extraData)
                .signature(prettySignnature)
                .accessKey(ACCESS_KEY)
                .build();

        // **Call MoMo once and return the single response**
        CreateMomoResponse resp = momoAPI.createMomoQR(createMomoRequest);
        log.info("MoMo resp: orderId={}, requestId={}, payUrl={}, deeplink={}, qrCodeUrl={}, resultCode={}, message={}",
                resp.getOrderId(), resp.getRequestId(), resp.getPayUrl(), resp.getDeeplink(), resp.getQrCodeUrl(),
                resp.getResultCode(), resp.getMessage());
        return resp;
    }



//    public boolean verifyIpnSignature(Map<String, String> params) {
//        // Momo gửi lên một số field, điển hình: partnerCode, accessKey, requestId, orderId, amount, orderInfo,
//        // orderType, transId, message, responseTime, resultCode, payType, signature
//        // thu tu phai dung theo doc momo ipn
//        List<String> orderedFields = Arrays.asList(
//                "partnerCode",
//                "accessKey",
//                "requestId",
//                "orderId",
//                "amount",
//                "orderInfo",
//                "orderType",
//                "transId",
//                "message",
//                "responseTime",
//                "resultCode",
//                "payType"
//        );
//
//        String raw = orderedFields.stream()
//                .map(key -> key + "=" + Objects.toString(params.getOrDefault(key, ""), ""))
//                .collect(Collectors.joining("&"));
//
//        log.info(" [IPN] Raw data received from MoMo:\n{}", raw);
//
//        String receivedSignature = params.get("signature");
//        if (receivedSignature == null) {
//            log.warn("IPN verify: no signature in params");
//            return false;
//        }
//
//        String computed;
//        try {
//            computed = signHmacSHA256(raw, SECRET_KEY);
//            log.info(" [IPN] Computed Signature (server): {}", computed);
//            log.info(" [IPN] Received Signature (MoMo):  {}", receivedSignature);
//        } catch (Exception e) {
//            log.error("Error computing IPN signature", e);
//            return false;
//        }
//
//        boolean ok = computed.equals(receivedSignature);
//        log.info("IPN verify: computed == received ? {} (computed={}, received={})", ok, computed, receivedSignature);
//        return ok;
//    }

    // HMAC SHA256 signing method
    // truyền dử liệu và key vào để mã hóa giữa client và server
    // dữ liệu truyền đi sẽ được mã hóa và chỉ có server mới giải mã được
    // đảm bảo tính toàn vẹn và bảo mật của dữ liệu không cho phép bên thứ 3 can thiệp
    private String signHmacSHA256(String data, String key) throws Exception {

        Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "" +
                "HmacSHA256");
        hmacSHA256.init(secretKeySpec);
        byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

}
