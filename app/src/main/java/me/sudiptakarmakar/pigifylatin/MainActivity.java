package me.sudiptakarmakar.pigifylatin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends ActionBarActivity {

    TextToSpeech ttsObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView textViewPigOutput = (TextView) findViewById(R.id.textViewPigOutput);
        final EditText editTextEngInput = (EditText) findViewById(R.id.editTextEngInput);
        final Button buttonCopyEng = (Button)findViewById(R.id.buttonCopyEng);
        final Button buttonCopyPig = (Button)findViewById(R.id.buttonCopyPig);
        final Button buttonClear = (Button)findViewById(R.id.buttonClear);
        final Button speakerButton = (Button) findViewById(R.id.speakerButton);
        speakerButton.setEnabled(false);

        buttonCopyEng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editTextEngInput = (EditText) findViewById(R.id.editTextEngInput);
                String engText = String.valueOf(editTextEngInput.getText());
                if(engText.length() > 0){
                    android.content.ClipboardManager clipboard =
                            (android.content.ClipboardManager)
                                    getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip =
                            android.content.ClipData.newPlainText("English Text", engText);
                    clipboard.setPrimaryClip(clip);
                    Context context = getApplicationContext();
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonCopyPig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textViewPigOutput;
                textViewPigOutput = (TextView) findViewById(R.id.textViewPigOutput);
                String pigText = String.valueOf(textViewPigOutput.getText());
                if(pigText.length() > 0) {
                    android.content.ClipboardManager clipboard =
                            (android.content.ClipboardManager)
                                    getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip =
                            android.content.ClipData.newPlainText("PigLatin Text", pigText);
                    clipboard.setPrimaryClip(clip);
                    Context context = getApplicationContext();
                    Toast.makeText(
                            context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editTextEngInput.setText("");
                textViewPigOutput.setText("");
            }
        });

        editTextEngInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

                if (editTextEngInput.getText().length() > 0) {
                    buttonCopyEng.setEnabled(true);
                    buttonClear.setEnabled(true);
                    buttonCopyPig.setEnabled(true);
                    //textViewPigOutput.setBackgroundColor(Color.TRANSPARENT);
                    textViewPigOutput.setBackground(null);
                    speakerButton.setEnabled(true);
                    speakerButton.setVisibility(View.VISIBLE);

                } else{
                    textViewPigOutput.setBackgroundResource(R.drawable.oinksalot);
                    buttonCopyEng.setEnabled(false);
                    buttonClear.setEnabled(false);
                    buttonCopyPig.setEnabled(false);
                    speakerButton.setEnabled(false);
                    speakerButton.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                TextTranslatorTask task = new TextTranslatorTask();
                task.execute();
            }
        });

        speakerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String eng = String.valueOf(editTextEngInput.getText());
                String[] mid = {" ; can be expressed as, ", " , in Pig-Latin, becomes, ",
                        " ; translates to, ", " ; is transformed into, "};
                String pig = String.valueOf(textViewPigOutput.getText());

                Random r = new Random();
                int i = r.nextInt(mid.length);

                speakText(eng + mid[i] + pig);
                /*Context context = getApplicationContext();
                Toast.makeText(
                        context, "opiedCay otay ipboardclaysdf.", Toast.LENGTH_SHORT).show();*/
            }
        });

        ttsObj = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    ttsObj.setLanguage(Locale.US);
                }
            }
        });

    }

    @Override
    public void onPause(){
        if(ttsObj !=null){
            ttsObj.stop();
            ttsObj.shutdown();
        }
        super.onPause();
    }

    @Override
    public void onDestroy(){
        if(ttsObj !=null){
            ttsObj.stop();
            ttsObj.shutdown();
        }
        super.onDestroy();
    }

    public void speakText(String toSpeak){
        ttsObj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        final Context context = getApplicationContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog dialog = null;
        switch (id){
            case R.id.action_about:
                String text = "Sudipta Karmakar\n";
                text = text + "[ @karmakarsudipta ]\n";
                text = text + "[ http://sudiptakarmakar.me ]";
                text = text + "[ (850)570-2504 ]";
                builder.setMessage(text).setTitle("Developed by:");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                        //Toast.makeText(context, "Wow!", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;

            /*case R.id.action_settings:
                builder.setMessage("R.string.dialog_message").setTitle("R.string.dialog_title");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                dialog = builder.create();
                dialog.show();
                break;*/

            case R.id.action_exit:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class TextTranslatorTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            EditText editTextEngInput = (EditText) findViewById(R.id.editTextEngInput);
            return PigUtility.getPigLatin(String.valueOf(editTextEngInput.getText()));
        }

        @Override
        protected void onPostExecute(String result) {
            TextView textViewPigOutput = (TextView) findViewById(R.id.textViewPigOutput);
            textViewPigOutput.setText(result);
        }
    }
    /*
    public class FireMissilesDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Hello")
                    .setPositiveButton("Fire", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // FIRE ZE MISSILES!
                            Context context = getApplicationContext();
                            Toast.makeText(
                                    context, "Click fire!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User cancelled the dialog
                            Context context = getApplicationContext();
                            Toast.makeText(
                                    context, "Click cancel!", Toast.LENGTH_SHORT).show();
                        }
                    });
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }*/


}
