package edu.utep.cs.cs4381.platformer;

import android.content.Context;
import android.media.SoundPool;

public class SoundManager {
    public enum Sound {
        COIN_PICKUP(R.raw.coin_pickup),
        EXPLODE(R.raw.explode),
        EXTRA_LIFE(R.raw.extra_life),
        GUN_UPGRADE(R.raw.gun_upgrade),
        HIT_GUARD(R.raw.hit_guard),
        JUMP(R.raw.jump),
        RICOCHET(R.raw.ricochet),
        SHOOT(R.raw.shoot),
        TELEPORT(R.raw.teleport);

        public final int resourceId;
        private int soundId;

        Sound(int resourceId) {
            this.resourceId = resourceId;
        }
    }

    private static SoundManager theInstance;

    private final SoundPool soundPool;

    private SoundManager(Context ctx) {
        soundPool = new SoundPool.Builder()
                .setMaxStreams(Sound.values().length).build();
        for (Sound sound: Sound.values()) {
            sound.soundId = soundPool.load(ctx, sound.resourceId, 1);
        }
    }

    public static SoundManager instance(Context context) {
        if (theInstance == null) {
            theInstance = new SoundManager(context);
        }
        return theInstance;
    }

    public void play(Sound sound) {
        soundPool.play(sound.soundId, 1, 1, 0, 0, 1);
    }
}

