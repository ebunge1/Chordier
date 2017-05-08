package com.evanbunge.chordier;

import android.util.Log;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class MelodyUtil
{
    private List<RadioGroup> m_noteSelectors;
    private ChordList m_chordList;

    /*
    NAME

        MelodyUtil - constructor for MelodyUtil class

    SYNOPSIS

        MelodyUtil( String a_key )

            a_key --> the key to build the progression in

    DESCRIPTION

        initializes the member variables
     */
    public MelodyUtil( ChordList a_chordList )
    {
        m_noteSelectors = new ArrayList<>();
        m_chordList = a_chordList;
    }

    /*
    NAME

        Add - adds a notegroup to the reference list

    SYNOPSIS

        void Add()

    DESCRIPTION

        adds radiogroup to the reference list
     */
    public void Add( RadioGroup a_note )
    {
        m_noteSelectors.add( a_note );
    }

    /*
    NAME

        Delete - Deletes a notegroup to the reference list

    SYNOPSIS

        void Delete()

    DESCRIPTION

        deletes the last radiogroup from the reference list
     */
    public void Delete()
    {
        m_noteSelectors.remove( m_noteSelectors.size() - 1 );
    }

    /*
    NAME

        GetNoteSelectors - adds a notegroup to the reference list

    SYNOPSIS

        List<RadioGroup> GetNoteSelectors()

    RETURNS

        the list of note selectors (radiogroups)
     */
    public List<RadioGroup> GetNoteSelectors()
    {
        return m_noteSelectors;
    }

    /*
    NAME

        GetHarmony - gets the harmony for the current melody

    SYNOPSIS

        List<String> GetHarmony( int a_freq )

            a_freq --> the frequency of beats per chord
                            1 -> beat   2 -> half   3 -> measure

    DESCRIPTION

        calculates the weight of each chord
        uses those weights to pick the best chord for the melody

    RETURNS

        a list containing the chords of the harmony
     */
    public List<String> GetHarmony( int a_freq )
    {
        List<String> harmony = new ArrayList<>();
        // multiply number of beats by four to get number of 1/16th notes
        int subdivision = a_freq * 4;

        // chord suggested per i
        for ( int i = 0; i < m_noteSelectors.size() / subdivision; i++ )
        {
            int anchor = subdivision * i;
            int[] chordWeights = GetWeights( anchor, subdivision );

            harmony.add( CalculateChord( chordWeights, harmony ) );
        }
        return harmony;
    }

    /*
    NAME

        GetWeights - gets the weight of each chord for the progression

    SYNOPSIS

        int[] GetWeights( int a_anchor, int a_subdivision )

            a_anchor --> the starting point for the chord
            a_subdivision --> how many notes to include

    DESCRIPTION

        initializes an array of chord weights
        loops each note in the given range and adds to the weights of the appropriate chords
            adds 1 for each chord that note appears in
            adds 2 if note appears on downbeat
            adds 1 if note appears on upbeat
            adds 1 to adjacent chords if passing

    RETURNS

        a list of the weight of each chord
     */
    private int[] GetWeights( int a_anchor, int a_subdivision )
    {
        int prev = a_anchor > 0 ? m_noteSelectors.get( a_anchor - 1 )
                                                 .getCheckedRadioButtonId() : - 1;
        // more popular chords like 1,4,5, and 6 are given an advantage
        int[] chordWeights = { 2, 0, 0, 2, 2, 1, - 1 };
        // j is how many 1/16ths to sum
        for ( int j = 0; j < a_subdivision; j++ )
        {
            int noteIndex = m_noteSelectors.get( a_anchor + j )
                                           .getCheckedRadioButtonId();
            if ( noteIndex >= 0 )
            {
                if ( ( j % 4 ) % 2 != 0 && noteIndex != prev && prev != - 1 )
                {
                    // treat as neighboring or passing
                    chordWeights[ ( noteIndex + 1 ) % 7 ] += 1;
                    chordWeights[ ( noteIndex + 6 ) % 7 ] += 1;
                }
                else
                {
                    int baseWeight = 0;
                    // downbeat
                    if ( j % 4 == 0 ) baseWeight += 2;
                    // upbeat
                    if ( j % 4 == 2 ) baseWeight += 1;

                    // add to each chord the note appears in
                    chordWeights[ noteIndex ] += baseWeight + 2;
                    // third up
                    chordWeights[ ( noteIndex + 2 ) % 7 ] += baseWeight + 1;
                    // fifth up
                    chordWeights[ ( noteIndex + 4 ) % 7 ] += baseWeight + 1;
                    // third down
                    chordWeights[ ( noteIndex + 5 ) % 7 ] += baseWeight + 1;
                    // fifth down
                    chordWeights[ ( noteIndex + 3 ) % 7 ] += baseWeight + 1;
                }
            }
            prev = noteIndex;
        }
        return chordWeights;
    }

    /*
    NAME

        CalculateChord - constructor for ChordUtil class

    SYNOPSIS

        String CalculateChord( int[] a_chordWeights, List<String> a_harmony )

            a_chordWeights --> the weights of each chord
            a_harmony --> the current progression

    DESCRIPTION

        finds the maximum weight
        checks if more than one chord ties
        if there is a tie then the chords are compared to popular chords found from
            MCMatrix and the more popular chord is chosen
        if no matches are found with the popular chords, then a random one is chosen

    RETURNS

        the chord that is the best fit for the progression
     */
    private String CalculateChord( int[] a_chordWeights, List<String> a_harmony )
    {
        int chordIndex = 0;
        int maxWeight = 0;

        // find maximum weight
        for ( int k = 0; k < 7; k++ )
        {
            if ( a_chordWeights[ k ] > maxWeight )
            {
                maxWeight = a_chordWeights[ k ];
                chordIndex = k;
            }
        }

        // count how many indexes match max weight
        int numCollisions = 0;
        for ( int n = 0; n < 7; n++ )
        {
            if ( a_chordWeights[ n ] == maxWeight )
            {
                numCollisions++;
            }
        }

        if ( numCollisions <= 1 )
        {
            return m_chordList.GetChord( chordIndex );
        }
        else
        {
            // get popular chords
            int index = a_harmony.size();
            int prev3 = index - 3 < 0 ? - 1 : m_chordList.GetChordIndex( a_harmony.get( index - 3 ) );
            int prev2 = index - 2 < 0 ? - 1 : m_chordList.GetChordIndex( a_harmony.get( index - 2 ) );
            int prev1 = index - 1 < 0 ? - 1 : m_chordList.GetChordIndex( a_harmony.get( index - 1 ) );
            List<Integer> popularChords = MCMatrix.GetChordIndices( prev3, prev2, prev1 );

            // check if any popular chords match the max weight
            for ( int chord : popularChords )
            {
                if ( a_chordWeights[ chord ] == maxWeight )
                {
                    return m_chordList.GetChord( chord );
                }
            }

            // does not match any popular chords, choose randomly
            List<Integer> maxWeightChords = new ArrayList<>();
            for ( int l = 0; l < 7; l++ )
            {
                if ( a_chordWeights[ l ] == maxWeight )
                {
                    maxWeightChords.add( l );
                }
            }
            Random rn = new Random();
            int randChoice = rn.nextInt( maxWeightChords.size() );
            return m_chordList.GetChord( maxWeightChords.get( randChoice ) );
        }
    }
}
