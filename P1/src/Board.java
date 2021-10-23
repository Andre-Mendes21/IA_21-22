import java.util.ArrayList;
import java.util.List;

public class Board implements Ilayout, Cloneable {
    private static final int dim = 3;
    private int board[][];

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
                board[i][j] = Character.getNumericValue(str.charAt(si++));
            }
        }
    }

    @Override
    public List<Ilayout> children() 
    {
       return null;
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
        String output = new String();

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
}
