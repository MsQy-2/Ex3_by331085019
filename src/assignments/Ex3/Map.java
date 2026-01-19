package assignments.Ex3;

import java.io.Serializable;
import java.util.*;

/**
 * This class represents a 2D map (int[w][h]) as a "screen" or a raster matrix or maze over integers.
 * This is the main class needed to be implemented.
 *
 * @author boaz.benmoshe
 *
 */
public class Map implements Map2D, Serializable{


    private int[][] _map;
    private boolean Cyclic = false;
    // edit this class below
    /**
     * Constructs a w*h 2D raster map with an init value v.
     * @param w
     * @param h
     * @param v
     */
    public Map(int w, int h, int v)
    {init(w, h, v);

    }

    /**
     * Constructs a square map (size*size).
     * @param size
     */
    public Map(int size)
    {
        this(size,size, 0);
        this._map = new int[size][size];
        for (int x = 0; x < size; x++){Arrays.fill(this._map[x],0);}
    }

    /**
     * Constructs a map from a given 2D array.
     * @param data
     */
    public Map(int[][] data)
    {
        init(data);

    }
    @Override
    public void init(int w, int h, int v) {
        int[][] map = new int[h][w];
        for (int x = 0; x < w; x++)
        {
            Arrays.fill(map[x],v);
        }
        this._map = map;
    }
    @Override
    public void init(int[][] data) {
        int length = data[0].length;
        for (int x = 0; x < data.length; x++)
        {
            if(data[x].length != length)
            {throw new RuntimeException("not valid map");}
        }
        if(data==null ){throw new RuntimeException("not valid map");}
        this._map = data;
    }
    @Override
    public int[][] getMap() {
        int[][] ans = this._map;

        return ans;
    }
    @Override
    public int getWidth() {
        int ans = this._map[0].length;

        return ans;
    }
    @Override
    public int getHeight() {
        int ans = this._map.length;

        return ans;
    }
    @Override
    public int getPixel(int x, int y) {
        int ans = this._map[y][x];

        return ans;
    }
    @Override
    public int getPixel(Pixel2D p) {
        int ans = this._map[p.getY()][p.getX()];

        return ans;
    }
    @Override
    public void setPixel(int x, int y, int v) {
        this._map[y][x] = v;

    }
    @Override
    public void setPixel(Pixel2D p, int v) {
        this._map[p.getY()][p.getX()] = v;

    }

    @Override
    public boolean isInside(Pixel2D p) {
        int maxX = this._map[0].length;
        int maxY = this._map.length;
        if(maxX > p.getX() || maxY > p.getY() ){return false;}
        return true;
    }
    @Override
    public boolean isCyclic() {return Cyclic;}
    @Override
    public void setCyclic(boolean c) {this.Cyclic = c;}


    public boolean sameDimensions(Map2D p) {
        return this.getWidth() == p.getWidth() && this.getHeight() == p.getHeight();
    }


    public void addMap2D(Map2D p) {
        if(sameDimensions(p))
        {
            for(int y = 0; y < this.getHeight(); y++){for(int x = 0; x < this.getWidth(); x++){this.setPixel(x, y, p.getPixel(x, y));}}
        }

    }


    public void mul(double scalar) {
        for(int y = 0; y < this.getHeight(); y++){for(int x = 0; x < this.getWidth(); x++){this._map[y][x] *= scalar;}}

    }


    public void rescale(double sx, double sy)
    {
        int X = (int) (this._map[0].length*sx);
        int Y = (int) (this._map.length*sy);
        int[][] new_map = new int[Y][X];
        for(int y = 0; y < this.getHeight(); y++){for(int x = 0; x < this.getWidth(); x++)
        {
            if(isInside(new Index2D(x, y))){new_map[y][x] = this._map[y][x];}
            else{new_map[y][x] = 0;}
        }}
        this._map = new_map;

    }


    public void drawCircle(Pixel2D center, double rad, int color)
    {for (int y = 0; y < this.getHeight(); y++){for (int x = 0; x < this.getWidth(); x++)
    {
        if(Math.pow(this.getWidth()- center.getX(),2)+Math.pow(this.getHeight()-center.getY(),2)<=Math.pow(rad,2)){this.setPixel(x,y,color);}
    }}

    }


    public void drawLine(Pixel2D p1, Pixel2D p2, int color)
    {
        int bigX = Math.max(p1.getX(), p2.getX());
        int bigY = Math.max(p1.getY(), p2.getY());
        int smallX = Math.min(p1.getX(), p2.getX());
        int smallY = Math.min(p1.getY(), p2.getY());
        for(int y = smallY; y <= bigY; y++){for(int x = smallX; x <= bigX; x++)
        {
            if(Math.abs(y-(((p1.getY()-p2.getY())/(p1.getX()- p2.getX()))*(x- p1.getX())+ p1.getY()))<1){
                this.setPixel(x,y,color);
            }
        }}

    }


    public void drawRect(Pixel2D p1,int rad, int color)
    {
        int bigX = p1.getX()+rad;
        int bigY = p1.getY()+rad;
        int smallX = p1.getX()-rad;
        int smallY = p1.getY()-rad;
        int xc,yc;
        for(int y = smallY; y <= bigY; y++){for(int x = smallX; x <= bigX; x++){
            xc=x;
            yc=y;
            if(x>= this._map[0].length){xc=x-this._map[0].length; }
            if(y>= this._map.length){yc=y-this._map.length;}
            if(x<0){xc=x+this._map[0].length;}
            if(y<0){yc=y+this._map.length;}

            this.setPixel(xc,yc,color);

        }}

    }

    @Override
    public boolean equals(Object ob) {
        if(ob instanceof Map)
        {if(this.sameDimensions((Map)ob))
        {
            for (int y = 0; y < this.getHeight(); y++){
                for (int x = 0; x < this.getWidth(); x++){
                    if(this.getPixel(x, y) != ((Map)ob).getPixel(x, y))return false;
                }
            }
            return true;
        }

        }
        return false;
    }
    @Override
    /**
     * Fills this map with the new color (new_v) starting from p.
     * https://en.wikipedia.org/wiki/Flood_fill
     */
    public int fill(Pixel2D xy, int new_v)
    {
        if(this.getPixel(xy)==new_v)return 1;
        directions(xy.getX(), xy.getY(),this.getPixel(xy),new_v,this.Cyclic);
        return 1;
    }

    @Override
    /**
     * BFS like shortest the computation based on iterative raster implementation of BFS, see:
     * https://en.wikipedia.org/wiki/Breadth-first_search
     */
    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor)
    {
        int w = this._map[0].length;
        int h = this._map.length;
        int sx = p1.getX(), sy = p1.getY();
        int tx = p2.getX(), ty = p2.getY();

        if (sx < 0 || sy < 0 || sx >= w || sy >= h ||
                tx < 0 || ty < 0 || tx >= w || ty >= h) {
            return null;
        }
        if (this._map[sy][sx] == obsColor || this._map[ty][tx] == obsColor) {
            return null;
        }

        boolean[][] visited = new boolean[h][w];
        Pixel2D[][] parent = new Pixel2D[h][w];

        Queue<Pixel2D> q = new LinkedList<>();
        q.add(p1);
        visited[sy][sx] = true;

        int[] dx = {1, -1, 0, 0};
        int[] dy = {0, 0, 1, -1};

        while (!q.isEmpty()) {
            Pixel2D p = q.poll();
            int x = p.getX();
            int y = p.getY();

            if (x == tx && y == ty) break;

            for (int i = 0; i < 4; i++) {
                int nx = x + dx[i];
                int ny = y + dy[i];

                if (this.Cyclic) {
                    nx = (nx + w) % w;
                    ny = (ny + h) % h;
                }

                if (nx >= 0 && ny >= 0 && nx < w && ny < h &&
                        !visited[ny][nx] &&
                        this._map[ny][nx] != obsColor) {

                    visited[ny][nx] = true;
                    parent[ny][nx] = p;
                    q.add(new Index2D(nx, ny));
                }
            }
        }

        // no path
        if (!visited[ty][tx]) {
            return null;
        }

        // reconstruct path
        List<Pixel2D> path = new ArrayList<>();
        Pixel2D curr = p2;

        while (curr != null) {
            path.add(curr);
            curr = parent[curr.getY()][curr.getX()];
        }

        Collections.reverse(path);
        return path.toArray(new Pixel2D[0]);
    }
    @Override
    public Map2D allDistance(Pixel2D start, int obsColor) {
        int w = getWidth();
        int h = getHeight();

        int[][] dist = new int[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {

                // obstacle
                if (this._map[y][x] == obsColor) {
                    dist[y][x] = -1;
                    continue;
                }

                Index2D target = new Index2D(x, y);
                Pixel2D[] path = shortestPath(start, target, obsColor);

                if (path == null) {
                    dist[y][x] = -1;
                } else {
                    dist[y][x] = path.length - 1;
                }
            }
        }

        return new Map(dist);
    }
    ////////////////////// Private Methods ///////////////////////
    public void directions(int x , int y,int old_v,int new_v,boolean cyclic)
    {if(cyclic)
    {if(x==_map[0].length)x=0;
        if(y==_map.length)y=0;
        if(x<0)x= _map[0].length-1;
        if(y<0)y=_map.length-1;
    }
        if(x==_map[0].length || y==_map.length||y== -1 || x==-1 || this._map[y][x]!=old_v){return;}
        this._map[y][x]=new_v;
        directions(x+1,y,old_v,new_v,cyclic);
        directions(x,y+1,old_v,new_v,cyclic);
        directions(x,y-1,old_v,new_v,cyclic);
        directions(x-1,y,old_v,new_v,cyclic);

    }




}