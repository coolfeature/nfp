package ionas.nfp.dialogs;

import ionas.nfp.R;
import ionas.nfp.db.Observation;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

public class ShareDialog extends Dialog {

	TextView tvEmail;
	TextView tvSms;
	Observation[] observations;

	Context context;
	String msg;

	public ShareDialog(Context context, Observation[] observations) {
		super(context);
		this.context = context;
		this.observations = observations;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.share_dialog);

		tvEmail = (TextView) findViewById(R.id.tvEmail);
		tvSms = (TextView) findViewById(R.id.tvSms);

		StringBuilder finalMsg = new StringBuilder();
		msg = null;
		for (Observation observation : observations) {
			msg = observation.toString() + "\r\n";
			finalMsg.append(msg);
		}
		finalMsg.toString();
		display();
	}

	public void display() {

		tvEmail.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent email = new Intent(Intent.ACTION_SEND);
				email.putExtra(Intent.EXTRA_EMAIL, new String[] { "" });
				email.putExtra(Intent.EXTRA_SUBJECT,
						"Zapiski z kalendarzyka Migotki");
				email.putExtra(Intent.EXTRA_TEXT, msg);
				email.setType("message/rfc822");
				context.startActivity(Intent.createChooser(email,
						"Wybierz klienta pocztowego :"));
			}
		});

		tvSms.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent sendIntent = new Intent(Intent.ACTION_VIEW);
				if (msg.length() > 765) {
					Toast.makeText(context, "The message is far too long",Toast.LENGTH_SHORT).show();
				} else {
					sendIntent.putExtra("sms_body", msg);
					sendIntent.setType("vnd.android-dir/mms-sms");
					context.startActivity(sendIntent);
				}
			}
		});

	}
}
