package com.ys.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Vector;

public class ServerAgentThread extends Thread{
	Server father;
	Socket sc;
	DataInputStream din;  //数据输入输出流
	DataOutputStream dout;
	boolean flag=true;
	public ServerAgentThread(Server father,Socket sc){
		this.father=father;
		this.sc=sc;
		try {
			din=new DataInputStream(sc.getInputStream());  //创建数据流
			dout=new DataOutputStream(sc.getOutputStream());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public void run() {
		while(flag){
			try {
				String msg=din.readUTF().trim();  //将客户端的消息转化为字符串
				if(msg.startsWith("<#NICK_NAME#>")){  //收到新玩家信息
					this.nick_name(msg);
				}else if(msg.startsWith("<#CLIENT_LEAVE#>")){ //收到玩家离开信息
					this.client_leave(msg);
				}else if(msg.startsWith("<#CHALLENGE#>")){  //玩家主动发起挑战信息
					this.challenge(msg);
				}else if(msg.startsWith("<#ACCEPT#>")){  //接受其他玩家发起的挑战
					this.accept(msg);
				}else if(msg.startsWith("<#REFUSE#>")){  //拒绝其他玩家发起的挑战
					this.refuse(msg);
				}else if(msg.startsWith("<#BUSY#>")){  //被挑战者正在游戏中
					this.busy(msg);
				}else if(msg.startsWith("<#MOVE#>")){  //对手走棋
					this.move(msg);
				}else if(msg.startsWith("<#ADMIT_DEFEAT#>")){  //对手人数的信息
					this.admit_defeat(msg);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
		
	}

	//玩家认输
	private void admit_defeat(String msg) {
		try {
			String name =msg.substring(16);
			Vector v=father.onlineList;
			int size=v.size();
			for(int i=0;i<size;i++){
				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
				if(satTemp.getName().equals(name)){
					satTemp.dout.writeUTF(msg);
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//玩家走棋
	private void move(String msg) {
		try {
			String name =msg.substring(8,msg.length()-4);
			Vector v=father.onlineList;
			int size=v.size();
			for(int i=0;i<size;i++){
                ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
                if(satTemp.getName().equals(name)){
                    satTemp.dout.writeUTF(msg);
                    break;
                }
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//玩家正在游戏中
	private void busy(String msg) {
		try {
			String name =msg.substring(8);
			Vector v=father.onlineList; //获取在线用户列表
			int size=v.size();
			for(int i=0;i<size;i++){  //遍历用户列表，如果用户名和提出挑战用户的玩家名匹配，则返回给提出挑战的玩家busy信息
                ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
                if(satTemp.getName().equals(name)){
                    satTemp.dout.writeUTF("<#BUSY#>");
                    break;
                }
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//拒绝挑战
	private void refuse(String msg) {
		try {
			String name=msg.substring(10);  //获取提出挑战的玩家名，其前缀为<#REFUSE#>，故截取第十个字符后的字符串
			Vector v=father.onlineList; //获取在线用户列表
			int size=v.size();
			for(int i=0;i<size;i++){  //遍历用户列表，如果用户名和提出挑战用户的玩家名匹配，则返回给提出挑战的玩家拒绝信息
                ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
                if(satTemp.getName().equals(name)){
                    satTemp.dout.writeUTF("<#REFUSE#>");
                    break;
                }
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//接受挑战
	private void accept(String msg) {
		try {
			String name=msg.substring(10);  //获取被挑战的用户名
			Vector v=father.onlineList;
			int size=v.size();
			//遍历用户列表
			for(int i=0;i<size;i++){
				ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
				if(satTemp.getName().equals(name)){
					satTemp.dout.writeUTF("<#ACCEPT#>");  //发送同意的信息
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//玩家主动发起挑战
	private void challenge(String msg) {
		try {
			String name1=this.getName();  //获取发起挑战的用户名
			String name2=msg.substring(13);  //获取被挑战的用户名
			Vector v=father.onlineList;
			int size=v.size();
			//遍历用户列表，将挑战信息（发起挑战的用户）发送给被挑战者
			for(int i=0;i<size;i++){
                ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
                if(satTemp.getName().equals(name2)){
                    satTemp.dout.writeUTF("<#CHALLENGE#>"+name1);
                    break;
                }
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//用户离开服务器
	private void client_leave(String msg) {
		try {
			Vector tempv=father.onlineList;
			tempv.remove(this);  //当前用户下线，从列表移除
			int size=tempv.size();
			String nList="<#NICK_LIST#>";
			for(int i=0;i<size;i++){
                ServerAgentThread satTemp = (ServerAgentThread) tempv.get(i);
                //向其他客户端发送当前用户离线消息
                satTemp.dout.writeUTF("<#MSG#>"+this.getName()+"离线了");
                nList=nList+"|"+satTemp.getName();  //新的用户列表已经不含当前用户
            }
            //向各个客户端发送新的用户列表
			for(int i=0;i<size;i++){
                ServerAgentThread satTemp = (ServerAgentThread) tempv.get(i);
                satTemp.dout.writeUTF(nList);
            }
			this.flag=false;
			father.refreshList();  //更新在线用户列表信息
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//新用户加入
	public void nick_name(String msg) {
		try {
			String name=msg.substring(13); //获得用户名称
			this.setName(name);  //用该名称给线程命名
			Vector v=father.onlineList;  //获取在线用户的列表
			boolean isRepeat=false;
			int size=v.size();
			for(int i=0;i<size;i++){
				//遍历用户列表，查看是否重名
				ServerAgentThread tempSat=(ServerAgentThread)v.get(i); //将onlineList用户中的线程提取出来
				if(tempSat.getName().equals(name)){
					isRepeat=true;
					break;
				}
			}
			if(isRepeat){	//如果重名
				dout.writeUTF("<#NAME_REPEAT#>");  //将重复信息发送给客户端	
				din.close();
				dout.close();
				sc.close();
				flag=false; 
			}else {
				v.add(this);
				father.refreshList();
				String nickListMsg="";
				size=v.size();
				//遍历更新后的列表，将所有用户存储到nickListMsg字符串中，以"|"隔开
				for(int i=0;i<size;i++){
					ServerAgentThread tempSat=(ServerAgentThread) v.get(i);
					nickListMsg=nickListMsg+'|'+tempSat.getName();
				}
				nickListMsg="<#NICK_LIST#>"+nickListMsg;
				Vector tempV=father.onlineList;
				size=tempV.size();
				for(int i=0;i<size;i++){
					ServerAgentThread satTemp=(ServerAgentThread) tempV.get(i);
					satTemp.dout.writeUTF(nickListMsg);		//将最新的列表发送给所有客户端
					if(satTemp!=this){
						//给其他用户发送上线信息
						satTemp.dout.writeUTF("<#MSG#>"+this.getName()+"上线了");
					}
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
	}




}
