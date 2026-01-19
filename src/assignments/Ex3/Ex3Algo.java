package assignments.Ex3;

import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 *
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo{
	private int _count;
private  Index2D pacPosition;
private int blue;
private int green;
private int black;
private int pink;
private  Map board1;
private  ArrayList<Pixel2D> points;
private Queue<Pixel2D> pathToP;
private boolean started = false;
private boolean dangerous = false;
private GhostCL[] ghosts;
private int code;
private Map dangerousMap;

private ArrayList<Pixel2D> safetyMap;



    public Ex3Algo() {_count=0;}
	@Override
	/**
	 *  Add a short description for the algorithm as a String.
	 */
	public String getInfo() {
		return null;
	}
	@Override
	/**
	 * This ia the main method - that you should design, implement and test.
	 */
	public int move(PacmanGame game) {
        if(!started) {
            if (Ex3Main.getCMD()!=null && Ex3Main.getCMD() == ' '){this.started = true;}
        }
        if(started) {

            if (_count == 0 || _count == 300) {
                int code = 0;
                this.code = code;
                int[][] board = game.getGame(code);
                printBoard(board);
                this.board1 = new Map(board);
                this.board1.setCyclic(true);
                this.blue = Game.getIntColor(Color.BLUE, code);
                this.pink = Game.getIntColor(Color.PINK, code);
                this.black = Game.getIntColor(Color.BLACK, code);
                this.green = Game.getIntColor(Color.GREEN, code);
                System.out.println("Blue=" + blue + ", Pink=" + pink + ", Black=" + black + ", Green=" + green);

                GhostCL[] ghosts = game.getGhosts(code);
                printGhosts(ghosts);
                this.ghosts = ghosts;
                int up = Game.UP, left = Game.LEFT, down = Game.DOWN, right = Game.RIGHT;

                if (_count == 0) {
                    String pos = game.getPos(code).toString();
                    System.out.println("Pacman coordinate: " + pos);
                    this.pacPosition = StringToCord(pos);
                    SetPoints(board);

                    this.dangerousMap = new Map(board1.getWidth(), board1.getHeight(), 0);
                    this.dangerousMap.setCyclic(true);


                }

            }

            SetDangerous();
            SetSafety(board1.getMap());
            mergeSortPoints(this.points);
            mergeSortPoints(this.safetyMap);
            if (dangerousMap.getPixel(pacPosition)==1)
            {SetState(true);}
            else
            {this.dangerous=false;}
            if (pathToP == null) {
                SetPath();
            }
            if (pathToP.size() == 0) {
                SetPath();
            }
            _count++;
            if(pathToP.peek()==pacPosition) {pathToP.poll();}
            int dir = MoveTo(pathToP.poll());
            MovePoint(this.pacPosition, dir);
            if (this.points.contains(this.pacPosition)) {
                this.points.remove(this.pacPosition);
            }
            return dir;
        }
        return 0;


	}
    private void SetPoints(int[][] board)
    {
        ArrayList<Pixel2D> points1= new ArrayList<Pixel2D>();
        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[0].length; j++) {
                if(board[i][j]==this.pink || board[i][j]==this.green )
                {
                    points1.add(new Index2D(j,i));
                }
            }
        }
        this.points = points1;

    }
    private void mergeSortPoints(ArrayList<Pixel2D> arr) {
        if (arr.size() <= 1) {
            return;
        }

        int mid = arr.size() / 2;

        ArrayList<Pixel2D> left = new ArrayList<>(arr.subList(0, mid));
        ArrayList<Pixel2D> right = new ArrayList<>(arr.subList(mid, arr.size()));

        mergeSortPoints(left);
        mergeSortPoints(right);

        merge(arr, left, right);
    }

    private void merge(ArrayList<Pixel2D> result, ArrayList<Pixel2D> left, ArrayList<Pixel2D> right)
    {

        int i = 0, j = 0, k = 0;

        while (i < left.size() && j < right.size()) {
            if (distance2Dcy(pacPosition ,left.get(i)) <= distance2Dcy( pacPosition,right.get(j))) {
                result.set(k++, left.get(i++));
            } else {
                result.set(k++, right.get(j++));
            }
        }

        while (i < left.size()) {
            result.set(k++, left.get(i++));
        }

        while (j < right.size()) {
            result.set(k++, right.get(j++));
        }
    }
    private int distance2Dcy(Pixel2D c,Pixel2D t)
    {
       Pixel2D[] path= board1.shortestPath(c,t,blue);
       return path.length;
    }



private static Index2D StringToCord(String s)
    {int i=0,x=0,y=0;
       while(s.charAt(i)!=','){char c=s.charAt(i);
           x=x*10 + Character.getNumericValue(c);
       i++;}
       i++;
       while(i<s.length()){char c=s.charAt(i);
       y=y*10 + Character.getNumericValue(c);
       i++;}
       return new Index2D(y,x);

    }
	private static void printBoard(int[][] b) {
		for(int y =0;y<b.length;y++){
			for(int x =0;x<b[0].length;x++){
				int v = b[y][x];
				System.out.print(x+","+ v+"\t");
			}
			System.out.println( y);
		}
	}
	private static void printGhosts(GhostCL[] gs) {
		for(int i=0;i<gs.length;i++){
			GhostCL g = gs[i];
			System.out.println(i+") status: "+g.getStatus()+",  type: "+g.getType()+",  pos: "+g.getPos(0)+",  time: "+g.remainTimeAsEatable(0));
		}
	}
	private int randomDir()
    {
        for(int i=1;i<5;i++)
        {
            if(Chackcolor(this.pacPosition,i)){return i;}
        }
        return 0;
	}

    private static void MovePoint(Pixel2D t,int direc) {
        if(direc==2)
        {
            if(t.getY()+1>22){t.setY(0);}
            else{
            t.setY(t.getY()+1);}
        }
        if(direc==3)
        {
            t.setX(t.getX()-1);
        }
        if(direc==4)
        {
            if(t.getY()-1<0){t.setY(22);}
            else
            {t.setY(t.getY()-1);}
        }
        if(direc==1)
        {
            t.setX(t.getX()+1);
        }
    }
    private int MoveTo(Pixel2D target)
    {
        int Px,Py,Tx,Ty,Dx,Dy;
        Px = pacPosition.getX();
        Py = pacPosition.getY();
        Tx = target.getX();
        Ty = target.getY();
        Dx =Px-Tx;
        Dy = Py-Ty;
        if(Dx==-1){return 1;}
        if(Dx==1){return 3;}
        if(Dy==-1||Dy==22){return 2;}
        if(Dy==1||Dy==-22){return 4;}
        return 0;

    }



    private boolean Chackcolor(Pixel2D t, int dir)
    {
        if(dir==1){return this.board1.getPixel(t.getX(), t.getY()+1)!=blue;}
        if(dir==2){return this.board1.getPixel(t.getX()-1, t.getY())!=blue;}
        if(dir==3){return this.board1.getPixel(t.getX(), t.getY()-1)!=blue;}
        if(dir==4){return this.board1.getPixel(t.getX()+1, t.getY())!=blue;}
        return false;
    }
    private void SetPath()
    {
        Pixel2D p ;
        if(dangerous){p=safetyMap.get(0);}
        else{p=points.get(0);}
        Pixel2D[] path2 = board1.shortestPath(pacPosition,p,blue);
        Queue<Pixel2D> q = new LinkedList<>();
        for(int i=0;i<path2.length;i++){q.add(path2[i]);}
        this.pathToP = q;
    }
    private void SetDangerous()
    {
        this.dangerousMap=new Map(board1.getWidth(), board1.getHeight(), 0);;
        for(int i=0;i<ghosts.length;i++)
        {
            if(ghosts[i].getStatus()!=0){dangerousMap.drawRect(StringToCord(ghosts[i].getPos(code)),3,1);}
        }

    }
    private void SetSafety(int[][] board)
    {
        ArrayList<Pixel2D> points1= new ArrayList<Pixel2D>();
        for (int i = 0; i < board.length; i++)
        {
            for (int j = 0; j < board[0].length; j++) {
                if(board[i][j]!=this.blue && dangerousMap.getPixel(j,i) != 1)
                {
                    points1.add(new Index2D(j,i));
                }
            }
        }
        this.safetyMap=points1;
    }
    private void SetState(boolean state)
    {if(this.dangerous != state)
        {pathToP.clear();
            this.dangerous =state;
        }
    }

}