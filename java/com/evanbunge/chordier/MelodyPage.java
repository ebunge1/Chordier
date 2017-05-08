package com.evanbunge.chordier;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/*
MelodyPage
    Provides the UI for creating a melody
 */
public class MelodyPage extends AppCompatActivity
{
    // reference to textviews that display the chords for the harmony
    private List<TextView> m_harmonyReference;
    private MelodyUtil m_util;

    /*
    NAME

        suggestListener - event handling for suggest button

    SYNOPSIS

        void onClick( View v )

            v --> the view that was selected (unused)

    DESCRIPTION

        Calculates the frequency of chords in the harmony using
            the selected item index of the spinner menu
        Gets the chords for the harmony
        Sets the chord in the proper textviews from the harmony reference
     */
    private View.OnClickListener suggestlistener = new View.OnClickListener()
    {
        @Override
        public void onClick( View v )
        {
            // beat==0 half==1 measure==2
            int frequency = ( (Spinner) findViewById( R.id.frequency ) ).getSelectedItemPosition();
            frequency = (int) Math.pow( 2, frequency );
            List<String> harmony = m_util.GetHarmony( frequency );
            for ( int i = 0, index = 0; i < m_harmonyReference.size(); i++ )
            {
                if ( i % frequency == 0 )
                {
                    m_harmonyReference.get( i )
                                      .setText( harmony.get( index++ ) );
                }
                else
                {
                    m_harmonyReference.get( i )
                                      .setText( "" );
                }
            }
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
        sets up the note legend and frequency selector and adds the first measure
     */
    @Override
    protected void onCreate( Bundle a_savedInstanceState )
    {
        super.onCreate( a_savedInstanceState );
        setContentView( R.layout.activity_melody_page );

        Toolbar toolbar = (Toolbar) findViewById( R.id.melody_toolbar );
        toolbar.setTitle( "Create a melody" );
        setSupportActionBar( toolbar );


        m_harmonyReference = new ArrayList<>();
        String key = getIntent().getStringExtra( "key" );
        ChordList chordlist = new ChordList( key );
        m_util = new MelodyUtil( chordlist );
        CreateNoteLegend( chordlist.GetAllChords() );
        AddMeasure();

        ( (Spinner) findViewById( R.id.frequency ) ).setSelection( 2 );

        findViewById( R.id.suggestChords ).setOnClickListener( suggestlistener );
        findViewById( R.id.addMeasure ).setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                AddMeasure();
            }
        } );
        findViewById( R.id.deleteMeasure ).setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View v )
            {
                DeleteMeasure();
            }
        } );
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
        getMenuInflater().inflate( R.menu.melody_menu, a_menu );
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
            case R.id.action_clearall:
                ClearAll();
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

        CreateNoteLegend - creates the note legend

    SYNOPSIS

        void CreateNoteLegend( List<String> a_notes )

            a_notes --> the notes of the chosen key

    DESCRIPTION

        Gets the chords from the scale and sets the textviews of the legend
            while removing the pitch and mode identifiers
     */
    private void CreateNoteLegend( List<String> a_notes )
    {
        LinearLayout legend = (LinearLayout) findViewById( R.id.noteLegend );

        for ( int i = 6; i >= 0; i-- )
        {
            TextView tv = new TextView( this );
            tv.setText( a_notes.get( i )
                               .replace( "m", "" )
                               .replace( "°", "" )
                      );
            tv.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 24 );
            tv.setGravity( Gravity.CENTER );
            legend.addView( tv );
        }
    }

    /*
    NAME

        AddMeasure - adds a measure to the melodyarea

    SYNOPSIS

        void AddMeasure()

    DESCRIPTION

        sets up a grid of sixteen radio groups (1/16 beats) of seven radio buttons (notes) each
            as four groups of four
        adds a textview for the chords above each quarter beat and a counter underneath for 1/16ths
        adds references to notegroups in utility
     */
    private void AddMeasure()
    {
        LinearLayout melodyArea = (LinearLayout) findViewById( R.id.melodyArea );

        // loop for each beat
        for ( int i = 1; i <= 4; i++ )
        {
            LinearLayout beat = new LinearLayout( this );
            beat.setOrientation( LinearLayout.VERTICAL );
            beat.setClipToPadding( false );
            melodyArea.addView( beat );

            TextView chordText = new TextView( this );
            chordText.setText( "" );
            chordText.setTextAppearance( R.style.fancyTextStyle );
            chordText.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 45 );
            chordText.setPadding( 20, - 15, 0, - 30 );
            beat.addView( chordText );
            m_harmonyReference.add( chordText );

            LinearLayout notegrid = new LinearLayout( this );
            if ( i % 2 == 1 )
            {
                notegrid.setBackgroundResource( R.color.colorPrimaryDark );
            }
            else
            {
                notegrid.setBackgroundResource( R.color.colorBackground );
            }
            notegrid.setElevation( 5 );
            beat.addView( notegrid );

            //loop for each note
            for ( int j = 0; j < 4; j++ )
            {
                RadioGroup noteGroup = new RadioGroup( this );
                notegrid.addView( noteGroup );
                m_util.Add( noteGroup );
                for ( int k = 0; k < 7; k++ )
                {
                    ToggleableRadioButton note = new ToggleableRadioButton( this );
                    note.setId( k );
                    note.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 1 );
                    noteGroup.addView( note, 0 );
                }
            }

            TextView count = new TextView( this );
            count.setText( String.valueOf( i ) );
            count.setPadding( 40, - 15, 0, 0 );
            beat.addView( count );

        }
    }

    /*
    NAME

        DeleteMeasure - deletes a measure from the melodyarea

    SYNOPSIS

        void DeleteMeasure()

    DESCRIPTION

        checks that there is more then one measure
        deletes the views and references for each beat of the last measure
     */
    private void DeleteMeasure()
    {
        ViewGroup melodyView = (ViewGroup) findViewById( R.id.melodyArea );
        if ( melodyView.getChildCount() > 4 )
        {
            melodyView.removeViews( melodyView.getChildCount() - 4, 4 );
            for ( int i = 0; i < 4; i++ )
            {
                m_harmonyReference.remove( m_harmonyReference.size() - 1 );
                for ( int j = 0; j < 4; j++ )
                {
                    m_util.Delete();
                }
            }
        }
    }

    /*
    NAME

        Clearall - resets the page

    SYNOPSIS

        void ClearAll()

    DESCRIPTION

        deletes all extra measures
        clears all checked radio buttons and chord textviews
     */
    private void ClearAll()
    {
        while ( m_harmonyReference.size() > 4 )
        {
            DeleteMeasure();
        }
        List<RadioGroup> ns = m_util.GetNoteSelectors();
        for ( RadioGroup rg : ns )
        {
            rg.clearCheck();
        }
        for ( TextView tv : m_harmonyReference )
        {
            tv.setText( "" );
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

        ( (TextView) popView.findViewById( R.id.infotext ) ).setText( R.string.melody_info );

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

        ToggleableRadioButton - nested class that adds extra functionality to radio buttons

    SYNOPSIS

        void toggle()

    DESCRIPTION

        allows the radio button to be toggled off on press
        also clears all chords so they don't reflect the old melody
     */
    private class ToggleableRadioButton extends RadioButton
    {

        public ToggleableRadioButton( Context context )
        {
            super( context );
        }

        @Override
        public void toggle()
        {
            if ( isChecked() )
            {
                if ( getParent() instanceof RadioGroup )
                {
                    ( (RadioGroup) getParent() ).clearCheck();
                }
            }
            else
            {
                setChecked( true );
            }
            for ( TextView tv : m_harmonyReference )
            {
                tv.setText( "" );
            }
        }
    }

}

