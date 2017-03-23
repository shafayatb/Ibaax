package UI;


import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.ibaax.com.ibaax.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Event.IEvent;
import Event.IHttpResponse;
import ServiceInvoke.HttpRequest;

public class MessageBox {

    public static void Show(Context context, String Title) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(Title)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do some thing here which you need
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();

    }

    public static void SaveSearchDialog(final Context context, final String APILink, final String WebLink, final String LocationName) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.popup_save_search, null);
        builder.setView(dialogView);
        final EditText editText = (EditText) dialogView.findViewById(R.id.txt_save_search);
        editText.setText(LocationName);
        builder.setTitle("Enter Search Name:")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().trim().length() > 0) {
                            Map<String, String> jsonParams = new HashMap<String, String>();
                            jsonParams.put("SavedSearchName", editText.getText().toString());
                            jsonParams.put("NotificationType", "2");
                            jsonParams.put("SavedSearchUrl", WebLink);
                            new HttpRequest(context, new IHttpResponse() {
                                @Override
                                public void RequestSuccess(Object response) {

                                    try {
                                        JSONObject jo = new JSONObject((String) response);
                                        if (jo.getInt("flag") == 1) {
                                            Toast.makeText(context, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {

                                    }

                                }

                                @Override
                                public void RequestFailed(String response) {

                                }
                            }).HttpPostProperty(jsonParams, APILink);

                        } else {
                            editText.setText(LocationName);
                        }
                    }
                });


        AlertDialog alert = builder.create();
        alert.show();


    }

    public static void SaveSearchRename(final Context context, final String APILink, final String LocationName, final IEvent listener) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = inflater.inflate(R.layout.popup_save_search, null);
        builder.setView(dialogView);
        final EditText editText = (EditText) dialogView.findViewById(R.id.txt_save_search);
        editText.setText(LocationName);
        builder.setTitle("Enter Search Name:")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (editText.getText().toString().trim().length() > 0) {
                            String FullURL = APILink + editText.getText().toString().replace(" ", "%20");
                            new HttpRequest(context, new IHttpResponse() {
                                @Override
                                public void RequestSuccess(Object response) {

                                    try {
                                        JSONObject jo = new JSONObject((String) response);
                                        if (jo.getInt("flag") == 1) {
                                            listener.onClick(editText.getText().toString());
                                        } else {
                                            Toast.makeText(context, jo.getString("message"), Toast.LENGTH_SHORT).show();
                                        }
                                    } catch (Exception e) {

                                    }

                                }

                                @Override
                                public void RequestFailed(String response) {

                                }
                            }).HttpPostProperty(null, FullURL);

                        } else {
                            editText.setText(LocationName);
                        }
                    }
                });


        AlertDialog alert = builder.create();
        alert.show();


    }

}
