package com.autonomousapps.reactivestopwatch.di;

import com.autonomousapps.reactivestopwatch.service.ServiceProxy;

import android.content.Context;
import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;

@Module(includes = {
        ContextModule.class
})
public class ServiceModule {

    @Provides
    ServiceProxy providesServiceProxy(@NonNull Context context) {
        return new ServiceProxy(context);
    }
}