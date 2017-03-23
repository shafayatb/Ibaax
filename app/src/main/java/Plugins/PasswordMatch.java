package Plugins;

import android.graphics.Color;
import android.widget.TextView;

import Event.IValidation;

/**
 * Created by iBaax on 2/23/16.
 */
public class PasswordMatch implements IValidation {

    private String Password;
    private String RetypePassowrd;
    private TextView ErrorMessageTextview;

    public PasswordMatch(String Password, String RetypePassowrd,
                         TextView ErrorMessageTextview) {

        this.Password = Password;
        this.RetypePassowrd = RetypePassowrd;
        this.ErrorMessageTextview = ErrorMessageTextview;
    }

    @Override
    public Boolean Invoke() {

        if (!Password.equals(RetypePassowrd)) {

            ErrorMessageTextview.setText("Passwords do not match");
            //ErrorMessageTextview.setTextColor(Color.parseColor("#ff0000"));
            return false;

        }

        ErrorMessageTextview.setText("");
        return true;
    }
}
