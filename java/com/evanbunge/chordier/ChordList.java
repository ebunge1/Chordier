package com.evanbunge.chordier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
ChordList
    provides the chords of the given key
 */
public class ChordList
{
    // bank of all possible chords
    private final String[] m_chordBank = { "C", "C♯", "C♯♯", "D♭♭", "D♭", "D", "D♯", "D♯♯", "E♭♭",
                                           "E♭", "E", "E♯", "", "F♭", "F", "F♯", "F♯♯", "G♭♭", "G♭",
                                           "G", "G♯", "G♯♯", "A♭♭", "A♭", "A", "A♯", "A♯♯", "B♭♭",
                                           "B♭", "B", "B♯", "", "C♭" };
    // list of all chords in the given key
    private List<String> m_chords;

    /*
    NAME

        ChordList - constructor for ChordList class

    SYNOPSIS

        ChordList( String a_key )

            a_key --> the chosen key

    DESCRIPTION

        Gets the index of the key in the chordBank.
        If the key is minor, the index of the relative major key is found.
        The chord list is filled with the chords of the key
     */
    public ChordList( String a_key )
    {
        m_chords = new ArrayList<>();
        int index = Arrays.asList( m_chordBank )
                          .indexOf( a_key );

        if ( a_key.endsWith( "m" ) )
        {
            String key = a_key.substring( 0, a_key.length() - 1 );
            index = Arrays.asList( m_chordBank )
                          .indexOf( key );
            index = ( index + 9 ) % m_chordBank.length;
        }

        FillList( index );
    }

    /*
    NAME

        FillList - adds the seven chords of the given key to the chord list

    SYNOPSIS

        voidFillList( int a_index )

            a_index --> the index of the key in the chordBank

    DESCRIPTION

        adds each chord of the key in order.
        the pattern of the indices for the chords is always the same.
     */
    private void FillList( int a_index )
    {
        m_chords.add( m_chordBank[ a_index ] );
        m_chords.add( m_chordBank[ ( a_index + 5 ) % m_chordBank.length ] + "m" );
        m_chords.add( m_chordBank[ ( a_index + 10 ) % m_chordBank.length ] + "m" );
        m_chords.add( m_chordBank[ ( a_index + 14 ) % m_chordBank.length ] );
        m_chords.add( m_chordBank[ ( a_index + 19 ) % m_chordBank.length ] );
        m_chords.add( m_chordBank[ ( a_index + 24 ) % m_chordBank.length ] + "m" );
        m_chords.add( m_chordBank[ ( a_index + 29 ) % m_chordBank.length ] + "°" );
    }

    /*
    NAME

        GetChord - gets the chord at the given index

    SYNOPSIS

        String GetChord( int a_chordIndex )

            a_chordIndex --> the index of the chord in the key

    RETURNS

        the name of the chord as a string
     */
    public String GetChord( int a_chordIndex )
    {
        return m_chords.get( a_chordIndex );
    }

    /*
    NAME

        GetChordIndex - finds the index of the chord in the key

    SYNOPSIS

        int GetChordIndex( String a_chord )

            a_chord --> the name of the chord

    RETURNS

        the index of the chord in the key
     */
    public int GetChordIndex( String a_chord )
    {
        return m_chords.indexOf( a_chord );
    }

    /*
    NAME

        GetAllChords - gets all the chords in the key
    SYNOPSIS

        List<String> GetAllChords()

    RETURNS

        A copy of the list of chords in the key
     */
    public List<String> GetAllChords()
    {
        return m_chords;
    }

}
