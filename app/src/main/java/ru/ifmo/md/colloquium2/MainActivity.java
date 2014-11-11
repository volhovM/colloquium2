package ru.ifmo.md.colloquium2;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class MainActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {
    public static final int VOTING_STATE_LOADER = 0;
    public static final int VOTING_CANDIDATES_LOADER = 1;

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;
    private ArrayList<Candidate> candidates;
    private ArrayAdapter<Candidate> adapter;
    private ListView listView;
    private boolean electionStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        databaseHelper = new DatabaseHelper(this);
        database = databaseHelper.getWritableDatabase();
        candidates = new ArrayList<Candidate>();
        getLoaderManager().initLoader(VOTING_STATE_LOADER, null, this).forceLoad();
        getLoaderManager().initLoader(VOTING_CANDIDATES_LOADER, null, this).forceLoad();
        adapter = new ArrayAdapter<Candidate>(this, R.layout.main_activity_layout, candidates) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                //TODO override
                TextView textView = new TextView(getContext());
                textView.setText(candidates.get(position).toString());
                return textView;
            }
        };
        setContentView(R.layout.main_activity_layout);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (electionStarted) {
                    candidates.get(i).addVote();
                    new AsyncTask<Candidate, Void, Void>() {
                        @Override
                        protected Void doInBackground(Candidate... candidates) {
                            Candidate candidate = candidates[0];
                            ContentValues values = new ContentValues();
                            values.put(DatabaseHelper.CANDIDATE_VOTES, candidate.getVotes());
                            database.update(DatabaseHelper.DATABASE_CANDIDATES_TABLE, values, DatabaseHelper.CANDIDATE_NAME + "='" + candidate.getName() + "'", null);
                            return null;
                        }
                    }.execute(candidates.get(i));
                    adapter.notifyDataSetChanged();
                } else showToast("Election is not started");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void putStateIntoDB() {
        new AsyncTask<Boolean, Void, Void>() {
            @Override
            protected Void doInBackground(Boolean... booleans) {
                boolean voting = booleans[0];
                database.delete(DatabaseHelper.DATABASE_STATS_TABLE, null, null);
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.VOTING_STARTED, voting);
                database.insert(DatabaseHelper.DATABASE_STATS_TABLE, null, values);
                return null;
            }
        }.execute(electionStarted);
    }

    public void onElectionStart(View view) {
        if (!electionStarted) {
            electionStarted = true;
            putStateIntoDB();
        } else showToast("Election is already started");
    }

    public void onElectionStop(View view) {
        if (electionStarted) {
            if (candidates.size() > 0) {
                electionStarted = false;
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        database.delete(DatabaseHelper.DATABASE_CANDIDATES_TABLE, null, null);
                        return null;
                    }
                }.execute();
                putStateIntoDB();
                Bundle bundle = new Bundle();
                Collections.sort(candidates, new Comparator<Candidate>() {
                    @Override
                    public int compare(Candidate candidate, Candidate candidate2) {
                        return candidate.getVotes() > candidate2.getVotes() ? -1 : (candidate.getVotes() == candidate2.getVotes() ? 0 : 1);
                    }
                });
                String[] can = new String[candidates.size()];
                for (int i = 0; i < can.length; i++) {
                    can[i] = candidates.get(i).toString();
                }
                bundle.putStringArray("candidates", can);
                Intent intent = new Intent(this, CandidatesListActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            } else showToast("No candidates, skipping");
        } else showToast("Election is not started");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!database.isOpen()) database = databaseHelper.getWritableDatabase();
    }

    public void onCandidateAdd(View view) {
        if (!electionStarted) {
            final AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Add new candidate");
            LayoutInflater inflater;
            inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.new_feed_dialog,
                    null);
            final TextView candidateName = (TextView) linearLayout.findViewById(R.id.editText);
            alert.setView(linearLayout);
            alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                public void onClick(final DialogInterface dialog, int whichButton) {
                    final String name = String.valueOf(candidateName.getText());
                    if (name != null && !name.equals("")) {
                        candidates.add(new Candidate(name));
                        new AsyncTask<Candidate, Void, Void>() {
                            @Override
                            protected Void doInBackground(Candidate... candidates) {
                                database.delete(DatabaseHelper.DATABASE_CANDIDATES_TABLE,
                                        DatabaseHelper.CANDIDATE_NAME + "='" + name + "'", null);
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DatabaseHelper.CANDIDATE_NAME, name);
                                contentValues.put(DatabaseHelper.CANDIDATE_VOTES, 0);
                                database.insert(DatabaseHelper.DATABASE_CANDIDATES_TABLE, null, contentValues);
                                return null;
                            }
                        }.execute(candidates.get(candidates.size() - 1));
                    } else {
                        Toast.makeText(getBaseContext(), "Wrong name format", Toast.LENGTH_LONG).show();
                    }
                }
            });
            alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                }
            });
            alert.show();
        } else showToast("It's permitted to add candidates after election start");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        switch (i) {
            case VOTING_CANDIDATES_LOADER: {
                return new AsyncTaskLoader<Cursor>(this) {
                    @Override
                    public Cursor loadInBackground() {
                        return database.query(DatabaseHelper.DATABASE_CANDIDATES_TABLE, null, null, null, null, null, DatabaseHelper.CANDIDATE_VOTES + " ASC");
                    }
                };
            }
            case VOTING_STATE_LOADER: {
                return new AsyncTaskLoader<Cursor>(this) {
                    @Override
                    public Cursor loadInBackground() {
                        return database.query(DatabaseHelper.DATABASE_STATS_TABLE, null, null, null, null, null, null);
                    }
                };
            }
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        switch (cursorLoader.getId()) {
            case VOTING_CANDIDATES_LOADER: {
                cursor.moveToFirst();
                candidates.clear();
                while (!cursor.isAfterLast()) {
                    candidates.add(new Candidate(cursor.getString(1), cursor.getInt(2)));
                    cursor.moveToNext();
                }
                break;
            }
            case VOTING_STATE_LOADER: {
                cursor.moveToFirst();
                if (cursor == null || cursor.isClosed() || cursor.isAfterLast() || cursor.isBeforeFirst() || cursor.getColumnCount() < 1) {
//                if (cursor.isNull(0) ||  cursor.isAfterLast()) {
                    electionStarted = false;
                } else {
                    int bool = cursor.getInt(0);
                    electionStarted = bool == 1;
                }
                break;
            }
        }
        cursor.close();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        // FIXME What am i supposed to write here?
    }

}
