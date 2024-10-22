package me.raining.gateway.core.response;

import com.fasterxml.jackson.databind.node.ObjectNode;
import io.netty.handler.codec.http.*;
import lombok.Data;
import me.raining.gateway.common.enums.ResponseCode;
import me.raining.gateway.common.utils.JSONUtil;
import org.asynchttpclient.Response;

/**
 * @author raining
 * @version 1.0.0
 * @description 网关响应类，无论是使用哪种协议请求网关，都可以将响应的结果数据封装成统的数据模型
 * 统一成 {code，status，data，message}
 */
@Data
public class LiteGatewayResponse {

    /**
     * 响应头
     */
    private HttpHeaders responseHeaders = new DefaultHttpHeaders();

    /**
     * 额外的响应结果
     */
    private final HttpHeaders extraResponseHeaders = new DefaultHttpHeaders();
    /**
     * 响应内容
     */
    private String content;

    /**
     * 异步返回对象
     */
    private Response futureResponse;

    /**
     * 响应返回码
     */
    private HttpResponseStatus httpResponseStatus;


    public LiteGatewayResponse(){

    }

    /**
     * 设置响应头信息
     */
    public  void  putHeader(CharSequence key, CharSequence val){
        responseHeaders.add(key,val);
    }

    /**
     * 构建异步响应对象
     */
    public static LiteGatewayResponse buildGatewayResponse(Response futureResponse){
        LiteGatewayResponse response = new LiteGatewayResponse();
        response.setFutureResponse(futureResponse);
        response.setHttpResponseStatus(HttpResponseStatus.valueOf(futureResponse.getStatusCode()));
        return response;
    }

    /**
     * 处理返回json对象，失败时调用
     */
    public static LiteGatewayResponse buildGatewayResponse(ResponseCode code){
        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS,code.getStatus().code());
        objectNode.put(JSONUtil.CODE,code.getCode());
        objectNode.put(JSONUtil.MESSAGE,code.getMessage());

        LiteGatewayResponse response = new LiteGatewayResponse();
        response.setHttpResponseStatus(code.getStatus());
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON+";charset=utf-8");
        response.setContent(JSONUtil.toJSONString(objectNode));

        return response;
    }

    public static LiteGatewayResponse buildGatewayResponse(ResponseCode code, String msg){
        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS,code.getStatus().code());
        objectNode.put(JSONUtil.CODE,code.getCode());
        objectNode.put(JSONUtil.MESSAGE,msg);

        LiteGatewayResponse response = new LiteGatewayResponse();
        response.setHttpResponseStatus(code.getStatus());
        response.putHeader(HttpHeaderNames.CONTENT_TYPE, HttpHeaderValues.APPLICATION_JSON+";charset=utf-8");
        response.setContent(JSONUtil.toJSONString(objectNode));

        return response;
    }

    /**
     * 处理返回json对象，成功时调用
     */
    public  static LiteGatewayResponse buildGatewayResponse(Object data){
        ObjectNode objectNode = JSONUtil.createObjectNode();
        objectNode.put(JSONUtil.STATUS, ResponseCode.SUCCESS.getStatus().code());
        objectNode.put(JSONUtil.CODE,ResponseCode.SUCCESS.getCode());
        objectNode.putPOJO(JSONUtil.DATA,data);

        LiteGatewayResponse response = new LiteGatewayResponse();
        response.setHttpResponseStatus(ResponseCode.SUCCESS.getStatus());
        response.putHeader(HttpHeaderNames.CONTENT_TYPE,HttpHeaderValues.APPLICATION_JSON+";charset=utf-8");
        response.setContent(JSONUtil.toJSONString(objectNode));

        return response;
    }
}
