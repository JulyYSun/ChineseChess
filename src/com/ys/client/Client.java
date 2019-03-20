package com.ys.client;

import com.ys.ChessBoard;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.Socket;
import java.util.Vector;


//播放背景音乐线程
class Bgm extends Thread{
    @Override
    public void run() {
            try {
                File file=new File("bgm.mp3");
                FileInputStream fis=new FileInputStream(file);
                BufferedInputStream stream=new BufferedInputStream(fis);
                Player player=new Player(stream);
                player.play();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (JavaLayerException e) {
                e.printStackTrace();
            }
        }
}
public class Client extends JFrame implements ActionListener {
    public static final Color bgColor=new Color(245,250,160);  //棋盘背景色
    public static final Color focusBg=new Color(242,242,242);  //棋子选中背景色
    public static final Color focusChar=new Color(96,95,91);  //棋子选中后的字符颜色
    public static final Color color1=new Color(205,0,0);  //红方棋子背景色
    public static final Color color2=Color.white;  //白方棋子背景色
    public static Bgm bgm=new Bgm();

//    JLabel jlHost =new JLabel("主机名");
//    JLabel jlPort =new JLabel("端口号");
    JLabel jlNickName =new JLabel("请输入你的名称");
//    JTextField jtfHost=new JTextField("127.0.0.1");  //默认服务器地址端口
//    JTextField jtfPort=new JTextField("8888");
    JLabel jlOnlineList=new JLabel("当前在线用户");
    public JTextField jtfNickName=new JTextField("小明");  //默认用户名
    public JButton jbConnect=new JButton("连接");
    public JButton jbDisConnect=new JButton("断开");
    public JButton jbFail=new JButton("认输");
    public JButton jbChallenge=new JButton("挑战");
    JComboBox jcbNickList=new JComboBox();
    public JButton jbYChallenge=new JButton("接受挑战");
    public JButton jbNChallenge=new JButton("拒绝挑战");
    //设置类控件
    JButton jbBgm=new JButton("背景音乐:开");
    JLabel jlBg=new JLabel("切换背景");
    JComboBox jcbBgList=new JComboBox();
    static boolean isBgm=true;


    int width=60;  //棋盘格子的宽度
    ChessMan[][] chessMan=new ChessMan[9][10];
    ChessBoard jpz=new ChessBoard(chessMan,width,this);  //棋盘
    //JPanel jpz=new JPanel();
    JPanel jpy=new JPanel();
    JPanel jpPlayer=new JPanel();
    JPanel jpGaming =new JPanel();
    JPanel jpOpt=new JPanel();
    JSplitPane jsp=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,jpz,jpy);
    public boolean begin=false;  //对战是否开始
    public int color=0;  //轮到哪个玩家走棋，0为红方。1为白方
    Socket sc;
    public ClientAgentThread clientAT;
    public Client(){
        this.initialComponent();  //初始化组件
        this.addListener();  //添加监听
        this.initialState();  //初始化控件状态
        this.initialChessMan();  //初始化棋子
        this.initialFrame();  //初始化窗体
    }

    private void initialChessMan() {
        chessMan[0][0]=new ChessMan(color1,"車",0,0);
        chessMan[1][0]=new ChessMan(color1,"馬",1,0);
        chessMan[2][0]=new ChessMan(color1,"相",2,0);
        chessMan[3][0]=new ChessMan(color1,"仕",3,0);
        chessMan[4][0]=new ChessMan(color1,"帥",4,0);
        chessMan[5][0]=new ChessMan(color1,"仕",5,0);
        chessMan[6][0]=new ChessMan(color1,"相",6,0);
        chessMan[7][0]=new ChessMan(color1,"馬",7,0);
        chessMan[8][0]=new ChessMan(color1,"車",8,0);
        chessMan[1][2]=new ChessMan(color1,"砲",1,2);
        chessMan[7][2]=new ChessMan(color1,"砲",7,2);
        chessMan[0][3]=new ChessMan(color1,"兵",0,3);
        chessMan[2][3]=new ChessMan(color1,"兵",2,3);
        chessMan[4][3]=new ChessMan(color1,"兵",4,3);
        chessMan[6][3]=new ChessMan(color1,"兵",6,3);
        chessMan[8][3]=new ChessMan(color1,"兵",8,3);
        chessMan[0][9]=new ChessMan(color2,"車",0,9);
        chessMan[1][9]=new ChessMan(color2,"馬",1,9);
        chessMan[2][9]=new ChessMan(color2,"象",2,9);
        chessMan[3][9]=new ChessMan(color2,"士",3,9);
        chessMan[4][9]=new ChessMan(color2,"將",4,9);
        chessMan[5][9]=new ChessMan(color2,"士",5,9);
        chessMan[6][9]=new ChessMan(color2,"象",6,9);
        chessMan[7][9]=new ChessMan(color2,"馬",7,9);
        chessMan[8][9]=new ChessMan(color2,"車",8,9);
        chessMan[1][7]=new ChessMan(color2,"炮",1,7);
        chessMan[7][7]=new ChessMan(color2,"炮",7,7);
        chessMan[0][6]=new ChessMan(color2,"卒",0,6);
        chessMan[2][6]=new ChessMan(color2,"卒",2,6);
        chessMan[4][6]=new ChessMan(color2,"卒",4,6);
        chessMan[6][6]=new ChessMan(color2,"卒",6,6);
        chessMan[8][6]=new ChessMan(color2,"卒",8,6);
    }

    public void initialState() {
        this.jbDisConnect.setEnabled(false);//将"断开"按钮设为不可用
        this.jbChallenge.setEnabled(false);//将"挑战"按钮设为不可用
        this.jbYChallenge.setEnabled(false);//将"接受挑战"按钮设为不可用
        this.jbNChallenge.setEnabled(false);//将"拒绝挑战"按钮设为不可用
        this.jbFail.setEnabled(false);//将"认输"按钮设为不可用
        bgm.start();
    }

    public void addListener() {
        this.jbConnect.addActionListener(this);//为"连接"按钮注册事件监听器
        this.jbDisConnect.addActionListener(this);//为"断开"按钮注册事件监听器
        this.jbChallenge.addActionListener(this);//为"挑战"按钮注册事件监听器
        this.jbFail.addActionListener(this);//为"认输"按钮注册事件监听器
        this.jbYChallenge.addActionListener(this);//为"同意挑战"按钮注册事件监听器
        this.jbNChallenge.addActionListener(this);//为"拒绝挑战"按钮注册事件监听器
        this.jbBgm.addActionListener(this);//为"拒绝挑战"按钮注册事件监听器
        this.jcbBgList.addActionListener(this);
    }

    public void initialComponent() {
        jpy.setLayout(null);  //设为空布局
        //此注释块是之前用于手动输入端口号和主机名的界面
//        this.jlHost.setBounds(10,10,50,20);
//        jpy.add(this.jlHost);  //添加"主机名"标签
//        this.jtfHost.setBounds(70,10,80,20);
//        jpy.add(this.jtfHost);  //添加用于输入主机名的文本框
//        this.jlPort.setBounds(10,40,50,20);
//        jpy.add(this.jlPort);  //添加"端口号"标签
//        this.jtfPort.setBounds(70,40,80,20);
//        jpy.add(this.jtfPort);  //添加用于输入端口号的文本框
        //一定要设置空布局！！！

        //用户初始化界面，只有连接和断开选项
        jpPlayer.setLayout(null);
        this.jlNickName.setBounds(10,20,100,20);
        jpPlayer.add(this.jlNickName);
        this.jtfNickName.setBounds(10,45,80,20);
        this.jpPlayer.add(this.jtfNickName);
        this.jbConnect.setBounds(10,70,80,20);
        jpPlayer.add(this.jbConnect);  //添加"连接"按钮
        this.jbDisConnect.setBounds(100,70,80,20);
        jpPlayer.add(this.jbDisConnect);  //添加"断开"按钮
        jpPlayer.setBounds(0,0,200,100);
        jpy.add(this.jpPlayer);

        //游戏设置，音效，背景切换等
        jpOpt.setLayout(null);
        this.jbBgm.setBounds(10,20,120,20);  //背景音乐控制
        this.jcbBgList.setBounds(95,50,100,20);  //背景颜色
        this.jlBg.setBounds(10,50,80,20);
        Vector v=new Vector();
        v.add("白色");
        v.add("蓝色");
        v.add("灰色");
        jcbBgList.setModel(new DefaultComboBoxModel(v));
        jpOpt.add(jbBgm);
        jpOpt.add(jcbBgList);
        jpOpt.add(jlBg);

        //游戏中的操作
        jpGaming.setLayout(null);
        this.jlOnlineList.setBounds(20,35,100,20);
        jpGaming.add(jlOnlineList);
        this.jcbNickList.setBounds(20,55,130,20);
        jpGaming.add(this.jcbNickList);  //添加用于显示当前用户的下拉列表框
        this.jbChallenge.setBounds(10,90,80,20);
        jpGaming.add(this.jbChallenge);  //添加"挑战"按钮
        this.jbFail.setBounds(100,90,80,20);
        jpGaming.add(this.jbFail);  //添加"认输"按钮
        this.jbYChallenge.setBounds(5,115,86,20);
        jpGaming.add(this.jbYChallenge);  //添加"接受挑战"按钮
        this.jbNChallenge.setBounds(100,115,86,20);
        jpGaming.add(this.jbNChallenge);  //添加"拒绝挑战"按钮


        jpGaming.setBounds(0,150,200,200);
        jpy.add(jpGaming);
        jpOpt.setBounds(0,500,200,300);
        jpy.add(jpOpt);
        jpz.setLayout(null);  //将棋盘设为空布局
        jpz.setBounds(0,0,700,700);  //设置大小
        jpGaming.setVisible(false);

    }

    public void initialFrame() {
        this.setTitle("象棋客户端");//设置窗体标题
        Image image=new ImageIcon("ico.jpg").getImage();
        this.setIconImage(image); //设置图标
        jsp.setEnabled(false);
        this.add(this.jsp);//添加JSplitPane
        jsp.setDividerLocation(710);//设置分割线位置及宽度
        jsp.setDividerSize(4);
        this.setBounds(30,30,930,730);//设置窗体大小
        this.setResizable(false);
        this.setVisible(true);//设置可见性
        this.addWindowListener(//为窗体添加监听器
                new WindowAdapter()
                {
                    public void windowClosing(WindowEvent e)
                    {
                        if(clientAT==null)  //客户端代理线程为空，直接退出
                        {
                            System.exit(0);//退出
                            return;
                        }
                        try
                        {
                            if(clientAT.opponent!=null)//正在下棋中
                            {
                                try
                                {
                                    //发送认输信息
                                    clientAT.dout.writeUTF("<#RENSHU#>"+clientAT.opponent);
                                }
                                catch(Exception ee)
                                {
                                    ee.printStackTrace();
                                }
                            }
                            clientAT.dout.writeUTF("<#CLIENT_LEAVE#>");//向服务器发送离开信息
                            clientAT.flag=false;//终止客户端代理线程
                            clientAT=null;

                        }
                        catch(Exception ee)
                        {
                            ee.printStackTrace();
                        }
                        System.exit(0);//退出
                    }

                }
        );
    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==this.jbConnect)
        {//单击"连接"按钮
            this.jbConnect_event();
        }
        else if(e.getSource()==this.jbDisConnect)
        {//当单击"断开"按钮时
            this.jbDisconnect_event();
        }
        else if(e.getSource()==this.jbChallenge)
        {//当单击"挑战"按钮时
            this.jbChallenge_event();
        }
        else if(e.getSource()==this.jbYChallenge)
        {//当单击"同意挑战"按钮时
            this.jbYChallenge_event();
        }
        else if(e.getSource()==this.jbNChallenge)
        {//当单击"拒绝挑战"按钮时
            this.jbNChallenge_event();
        }
        else if(e.getSource()==this.jbFail)
        {//当单击"认输"按钮时
            this.jbFail_event();
        }else if(e.getSource()==this.jbBgm){
            this.jbBgm_event();
        }else if(e.getSource()==this.jcbBgList){
            this.jcbBgList_event();
        }
    }

    private void jcbBgList_event() {
        String bg= (String) jcbBgList.getSelectedItem();
        if(bg.equals("白色")){
            this.jpz.setBackground(color2);
            this.jpGaming.setBackground(color2);
            this.jpPlayer.setBackground(color2);
            this.jpOpt.setBackground(color2);
            this.jpy.setBackground(color2);
        }else if(bg.equals("灰色")){
            this.jpz.setBackground(Color.gray);
            this.jpy.setBackground(Color.gray);
            this.jpGaming.setBackground(Color.gray);
            this.jpPlayer.setBackground(Color.gray);
            this.jpOpt.setBackground(Color.gray);
        }else if(bg.equals("蓝色")){
            this.jpz.setBackground(new Color(152 ,245 ,255));
            this.jpy.setBackground(new Color(152 ,245 ,255));
            this.jpGaming.setBackground(new Color(152 ,245 ,255));
            this.jpPlayer.setBackground(new Color(152 ,245 ,255));
            this.jpOpt.setBackground(new Color(152 ,245 ,255));
        }
    }

    //创建播放背景音乐事件
    private void jbBgm_event() {
        isBgm=!isBgm;
        if(isBgm){
            this.jbBgm.setText("背景音乐：开");

            bgm.resume();
        }else {
            bgm.suspend();

            this.jbBgm.setText("背景音乐：关");
        }

    }

    private void jbFail_event() {
        try{   //发送认输的信息
            this.clientAT.dout.writeUTF("<#ADMIT_DEFEAT#>"+this.clientAT.opponent);
            this.clientAT.opponent=null;//将对手设为空
            this.color=0;//将color设为0
            this.begin=false;//将begin设为false
            this.next();//初始化下一局
//            this.jtfHost.setEnabled(false);//将用于输入主机名的文本框设为不可用
//            this.jtfPort.setEnabled(false);//将用于输入端口号的文本框设为不可用
            this.jtfNickName.setEnabled(false);//将用于输入昵称的文本框设为不可用
            this.jbConnect.setEnabled(false);//将"连接"按钮设为不可用
            this.jbDisConnect.setEnabled(true);//将"断开"按钮设为可用
            this.jbChallenge.setEnabled(true);//将"挑战"按钮设为可用
            this.jbYChallenge.setEnabled(false);//将"接受挑战"按钮设为不可用
            this.jbNChallenge.setEnabled(false);//将"拒绝挑战"按钮设为不可用
            this.jbFail.setEnabled(false);//将"认输"按钮设为不可用
        }
        catch(Exception ee){ee.printStackTrace();}
    }

    public void next() {
        for(int i=0;i<9;i++){//将棋子数组都置空
            for(int j=0;j<10;j++){
                this.chessMan[i][j]=null;
            }
        }
        this.begin=false;
        this.initialChessMan();//重新初始化棋子
        this.repaint();//重绘
    }

    private void jbNChallenge_event() {
        try{   //发送拒绝挑战的信息
            this.clientAT.dout.writeUTF("<#REFUSE#>"+this.clientAT.opponent);
            this.clientAT.opponent=null;//将对手设为空
//            this.jtfHost.setEnabled(false);//将用于输入主机名的文本框设为不可用
//            this.jtfPort.setEnabled(false);//将用于输入端口号的文本框设为不可用
            this.jtfNickName.setEnabled(false);//将用于输入昵称的文本框设为不可用
            this.jbConnect.setEnabled(false);//将"连接"按钮设为不可用
            this.jbDisConnect.setEnabled(true);//将"断开"按钮设为可用
            this.jbChallenge.setEnabled(true);//将"挑战"按钮设为可用
            this.jbYChallenge.setEnabled(false);//将"接受挑战"按钮设为不可用
            this.jbNChallenge.setEnabled(false);//将"拒绝挑战"按钮设为不可用
            this.jbFail.setEnabled(false);//将"认输"按钮设为不可用
        }
        catch(Exception ee){ee.printStackTrace();}
    }

    private void jbYChallenge_event() {
        try{	//发送同意挑战的信息
            this.clientAT.dout.writeUTF("<#ACCEPT#>"+this.clientAT.opponent);
            this.begin=false;//将begin设为false
            this.color=1;//将color设为1
//            this.jtfHost.setEnabled(false);//将用于输入主机名的文本框设为不可用
//            this.jtfPort.setEnabled(false);//将用于输入端口号的文本框设为不可用
            this.jtfNickName.setEnabled(false);//将用于输入昵称的文本框设为不可用
            this.jbConnect.setEnabled(false);//将"连接"按钮设为不可用
            this.jbDisConnect.setEnabled(!true);//将"断开"按钮设为不可用
            this.jbChallenge.setEnabled(!true);//将"挑战"按钮设为不可用
            this.jbYChallenge.setEnabled(false);//将"接受挑战"按钮设为不可用
            this.jbNChallenge.setEnabled(false);//将"拒绝挑战"按钮设为不可用
            this.jbFail.setEnabled(!false);//将"认输"按钮设为可用
        }
        catch(Exception ee){ee.printStackTrace();}
    }

    private void jbChallenge_event() {
        //获得用户选中的挑战对象
        Object o=this.jcbNickList.getSelectedItem();
        if(o==null||((String)o).equals("")) {
            JOptionPane.showMessageDialog(this,"请选择你的对手！","错误",
                    JOptionPane.ERROR_MESSAGE);//当未选中挑战对象，给出错误提示信息
        }
        else{
            String name2=(String)this.jcbNickList.getSelectedItem();//获得挑战对象
            try{
//                this.jtfHost.setEnabled(false);//将用于输入主机名的文本框设为不可用
//                this.jtfPort.setEnabled(false);//将用于输入端口号的文本框设为不可用
                this.jtfNickName.setEnabled(false);//将用于输入昵称的文本框设为不可用
                this.jbConnect.setEnabled(false);//将"连接"按钮设为不可用
                this.jbDisConnect.setEnabled(!true);//将"断开"按钮设为不可用
                this.jbChallenge.setEnabled(!true);//将"挑战"按钮设为不可用
                this.jbYChallenge.setEnabled(false);//将"接受挑战"按钮设为不可用
                this.jbNChallenge.setEnabled(false);//将"拒绝挑战"按钮设为不可用
                this.jbFail.setEnabled(false);//将"认输"按钮设为不可用
                this.clientAT.opponent=name2;//设置挑战对象
                this.begin=true;//将caiPan设为true
                this.color=0;//将color设为0
                this.clientAT.dout.writeUTF("<#CHALLENGE#>"+name2);//发送挑战信息
                JOptionPane.showMessageDialog(this,"已提出挑战,请等待恢复...","提示",
                        JOptionPane.INFORMATION_MESSAGE);//给出信息发出的提示信息
            }
            catch(Exception ee){ee.printStackTrace();}
        }
    }

    private void jbDisconnect_event() {
        //对单击"断开"按钮事件的业务处理代码
        try
        {
            this.clientAT.dout.writeUTF("<#CLIENT_LEAVE#>");//向服务器发送离开的信息
            this.clientAT.flag=false;//终止客户端代理线程
            this.clientAT=null;
            this.jpGaming.setVisible(false);
//            this.jtfHost.setEnabled(!false);//将用于输入主机名的文本框设为可用
//            this.jtfPort.setEnabled(!false);//将用于输入端口号的文本框设为可用
            this.jtfNickName.setEnabled(!false);//将用于输入昵称的文本框设为可用
            this.jbConnect.setEnabled(!false);//将"连接"按钮设为可用
            this.jbDisConnect.setEnabled(!true);//将"断开"按钮设为不可用
            this.jbChallenge.setEnabled(!true);//将"挑战"按钮设为不可用
            this.jbYChallenge.setEnabled(false);//将"接受挑战"按钮设为不可用
            this.jbNChallenge.setEnabled(false);//将"拒绝挑战"按钮设为不可用
            this.jbFail.setEnabled(false);//将"认输"按钮设为不可用
        }
        catch(Exception ee)
        {
            ee.printStackTrace();
        }
    }

    private void jbConnect_event() {
        //对单击"连接"按钮事件的业务处理代码
        int port=8888;
//        try
//        {//获得用户输入的断口号并转化为整型
//            port=Integer.parseInt(this.jtfPort.getText().trim());
//        }
//        catch(Exception ee)
//        {//不是整数，给出错误提示
//            JOptionPane.showMessageDialog(this,"端口号只能是整数","错误",
//                    JOptionPane.ERROR_MESSAGE);
//            return;
//        }
//        if(port>65535||port<0)
//        {//端口号不合法，给出错误提示
//            JOptionPane.showMessageDialog(this,"端口号只能是0-65535的整数","错误",
//                    JOptionPane.ERROR_MESSAGE);
//            return;
//        }
        String name=this.jtfNickName.getText().trim();//获得昵称
        if(name.length()==0)
        {//昵称为空，给出错误提示信息
            JOptionPane.showMessageDialog(this,"玩家姓名不能为空","错误",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        try
        {
            sc=new Socket("39.105.157.69",port);//创建Socket对象
            clientAT=new ClientAgentThread(this);//创建客户端代理线程
            clientAT.start();//启动客户端代理线程
            this.setTitle("欢迎您，"+name);
//            this.jtfHost.setEnabled(false);//将用于输入主机名的文本框设为不可用
//            this.jtfPort.setEnabled(false);//将用于输入端口号的文本框设为不可用
            jpGaming.setVisible(true);
            this.jtfNickName.setEnabled(false);//将用于输入昵称的文本框设为不可用
            this.jbConnect.setEnabled(false);//将"连接"按钮设为不可用
            this.jbDisConnect.setEnabled(true);//将"断开"按钮设为可用
            this.jbChallenge.setEnabled(true);//将"挑战"按钮设为可用
            this.jbYChallenge.setEnabled(false);//将"接受挑战"按钮设为不可用
            this.jbNChallenge.setEnabled(false);//将"拒绝挑战"按钮设为不可用
            this.jbFail.setEnabled(false);//将"认输"按钮设为不可用
            JOptionPane.showMessageDialog(this,"已连接到服务器","提示",
                    JOptionPane.INFORMATION_MESSAGE);//连接成功，给出提示信息
        }
        catch(Exception ee)
        {
            JOptionPane.showMessageDialog(this,"连接服务器失败","错误",
                    JOptionPane.ERROR_MESSAGE);//连接失败，给出提示信息
            return;
        }
    }

    public static void main(String[] args) {
        new Client();
    }

}
