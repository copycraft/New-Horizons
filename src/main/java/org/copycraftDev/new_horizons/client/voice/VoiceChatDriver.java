package org.copycraftDev.new_horizons.client.voice;

import javax.sound.sampled.*;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class VoiceChatDriver implements net.fabricmc.api.ClientModInitializer {
    private static final AudioFormat FORMAT = new AudioFormat(16000f, 16, 1, true, false);
    private static final Map<UUID, SourceDataLine> lines = new ConcurrentHashMap<>();

    public static void playReceivedAudio(UUID speaker, byte[] audioBytes) {
        try {
            SourceDataLine line = lines.computeIfAbsent(speaker, id -> {
                try {
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, FORMAT);
                    SourceDataLine newLine = (SourceDataLine) AudioSystem.getLine(info);
                    newLine.open(FORMAT);
                    newLine.start();
                    return newLine;
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            });

            if (line != null) {
                line.write(audioBytes, 0, audioBytes.length);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopAll() {
        for (SourceDataLine line : lines.values()) {
            line.stop();
            line.close();
        }
        lines.clear();
    }

    @Override
    public void onInitializeClient() {

    }
}
