package com.study.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.study.service.ProtoRequest;
import com.study.service.ProtoResponse;

public class ProtoUtil {
	
	public static byte[] encode(ProtoRequest.msgInfo req){
		return req.toByteArray();
	}
	
	public static ProtoRequest.msgInfo decode(byte[] req) throws InvalidProtocolBufferException{
		return ProtoRequest.msgInfo.parseFrom(req);
	}
	
	
	public static ProtoRequest.msgInfo createReq(String methodName,int num1,int num2){
		
		ProtoRequest.msgInfo.Builder builder = ProtoRequest.msgInfo.newBuilder();
		builder.setNum1(num1);
		builder.setNum2(num2);
		builder.setMethodName(methodName);
		return builder.build();
		
	}
	
	
	public static ProtoResponse.msgInfo createReqonse(int result){
		ProtoResponse.msgInfo.Builder builder = ProtoResponse.msgInfo.newBuilder();
		builder.setResult(result);
		return builder.build();
	}
}
