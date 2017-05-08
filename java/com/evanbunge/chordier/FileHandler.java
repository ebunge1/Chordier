package com.evanbunge.chordier;


import android.app.Activity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

/*
FileHandler
    Handles saving and loading chord progressions
 */
public class FileHandler
{
    private final Activity m_activity;

    /*
    NAME

        FileHandler - constructor for FileHandler class

    SYNOPSIS

        FileHandler( Activity a_activity )

            a_activity --> the current activity, used for the Context to access the file system

    DESCRIPTION

        initializes the member variable
     */
    FileHandler( Activity a_activity )
    {
        m_activity = a_activity;
    }

    /*
    NAME

        LoadChordFile - loads a progression from a file

    SYNOPSIS

        ChordUtil LoadChordFile( String a_filename )

            a_filename --> the name of the file to load

    DESCRIPTION

        Reads the file line by line.
        the first line is the key.
        each subsequent line is the next chord in the progression.

    RETURNS

        a ChordUtil that holds the key and progression
     */
    public ChordUtil LoadChordFile( String a_filename )
    {
        String key = null;
        List<String> progression = new ArrayList<>();
        try
        {
            BufferedReader reader = new BufferedReader( new InputStreamReader( m_activity.openFileInput( a_filename ) ) );
            // get key
            key = reader.readLine();
            // get progression
            String chord;
            while ( ( chord = reader.readLine() ) != null )
            {
                progression.add( chord );
            }
            reader.close();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        return new ChordUtil( key, progression );
    }

    /*
    NAME

        SaveChordFile - saves the key and chord progression to a file

    SYNOPSIS

        void SaveChordFile( ChordUtil a_util, String a_filename )

            a_util --> the utility class of the chord page
            a_filename --> the name to save the file as

    DESCRIPTION

        gets the key and the progression from the utility class
        saves the key to the first line of the file
        saves each line of the progression on each subsequent line
    */
    public void SaveChordFile( ChordUtil a_util, String a_filename )
    {
        String key = a_util.GetKey();
        List<String> progression = a_util.GetProgression();
        try
        {
            PrintWriter writer = new PrintWriter( m_activity.openFileOutput( a_filename, ChordPage.MODE_PRIVATE ) );
            writer.println( key );
            for ( String chord : progression )
            {
                writer.println( chord );
            }
            writer.flush();
            writer.close();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
}
