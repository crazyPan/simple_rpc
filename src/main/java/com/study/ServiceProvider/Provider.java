package com.study.ServiceProvider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

import com.study.service.ProtoRequest;
import com.study.service.Request;

public class Provider {
	
	  /**
	   * 
	   * @param service:要发布的服务
	   * @param port：服务暴露的端口
	   * @throws Exception
	   */
	  public static void export(final Object service) throws Exception {  
		  ServerSocket server = null;
		  Socket socket = null;
		  //final 在方法内部不能对其做修改
		  
		  ObjectInputStream input = null;
		  ObjectOutputStream out = null;
		  try {
			  server = new ServerSocket(4321);
			  socket = server.accept();
			  input = new ObjectInputStream(socket.getInputStream());
			  String methodName = input.readUTF();
			  Request arguments = (Request)input.readObject();  
			  
			  System.out.println("---methodName---"+methodName);
			  
			  Method method = service.getClass().getDeclaredMethod(methodName, Request.class);
			  Object result = method.invoke(service,arguments);
			  
			  out = new ObjectOutputStream(socket.getOutputStream());
			  out.writeObject(result);
			  out.flush();
		  } catch (Exception e) {
			  e.printStackTrace();
			
		  }finally{
			  if(null != input){
				  input.close();
			  }
			  if(server != null){
				  server.close();
			  }
			  if(socket != null){
				  socket.close();
			  }
			  if(out != null){
				  out.close();
			  }
		  }
	  }
	  
	  
	  
//	  public static void bind() throws Exception {  
//		  EventLoopGroup boss = new NioEventLoopGroup();
//		  EventLoopGroup worker = new NioEventLoopGroup();
//		  
//		  try{
//			  ServerBootstrap server = new ServerBootstrap();
//			  server.group(boss, worker)
//			  	.channel(NioServerSocketChannel.class);
//			  server.option(ChannelOption.SO_BACKLOG, 1024);
//			  //TODO 6 childHandler handler什么区别
//			  server.childHandler(new ChannelInitializer<Channel>() {
//
//				@Override
//				protected void initChannel(Channel arg0) throws Exception {
//					arg0.pipeline()
//					//TODO:ClassResolver 是什么东西
////				    .addLast(new ObjectDecoder(
////				    		ClassResolvers.weakCachingConcurrentResolver(this.getClass().getClassLoader())),new ObjectEncoder())
//	                .addLast(new ObjectEncoder(),new ObjectDecoder(ClassResolvers.cacheDisabled(null)),new ProviderHandler());
//
//				}
//			  });
//			  //TODO sync到底做了什么
//			  ChannelFuture future = server.bind(4321).sync();
//			  future.channel().closeFuture().sync();
//		  }finally{
//			  boss.shutdownGracefully();
//			  worker.shutdownGracefully();
//		  }
//		
//	  }

	  
	  
	  
	  public static void bind(int port) throws Exception {
	        EventLoopGroup bossGroup = new NioEventLoopGroup();
	        EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	            // 配置服务器的NIO线程租
	            ServerBootstrap b = new ServerBootstrap();
	            b.group(bossGroup, workerGroup)
	                    .channel(NioServerSocketChannel.class)
	                    .option(ChannelOption.SO_BACKLOG, 1024)
	                    .childHandler(new ChildChannelHandler());

	            // 绑定端口，同步等待成功
	            ChannelFuture f = b.bind(port).sync();
	            // 等待服务端监听端口关闭
	            f.channel().closeFuture().sync();
	        } finally {
	            // 优雅退出，释放线程池资源
	            bossGroup.shutdownGracefully();
	            workerGroup.shutdownGracefully();
	        }
	    }

	    
	  
	  
	  public static void main(String[] args) {
		  
			try {
				bind(4321);
			} catch (Exception e) {
				e.printStackTrace();
			}
	  }
}


class ChildChannelHandler extends ChannelInitializer<SocketChannel> {
	@Override
	protected void initChannel(SocketChannel arg0) throws Exception {
		System.out.println("server initChannel..");
		arg0.pipeline().addLast(
				new ProtobufEncoder(),
        		new ProtobufDecoder(ProtoRequest.msgInfo.getDefaultInstance()),
        		new ProviderHandler());
    }
}
