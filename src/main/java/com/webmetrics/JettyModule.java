package com.webmetrics;

import com.google.inject.Binder;
import com.google.inject.Module;
import com.webmetrics.providers.JettyServerProvider;
import org.eclipse.jetty.server.Server;

public class JettyModule implements Module {
    public void configure(Binder binder) {
        binder.bind(Server.class).toProvider(JettyServerProvider.class);

        //binder.bind(Mongo.class).toProvider(MongoProvider.class);
        //binder.bind(Morphia.class).toProvider(MorphiaProvider.class);
    }
}
