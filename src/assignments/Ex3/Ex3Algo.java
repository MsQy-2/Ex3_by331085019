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
public class Ex3Algo implements PacManAlgo {
    private int _count;
    private Index2D pacPosition;
    private int blue;
    private int green;
    private int black;
    private int pink;
    private Map board1;
    private ArrayList<Pixel2D> points;
    private ArrayList<Pixel2D> points2;
    private Queue<Pixel2D> pathToP;
    private boolean started = false;
    private GhostCL[] ghosts;


    private ArrayList<Pixel2D> safetyMap;


    public Ex3Algo() {
        _count = 0;
    }

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
        if (!started) {
            if (Ex3Main.getCMD() != null && Ex3Main.getCMD() == ' ') {
                this.started = true;
            }
        }
        if (started) {

            if (_count == 0 || _count == 300) {
                int code = 0;

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
                    this.points2 = new ArrayList<>(points);
                }

            }
            GhostCL[] ghosts = game.getGhosts(0);
            this.ghosts = ghosts;
            mergeSortPoints(this.points);
            mergeSortPoints2(this.points2);
            if (pathToP == null) {
                SetPath(points);
            }
            if (pathToP.size() == 0) {
                SetPath(points);
            }
            if (DFCG(pacPosition) < 6) {
                pathToP.clear();
                SetPath(this.points2);
            }
            _count++;
            if (pathToP.peek() == pacPosition) {
                pathToP.poll();
            }
            int dir = MoveTo(pathToP.poll());
            MovePoint(this.pacPosition, dir);
            if (this.points.contains(this.pacPosition)) {
                this.points.remove(this.pacPosition);
            }
            System.out.println(pacPosition);
            System.out.println(game.getPos(0));
            printGhosts(ghosts);
            return dir;
        }
        return 0;


    }

    private void SetPoints(int[][] board) {
        ArrayList<Pixel2D> points1 = new ArrayList<Pixel2D>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j] == this.pink || board[i][j] == this.green) {
                    points1.add(new Index2D(j, i));
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

    private void merge(ArrayList<Pixel2D> result, ArrayList<Pixel2D> left, ArrayList<Pixel2D> right) {

        int i = 0, j = 0, k = 0;

        while (i < left.size() && j < right.size()) {
            if (distance2Dcy(pacPosition, left.get(i)) <= distance2Dcy(pacPosition, right.get(j))) {
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

    private void mergeSortPoints2(ArrayList<Pixel2D> arr) {
        if (arr.size() <= 1) {
            return;
        }

        int mid = arr.size() / 2;

        ArrayList<Pixel2D> left = new ArrayList<>(arr.subList(0, mid));
        ArrayList<Pixel2D> right = new ArrayList<>(arr.subList(mid, arr.size()));

        mergeSortPoints2(left);
        mergeSortPoints2(right);

        merge2(arr, left, right);
    }

    private void merge2(ArrayList<Pixel2D> result, ArrayList<Pixel2D> left, ArrayList<Pixel2D> right) {

        int i = 0, j = 0, k = 0;

        while (i < left.size() && j < right.size()) {
            if (DSFG(left.get(i)) >= DSFG(right.get(j))) {
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

    private int distance2Dcy(Pixel2D c, Pixel2D t) {
        Map board2 = new Map(board1.getMap());
        for (int x = 0; x < ghosts.length; x++) {
            board2.setPixel(StringToCord(ghosts[x].getPos(0)), blue);
        }
        Pixel2D[] path = board2.shortestPath(c, t, blue);
        if (path == null) return 1000;
        return path.length;
    }

    private static Index2D StringToCord(String s) {
        int i = 0, x = 0, y = 0;
        while (s.charAt(i) != ',') {
            char c = s.charAt(i);
            x = x * 10 + Character.getNumericValue(c);
            i++;
        }
        i++;
        while (i < s.length()) {
            char c = s.charAt(i);
            y = y * 10 + Character.getNumericValue(c);
            i++;
        }
        return new Index2D(y, x);

    }

    private static void printBoard(int[][] b) {
        for (int y = 0; y < b.length; y++) {
            for (int x = 0; x < b[0].length; x++) {
                int v = b[y][x];
                System.out.print(x + "," + v + "\t");
            }
            System.out.println(y);
        }
    }

    private static void printGhosts(GhostCL[] gs) {
        for (int i = 0; i < gs.length; i++) {
            GhostCL g = gs[i];
            System.out.println(i + ") status: " + g.getStatus() + ",  type: " + g.getType() + ",  pos: " + g.getPos(0) + ",  time: " + g.remainTimeAsEatable(0));
        }
    }

    private static void MovePoint(Pixel2D t, int direc) {
        if (direc == 2) {
            if (t.getY() + 1 > 22) {
                t.setY(0);
            } else {
                t.setY(t.getY() + 1);
            }
        }
        if (direc == 3) {
            t.setX(t.getX() - 1);
        }
        if (direc == 4) {
            if (t.getY() - 1 < 0) {
                t.setY(22);
            } else {
                t.setY(t.getY() - 1);
            }
        }
        if (direc == 1) {
            t.setX(t.getX() + 1);
        }
    }

    private int MoveTo(Pixel2D target) {
        int Px, Py, Tx, Ty, Dx, Dy;
        Px = pacPosition.getX();
        Py = pacPosition.getY();
        Tx = target.getX();
        Ty = target.getY();
        Dx = Px - Tx;
        Dy = Py - Ty;
        if (Dx == -1) {
            return 1;
        }
        if (Dx == 1) {
            return 3;
        }
        if (Dy == -1 || Dy == 22) {
            return 2;
        }
        if (Dy == 1 || Dy == -22) {
            return 4;
        }
        return 0;

    }

    private void SetPath(ArrayList<Pixel2D> arr) {
        int j = 0;
        Pixel2D[] path2 =board1.shortestPath(pacPosition, arr.get(j), blue);
        while (path2 == null) {path2 =board1.shortestPath(pacPosition, arr.get(j++), blue);}
        Queue<Pixel2D> q = new LinkedList<>();
        for (int i = 0; i < path2.length; i++) {
            q.add(path2[i]);
        }
        this.pathToP = q;
    }

    private double DFCG(Pixel2D point) {

        double firtDist = point.distance2D(StringToCord(ghosts[0].getPos(0)));
        if (ghosts[0].getStatus() == 0) {
            firtDist = 1000;
        }
        for (int i = 1; i < ghosts.length; i++) {
            if (ghosts[i].getStatus() != 0) {
                firtDist = Math.min(firtDist, point.distance2D(StringToCord(ghosts[i].getPos(0))));
            }
        }
        return firtDist;
    }

    private double DSFG(Pixel2D point) {
        double score = 0;

        for (int i = 0; i < ghosts.length; i++) {
            if (ghosts[i].getStatus() != 0) {
                score += point.distance2D(StringToCord(ghosts[i].getPos(0)));
            }
        }
        return score;
    }
}