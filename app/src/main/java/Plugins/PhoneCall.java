package Plugins;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by S.R Rain on 1/16/2016.
 */
public class PhoneCall {


    public static void Call(Context context, String Number) {


        Uri number = Uri.parse("tel:" + Number);
        Intent callIntent = new Intent(Intent.ACTION_DIAL, number);
        context.startActivity(callIntent);
    }
}
