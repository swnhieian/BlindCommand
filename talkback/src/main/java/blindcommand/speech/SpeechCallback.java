package blindcommand.speech;

import java.util.List;

public interface SpeechCallback {
    void onResult(List<SpeechResult> result);

    void onBeginOfSpeech();

    void onEndOfSpeech();

    void onStringResult(String result);
}
