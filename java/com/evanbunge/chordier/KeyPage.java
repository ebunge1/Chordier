package com.evanbunge.chordier;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

/*
KeyPage
    lets user choose key for chord progression or melody
 */
public class KeyPage extends AppCompatActivity implements View.OnClickListener
{

    /*
    NAME

        radioListener - event handler for the radio buttons

    SYNOPSIS

        void onCheckChanged( RadioGroup a_radioGroup, int a_checkedId )

            a_radioGroup --> the radioGroup that was selected
            a_checkedId --> the id of the selected radiobutton

    DESCRIPTION

        sets the text of the selected radiobutton to the appropriate textview
     */
    RadioGroup.OnCheckedChangeListener radioListener = new RadioGroup.OnCheckedChangeListener()
    {
        @Override
        public void onCheckedChanged( RadioGroup a_radioGroup, int a_checkedId )
        {
            switch ( a_radioGroup.getId() )
            {
                case R.id.key:
                    ( (TextView) findViewById( R.id.keytext ) ).setText( ( (RadioButton) findViewById( a_checkedId ) ).getText() );
                    break;
                case R.id.pitch:
                    switch ( (String) ( (RadioButton) findViewById( a_checkedId ) ).getText() )
                    {
                        case "Sharp":
                            ( (TextView) findViewById( R.id.pitchtext ) ).setText( "♯" );
                            break;
                        case "Natural":
                            ( (TextView) findViewById( R.id.pitchtext ) ).setText( "" );
                            break;
                        case "Flat":
                            ( (TextView) findViewById( R.id.pitchtext ) ).setText( "♭" );
                            break;
                        default:
                            break;
                    }
                    break;
                case R.id.mode:
                    ( (TextView) findViewById( R.id.modetext ) ).setText( ( (RadioButton) findViewById( a_checkedId ) ).getText() );
                    break;
                default:
                    break;
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

        set up the toolbar and sets click listeners.
        sets checks of the default radiobuttons
     */
    @Override
    protected void onCreate( Bundle savedInstanceState )
    {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_key_selection );

        Toolbar myToolbar = (Toolbar) findViewById( R.id.key_toolbar );
        myToolbar.setTitle( "Choose a key" );
        setSupportActionBar( myToolbar );

        Button okButton = (Button) findViewById( R.id.ok_button );
        okButton.setOnClickListener( this );

        RadioGroup keyGroup = (RadioGroup) findViewById( R.id.key );
        RadioGroup pitchGroup = (RadioGroup) findViewById( R.id.pitch );
        RadioGroup modeGroup = (RadioGroup) findViewById( R.id.mode );

        keyGroup.setOnCheckedChangeListener( radioListener );
        pitchGroup.setOnCheckedChangeListener( radioListener );
        modeGroup.setOnCheckedChangeListener( radioListener );

        keyGroup.check( R.id.defaultKey );
        pitchGroup.check( R.id.defaultPitch );
        modeGroup.check( R.id.defaultMode );
    }

    /*
    NAME

        onClick - event handler for button press

    SYNOPSIS

        onClick( View v )

            v --> the view selected (unused)

    DESCRIPTION

        concatenates text from selected radiobuttons to get the selected key
        and starts the activity gotten from the intent from HomePage.
        ends this activity.
     */
    @Override
    public void onClick( View v )
    {
        String finalkey = (String) ( (TextView) findViewById( R.id.keytext ) ).getText();
        finalkey += (String) ( (TextView) findViewById( R.id.pitchtext ) ).getText();
        if ( ( (TextView) findViewById( R.id.modetext ) ).getText().equals( "Minor" ) )
        {
            finalkey += "m";
        }

        Class activity = (Class) getIntent().getSerializableExtra( "activity" );
        Intent i = new Intent( this, activity );
        i.putExtra( "key", finalkey );
        startActivity( i );

        finish();
    }
}
