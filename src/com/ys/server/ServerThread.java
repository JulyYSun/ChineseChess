package com.ys.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import java.net.*;

public class ServerThread extends Thread {
	Server father;
	ServerSocket ss;
	DataInputStream din;  //数据输入流与数据输出流
	DataOutputStream dout;
	boolean flag=true;
	public ServerThread(Server father){
		//构造器
		this.father=father;
		ss=father.ss;
	}
	public void run() {
		while(flag){
			try {
				Socket sc=ss.accept(); //等待客户端连接
				ServerAgentThread sat=new ServerAgentThread(father,sc);
				sat.start();//创建并启动服务器代理线程
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
	}

}
