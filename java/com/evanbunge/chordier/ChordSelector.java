package com.evanbunge.chordier;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.List;

/*
ChordSelector
    Handles all UI features of the chord selectors
    has two states:
        edit: shows scrollable list of chord choices
        display: shows the chord choice and two buttons to change or delete
 */
public class ChordSelector
{
    private Activity m_currentActivity;
    private ChordUtil m_chordUtil;
    // tells if a selector is editing
    private boolean m_editFlag;
    // list of all created selectors in order
    private List<ViewGroup> m_selectorReference;

    /*
    NAME

        selectionListener - event handler for chord choices

    SYNOPSIS

        void onClick( View a_view )

            a_view --> the view selected

    DESCRIPTION

        get the chord name from the buttons text.
        get the selector by going up the chain of parent views.
        edit the progression in the utility class.
        change views to the display state.
        unset edit flag.
     */
    private View.OnClickListener selectionListener = new View.OnClickListener()
    {
        @Override
        public void onClick( View a_view )
        {
            String choice = (String) ( (Button) a_view ).getText();

            ViewGroup selector = (ViewGroup) a_view.getParent()
                                                   .getParent()
                                                   .getParent();

            m_chordUtil.Edit( m_selectorReference.indexOf( selector ), choice );

            selector.getChildAt( 0 )
                    .setVisibility( View.GONE );

            TextView display = (TextView) selector.getChildAt( 1 );
            display.setVisibility( View.VISIBLE );
            display.setText( choice );

            selector.getChildAt( 2 )
                    .setVisibility( View.VISIBLE );

            selector.getChildAt( 3 )
                    .setVisibility( View.VISIBLE );

            UnsetEditFlag();
        }
    };

    /*
    NAME

        deleteListener - event handler to delete chord

    SYNOPSIS

        void onClick( View a_view )

            a_view --> the view selected

    DESCRIPTION

        check if the edit flag is set.
        if not, delete chord from progression and remove the selector
     */
    private View.OnClickListener deleteListener = new View.OnClickListener()
    {
        @Override
        public void onClick( View a_view )
        {
            if ( IsEditing() )
            {
                Toast.makeText( m_currentActivity, "Cannot delete a chord while editing another.", Toast.LENGTH_SHORT )
                     .show();
            }
            else
            {
                LinearLayout selector = (LinearLayout) a_view.getParent();

                m_chordUtil.Delete(
                        m_selectorReference.indexOf( selector )
                );

                m_selectorReference.remove( selector );
                ( (ViewGroup) selector.getParent() ).removeView( selector );
            }
        }
    };

    /*
    NAME

        changeListener - event handler to edit the chord

    SYNOPSIS

        void onClick( View a_view )

            a_view --> the view selected

    DESCRIPTION

        check if the edit flag is set.
        if not, set the edit flag, change selector to edit state,
            and populate the scroll list with chords
     */
    private View.OnClickListener changeListener = new View.OnClickListener()
    {
        @Override
        public void onClick( View view )
        {
            if ( IsEditing() )
            {
                Toast.makeText( m_currentActivity, "Cannot edit two chords simultaneously.", Toast.LENGTH_SHORT )
                     .show();
            }
            else
            {
                SetEditFlag();

                ViewGroup selector = (ViewGroup) view.getParent();
                selector.getChildAt( 1 )
                        .setVisibility( View.GONE );
                selector.getChildAt( 2 )
                        .setVisibility( View.GONE );
                selector.getChildAt( 3 )
                        .setVisibility( View.GONE );

                ViewGroup scrView = (ViewGroup) selector.getChildAt( 0 );
                scrView.setVisibility( View.VISIBLE );

                ViewGroup chordBox = (ViewGroup) scrView.getChildAt( 0 );

                chordBox.removeAllViews();
                Populate( chordBox );
            }
        }
    };

    /*
    NAME

        ChordSelector - constructor for ChordSelector class

    SYNOPSIS

        ChordSelector( Activity a_activity, ChordUtil a_chordUtil )

            a_activity --> the current activity
            a_chordUtil --> the ChordUtil being used

    DESCRIPTION

        initializes member variables
     */
    public ChordSelector( Activity a_activity, ChordUtil a_chordUtil )
    {
        m_currentActivity = a_activity;
        m_chordUtil = a_chordUtil;
        m_selectorReference = new ArrayList<>();
        m_editFlag = false;
    }

    /*
    NAME

        Create - creates a chord selector

    SYNOPSIS

        void Create()

    DESCRIPTION

        Creates the views in the chord selector and set in edit state
            a vertically scrolling list for chord choice buttons
            text for chosen chord name
            button to edit chord
            button to delete chord
        creates a placeholder in the progression
        populates the scrollable list with chord choices
        sets the edit flag
     */
    public void Create()
    {
        LinearLayout selectionArea = (LinearLayout) m_currentActivity.findViewById( R.id.selection_area );

        LinearLayout selector = new LinearLayout( m_currentActivity );
        selector.setLayoutParams( new FrameLayout.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT ) );
        selector.setOrientation( LinearLayout.VERTICAL );
        selector.setGravity( Gravity.CENTER );
        selector.setPadding( 10, 0, 10, 0 );
        m_selectorReference.add( selector );
        selectionArea.addView( selector, selectionArea.getChildCount() - 1 );

        ScrollView scrView = new ScrollView( m_currentActivity );
        scrView.setLayoutParams( new FrameLayout.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT ) );
        selector.addView( scrView );

        LinearLayout chordBox = new LinearLayout( m_currentActivity );
        chordBox.setLayoutParams( new FrameLayout.LayoutParams( FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT ) );
        chordBox.setOrientation( LinearLayout.VERTICAL );
        scrView.addView( chordBox );

        TextView tv = new TextView( m_currentActivity );
        tv.setVisibility( View.GONE );
        tv.setTextAppearance( R.style.fancyTextStyle_chord );
        tv.setGravity( Gravity.CENTER );
        selector.addView( tv );

        Button change = new Button( m_currentActivity );
        change.setText( R.string.change );
        change.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 30 );
        change.setVisibility( View.GONE );
        change.setOnClickListener( changeListener );
        selector.addView( change );

        Button delete = new Button( m_currentActivity );
        delete.setText( R.string.delete );
        delete.setTextSize( TypedValue.COMPLEX_UNIT_DIP, 30 );
        delete.setVisibility( View.GONE );
        delete.setOnClickListener( deleteListener );
        selector.addView( delete );

        m_chordUtil.Add( "" );
        Populate( chordBox );
        SetEditFlag();
    }

    /*
    NAME

        Create - creates chord selector with preselected chord

    SYNOPSIS

        void Create( String a_chord )

            a_chord --> the chord to set

    DESCRIPTION

        calls the default create method
        adjusts the visibility of the views to the display state
        set the text to the chord name
        deletes the placeholder made in Create()
        unsets the editflag
     */
    public void Create( String a_chord )
    {
        Create();

        LinearLayout selectionArea = (LinearLayout) m_currentActivity.findViewById( R.id.selection_area );

        LinearLayout selector = (LinearLayout) selectionArea.getChildAt( selectionArea.getChildCount() - 2 );

        selector.getChildAt( 0 )
                .setVisibility( View.GONE );
        selector.getChildAt( 2 )
                .setVisibility( View.VISIBLE );
        selector.getChildAt( 3 )
                .setVisibility( View.VISIBLE );

        TextView tv = (TextView) selector.getChildAt( 1 );
        tv.setVisibility( View.VISIBLE );
        tv.setText( a_chord );

        m_chordUtil.DeleteEnd();

        UnsetEditFlag();
    }

    /*
    NAME

        Populate - adds chords to the scrolling list

    SYNOPSIS

        void Populate( ViewGroup a_vGroup )

            a_vGroup --> the ViewGroup to add the chords to

    DESCRIPTION

        checks the toggle button and adds all chords or suggests best chords accordingly
        adds a button for each chord to the scroll list
     */
    private void Populate( ViewGroup a_vGroup )
    {
        List<String> chordOptions;
        if ( ( (ToggleButton) m_currentActivity.findViewById( R.id.suggest ) ).isChecked() )
        {
            chordOptions = m_chordUtil.GetSuggestedChordsFor(
                    m_selectorReference.indexOf( a_vGroup.getParent().getParent() )
            );
        }
        else
        {
            chordOptions = m_chordUtil.GetAllChords();
        }
        for ( String chord : chordOptions )
        {
            Button butt = new Button( m_currentActivity );
            butt.setText( chord );
            butt.setTextAppearance( R.style.fancyTextStyle );
            butt.setBackgroundColor( Color.TRANSPARENT );
            a_vGroup.addView( butt );
            butt.setOnClickListener( selectionListener );
        }
    }

    /*
    NAME

        IsEditing - tells if any selectors are currently editing

    SYNOPSIS

        boolean IsEditing()

    RETURNS

        true if any selectors are currently editing
     */
    public boolean IsEditing()
    {
        return m_editFlag;
    }

    /*
    NAME

        SetEditFlag - sets the edit flag

    SYNOPSIS

        void SetEditingFlag()

    DESCRIPTION

        sets the edit flag and makes the add button invisible
    */
    private void SetEditFlag()
    {
        m_editFlag = true;
        m_currentActivity.findViewById( R.id.add_chord )
                         .setVisibility( View.INVISIBLE );
    }

    /*
    NAME

        UnsetEditFlag - unsets the edit flag

    SYNOPSIS

        void UnsetEditFlag()

    DESCRIPTION

        unsets the edit flag and makes the visible
     */
    private void UnsetEditFlag()
    {
        m_editFlag = false;
        m_currentActivity.findViewById( R.id.add_chord )
                         .setVisibility( View.VISIBLE );
    }

    /*
    NAME

        GetUtil - gets the utility class being used

    SYNOPSIS

        ChordUtil GetUtil()

    RETURNS

        the ChordUtil used by this class
     */
    public ChordUtil GetUtil()
    {
        return m_chordUtil;
    }
}
