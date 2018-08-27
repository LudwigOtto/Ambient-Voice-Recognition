# Ambient-Voice-Recognition
## Workflow diagram and APP configure

Four labels — “silence”, “single voice”, “crowd voice”, and “ambient noise” are set to represent the four scenarios that are able to describe user’s most common environment. The difference among these four scenarios is basically upon the sound type, therefore the environment types (e.g. indoor or outdoor) is not taken into consideration. Some scenarios may be hard to be identified from others (e.g. crowd voice from single voice). Following are the detailed definition of four labels:
#### Silence
In default most quiet places are classified as the silence scenario.
#### Single Voice
This label is for the scenario where only one or two persons are talking around the user. Usually this happens in someplace like the user stays in the study room with friends or listens to a speech in the lecture hall.
#### Crowd Voice
This label is for the scenario where multiple human voices are occurring continuously around the user. Some cases like a shuttle full with people is a typical example for this scenario.
#### Ambient noise
This label is for the scenario where there is no human voice but a specific sound that almost never stops disturbing the user. A good example is the NCS commuter lounge room. The vendor machine over there periodically generate noise that is hard to ignore.

## Collect background sound data in Android devices
The Android media framework provides basic media services, including two major tasks in our app — collecting audio signals from devices’ audio source, usually from microphone, and playing various format media files as well. Here, our app use the AudioRecord APIs to collect background sound and use the MediaPlayer APIs to play music. Like a human being, the AudioRecord module is the ear of an app, which is used to hear surrounding voices from its microphone, and the MediaPlayer moulde is the mouth of an app, which sings a beautiful song.
Since we need to get the raw data of input audio signals for further analysis, we use the AudioRecord API, which manages the audio resources for Java applications to record audio from the audio input hardware of the platform. The collected audio data is encoded in PCM (Pulse-code modulation) format. From the PCM-encoded audio data, we can easily retrieve the amplitude and frequency of the sound.
