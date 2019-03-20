package com.ys;

import com.ys.client.ChessMan;
import com.ys.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class ChessBoard extends JPanel implements MouseListener {
    private int width;  //棋盘两条线之间的距离
    boolean focus;  //棋子是否被选中
    int jiang1_i=4;  //帅和将的位置
    int jiang1_j=0;
    int jiang2_i=4;
    int jiang2_j=9;
    int startI=-1;  //棋子的起始位置
    int endI=-1;
    int startJ=-1;
    int endJ=-1;
    public ChessMan chessMan[][];
    Client client=null;  //客户端窗口
    Rule rule;
    public ChessBoard(ChessMan chessMan[][],int width,Client client){
        this.client=client;
        this.chessMan=chessMan;
        this.width=width;
        rule=new Rule(chessMan);
        this.addMouseListener(this);
        this.setBackground(new Color(205 ,170, 125));
        this.setBounds(0,0,700,700);
        this.setLayout(null);
    }
    public void paint(Graphics g1){
        Graphics2D g=(Graphics2D)g1;
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        Color c=g.getColor();  //获得画笔颜色
        g.setColor(client.bgColor);  //设置为背景色

        g.fill3DRect(60,30,580,630,false);

        g.setColor(Color.black);
        g.setStroke(new BasicStroke(3));
        g.drawLine(105,75,595,75);  //绘制棋盘周围的四条线
        g.drawLine(105,625,595,625);
        g.drawLine(105,75,105,625);
        g.drawLine(595,75,595,625);
        g.setStroke(new BasicStroke(1));
        for(int i=80;i<=620;i+=60){  //绘制中间的横线
            g.drawLine(110,i,590,i);
        }
        g.drawLine(110,80,110,620);  //绘制最左和最右的横线
        g.drawLine(590,80,590,620);
        for(int i=170;i<=530;i+=60){  //绘制竖线
            g.drawLine(i,80,i,320);
            g.drawLine(i,380,i,620);
        }
        g.drawLine(290,80,410,200);  //绘制将周围的斜线
        g.drawLine(290,200,410,80);
        g.drawLine(290,500,410,620);
        g.drawLine(290,620,410,500);

        this.smallLine(g,1,2);//绘制红炮所在位置的标志
        this.smallLine(g,7,2);//绘制红炮所在位置的标志
        this.smallLine(g,0,3);//绘制兵所在位置的标志
        this.smallLine(g,2,3);//绘制兵所在位置的标志
        this.smallLine(g,4,3);//绘制兵所在位置的标志
        this.smallLine(g,6,3);//绘制兵所在位置的标志
        this.smallLine(g,8,3);//绘制兵所在位置的标志
        this.smallLine(g,0,6);//绘制卒所在位置的标志
        this.smallLine(g,2,6);//绘制卒所在位置的标志
        this.smallLine(g,4,6);//绘制卒所在位置的标志
        this.smallLine(g,6,6);//绘制卒所在位置的标志
        this.smallLine(g,8,6);//绘制卒所在位置的标志
        this.smallLine(g,1,7);//绘制白炮所在位置的标志
        this.smallLine(g,7,7);//绘制白炮所在位置的标志

        g.setColor(Color.black);
        Font font1=new Font("宋体",Font.BOLD,50);//设置字体
        g.setFont(font1);
        g.drawString("楚 河",170,365);//绘制楚河汉界
        g.drawString("漢 界",400,365);
        Font font=new Font("宋体",Font.BOLD,30);
        g.setFont(font);//设置字体
        for(int i=0;i<9;i++){
            for(int j=0;j<10;j++){  //绘制棋子
                if(chessMan[i][j]!=null){
                    if(this.chessMan[i][j].isFocus()!=false){//是否被选中
                        g.setColor(Client.focusBg);  //选中后的背景色
                        g.fillOval(110+i*60-25,80+j*60-25,50,50);  //绘制该棋子
                        g.setColor(Client.focusChar);  //字符的颜色
                    }
                    else{
                        g.fillOval(110+i*60-25,80+j*60-25,50,50);//绘制该棋子
                        g.setColor(chessMan[i][j].getColor());//设置画笔颜色
                    }
                    g.drawString(chessMan[i][j].getName(),110+i*60-15,80+j*60+10);
                    g.setColor(Color.black);//设为黑色
                }
            }
        }
        g.setColor(c);//还原画笔颜色
    }

    private void smallLine(Graphics2D g, int i, int j) {
        int x=110+60*i;//计算坐标
        int y=80+60*j;
        if(i>0){//绘制左上方的标志
            g.drawLine(x-3,y-3,x-20,y-3);g.drawLine(x-3,y-3,x-3,y-20);
        }
        if(i<8){//绘制右上方的标志
            g.drawLine(x+3,y-3,x+20,y-3);g.drawLine(x+3,y-3,x+3,y-20);
        }
        if(i>0){//绘制左下方的标志
            g.drawLine(x-3,y+3,x-20,y+3);g.drawLine(x-3,y+3,x-3,y+20);
        }
        if(i<8){//绘制右下方的标志
            g.drawLine(x+3,y+3,x+20,y+3);g.drawLine(x+3,y+3,x+3,y+20);
        }
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if(this.client.begin==true){//判断是否轮到该玩家走棋
            int i=-1,j=-1;
            int[] pos=getPos(e);
            i=pos[0];
            j=pos[1];
            if(i>=0&&i<=8&&j>=0&&j<=9){//如果在棋盘范围内
                if(focus==false){//如果其面没有选中棋子
                    this.noFocus(i,j);
                }
                else{//如果以前选中过棋子
                    if(chessMan[i][j]!=null){//如果该处有棋子
                        if(chessMan[i][j].getColor()==chessMan[startI][startJ].getColor())
                        {//如果是自己的棋子
                            chessMan[startI][startJ].setFocus(false);
                            chessMan[i][j].setFocus(true);//更改选中对象
                            startI=i;startJ=j;//保存修改
                        }
                        else{//如果是对方棋子
                            endI=i;//保存该点
                            endJ=j;
                            String name=chessMan[startI][startJ].getName();//获得该棋子的名字
                            //看是否可以移动
                            boolean canMove=rule.canMove(startI,startJ,endI,endJ,name);
                            if(canMove)//如果可以移动
                            {
                                try{//将该移动信息发送给对方
                                    this.client.clientAT.dout.writeUTF("<#MOVE#>"+
                                            this.client.clientAT.opponent+startI+startJ+endI+endJ);
                                    this.client.begin=false;
                                    if(chessMan[endI][endJ].getName().equals("帥")||
                                            chessMan[endI][endJ].getName().equals("將"))
                                    {//如果终点处是对方的"将"
                                        this.success();
                                    }
                                    else{//如果终点不是对方的"将"
                                        this.noJiang();
                                    }
                                }
                                catch(Exception ee){ee.printStackTrace();}
                            }
                        }
                    }
                    else{//如果没有棋子
                        endI=i;
                        endJ=j;//保存终点
                        String name=chessMan[startI][startJ].getName();//获得该棋的名字
                        boolean canMove=rule.canMove(startI,startJ,endI,endJ,name);//判断是否可走
                        if(canMove){//如果可以移动
                            this.noChessman();
                        }
                    }
                }
            }
            this.client.repaint();//重绘
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void noChessman() {
        try{//将该移动信息发送给对方
            this.client.clientAT.dout.writeUTF("<#MOVE#>"+this.client.clientAT.opponent+startI+startJ+endI+endJ);
            this.client.begin=false;
            chessMan[endI][endJ]=chessMan[startI][startJ];
            chessMan[startI][startJ]=null;//走棋
            chessMan[endI][endJ].setFocus(false);//将该棋设为非选中状态
            this.client.repaint();//重绘
            if(chessMan[endI][endJ].getName().equals("帥")){//如果移动的是"帥"
                jiang1_i=endI;//更新"帥"的位置坐标
                jiang1_j=endJ;
            }
            else if(chessMan[endI][endJ].getName().equals("將")){//如果移动的是"將"
                jiang2_i=endI;//更新"將"的位置坐标
                jiang2_j=endJ;
            }
            if(jiang1_i==jiang2_i)//如果"將"和"帥"在一条竖线上
            {
                int count=0;
                for(int jiang_j=jiang1_j+1;jiang_j<jiang2_j;jiang_j++){//遍历这条竖线
                    if(chessMan[jiang1_i][jiang_j]!=null){
                        count++;break;
                    }
                }
                if(count==0){//如果等于零则照将
                    JOptionPane.showMessageDialog(this.client,"照将！！！你失败了！！！","提示",
                            JOptionPane.INFORMATION_MESSAGE);//给出失败信息
                    this.client.clientAT.opponent=null;
                    this.client.color=0;//还原棋盘，进入下一盘
                    this.client.begin=false;
                    this.client.next();//进入下一盘
                    //this.client.jtfHost.setEnabled(false);
                    //this.client.jtfPort.setEnabled(false);
                    this.client.jtfNickName.setEnabled(false);//设置各控件状态
                    this.client.jbConnect.setEnabled(false);
                    this.client.jbDisConnect.setEnabled(true);
                    this.client.jbChallenge.setEnabled(true);
                    this.client.jbYChallenge.setEnabled(false);
                    this.client.jbNChallenge.setEnabled(false);
                    this.client.jbFail.setEnabled(false);
                    jiang1_i=4;//"帥"的i坐标
                    jiang1_j=0;//"帥"的j坐标
                    jiang2_i=4;//"將"的i坐标
                    jiang2_j=9;//"將"的j坐标
                }
            }
            startI=-1;
            startJ=-1;//还原保存点
            endI=-1;
            endJ=-1;
            focus=false;
        }
        catch(Exception ee){ee.printStackTrace();}
    }

    private void noJiang() {
        chessMan[endI][endJ]=chessMan[startI][startJ];
        chessMan[startI][startJ]=null;//走棋
        chessMan[endI][endJ].setFocus(false);//将该棋设为非选中状态
        this.client.repaint();//重绘
        if(chessMan[endI][endJ].getName().equals("帥")){//如果移动的是"帥"
            jiang1_i=endI;//更新"帥"的位置坐标
            jiang1_j=endJ;
        }
        else if(chessMan[endI][endJ].getName().equals("將")){//如果移动的是"將"
            jiang2_i=endI;//更新"將"的位置坐标
            jiang2_j=endJ;
        }
        if(jiang1_i==jiang2_i){//如果"將"和"帥"在一条竖线上
            int count=0;
            for(int jiang_j=jiang1_j+1;jiang_j<jiang2_j;jiang_j++){//遍历这条竖线
                if(chessMan[jiang1_i][jiang_j]!=null){
                    count++;break;
                }
            }
            if(count==0){//如果等于零则照将
                JOptionPane.showMessageDialog(this.client,"照将！！！你失败了！！！","提示",
                        JOptionPane.INFORMATION_MESSAGE);//给出失败信息
                this.client.clientAT.opponent=null;
                this.client.color=0;//还原棋盘，进入下一盘
                this.client.begin=false;
                this.client.next();//进入下一盘
               // this.client.jtfHost.setEnabled(false);
                //this.client.jtfPort.setEnabled(false);//设置各控件状态
                this.client.jtfNickName.setEnabled(false);
                this.client.jbConnect.setEnabled(false);
                this.client.jbDisConnect.setEnabled(true);
                this.client.jbChallenge.setEnabled(true);
                this.client.jbYChallenge.setEnabled(false);
                this.client.jbNChallenge.setEnabled(false);
                this.client.jbFail.setEnabled(false);
                jiang1_i=4;//"帥"的i坐标
                jiang1_j=0;//"帥"的j坐标
                jiang2_i=4;//"將"的i坐标
                jiang2_j=9;//"將"的j坐标
            }
        }
        startI=-1;
        startJ=-1;//还原保存点
        endI=-1;
        endJ=-1;
        focus=false;
    }

    private void success() {
        chessMan[endI][endJ]=chessMan[startI][startJ];//吃掉该棋子
        chessMan[startI][startJ]=null;//将原来的位置设为空
        this.client.repaint();//重绘
        JOptionPane.showMessageDialog(this.client,"恭喜您，您获胜了","提示",
                JOptionPane.INFORMATION_MESSAGE);//给出获胜信息
        this.client.clientAT.opponent=null;
        this.client.color=0;
        this.client.begin=false;
        this.client.next();//还原棋盘，进入下一盘
        //this.client.jtfHost.setEnabled(false);
        //this.client.jtfPort.setEnabled(false);//设置各控件的状态
        this.client.jtfNickName.setEnabled(false);
        this.client.jbConnect.setEnabled(false);
        this.client.jbDisConnect.setEnabled(true);
        this.client.jbChallenge.setEnabled(true);
        this.client.jbYChallenge.setEnabled(false);
        this.client.jbNChallenge.setEnabled(false);
        this.client.jbFail.setEnabled(false);
        startI=-1;//还原保存点
        startJ=-1;
        endI=-1;
        endJ=-1;
        jiang1_i=4;//"帥"的i坐标
        jiang1_j=0;//"帥"的j坐标
        jiang2_i=4;//"將"的i坐标
        jiang2_j=9;//"將"的j坐标
        focus=false;

    }

    private void noFocus(int i, int j) {
        if(this.chessMan[i][j]!=null)//如果该位置有棋子
        {
            if(this.client.color==0)//如果是红方
            {
                if(this.chessMan[i][j].getColor().equals(Client.color1))//如果棋子是红色
                {
                    this.chessMan[i][j].setFocus(true);//将该棋子设为选中状态
                    focus=true;//将focus设为true
                    startI=i;//保存该坐标点
                    startJ=j;
                }
            }
            else//如果是白方
            {
                if(this.chessMan[i][j].getColor().equals(Client.color2))//如果该棋子是白色
                {
                    this.chessMan[i][j].setFocus(true);//将该棋子设为选中状态
                    focus=true;//将focus设为true
                    startI=i;//保存该坐标点
                    startJ=j;
                }
            }
        }
    }

    private int[] getPos(MouseEvent e) {
        int[] pos=new int[2];
        pos[0]=-1;
        pos[1]=-1;
        Point p=e.getPoint();//获得事件发生的坐标点
        double x=p.getX();
        double y=p.getY();
        if(Math.abs((x-110)/1%60)<=25){//获得对应于数组x下标的位置
            pos[0]=Math.round((float)(x-110))/60;
        }
        else if(Math.abs((x-110)/1%60)>=35){
            pos[0]=Math.round((float)(x-110))/60+1;
        }
        if(Math.abs((y-80)/1%60)<=25){//获得对应于数组y下标的位置
            pos[1]=Math.round((float)(y-80))/60;
        }
        else if(Math.abs((y-80)/1%60)>=35){
            pos[1]=Math.round((float)(y-80))/60+1;
        }
        return pos;
    }
    public void move(int startI,int startJ,int endI,int endJ){
        if(chessMan[endI][endJ]!=null&&(chessMan[endI][endJ].getName().equals("帥")||
                chessMan[endI][endJ].getName().equals("將"))){//如果"将"被吃了
            chessMan[endI][endJ]=chessMan[startI][startJ];
            chessMan[startI][startJ]=null;//走棋
            this.client.repaint();//重绘
            JOptionPane.showMessageDialog(this.client,"很遗憾，您失败了！！！","提示",
                    JOptionPane.INFORMATION_MESSAGE);//给出失败信息
            this.client.clientAT.opponent=null;
            this.client.color=0;//还原棋盘进入下一盘
            this.client.begin=false;
            this.client.next();
           // this.client.jtfHost.setEnabled(false);//还原各个控件的状态
           // this.client.jtfPort.setEnabled(false);
            this.client.jtfNickName.setEnabled(false);
            this.client.jbConnect.setEnabled(false);
            this.client.jbDisConnect.setEnabled(true);
            this.client.jbChallenge.setEnabled(true);
            this.client.jbYChallenge.setEnabled(false);
            this.client.jbNChallenge.setEnabled(false);
            this.client.jbFail.setEnabled(false);
            jiang1_i=4;//"帥"的i坐标
            jiang1_j=0;//"帥"的j坐标
            jiang2_i=4;//"將"的i坐标
            jiang2_j=9;//"將"的j坐标
        }
        else{//如果不是"将"
            chessMan[endI][endJ]=chessMan[startI][startJ];
            chessMan[startI][startJ]=null;//走棋
            this.client.repaint();//重绘
            if(chessMan[endI][endJ].getName().equals("帥")){
                jiang1_i=endI;//如果是"帥"
                jiang1_j=endJ;
            }
            else if(chessMan[endI][endJ].getName().equals("將")){
                jiang2_i=endI;//如果是"將"
                jiang2_j=endJ;
            }
            if(jiang1_i==jiang2_i){//如果两将在一条线上
                int count=0;
                for(int jiang_j=jiang1_j+1;jiang_j<jiang2_j;jiang_j++){
                    if(chessMan[jiang1_i][jiang_j]!=null){//有棋子
                        count++;break;
                    }
                }
                if(count==0){
                    JOptionPane.showMessageDialog(this.client,"对方照将！！！你胜利了！！！",
                            "提示",JOptionPane.INFORMATION_MESSAGE);//给出失败信息
                    this.client.clientAT.opponent=null;
                    this.client.color=0;//还原棋盘，进入下一盘
                    this.client.begin=false;
                    this.client.next();
                    //this.client.jtfHost.setEnabled(false);
                   // this.client.jtfPort.setEnabled(false);//设置各空间位置
                    this.client.jtfNickName.setEnabled(false);
                    this.client.jbConnect.setEnabled(false);
                    this.client.jbDisConnect.setEnabled(true);
                    this.client.jbChallenge.setEnabled(true);
                    this.client.jbYChallenge.setEnabled(false);
                    this.client.jbNChallenge.setEnabled(false);
                    this.client.jbFail.setEnabled(false);
                    jiang1_i=4;//"帥"的i坐标
                    jiang1_j=0;//"帥"的j坐标
                    jiang2_i=4;//"將"的i坐标
                    jiang2_j=9;//"將"的j坐标
                }
            }
        }
        this.client.repaint();//重绘
    }


}













