/*
 * This is the client side of an application that communicates over Sockets.
 * The server side may be hosted on any server with an IP address.
 * As of Android 4 all creation and use of sockets must be done on a background thread.
 * When the button is clicked, the thread is started.
 * Notice the permissions in the Manifest.
 */
package com.course.example.socketdemo;

import java.io.*;
import java.net.*;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class SocketDemo extends Activity {

	private TextView text;
	private Button button;
	private EditText field;

	private Socket socket = null;
	private Thread t = null;
	
	private double radius = 0;
	private double area = 0;
	private boolean loop = true;

	// IO streams
	private DataOutputStream toServer;
	private DataInputStream fromServer;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		text = (TextView) findViewById(R.id.TextView01);
		button = (Button) findViewById(R.id.Button01);
		field = (EditText) findViewById(R.id.EditText01);
		field.setHint("Enter Radius");

		button.setOnClickListener(new OnClickListener() {
			public void onClick(View view) {

				// Get the radius from the text field
				radius = Double.parseDouble(field.getText().toString());
				loop = true;

				// create thread and do socket work
				t = new Thread(background);
				t.start();

				// wait for thread to finish
				while (loop) {
					if (t == null) {
						String str = String.format("%.2f", area);
						// Display to the text area
						text.append("Radius is " + radius + "\n");
						text.append("Area received from the server is " + str
								+ '\n');
						field.setText("");
						field.setHint("Enter Radius");
						loop = false;
					}
				}

			}

		});

	}

	// background task for socket work
	Runnable background = new Runnable() {
		public void run() {
			try {
				// Create a socket to connect to the server
				socket = new Socket("frodo.bentley.edu", 10000);

				// Create an input stream to receive data from the server
				fromServer = new DataInputStream(socket.getInputStream());

				// Create an output stream to send data to the server
				toServer = new DataOutputStream(socket.getOutputStream());
				// Send the radius to the server
				toServer.writeDouble(radius);
				toServer.flush();

				// Get area from the server
				area = fromServer.readDouble();
				socket.close();
				t = null;
				
				Log.i("SocketDemo", "IO Complete");

			} catch (IOException ex) {
				Log.e("SocketDemo", "IO Exception");
			}
		}
	};

}