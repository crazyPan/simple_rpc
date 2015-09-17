package com.study.ServiceProvider;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.Method;

import com.study.service.MyServiceImpl;
import com.study.service.ProtoRequest;
import com.study.service.Request;

public class ProviderHandler extends ChannelInboundHandlerAdapter{
	
	String methodName = "";
	Request req = null;
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
//		if(msg instanceof String){
//			methodName = (String)msg;
//			System.out.println("methodName" + methodName);
//		}else if(msg instanceof Request){
//			MyServiceImpl service = new MyServiceImpl();
//			req = (Request) msg;
//			Method method = service.getClass().getDeclaredMethod(methodName, Request.class);
//			Object result = method.invoke(service,req);
//			ctx.writeAndFlush(result);
//		}
		if(msg instanceof ProtoRequest.msgInfo){
			ProtoRequest.msgInfo req = (ProtoRequest.msgInfo) msg;
			String methodName = req.getMethodName();
			MyServiceImpl service = new MyServiceImpl();
			Method method = service.getClass().getDeclaredMethod(methodName, ProtoRequest.msgInfo.class);
			Object result = method.invoke(service,req);
			ctx.writeAndFlush(result);
		}
		
		
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		
		System.out.println("server active");
		
		
	}
}
