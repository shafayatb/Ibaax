package UI;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

import Event.IValidation;

/**
 * Created by iBaax on 2/18/16.
 */
public class EdittextRequired implements IValidation {
    String text;
    TextView Error;
    String ErrorMessage;

    public EdittextRequired(String text, TextView Error, String ErrorMessage) {
        this.text = text;
        this.Error = Error;
        this.ErrorMessage = ErrorMessage;

    }

    @Override
    public Boolean Invoke() {
        Log.v("editt", "edittextRequired1");
        if (text.length() == 0) {
            Log.v("editt", "edittextRequired2");
            //Error.setVisibility(View.VISIBLE);
            Error.setText(ErrorMessage);
            //Error.setTextColor(Color.parseColor("#ff0000"));
            return false;
        }

        Error.setText("");
        Log.v("editt", "edittextRequired3");
        return true;
    }

}