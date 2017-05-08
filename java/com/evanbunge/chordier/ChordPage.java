package com.evanbunge.chordier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
ChordPage
    Provides the UI to build chord progressions
 */
public class ChordPage extends AppCompatActivity
{
    private ChordSelector m_selector;
    /*
    NAME

        addListener - event handler for the add button

    SYNOPSIS

        void onClick( View v )

            v --> the view clicked (unused in method)

    DESCRIPTION

        creates a new chord selector if no other selectors are being edited
     */
    private View.OnClickListener addListener = new View.OnClickListener()
    {
        @Override
        public void onClick( View v )
        {
            m_selector.Create();
        }
    };

    /*
    NAME

        onCreate - sets up the activity

    SYNOPSIS

        void onCreate( Bundle a_savedInstance )

            a_savedInstance --> bundle of data that is passed to superclass

    DESCRIPTION

        sets up the toolbar and sets click listeners and initializes member variables
        if from file, reads the progression and creates the selectors for each chord in the progression
        otherwise creates only one
     */
    @Override
    protected void onCreate( Bundle a_savedInstanceState )
    {
        super.onCreate( a_savedInstanceState );
        setContentView( R.layout.activity_chord_page );

        Toolbar toolbar = (Toolbar) findViewById( R.id.chord_toolbar );
        toolbar.setTitle( "Create a chord progression" );
        setSupportActionBar( toolbar );

        Intent chordListSetup = getIntent();
        Boolean loadFile = chordListSetup.getBooleanExtra( "load", false );
        if ( loadFile )
        {
            String filename = chordListSetup.getStringExtra( "filename" );
            FileHandler fh = new FileHandler( this );
            ChordUtil util = fh.LoadChordFile( filename );
            m_selector = new ChordSelector( this, util );
            List<String> prog = new ArrayList<>();
            prog.addAll( util.GetProgression() );
            for ( String chord : prog )
            {
                m_selector.Create( chord );
            }
        }
        else
        {
            String key = chordListSetup.getStringExtra( "key" );
            ChordUtil util = new ChordUtil( key );
            m_selector = new ChordSelector( this, util );

            m_selector.Create();
        }

        findViewById( R.id.add_chord ).setOnClickListener( addListener );
    }

    /*
    NAME

        onCreateOptionsMenu - inflates the toolbar menu

    SYNOPSIS

        boolean onCreateOptionsMenu( Menu a_menu )

            a_menu --> the menu to add items to

    DESCRIPTION

        inflates menu with items from the resource file

    RETURNS

        true when menu is inflated
     */
    @Override
    public boolean onCreateOptionsMenu( Menu a_menu )
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate( R.menu.chord_menu, a_menu );
        return true;
    }

    /*
    NAME

        onOptionsItemSelected - event handling when items in toolbar are selected

    SYNOPSIS

        boolean onOptionsItemSelected( MenuItem a_item )

            a_item --> the menu item selected

    DESCRIPTION

        checks the ID and launches the appropriate prompt

    RETURNS

        True if item click was recognized, otherwise calls to the superclass
     */
    @Override
    public boolean onOptionsItemSelected( MenuItem a_item )
    {
        switch ( a_item.getItemId() )
        {
            case R.id.action_save:
                savePrompt();
                return true;

            case R.id.action_info:
                ShowInfo();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected( a_item );

        }
    }

    /*
    NAME

        ShowInfo - displays pop-up window with helpful information

    SYNOPSIS

        void ShowInfo()

    DESCRIPTION

        creates a pop up window that holds helpful info for the user.
        sets an event handler to dismiss the window on button press.
     */
    private void ShowInfo()
    {
        View parentView = findViewById( R.id.parentview );
        int width = parentView.getWidth() / 2;
        int height = parentView.getHeight() / 2;

        final View popView = View.inflate( getApplicationContext(), R.layout.popup_info, null );
        final PopupWindow window = new PopupWindow( popView, width, height, true );
        window.showAtLocation( parentView, Gravity.CENTER, 0, 0 );

        ( (TextView) popView.findViewById( R.id.infotext ) ).setText( R.string.chord_info );

        popView.findViewById( R.id.ok_button )
               .setOnClickListener( new View.OnClickListener()
               {
                   @Override
                   public void onClick( View v )
                   {
                       window.dismiss();
                   }
               } );
    }

    /*
    NAME

        savePrompt - creates pop up window with support to save a progression

    SYNOPSIS

        void savePrompt()

    DESCRIPTION

        creates a pop up window.
        adds event handler to dismiss the window
        adds event handler to save the progression and dismiss window
        adds event handler to save and quit out of the activity
     */
    private void savePrompt()
    {
        View parentView = findViewById( R.id.parentview );
        int width = parentView.getWidth() / 2;
        int height = parentView.getHeight() / 2;

        final View popView = View.inflate( getApplicationContext(), R.layout.popup_save, null );
        final PopupWindow window = new PopupWindow( popView, width, height, true );
        window.showAtLocation( parentView, Gravity.CENTER, 0, 0 );

        final FileHandler fh = new FileHandler( this );

        popView.findViewById( R.id.cancel )
               .setOnClickListener( new View.OnClickListener()
               {
                   @Override
                   public void onClick( View v )
                   {
                       window.dismiss();
                   }
               } );

        popView.findViewById( R.id.save )
               .setOnClickListener( new View.OnClickListener()
               {
                   @Override
                   public void onClick( View v )
                   {
                       EditText fileEntry = (EditText) popView.findViewById( R.id.fileEntry );
                       String filename = fileEntry.getText()
                                                  .toString();

                       fh.SaveChordFile( m_selector.GetUtil(), filename );

                       window.dismiss();
                   }
               } );

        popView.findViewById( R.id.savequit )
               .setOnClickListener( new View.OnClickListener()
               {
                   @Override
                   public void onClick( View v )
                   {
                       EditText fileEntry = (EditText) popView.findViewById( R.id.fileEntry );
                       String filename = fileEntry.getText()
                                                  .toString();

                       fh.SaveChordFile( m_selector.GetUtil(), filename );

                       window.dismiss();

                       finish();
                   }
               } );
    }
}