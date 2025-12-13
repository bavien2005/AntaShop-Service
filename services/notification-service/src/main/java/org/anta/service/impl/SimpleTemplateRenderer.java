package org.anta.service.impl;


import org.anta.service.TemplateRenderer;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SimpleTemplateRenderer implements TemplateRenderer {

    @Override
    public String render(String templateId, Map<String, Object> data) {

        if ("welcome_v1".equals(templateId)) {
            return "<h1>Welcome " + data.getOrDefault("username","")
                    + "</h1><p>Welcome to our shop!</p>";
        }

        if ("order_confirm_v1".equals(templateId)) {
            return "<h2>Order #" + data.getOrDefault("orderId","")
                    + " confirmed</h2>";
        }

        // ✅ NEW: order success template
        if ("order_success_v1".equals(templateId)) {
            String name = String.valueOf(data.getOrDefault("customerName", "bạn"));
            String orderNumber = String.valueOf(data.getOrDefault("orderNumber", ""));
            String total = String.valueOf(data.getOrDefault("total", ""));
            String totalLine = (total == null || total.equals("null") || total.isBlank())
                    ? ""
                    : "<p><b>Tổng thanh toán:</b> " + total + "₫</p>";

            return "<h2>Đặt hàng thành công 🎉</h2>"
                    + "<p>Xin chào " + name + ",</p>"
                    + "<p>Đơn hàng <b>#" + orderNumber + "</b> của bạn đã được đặt thành công.</p>"
                    + totalLine
                    + "<p>Cảm ơn bạn đã tin tưởng và mua sắm tại ANTA Việt Nam!</p>"
                    + "<p><i>Trân trọng, ANTA Việt Nam</i></p>";
        }

        if ("password_reset".equals(templateId)) {
            return "<p>Your reset code: <b>" + data.getOrDefault("code","")
                    + "</b></p>";
        }

        return data != null && data.containsKey("html")
                ? data.get("html").toString()
                : "<p>No template</p>";
    }

}
