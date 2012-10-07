package com.example.insuranceclaim;

import java.io.File;
import java.io.FileOutputStream;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.actionbarsherlock.app.SherlockFragment;

/**
 * A fragment representing a single Vehicle detail screen. This fragment is
 * either contained in a {@link VehicleListActivity} in two-pane mode (on
 * tablets) or a {@link VehicleDetailActivity} on handsets.
 */
public class VehicleDetailFragment extends SherlockFragment {
	private static final String TAG = VehicleDetailFragment.class.getSimpleName();
	
	/**
	 * The fragment argument representing the item ID that this fragment
	 * represents.
	 */
	public static final String ARG_ITEM_ID = "item_id";

	/**
	 * The vehicle this fragment is presenting.
	 */
	private VehicleContent.Vehicle mVehicle;
	
	/**
	 * Signature view to draw on.
	 */
	private SignatureView signatureView;

	/**
	 * Mandatory empty constructor for the fragment manager to instantiate the
	 * fragment (e.g. upon screen orientation changes).
	 */
	public VehicleDetailFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (getArguments().containsKey(ARG_ITEM_ID)) {
			// Load the vehicle content specified by the fragment
			// arguments. In a real-world scenario, use a Loader
			// to load content from a content provider.
			mVehicle = VehicleContent.VEHICLE_MAP.get(getArguments().getString(
					ARG_ITEM_ID));
		}
	}
	
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_vehicle_damage,
				container, false);
		
		signatureView = (SignatureView) rootView.findViewById(R.id.vehicleAndSignature);
		signatureView.setDrawingCacheEnabled(true);
		
		Button save = (Button) rootView.findViewById(R.id.save);
		save.setOnClickListener(saveClickListener);

		// Show the vehicle.
		if (mVehicle != null) {
			signatureView.setBackgroundResource(mVehicle.image);
		}

		return rootView;
	}
	
	private void showSaveResult(String filePath) {
		new AlertDialog.Builder(getSherlockActivity())
			.setMessage(String.format(getString(R.string.waiver_saved), filePath))
			.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			})
			.create().show();
	}
	
	/**
	 * OnClickListener that implements the save functionality.
	 */
	OnClickListener saveClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Bitmap image = signatureView.getDrawingCache();
			
			try {
				File file = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "insurance_form_" + System.currentTimeMillis() + ".png" );
				image.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
				
				showSaveResult(file.getAbsolutePath());
			} catch (Exception e) {
				Log.e(TAG, "Could not save image!", e);
				throw new RuntimeException(e);
			}
		}
	};
}
