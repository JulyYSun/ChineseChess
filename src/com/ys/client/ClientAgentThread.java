package com.ys.client;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Vector;

public class ClientAgentThread extends Thread{
    Client father;
    boolean flag=true;  //控制线程进行的标志
    DataInputStream din;
    public DataOutputStream dout;  //数据输入输出流
    public String opponent=null;  //对手名

    public ClientAgentThread(Client father){
        this.father=father;
        try {
            din=new DataInputStream(father.sc.getInputStream());
            dout=new DataOutputStream(father.sc.getOutputStream());
            String name=father.jtfNickName.getText().trim();
            dout.writeUTF("<#NICK_NAME#>"+name);  //向服务器发送当前用户名
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (flag){
            try {
                String msg=din.readUTF().trim();
                if(msg.startsWith("<#NAME_REPEAT#>")){  //昵称重复
                    this.name_repeat();
                }else if(msg.startsWith("<#NICK_LIST#>")){  //收到服务器返回的玩家列表消息
                    this.nick_list(msg);
                }else if(msg.startsWith("<#SERVER_DOWN#>")){  //服务器关闭
                    this.server_down();
                }else if(msg.startsWith("<#CHALLENGE#>")){  //收到对手发起挑战
                    this.challenge(msg);
                }else if(msg.startsWith("<#ACCEPT#>")){  //对手接受挑战消息
                    this.accept();
                }else if(msg.startsWith("<#REFUSE#>")){  //对手拒绝挑战消息
                    this.refuse();
                }else if(msg.startsWith("<#BUSY#>")){  //对手正在游戏中
                    this.busy();
                }else if(msg.startsWith("<#MOVE#>")){  //对手走棋
                    this.move(msg);
                }else if(msg.startsWith("<#ADMIT_DEFEAT#>")){  //对手认输
                    this.addmit_defeat();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void move(String msg) {
        int length=msg.length();
        int startI=Integer.parseInt(msg.substring(length-4,length-3));//获得棋子的原始位置
        int startJ=Integer.parseInt(msg.substring(length-3,length-2));
        int endI=Integer.parseInt(msg.substring(length-2,length-1));//获得走后的位置
        int endJ=Integer.parseInt(msg.substring(length-1));
        this.father.jpz.move(startI,startJ,endI,endJ);//调用方法走棋
        this.father.begin=true;//将begin设为true
    }

    private void busy() {
        this.father.begin=false;//将begin设为false
        this.father.color=0;//将color设为0
//        this.father.jtfHost.setEnabled(false);//将用于输入主机名的文本框设为不可用
//        this.father.jtfPort.setEnabled(false);//将用于输入端口号的文本框设为不可用
        this.father.jtfNickName.setEnabled(false);//将用于输入昵称的文本框设为不可用
        this.father.jbConnect.setEnabled(false);//将"连接"按钮设为不可用
        this.father.jbDisConnect.setEnabled(true);//将"断开"按钮设为可用
        this.father.jbChallenge.setEnabled(true);//将"挑战"按钮设为可用
        this.father.jbYChallenge.setEnabled(false);//将"接受挑战"按钮设为不可用
        this.father.jbNChallenge.setEnabled(false);//将"拒绝挑战"按钮设为不可用
        this.father.jbFail.setEnabled(false);//将"认输"按钮设为不可用
        JOptionPane.showMessageDialog(this.father,"对方正在游戏中！","提示",
                JOptionPane.INFORMATION_MESSAGE);//给出对方忙碌的提示信息
        this.opponent=null;
    }

    private void refuse() {
        this.father.begin=false;//将begin设为false
        this.father.color=0;//将color设为0
//        this.father.jtfHost.setEnabled(false);//将用于输入主机名的文本框设为不可用
//        this.father.jtfPort.setEnabled(false);//将用于输入端口号的文本框设为不可用
        this.father.jtfNickName.setEnabled(false);//将用于输入昵称的文本框设为不可用
        this.father.jbConnect.setEnabled(false);//将"连接"按钮设为不可用
        this.father.jbDisConnect.setEnabled(true);//将"断开"按钮设为可用
        this.father.jbChallenge.setEnabled(true);//将"挑战"按钮设为可用
        this.father.jbYChallenge.setEnabled(false);//将"接受挑战"按钮设为不可用
        this.father.jbNChallenge.setEnabled(false);//将"拒绝挑战"按钮设为不可用
        this.father.jbFail.setEnabled(false);//将"认输"按钮设为不可用
        JOptionPane.showMessageDialog(this.father,"对方拒绝您的挑战!","提示",
                JOptionPane.INFORMATION_MESSAGE);//给出对方拒绝挑战的提示信息
        this.opponent=null;
    }

    private void accept() {
//        this.father.jtfHost.setEnabled(false);//将用于输入主机名的文本框设为不可用
//        this.father.jtfPort.setEnabled(false);//将用于输入端口号的文本框设为不可用
        this.father.jtfNickName.setEnabled(false);//将用于输入昵称的文本框设为不可用
        this.father.jbConnect.setEnabled(false);//将"连接"按钮设为不可用
        this.father.jbDisConnect.setEnabled(!true);//将"断开"按钮设为不可用
        this.father.jbChallenge.setEnabled(!true);//将"挑战"按钮设为不可用
        this.father.jbYChallenge.setEnabled(false);//将"接受挑战"按钮设为不可用
        this.father.jbNChallenge.setEnabled(false);//将"拒绝挑战"按钮设为不可用
        this.father.jbFail.setEnabled(!false);//将"认输"按钮设为不可用
        JOptionPane.showMessageDialog(this.father,"对方接受你的挑战!你先走棋(红棋)",
                "提示",JOptionPane.INFORMATION_MESSAGE);
    }

    private void challenge(String msg) {
        try{
            String name=msg.substring(13);//获得挑战者的昵称
            if(this.opponent==null){//如果玩家空闲
                opponent=msg.substring(13);//将对手名的值赋为挑战者的昵称
//                this.father.jtfHost.setEnabled(false);//将用于输入主机名的文本框设为不可用
//                this.father.jtfPort.setEnabled(false);//将用于输入端口号的文本框设为不可用
                this.father.jtfNickName.setEnabled(false);//将用于输入昵称的文本框设为不可用
                this.father.jbConnect.setEnabled(false);//将"连接"按钮设为不可用
                this.father.jbDisConnect.setEnabled(!true);//将"断开"按钮设为不可用
                this.father.jbChallenge.setEnabled(!true);//将"挑战"按钮设为不可用
                this.father.jbYChallenge.setEnabled(!false);//将"接受挑战"按钮设为可用
                this.father.jbNChallenge.setEnabled(!false);//将"拒绝挑战"按钮设为可用
                this.father.jbFail.setEnabled(false);//将"认输"按钮设为不可用
                JOptionPane.showMessageDialog(this.father,opponent+"向你挑战!",
                        "提示",JOptionPane.INFORMATION_MESSAGE);//给出挑战信息
            }
            else{//如果该玩家忙碌中 ，则返回一个<#BUSY#>开头的信息
                this.dout.writeUTF("<#BUSY#>"+name);
            }
        }
        catch(IOException e){e.printStackTrace();}
    }

    private void server_down() {
//        this.father.jtfHost.setEnabled(!false);  //将用于输入主机名的文本框设为可用
//        this.father.jtfPort.setEnabled(!false);;  //将用于输入端口号的文本框设为可用
        this.father.jtfNickName.setEnabled(!false);  //将用于输入昵称的文本框设为可用
        this.father.jbConnect.setEnabled(!false);  //将"连接"按钮设为可用
        this.father.jbDisConnect.setEnabled(!true); //将"断开"按钮设为不可用
        this.father.jbChallenge.setEnabled(!true);  //将"挑战"按钮设为不可用
        this.father.jbYChallenge.setEnabled(false);  //将"接受挑战"按钮设为不可用
        this.father.jbNChallenge.setEnabled(false);  //将"拒绝挑战"按钮设为不可用
        this.father.jbFail.setEnabled(false);  //将"认输"按钮设为不可用
        this.flag=false;  //终止该客户端代理线程
        father.clientAT=null;
        JOptionPane.showMessageDialog(this.father,"服务器已停止运行！","提示",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void nick_list(String msg) {
        String s=msg.substring(13);  //分解并得到有用信息
        String[] na=s.split("\\|");  //以|分割用户列表信息
        Vector v=new Vector();
        for(int i=0;i<na.length;i++){
            if(na[i].trim().length()!=0&&(!na[i].trim().equals(father.jtfNickName.getText().trim()))){
                v.add(na[i]);  //将除当前用户名的其他用户添加到列表中
            }
        }
        father.jcbNickList.setModel(new DefaultComboBoxModel(v));  //设置下拉列表的值
    }

    private void name_repeat() {

        try {
            JOptionPane.showMessageDialog(this.father,
                    "该用户名已经被占用，请更换用户名！","错误",JOptionPane.ERROR_MESSAGE);
            din.close();//关闭数据输入流
            dout.close();//关闭数据输出流
//            this.father.jtfHost.setEnabled(!false);  //将用于输入主机名的文本框设为可用
//            this.father.jtfPort.setEnabled(!false);  //将用于输入端口号的文本框设为可用
            this.father.jtfNickName.setEnabled(!false);//将用于输入昵称的文本框设为可用
            this.father.jbConnect.setEnabled(!false);  //将"连接"按钮设为可用
            this.father.jbDisConnect.setEnabled(!true);  //将"断开"按钮设为不可用
            this.father.jbChallenge.setEnabled(!true);  //将"挑战"按钮设为不可用
            this.father.jbYChallenge.setEnabled(false);  //将"接受挑战"按钮设为不可用
            this.father.jbNChallenge.setEnabled(false);  //将"拒绝挑战"按钮设为不可用
            this.father.jbFail.setEnabled(false);  //将"认输"按钮设为不可用
            father.sc.close();  //关闭Socket
            father.sc=null;
            father.clientAT=null;
            flag=false;  //终止该客户端代理线程
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addmit_defeat() {
        JOptionPane.showMessageDialog(this.father,"对方认输,你获胜！","提示",
                JOptionPane.INFORMATION_MESSAGE);//给出获胜信息
        this.opponent=null;//将挑战者设为空
        this.father.color=0;//将color设为0
        this.father.begin=false;//将caiPan设为false
        this.father.next();//进入下一盘
//        this.father.jtfHost.setEnabled(false);//将用于输入主机名的文本框设为不可用
//        this.father.jtfPort.setEnabled(false);//将用于输入端口号的文本框设为不可用
        this.father.jtfNickName.setEnabled(false);//将用于输入昵称的文本框设为不可用
        this.father.jbConnect.setEnabled(false);//将"连接"按钮设为不可用
        this.father.jbDisConnect.setEnabled(true);//将"断开"按钮设为可用
        this.father.jbChallenge.setEnabled(true);//将"挑战"按钮设为可用
        this.father.jbYChallenge.setEnabled(false);//将"接受挑战"按钮设为不可用
        this.father.jbNChallenge.setEnabled(false);//将"拒绝挑战"按钮设为不可用
        this.father.jbFail.setEnabled(false);//将"认输"按钮设为不可用
    }
}
