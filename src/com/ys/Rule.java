package com.ys;

import com.ys.client.ChessMan;

public class Rule {
    ChessMan[][] chessMEN;
    boolean canMove=false;
    int i;
    int j;
    public Rule(ChessMan[][] chessMEN){
        this.chessMEN=chessMEN;
    }
    public boolean canMove(int startI,int startJ,int endI,int endJ,String name){  //name 为走的棋子名
        int maxI;
        int maxJ;
        int minI;
        int minJ;
        canMove=true;
        if(startI>=endI)//确定起始坐标的大小关系
        {
            maxI=startI;
            minI=endI;
        }
        else//确定maxI和minI
        {
            maxI=endI;
            minI=startI;
        }
        if(startJ>=endJ)//确定endJ和startJ
        {
            maxJ=startJ;
            minJ=endJ;
        }
        else
        {
            maxJ=endJ;
            minJ=startJ;
        }
        if(name.equals("車"))//如果是"車"
        {
            this.ju(maxI,minI,maxJ,minJ);
        }
        else if(name.equals("馬"))//如果是"馬"
        {
            this.ma(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
        }
        else if(name.equals("相"))//如果是"相"
        {
            this.xiang1(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
        }
        else if(name.equals("象"))//如果是"象"
        {
            this.xiang2(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
        }
        else if(name.equals("士")||name.equals("仕")) //如果是"士"
        {
            this.shi(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
        }
        else if(name.equals("帥")||name.equals("將"))//如果是"将"
        {
            this.jiang(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
        }
        else if(name.equals("炮")||name.equals("砲"))//如果是"炮"
        {
            this.pao(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
        }
        else if(name.equals("兵"))//如果是"兵"
        {
            this.bing(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);

        }
        else if(name.equals("卒"))//如果是"卒"
        {
            this.zu(maxI,minI,maxJ,minJ,startI,startJ,endI,endJ);
        }
        return canMove;

    }

    private void zu(int maxI, int minI, int maxJ, int minJ, int startI, int startJ, int endI, int endJ) {
        if(startJ>4)
        {//如果还没有过河
            if(startI!=endI)
            {//如果不是向前走
                canMove=false;//不可以走
            }
            if(endJ-startJ!=-1)//如果走的不是一格
            {
                canMove=false;
            }
        }
        else
        {//如果已经过河
            if(startI==endI)
            {//如果走的是竖线
                if(endJ-startJ!=-1)
                {//如果走的不是一格
                    canMove=false;//不可以走
                }
            }
            else if(startJ==endJ)
            {//如果走的是横线
                if(maxI-minI!=1)
                {//如果走的不是一格
                    canMove=false;//不可以走
                }
            }
            else if(startI!=endI&&startJ!=endJ)
            {//如果走的既不是竖线，也不是横线，则不可以走
                canMove=false;
            }
        }
    }

    private void bing(int maxI, int minI, int maxJ, int minJ, int startI, int startJ, int endI, int endJ) {
        if(startJ<5)//如果还没有过河
        {
            if(startI!=endI)//如果不是向前走
            {
                canMove=false;//不可以走
            }
            if(endJ-startJ!=1)//如果走的不是一格
            {
                canMove=false;//不可以走
            }
        }
        else
        {//如果已经过河
            if(startI==endI)
            {//如果走的是竖线
                if(endJ-startJ!=1)//如果走的不是一格
                {
                    canMove=false;//不可以走
                }
            }
            else if(startJ==endJ)
            {//如果走的是横线
                if(maxI-minI!=1)
                {//如果走的不是一格
                    canMove=false;//不可以走
                }
            }
            else if(startI!=endI&&startJ!=endJ)
            {//如果走的既不是竖线，也不是横线，则不可以走
                canMove=false;
            }
        }
    }

    private void pao(int maxI, int minI, int maxJ, int minJ, int startI, int startJ, int endI, int endJ) {
        if(maxI==minI)//如果走的竖线
        {
            if(chessMEN[endI][endJ]!=null)//如果终点有棋子
            {
                int count=0;
                for(j=minJ+1;j<maxJ;j++)
                {
                    if(chessMEN[minI][j]!=null)//判断起点与终点之间有几个棋子
                    {
                        count++;
                    }
                }
                if(count!=1)
                {//如果不是一个棋子
                    canMove=false;//不可以走
                }
            }
            else if(chessMEN[endI][endJ]==null)//如果终点没有棋子
            {
                for(j=minJ+1;j<maxJ;j++)
                {
                    if(chessMEN[minI][j]!=null)//如果起止点之间有棋子
                    {
                        canMove=false;//不可以走
                        break;
                    }
                }
            }
        }
        else if(maxJ==minJ)//如果走的是横线
        {
            if(chessMEN[endI][endJ]!=null)//如果终点有棋子
            {
                int count=0;
                for(i=minI+1;i<maxI;i++)
                {
                    if(chessMEN[i][minJ]!=null)//判断起点与终点之间有几个棋子
                    {
                        count++;
                    }
                }
                if(count!=1)//如果不是一个棋子
                {
                    canMove=false;//不可以走
                }
            }
            else if(chessMEN[endI][endJ]==null)//如果终点没有棋子
            {
                for(i=minI+1;i<maxI;i++)
                {
                    if(chessMEN[i][minJ]!=null)//如果起止点之间有棋子
                    {
                        canMove=false;//不可以走
                        break;
                    }
                }
            }
        }
        else if(maxJ!=minJ&&maxI!=minI)
        {//如果走的既不是竖线，也不是横线，则不可以走
            canMove=false;
        }
    }

    private void jiang(int maxI, int minI, int maxJ, int minJ, int startI, int startJ, int endI, int endJ) {
        int a=maxI-minI;
        int b=maxJ-minJ;//获得X,Y坐标的差
        if((a==1&&b==0)||(a==0&&b==1))
        {//如果走的是一小格
            if(startJ>4)//如果是下方的将
            {
                if(endJ<7)//如果越界
                {
                    canMove=false;//不可以走
                }
            }
            else
            {//如果是上方的将
                if(endJ>2)//如果越界
                {
                    canMove=false;//不可以走
                }
            }
            if(endI>5||endI<3)//如果左右越界，不可以走
            {
                canMove=false;
            }
        }
        else
        {//如果走的不是一格，不可以走
            canMove=false;
        }
    }

    private void shi(int maxI, int minI, int maxJ, int minJ, int startI, int startJ, int endI, int endJ) {
        int a=maxI-minI;
        int b=maxJ-minJ;//获得X,Y坐标的差
        if(a==1&&b==1)//如果是小斜线
        {
            if(startJ>4)//如果是下方的士
            {
                if(endJ<7)
                {
                    canMove=false;//如果下方的士越界，不可以走
                }
            }
            else
            {//如果是上方的仕
                if(endJ>2)
                {
                    canMove=false;//如果上方的仕越界，不可以走
                }
            }
            if(endI>5||endI<3)//如果左右越界，则不可以走
            {
                canMove=false;
            }
        }
        else
        {//如果不是小斜线
            canMove=false;//不可以走
        }
    }

    private void xiang2(int maxI, int minI, int maxJ, int minJ, int startI, int startJ, int endI, int endJ) {
        int a=maxI-minI;
        int b=maxJ-minJ;//获得X,Y坐标的差
        if(a==2&&b==2)//如果是"田"字
        {
            if(endJ<5)//如果过河了
            {
                canMove=false;//不可以走
            }
            if(chessMEN[(maxI+minI)/2][(maxJ+minJ)/2]!=null)//如果"田"字中间有棋子
            {
                canMove=false;//不可以走
            }
        }
        else
        {
            canMove=false;//如果不是"田"字，不可以走
        }
    }

    private void xiang1(int maxI, int minI, int maxJ, int minJ, int startI, int startJ, int endI, int endJ) {
        int a=maxI-minI;
        int b=maxJ-minJ;//获得X,Y坐标的差
        if(a==2&&b==2)//如果是"田"字
        {
            if(endJ>4)//如果过河了
            {
                canMove=false;//不可以走
            }
            if(chessMEN[(maxI+minI)/2][(maxJ+minJ)/2]!=null)//如果"田"字中间有棋子
            {
                canMove=false;//不可以走
            }
        }
        else
        {
            canMove=false;//如果不是"田"字，不可以走
        }
    }

    private void ma(int maxI, int minI, int maxJ, int minJ, int startI, int startJ, int endI, int endJ) {
        int a=maxI-minI;
        int b=maxJ-minJ;//获得两坐标点之间的差
        if(a==1&&b==2)//如果是竖着的"日"字
        {
            if(startJ>endJ)//如果是从下向上走
            {
                if(chessMEN[startI][startJ-1]!=null)//如果马腿处有棋子
                {
                    canMove=false;//不可以走
                }
            }
            else
            {//如果是从上往下走
                if(chessMEN[startI][startJ+1]!=null)//如果马腿处有棋子
                {
                    canMove=false;//不可以走
                }
            }
        }
        else if(a==2&&b==1)//如果是横着的"日"
        {
            if(startI>endI)//如果是从右往左走
            {
                if(chessMEN[startI-1][startJ]!=null)//如果马腿处有棋子
                {
                    canMove=false;//不可以走
                }
            }
            else
            {//如果是从左往右走
                if(chessMEN[startI+1][startJ]!=null)//如果马腿处有棋子
                {
                    canMove=false;//不可以走
                }
            }
        }
        else if(!((a==2&&b==1)||(a==1&&b==2)))//如果不时"日"字
        {
            canMove=false;//不可以走
        }
    }

    private void ju(int maxI, int minI, int maxJ, int minJ) {
        if(maxI==minI)//如果在一条横线上
        {
            for(j=minJ+1;j<maxJ;j++)
            {
                if(chessMEN[maxI][j]!=null)//如果中间有棋子
                {
                    canMove=false;//不可以走棋
                    break;
                }
            }
        }
        else if(maxJ==minJ)//如果在一条竖线上
        {
            for(i=minJ+1;i<maxJ;i++)
            {
                if(chessMEN[i][maxJ]!=null)//如果中间有棋子
                {
                    canMove=false;//不可以走棋
                    break;
                }
            }
        }
        else if(maxI!=minI&&maxJ!=minJ)//如果不在同一条线上
        {
            canMove=false;//不可以走棋
        }
    }

}
