package com.evanbunge.chordier;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
MCMatrix
    creates the markov chain model that is used to suggest chords
 */
public class MCMatrix
{
    // matrix for suggesting chords in the middle of a progression
    private static double[][] mid;
    // matrix for suggesting chords at the end of a progression
    private static double[][] end;

    // this static block acts as the constructor and initializes the matrices
    static
    {
        end = new double[ 8 * 8 * 8 ][ 7 ];
        mid = new double[ 8 * 8 * 8 * 8 ][ 7 ];
        CreateMatrices();
    }

    /*
    NAME

        CreateMatrices - populates the matrices with data

    SYNOPSIS

        void CreateMatrices()

    DESCRIPTION

        Reads progressions line by line from asset file.
        Scans each progression chord by chord and updates the weights of each matrix accordingly.
        Normalizes the matrices.
     */
    private static void CreateMatrices()
    {
        try
        {
            BufferedReader reader = new BufferedReader( new InputStreamReader( HomePage.m_context.getAssets()
                                                                                                 .open( "DefaultProgIndex" ) ) );
            String prog;
            while ( ( prog = reader.readLine() ) != null )
            {
                for ( int i = 5; i < prog.length(); i++ )
                {
                    UpdateWeight( Character.getNumericValue( prog.charAt( i - 4 ) ), Character.getNumericValue( prog.charAt( i - 3 ) ), Character.getNumericValue( prog.charAt( i - 2 ) ), Character.getNumericValue( prog.charAt( i - 1 ) ), Character.getNumericValue( prog.charAt( i ) ) );
                    UpdateWeight( Character.getNumericValue( prog.charAt( i - 5 ) ), Character.getNumericValue( prog.charAt( i - 4 ) ), Character.getNumericValue( prog.charAt( i - 3 ) ), Character.getNumericValue( prog.charAt( i - 2 ) ) );
                }
            }
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        NormalizeMatrices();
    }

    /*
    NAME

        UpdateWeight - updates the weight of a target chord

    SYNOPSIS

        void UpdateWeight( int a_prev3, int a_prev2, int a_prev, int a_target )

            a_prev3 --> the chord 3 before the target chord
            a_prev2 --> the chord 2 before the target chord
            a_prev --> the chord before the target chord
            a_target --> the target chord

    DESCRIPTION

        calculates the key index and increments the weight of the target chord
     */
    private static void UpdateWeight( int a_prev3, int a_prev2, int a_prev, int a_target )
    {
        // decrementing target chord converts it from chord number to chord index
        end[ a_prev3 * 8 * 8 + a_prev2 * 8 + a_prev ][ a_target - 1 ]++;
    }

    /*
    NAME

        UpdateWeight - updates the weight of a target chord

    SYNOPSIS

        void UpdateWeight( int a_prev2, int a_prev, int a_target, int a_next, int a_next2 )

            a_prev2 --> the chord 2 before the target chord
            a_prev --> the chord before the target chord
            a_target --> the target chord
            a_next --> the chord after the target chord
            a_next2 --> the chord 2 after the target chord

    DESCRIPTION

        calculates the key index and increments the weight of the target chord
     */
    private static void UpdateWeight( int a_prev2, int a_prev, int a_target, int a_next, int a_next2 )
    {
        // decrementing target chord converts it from chord number to chord index
        mid[ a_prev2 * 8 * 8 * 8 + a_prev * 8 * 8 + a_next * 8 + a_next2 ][ a_target - 1 ]++;
    }

    /*
    NAME

        NormalizeMatrices - converts weights to percentages

    SYNOPSIS

        void NormalizeMatrices()

    DESCRIPTION

        traverses through matrix rows
        gets sum of columns and divides each by the total to get percentages
        repeat for both matrices
     */
    private static void NormalizeMatrices()
    {
        for ( int i = 0; i < 8 * 8 * 8; i++ )
        {
            int sum = GetSum( end, i );
            if ( sum != 0 )
            {
                for ( int j = 0; j < 7; j++ )
                {
                    end[ i ][ j ] /= sum;
                }
            }
        }
        for ( int i = 0; i < 8 * 8 * 8 * 8; i++ )
        {
            int sum = GetSum( mid, i );
            if ( sum != 0 )
            {
                for ( int j = 0; j < 7; j++ )
                {
                    mid[ i ][ j ] /= sum;
                }
            }
        }
    }

    /*
    NAME

        GetSum - gets the sum of the columns in a row of the matrix

    SYNOPSIS

        int GetSum( double[][] a_matrix, int a_row )

            a_matrix --> the matrix being normalized
            a_row --> the row being summed

    DESCRIPTION

        traverses each column in the row and accumulates the sum as it goes

    RETURNS
        the sum of each column in the row
     */
    private static int GetSum( double[][] a_matrix, int a_row )
    {
        int sum = 0;

        for ( int i = 0; i < 7; i++ )
        {
            sum += a_matrix[ a_row ][ i ];
        }

        return sum;
    }

    /*
    NAME

        GetChordIndices - gets the indices of the popular chord suggestions given previous chords
    SYNOPSIS

        List<Integer> GetChordIndices( int a_prev3, int a_prev 2, ine a_prev )

            a_prev3 --> the chord 3 before the target chord
            a_prev2 --> the chord 2 before the target chord
            a_prev --> the chord before the target chord

    DESCRIPTION

        converts the given chord indices to chord numbers by incrementing
        calculates the key index and queries end the matrix for the best chords

    RETURNS
        the list of suggested chords as chord indices
     */
    public static List<Integer> GetChordIndices( int a_prev3, int a_prev2, int a_prev )
    {
        a_prev3++;
        a_prev2++;
        a_prev++;
        int index = ( 8 * 8 * a_prev3 ) + ( 8 * a_prev2 ) + a_prev;
        return ChordsFromMatrix( end, index );
    }

    /*
    NAME

        GetChordIndices - gets the indices of the popular chord suggestions given the surrounding chords
    SYNOPSIS

        List<Integer> GetChordIndices( int a_prev 2, ine a_prev, int a_next, int a_next2 )

            a_prev2 --> the chord 2 before the target chord
            a_prev --> the chord before the target chord
            a_next --> the chord after the target chord
            a_next2 --> the chord 2 after the target chord

    DESCRIPTION

        converts the given chord indices to chord numbers by incrementing
        calculates the key index and queries the mid matrix for the best chords

    RETURNS
        the list of suggested chords as chord indices
     */
    public static List<Integer> GetChordIndices( int a_prev2, int a_prev, int a_next, int a_next2 )
    {
        a_prev2++;
        a_prev++;
        a_next++;
        a_next2++;
        int index = ( a_prev2 * 8 * 8 * 8 ) + ( a_prev * 8 * 8 ) + ( a_next * 8 ) + a_next2;
        return ChordsFromMatrix( mid, index );
    }

    /*
    NAME

        ChordsFromMatrix - finds the most popular chords given an key index

    SYNOPSIS

        List<Integer> ChordsFromMatrix( double[][] a_matrix, int a_index )

    DESCRIPTION

        finds the top three percentages
        inserts these chords with higher percentages into a list in order of popularity,
            with the most popular chords at the front.

    RETURNS

        The list of popular chords as chord indices
     */
    private static List<Integer> ChordsFromMatrix( double[][] a_matrix, int a_index )
    {
        List<Integer> chordIndices = new ArrayList<>();
        double first = 0;
        double second = 0;
        double third = 0;
        double current;

        // find top three ratios
        for ( int i = 0; i < 7; i++ )
        {
            current = a_matrix[ a_index ][ i ];
            if ( current > first )
            {
                third = second;
                second = first;
                first = current;
            }
            else if ( current > second && current < first )
            {
                third = second;
                second = current;
            }
            else if ( current > third && current < second )
            {
                third = current;
            }
        }
        // add chords matching top ratio
        for ( int j = 0; j < 7; j++ )
        {
            if ( a_matrix[ a_index ][ j ] == first )
            {
                chordIndices.add( j );
            }
        }
        // add chords matching second
        if ( second != 0 )
        {
            for ( int k = 0; k < 7; k++ )
            {
                if ( a_matrix[ a_index ][ k ] == second )
                {
                    chordIndices.add( k );
                }
            }
        }
        // add chords matching third
        if ( third != 0 )
        {
            for ( int n = 0; n < 7; n++ )
            {
                if ( a_matrix[ a_index ][ n ] == third )
                {
                    chordIndices.add( n );
                }
            }
        }
        return chordIndices;
    }

}
