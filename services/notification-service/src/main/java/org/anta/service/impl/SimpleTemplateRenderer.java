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
        if ("password_reset".equals(templateId)) {
            return "<p>Your reset code: <b>" + data.getOrDefault("code","")
                    + "</b></p>";
        }
        return data != null && data.containsKey("html") ? data.get("html").toString() : "<p>No template</p>";
    }
}
