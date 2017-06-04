package com.nexo.nexorouter.microservice.mail.utils;

import io.vertx.core.json.JsonObject;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by carlos on 18/05/17.
 */
public class HtmlParser {
    public static String parserHtml(String html, JsonObject params){

        try {
            for(Map.Entry<String, Object> param: params){
                String to = "${"+param.getKey()+"}";
                String _for = param.getValue().toString();
                html = html.replaceAll(Pattern.quote(to), _for);
            }
        }catch (Exception e){
            System.out.println(e.getCause());
        }
        return html;
    }
}
