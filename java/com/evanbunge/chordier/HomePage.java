package com.evanbunge.chordier;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

/*
HomePage
    sets up the main page of the app
 */
public class HomePage extends AppCompatActivity implements View.OnClickListener
{
    // used to provide context to MCMatrix
    public static Context m_context;

    /*
    NAME

        onCreate - sets up the activity

    SYNOPSIS

        void onCreate( Bundle a_savedInstance )

            a_savedInstance --> bundle of data that is passed to superclass

    DESCRIPTION

        initializes member variable and sets click listeners.
     */
    @Override
    public void onCreate( Bundle a_savedInstanceState )
    {
        super.onCreate( a_savedInstanceState );
        setContentView( R.layout.activity_home_screen );

        m_context = getApplicationContext();

        findViewById( R.id.load_song ).setOnClickListener( this );
        findViewById( R.id.new_chord ).setOnClickListener( this );
        findViewById( R.id.new_melody ).setOnClickListener( this );

        findViewById( R.id.infoButton ).setOnClickListener( new View.OnClickListener()
        {
            @Override
            public void onClick( View view )
            {
                ShowInfo();
            }
        } );
    }

    /*
    NAME

        onClick - event handler for button press

    SYNOPSIS

        onClick( View a_view )

            a_view --> the view selected

    DESCRIPTION

        deciphers which view was pressed.
        launches intent to KeyPage with information of which activity was chosen
        or opens a pop up menu to choose a file to load.
     */
    @Override
    public void onClick( View a_view )
    {
        Intent keySelect;
        switch ( a_view.getId() )
        {
            case R.id.new_chord:
                keySelect = new Intent( this, KeyPage.class );
                keySelect.putExtra( "activity", ChordPage.class );
                startActivity( keySelect );
                break;
            case R.id.load_song:
                LoadMenu();
                break;
            case R.id.new_melody:
                keySelect = new Intent( this, KeyPage.class );
                keySelect.putExtra( "activity", MelodyPage.class );
                startActivity( keySelect );
                break;
        }
    }

    /*
    NAME

        LoadMenu - opens a pop up menu to choose file to load

    SYNOPSIS

        void LoadMenu()

    DESCRIPTION

        Check for file to load.
        create pop up menu and populate it with files to choose from
        set click listeners for each menu item to launch ChordPage with filename as extra
     */
    private void LoadMenu()
    {
        File path = getFilesDir();
        String[] fileList = path.list();
        if ( fileList.length == 0 )
        {
            Toast.makeText( HomePage.this, "No files available to load", Toast.LENGTH_SHORT )
                 .show();
        }
        else
        {
            PopupMenu menu = new PopupMenu( this, findViewById( R.id.load_song ), Gravity.CENTER );
            for ( String file : fileList )
            {
                menu.getMenu()
                    .add( file );
            }
            menu.setOnMenuItemClickListener( new PopupMenu.OnMenuItemClickListener()
            {
                @Override
                public boolean onMenuItemClick( MenuItem item )
                {
                    String filename = (String) item.getTitle();
                    Intent chordPage = new Intent( HomePage.this, ChordPage.class );
                    chordPage.putExtra( "load", true );
                    chordPage.putExtra( "filename", filename );
                    startActivity( chordPage );
                    return true;
                }
            } );
            menu.show();
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

        ( (TextView) popView.findViewById( R.id.infotext ) ).setText( R.string.home_info );

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
}
