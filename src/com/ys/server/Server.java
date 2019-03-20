package com.ys.server;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Vector;

import javax.swing.*;

public class Server extends JFrame implements ActionListener
{
	JLabel jlPort=new JLabel("端口号"); //端口号输入标签
	JTextField jtfPort=new JTextField("8888");
	JButton jbStart=new JButton("启动"); //启动关闭按钮
	JButton jbStop=new JButton("关闭");
	JPanel jps=new JPanel();
	JList jlUserOnline=new JList();
	JScrollPane jspx=new JScrollPane(jlUserOnline);
	JSplitPane jspz=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jspx,jps); //界面主面板
	ServerSocket  ss;
	ServerThread st;  //服务器线程
	Vector onlineList=new Vector();  //用户列表
	public Server(){
		this.initialComponent(); //初始化控件
		this.addListener(); //给控件添加监听器
		this.initialFrame(); //初始化窗体
		
	}
	public  void initialComponent() {
		jps.setLayout(null);
		jlPort.setBounds(20, 20, 50, 20);  //设置大小
		jps.add(jlPort);	//添加到主panel中
		this.jtfPort.setBounds(85, 20, 60, 20);
		jps.add(jtfPort);
		this.jbStart.setBounds(18, 50, 60, 20);
		jps.add(jbStart);
		this.jbStop.setBounds(80, 50, 60, 20);
		jps.add(jbStop);
		this.jbStop.setEnabled(false);
		
	}
	public void addListener(){
		this.jbStart.addActionListener(this);
		this.jbStop.addActionListener(this);
	}
	public void initialFrame() {
		this.setTitle("Chinese Chess com.ys.server.Server");
		Image image=new ImageIcon("ico.gif").getImage();
		this.setIconImage(image);
		this.add(jspz);
		jspz.setDividerLocation(250);
		jspz.setDividerSize(4);
		this.setBounds(20, 20, 420, 320);
		this.setVisible(true);
		this.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        //服务器线程为空，直接关闭
                        if(st==null){
                            System.exit(0);
                            return;
                        }
                        try {
                            Vector v=onlineList;
                            int size=v.size();
                            //想每个用户发送服务器关闭消息
                            for(int i=0;i<size;i++){
                                ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
                                satTemp.dout.writeUTF("<#SERVER_DOWN#>");
                                satTemp.flag=false;
                            }
                            st.flag=false;  //终止服务器线程
                            st=null;
                            ss.close();
                            v.clear();
                            refreshList();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        System.exit(0);

                    }
                }
        );
		
		
	}
	
	@Override
	//先给对应的控件添加监听事件，如 this.jbStart.addActionListener(this);
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==this.jbStart){
		    //启动服务器按钮
			jbStart_event();
		}
		if(e.getSource()==this.jbStop){
		    //关闭按钮
			jbStop_event();
		}
		
	}
	
	//启动关闭按钮事件
	public void jbStart_event() {
	    int port=0;
	    //获取端口号码，处理异常
        try {
            port=Integer.parseInt(this.jtfPort.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,"端口号只能是整数!",
                    "错误",JOptionPane.ERROR_MESSAGE);
            return;
        }
        //端口号不规范
        if(port<0||port>65535){
            JOptionPane.showMessageDialog(this,"端口号只能是0-65535的整数!",
                    "错误",JOptionPane.ERROR_MESSAGE);
            return;
        }
        try{
            this.jbStart.setEnabled(false);
            this.jbStop.setEnabled(true);
            this.jtfPort.setEnabled(false);  //输入端口变为不可更改
            ss=new ServerSocket(port);
            st=new ServerThread(this);
            st.start();
            JOptionPane.showMessageDialog(this,"服务器启动成功！",
                    "提示", JOptionPane.INFORMATION_MESSAGE);

        } catch (IOException e) {
            JOptionPane.showMessageDialog(this,"服务器启动失败！",
                    "错误", JOptionPane.INFORMATION_MESSAGE);
            this.jbStart.setEnabled(true);
            this.jbStop.setEnabled(false);
            this.jtfPort.setEnabled(true);
        }
        System.out.println("服务器启动成功！");

	}

	//
	public void jbStop_event() {
        try {
            Vector v=onlineList;
            int size=v.size();
            //像每个客户端发送服务器关闭消息
            for(int i=0;i<size;i++){
                ServerAgentThread satTemp = (ServerAgentThread) v.get(i);
                satTemp.dout.writeUTF("<#SERVER_DOWN#>");
                satTemp.flag=false;
            }
            //关闭服务器线程
            st.flag=false;
            st=null;
            ss.close();
            v.clear();
            refreshList();
            System.out.println("服务器正常关闭！");
            this.jtfPort.setEnabled(true);
            this.jbStart.setEnabled(true);
            this.jbStop.setEnabled(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //更新在线用户列表
	public void refreshList() {
		Vector v=new Vector();
		int size=this.onlineList.size();
		//获取每个用户，并添加到列表中
		for(int i=0;i<size;i++){
            ServerAgentThread satTemp = (ServerAgentThread) this.onlineList.get(i);
            String user=satTemp.sc.getInetAddress().toString();
            user=user+"|"+satTemp.getName();
            v.add(user);
        }
        //将列表显示到userList中
        this.jlUserOnline.setListData(v);
		
	}
	
	public static void main(String[] args) {
		new Server();
	}
	

	

}
