package ru.ifmo.md.colloquium2;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class CandidatesListActivity extends Activity {

//    private DatabaseHelper databaseHelper;
//    private SQLiteDatabase database;
    private String[] candidates;
    private ListView summaryList;
    private ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidates_list);
        Intent intent = getIntent();
        candidates = intent.getExtras().getStringArray("candidates");
//        databaseHelper = new DatabaseHelper(this);
//        database = databaseHelper.getReadableDatabase();
//        getLoaderManager().initLoader(0, null, this).forceLoad();
        summaryList = (ListView) findViewById(R.id.summary_listview);
        arrayAdapter = new ArrayAdapter<String>(this, R.layout.activity_candidates_list, candidates) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = new TextView(getContext());
                textView.setText(candidates[position]);
                if (position == 0) textView.setTextColor(Color.RED);
                return textView;
            }
        };
        summaryList.setAdapter(arrayAdapter);
    }
//
//    @Override
//    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        return new AsyncTaskLoader<Cursor>(this) {
//            @Override
//            public Cursor loadInBackground() {
//                return database.query(DatabaseHelper.DATABASE_CANDIDATES_TABLE, null, null, null, null, null, null);
//            }
//        };
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        cursor.moveToFirst();
//        while (!cursor.isAfterLast()) {
//            candidates.add(new Candidate(cursor.getString(1), cursor.getInt(2)));
//            if (arrayAdapter != null) arrayAdapter.notifyDataSetChanged();
//            cursor.moveToNext();
//        }
//        Collections.sort(candidates, new Comparator<Candidate>() {
//            @Override
//            public int compare(Candidate candidate, Candidate candidate2) {
//                return candidate.getVotes() > candidate2.getVotes() ? 1 : candidate.getVotes() == candidate.getVotes() ? 0 : -1;
//            }
//        });
//    }
//
//    @Override
//    public void onLoaderReset(Loader<Cursor> cursorLoader) {
//
//    }
}
