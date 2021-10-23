import java.util.ArrayList;
import java.util.List;

public class Board implements Ilayout, Cloneable {
    private static final int[] tuplePos = {1, 0, 0, -1, -1, 0, 0, 1}; // Up, Right, Down, Left

    private static final int dim = 3;
    private int board[][];
    private int spaceX;
    private int spaceY;


    public Board()
    {
        this.board = new int[dim][dim];
    }

    public Board(String str) throws IllegalStateException
    {
        if(str.length() != dim * dim)
        {
            throw new IllegalStateException("Invalid arg in Board constructor");
        }

        this.board = new int[dim][dim];
        int si = 0;

        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim; j++)
            {
                this.board[i][j] = Character.getNumericValue(str.charAt(si++));

                if(board[i][j] == 0)
                {
                    this.spaceX = j;
                    this.spaceY = i;
                }
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    @Override
    public List<Ilayout> children() 
    {
        List<Ilayout> children = new ArrayList<>();
        Board newBoard = new Board();

        for(int i = 1; i < tuplePos.length; i += 2)
        {
            try {
                newBoard = (Board) this.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }

            int dx = this.spaceX - tuplePos[i];
            int dy = this.spaceY - tuplePos[i - 1];

            if((dx >= dim || dx < 0) || (dy >= dim || dy < 0)) // Checks if out-of-bounds
            {
                continue;
            }

            // Create new valid child state
            int temp = newBoard.board[dy][dx];
            newBoard.board[dy][dx] = 0;
            newBoard.board[this.spaceY][this.spaceX] = temp;
            newBoard.spaceX = dx;
            newBoard.spaceY = dy;

            // Add child state to list
            children.add(newBoard);
        }

        return children;
    }

    @Override
    public boolean isGoal(Ilayout l) 
    {
        return this.equals(l);
    }

    @Override
    public double getG() 
    {
        return 1;
    }

    public String toString()
    {
        String output = "";

        for(int i = 0; i < dim; i++)
        {
            for(int j = 0; j < dim - 1; j++)
            {
                output += this.board[i][j] == 0 ? " " : String.format("%d", this.board[i][j]);
            }
            output += this.board[i][dim-1] == 0 ? " \n" : String.format("%d\n", this.board[i][dim - 1]);
        }
        return output;
    }

    @Override
    public boolean equals(Object that)
    {
        if(that == null)
        {
            return false;
        }

        if(!(that instanceof Board))
        {
            return false;
        }

        Board other = (Board) that;

        for(int i = 0; i < dim; i++)
        {
            if(!this.board[i].equals(other.board[i]))
            {
                return false;
            }
        }

        return true;
    }


    @Override
    public int hashCode() 
    {
        return super.hashCode();
    }
}
