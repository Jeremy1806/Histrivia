package com.example.guessingthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;

import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    String[] images = new String[100] ;
    String[] names = new String[100] ;
    ArrayList<Integer> answers = new ArrayList<Integer>();
    int position_of_correct,number_of_array;
    Button button0,button1,button2,button3;
    ImageView imageView;

    public class DownloadingContent extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result="";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data!=-1){
                    char current = (char) data;
                    result+=current;
                    data = reader.read();
                }
                return result;
            }catch (Exception e){
                e.printStackTrace();
            }

            return "Done";
        }
    }

    public class DownloadingImages extends AsyncTask<String,Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap myBitMap = BitmapFactory.decodeStream(in);
                return myBitMap;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    public void selectAnswer(View view){
        if(view.getTag().toString().equals(Integer.toString(position_of_correct))){
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();

            options();
        }
        else{
            Toast.makeText(this, "Wrong!", Toast.LENGTH_SHORT).show();
        }
    }

    public void options(){
        Random rand = new Random();

        position_of_correct = rand.nextInt(4);
        number_of_array=rand.nextInt(50);
        answers.clear();
        for(int i = 0 ;i<4;i++){
            if(i==position_of_correct){
                answers.add(number_of_array);
            }
            else{
                int wrongAnswer = rand.nextInt(50);
                while (names[wrongAnswer].equals(names[number_of_array])){
                    wrongAnswer = rand.nextInt(50);
                }
                answers.add(wrongAnswer);
            }
        }
        Bitmap myImage;
        DownloadingImages task = new DownloadingImages();
        try {
            myImage = task.execute("https://www.onthisday.com"+images[number_of_array]).get();
            imageView.setImageBitmap(myImage);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        button0.setText(names[answers.get(0)]);
        button1.setText(names[answers.get(1)]);
        button2.setText(names[answers.get(2)]);
        button3.setText(names[answers.get(3)]);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String str="";
        button0 = findViewById(R.id.button1);
        button1 = findViewById(R.id.button2);
        button2 = findViewById(R.id.button3);
        button3 = findViewById(R.id.button4);
        imageView = findViewById(R.id.imageView);
        String result=null;
        DownloadingContent task = new DownloadingContent();
        try {
            result = task.execute("https://www.onthisday.com/people/most-popular.php").get();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        Log.i("Result", result);

        Pattern p = Pattern.compile("<img src=(.*?) width");
        Matcher m = p.matcher(result);
        int i=0;
        while(m.find()){
            str=m.group(1);
            str = str.substring(1, str.length() - 1);
            images[i]=str;
            i++;
        }
        p = Pattern.compile("data-src=(.*?) width");
        m=p.matcher(result);
        while (m.find()){
            str=m.group(1);
            str = str.substring(1, str.length() - 1);
           images[i]=str;
            i++;
        }
        //NAMES
        p = Pattern.compile("alt=(.*?) />");
        m=p.matcher(result);
        i=0;
        while (m.find()){
            str=m.group(1);
            str = str.substring(1, str.length() - 1);
            names[i]=str;
            i++;
        }

        options();



    }
}