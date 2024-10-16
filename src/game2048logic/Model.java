package game2048logic;

import game2048rendering.Board;
import game2048rendering.Side;
import game2048rendering.Tile;

import java.util.Formatter;

import org.checkerframework.checker.units.qual.s;


/** The state of a game of 2048.
 *  @author P. N. Hilfinger + Josh Hug
 */
public class Model {
    /** Current contents of the board. */
    private final Board board;
    /** Current score. */
    private int score;

    /* Coordinate System: column x, row y of the board (where x = 0,
     * y = 0 is the lower-left corner of the board) will correspond
     * to board.tile(x, y).  Be careful!
     */

    /** Largest piece value. */
    public static final int MAX_PIECE = 2048;

    /** A new 2048 game on a board of size SIZE with no pieces
     *  and score 0. */
    public Model(int size) {
        board = new Board(size);
        score = 0;
    }

    /** A new 2048 game where RAWVALUES contain the values of the tiles
     * (0 if null). VALUES is indexed by (x, y) with (0, 0) corresponding
     * to the bottom-left corner. Used for testing purposes. */
    public Model(int[][] rawValues, int score) {
        board = new Board(rawValues);
        this.score = score;
    }

    /** Return the current Tile at (x, y), where 0 <= x < size(),
     *  0 <= y < size(). Returns null if there is no tile there.
     *  Used for testing. */
    public Tile tile(int x, int y) {
        return board.tile(x, y);
    }

    /** Return the number of squares on one side of the board. */
    public int size() {
        return board.size();
    }

    /** Return the current score. */
    public int score() {
        return score;
    }


    /** Clear the board to empty and reset the score. */
    public void clear() {
        score = 0;
        board.clear();
    }

    /** Add TILE to the board. There must be no Tile currently at the
     *  same position. */
    public void addTile(Tile tile) {
        board.addTile(tile);
    }

    /** Return true iff the game is over (there are no moves, or
     *  there is a tile with value 2048 on the board). */
    public boolean gameOver() {
        return maxTileExists() || !atLeastOneMoveExists();
    }

    /** Returns this Model's board. */
    public Board getBoard() {
        return board;
    }

    /** Returns true if at least one space on the Board is empty.
     *  Empty spaces are stored as null.
     * */
    public boolean emptySpaceExists() {
        int x = 0,y =0;
        int size = size();
        if(score ==0)
        {
            for(;x<size;x++)
            {
                for(;y<size;y++)
                {
                    if(this.board.tile(x, y) != null)
                    {
                        return true;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Returns true if any tile is equal to the maximum valid value.
     * Maximum valid value is given by this.MAX_PIECE. Note that
     * given a Tile object t, we get its value with t.value().
     */
    public boolean maxTileExists() {
        int x = 0,y = 0;
        int size = size();
        for(;x<size;x++)
        {
            for(;y<size;y++)
            {
               Tile t = board.tile(x, y);
                if (t != null && t.value()== MAX_PIECE) 
                    return true;

            }
        }
        return false;
    }

    /**
     * Returns true if there are any valid moves on the board.
     * There are two ways that there can be valid moves:
     * 1. There is at least one empty space on the board.
     * 2. There are two adjacent tiles with the same value.
     */
    public boolean atLeastOneMoveExists() {
        // TODO: Fill in this function.
        int x=0,y=0;
        int size = board.size();
        if(emptySpaceExists())
        {
            return true;
        }

        //中间块
        for(x=1;x<size-1;x++)
        {
            for(y=1;y<size;y++)
            {
                Tile T = board.tile(x, y);
                Tile Tright = board.tile(x+1, y);
                Tile Tleft = board.tile(x-1, y);
                Tile Tup = board.tile(x, y+1);
                Tile Tdown = board.tile(x, y-1);
                if(T.value()== Tright.value()||
                T.value()== Tleft.value() ||
                T.value()== Tup.value() ||
                T.value()== Tdown.value()
                ) return true;
            }
        }

        //下侧
        for(x =0;x<size-2;x++)
        {
            if(board.tile(x, 0).value() ==board.tile(x+1, y).value())
            {
                return true;
            }
        }
        //上侧
        for (x = 0; x < size - 2; x++) {
            if (board.tile(x, size-1).value() == board.tile(x + 1, size-1).value()) {
                return true;
            }
        }
        //左侧
        for(y=0;y<size-2;y++)
        {
            if (board.tile(size-1, y).value() == board.tile(size-1,y).value()) {
                return true;
        }}
        //右侧
        for(y=0;y<size-2;y++)
        {
            if (board.tile(0, y).value() == board.tile(0,y).value()) {
                return true;}
        }
        return false;
    }

    /**
     * Moves the tile at position (x, y) as far up as possible.
     *
     * Rules for Tilt:
     * 1. If two Tiles are adjacent in the direction of motion and have
     *    the same value, they are merged into one Tile of twice the original
     *    value and that new value is added to the score instance variable
     * 2. A tile that is the result of a merge will not merge again on that
     *    tilt. So each move, every tile will only ever be part of at most one
     *    merge (perhaps zero).
     * 3. When three adjacent tiles in the direction of motion have the same
     *    value, then the leading two tiles in the direction of motion merge,
     *    and the trailing tile does not.
     */
    public void moveTileUpAsFarAsPossible(int x, int y) {
        Tile currTile = board.tile(x, y);
        int myValue = currTile.value();
        int targetY = y;

        int size = board.size();

        // 找到第一个不为空的方块

        // 记录上移的次数
        int count = 0;

        // 要移动的格子已经在最顶，不用处理移动不了
        if (y == size - 1) {
            return;
        }
        // 用来判断是否一直是为空格子
        boolean isNull = true;
        for (targetY = y + 1; targetY < size; targetY++) {
            // 找到第一个不为空的块
            count++;
            if (board.tile(x, targetY) != null) {
                isNull = false;
                break;
            }
        }

        // 表示移动的格子数为1，则表示下一个移动的格子不为空
        if (count == 1) {

            if (isNull) {
                board.move(x, y + count, currTile);
                return;
            } else {
                if (board.tile(x, y + count).value() == myValue && !board.tile(x, y + count).wasMerged()) {

                    board.move(x, y + count, currTile);
                    this.score += board.tile(x, y + count).value();
                } else {
                    return;
                }
            }

        }

        // 移动的次数不为0，表示可以移动
        else {
            if (isNull) {
                board.move(x, y + count, currTile);
                return;
            } else {
                if (board.tile(x, y + count).value() == myValue && !board.tile(x, y + count).wasMerged()) {

                    board.move(x, y + count, currTile);
                    this.score += board.tile(x, y + count).value();
                } else {
                    // if (y + count == size - 1 ) {

                    // }
                    board.move(x, y + count - 1, currTile);
                }
            }
        }
    }

    /** Handles the movements of the tilt in column x of the board
     * by moving every tile in the column as far up as possible.
     * The viewing perspective has already been set,
     * so we are tilting the tiles in this column up.
     * */
    public void tiltColumn(int x) {
    }

    public void tilt(Side side) {
    }

    /** Tilts every column of the board toward SIDE.
     */
    public void tiltWrapper(Side side) {
        board.resetMerged();
        tilt(side);
    }


    @Override
    public String toString() {
        Formatter out = new Formatter();
        out.format("%n[%n");
        for (int y = size() - 1; y >= 0; y -= 1) {
            for (int x = 0; x < size(); x += 1) {
                if (tile(x, y) == null) {
                    out.format("|    ");
                } else {
                    out.format("|%4d", tile(x, y).value());
                }
            }
            out.format("|%n");
        }
        String over = gameOver() ? "over" : "not over";
        out.format("] %d (game is %s) %n", score(), over);
        return out.toString();
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Model m) && this.toString().equals(m.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }
}
