package rss.gReader;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class GReaderPreferenceActivity extends PreferenceActivity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.linear);
        addPreferencesFromResource(R.xml.pref);
        Button btn = (Button)findViewById(R.id.Button01);
        btn.setOnClickListener( this);
   }

	public void onClick(View arg0) {
        switch(arg0.getId())
        {
            case R.id.Button01:
	            // タスクはその都度生成する
            	LDRFullFeedParserTask task = new LDRFullFeedParserTask(this);
	            task.execute();
                break;
        }	}
}
