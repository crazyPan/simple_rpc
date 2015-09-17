package com.study.ServiceComsumer;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import com.study.service.ProtoRequest;
import com.study.service.ProtoResponse;
import com.study.util.ProtoUtil;

public class ConsumerHandler extends ChannelInboundHandlerAdapter implements
		ChannelHandler {
	
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if(msg instanceof ProtoResponse.msgInfo){
			ProtoResponse.msgInfo result = (ProtoResponse.msgInfo)msg;
			System.out.println(result.getResult());
		}
	}
	
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		 System.out.println("client active");
//		 Request req = new Request(1, 2);
		 ProtoRequest.msgInfo info = ProtoUtil.createReq("Plus",1, 2);
		 ctx.writeAndFlush(info);
	}
}
