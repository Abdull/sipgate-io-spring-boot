package com.geeshenk.sipgateio.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.inject.Inject;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.geeshenk.commons.ApplicationException;
import com.geeshenk.commons.DumpUtil;
import com.geeshenk.sipgateio.configuration.ServingResourcesFromFileSystemAdapter;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import com.sun.speech.freetts.audio.AudioPlayer;
import com.sun.speech.freetts.audio.SingleFileAudioPlayer;

@Component
public class SipgateIoService {

    private final static Logger logger = LoggerFactory.getLogger(SipgateIoService.class);
    
    @Value("${name:World}")
    private String name;
    
    
    private ServingResourcesFromFileSystemAdapter servingResourcesFromFileSystemAdapter;
    
    @Inject
    public SipgateIoService(ServingResourcesFromFileSystemAdapter servingResourcesFromFileSystemAdapter) {
        this.servingResourcesFromFileSystemAdapter = servingResourcesFromFileSystemAdapter;
    }

    public String getHelloMessage() {
        return "Hello " + this.name;
    }

    public String getWaveFileLocationForSpokenString(String toBeSpoken) throws ApplicationException  {

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            logger.error("Could not instantiate SHA-256 algorithm", e);
            throw new ApplicationException(e);
        }
        
        try {
            md.update(toBeSpoken.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("UTF-8 does not exist (1)", e);
            throw new ApplicationException(e);
        } 
        
        byte[] toBeSpokenHashByteArray = md.digest();
        String toBeSpokenHash;
        
        toBeSpokenHash = DumpUtil.toHex(toBeSpokenHashByteArray);
        logger.info("toBeSpokenHash: {}", toBeSpokenHash);
        
        AudioPlayer audioPlayer = null;
        
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice speakingVoice = voiceManager.getVoice("kevin16");
        speakingVoice.allocate();
        
        String outputDirectory = servingResourcesFromFileSystemAdapter.getExternalFileSystemChroot();
        String waveFileUnprocessedFilesystemLocationWithoutFileExtension = outputDirectory + toBeSpokenHash + "_unprocessed";
        logger.info("Will be writing an unprocessed wave file to location: {}", waveFileUnprocessedFilesystemLocationWithoutFileExtension);
        
        audioPlayer = new SingleFileAudioPlayer(waveFileUnprocessedFilesystemLocationWithoutFileExtension, Type.WAVE);
        
        /*
         * TODO FreeTTS seems to have a bug: it cannot change the sampling rate,
         * it always produces files with a sampling rate of 16000 Hz AudioFormat
         * audioFormat = new AudioFormat(44100f, 16, 2, true, true);
         * audioPlayer.setAudioFormat(audioFormat);
         * 
         * System.out.println("Sample rate: " +
         * audioPlayer.getAudioFormat().getSampleRate() ); if
         * (audioPlayer.getAudioFormat().getSampleRate() == 8000) {
         * System.out.println("is true"); } else {
         * System.out.println("is false"); }
         */
        
        speakingVoice.setAudioPlayer(audioPlayer);
        speakingVoice.speak(toBeSpoken);
        speakingVoice.deallocate();
        audioPlayer.close();
        
        AudioFormat newAudioFormat = new AudioFormat(8000f, 16, 1, true, false);
        AudioInputStream oldStream;
        try {
            oldStream = AudioSystem.getAudioInputStream( new File(waveFileUnprocessedFilesystemLocationWithoutFileExtension + ".wav") );
        } catch (UnsupportedAudioFileException | IOException e) {
            logger.error("Problem 1 while getting audio stream", e);
            throw new ApplicationException(e);
        } 
        
        AudioInputStream newAudioStream = AudioSystem.getAudioInputStream(
                newAudioFormat, oldStream);
        try {
            String waveFileProcessedFilesystemLocation = outputDirectory + toBeSpokenHash + "_processed.wav";
            AudioSystem.write( newAudioStream, Type.WAVE, new File(waveFileProcessedFilesystemLocation) );
            return waveFileProcessedFilesystemLocation;
        } catch (IOException e) {
            logger.error("Problem 2 while writing audio stream", e);
            throw new ApplicationException(e);
        }
        
    }

}
