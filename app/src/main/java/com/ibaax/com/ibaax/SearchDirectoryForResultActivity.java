package com.ibaax.com.ibaax;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import Adapter.PropertyTypeGridAdapter2;
import Entity.Dictunary;
import Entity.SearchDirectoryFilters;
import Event.IPropertyType;
import Plugins.TextBoxHandler;
import UI.TagView.TagContainerLayout;
import UI.TagView.TagView;

public class SearchDirectoryForResultActivity extends AppCompatActivity {

    static List<Dictunary> directoryList = new ArrayList<>();
    Spinner spnAgentDirectory;
    EditText txtAgentKeyword;
    EditText txtAgentArea;
    SearchDirectoryFilters searchDirectoryFilters;
    UI.ExpandableHeightGridView gridDirectory;
    PropertyTypeGridAdapter2 directoryAdapter;
    TagContainerLayout KeywordTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_directory_for_result);
        getSupportActionBar().setTitle("Filter Directory");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        searchDirectoryFilters = new SearchDirectoryFilters();
        findViews();
    }


    private void findViews() {
        spnAgentDirectory = (Spinner) findViewById(R.id.spn_agent_directory);
        txtAgentKeyword = (EditText) findViewById(R.id.txt_agent_keyword);
        txtAgentArea = (EditText) findViewById(R.id.txt_agent_area);
        gridDirectory = (UI.ExpandableHeightGridView) findViewById(R.id.grid_directory);
        KeywordTags = (TagContainerLayout) findViewById(R.id.tag_keyword);
        InitGridDirectory();
        AddTags();
    }

    void InitGridDirectory() {
        if (!(directoryList.size() > 0)) {
            directoryList.add(new Dictunary(4, "Builder/Developer", true));
            directoryList.add(new Dictunary(1, "Real Estate Agent", true));
            directoryList.add(new Dictunary(2, "Real Estate Brokerage", true));
        }

        directoryAdapter = new PropertyTypeGridAdapter2(this, directoryList,
                new IPropertyType() {
                    @Override
                    public void OnClick(Object obj) {

                    }
                });
        gridDirectory.setAdapter(directoryAdapter);
        gridDirectory.setExpanded(true);

    }


    void AddTags() {

        txtAgentKeyword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_NEXT) {
                    KeywordTags.addTag(txtAgentKeyword.getText().toString());
                }else if(i==EditorInfo.IME_ACTION_DONE){
                    KeywordTags.addTag(txtAgentKeyword.getText().toString());
                }

                return false;
            }
        });

        KeywordTags.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {
                KeywordTags.removeTag(position);
            }

            @Override
            public void onTagLongClick(int position, String text) {

            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent();
            setResult(0, intent);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filters, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
    }


    public void btn_directory_filter(View view) {

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < directoryList.size(); i++) {
            if (directoryList.get(i).IsSelected) {
                sb.append(directoryList.get(i).ID).append(",");

            }
        }
        searchDirectoryFilters.DirectoryType = TextBoxHandler.IsNullOrEmpty(sb.toString());
        if (sb.toString().length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }
        sb.delete(0, sb.length());


        searchDirectoryFilters.QueryString = TextBoxHandler
                .IsNullOrEmpty(txtAgentKeyword.getText().toString());
        Intent intent = new Intent();
        intent.putExtra("DirectoryFilterResults", (Parcelable) searchDirectoryFilters);
        setResult(1331, intent);
        finish();
    }


    /*void initDirectoryList() {
        directoryList.add(new Directory("4", "Builder/Developer"));
        directoryList.add(new Directory("1", "Real Estate Agent"));
        directoryList.add(new Directory("2", "Real Estate Brokerage"));

        final List<String> DirectoryTypeList = new ArrayList<>();
        for (int i = 0; i < directoryList.size(); i++) {
            DirectoryTypeList.add(directoryList.get(i).DirectoryType);
        }
        searchDirectoryFilters.DirectoryType = "1";
        ArrayAdapter<String> DirectoryAdp = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, DirectoryTypeList);
        DirectoryAdp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnAgentDirectory.setAdapter(DirectoryAdp);
        spnAgentDirectory.setSelection(1, true);
        spnAgentDirectory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                searchDirectoryFilters.DirectoryType = directoryList.get(i).DirectoryValue;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }*/

}
