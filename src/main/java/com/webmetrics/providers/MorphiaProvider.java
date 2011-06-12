package com.webmetrics.providers;

import com.google.code.morphia.Morphia;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class MorphiaProvider implements Provider<Morphia> {
    private Morphia morphia;

    public Morphia get() {
        if (morphia == null) {
            morphia = new Morphia();
        }

        return morphia;
    }
}
