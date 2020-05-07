package ru.pylaev.springexample;

import java.util.ArrayList;
import java.util.List;

public class MusicPlayer {
    private List<Music> playList = new ArrayList<Music>();
    private String playerModel;
    private int volume;

    //Ioc
    public MusicPlayer(List<Music> playList) {
        this.playList = playList;
    }

    public MusicPlayer(Music music) {
        playList.add(music);
    }

    public MusicPlayer() {    }

    public void setPlayList(List<Music> playList) {
        this.playList = playList;
    }

    public void setPlayerModel(String playerName) {
        this.playerModel = playerModel;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void playMusic() {
        if (!playList.isEmpty()) {
            System.out.println("Playing: " + playList.get(0).getSong());
            for (int i = 1; i < playList.size(); ++i) {
                System.out.println("In queue: " + playList.get(i).getSong());
            }
        }
        else {
            System.out.println("No songs");
        }
    }

    public void getPlayerInfo() {
        System.out.println("Info about player: ");
        System.out.println("    - Model: " + playerModel);
        System.out.println("    - Volume: " + volume);
    }
}
