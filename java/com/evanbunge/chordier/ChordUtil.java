package com.evanbunge.chordier;


import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/*
ChordUtil
    Provides the functional logic to build chord progressions
 */
public class ChordUtil
{
    private ChordList m_chordList;
    private List<String> m_progression;
    private String m_key;

    /*
    NAME

        ChordUtil - constructor for ChordUtil class

    SYNOPSIS

        ChordUtil( String a_key )

            a_key --> the key to build the progression in

    DESCRIPTION

        initializes the member variables
     */
    public ChordUtil( String a_key )
    {
        m_chordList = new ChordList( a_key );
        m_progression = new ArrayList<>();
        m_key = a_key;
    }

    /*
    NAME

        ChordUtil - constructor for ChordUtil class when loading from file

    SYNOPSIS

        ChordUtil( String a_key, List<String> a_progression )

            a_key --> the key to build the progression in
            a_progression --> the pre-existing progression

    DESCRIPTION

        calls other constructor to initialize the member variables
        copies the progression to the member variable list
     */
    public ChordUtil( String a_key, List<String> a_progression )
    {
        this( a_key );
        m_progression.addAll( a_progression );
    }

    /*
    NAME

        GetKey - gets the current key

    SYNOPSIS

        String GetKey()

    RETURNS

        the key as a string
     */
    public String GetKey()
    {
        return m_key;
    }

    /*
    NAME

        GetProgression - gets the current progression

    SYNOPSIS

        List<String> GetProgression()

    RETURNS

        the progression as a list of strings
     */
    public List<String> GetProgression()
    {
        return m_progression;
    }

    /*
    NAME

        Edit - edits a chord in the progression

    SYNOPSIS

        void Edit( int a_index, String a_newChord )

            a_index --> the index of the chord to change
            a_newChord --> the new chord to set that index to

    DESCRIPTION

        writes the new chord to the index
     */
    public void Edit( int a_index, String a_newChord )
    {
        m_progression.set( a_index, a_newChord );
    }

    /*
    NAME

        Add - adds new chord to the end

    SYNOPSIS

        void Add( String a_chord )

            a_chord --> the chord to add

    DESCRIPTION

        adds new chord to the end
     */
    public void Add( String a_chord )
    {
        m_progression.add( a_chord );
    }

    /*
    NAME

        Delete - deletes chord at index

    SYNOPSIS

        void Delete( int a_index )

            a_index --> the index of the chord to delete

    DESCRIPTION

        deletes the chord at the given index
     */
    public void Delete( int a_index )
    {
        m_progression.remove( a_index );
    }

    /*
    NAME

        DeleteEnd - deletes the last chord

    SYNOPSIS

        void DeleteEnd()

    DESCRIPTION

        deletes the last chord of the progression
     */
    public void DeleteEnd()
    {
        m_progression.remove( m_progression.size() - 1 );
    }

    /*
    NAME

        GetAllChords - gets all the chords in the key

    SYNOPSIS

        GetAllChords()

    RETURNS

        a list of all the chords in the key
     */
    public List<String> GetAllChords()
    {
        return m_chordList.GetAllChords();
    }


    /*
    NAME

        GetSuggestedChordsFor - gets the best chords for a given index

    SYNOPSIS

        List<String> GetSuggestedChordsFor( int a_index )

            a_index --> the index to suggest chords for

    DESCRIPTION

        if index is at the end, finds previous three chords to make suggestions
        if index is in the middle, finds the previous two and next two chords to make suggestions
        converts the chord numbers suggested to the proper chord names

    RETURNS

        a list of the best chords for that spot in the progression
     */
    public List<String> GetSuggestedChordsFor( int a_index )
    {
        int prev2 = a_index - 2 < 0 ? - 1 : m_chordList.GetChordIndex( m_progression.get( a_index - 2 ) );
        int prev1 = a_index - 1 < 0 ? - 1 : m_chordList.GetChordIndex( m_progression.get( a_index - 1 ) );

        List<Integer> chordIndices;
        // checks if index is at end
        if ( a_index == m_progression.size() - 1 )
        {
            int prev3 = a_index - 3 < 0 ? - 1 : m_chordList.GetChordIndex( m_progression.get( a_index - 3 ) );
            chordIndices = MCMatrix.GetChordIndices( prev3, prev2, prev1 );
        }
        else
        {
            int next1 = m_chordList.GetChordIndex( m_progression.get( a_index + 1 ) );
            int next2 = a_index + 2 >= m_progression.size() ? - 1 : m_chordList.GetChordIndex( m_progression.get( a_index + 2 ) );
            chordIndices = MCMatrix.GetChordIndices( prev2, prev1, next1, next2 );
        }
        // adds suggested chords to the list
        List<String> suggestedChords = new ArrayList<>();
        for ( int index : chordIndices )
        {
            suggestedChords.add( m_chordList.GetChord( index ) );
        }
        return suggestedChords;
    }


}
