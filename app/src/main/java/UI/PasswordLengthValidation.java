package UI;

import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import Event.IValidation;

/**
 * Created by iBaax on 2/18/16.
 */
public class PasswordLengthValidation implements IValidation {

    String text;
    TextView Error;
    String ErrorMessage;

    public PasswordLengthValidation(String text, TextView Error,
                                    String ErrorMessage) {

        this.text = text;
        this.Error = Error;
        this.ErrorMessage = ErrorMessage;
    }

    @Override
    public Boolean Invoke() {

        Log.v("passw", "PasswordLengthValidation1");

        // TODO Auto-generated method stub
        if (text.length() < 6) {
            Log.v("passw", "PasswordLengthValidation2");
            //Error.setVisibility(View.VISIBLE);
            Error.setText(ErrorMessage);
            //Error.setTextColor(Color.parseColor("#ff0000"));
            return false;
        } else {
            Log.v("passw", "PasswordLengthValidation3");
            Error.setText("");
            return true;
        }

    }

}
