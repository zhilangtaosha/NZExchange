package com.nze.nzexchange.http;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;


import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

@SuppressWarnings({"ConstantConditions", "NullableProblems", "unchecked", "WeakerAccess"})
public final class YaRxJava2CallAdapterFactory extends CallAdapter.Factory {
    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        final CallAdapter adapter = RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()).get(returnType, annotations, retrofit);
        return new CallAdapter<Object, Object>() {
            @Override
            public Type responseType() {
                return adapter.responseType();
            }

            @Override
            public Object adapt(Call<Object> call) {
                return ((Observable<?>) adapter.adapt(call))
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
