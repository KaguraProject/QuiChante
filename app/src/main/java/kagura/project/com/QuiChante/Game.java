package kagura.project.com.QuiChante;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class Game {

    private Context context;
    private Music music;
    private int yearMusic;
    private Random rand = new Random();
    private String artist;
    private int placementGoodAnswerSinger;
    private int placementGoodAnswerSing;
    private int artistPositionInJson;
    private String actualSong;
    private ArrayList<String> listSingsAnswer;


    Game(Context context, int yearMusic){
        this.context = context;
        this.yearMusic = yearMusic;
    }

    List<String> initGameSingers(int artistPosition){
        this.artistPositionInJson = artistPosition;
        artist = null;
        List<String> artists = null;
        try {
            JSONObject object = new JSONObject(loadJSONFromAsset("chanteurs.json"));
            JSONArray arrayYear = object.getJSONArray(Integer.toString(yearMusic));
            artist = arrayYear.get(artistPosition).toString();
            Log.i("artist", artist);
            artists = new ArrayList<>();
            for(int i = 0; i < arrayYear.length(); i++){
                artists.add(arrayYear.get(i).toString());
            }
            Log.i("artisteS", artists.toString());
            artists.remove(artist);
            Log.i("artisteS - ", artists.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return buildListSingersAnswer(artist, artists);
    }

    private List<String> buildListSingersAnswer(String artist, List<String> artists) {
        List<String> listSingersAnswer = new ArrayList<>(Collections.nCopies(4 , ""));
        placementGoodAnswerSinger = rand.nextInt(3);

        for (int i =0; i < 4; i++){
            if(i == placementGoodAnswerSinger){
                listSingersAnswer.set(i, artist);

            }else{
                int random = rand.nextInt(artists.size());
                listSingersAnswer.set(i, artists.get(random));
                artists.remove(random);
            }

            Log.i("listsingersanswer", listSingersAnswer.toString());
        }

        return listSingersAnswer;
    }

    void initGameSings(){

        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset("musiques" + yearMusic + ".json"));
            JSONArray jsonArray = obj.getJSONArray("musiques");
            JSONObject jsonObjArtiste = jsonArray.getJSONObject(artistPositionInJson);
            //artistes = new ArrayList<>();
            music = new Music();

            JSONArray jsonArrayChansons = jsonObjArtiste.getJSONArray("chansons");


            List<String> titres = new ArrayList<>();
            List<String> pathMusics = new ArrayList<>();
            List<Drawable> images = new ArrayList<>();

            for(int i = 0; i < jsonArrayChansons.length(); i++){
                JSONObject jsonObjChansons = jsonArrayChansons.getJSONObject(i);
                titres.add(jsonObjChansons.getString("titre"));
                pathMusics.add(jsonObjChansons.getString("path_music"));
                images.add(context.getResources().getDrawable(context.getResources().getIdentifier(jsonObjChansons.getString("path_image"), "drawable", context.getPackageName())));
            }

            music.setTitres(titres);
            music.setPathMusics(pathMusics);
            music.setImages(images);

        } catch (JSONException e){
            e.printStackTrace();
        }
    }

    void buildListSingsAnswer(int musicPosition) {

        listSingsAnswer = new ArrayList<>(Collections.nCopies(4 , ""));

        placementGoodAnswerSing = rand.nextInt(3);
        actualSong = music.getTitres().get(musicPosition);
        listSingsAnswer.set(placementGoodAnswerSing, music.getTitres().get(musicPosition));
        Log.i("size string", Integer.toString(music.getTitres().size()));
        Log.i("randomGood", Integer.toString(placementGoodAnswerSing));
        Log.i("titres", music.getTitres().toString());

        for (int i =0; i < 4; i++){

            if(i != placementGoodAnswerSing){
                int randomMusic = rand.nextInt(music.getTitres().size());
                if(!listSingsAnswer.contains(music.getTitres().get(randomMusic))){
                    listSingsAnswer.set(i, music.getTitres().get(randomMusic));
                }
                else{
                    i--;
                }
            }
            
            Log.i("listsingsAnswer", listSingsAnswer.toString());

        }
    }

    List<String> getListSingsAnswer(){
        return listSingsAnswer;
    }


    @Nullable
    private String loadJSONFromAsset(String jsonPath) {
        String json;
        try {
            InputStream is = context.getAssets().open(jsonPath);
            int size = is.available();
            byte[] buffer = new byte[size];

            //noinspection ResultOfMethodCallIgnored
            is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        Log.i("json", json);
        return json;
    }

    String[] checkIfArtistFound(String artist) {
        String state;
        if(this.artist.equals(artist)){
            state = "good";
        }else{
            state = "false";
        }
        return buildAnswerStringArray(state, placementGoodAnswerSinger);
    }

    String[] checkIfSongFound(String song) {
        String state;
        if(actualSong.equals(song)){
            state = "good";
        }else{
            state = "false";
        }
        return buildAnswerStringArray(state, placementGoodAnswerSing);
    }

    String getMusicPath(int musicPosition) {
        Log.i("pathMusic", music.getPathMusics().get(musicPosition));
        return music.getPathMusics().get(musicPosition);
    }

    Drawable getImage(int musicPosition) {
        Log.i("pathMusic", music.getPathMusics().get(musicPosition));
        return music.getImages().get(musicPosition);
    }

    private String[] buildAnswerStringArray(String state, int placementGoodAnswer){
        return new String[]{state, Integer.toString(placementGoodAnswer)};
    }
}