package com.study.ServiceComsumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.net.UnknownHostException;

import com.study.service.ProtoRequest;
import com.study.service.ProtoResponse;
import com.study.service.Response;

public class Consumer {
	public static Response consum(String host, int port){
		Socket socket = null;
		ObjectOutputStream out = null;
		ObjectInputStream in = null;
        try {
			socket = new Socket(host, port);
			out = new ObjectOutputStream(socket.getOutputStream());
			
			out.writeUTF(new String("Plus"));
			
			//-----protobuf序列化的使用--------------------
			ProtoRequest.msgInfo.Builder builder=ProtoRequest.msgInfo.newBuilder();  
			builder.setNum1(1);
			builder.setNum2(2);
			ProtoRequest.msgInfo info=builder.build();  

			//-----protobuf序列化的使用--------------------

			out.writeObject(info);
			out.flush();
			in = new ObjectInputStream(socket.getInputStream());
			Response result = (Response)in.readObject();
			System.out.println("---result-----" + result.getResult());
			return result;
		} catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;

		}  finally {
			if(null != socket){
				try {
					socket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(out != null ){
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 这个地方这个泛型用起来还是生疏
	 * @param <T>
	 * @param interfaceClass
	 * @param host
	 * @param port
	 * @return
	 */
	public static  <T> T proxy(final Class<T> interfaceClass, final String host, final int port){
		
		//这个地方咋又是？了呢
		T proxy = (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass}, new InvocationHandler() {
			
			public Object invoke(Object obj, Method method, Object[] aobj)
					throws Throwable {
				return consum(host,port);
			}
		});
		return proxy;
	}
	
	
	public static Response consumNetty(String host, int port){
		
		Bootstrap client = new Bootstrap();
		EventLoopGroup group = new NioEventLoopGroup();		
		client.group(group);
		client.channel(NioSocketChannel.class);
		client.option(ChannelOption.TCP_NODELAY, true);
		
		client.handler(new ChannelInitializer<Channel>() {
			protected void initChannel(Channel arg0) throws Exception {
				arg0.pipeline().addLast(
						new ProtobufEncoder(), 
						new ProtobufDecoder(ProtoResponse.msgInfo.getDefaultInstance()),
                        new ConsumerHandler());
			};
		});
		
		ChannelFuture future;
		try {
			future = client.connect(host, port).sync();
			//TODO：是做什么
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally{
			group.shutdownGracefully();
		}
	
		return null;
	}

	
	public static void main(String[] args) {
		// MyService service = proxy(MyService.class,"192.168.1.15",4321);
		// service.Plus(new Request(1, 2));
		consumNetty("127.0.0.1",4321);
	}
}
