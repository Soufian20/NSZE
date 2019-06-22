package com.example.myapplication;

import java.io.Serializable;
import java.util.ArrayList;

public class Channel implements Serializable {
    private String frequency;
    private String channel;
    private int quality;
    private String program;
    private String provider;

    public Channel(String frequency, String channel, int quality, String program, String provider)
    {
        this.frequency = frequency;
        this.channel = channel;
        this.quality = quality;
        this.program = program;
        this.provider = provider;
    }

    public String getProgram() {
        return program;
    }

    public String getFrequency() {
        return frequency;
    }

    public String getChannel() {
        return channel;
    }

    public int getQuality() {
        return quality;
    }

    public String getProvider() {
        return provider;
    }

}
